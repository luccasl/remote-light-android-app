package com.home.johnsmith.lightswitchapp.task;

import android.os.AsyncTask;

import com.home.johnsmith.lightswitchapp.Client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AsyncSendSignal extends AsyncTask<Object, Object, String> {
    private CallbackEvent onSignalSent;

    public void setOnSignalSent(Object context, Method method) {
        onSignalSent = new CallbackEvent(context, method);
    }

    @Override
    protected String doInBackground(Object... params) {
        Client client = (Client)params[0];
        String signal = (String)params[1];

        try {
            client.SendSignal(signal);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return signal;
    }

    @Override
    protected void onPostExecute(String signal) {
        try {
            onSignalSent.call(signal);
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
