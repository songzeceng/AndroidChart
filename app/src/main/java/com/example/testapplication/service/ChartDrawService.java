package com.example.testapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.example.testapplication.IChartDrawAidlInterface;

import static com.example.testapplication.utils.Utils.sTYPE_CHART_INTENT;

public class ChartDrawService extends Service {
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int mChartType = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mChartType = intent.getIntExtra(sTYPE_CHART_INTENT, -1);
        return new IChartDrawAidlInterface.Stub() {
            @Override
            public void viewCreate() throws RemoteException {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), PopupWindowActivity.class);
                        intent.putExtra(sTYPE_CHART_INTENT, mChartType);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }
        };
    }
}
