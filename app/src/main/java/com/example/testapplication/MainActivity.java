package com.example.testapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.testapplication.BarChart.BarActivity;
import com.example.testapplication.LineChart.LineChartActivity;
import com.example.testapplication.PieChart.PieChartActivity;
import com.example.testapplication.service.ChartDrawService;
import com.example.testapplication.service.MultiProcessLineChartActivity;

public class MainActivity extends Activity implements View.OnClickListener {
//    private SurfaceView mMultiProcessSurface;
//    private ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            IChartDrawAidlInterface drawAidlInterface = IChartDrawAidlInterface.Stub.asInterface(service);
//            try {
//                drawAidlInterface.viewCreate(mMultiProcessSurface.getHolder().getSurface());
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        findViewById(R.id.btn_line).setOnClickListener(this);
        findViewById(R.id.btn_bar).setOnClickListener(this);
        findViewById(R.id.btn_pie).setOnClickListener(this);
        findViewById(R.id.btn_multi_process).setOnClickListener(this);

//        mMultiProcessSurface = findViewById(R.id.surface_multi_process);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Class dest = null;
        switch (v.getId()) {
            case R.id.btn_line:
                dest = LineChartActivity.class;
                break;
            case R.id.btn_bar:
                dest = BarActivity.class;
                break;
            case R.id.btn_pie:
                dest = PieChartActivity.class;
                break;
            case R.id.btn_multi_process:
                dest = MultiProcessLineChartActivity.class;
        }

        if (dest != null) {
            startActivity(new Intent(this, dest));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(mConnection);
    }
}
