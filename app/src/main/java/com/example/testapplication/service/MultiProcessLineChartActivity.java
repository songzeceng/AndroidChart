package com.example.testapplication.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.example.testapplication.IChartDrawAidlInterface;
import com.example.testapplication.LineChart.LineChartView;
import com.example.testapplication.PieChart.PieChartView;
import com.example.testapplication.R;
import com.example.testapplication.WeakHandler;
import com.example.testapplication.interfaces.IHandler;
import com.example.testapplication.utils.DataHelper;
import com.example.testapplication.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.example.testapplication.utils.Utils.*;

public class MultiProcessLineChartActivity extends Activity implements IHandler, View.OnClickListener {
    private static final String TAG = "MultiProcessLineChartActivity";

    private WeakHandler mHandler = new WeakHandler(this);
    private IChartDrawAidlInterface mDrawAidlInterface;

    private double[] dataForLineAndBar = new double[sINIT_DATA_COUNT];
    private int[] dataForPie = new int[sINIT_DATA_COUNT];
    private ArrayList<PieChartView.Data> rawDataForPie;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDrawAidlInterface = IChartDrawAidlInterface.Stub.asInterface(service);
            try {
                Log.i(TAG, "onServiceConnected: current thread`s name: " + Thread.currentThread().getName());
                mDrawAidlInterface.viewCreate();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int chartType = intent.getIntExtra(sTYPE_CHART_INTENT, sTYPE_CHART_LINE);
            int dataType = intent.getIntExtra(sTYPE_CHART_DATA_INTENT, sTYPE_MEMORY);

            Bundle bundle = new Bundle();
            DataHelper helper = DataHelper.getInstance();

            switch (intent.getAction()) {
                case "sub_process_activity_started":
                    intent = new Intent();
                    intent.setAction("init_data");
                    switch (chartType) {
                        case sTYPE_CHART_LINE:
                            double rulerValue = helper.initRawDataForLine(dataForLineAndBar, dataType);
                            bundle.putDouble(sVALUE_LINE_CHART_RULER_INTENT, rulerValue);
                            bundle.putDoubleArray(sDATA_CHART_INTENT, dataForLineAndBar);
                            break;
                        case sTYPE_CHART_BAR:
                            helper.initRawDataForBar(dataForLineAndBar, dataType);
                            bundle.putDoubleArray(sDATA_CHART_INTENT, dataForLineAndBar);
                            break;
                        case sTYPE_CHART_PIE:
                            rawDataForPie = (ArrayList<PieChartView.Data>) helper.initRawDataForPie(dataForPie, dataType);
                            bundle.putParcelableArrayList("pie_data_array", rawDataForPie);
                            break;
                    }

                    bundle.putInt(sTYPE_CHART_INTENT, chartType);
                    bundle.putInt(sTYPE_CHART_DATA_INTENT, dataType);

                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);
                    sendMessage(chartType, dataType);
                    break;
                case "switch_data":
                    intent = new Intent();
                    intent.setAction("init_data");
                    switch (chartType) {
                        case sTYPE_CHART_LINE:
                            double rulerValue = helper.initRawDataForLine(dataForLineAndBar, dataType);
                            bundle.putDouble(sVALUE_LINE_CHART_RULER_INTENT, rulerValue);
                            bundle.putDoubleArray(sDATA_CHART_INTENT, dataForLineAndBar);
                            break;
                        case sTYPE_CHART_BAR:
                            helper.initRawDataForBar(dataForLineAndBar, dataType);
                            bundle.putDoubleArray(sDATA_CHART_INTENT, dataForLineAndBar);
                            break;
                        case sTYPE_CHART_PIE:
                            rawDataForPie = (ArrayList<PieChartView.Data>) helper.initRawDataForPie(dataForPie, dataType);
                            bundle.putParcelableArrayList("pie_data_array", rawDataForPie);
                            break;
                    }

                    bundle.putInt(sTYPE_CHART_DATA_INTENT, dataType);
                    bundle.putInt(sTYPE_CHART_INTENT, chartType);

                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);

                    updateData(chartType, dataType);
            }
        }
    };

    private void sendMessage(int chartType, int dataType) {
        mHandler.removeCallbacksAndMessages(null);

        Message msg = mHandler.obtainMessage(dataType);
        msg.arg1 = chartType;
        mHandler.sendMessageDelayed(msg, Utils.sINTERVAL_UPDATE_DATA);
    }

    private void updateData(int chartType, int dataType) {
        Bundle bundle = new Bundle();
        bundle.putInt(sTYPE_CHART_INTENT, chartType);
        bundle.putInt(sTYPE_CHART_DATA_INTENT, dataType);

        double data = 0;
        switch (chartType) {
            case sTYPE_CHART_LINE:
            case sTYPE_CHART_BAR:
                data = DataHelper.getInstance().generateNewDoubleDataForLineAndBar(dataType);
                bundle.putDouble("new_data", data);
                break;
            case sTYPE_CHART_PIE:
                PieChartView.Data newDataForPie = DataHelper.getInstance().generateNewDataForPie(dataForPie, dataType);
                bundle.putParcelable("pie_data", newDataForPie);
                break;
        }

        Intent intent = new Intent();
        intent.setAction("update_data");
        intent.putExtra("data", bundle);
        sendBroadcast(intent);

        Message msg = mHandler.obtainMessage(dataType);
        msg.arg1 = chartType;
        mHandler.sendMessageDelayed(msg, Utils.sINTERVAL_UPDATE_DATA);
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiprocess_line);

        findViewById(R.id.btn_multi_line_cart).setOnClickListener(this);
        findViewById(R.id.btn_multi_bar_cart).setOnClickListener(this);
        findViewById(R.id.btn_multi_pie_cart).setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("sub_process_activity_started");
        filter.addAction("sub_process_data_initialized");
        filter.addAction("switch_data");

        registerReceiver(mReceiver, filter);
    }

    @Override
    public void handleMessage(Message msg) {
        updateData(msg.arg1, msg.what);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int chartType = sTYPE_CHART_LINE;
        switch (view.getId()) {
            case R.id.btn_multi_line_cart:
                chartType = sTYPE_CHART_LINE;
                break;
            case R.id.btn_multi_bar_cart:
                chartType = sTYPE_CHART_BAR;
                break;
            case R.id.btn_multi_pie_cart:
                chartType = sTYPE_CHART_PIE;
                break;
        }

        Intent intent = new Intent(this, ChartDrawService.class);
        intent.putExtra(sTYPE_CHART_INTENT, chartType);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        DataHelper.getInstance().clearData(mLineChartView);
        unregisterReceiver(mReceiver);
        unbindService(mConnection);
    }
}
