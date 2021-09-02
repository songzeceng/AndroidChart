package com.example.testapplication.service;

import android.annotation.SuppressLint;
import android.app.Presentation;
import android.os.Message;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.example.testapplication.BarChart.BarChartView;
import com.example.testapplication.LineChart.LineChartView;
import com.example.testapplication.R;
import com.example.testapplication.WeakHandler;
import com.example.testapplication.interfaces.IHandler;
import com.example.testapplication.interfaces.IPresentationCallback;
import com.example.testapplication.utils.DataHelper;
import com.example.testapplication.utils.Utils;

import static com.example.testapplication.utils.Utils.sTYPE_CPU;
import static com.example.testapplication.utils.Utils.sTYPE_CPU_SPEED;
import static com.example.testapplication.utils.Utils.sTYPE_CURRENT;
import static com.example.testapplication.utils.Utils.sTYPE_FPS;
import static com.example.testapplication.utils.Utils.sTYPE_GPU;
import static com.example.testapplication.utils.Utils.sTYPE_MEMORY;
import static com.example.testapplication.utils.Utils.sTYPE_NETWORK_STATUS;
import static com.example.testapplication.utils.Utils.sTYPE_TEMPERATURE;
import static com.example.testapplication.utils.Utils.sTYPE_UI;

public class BarChartController implements View.OnClickListener, IHandler, IPresentationCallback {
    private BarChartView mBarChartView;
    private HorizontalScrollView mScrollContainer;

    private WeakHandler mHandler = new WeakHandler(this);
    private Presentation mPresentation;

    public BarChartController(Presentation presentation) {
        if (presentation == null) {
            throw new IllegalArgumentException("Presentation here must not be null!");
        }

        mPresentation = presentation;

        mBarChartView = mPresentation.findViewById(R.id.bar_chart_view);
        mScrollContainer = mPresentation.findViewById(R.id.scroll_container);
        mScrollContainer.getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> mScrollContainer.post(()
                        -> mScrollContainer.fullScroll(View.FOCUS_RIGHT)));

        mPresentation.findViewById(R.id.btn_cpu_bar).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_cpu_speed_bar).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_current_bar).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_fps_bar).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_gpu_bar).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_memory_bar).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_network_status_bar).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_temperature_bar).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_ui_stall_duration_bar).setOnClickListener(this);

        initData(sTYPE_MEMORY);
    }

    private void initData(int type) {
        mHandler.removeCallbacksAndMessages(null);

        DataHelper.getInstance().initData(mBarChartView, type);
        mHandler.sendEmptyMessageDelayed(type, Utils.sINTERVAL_UPDATE_DATA);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_memory_bar:
                initData(sTYPE_MEMORY);
                break;
            case R.id.btn_cpu_bar:
                initData(sTYPE_CPU);
                break;
            case R.id.btn_cpu_speed_bar:
                initData(sTYPE_CPU_SPEED);
                break;
            case R.id.btn_current_bar:
                initData(sTYPE_CURRENT);
                break;
            case R.id.btn_fps_bar:
                initData(sTYPE_FPS);
                break;
            case R.id.btn_gpu_bar:
                initData(sTYPE_GPU);
                break;
            case R.id.btn_network_status_bar:
                initData(sTYPE_NETWORK_STATUS);
                break;
            case R.id.btn_temperature_bar:
                initData(sTYPE_TEMPERATURE);
                break;
            case R.id.btn_ui_stall_duration_bar:
                initData(sTYPE_UI);
                break;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        DataHelper.getInstance().appendBarChartData(mBarChartView, msg.what);
        mHandler.sendEmptyMessageDelayed(msg.what, Utils.sINTERVAL_UPDATE_DATA);
    }

    @Override
    public void onDisplayRemoved() {
        DataHelper.getInstance().clearData(mBarChartView);
    }
}
