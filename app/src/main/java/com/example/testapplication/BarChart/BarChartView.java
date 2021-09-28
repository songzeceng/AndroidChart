package com.example.testapplication.BarChart;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testapplication.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BarChartView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private int mDefaultLineColor = Color.parseColor("#FF4081");
    private int mDefaultBorderColor = Color.parseColor("#BBBBBB");
    private int mTitleTextColor = Color.argb(255, 217, 217, 217);
    private int mLabelTextColor = Color.GRAY;
    private int mDataColor = Color.parseColor("#009688");
    private int mDescriptionColor = mDefaultBorderColor;
    private int mTitleTextSize = 42;
    private int mLabelTextSize = 20;
    private int mDescriptionTextSize = 20;
    private int mWidth;
    private int mHeight;
    private float mPerBarW = 30;
    private int mLeftTextSpace = 80;
    private int mBottomTextSpace = 20;
    private int mTopTextSpace = 50;
    private double mScale = 0.5f;
    private double mScaleNew = 0.5f;
    private double mStartDistance;
    protected Paint mBorderLinePaint;
    protected Paint mDataLinePaint;
    private Double mMaxData;
    private Double mMinData;
    private int mTablePadding;

    private List<Data> mDataList = new ArrayList<>();
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);

    private Paint mTextPaint;
    private Paint mTitleTextPaint;

    private float mDataTextDefaultSize = 20;
    private float mDataTextSize = mDataTextDefaultSize;
    private float mDataTextMaxSize = 60;
    private float mDataTextMinSize = 20;
    private int mStepStart;
    private int mStepEnd;
    private boolean mInitialized = false;

    private boolean mRunning = true;
    private SurfaceHolder mHolder;
    private Thread mDrawThread = new Thread() {
        @Override
        public void run() {
            try {
                while (mRunning) {
                    Canvas canvas = getCanvas();
                    drawCanvas(canvas);
                    unlockCanvas(canvas);
                    Thread.sleep(Utils.sINTERVAL_UPDATE_CHART);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void unlockCanvas(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mHolder.getSurface().unlockCanvasAndPost(canvas);
        } else {
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    private Canvas getCanvas() {
        Canvas canvas;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            canvas = mHolder.getSurface().lockHardwareCanvas();
        } else {
            canvas = mHolder.lockCanvas();
        }
        return canvas;
    }

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
        int width = mTablePadding + getTableEnd() + getPaddingLeft() + getPaddingRight();
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
        return (int) ((mStepEnd + mTablePadding) * mScale);
    }

    private void init() {
        resetParam();

        if (!mInitialized) {
            mHolder = getHolder();
            mHolder.setFormat(PixelFormat.TRANSPARENT);
            mHolder.addCallback(this);

            mBorderLinePaint = generatePaint();
            mTextPaint = generatePaint();
            mTitleTextPaint = generatePaint();
            mDataLinePaint = new Paint();

            mTablePadding = Utils.dip2px(getContext(), 10);
            setOnTouchListener(this);
            mInitialized = true;
        }

        mBorderLinePaint.setColor(mDefaultBorderColor);
        mBorderLinePaint.setStrokeWidth(Utils.dip2px(getContext(), 1));

        mTextPaint.setColor(mLabelTextColor);
        mTextPaint.setTextSize(mLabelTextSize);

        mTitleTextPaint.setColor(mTitleTextColor);
        mTitleTextPaint.setTextSize(mTitleTextSize);

        mDataLinePaint.setAntiAlias(true);
        mDataLinePaint.setColor(mDefaultLineColor);
        mDataLinePaint.setStyle(Paint.Style.FILL);
        mDataLinePaint.setStrokeWidth((float) (mPerBarW * mScale));
    }

    private Paint generatePaint() {
        Paint m = new Paint();
        m.setAntiAlias(true);
        m.setStyle(Paint.Style.STROKE);
        return m;
    }

    private void updateMaxMinData() {
        mMaxData = -1d;
        mMinData = Double.MAX_VALUE;

        for (Data data : mDataList) {
            if (data.value > mMaxData) {
                mMaxData = data.value;
            }

            if (data.value < mMinData) {
                mMinData = data.value;
            }
        }
    }

    private void drawCanvas(Canvas canvas) {
        if (mScaleNew != mScale) {
            clearCanvas();
            mScale = mScaleNew;
            mDataTextSize = (float) (mDataTextDefaultSize * mScale);
            mDataTextSize = mDataTextSize > mDataTextMaxSize ? mDataTextMaxSize : Math.max(mDataTextSize, mDataTextMinSize);
        }
        double max = Math.ceil(mMaxData);
        double min = Math.floor(mMinData);
        double totalDiff = mMaxData - mMinData;
        float minHeight = Utils.dip2px(getContext(), 5);
        float totalHeight = mHeight + mBottomTextSpace + mTopTextSpace + minHeight;

        canvas.translate(mLeftTextSpace, mHeight - mBottomTextSpace);

        canvas.drawLine(0, 0, 0, (float) (-totalHeight * mScale), mBorderLinePaint);

        canvas.drawText(String.format(Locale.CHINA, "%.1f", (max + min) / 2),
                (float) (-mLeftTextSpace / 2f - mTextPaint.measureText(String.valueOf(Math.ceil((max + min) / 2))) / 2 * mScale),
                (float) (-totalHeight / 2 * mScale),
                mTextPaint);

        canvas.drawText(String.format(Locale.CHINA, "%.1f", Math.ceil(max * 1.05)),
                (float) (-mLeftTextSpace / 2f - mTextPaint.measureText(String.valueOf(max)) / 2 * mScale),
                (float) (-totalHeight * mScale),
                mTextPaint);

        canvas.drawLine(0, 0, getTableEnd(), 0, mBorderLinePaint);
        for (int i = 0; i < mDataList.size(); i++) {
            Data data = mDataList.get(i);
            String perData = String.format(Locale.CHINA, "%.2f", data.value);

            double currentDiff = data.value - mMinData;
            float x = 3f * (i + 1) * mPerBarW;
            float y = currentDiff > 1e-5 ? (float) (totalHeight / totalDiff * currentDiff) : minHeight;
            canvas.drawLine((float) (x * mScale), 0, (float) (x * mScale), (float) (-y * mScale), mDataLinePaint);

            mTextPaint.setTextSize(mDataTextSize);
            mTextPaint.setColor(mDataColor);
            canvas.drawText(perData,
                    (float) ((x - mTextPaint.measureText(perData) / 2) * mScale),
                    (float) ((-y - mDataTextSize) * mScale),
                    mTextPaint);

            mTextPaint.setTextSize(mDescriptionTextSize);
            mTextPaint.setColor(mDescriptionColor);
            canvas.drawText(mSimpleDateFormat.format(data.time),
                    (float) ((x - mTextPaint.measureText(mSimpleDateFormat.format(data.time)) / 2) * mScale),
                    mDescriptionTextSize,
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

        mStepStart = mTablePadding * 2;
        mStepEnd = (int) (mStepStart + 3 * mPerBarW * (mDataList == null || mDataList.isEmpty() ? 0 : (mDataList.size() + 1)));
    }

    private void refreshLayout() {
        init();
        requestLayout();
        postInvalidate();
    }

    public void setData(List<Data> datas) {
        mDataList.clear();
        mDataList.addAll(datas);
        updateMaxMinData();
        refreshLayout();
    }

    public void appendData(Data data) {
        mDataList.add(data);
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
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                mStartDistance = Utils.getDistance(event);
                break;
            case MotionEvent.ACTION_MOVE:
                double newDistance = Utils.getDistance(event);
                if (mStartDistance > 0 && newDistance > 30) {
                    mScaleNew = newDistance / mStartDistance;
                }
                break;
        }
        return true;
    }

    private void clearCanvas() {
        if (Utils.isMainThread()) {
            refreshLayout();
        } else {
            post(() -> refreshLayout());
        }
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
