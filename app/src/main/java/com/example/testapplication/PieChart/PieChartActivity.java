package com.example.testapplication.PieChart;

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

public class PieChartActivity extends Activity implements View.OnClickListener, IHandler {

    PieChartView mPieChartView;
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
    private int[] mTemperatureFakeData = new int[11];

    // current fake data
    private double[] mCurrentFakeData = new double[sINIT_DATA_COUNT];

    // network status fake data
    private int[] mNetworkStatusFakeData = new int[11];

    // cpu speed fake data
    private double[] mCPUSpeedFakeData = new double[sINIT_DATA_COUNT];

    private WeakHandler mHandler = new WeakHandler(this);
    private Random mRandom = new Random();

    private List<PieChartView.Data> mData = new ArrayList<>();

    private void initData(int type) {
        mRandom.setSeed(System.currentTimeMillis());
        mHandler.removeCallbacksAndMessages(null);

        mData.clear();

        switch (type) {
//            case sTYPE_MEMORY:
//                for (int i = 0; i < mMemoryFakeData.length; i++) {
//                    mMemoryFakeData[i] = mRandom.nextInt(200) + 900; // [900, 1100)
//                }
//                initChart(mMemoryFakeData);
//                break;
//            case sTYPE_FPS:
//                for (int i = 0; i < mFpsFakeData.length; i++) {
//                    mFpsFakeData[i] = mRandom.nextInt(80) + 20; // [20, 100)
//                }
//                initChart(mFpsFakeData);
//                break;
//            case sTYPE_CPU:
//                for (int i = 0; i < mCPUFakeData.length; i++) {
//                    mCPUFakeData[i] = mRandom.nextDouble(); // [0, 1)
//                }
//                initChart(mCPUFakeData);
//                break;
//            case sTYPE_UI:
//                for (int i = 0; i < mUIStallFakeData.length; i++) {
//                    mUIStallFakeData[i] = mRandom.nextInt(400) + 600; // [400, 1000)
//                }
//                initChart(mUIStallFakeData);
//                break;
//            case sTYPE_GPU:
//                for (int i = 0; i < mGPUFakeData.length; i++) {
//                    mGPUFakeData[i] = mRandom.nextDouble() * 6 - 1; // [-1, 5)
//                }
//                initChart(mGPUFakeData);
//                break;
//            case sTYPE_TEMPERATURE:
//                for (int i = 0; i < mTemperatureFakeData.length; i++) {
//                    mTemperatureFakeData[i] = mRandom.nextInt(25) + 10; // [25, 35)
//                }
//                initChart(mTemperatureFakeData);
//                break;
//            case sTYPE_CURRENT:
//                for (int i = 0; i < mCurrentFakeData.length; i++) {
//                    mCurrentFakeData[i] = mRandom.nextDouble() * 3 - 1; // [-1, 2)
//                }
//                initChart(mCurrentFakeData);
//                break;
            case sTYPE_NETWORK_STATUS:
                for (int i = 0; i < sINIT_DATA_COUNT; i++) {
                    int index = mRandom.nextInt(11);
                    mNetworkStatusFakeData[index]++; // [-2, 9)
                    PieChartView.Data.addData(mData,
                            new PieChartView.Data(mNetworkStatusFakeData[index], new Date(), String.valueOf(index)));
                }
                break;
//            case sTYPE_CPU_SPEED:
//                for (int i = 0; i < mCPUSpeedFakeData.length; i++) {
//                    mCPUSpeedFakeData[i] = mRandom.nextDouble() * 5; // [0, 5)
//                }
//                initChart(mCPUSpeedFakeData);
//                break;
        }

        mPieChartView.setData(mData);
        mHandler.sendEmptyMessageDelayed(type, Utils.sINTERVAL_UPDATE_DATA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie);

        mPieChartView = findViewById(R.id.pie_chart_view);
        mScrollContainer = findViewById(R.id.scroll_container);
        mScrollContainer.getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> mScrollContainer.post(()
                        -> mScrollContainer.fullScroll(View.FOCUS_RIGHT)));

//        findViewById(R.id.btn_cpu_pie).setOnClickListener(this);
//        findViewById(R.id.btn_cpu_speed_pie).setOnClickListener(this);
//        findViewById(R.id.btn_current_pie).setOnClickListener(this);
//        findViewById(R.id.btn_fps_pie).setOnClickListener(this);
//        findViewById(R.id.btn_gpu_pie).setOnClickListener(this);
//        findViewById(R.id.btn_memory_pie).setOnClickListener(this);
        findViewById(R.id.btn_network_status_pie).setOnClickListener(this);
//        findViewById(R.id.btn_temperature_pie).setOnClickListener(this);
//        findViewById(R.id.btn_ui_stall_duration_pie).setOnClickListener(this);

        initData(sTYPE_NETWORK_STATUS);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
//            case sTYPE_MEMORY:
//                mPieChartView.appendData(new PieChartView.Data((double) (mRandom.nextInt(200) + 900), new Date()));
//                break;
//            case sTYPE_FPS:
//                mPieChartView.appendData(new PieChartView.Data((double) (mRandom.nextInt(80) + 20), new Date()));
//                break;
//            case sTYPE_CPU:
//                mPieChartView.appendData(new PieChartView.Data(mRandom.nextDouble(),  new Date()));
//                break;
//            case sTYPE_UI:
//                mPieChartView.appendData(new PieChartView.Data((double) (mRandom.nextInt(600) + 400), new Date()));
//                break;
//            case sTYPE_GPU:
//                mPieChartView.appendData(new PieChartView.Data(mRandom.nextDouble() * 6 - 1, new Date()));
//                break;
//            case sTYPE_TEMPERATURE:
//                mPieChartView.appendData(new PieChartView.Data((double) (mRandom.nextInt(10) + 25), new Date()));
//                break;
//            case sTYPE_CURRENT:
//                mPieChartView.appendData(new PieChartView.Data(mRandom.nextDouble() * 3 - 1, new Date()));
//                break;
            case sTYPE_NETWORK_STATUS:
                int newData = mRandom.nextInt(11);
                mNetworkStatusFakeData[newData]++;
                mPieChartView.appendData(new PieChartView.Data(mNetworkStatusFakeData[newData], new Date(), String.valueOf(newData)));
                break;
//            case sTYPE_CPU_SPEED:
//                mPieChartView.appendData(new PieChartView.Data(mRandom.nextDouble() * 5, new Date()));
//                break;
        }
        mHandler.sendEmptyMessageDelayed(msg.what, Utils.sINTERVAL_UPDATE_DATA);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
}
