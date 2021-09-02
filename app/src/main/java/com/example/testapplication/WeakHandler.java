package com.example.testapplication;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.testapplication.interfaces.IHandler;

import java.lang.ref.WeakReference;

public class WeakHandler extends Handler {
    private WeakReference<IHandler> handlerRef;

    public WeakHandler(IHandler handler) {
        if (handler != null) {
            handlerRef = new WeakReference<IHandler>(handler);
        }
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (handlerRef != null && handlerRef.get() != null) {
            handlerRef.get().handleMessage(msg);
        }
    }
}
