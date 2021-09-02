package com.example.testapplication.LineChart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.example.testapplication.IHandler;
import com.example.testapplication.R;
import com.example.testapplication.WeakHandler;
import com.example.testapplication.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.example.testapplication.utils.Utils.*;

public class LineChartActivity extends Activity implements IHandler, View.OnClickListener {
    LineChartView mLineChartView;
    private HorizontalScrollView mScrollContainer;

    private double mRulerValue = 10;

    // memory fake data
    private int[] mMemoryFakeData = new int[sINIT_DATA_COUNT];

    // fps fake data
    private int[] mFpsFakeData = new int[sINIT_DATA_COUNT];

    // cpu fake data
    private double[] mCPUFakeData = new double[sINIT_DATA_COUNT];

    // ui stall duration fake data
    private int[] mUIStallFakeData = new int[sINIT_DATA_COUNT];

    // gpu fake data
    private double[] mGPUFakeData = new double[sINIT_DATA_COUNT];

    // temperature fake data
    private int[] mTemperatureFakeData = new int[sINIT_DATA_COUNT];

    // current fake data
    private double[] mCurrentFakeData = new double[sINIT_DATA_COUNT];

    // network status fake data
    private int[] mNetworkStatusFakeData = new int[sINIT_DATA_COUNT];

    // cpu speed fake data
    private double[] mCPUSpeedFakeData = new double[sINIT_DATA_COUNT];

    private WeakHandler mHandler = new WeakHandler(this);
    private Random mRandom = new Random();

    private void initData(int type) {
        mRandom.setSeed(System.currentTimeMillis());
        mHandler.removeCallbacksAndMessages(null);

        switch (type) {
            case sTYPE_MEMORY:
                for (int i = 0; i < mMemoryFakeData.length; i++) {
                    mMemoryFakeData[i] = mRandom.nextInt(200) + 900; // [900, 1100)
                }
                mRulerValue = 200;
                initChart(mMemoryFakeData);
                break;
            case sTYPE_FPS:
                for (int i = 0; i < mFpsFakeData.length; i++) {
                    mFpsFakeData[i] = mRandom.nextInt(80) + 20; // [20, 100)
                }
                mRulerValue = 10;
                initChart(mFpsFakeData);
                break;
            case sTYPE_CPU:
                for (int i = 0; i < mCPUFakeData.length; i++) {
                    mCPUFakeData[i] = mRandom.nextDouble(); // [0, 1)
                }
                mRulerValue = 0.2;
                initChart(mCPUFakeData);
                break;
            case sTYPE_UI:
                for (int i = 0; i < mUIStallFakeData.length; i++) {
                    mUIStallFakeData[i] = mRandom.nextInt(400) + 600; // [400, 1000)
                }
                mRulerValue = 200;
                initChart(mUIStallFakeData);
                break;
            case sTYPE_GPU:
                for (int i = 0; i < mGPUFakeData.length; i++) {
                    mGPUFakeData[i] = mRandom.nextDouble() * 6 - 1; // [-1, 5)
                }
                mRulerValue = 1;
                initChart(mGPUFakeData);
                break;
            case sTYPE_TEMPERATURE:
                for (int i = 0; i < mTemperatureFakeData.length; i++) {
                    mTemperatureFakeData[i] = mRandom.nextInt(25) + 10; // [25, 35)
                }
                mRulerValue = 5;
                initChart(mTemperatureFakeData);
                break;
            case sTYPE_CURRENT:
                for (int i = 0; i < mCurrentFakeData.length; i++) {
                    mCurrentFakeData[i] = mRandom.nextDouble() * 3 - 1; // [-1, 2)
                }
                mRulerValue = 0.2;
                initChart(mCurrentFakeData);
                break;
            case sTYPE_NETWORK_STATUS:
                for (int i = 0; i < mNetworkStatusFakeData.length; i++) {
                    mNetworkStatusFakeData[i] = mRandom.nextInt(11) - 2; // [-2, 9)
                }
                mRulerValue = 1;
                initChart(mNetworkStatusFakeData);
                break;
            case sTYPE_CPU_SPEED:
                for (int i = 0; i < mCPUSpeedFakeData.length; i++) {
                    mCPUSpeedFakeData[i] = mRandom.nextDouble() * 5; // [0, 5)
                }
                mRulerValue = 0.5;
                initChart(mCPUSpeedFakeData);
                break;
        }
        mHandler.sendEmptyMessageDelayed(type, Utils.sINTERVAL_UPDATE_DATA);
    }

    private void initChart(int[] dataArray) {
        List<LineChartView.Data<Integer>> datas = new ArrayList<>();
        for (int value : dataArray) {
            LineChartView.Data<Integer> data = new LineChartView.Data<>(value, new Date());
            datas.add(data);
        }

        mLineChartView.setRulerYSpace(mRulerValue);
        mLineChartView.setData(datas);
    }

    private void initChart(double[] dataArray) {
        List<LineChartView.Data<Double>> datas = new ArrayList<>();
        for (double value : dataArray) {
            LineChartView.Data<Double> data = new LineChartView.Data<>((double) value, new Date());
            datas.add(data);
        }

        mLineChartView.setRulerYSpace(mRulerValue);
        mLineChartView.setData(datas);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);

        mLineChartView = (LineChartView) findViewById(R.id.line_chart_view);
        mScrollContainer = findViewById(R.id.scroll_container);
        mScrollContainer.getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> mScrollContainer.post(()
                        -> mScrollContainer.fullScroll(View.FOCUS_RIGHT)));

        findViewById(R.id.btn_cpu_line).setOnClickListener(this);
        findViewById(R.id.btn_cpu_speed_line).setOnClickListener(this);
        findViewById(R.id.btn_current_line).setOnClickListener(this);
        findViewById(R.id.btn_fps_line).setOnClickListener(this);
        findViewById(R.id.btn_gpu_line).setOnClickListener(this);
        findViewById(R.id.btn_memory_line).setOnClickListener(this);
        findViewById(R.id.btn_network_status_line).setOnClickListener(this);
        findViewById(R.id.btn_temperature_line).setOnClickListener(this);
        findViewById(R.id.btn_ui_stall_duration_line).setOnClickListener(this);

        initData(sTYPE_MEMORY);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case sTYPE_MEMORY:
                mLineChartView.appendData(new LineChartView.Data<Integer>(mRandom.nextInt(200) + 900,
                        new Date()));
                break;
            case sTYPE_FPS:
                mLineChartView.appendData(new LineChartView.Data<Integer>(mRandom.nextInt(80) + 20,
                        new Date()));
                break;
            case sTYPE_CPU:
                mLineChartView.appendData(new LineChartView.Data<Double>(mRandom.nextDouble(),  new Date()));
                break;
            case sTYPE_UI:
                mLineChartView.appendData(new LineChartView.Data<Integer>(mRandom.nextInt(600) + 400,
                        new Date()));
                break;
            case sTYPE_GPU:
                mLineChartView.appendData(new LineChartView.Data<Double>(mRandom.nextDouble() * 6 - 1,
                        new Date()));
                break;
            case sTYPE_TEMPERATURE:
                mLineChartView.appendData(new LineChartView.Data<Integer>(mRandom.nextInt(10) + 25,
                        new Date()));
                break;
            case sTYPE_CURRENT:
                mLineChartView.appendData(new LineChartView.Data<Double>(mRandom.nextDouble() * 3 - 1,
                        new Date()));
                break;
            case sTYPE_NETWORK_STATUS:
                mLineChartView.appendData(new LineChartView.Data<Integer>(mRandom.nextInt(11) - 2,
                        new Date()));
                break;
            case sTYPE_CPU_SPEED:
                mLineChartView.appendData(new LineChartView.Data<Double>(mRandom.nextDouble() * 5,
                        new Date()));
                break;
        }
        mHandler.sendEmptyMessageDelayed(msg.what, Utils.sINTERVAL_UPDATE_DATA);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
}
