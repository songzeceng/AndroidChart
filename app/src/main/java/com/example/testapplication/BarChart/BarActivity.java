package com.example.testapplication.BarChart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.example.testapplication.R;
import com.example.testapplication.WeakHandler;
import com.example.testapplication.interfaces.IHandler;
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

public class BarActivity extends Activity implements View.OnClickListener, IHandler {
    BarChartView mBarChartView;
    private HorizontalScrollView mScrollContainer;

    private WeakHandler mHandler = new WeakHandler(this);

    private void initData(int type) {
        mHandler.removeCallbacksAndMessages(null);

        DataHelper.getInstance().initData(mBarChartView, type);
        mHandler.sendEmptyMessageDelayed(type, Utils.sINTERVAL_UPDATE_DATA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);

        mBarChartView = findViewById(R.id.bar_chart_view);
        mScrollContainer = findViewById(R.id.scroll_container);
        mScrollContainer.getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> mScrollContainer.post(()
                        -> mScrollContainer.fullScroll(View.FOCUS_RIGHT)));

        findViewById(R.id.btn_cpu).setOnClickListener(this);
        findViewById(R.id.btn_cpu_speed).setOnClickListener(this);
        findViewById(R.id.btn_current).setOnClickListener(this);
        findViewById(R.id.btn_fps).setOnClickListener(this);
        findViewById(R.id.btn_gpu).setOnClickListener(this);
        findViewById(R.id.btn_memory).setOnClickListener(this);
        findViewById(R.id.btn_network_status).setOnClickListener(this);
        findViewById(R.id.btn_temperature).setOnClickListener(this);
        findViewById(R.id.btn_ui_stall_duration).setOnClickListener(this);

        initData(sTYPE_MEMORY);
    }

    @Override
    public void handleMessage(Message msg) {
        DataHelper.getInstance().appendBarChartData(mBarChartView, msg.what);
        mHandler.sendEmptyMessageDelayed(msg.what, Utils.sINTERVAL_UPDATE_DATA);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_memory:
                initData(sTYPE_MEMORY);
                break;
            case R.id.btn_cpu:
                initData(sTYPE_CPU);
                break;
            case R.id.btn_cpu_speed:
                initData(sTYPE_CPU_SPEED);
                break;
            case R.id.btn_current:
                initData(sTYPE_CURRENT);
                break;
            case R.id.btn_fps:
                initData(sTYPE_FPS);
                break;
            case R.id.btn_gpu:
                initData(sTYPE_GPU);
                break;
            case R.id.btn_network_status:
                initData(sTYPE_NETWORK_STATUS);
                break;
            case R.id.btn_temperature:
                initData(sTYPE_TEMPERATURE);
                break;
            case R.id.btn_ui_stall_duration:
                initData(sTYPE_UI);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataHelper.getInstance().clearData(mBarChartView);
    }
}
