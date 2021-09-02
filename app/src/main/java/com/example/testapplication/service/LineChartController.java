package com.example.testapplication.service;

import android.annotation.SuppressLint;
import android.app.Presentation;
import android.os.Message;
import android.view.View;
import android.widget.HorizontalScrollView;

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

public class LineChartController implements View.OnClickListener, IHandler, IPresentationCallback {
    private LineChartView mLineChartView;
    private HorizontalScrollView mScrollContainer;

    private WeakHandler mHandler = new WeakHandler(this);
    private Presentation mPresentation;

    public LineChartController(Presentation presentation) {
        if (presentation == null) {
            throw new IllegalArgumentException("Presentation here must not be null!");
        }

        mPresentation = presentation;

        mLineChartView = (LineChartView) mPresentation.findViewById(R.id.line_chart_view);
        mScrollContainer = mPresentation.findViewById(R.id.scroll_container);
        mScrollContainer.getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> mScrollContainer.post(()
                        -> mScrollContainer.fullScroll(View.FOCUS_RIGHT)));

        mPresentation.findViewById(R.id.btn_cpu_line).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_cpu_speed_line).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_current_line).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_fps_line).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_gpu_line).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_memory_line).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_network_status_line).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_temperature_line).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_ui_stall_duration_line).setOnClickListener(this);

        initData(sTYPE_MEMORY);
    }

    private void initData(int type) {
        mHandler.removeCallbacksAndMessages(null);

        DataHelper.getInstance().initData(mLineChartView, type);
        mPresentation.show();

        mHandler.sendEmptyMessageDelayed(type, Utils.sINTERVAL_UPDATE_DATA);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_memory_line:
                initData(sTYPE_MEMORY);
                break;
            case R.id.btn_cpu_line:
                initData(sTYPE_CPU);
                break;
            case R.id.btn_cpu_speed_line:
                initData(sTYPE_CPU_SPEED);
                break;
            case R.id.btn_current_line:
                initData(sTYPE_CURRENT);
                break;
            case R.id.btn_fps_line:
                initData(sTYPE_FPS);
                break;
            case R.id.btn_gpu_line:
                initData(sTYPE_GPU);
                break;
            case R.id.btn_network_status_line:
                initData(sTYPE_NETWORK_STATUS);
                break;
            case R.id.btn_temperature_line:
                initData(sTYPE_TEMPERATURE);
                break;
            case R.id.btn_ui_stall_duration_line:
                initData(sTYPE_UI);
                break;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        DataHelper.getInstance().appendLineChartData(mLineChartView, msg.what);
        mPresentation.show();

        mHandler.sendEmptyMessageDelayed(msg.what, Utils.sINTERVAL_UPDATE_DATA);
    }

    @Override
    public void onDisplayRemoved() {
        DataHelper.getInstance().clearData(mLineChartView);
    }
}
