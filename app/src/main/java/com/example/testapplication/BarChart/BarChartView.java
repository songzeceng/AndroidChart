package com.example.testapplication.BarChart;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testapplication.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BarChartView extends SurfaceView implements SurfaceHolder.Callback {
    private int defaultLineColor = Color.parseColor("#FF4081");
    private int defaultBorderColor = Color.parseColor("#BBBBBB");
    private int titleTextColor = Color.argb(255, 217, 217, 217);
    private int labelTextColor = Color.GRAY;
    private int dataColor = Color.parseColor("#009688");
    private int descriptionColor = defaultBorderColor;
    private int mTitleTextSize = 42;
    private int mLabelTextSize = 20;
    private int descriptionTextSize = 20;
    private int mWidth;
    private int mHeight;
    private float perBarW = 30;
    private int mLeftTextSpace = 80;
    private int mBottomTextSpace = 20;
    private int mTopTextSpace = 50;
    private float scale = 0.5f;
    protected Paint mBorderLinePaint;
    protected Paint mDataLinePaint;
    private Double maxData;
    private Double minData;
    private int tablePadding;

    private List<Data> mDatas = new ArrayList<>();;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);

    /**
     * 备注文本画笔
     */
    private Paint mTextPaint;
    /**
     * 标题文本画笔
     */
    private Paint mTitleTextPaint;

    private float dataTextSize = 20;
    private int stepStart;
    private int stepEnd;

    private boolean mRunning = true;
    private SurfaceHolder mHolder;
    private Thread mDrawThread = new Thread() {
        @Override
        public void run() {
            try {
                while (mRunning) {
                    Canvas canvas;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        canvas = mHolder.getSurface().lockHardwareCanvas();
                    } else {
                        canvas = mHolder.lockCanvas();
                    }

                    drawCanvas(canvas);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        mHolder.getSurface().unlockCanvasAndPost(canvas);
                    } else {
                        mHolder.unlockCanvasAndPost(canvas);
                    }

                    Thread.sleep(Utils.sINTERVAL_UPDATE_CHART);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public BarChartView(@NonNull Context context) {
        super(context);
        init();
    }

    public BarChartView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BarChartView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = tablePadding + getTableEnd() + getPaddingLeft() + getPaddingRight();
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (MeasureSpec.EXACTLY == heightMode) {
            height = getPaddingTop() + getPaddingBottom() + height;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    private int getTableEnd() {
        return stepEnd + tablePadding;
    }

    private void init() {
        resetParam();

        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mHolder.addCallback(this);

        mBorderLinePaint = generatePaint();
        mBorderLinePaint.setColor(defaultBorderColor);
        mBorderLinePaint.setStrokeWidth(Utils.dip2px(getContext(), 1));

        mTextPaint = generatePaint();
        mTextPaint.setColor(labelTextColor);
        mTextPaint.setTextSize(mLabelTextSize);

        mTitleTextPaint = generatePaint();
        mTitleTextPaint.setColor(titleTextColor);
        mTitleTextPaint.setTextSize(mTitleTextSize);

        mDataLinePaint = new Paint();
        mDataLinePaint.setAntiAlias(true);
        mDataLinePaint.setColor(defaultLineColor);
        mDataLinePaint.setStyle(Paint.Style.FILL);
        mDataLinePaint.setStrokeWidth(perBarW);

        tablePadding = Utils.dip2px(getContext(), 10);
    }

    private Paint generatePaint() {
        Paint m = new Paint();
        m.setAntiAlias(true);
        m.setStyle(Paint.Style.STROKE);
        return m;
    }

    private void updateMaxMinData() {
        maxData = -1d;
        minData = Double.MAX_VALUE;

        for (Data data : mDatas) {
            if (data.value > maxData) {
                maxData = data.value;
            }

            if (data.value < minData) {
                minData = data.value;
            }
        }
    }

    private void drawCanvas(Canvas canvas) {
        double max = Math.ceil(maxData);
        double min = Math.floor(minData);
        double totalDiff = maxData - minData;
        float minHeight = Utils.dip2px(getContext(), 5);
        float totalHeight = mHeight + mBottomTextSpace + mTopTextSpace + minHeight;

        canvas.translate(mLeftTextSpace, mHeight - mBottomTextSpace);

        canvas.drawLine(0, 0, 0, -totalHeight * scale, mBorderLinePaint);

        canvas.drawText(String.format(Locale.CHINA, "%.1f", (max + min) / 2),
                -mLeftTextSpace / 2f - mTextPaint.measureText(String.valueOf(Math.ceil((max + min) / 2))) / 2,
                -totalHeight / 2 * scale,
                mTextPaint);

        canvas.drawText(String.format(Locale.CHINA, "%.1f", Math.ceil(max * 1.05)),
                -mLeftTextSpace / 2f - mTextPaint.measureText(String.valueOf(max)) / 2,
                -totalHeight * scale,
                mTextPaint);

        canvas.drawLine(0, 0, getTableEnd(), 0, mBorderLinePaint);
        for (int i = 0; i < mDatas.size(); i++) {
            Data data = mDatas.get(i);
            String perData = String.format(Locale.CHINA, "%.2f", data.value);

            double currentDiff = data.value - minData;
            float x = 3f * (i + 1) * perBarW;
            float y = data.value - minData > 1e-5 ? (float) (totalHeight * scale / totalDiff * currentDiff) : minHeight;
            canvas.drawLine(x, 0, x, -y, mDataLinePaint);

            mTextPaint.setTextSize(dataTextSize);
            mTextPaint.setColor(dataColor);
            canvas.drawText(perData,
                    x - mTextPaint.measureText(perData) / 2,
                    -y - dataTextSize,
                    mTextPaint);

            mTextPaint.setTextSize(descriptionTextSize);
            mTextPaint.setColor(descriptionColor);
            canvas.drawText(mSimpleDateFormat.format(data.time),
                    x - mTextPaint.measureText(mSimpleDateFormat.format(data.time)) / 2,
                    descriptionTextSize,
                    mTextPaint);
        }
    }

    private void resetParam() {
        if (mTextPaint != null) {
            mTextPaint.reset();
        }
        if (mBorderLinePaint != null) {
            mBorderLinePaint.reset();
        }
        if (mDataLinePaint != null) {
            mDataLinePaint.reset();
        }

        stepStart = tablePadding * 2;
        stepEnd = (int) (stepStart + 3 * perBarW * (mDatas == null || mDatas.isEmpty() ? 0 : mDatas.size()));
    }

    private void refreshLayout() {
        init();
        requestLayout();
        postInvalidate();
    }

    public void setData(List<Data> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        updateMaxMinData();
        refreshLayout();
    }

    public void appendData(Data data) {
        mDatas.add(data);
        updateMaxMinData();
        refreshLayout();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mDrawThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mRunning = false;
        mDrawThread.interrupt();
    }

    public int getDataCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public static class Data {
        double value;
        Date time;

        public Data(double value, Date time) {
            this.value = value;
            this.time = time;
        }
    }
}
