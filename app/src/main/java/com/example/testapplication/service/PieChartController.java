package com.example.testapplication.service;

import android.annotation.SuppressLint;
import android.app.Presentation;
import android.os.Message;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.example.testapplication.PieChart.PieChartView;
import com.example.testapplication.R;
import com.example.testapplication.WeakHandler;
import com.example.testapplication.interfaces.IHandler;
import com.example.testapplication.interfaces.IPresentationCallback;
import com.example.testapplication.utils.DataHelper;
import com.example.testapplication.utils.Utils;

import static com.example.testapplication.utils.Utils.sTYPE_MEMORY;
import static com.example.testapplication.utils.Utils.sTYPE_NETWORK_STATUS;

public class PieChartController implements View.OnClickListener, IHandler, IPresentationCallback {
    private PieChartView mPieChartView;
    private HorizontalScrollView mScrollContainer;

    private WeakHandler mHandler = new WeakHandler(this);
    private Presentation mPresentation;

    public PieChartController(Presentation presentation) {
        if (presentation == null) {
            throw new IllegalArgumentException("Presentation here must not be null!");
        }

        mPresentation = presentation;

        mPieChartView = mPresentation.findViewById(R.id.pie_chart_view);
        mScrollContainer = mPresentation.findViewById(R.id.scroll_container);
        mScrollContainer.getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> mScrollContainer.post(()
                        -> mScrollContainer.fullScroll(View.FOCUS_RIGHT)));

        mPresentation.findViewById(R.id.btn_cpu_pie).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_cpu_speed_pie).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_current_pie).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_fps_pie).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_gpu_pie).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_memory_pie).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_network_status_pie).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_temperature_pie).setOnClickListener(this);
        mPresentation.findViewById(R.id.btn_ui_stall_duration_pie).setOnClickListener(this);

        initData(sTYPE_MEMORY);
    }

    private void initData(int type) {
        mHandler.removeCallbacksAndMessages(null);

        DataHelper.getInstance().initData(mPieChartView, type);
        mHandler.sendEmptyMessageDelayed(type, Utils.sINTERVAL_UPDATE_DATA);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_memory_pie:
//                initData(sTYPE_MEMORY);
//                break;
//            case R.id.btn_cpu_pie:
//                initData(sTYPE_CPU);
//                break;
//            case R.id.btn_cpu_speed_pie:
//                initData(sTYPE_CPU_SPEED);
//                break;
//            case R.id.btn_current_pie:
//                initData(sTYPE_CURRENT);
//                break;
//            case R.id.btn_fps_pie:
//                initData(sTYPE_FPS);
//                break;
//            case R.id.btn_gpu_pie:
//                initData(sTYPE_GPU);
//                break;
            case R.id.btn_network_status_pie:
                initData(sTYPE_NETWORK_STATUS);
                break;
//            case R.id.btn_temperature_pie:
//                initData(sTYPE_TEMPERATURE);
//                break;
//            case R.id.btn_ui_stall_duration_pie:
//                initData(sTYPE_UI);
//                break;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        DataHelper.getInstance().appendPieChartView(mPieChartView, msg.what);
        mHandler.sendEmptyMessageDelayed(msg.what, Utils.sINTERVAL_UPDATE_DATA);
    }

    @Override
    public void onDisplayRemoved() {
        DataHelper.getInstance().clearData(mPieChartView);
    }
}
