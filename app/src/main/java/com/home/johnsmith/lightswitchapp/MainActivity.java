package com.home.johnsmith.lightswitchapp;

import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.home.johnsmith.lightswitchapp.task.AsyncSendSignal;
import com.home.johnsmith.lightswitchapp.task.AsyncTryConnect;

public class MainActivity extends AppCompatActivity {

    private ScrollView svMain;
    private TextView lbSwitch;
    private Button btSwitch;
    private TextView lbState;
    private boolean switchState = false;
    private Client client;
    private Thread threadClient;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btSwitch = findViewById(R.id.btSwitch);
        lbSwitch = findViewById(R.id.lbSwitch);
        svMain = findViewById(R.id.svMain);
        lbState = findViewById(R.id.lbState);

        btSwitch.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onClickBtSwitch(view);
            }
        });

        setupResources();

        // Iniciando instancia do client
        client = new Client();

        try {
            client.SetOnDisconnected(this, getClass().getMethod("onDisconnected"));
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }

        // Executando operações de conexão em um Thread separado(responsividade)
        AsyncTryConnect connectTask = new AsyncTryConnect();
        try {
            connectTask.setOnIsConnected(this, getClass().getMethod("onIsConnected"));
            connectTask.setOnConnectionFailure(this, getClass().getMethod("onConnectionFailure"));
            connectTask.setOnConnecting(this, getClass().getMethod("onConnecting"));
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        connectTask.execute(client);
    }

    public void onIsConnected() {
        lbState.setText("Connected.");
        System.out.println("Connected.");
    }

    public void onConnectionFailure() {
        lbState.setText("Connection failure");
    }

    public void onConnecting() {
        lbState.setText("Connecting...");
        System.out.println("Connecting...");
    }

    public void onReadyForSignal() {
        onIsConnected();

        // Executando operações de conexão em um Thread separado(responsividade)
        AsyncSendSignal sendSignalTask = new AsyncSendSignal();
        try {
            sendSignalTask.setOnSignalSent(this, getClass().getMethod("onSignalSent", String.class));
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        sendSignalTask.execute(client, switchState ? "1" : "-1");
    }

    public void onSignalSent(String signal) {
        switch(signal) {
            case "1":
                // Atualizar UI
                btSwitch.setText("on");
                btSwitch.setBackgroundResource(R.color.darkerLight);
                btSwitch.setTextColor(res.getColor(R.color.darker));
                lbSwitch.setTextColor(res.getColor(R.color.darker));
                svMain.setBackgroundResource(R.color.light);
                break;

            case "-1":
                // Atualizar UI
                btSwitch.setText("off");
                btSwitch.setBackgroundResource(R.color.mildBlue);
                btSwitch.setTextColor(res.getColor(R.color.light));
                lbSwitch.setTextColor(res.getColor(R.color.light));
                svMain.setBackgroundResource(R.color.dark);
                break;
        }
    }

    public void onDisconnected() {
        lbState.setText("Connection lost");
    }

    /**
     * Método utilizado para obter objeto com recursos do projeto
     */
    private void setupResources() {
        res = getResources();
    }

    /**
     * Evento disparado ao clicar no botão de On/Off
     */
    public void onClickBtSwitch(View view) {
        if(!client.IsConnected()) {
            AsyncTryConnect connectTask = new AsyncTryConnect();
            try {
                connectTask.setOnIsConnected(this, getClass().getMethod("onReadyForSignal"));
                connectTask.setOnConnectionFailure(this, getClass().getMethod("onConnectionFailure"));
                connectTask.setOnConnecting(this, getClass().getMethod("onConnecting"));
            } catch(NoSuchMethodException e) {
                e.printStackTrace();
            }
            connectTask.execute(client);
        } else {
            // Executando operações de conexão em um Thread separado(responsividade)
            AsyncSendSignal sendSignalTask = new AsyncSendSignal();
            try {
                sendSignalTask.setOnSignalSent(this, getClass().getMethod("onSignalSent", String.class));
            } catch(NoSuchMethodException e) {
                e.printStackTrace();
            }
            sendSignalTask.execute(client, switchState ? "1" : "-1");
        }
    }
}
