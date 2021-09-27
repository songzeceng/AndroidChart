package com.example.testapplication.service;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.PopupWindow;

import com.example.testapplication.BarChart.BarChartView;
import com.example.testapplication.LineChart.LineChartView;
import com.example.testapplication.PieChart.PieChartView;
import com.example.testapplication.R;
import com.example.testapplication.WeakHandler;
import com.example.testapplication.interfaces.IHandler;
import com.example.testapplication.interfaces.IPopupWindowListener;
import com.example.testapplication.utils.DataHelper;

import java.util.Date;
import java.util.List;

import static com.example.testapplication.utils.Utils.sTYPE_CHART_BAR;
import static com.example.testapplication.utils.Utils.sTYPE_CHART_LINE;
import static com.example.testapplication.utils.Utils.sTYPE_CHART_PIE;
import static com.example.testapplication.utils.Utils.sTYPE_CPU;
import static com.example.testapplication.utils.Utils.sTYPE_CPU_SPEED;
import static com.example.testapplication.utils.Utils.sTYPE_CURRENT;
import static com.example.testapplication.utils.Utils.sTYPE_FPS;
import static com.example.testapplication.utils.Utils.sTYPE_GPU;
import static com.example.testapplication.utils.Utils.sTYPE_MEMORY;
import static com.example.testapplication.utils.Utils.sTYPE_NETWORK_STATUS;
import static com.example.testapplication.utils.Utils.sTYPE_TEMPERATURE;
import static com.example.testapplication.utils.Utils.sTYPE_UI;

public class DrawChartPopupWindow implements PopupWindow.OnDismissListener, View.OnClickListener, IHandler {
    private PopupWindow mPopupWindow;
    private Activity mActivity;
    private IPopupWindowListener mListener;
    private View mParentView;
    private int mWidth;
    private int mHeight;
    private View mPopupWindowView;
    private boolean focusable;
    private int mChartType;

    private LineChartView mLineChartView;
    private BarChartView mBarChartView;
    private PieChartView mPieChartView;
    private HorizontalScrollView mScrollContainer;

    private WeakHandler mHandler = new WeakHandler(this);

    public DrawChartPopupWindow(int chartType, View parentView, View contentView, IPopupWindowListener listener, int width, int
            height, Activity activity, boolean focusable) {

        mChartType = chartType;
        mListener = listener;
        mParentView = parentView;
        mWidth = width;
        mHeight = height;
        this.focusable = focusable;
        mPopupWindowView = contentView;
        mActivity = activity;
    }

    public void showView() {
        mPopupWindow = new PopupWindow();
        mPopupWindow.setWidth(mWidth);
        mPopupWindow.setHeight(mHeight);
        mPopupWindow.setFocusable(focusable);
        mPopupWindow.setContentView(mPopupWindowView);

        switch (mChartType) {
            case sTYPE_CHART_LINE:
                mLineChartView = (LineChartView) mPopupWindowView.findViewById(R.id.line_chart_view);
                break;
            case sTYPE_CHART_BAR:
                mBarChartView = (BarChartView) mPopupWindowView.findViewById(R.id.bar_chart_view);
                break;
            case sTYPE_CHART_PIE:
                mPieChartView = (PieChartView) mPopupWindowView.findViewById(R.id.pie_chart_view);
                break;
        }

        WindowManager.LayoutParams lp = mActivity.getWindow()
                .getAttributes();
        lp.alpha = 1.0f;
        (mActivity).getWindow().setAttributes(lp);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        mScrollContainer = mPopupWindowView.findViewById(R.id.scroll_container);
        mScrollContainer.getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> mScrollContainer.post(()
                        -> mScrollContainer.fullScroll(View.FOCUS_RIGHT)));

        mPopupWindowView.findViewById(R.id.btn_cpu).setOnClickListener(this);
        mPopupWindowView.findViewById(R.id.btn_cpu_speed).setOnClickListener(this);
        mPopupWindowView.findViewById(R.id.btn_current).setOnClickListener(this);
        mPopupWindowView.findViewById(R.id.btn_fps).setOnClickListener(this);
        mPopupWindowView.findViewById(R.id.btn_gpu).setOnClickListener(this);
        mPopupWindowView.findViewById(R.id.btn_memory).setOnClickListener(this);
        mPopupWindowView.findViewById(R.id.btn_network_status).setOnClickListener(this);
        mPopupWindowView.findViewById(R.id.btn_temperature).setOnClickListener(this);
        mPopupWindowView.findViewById(R.id.btn_ui_stall_duration).setOnClickListener(this);

        mScrollContainer.post(() -> {
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009688")));
            mPopupWindow.showAtLocation(mParentView, Gravity.CENTER, 0, 0);
            mPopupWindow.update();
            mPopupWindow.setOnDismissListener(this);
        });
    }

    public void initPieChartData(List<PieChartView.Data> data) {
        if (mPieChartView == null) {
            return;
        }

        DataHelper.getInstance().initPieChart(mPieChartView, data);
        showWindow();
    }

    private void showWindow() {
        mPopupWindow.update();
        mPopupWindow.showAtLocation(mParentView, Gravity.CENTER, 0, 0);
    }

    public void initBarChartData(double[] data) {
        if (mBarChartView == null) {
            return;
        }

        DataHelper.getInstance().initBarChart(mBarChartView, data);
        showWindow();
    }

    public void initLineChartData(double[] data, double ruler_value) {
        if (mLineChartView == null) {
            return;
        }

        DataHelper.getInstance().initLineChart(mLineChartView, data, ruler_value);
        showWindow();
    }

    public void updatePieChart(PieChartView.Data data) {
        if (mPieChartView == null || data == null) {
            return;
        }
        mPieChartView.appendData(data);
        if (!mPopupWindow.isShowing()) {
            showWindow();
        }
    }

    public void updateBarChart(double data, Date date) {
        if (mBarChartView == null) {
            return;
        }
        mBarChartView.appendData(new BarChartView.Data(data, date));
        if (!mPopupWindow.isShowing()) {
            showWindow();
        }
    }

    public void updateLineChart(double data, Date date) {
        if (mLineChartView == null) {
            return;
        }
        mLineChartView.appendData(new LineChartView.Data(data, date));
        if (!mPopupWindow.isShowing()) {
            showWindow();
        }
    }

    /**
     * 点击悬浮窗外面时的操作
     */
    @Override
    public void onDismiss() {
        DataHelper.getInstance().clearData(mLineChartView);
//        setBackgroundAlpha(1f);
    }

    private void initLineChartData(int type) {
        mHandler.removeCallbacksAndMessages(null);

        DataHelper.getInstance().initData(mLineChartView, type);

//        mHandler.sendEmptyMessageDelayed(type, Utils.sINTERVAL_UPDATE_DATA);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_memory:
                mListener.changeData(mChartType, sTYPE_MEMORY);
                break;
            case R.id.btn_cpu:
                mListener.changeData(mChartType, sTYPE_CPU);
                break;
            case R.id.btn_cpu_speed:
                mListener.changeData(mChartType, sTYPE_CPU_SPEED);
                break;
            case R.id.btn_current:
                mListener.changeData(mChartType, sTYPE_CURRENT);
                break;
            case R.id.btn_fps:
                mListener.changeData(mChartType, sTYPE_FPS);
                break;
            case R.id.btn_gpu:
                mListener.changeData(mChartType, sTYPE_GPU);
                break;
            case R.id.btn_network_status:
                mListener.changeData(mChartType, sTYPE_NETWORK_STATUS);
                break;
            case R.id.btn_temperature:
                mListener.changeData(mChartType, sTYPE_TEMPERATURE);
                break;
            case R.id.btn_ui_stall_duration:
                mListener.changeData(mChartType, sTYPE_UI);
                break;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        //        DataHelper.getInstance().appendLineChartData(mLineChartView, msg.what);
//        mPresentation.show();

//        mHandler.sendEmptyMessageDelayed(msg.what, Utils.sINTERVAL_UPDATE_DATA);
    }

//    /**
//     * 隐藏PopupWindow
//     */
//    public void dismiss() {
//        if (mPopupWindow != null) {
//            mPopupWindow.dismiss();
//            mPopupWindow = null;
//        }
//    }
//
//    //设置屏幕背景透明效果
//    public void setBackgroundAlpha(float alpha) {
//        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        lp.alpha = alpha;
//        mActivity.getWindow().setAttributes(lp);
//    }
}
