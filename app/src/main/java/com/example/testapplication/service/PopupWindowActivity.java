package com.example.testapplication.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.example.testapplication.PieChart.PieChartView;
import com.example.testapplication.R;
import com.example.testapplication.interfaces.IPopupWindowListener;

import java.util.ArrayList;
import java.util.Date;

import static com.example.testapplication.utils.Utils.sDATA_CHART_INTENT;
import static com.example.testapplication.utils.Utils.sTYPE_CHART_BAR;
import static com.example.testapplication.utils.Utils.sTYPE_CHART_DATA_INTENT;
import static com.example.testapplication.utils.Utils.sTYPE_CHART_INTENT;
import static com.example.testapplication.utils.Utils.sTYPE_CHART_LINE;
import static com.example.testapplication.utils.Utils.sTYPE_CHART_PIE;
import static com.example.testapplication.utils.Utils.sTYPE_MEMORY;
import static com.example.testapplication.utils.Utils.sTYPE_NETWORK_STATUS;
import static com.example.testapplication.utils.Utils.sVALUE_LINE_CHART_RULER_INTENT;

public class PopupWindowActivity extends Activity implements IPopupWindowListener {
    private View mPopupWindowView;
    private DrawChartPopupWindow mPopupWindow;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("data");
            if (bundle == null || !bundle.containsKey(sTYPE_CHART_DATA_INTENT) || !bundle.containsKey(sTYPE_CHART_INTENT)) {
                return;
            }

            if (!intent.getAction().equals("init_data") && (bundle.getInt(sTYPE_CHART_INTENT) != mChartType
                    || bundle.getInt(sTYPE_CHART_DATA_INTENT) != mDataType)) {
                return;
            }

            double[] data;
            double new_data;

            switch (intent.getAction()) {
                case "init_data":
                    if (!bundle.containsKey(sDATA_CHART_INTENT) && !bundle.containsKey("pie_data_array")) {
                        return;
                    }

                    switch (mChartType) {
                        case sTYPE_CHART_LINE:
                            if (!bundle.containsKey(sVALUE_LINE_CHART_RULER_INTENT)) {
                                return;
                            }

                            data = bundle.getDoubleArray(sDATA_CHART_INTENT);
                            double rulerValue = bundle.getDouble(sVALUE_LINE_CHART_RULER_INTENT);
                            mPopupWindow.initLineChartData(data, rulerValue);
                            break;
                        case sTYPE_CHART_BAR:
                            data = bundle.getDoubleArray(sDATA_CHART_INTENT);
                            mPopupWindow.initBarChartData(data);
                            break;
                        case sTYPE_CHART_PIE:
                            ArrayList<PieChartView.Data> pieData = bundle.getParcelableArrayList("pie_data_array");
                            mPopupWindow.initPieChartData(pieData);
                            break;
                    }
                    break;
                case "update_data":
                    if (!bundle.containsKey("new_data") && !bundle.containsKey("pie_data")) {
                        return;
                    }

                    switch (mChartType) {
                        case sTYPE_CHART_LINE:
                            new_data = bundle.getDouble("new_data");
                            mPopupWindow.updateLineChart(new_data, new Date());
                            break;
                        case sTYPE_CHART_BAR:
                            new_data = bundle.getDouble("new_data");
                            mPopupWindow.updateBarChart(new_data, new Date());
                            break;
                        case sTYPE_CHART_PIE:
                            PieChartView.Data pieData = bundle.getParcelable("pie_data");
                            mPopupWindow.updatePieChart(pieData);
                            break;
                    }

                    break;
            }
        }
    };
    private int mChartType = 0;
    private int mDataType = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subprocess_line);

        Intent intent = getIntent();
        mChartType = intent.getIntExtra(sTYPE_CHART_INTENT, 0);

        int contentLayoutId = R.layout.activity_line;
        switch (mChartType) {
            case sTYPE_CHART_LINE:
                contentLayoutId = R.layout.activity_line;
                break;
            case sTYPE_CHART_BAR:
                contentLayoutId = R.layout.activity_bar;
                break;
            case sTYPE_CHART_PIE:
                contentLayoutId = R.layout.activity_pie;
                break;
        }

        mPopupWindowView = LayoutInflater.from(this).inflate(contentLayoutId, null);
        mPopupWindow = new DrawChartPopupWindow(mChartType, findViewById(R.id.multi_root_view), mPopupWindowView, this,
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.showView();

        mDataType = mChartType == sTYPE_CHART_PIE ? sTYPE_NETWORK_STATUS : sTYPE_MEMORY;

        IntentFilter filter = new IntentFilter();
        filter.addAction("init_data");
        filter.addAction("update_data");
        registerReceiver(mReceiver, filter);

        intent = new Intent();
        intent.setAction("sub_process_activity_started");
        intent.putExtra(sTYPE_CHART_INTENT, mChartType);
        intent.putExtra(sTYPE_CHART_DATA_INTENT, mChartType == sTYPE_CHART_PIE ? sTYPE_NETWORK_STATUS : sTYPE_MEMORY);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void changeData(int chartType, int dataType) {
        mDataType = dataType;

        Intent intent = new Intent();
        intent.setAction("switch_data");
        intent.putExtra(sTYPE_CHART_INTENT, chartType);
        intent.putExtra(sTYPE_CHART_DATA_INTENT, dataType);
        sendBroadcast(intent);
    }
}
