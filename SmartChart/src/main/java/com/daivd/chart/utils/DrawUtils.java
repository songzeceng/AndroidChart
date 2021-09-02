package com.daivd.chart.utils;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.daivd.chart.data.style.FontStyle;


/**
 * Created by huang on 2017/11/1.
 */

public class DrawUtils {

    public static int getTextHeight(FontStyle style, Paint paint){
        style.fillPaint(paint);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) (fontMetrics.descent - fontMetrics.ascent);
    }

    public static int getTextHeight(Paint paint){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) (fontMetrics.descent - fontMetrics.ascent);
    }

    public static float getTextCenterY(int centerY,Paint paint){
       return centerY-((paint.descent() + paint.ascent()) / 2);
    }

    public static boolean isMixRect(Rect rect,int left,int top,int right,int bottom){

        return rect.bottom>= top && rect.right >= left && rect.top <bottom && rect.left< right;
    }

    public static boolean isClick(int left, int top, int right, int bottom, PointF clickPoint){
        return clickPoint.x >= left && clickPoint.x <=right && clickPoint.y>=top && clickPoint.y <=bottom;
    }



    public static boolean isMixHorizontalRect(Rect rect,int left,int right){

        return   rect.right >= left  && rect.left<= right;
    }
    public static boolean isVerticalMixRect(Rect rect,int top,int bottom){

        return rect.bottom>= top  && rect.top <=bottom;
    }
}
