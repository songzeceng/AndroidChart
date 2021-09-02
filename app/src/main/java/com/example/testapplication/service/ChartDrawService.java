package com.example.testapplication.service;

import android.app.Presentation;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.Nullable;

import com.example.testapplication.IHandler;
import com.example.testapplication.WeakHandler;

public class ChartDrawService extends Service {
    private Presentation mPresentation;
    private Handler mHandler = new Handler(getMainLooper());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
