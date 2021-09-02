package com.example.testapplication.PieChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.testapplication.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PieChartView extends SurfaceView implements SurfaceHolder.Callback {
    private static final HashMap<Integer, Integer> sALL_COLORS = new HashMap<>();
    private float mRadius;
    private Random mRandom = new Random();
    private int mColorUsed = 0;

    private Paint mArcPaint;

    private int mWidth = 0;
    private int mHeight = 0;

    private float scale = 1f;

    private RectF arcRect; // 画扇形时所需的边界矩形

    private List<PieChartView.Data> mData = new ArrayList<>();

    private int lineColor = Color.parseColor("#FF4081"); // 指示线颜色

    private int defaultBackColor = Color.argb(255, 217, 217, 217);
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm");

    private boolean mRunning = true;
    private SurfaceHolder mHolder;
    private Thread mDrawThread = new Thread() {
        @Override
        public void run() {
            try {
                while (mRunning) {
                    draw();
                    Thread.sleep(30);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    static {
        sALL_COLORS.put(Color.parseColor("#ffff00"), 0);
        sALL_COLORS.put(Color.parseColor("#ff9900"), 0);
        sALL_COLORS.put(Color.parseColor("#ff6600"), 0);
        sALL_COLORS.put(Color.parseColor("#ff3300"), 0);
        sALL_COLORS.put(Color.parseColor("#ff6666"), 0);
        sALL_COLORS.put(Color.parseColor("#ff3366"), 0);
        sALL_COLORS.put(Color.parseColor("#ff3399"), 0);
        sALL_COLORS.put(Color.parseColor("#ff99ff"), 0);
        sALL_COLORS.put(Color.parseColor("#cc9900"), 0);
        sALL_COLORS.put(Color.parseColor("#cc3399"), 0);
        sALL_COLORS.put(Color.parseColor("#cc66ff"), 0);
        sALL_COLORS.put(Color.parseColor("#99ff00"), 0);
        sALL_COLORS.put(Color.parseColor("#99ccff"), 0);
        sALL_COLORS.put(Color.parseColor("#9933ff"), 0);
        sALL_COLORS.put(Color.parseColor("#663300"), 0);
        sALL_COLORS.put(Color.parseColor("#663399"), 0);
        sALL_COLORS.put(Color.parseColor("#0000ee"), 0);
        sALL_COLORS.put(Color.parseColor("#ff3333"), 0);
        sALL_COLORS.put(Color.parseColor("#caff70"), 0);
        sALL_COLORS.put(Color.parseColor("#191970"), 0);
    }

    private void init() {
        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mHolder.addCallback(this);
        setZOrderOnTop(true);

        mArcPaint = new Paint();
        mArcPaint.setStyle(Paint.Style.FILL);
        mArcPaint.setAntiAlias(true);

        arcRect = new RectF();

        mRadius = Utils.dip2px(getContext(), 100);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (MeasureSpec.EXACTLY == widthMode) {
            width = getPaddingLeft() + getPaddingRight() + width;
        }
        mWidth = width;

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (MeasureSpec.EXACTLY == heightMode) {
            height = getPaddingTop() + getPaddingBottom() + height;
        }
        mHeight = height;
        setMeasuredDimension(mWidth, height);

        float left = mWidth / 2f - mRadius;
        float top = mHeight / 2f - mRadius;
        float right = left + 2 * mRadius;
        float bottom = top + 2 * mRadius;

        arcRect.set(left, top, right, bottom); // 饼图矩形位于父视图中间
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        draw();
    }

    private void draw() {
        Canvas canvas = mHolder.lockCanvas();

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); // 饼图和折线图或条形图不同，它每次绘图前必须清空已有图形

        drawDescription(canvas);
        drawArc(canvas);

        mHolder.unlockCanvasAndPost(canvas);
    }

    private void drawArc(Canvas canvas) {
        float drawArc = 360 * scale;

        for (int i = 0; i < mData.size(); i++) {
            // 画出每一部分对应的扇形
            if (mData.get(i).ratio == 0) {
                continue;
            }
            mArcPaint.setColor(mData.get(i).color);
            canvas.drawArc(arcRect, getRatioSum(i) * drawArc,
                    (float) (mData.get(i).ratio * drawArc), true, mArcPaint);
        }
    }

    private void drawDescription(Canvas canvas) {
        int circleCenterX = (int) ((arcRect.left + arcRect.right) / 2);
        int circleCenterY = (int) ((arcRect.top + arcRect.bottom) / 2);

        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).ratio == 0) {
                continue;
            }
            if (lineColor != -1) {
                mArcPaint.setColor(lineColor);
            } else {
                mArcPaint.setColor(mData.get(i).color);
            }

            canvas.save();

            canvas.translate(circleCenterX, circleCenterY);
            canvas.rotate(getRatioHalfSumDegrees(i));
            canvas.drawLine(mRadius + Utils.dip2px(getContext(), 4), 0,
                    mRadius + Utils.dip2px(getContext(), 20), 0, mArcPaint); // 画指示长线
            drawIndicateLines(canvas, i);

            canvas.restore();
        }
    }

    private void drawIndicateLines(Canvas canvas, int i) {
        if (i >= mData.size()) {
            return;
        }

        PieChartView.Data current = mData.get(i);
        canvas.save();

        canvas.translate(mRadius + Utils.dip2px(getContext(), 20), 0);

        float ro = getRatioHalfSumDegrees(i);
        canvas.rotate(-ro);

        mArcPaint.setTextSize(Utils.dip2px(getContext(), 8));
        if ((ro > 270 && ro < 360) || (ro > 0 && ro < 90)) {
            canvas.drawLine(0, 0, Utils.dip2px(getContext(), 6), 0, mArcPaint); // 画指示短线
            mArcPaint.setColor(mData.get(i).color);
            // 画说明信息
            canvas.drawText("name: " + current.info,
                    Utils.dip2px(getContext(), 13), -Utils.dip2px(getContext(), 4), mArcPaint);
            canvas.drawText("count: " + String.format(Locale.CHINA, "%.0f", current.value),
                    Utils.dip2px(getContext(), 13), Utils.dip2px(getContext(), 4), mArcPaint);
            canvas.drawText("rate: " + Math.floor(current.ratio * 100) + "%",
                    Utils.dip2px(getContext(), 13), Utils.dip2px(getContext(), 12), mArcPaint);
        } else {
            canvas.drawLine(0, 0, -Utils.dip2px(getContext(), 6), 0, mArcPaint); // 画指示短线
            mArcPaint.setColor(current.color);
            // 画说明信息
            canvas.drawText("name: " + current.info,
                    -Utils.dip2px(getContext(), 40), -Utils.dip2px(getContext(), 4), mArcPaint);
            canvas.drawText("count: " + String.format(Locale.CHINA, "%.0f", current.value),
                    -Utils.dip2px(getContext(), 40), Utils.dip2px(getContext(), 4), mArcPaint);
            canvas.drawText("rate: " + Math.floor(current.ratio * 100) + "%",
                    -Utils.dip2px(getContext(), 40), Utils.dip2px(getContext(), 12), mArcPaint);
        }
        canvas.restore();
    }

    private float getRatioSum(int j) {
        float sum = 0;
        for (int i = 0; i < j; i++) {
            sum += mData.get(i).ratio;
        }
        return sum;
    }

    private float getRatioHalfSumDegrees(int j) {
        float sum = getRatioSum(j);
        sum += mData.get(j).ratio / 2;
        return sum * 360;
    }

    private void refreshLayout() {
        init();
        requestLayout();
        postInvalidate();
    }

    public void setData(List<PieChartView.Data> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        mData.clear();
        resetColorMap();

        for (Data d : data) {
            if (d.value > 0) {
                mData.add(d);
            }
        }

        computeRatio();
        assignColor(data);
        refreshLayout();
    }

    public void appendData(PieChartView.Data data) {
        if (data == null || data.info == null || mColorUsed >= sALL_COLORS.size()) {
            return;
        }

        PieChartView.Data.addData(mData, data);

        computeRatio();
        assignColor(data);
        refreshLayout();
    }

    private void assignColor(List<PieChartView.Data> dataList) {
        mRandom.setSeed(System.currentTimeMillis());
        for (Data data : dataList) {
            assignColor(data);
        }
    }

    private void assignColor(PieChartView.Data data) {
        while (mColorUsed < sALL_COLORS.size()) {
            int index = mRandom.nextInt(sALL_COLORS.size());
            Integer key = null;
            int temp = 0;
            for (Integer integer : sALL_COLORS.keySet()) {
                if (temp == index) {
                    key = integer;
                    break;
                }
                temp++;
            }

            if (sALL_COLORS.get(key) == 0) {
                data.color = key;
                sALL_COLORS.put(key, 1);
                mColorUsed++;
                break;
            }
        }
    }

    private void resetColorMap() {
        for (Integer integer : sALL_COLORS.keySet()) {
            sALL_COLORS.put(integer, 0);
        }
        mColorUsed = 0;
    }

    private void computeRatio() {
        double sum = 0;
        for (Data data : mData) {
            sum += data.value;
        }

        for (Data data : mData) {
            data.ratio = data.value / sum;
        }
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

    static class Data {
        double value; // frequency value
        Date time;
        int color;
        double ratio;
        String info = "";

        public Data(double value, Date time, String info) {
            this.value = value;
            this.time = time;
            this.info = info;
        }

        public static void addData(List<PieChartView.Data> list, PieChartView.Data data) {
            if (data == null || TextUtils.isEmpty(data.info)) {
                return;
            }

            if (list == null) {
                list = new ArrayList<>();
            }
            if (list.isEmpty()) {
                list.add(data);
                return;
            }

            boolean existed = false;
            for (Data d : list) {
                if (d.info.equals(data.info)) {
                    d.value += 1;
                    existed = true;
                }
            }

            if (!existed) {
                list.add(data);
            }
        }
    }
}
