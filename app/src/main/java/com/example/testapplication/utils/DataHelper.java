package com.example.testapplication.utils;

import android.view.SurfaceView;

import com.example.testapplication.BarChart.BarChartView;
import com.example.testapplication.LineChart.LineChartView;
import com.example.testapplication.PieChart.PieChartView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.example.testapplication.utils.Utils.*;

public class DataHelper {
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

    // network status fake data
    private int[] mNetworkStatusFakeDataForPie = new int[sINIT_DATA_COUNT];

    // cpu speed fake data
    private double[] mCPUSpeedFakeData = new double[sINIT_DATA_COUNT];

    private List<PieChartView.Data> mPieData = new ArrayList<>();

    private Random mRandom = new Random();

    private static DataHelper sInstance;

    public static DataHelper getInstance() {
        if (sInstance == null) {
            synchronized (DataHelper.class) {
                if (sInstance == null) {
                    sInstance = new DataHelper();
                }
            }
        }
        return sInstance;
    }

    public void initData(SurfaceView view, int type) {
        if (view == null || (!(view instanceof BarChartView) && !(view instanceof LineChartView) && !(view instanceof PieChartView))) {
            return;
        }

        mRandom.setSeed(System.currentTimeMillis());

        if (view instanceof PieChartView) {
            mPieData.clear();

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
                        int index = mRandom.nextInt(10);
                        mNetworkStatusFakeDataForPie[index]++; // [-2, 9)
                        PieChartView.Data.addData(mPieData,
                                new PieChartView.Data(mNetworkStatusFakeData[index], String.valueOf(index)));
                    }
                    break;
//            case sTYPE_CPU_SPEED:
//                for (int i = 0; i < mCPUSpeedFakeData.length; i++) {
//                    mCPUSpeedFakeData[i] = mRandom.nextDouble() * 5; // [0, 5)
//                }
//                initChart(mCPUSpeedFakeData);
//                break;
            }

            ((PieChartView) view).setData(mPieData);

            return;
        }

        switch (type) {
            case sTYPE_MEMORY:
                for (int i = 0; i < mMemoryFakeData.length; i++) {
                    mMemoryFakeData[i] = mRandom.nextInt(200) + 900; // [900, 1100)
                }
                initChart(view, mMemoryFakeData, type);
                break;
            case sTYPE_FPS:
                for (int i = 0; i < mFpsFakeData.length; i++) {
                    mFpsFakeData[i] = mRandom.nextInt(80) + 20; // [20, 100)
                }
                initChart(view, mFpsFakeData, type);
                break;
            case sTYPE_CPU:
                for (int i = 0; i < mCPUFakeData.length; i++) {
                    mCPUFakeData[i] = mRandom.nextDouble(); // [0, 1)
                }
                initChart(view, mCPUFakeData, type);
                break;
            case sTYPE_UI:
                for (int i = 0; i < mUIStallFakeData.length; i++) {
                    mUIStallFakeData[i] = mRandom.nextInt(400) + 600; // [400, 1000)
                }
                initChart(view, mUIStallFakeData, type);
                break;
            case sTYPE_GPU:
                for (int i = 0; i < mGPUFakeData.length; i++) {
                    mGPUFakeData[i] = mRandom.nextDouble() * 6 - 1; // [-1, 5)
                }
                initChart(view, mGPUFakeData, type);
                break;
            case sTYPE_TEMPERATURE:
                for (int i = 0; i < mTemperatureFakeData.length; i++) {
                    mTemperatureFakeData[i] = mRandom.nextInt(25) + 10; // [25, 35)
                }
                initChart(view, mTemperatureFakeData, type);
                break;
            case sTYPE_CURRENT:
                for (int i = 0; i < mCurrentFakeData.length; i++) {
                    mCurrentFakeData[i] = mRandom.nextDouble() * 3 - 1; // [-1, 2)
                }
                initChart(view, mCurrentFakeData, type);
                break;
            case sTYPE_NETWORK_STATUS:
                for (int i = 0; i < mNetworkStatusFakeData.length; i++) {
                    mNetworkStatusFakeData[i] = mRandom.nextInt(11) - 2; // [-2, 9)
                }
                initChart(view, mNetworkStatusFakeData, type);
                break;
            case sTYPE_CPU_SPEED:
                for (int i = 0; i < mCPUSpeedFakeData.length; i++) {
                    mCPUSpeedFakeData[i] = mRandom.nextDouble() * 5; // [0, 5)
                }
                initChart(view, mCPUSpeedFakeData, type);
                break;
        }
    }

    public void initRawDataForBar(double[] data, int type) {
        if (Utils.arrayEmpty(data)) {
            throw new IllegalArgumentException("Data to be initialized must not be empty.");
        }

        mRandom.setSeed(System.currentTimeMillis());

        switch (type) {
            case sTYPE_MEMORY:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextInt(200) + 900; // [900, 1100)
                }
                break;
            case sTYPE_FPS:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextInt(80) + 20; // [20, 100)
                }
                break;
            case sTYPE_CPU:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextDouble(); // [0, 1)
                }
                break;
            case sTYPE_UI:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextInt(400) + 600; // [400, 1000)
                }
                break;
            case sTYPE_GPU:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextDouble() * 6 - 1; // [-1, 5)
                }
                break;
            case sTYPE_TEMPERATURE:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextInt(25) + 10; // [25, 35)
                }
                break;
            case sTYPE_CURRENT:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextDouble() * 3 - 1; // [-1, 2)
                }
                break;
            case sTYPE_NETWORK_STATUS:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextInt(11) - 2; // [-2, 9)
                }
                break;
            case sTYPE_CPU_SPEED:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextDouble() * 5; // [0, 5)
                }
                break;
        }
    }

    public double initRawDataForLine(double[] data, int type) {
        if (Utils.arrayEmpty(data)) {
            throw new IllegalArgumentException("Data to be initialized must not be empty.");
        }

        mRandom.setSeed(System.currentTimeMillis());
        double rulerValue = 0;

        switch (type) {
            case sTYPE_MEMORY:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextInt(200) + 900; // [900, 1100)
                }
                rulerValue = 50;
                break;
            case sTYPE_FPS:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextInt(80) + 20; // [20, 100)
                }
                rulerValue = 10;
                break;
            case sTYPE_CPU:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextDouble(); // [0, 1)
                }
                rulerValue = 0.2;
                break;
            case sTYPE_UI:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextInt(400) + 600; // [400, 1000)
                }
                rulerValue = 200;
                break;
            case sTYPE_GPU:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextDouble() * 6 - 1; // [-1, 5)
                }
                rulerValue = 1;
                break;
            case sTYPE_TEMPERATURE:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextInt(25) + 10; // [25, 35)
                }
                rulerValue = 5;
                break;
            case sTYPE_CURRENT:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextDouble() * 3 - 1; // [-1, 2)
                }
                rulerValue = 0.2;
                break;
            case sTYPE_NETWORK_STATUS:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextInt(11) - 2; // [-2, 9)
                }
                rulerValue = 1;
                break;
            case sTYPE_CPU_SPEED:
                for (int i = 0; i < data.length; i++) {
                    data[i] = mRandom.nextDouble() * 5; // [0, 5)
                }
                rulerValue = 0.5;
                break;
        }

        return rulerValue;
    }

    public double generateNewDoubleDataForLineAndBar(int type) {
        mRandom.setSeed(System.currentTimeMillis());
        switch (type) {
            case sTYPE_MEMORY:
                return mRandom.nextInt(200) + 900;
            case sTYPE_FPS:
                return mRandom.nextInt(80) + 20;
            case sTYPE_UI:
                return mRandom.nextInt(400) + 600;
            case sTYPE_TEMPERATURE:
                return mRandom.nextInt(25) + 10;
            case sTYPE_NETWORK_STATUS:
                return mRandom.nextInt(11) - 2;
            case sTYPE_CPU:
                return mRandom.nextDouble();
            case sTYPE_GPU:
                return mRandom.nextDouble() * 6 - 1;
            case sTYPE_CURRENT:
                return mRandom.nextDouble() * 3 - 1;
            case sTYPE_CPU_SPEED:
                return mRandom.nextDouble() * 5; // [0, 5)
        }

        throw new IllegalArgumentException("Type invalid");
    }

    public PieChartView.Data generateNewDataForPie(int[] data, int type) {
        if (Utils.arrayEmpty(data)) {
            return null;
        }

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
                int index = mRandom.nextInt(10);
                return new PieChartView.Data(data[index], String.valueOf(index));
//            case sTYPE_CPU_SPEED:
//                for (int i = 0; i < mCPUSpeedFakeData.length; i++) {
//                    mCPUSpeedFakeData[i] = mRandom.nextDouble() * 5; // [0, 5)
//                }
//                initChart(mCPUSpeedFakeData);
//                break;
        }
        return null;
    }

    public List<PieChartView.Data> initRawDataForPie(int[] data, int type) {
        if (Utils.arrayEmpty(data)) {
            return null;
        }

        List<PieChartView.Data> list = new ArrayList<>();
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
                for (int i = 0; i < data.length; i++) {
                    int index = mRandom.nextInt(10);
                    data[index]++; // [-2, 9)
                    PieChartView.Data.addData(list,
                            new PieChartView.Data(data[index], String.valueOf(index)));
                }
                break;
//            case sTYPE_CPU_SPEED:
//                for (int i = 0; i < mCPUSpeedFakeData.length; i++) {
//                    mCPUSpeedFakeData[i] = mRandom.nextDouble() * 5; // [0, 5)
//                }
//                initChart(mCPUSpeedFakeData);
//                break;
        }

        return list;
    }

    private void initChart(SurfaceView view, int[] data, int type) {
        if (view instanceof LineChartView) {
            int rulerValue = 0;
            switch (type) {
                case sTYPE_MEMORY:
                    rulerValue = 50;
                    break;
                case sTYPE_UI:
                    rulerValue = 200;
                    break;
                case sTYPE_FPS:
                    rulerValue = 10;
                    break;
                case sTYPE_NETWORK_STATUS:
                    rulerValue = 1;
                    break;
                case sTYPE_TEMPERATURE:
                    rulerValue = 5;
                    break;
            }
            initLineChart((LineChartView) view, data, rulerValue);
        } else if (view instanceof BarChartView) {
            initBarChart((BarChartView) view, data);
        }
    }

    private void initChart(SurfaceView view, double[] data, int type) {
        if (view instanceof LineChartView) {
            double rulerValue = 10;
            switch (type) {
                case sTYPE_CPU:
                case sTYPE_CURRENT:
                    rulerValue = 0.2;
                    break;
                case sTYPE_GPU:
                    rulerValue = 1;
                    break;
                case sTYPE_CPU_SPEED:
                    rulerValue = 0.5;
                    break;
            }
            initLineChart((LineChartView) view, data, rulerValue);
        } else if (view instanceof BarChartView) {
            initBarChart((BarChartView) view, data);
        }
    }

    public void initLineChart(LineChartView view, double[] dataArray, double rulerValue) {
        if (Utils.arrayEmpty(dataArray) || rulerValue <= 0) {
            return;
        }

        List<LineChartView.Data<Double>> datas = new ArrayList<>();
        for (double value : dataArray) {
            LineChartView.Data<Double> data = new LineChartView.Data<>((double) value, new Date());
            datas.add(data);
        }

        view.setRulerYSpace(rulerValue);
        view.setData(datas);
    }

    public void initLineChart(LineChartView view, int[] dataArray, int rulerValue) {
        if (Utils.arrayEmpty(dataArray) || rulerValue <= 0) {
            return;
        }

        List<LineChartView.Data<Double>> datas = new ArrayList<>();
        for (double value : dataArray) {
            LineChartView.Data<Double> data = new LineChartView.Data<>((double) value, new Date());
            datas.add(data);
        }

        view.setRulerYSpace(rulerValue);
        view.setData(datas);
    }

    public void initBarChart(BarChartView view, int[] dataArray) {
        if (Utils.arrayEmpty(dataArray)) {
            return;
        }

        List<BarChartView.Data> dataDouble = new ArrayList<>();
        for (int value : dataArray) {
            dataDouble.add(new BarChartView.Data((double) value, new Date()));
        }
        view.setData(dataDouble);
    }

    public void initBarChart(BarChartView view, double[] dataArray) {
        if (Utils.arrayEmpty(dataArray)) {
            return;
        }

        List<BarChartView.Data> dataDouble = new ArrayList<>();
        for (double value : dataArray) {
            dataDouble.add(new BarChartView.Data(value, new Date()));
        }
        view.setData(dataDouble);
    }

    public void initPieChart(PieChartView view, List<PieChartView.Data> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        view.setData(data);
    }

    public void appendLineChartData(LineChartView view, int type) {
        if (view == null) {
            return;
        }

        switch (type) {
            case sTYPE_MEMORY:
                view.appendData(new LineChartView.Data<Integer>(mRandom.nextInt(200) + 900,
                        new Date()));
                break;
            case sTYPE_FPS:
                view.appendData(new LineChartView.Data<Integer>(mRandom.nextInt(80) + 20,
                        new Date()));
                break;
            case sTYPE_CPU:
                view.appendData(new LineChartView.Data<Double>(mRandom.nextDouble(),  new Date()));
                break;
            case sTYPE_UI:
                view.appendData(new LineChartView.Data<Integer>(mRandom.nextInt(600) + 400,
                        new Date()));
                break;
            case sTYPE_GPU:
                view.appendData(new LineChartView.Data<Double>(mRandom.nextDouble() * 6 - 1,
                        new Date()));
                break;
            case sTYPE_TEMPERATURE:
                view.appendData(new LineChartView.Data<Integer>(mRandom.nextInt(10) + 25,
                        new Date()));
                break;
            case sTYPE_CURRENT:
                view.appendData(new LineChartView.Data<Double>(mRandom.nextDouble() * 3 - 1,
                        new Date()));
                break;
            case sTYPE_NETWORK_STATUS:
                view.appendData(new LineChartView.Data<Integer>(mRandom.nextInt(11) - 2,
                        new Date()));
                break;
            case sTYPE_CPU_SPEED:
                view.appendData(new LineChartView.Data<Double>(mRandom.nextDouble() * 5,
                        new Date()));
                break;
        }
    }

    public void appendBarChartData(BarChartView view, int type) {
        if (view == null) {
            return;
        }

        switch (type) {
            case sTYPE_MEMORY:
                view.appendData(new BarChartView.Data((double) (mRandom.nextInt(200) + 900), new Date()));
                break;
            case sTYPE_FPS:
                view.appendData(new BarChartView.Data((double) (mRandom.nextInt(80) + 20), new Date()));
                break;
            case sTYPE_CPU:
                view.appendData(new BarChartView.Data(mRandom.nextDouble(),  new Date()));
                break;
            case sTYPE_UI:
                view.appendData(new BarChartView.Data((double) (mRandom.nextInt(600) + 400), new Date()));
                break;
            case sTYPE_GPU:
                view.appendData(new BarChartView.Data(mRandom.nextDouble() * 6 - 1, new Date()));
                break;
            case sTYPE_TEMPERATURE:
                view.appendData(new BarChartView.Data((double) (mRandom.nextInt(10) + 25), new Date()));
                break;
            case sTYPE_CURRENT:
                view.appendData(new BarChartView.Data(mRandom.nextDouble() * 3 - 1, new Date()));
                break;
            case sTYPE_NETWORK_STATUS:
                view.appendData(new BarChartView.Data((double) (mRandom.nextInt(11) - 2), new Date()));
                break;
            case sTYPE_CPU_SPEED:
                view.appendData(new BarChartView.Data(mRandom.nextDouble() * 5, new Date()));
                break;
        }
    }

    public void appendPieChartView(PieChartView view, int type) {
        switch (type) {
//            case sTYPE_MEMORY:
//                view.appendData(new PieChartView.Data((double) (mRandom.nextInt(200) + 900), new Date()));
//                break;
//            case sTYPE_FPS:
//                view.appendData(new PieChartView.Data((double) (mRandom.nextInt(80) + 20), new Date()));
//                break;
//            case sTYPE_CPU:
//                view.appendData(new PieChartView.Data(mRandom.nextDouble(),  new Date()));
//                break;
//            case sTYPE_UI:
//                view.appendData(new PieChartView.Data((double) (mRandom.nextInt(600) + 400), new Date()));
//                break;
//            case sTYPE_GPU:
//                view.appendData(new PieChartView.Data(mRandom.nextDouble() * 6 - 1, new Date()));
//                break;
//            case sTYPE_TEMPERATURE:
//                view.appendData(new PieChartView.Data((double) (mRandom.nextInt(10) + 25), new Date()));
//                break;
//            case sTYPE_CURRENT:
//                view.appendData(new PieChartView.Data(mRandom.nextDouble() * 3 - 1, new Date()));
//                break;
            case sTYPE_NETWORK_STATUS:
                int newData = mRandom.nextInt(10);
                mNetworkStatusFakeDataForPie[newData]++;
                view.appendData(new PieChartView.Data(mNetworkStatusFakeData[newData], String.valueOf(newData)));
                break;
//            case sTYPE_CPU_SPEED:
//                view.appendData(new PieChartView.Data(mRandom.nextDouble() * 5, new Date()));
//                break;
        }
    }

    public void clearData(SurfaceView view) {
        if (view == null || (!(view instanceof BarChartView) || !(view instanceof LineChartView) || !(view instanceof PieChartView))) {
            return;
        }

        if (view instanceof PieChartView) {
            mPieData.clear();
            for (int i = 0; i < sINIT_DATA_COUNT; i++) {
                mNetworkStatusFakeDataForPie[i] = 0;
            }
            return;
        }

        for (int i = 0; i < sINIT_DATA_COUNT; i++) {
            mCPUFakeData[i] = 0;
            mMemoryFakeData[i] = 0;
            mCPUSpeedFakeData[i] = 0;
            mCurrentFakeData[i] = 0;
            mFpsFakeData[i] = 0;
            mGPUFakeData[i] = 0;
            mNetworkStatusFakeData[i] = 0;
            mTemperatureFakeData[i] = 0;
            mUIStallFakeData[i] = 0;
        }
    }
}
