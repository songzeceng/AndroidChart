package com.example.testapplication.LineChart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.testapplication.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LineChartView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private Paint mLinePaint; // 曲线画笔
    private Paint mPointPaint; // 曲线上锚点画笔
    private Paint mTablePaint; // 表格画笔
    private Paint mTextRulerPaint; // 标尺文本画笔
    private Paint mTextPointPaint; // 曲线上锚点文本画笔
    private Canvas mCanvas;

    private Path mLinePath; // 曲线路径
    private Path mTablePath; // 表格路径

    private int mWidth, mHeight;

    private List<Data> mDataList = new ArrayList<>();

    private Point[] mLinePoints;
    private int mStepStart;
    private int mStepEnd;
    private int mStepSpace;
    private int mStepSpaceDefault = 40;
    private int mStepSpaceDP = mStepSpaceDefault; // item宽度默认dp
    private int mTopSpace, mBottomSpace;
    private int mTablePadding;
    private int mTablePaddingDP = 30; // view四周padding默认dp

    private double mMaxValue, mMinValue;
    private double mRulerValueDefault = 10;
    private double mRulerValue = mRulerValueDefault; // 刻度单位跨度
    private int mRulerValuePadding; // 刻度单位与轴的间距
    private int mRulerValuePaddingDP = 8; // 刻度单位与轴的间距默认dp
    private float mHeightPercent = 0.618f;

    private int mLineColor = Color.parseColor("#286DD4"); // 曲线颜色
    private float mLineWidthDP = 2f; // 曲线宽度dp

    private int mPointColor = Color.parseColor("#FF4081"); // 锚点颜色
    private float mPointWidthDefault = 8f;
    private float mPointWidthDP = mPointWidthDefault; // 锚点宽度dp

    private int mTableColor = Color.parseColor("#BBBBBB"); // 表格线颜色
    private float mTableWidthDP = 0.5f; // 表格线宽度dp

    private int mRulerTextColor = mTableColor; // 表格标尺文本颜色
    private float mRulerTextSizeSP = 10f; // 表格标尺文本大小

    private int mPointTextColor = Color.parseColor("#009688"); // 锚点文本颜色
    private float mPointTextSizeSP = 10f; // 锚点文本大小

    private boolean mIsInitialized = false;

    private boolean mRunning = true;
    private SurfaceHolder mHolder;
    private Bitmap bitmapCache;
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mHolder.getSurface().unlockCanvasAndPost(canvas);
        } else {
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    private Canvas getCanvas() {
        Canvas canvas;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            canvas = mHolder.getSurface().lockHardwareCanvas();
        } else {
            canvas = mHolder.lockCanvas();
        }
        return canvas;
    }

    private double mScale = 1f;
    private double mScaleNew = 1f;
    private double mStartDistance;

    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setupView();
    }

    private void setupView() {
        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mHolder.addCallback(this);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(Utils.dip2px(getContext(), mLineWidthDP));

        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(mPointColor);
        mPointPaint.setStrokeWidth(Utils.dip2px(getContext(), mPointWidthDP));

        mTablePaint = new Paint();
        mTablePaint.setAntiAlias(true);
        mTablePaint.setStyle(Paint.Style.STROKE);
        mTablePaint.setColor(mTableColor);
        mTablePaint.setStrokeWidth(Utils.dip2px(getContext(), mTableWidthDP));

        mTextRulerPaint = new Paint();
        mTextRulerPaint.setAntiAlias(true);
        mTextRulerPaint.setStyle(Paint.Style.FILL);
        mTextRulerPaint.setTextAlign(Paint.Align.CENTER);
        mTextRulerPaint.setColor(mRulerTextColor);
        mTextRulerPaint.setTextSize(Utils.sp2px(getContext(), mRulerTextSizeSP));

        mTextPointPaint = new Paint();
        mTextPointPaint.setAntiAlias(true);
        mTextPointPaint.setStyle(Paint.Style.FILL);
        mTextPointPaint.setTextAlign(Paint.Align.CENTER);
        mTextPointPaint.setColor(mPointTextColor);
        mTextPointPaint.setTextSize(Utils.sp2px(getContext(), mPointTextSizeSP));

        mLinePath = new Path();
        mTablePath = new Path();

        setOnTouchListener(this);

        resetParam();
    }

    private void resetParam() {
        mLinePath.reset();
        mTablePath.reset();
        mStepSpace = Utils.dip2px(getContext(), mStepSpaceDP);
        mTablePadding = Utils.dip2px(getContext(), mTablePaddingDP);
        mRulerValuePadding = Utils.dip2px(getContext(), mRulerValuePaddingDP);
        mStepStart = mTablePadding * 2;
        mStepEnd = mStepStart + mStepSpace * (mDataList.size() - 1);
        mTopSpace = mBottomSpace = mTablePadding;
        mLinePoints = new Point[mDataList.size()];

        mIsInitialized = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mTablePadding + getTableEnd() + getPaddingLeft() + getPaddingRight(); // 计算自己的宽度
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec); // 父类期望的高度
        if (MeasureSpec.EXACTLY == heightMode) {
            height = getPaddingTop() + getPaddingBottom() + height;
        }
        setMeasuredDimension(width, height); // 设置自己的宽度和高度
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void drawCanvas(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT); // 绘制背景颜色
        canvas.translate(0f, mHeight / 2f + (getViewDrawHeight() + mTopSpace + mBottomSpace) / 2f); // 设置画布中心点垂直居中

        if (mScaleNew != mScale) {
            clearCanvas();
            mScale = mScaleNew;
//            mRulerValue = mRulerValue * mScale;
            mStepStart = (int) (mStepStart * mScale);
        }

        if (!mIsInitialized) {
            setupLine();
        }

        drawTable(canvas); // 绘制表格
        drawLine(canvas); // 绘制曲线
        drawLinePoints(canvas); // 绘制曲线上的点
    }

    private void drawText(Canvas canvas, Paint textPaint, String text, float x, float y) {
        canvas.drawText(text, x, y, textPaint);
    }

    private void drawRulerYText(Canvas canvas, String text, float x, float y) {
        mTextRulerPaint.setTextAlign(Paint.Align.RIGHT);
        Paint.FontMetrics fontMetrics = mTextRulerPaint.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        float offsetY = fontTotalHeight / 2 - fontMetrics.bottom;
        float newY = (float) (y + offsetY * mScale);
        float newX = (float) (x - mRulerValuePadding * mScale);
        drawText(canvas, mTextRulerPaint, text, newX, newY);
    }

    private void drawRulerXText(Canvas canvas, String text, float x, float y) {
        mTextRulerPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = mTextRulerPaint.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        float offsetY = fontTotalHeight / 2 - fontMetrics.bottom;
        float newY = (float) (y + offsetY * mScale + mRulerValuePadding * mScale);
        drawText(canvas, mTextRulerPaint, text, x, newY);
    }

    private void drawLinePointText(Canvas canvas, String text, float x, float y) {
        mTextPointPaint.setTextAlign(Paint.Align.CENTER);
        float newY = y - mRulerValuePadding;
        drawText(canvas, mTextPointPaint, text, x, newY);
    }

    private int getTableStart() {
        return (int) ((mStepStart + mTablePadding) * mScale);
    }

    private int getTableEnd() {
        return (int) ((mStepEnd + mTablePadding) * mScale);
    }

    private int mPaddingBottom = 150;

    private void drawTable(Canvas canvas) {
        int tableEnd = getTableEnd();

        double startValue = mMinValue - (mMinValue > 0 ? 0 : mMinValue % mRulerValue);
        double endValue = mMaxValue + mRulerValue;

        int startHeight;
        // 标尺y轴连接线
        do {
            startHeight = -getValueHeight(startValue);
            mTablePath.moveTo(mStepStart, startHeight);
            mTablePath.lineTo(tableEnd, startHeight);
            // 绘制y轴刻度单位
            drawRulerYText(canvas, String.format(Locale.CHINA, "%.2f", startValue), mStepStart, startHeight);
            startValue += mRulerValue * mScale;
        } while (startValue < endValue);

        mTablePath.moveTo(mStepStart, startHeight); // 加上顶部的间隔
        mTablePath.lineTo(mStepStart, (float) (mPaddingBottom * mScale)); // 标尺y轴
        mTablePath.lineTo(tableEnd, (float) (mPaddingBottom * mScale)); // 标尺x轴

        canvas.drawPath(mTablePath, mTablePaint);
        // 绘制x轴刻度单位
        drawRulerXValue(canvas);
    }

    private void drawRulerXValue(Canvas canvas) {
        if (mLinePoints == null) return;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Date lastAnchorDate = null;
        for (int i = 0; i < mLinePoints.length; i++) {
            try {
                Date time = mDataList.get(i).time;
                if (i == 0) {
                    lastAnchorDate = time;
                }
                Point point = mLinePoints[i];
                if (point == null) break;

                if (i == 0 ) {
                    drawRulerXText(canvas, dateFormat.format(time), point.x, (float) (mPaddingBottom * mScale));
                } else {
                    if (time != null && lastAnchorDate != null) {
                        if (time.getTime() - lastAnchorDate.getTime() > 1000 * 5) {
                            lastAnchorDate = time;
                            drawRulerXText(canvas, dateFormat.format(time), point.x, (float) (mPaddingBottom * mScale));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void drawLine(Canvas canvas) {
        canvas.drawPath(mLinePath, mLinePaint);
    }

    private void drawLinePoints(Canvas canvas) {
        if (mLinePoints == null) return;

        float pointWidth = Utils.dip2px(getContext(), mPointWidthDP) / 2f;
        int pointCount = mLinePoints.length;

        for (int i = 0; i < pointCount; i++) {
            Point point = mLinePoints[i];
            if (point == null) break;

            canvas.drawCircle(point.x, point.y, pointWidth, mPointPaint);
            // 绘制点的文本
            String text = String.format(Locale.CHINA, "%.2f", mDataList.get(i).getValue());
            drawLinePointText(canvas, text, point.x, point.y);
        }
    }

    private int getValueHeight(double value) {
        value = round(value, 2);
        double valuePercent = Math.abs(value - mMinValue) * 100f / (Math.abs(mMaxValue - mMinValue) * 100f); // 计算value所占百分比
        return (int) ((getViewDrawHeight() * valuePercent + mBottomSpace - mPaddingBottom) * mScale); // 底部加上间隔
    }

    private float getViewDrawHeight() {
        return getMeasuredHeight() * mHeightPercent;
    }

    private void setupLine() {
        if (mDataList.isEmpty()) return;

        int stepTemp = getTableStart();
        Point point = new Point();
        Data data = mDataList.get(0);

        double value = (Double) data.getValue();

        point.set(stepTemp, -getValueHeight(value)); // 坐标系从0,0默认在第四象限绘制
        mLinePoints[0] = point;
        mLinePath.moveTo(point.x, point.y);

        if (mDataList.size() == 1) {
            mIsInitialized = true;
            return;
        }

        for (int i = 1; i < mDataList.size(); i++) {
            data = mDataList.get(i);
            value = (Double) data.getValue();

            point = new Point();
            point.set(stepTemp += mStepSpace * mScale, -getValueHeight(value));
            mLinePath.lineTo(point.x, point.y);

            mLinePoints[i] = point;
        }

        mIsInitialized = true;
    }

    private void refreshLayout() {
        resetParam();
        requestLayout();
        postInvalidate();
    }

    private double round(double num, int scale) {
        double dig = Math.pow(10, scale);
        return Math.ceil(num * dig) / dig;
    }

    private void updateMaxMin() {
        mMaxValue = Collections.max(this.mDataList, (o1, o2) -> {
            double compare = o1.getValue() - o2.getValue();
            return compare == 0 ? 0 : (compare > 0 ? 1 : -1);
        }).getValue();


        mMinValue = Collections.min(this.mDataList, (o1, o2) -> {
            double compare = o1.getValue() - o2.getValue();
            return compare == 0 ? 0 : (compare > 0 ? 1 : -1);
        }).getValue();
    }

    /*-------------可操作方法---------------*/

    public void setData(List<Data> dataList) {
        if (dataList == null || dataList.isEmpty()) return;

        mDataList.clear();
        mDataList.addAll(dataList);
        updateMaxMin();
        refreshLayout();
    }

    public void appendData(Data data) {
        if (data == null) {
            return;
        }

        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }

        mDataList.add(data);
        updateMaxMin();
        refreshLayout();
    }

    public void setRulerYSpace(double space) {
        if (space <= 0) {
            space = mRulerValueDefault;
        }
        mRulerValue = space;
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
                if (mStartDistance > 0 && newDistance > 10) {
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
        Double value;
        Date time;

        public Data(Double value, Date time) {
            this.value = value;
            this.time = time;
        }

        public Double getValue() {
            return value;
        }
    }
}
