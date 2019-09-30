package com.home.johnsmith.lightswitchapp;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import com.home.johnsmith.lightswitchapp.Client;

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

        // Executando operações de conexão em um Thread separado(responsividade)
        threadClient = new Thread(new ClientThread());
        threadClient.start();
    }

    /**
     * Método utilizado para obter objeto com recursos do projeto
     */
    private void setupResources() {
        res = getResources();
    }

    /**
     * Evento disparado após se conectar ao servidor
     */
    private void onConnected() {
        if (switchState) {
            // Enviar sinal (ligar)
            client.SendSignal("1");

            // Atualizar UI
            btSwitch.setText("on");
            btSwitch.setBackgroundResource(R.color.darkerLight);
            btSwitch.setTextColor(res.getColor(R.color.darker));
            lbSwitch.setTextColor(res.getColor(R.color.darker));
            svMain.setBackgroundResource(R.color.light);
        } else {
            // Enviar sinal (desligar)
            client.SendSignal("-1");

            // Atualizar UI
            btSwitch.setText("off");
            btSwitch.setBackgroundResource(R.color.mildBlue);
            btSwitch.setTextColor(res.getColor(R.color.light));
            lbSwitch.setTextColor(res.getColor(R.color.light));
            svMain.setBackgroundResource(R.color.dark);
        }

        switchState = !switchState;
    }

    /**
     * Evento disparado ao clicar no botão de On/Off
     */
    public void onClickBtSwitch(View view) {
        // Executando operações de conexão em um Thread separado(responsividade)
        if(threadClient.isAlive()) {
            threadClient.stop();
        }

        threadClient.start();
    }

    /**
     * Runnable que executa de fundo e tenta se conectar ao servidor e executar ação do botão
     */
    class ClientThread implements Runnable {
        @Override
        public void run() {
            int success = -1;

            // Checando por instancia do client
            if(client == null) {
                client = new Client();
            }

            // Checando conexão
            if(!client.IsConnected()){
                try {
                    // Tentando se conectar ao servidor
                    success = client.Connect();
                }
                catch (Exception e) {
                    lbState.setText("Connection error: " + e.getMessage());
                }
            } else {
                success = 0;
            }

            if(success == 0) {
                // Conectado com sucesso
                onConnected();

                lbState.setText("Connected.");
            } else {
                // Conexão falhou
                lbState.setText("Connection failed.");
            }
        }
    }
}
