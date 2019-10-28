package com.home.johnsmith.lightswitchapp;

import android.os.Handler;
import android.os.Looper;

import com.home.johnsmith.lightswitchapp.task.AsyncCheckConnection;
import com.home.johnsmith.lightswitchapp.task.CallbackEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private int port;                           // Porta do servidor
    private String targetHost;                  // Endereço do servidor
    private InetAddress ipAddress;              // Interface de conexão
    private Socket socket;                      // Socket de conexão
    private boolean isConnected = true;        // Indica o sucesso da conexão
    private CallbackEvent onDisconnected;

    /**
     * Cria uma nova instancia de um client que permite se comunicar com o servidor por meio de sockets
     */
    public Client() {
        port = 4000;
        targetHost = "192.168.0.10";
    }

    /**
     * Cria uma nova instancia de um client que permite se comunicar com o servidor por meio de sockets
     */
    public Client(final int port, final String address) {
        this.port = port;
        this.targetHost = address;
    }

    public void SetOnDisconnected(Object context, Method method) {
        onDisconnected = new CallbackEvent(context, method);
    }

    /**
     * Indica se o client está conectado ao servidor
     */
    public boolean IsConnected() {
        return isConnected;
    }

    private class CheckConnection implements Runnable {
        private boolean tryConnect = true;
        private Method postConnectionCheck;

        public CheckConnection(Method postConnectionCheck) {
            this.postConnectionCheck = postConnectionCheck;
        }

        @Override
        public void run() {
            while(tryConnect) {
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

                if(!isConnected) {
                    tryConnect = false;
                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(onDisconnected != null) {
                                try {
                                    onDisconnected.call();
                                } catch(IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch(InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AsyncCheckConnection checkConnection = new AsyncCheckConnection();
                        checkConnection.setPostConnectionCheck(this, postConnectionCheck);
                        checkConnection.execute(socket);
                    }
                });
            }
        }
    }

    public void PostConnectionCheck(Boolean isConnected) {
        this.isConnected = isConnected;
    }

    /**
     * Método responsável por tentar se conectar ao servidor
     */
    public int Connect() {
        try {
            ipAddress = InetAddress.getByName(targetHost);
        } catch (UnknownHostException e) {
            e.printStackTrace();

            isConnected = false;

            return -1;
        }

        try {
            socket = new Socket(ipAddress, port);
        } catch (IOException e) {
            e.printStackTrace();

            isConnected = false;

            return -1;
        }

        AsyncCheckConnection checkConnection = new AsyncCheckConnection();
        try {
            checkConnection.setPostConnectionCheck(this, getClass().getMethod("PostConnectionCheck", Boolean.class));
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
            return -1;
        }
        checkConnection.execute(socket);
        try {
            new Thread(new CheckConnection(getClass().getMethod("PostConnectionCheck", Boolean.class))).start();
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    /**
     * Método responsável por desconectar do servidor
     */
    public void Disconnect() throws IOException {
        if(socket != null && socket.isConnected()) {
            socket.close();
        }

        isConnected = false;
    }

    /**
     * Método responsável por enviar sinais ao servidor, que serão repassados à porta serial
     */
    public void SendSignal(String signal) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.print("signal " + signal);
            out.flush();
        } catch(UnknownHostException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
