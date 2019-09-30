package com.home.johnsmith.lightswitchapp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private int port;                           // Porta do servidor
    private String targetHost;                  // Endereço do servidor
    private InetAddress ipAddress;              // Interface de conexão
    private Socket socket;                      // Socket de conexão
    private boolean isConnected = false;        // Indica o sucesso da conexão

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

    /**
     * Indica se o client está conectado ao servidor
     */
    public boolean IsConnected() {
        return isConnected;
    }

    /**
     * Método responsável por tentar se conectar ao servidor
     */
    public int Connect() {
        try {
            ipAddress = InetAddress.getByName(targetHost);
        } catch (UnknownHostException e) {
            e.printStackTrace();

            return -1;
        }

        try {
            socket = new Socket(ipAddress, port);
        } catch (IOException e) {
            e.printStackTrace();

            return -1;
        }

        isConnected = true;
        return 0;
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
