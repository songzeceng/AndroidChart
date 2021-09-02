package com.example.testapplication.LineChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.testapplication.interfaces.IChart;
import com.example.testapplication.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jeanboy on 2017/6/12.
 */

public class LineChartView<T> extends SurfaceView implements SurfaceHolder.Callback, IChart {

    private Paint linePaint;//曲线画笔
    private Paint pointPaint;//曲线上锚点画笔
    private Paint tablePaint;//表格画笔
    private Paint textRulerPaint;//标尺文本画笔
    private Paint textPointPaint;//曲线上锚点文本画笔
    private Canvas mCanvas;

    private Path linePath;//曲线路径
    private Path tablePath;//表格路径

    private int mWidth, mHeight;

    private List<Data> dataList = new ArrayList<>();

    private Point[] linePoints;
    private int stepStart;
    private int stepEnd;
    private int stepSpace;
    private int stepSpaceDefault = 40;
    private int stepSpaceDP = stepSpaceDefault;//item宽度默认dp
    private int topSpace, bottomSpace;
    private int tablePadding;
    private int tablePaddingDP = 30;//view四周padding默认dp

    private double maxValue, minValue;
    private double rulerValueDefault = 10;
    private double rulerValue = rulerValueDefault;//刻度单位跨度
    private int rulerValuePadding;//刻度单位与轴的间距
    private int rulerValuePaddingDP = 8;//刻度单位与轴的间距默认dp
    private float heightPercent = 0.618f;

    private int lineColor = Color.parseColor("#286DD4");//曲线颜色
    private float lineWidthDP = 2f;//曲线宽度dp

    private int pointColor = Color.parseColor("#FF4081");//锚点颜色
    private float pointWidthDefault = 8f;
    private float pointWidthDP = pointWidthDefault;//锚点宽度dp

    private int tableColor = Color.parseColor("#BBBBBB");//表格线颜色
    private float tableWidthDP = 0.5f;//表格线宽度dp

    private int rulerTextColor = tableColor;//表格标尺文本颜色
    private float rulerTextSizeSP = 10f;//表格标尺文本大小

    private int pointTextColor = Color.parseColor("#009688");//锚点文本颜色
    private float pointTextSizeSP = 10f;//锚点文本大小

    private boolean isBezierLine = false;
    private boolean isCubePoint = false;
    private boolean isInitialized = false;

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
        setZOrderOnTop(true);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);//抗锯齿
        linePaint.setStyle(Paint.Style.STROKE);//STROKE描边FILL填充
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(Utils.dip2px(getContext(), lineWidthDP));//边框宽度

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(pointColor);
        pointPaint.setStrokeWidth(Utils.dip2px(getContext(), pointWidthDP));

        tablePaint = new Paint();
        tablePaint.setAntiAlias(true);
        tablePaint.setStyle(Paint.Style.STROKE);
        tablePaint.setColor(tableColor);
        tablePaint.setStrokeWidth(Utils.dip2px(getContext(), tableWidthDP));

        textRulerPaint = new Paint();
        textRulerPaint.setAntiAlias(true);
        textRulerPaint.setStyle(Paint.Style.FILL);
        textRulerPaint.setTextAlign(Paint.Align.CENTER);
        textRulerPaint.setColor(rulerTextColor);//文本颜色
        textRulerPaint.setTextSize(Utils.sp2px(getContext(), rulerTextSizeSP));//字体大小

        textPointPaint = new Paint();
        textPointPaint.setAntiAlias(true);
        textPointPaint.setStyle(Paint.Style.FILL);
        textPointPaint.setTextAlign(Paint.Align.CENTER);
        textPointPaint.setColor(pointTextColor);//文本颜色
        textPointPaint.setTextSize(Utils.sp2px(getContext(), pointTextSizeSP));//字体大小

        linePath = new Path();
        tablePath = new Path();

        resetParam();
    }

    private void resetParam() {
        linePath.reset();
        tablePath.reset();
        stepSpace = Utils.dip2px(getContext(), stepSpaceDP);
        tablePadding = Utils.dip2px(getContext(), tablePaddingDP);
        rulerValuePadding = Utils.dip2px(getContext(), rulerValuePaddingDP);
        stepStart = tablePadding * 2;
        stepEnd = stepStart + stepSpace * (dataList.size() - 1);
        topSpace = bottomSpace = tablePadding;
        linePoints = new Point[dataList.size()];

        isInitialized = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = tablePadding + getTableEnd() + getPaddingLeft() + getPaddingRight();//计算自己的宽度
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);//父类期望的高度
        if (MeasureSpec.EXACTLY == heightMode) {
            height = getPaddingTop() + getPaddingBottom() + height;
        }
        setMeasuredDimension(width, height);//设置自己的宽度和高度
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        draw();
    }

    private void drawCanvas(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);//绘制背景颜色
        canvas.translate(0f, mHeight / 2f + (getViewDrawHeight() + topSpace + bottomSpace) / 2f);//设置画布中心点垂直居中

        if (!isInitialized) {
            setupLine();
        }

        drawTable(canvas);//绘制表格
        drawLine(canvas);//绘制曲线
        drawLinePoints(canvas);//绘制曲线上的点
    }

    private void drawText(Canvas canvas, Paint textPaint, String text, float x, float y) {
        canvas.drawText(text, x, y, textPaint);
    }

    /**
     * 绘制标尺y轴文本
     *
     * @param canvas
     * @param text
     * @param x
     * @param y
     */
    private void drawRulerYText(Canvas canvas, String text, float x, float y) {
        textRulerPaint.setTextAlign(Paint.Align.RIGHT);
        Paint.FontMetrics fontMetrics = textRulerPaint.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        float offsetY = fontTotalHeight / 2 - fontMetrics.bottom;
        float newY = y + offsetY;
        float newX = x - rulerValuePadding;
        drawText(canvas, textRulerPaint, text, newX, newY);
    }

    /**
     * 绘制标尺x轴文本
     *
     * @param canvas
     * @param text
     * @param x
     * @param y
     */
    private void drawRulerXText(Canvas canvas, String text, float x, float y) {
        textRulerPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = textRulerPaint.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        float offsetY = fontTotalHeight / 2 - fontMetrics.bottom;
        float newY = y + offsetY + rulerValuePadding;
        drawText(canvas, textRulerPaint, text, x, newY);
    }

    /**
     * 绘制曲线上锚点文本
     *
     * @param canvas
     * @param text
     * @param x
     * @param y
     */
    private void drawLinePointText(Canvas canvas, String text, float x, float y) {
        textPointPaint.setTextAlign(Paint.Align.CENTER);
        float newY = y - rulerValuePadding;
        drawText(canvas, textPointPaint, text, x, newY);
    }

    private int getTableStart() {
        return stepStart + tablePadding;
    }

    private int getTableEnd() {
        return stepEnd + tablePadding;
    }

    /**
     * 绘制背景表格
     *
     * @param canvas
     */
    private void drawTable(Canvas canvas) {
        int tableEnd = getTableEnd();

        int rulerCount = (int) (maxValue / rulerValue);
        int rulerMaxCount = maxValue % rulerValue > 0 ? rulerCount + 1 : rulerCount;
        int rulerMax = (int) (rulerValue * rulerMaxCount + rulerValueDefault);

        tablePath.moveTo(stepStart, -getValueHeight(rulerMax));//加上顶部的间隔
        tablePath.lineTo(stepStart, 0);//标尺y轴
        tablePath.lineTo(tableEnd, 0);//标尺x轴

        double startValue = minValue - (minValue > 0 ? 0 : minValue % rulerValue);
        double endValue = maxValue + rulerValue;

        //标尺y轴连接线
        do {
            int startHeight = -getValueHeight(startValue);
            tablePath.moveTo(stepStart, startHeight);
            tablePath.lineTo(tableEnd, startHeight);
            //绘制y轴刻度单位
            drawRulerYText(canvas, String.format(Locale.CHINA, "%.2f", startValue), stepStart, startHeight);
            startValue += rulerValue;
        } while (startValue < endValue);

        canvas.drawPath(tablePath, tablePaint);
        //绘制x轴刻度单位
        drawRulerXValue(canvas);
    }

    /**
     * 绘制标尺x轴上所有文本
     *
     * @param canvas
     */
    private void drawRulerXValue(Canvas canvas) {
        if (linePoints == null) return;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date lastAnchorDate = null;
        for (int i = 0; i < linePoints.length; i++) {
            try {
                Date time = dataList.get(i).time;
                if (i == 0) {
                    lastAnchorDate = time;
                }
                Point point = linePoints[i];
                if (point == null) break;

                if (i == 0 ) {
                    drawRulerXText(canvas, dateFormat.format(time), linePoints[i].x, 0);
                } else {
                    if (time != null && lastAnchorDate != null) {
                        if (time.getTime() - lastAnchorDate.getTime() > 1000 * 5) {
                            lastAnchorDate = time;
                            drawRulerXText(canvas, dateFormat.format(time), linePoints[i].x, 0);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 绘制曲线
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        canvas.drawPath(linePath, linePaint);
    }

    /**
     * 绘制曲线上的锚点
     *
     * @param canvas
     */
    private void drawLinePoints(Canvas canvas) {
        if (linePoints == null) return;

        float pointWidth = Utils.dip2px(getContext(), pointWidthDP) / 2f;
        int pointCount = linePoints.length;

        for (int i = 0; i < pointCount; i++) {
            Point point = linePoints[i];
            if (point == null) break;
            if (isCubePoint) {
                canvas.drawPoint(point.x, point.y, pointPaint);
            } else {
                canvas.drawCircle(point.x, point.y, pointWidth, pointPaint);
            }
            //绘制点的文本
            String text = "";
            T value = (T) dataList.get(i).getValue();
            if (value instanceof Integer) {
                text = String.valueOf(value);
            } else {
                text = String.format(Locale.CHINA, "%.2f", (Double) value);
            }
            drawLinePointText(canvas, text, point.x, point.y);
        }
    }

    /**
     * 获取value值所占的view高度
     *
     * @param value
     * @return
     */
    private int getValueHeight(double value) {
        value = round(value, 2);
        double valuePercent = Math.abs(value - minValue) * 100f / (Math.abs(maxValue - minValue) * 100f);//计算value所占百分比
        return (int) (getViewDrawHeight() * valuePercent + bottomSpace + 0.5f);//底部加上间隔
    }

    /**
     * 获取绘制区域高度
     *
     * @return
     */
    private float getViewDrawHeight() {
        return getMeasuredHeight() * heightPercent;
    }

    /**
     * 初始化曲线数据
     */
    private void setupLine() {
        if (dataList.isEmpty()) return;

        int stepTemp = getTableStart();
        Point pre = new Point();
        double value = 0;
        Data data = dataList.get(0);
        if (data.getValue() instanceof Integer) {
            value = (int) data.getValue();
        } else {
            value = (Double) data.getValue();
        }

        pre.set(stepTemp, -getValueHeight(value));//坐标系从0,0默认在第四象限绘制
        linePoints[0] = pre;
        linePath.moveTo(pre.x, pre.y);

        if (dataList.size() == 1) {
            isInitialized = true;
            return;
        }

        for (int i = 1; i < dataList.size(); i++) {
            data = dataList.get(i);
            Point next = new Point();

            if (data.getValue() instanceof Integer) {
                value = (int) data.getValue();
            } else {
                value = (Double) data.getValue();
            }
            next.set(stepTemp += stepSpace, -getValueHeight(value));

            if (isBezierLine) {
                int cW = pre.x + stepSpace / 2;

                Point p1 = new Point();//控制点1
                p1.set(cW, pre.y);

                Point p2 = new Point();//控制点2
                p2.set(cW, next.y);

                linePath.cubicTo(p1.x, p1.y, p2.x, p2.y, next.x, next.y);//创建三阶贝塞尔曲线
            } else {
                linePath.lineTo(next.x, next.y);
            }

            pre = next;
            linePoints[i] = next;
        }

        isInitialized = true;
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
        T max = (T) Collections.max(this.dataList, (Comparator<Data>) (o1, o2) -> {
            if (o1.value instanceof Integer) {
                int v1 = (Integer) o1.getValue();
                if (o2.value instanceof Integer) {
                    return v1 - (Integer) o2.getValue();
                } else {
                    double compare =  round(v1 - round((Double) o2.getValue(), 2), 2);
                    return compare == 0 ? 0 : (compare > 0 ? 1 : -1);
                }
            } else if (o1.value instanceof Double) {
                double v1 = round((Double) o1.getValue(), 2);
                double compare = 0;
                if (o2.value instanceof Integer) {
                     compare = round((v1 - (Integer) o2.getValue()), 2);
                } else {
                    compare = round(v1 - round((Double) o2.getValue(), 2), 2);
                }
                return compare == 0 ? 0 : (compare > 0 ? 1 : -1);
            }
            return 0;
        }).getValue();

        if (max instanceof Integer) {
            maxValue =  (Integer) max;
        } else {
            maxValue = round((Double) max, 2);
        }

        T min = (T) Collections.min(this.dataList, (Comparator<Data>) (o1, o2) -> {
            if (o1.value instanceof Integer) {
                int v1 = (Integer) o1.getValue();
                if (o2.value instanceof Integer) {
                    return v1 - (Integer) o2.getValue();
                } else {
                    double compare =  round(v1 - round((Double) o2.getValue(), 2), 2);
                    return compare == 0 ? 0 : (compare > 0 ? 1 : -1);
                }
            } else if (o1.value instanceof Double) {
                double v1 = round((Double) o1.getValue(), 2);
                double compare = 0;
                if (o2.value instanceof Integer) {
                    compare = round((v1 - (Integer) o2.getValue()), 2);
                } else {
                    compare = round(v1 - round((Double) o2.getValue(), 2), 2);
                }
                return compare == 0 ? 0 : (compare > 0 ? 1 : -1);
            }
            return 0;
        }).getValue();

        if (min instanceof Integer) {
            minValue = (Integer) min;
        } else {
            minValue = round((Double) min, 2);
        }
    }

    /*-------------可操作方法---------------*/

    /**
     * 设置数据
     *
     * @param dataList
     */
    public void setData(List<Data> dataList) {
        if (dataList == null) {
            throw new RuntimeException("dataList cannot is null!");
        }
        if (dataList.isEmpty()) return;

        this.dataList.clear();
        this.dataList.addAll(dataList);

        updateMaxMin();

        refreshLayout();
    }

    public void appendData(Data data) {
        if (data == null) {
            return;
        }

        if (dataList == null) {
            dataList = new ArrayList<>();
        }

        dataList.add(data);

        updateMaxMin();

        refreshLayout();
    }

    /**
     * 设置是否是贝塞尔曲线
     *
     * @param isBezier
     */
    public void setBezierLine(boolean isBezier) {
        isBezierLine = isBezier;
        refreshLayout();
    }

    /**
     * 设置标尺y轴间距
     *
     * @param space
     */
    public void setRulerYSpace(double space) {
        if (space <= 0) {
            space = rulerValueDefault;
        }
        rulerValue = space;
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

    @Override
    public void draw(Surface surface) {
        if (surface == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        drawCanvas(surface.lockHardwareCanvas());
    }

    public static class Data<T> {
        T value;
        Date time;

        public Data(T value, Date time) {
            this.value = value;
            this.time = time;
        }

        public T getValue() {
            return value;
        }
    }
}
