package com.home.johnsmith.lightswitchapp.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class AsyncCheckConnection extends AsyncTask<Socket, Object, Boolean> {
    CallbackEvent postConnectionCheck;

    public void setPostConnectionCheck(Object context, Method method) {
        postConnectionCheck = new CallbackEvent(context, method);
    }

    @Override
    protected Boolean doInBackground(Socket... params) {
        BufferedReader br;
        String resp;
        Socket socket = params[0];

        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            resp = br.readLine();
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }

        if(resp == null) {
            return false;
        }

        System.out.println("Heartbeat: " + resp);

        return resp == "alive";
    }

    @Override
    protected void onPostExecute(Boolean isConnected) {
        try {
            postConnectionCheck.call(isConnected);
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
