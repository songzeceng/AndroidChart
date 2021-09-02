package com.example.testapplication.BarChart;

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

public class BarActivity extends Activity implements View.OnClickListener, IHandler {

    BarChartView mBarChartView;
    private HorizontalScrollView mScrollContainer;

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
                initChart(mMemoryFakeData);
                break;
            case sTYPE_FPS:
                for (int i = 0; i < mFpsFakeData.length; i++) {
                    mFpsFakeData[i] = mRandom.nextInt(80) + 20; // [20, 100)
                }
                initChart(mFpsFakeData);
                break;
            case sTYPE_CPU:
                for (int i = 0; i < mCPUFakeData.length; i++) {
                    mCPUFakeData[i] = mRandom.nextDouble(); // [0, 1)
                }
                initChart(mCPUFakeData);
                break;
            case sTYPE_UI:
                for (int i = 0; i < mUIStallFakeData.length; i++) {
                    mUIStallFakeData[i] = mRandom.nextInt(400) + 600; // [400, 1000)
                }
                initChart(mUIStallFakeData);
                break;
            case sTYPE_GPU:
                for (int i = 0; i < mGPUFakeData.length; i++) {
                    mGPUFakeData[i] = mRandom.nextDouble() * 6 - 1; // [-1, 5)
                }
                initChart(mGPUFakeData);
                break;
            case sTYPE_TEMPERATURE:
                for (int i = 0; i < mTemperatureFakeData.length; i++) {
                    mTemperatureFakeData[i] = mRandom.nextInt(25) + 10; // [25, 35)
                }
                initChart(mTemperatureFakeData);
                break;
            case sTYPE_CURRENT:
                for (int i = 0; i < mCurrentFakeData.length; i++) {
                    mCurrentFakeData[i] = mRandom.nextDouble() * 3 - 1; // [-1, 2)
                }
                initChart(mCurrentFakeData);
                break;
            case sTYPE_NETWORK_STATUS:
                for (int i = 0; i < mNetworkStatusFakeData.length; i++) {
                    mNetworkStatusFakeData[i] = mRandom.nextInt(11) - 2; // [-2, 9)
                }
                initChart(mNetworkStatusFakeData);
                break;
            case sTYPE_CPU_SPEED:
                for (int i = 0; i < mCPUSpeedFakeData.length; i++) {
                    mCPUSpeedFakeData[i] = mRandom.nextDouble() * 5; // [0, 5)
                }
                initChart(mCPUSpeedFakeData);
                break;
        }
        mHandler.sendEmptyMessageDelayed(type, Utils.sINTERVAL_UPDATE_DATA);
    }

    private void initChart(int[] dataArray) {
        List<BarChartView.Data> dataDouble = new ArrayList<>();
        for (int value : dataArray) {
            dataDouble.add(new BarChartView.Data((double) value, new Date()));
        }
        mBarChartView.setData(dataDouble);
    }

    private void initChart(double[] dataArray) {
        List<BarChartView.Data> dataDouble = new ArrayList<>();
        for (double value : dataArray) {
            dataDouble.add(new BarChartView.Data(value, new Date()));
        }
        mBarChartView.setData(dataDouble);
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

        findViewById(R.id.btn_cpu_bar).setOnClickListener(this);
        findViewById(R.id.btn_cpu_speed_bar).setOnClickListener(this);
        findViewById(R.id.btn_current_bar).setOnClickListener(this);
        findViewById(R.id.btn_fps_bar).setOnClickListener(this);
        findViewById(R.id.btn_gpu_bar).setOnClickListener(this);
        findViewById(R.id.btn_memory_bar).setOnClickListener(this);
        findViewById(R.id.btn_network_status_bar).setOnClickListener(this);
        findViewById(R.id.btn_temperature_bar).setOnClickListener(this);
        findViewById(R.id.btn_ui_stall_duration_bar).setOnClickListener(this);

        initData(sTYPE_MEMORY);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case sTYPE_MEMORY:
                mBarChartView.appendData(new BarChartView.Data((double) (mRandom.nextInt(200) + 900), new Date()));
                break;
            case sTYPE_FPS:
                mBarChartView.appendData(new BarChartView.Data((double) (mRandom.nextInt(80) + 20), new Date()));
                break;
            case sTYPE_CPU:
                mBarChartView.appendData(new BarChartView.Data(mRandom.nextDouble(),  new Date()));
                break;
            case sTYPE_UI:
                mBarChartView.appendData(new BarChartView.Data((double) (mRandom.nextInt(600) + 400), new Date()));
                break;
            case sTYPE_GPU:
                mBarChartView.appendData(new BarChartView.Data(mRandom.nextDouble() * 6 - 1, new Date()));
                break;
            case sTYPE_TEMPERATURE:
                mBarChartView.appendData(new BarChartView.Data((double) (mRandom.nextInt(10) + 25), new Date()));
                break;
            case sTYPE_CURRENT:
                mBarChartView.appendData(new BarChartView.Data(mRandom.nextDouble() * 3 - 1, new Date()));
                break;
            case sTYPE_NETWORK_STATUS:
                mBarChartView.appendData(new BarChartView.Data((double) (mRandom.nextInt(11) - 2), new Date()));
                break;
            case sTYPE_CPU_SPEED:
                mBarChartView.appendData(new BarChartView.Data(mRandom.nextDouble() * 5, new Date()));
                break;
        }
        mHandler.sendEmptyMessageDelayed(msg.what, Utils.sINTERVAL_UPDATE_DATA);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
}
