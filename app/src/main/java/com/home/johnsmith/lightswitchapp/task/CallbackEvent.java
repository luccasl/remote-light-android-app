package com.home.johnsmith.lightswitchapp.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CallbackEvent {
    private Object receiver;
    private Method method;

    public CallbackEvent(Object receiver, Method method) {
        this.receiver = receiver;
        this.method = method;
    }

    public void call() throws InvocationTargetException, IllegalAccessException {
        method.invoke(receiver);
    }

    public void call(Object... params) throws InvocationTargetException, IllegalAccessException {
        method.invoke(receiver, params);
    }
}