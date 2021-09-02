package com.daivd.chart.data.style;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;

import com.daivd.chart.utils.DensityUtils;

/**
 * Created by huang on 2017/9/27.
 */

public class LineStyle implements IStyle {

    private float width;
    private int color;
    private PathEffect effect = new PathEffect();
    private static  float defaultLineSize = 2f;
    private static  int  defaultLineColor = Color.parseColor("#888888");

    public LineStyle() {
    }

    public LineStyle(float width, int color) {
        this.width = width;
        this.color = color;
    }
    public LineStyle(Context context,float dp, int color) {
        this.width = DensityUtils.dp2px(context,dp);
        this.color = color;
    }
    public static void setDefaultLineSize(float width){
        defaultLineSize = width;
    }

    public static void setDefaultLineSize(Context context,float dp){
        defaultLineSize = DensityUtils.dp2px(context,dp);
    }

    public static void setDefaultLineColor(int color){
        defaultLineColor = color;
    }

    public float getWidth() {
        if(width == 0){
            return defaultLineSize;
        }
        return width;
    }



    public LineStyle setWidth(float width) {
        this.width = width;
        return this;
    }
    public LineStyle setWidth(Context context,int dp) {
        this.width = DensityUtils.dp2px(context,dp);
        return this;
    }

    public int getColor() {
        if(color == 0){
            return defaultLineColor;
        }
        return color;
    }

    public LineStyle setColor(int color) {

        this.color = color;
        return this;
    }

    public LineStyle setEffect(PathEffect effect) {
        this.effect = effect;
        return this;
    }

    @Override
    public void fillPaint(Paint paint){
        paint.setColor(getColor());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(getWidth());
        paint.setPathEffect(effect);

    }
}
