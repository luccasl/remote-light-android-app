package com.home.johnsmith.lightswitchapp.task;

import android.os.AsyncTask;

import com.home.johnsmith.lightswitchapp.Client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AsyncTryConnect extends AsyncTask<Client, Object, Boolean> {
    private CallbackEvent onIsConnected;
    private CallbackEvent onConnectionFailure;
    private CallbackEvent onConnecting;

    public void setOnIsConnected(Object context, Method method) {
        onIsConnected = new CallbackEvent(context, method);
    }

    public void setOnConnectionFailure(Object context, Method method) {
        onConnectionFailure = new CallbackEvent(context, method);
    }

    public void setOnConnecting(Object context, Method method) {
        onConnecting = new CallbackEvent(context, method);
    }

    @Override
    protected void onPreExecute() {
        if(onConnecting == null) {
            return;
        }

        try {
            onConnecting.call();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Boolean doInBackground(Client... params) {
        int success = -1;

        try {
            success = params[0].Connect();
        } catch(Exception e) {
            e.printStackTrace();

            return false;
        }

        return success == 0;
    }

    @Override
    protected void onPostExecute(Boolean isConnected) {
        if(isConnected) {
            if(onIsConnected != null) {
                try {
                    onIsConnected.call();
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                } catch(InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if(onConnectionFailure != null) {
                try {
                    onConnectionFailure.call();
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                } catch(InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
