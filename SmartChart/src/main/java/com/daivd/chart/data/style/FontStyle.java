package com.daivd.chart.data.style;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import com.daivd.chart.utils.DensityUtils;

/**
 * Created by huang on 2017/9/27.
 */

public class FontStyle implements IStyle{

    public static int defaultFontSize = 12;
    public static int defaultFontColor = Color.parseColor("#333333");
    private int textSize;
    private int textColor;
    private int padding = 10;

    public static void setDefaultTextSize(int defaultTextSize){
        defaultFontSize = defaultTextSize;
    }

    public static void setDefaultTextSpSize(Context context,int defaultTextSpSize){
        defaultFontSize = DensityUtils.sp2px(context,defaultTextSpSize);
    }
    public static void setDefaultTextColor(int defaultTextColor){
        defaultFontColor = defaultTextColor;
    }

    public FontStyle() {
    }

    public FontStyle(int textSize, int textColor) {
        this.textSize = textSize;
        this.textColor = textColor;
    }
    
    public FontStyle(Context context,int sp, int textColor) {
        this.textSize = DensityUtils.sp2px(context,sp);
        this.textColor = textColor;
    }
    
    


    public int getTextSize() {
        if(textSize == 0){
            return defaultFontSize;
        }
        return textSize;
    }

    public FontStyle setTextSize(int textSize) {
        this.textSize = textSize;
        return this;
    }

    public FontStyle setTextSpSize(Context context,int sp){
        this.setTextSize(DensityUtils.sp2px(context,sp));
        return this;
    }

    public int getTextColor() {
        if(textColor == 0){
            return defaultFontColor;
        }
        return textColor;
    }

    public FontStyle setTextColor(int textColor) {
        
        this.textColor = textColor;
        return this;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }
    @Override
    public void fillPaint(Paint paint){
        paint.setColor(getTextColor());
        paint.setTextSize(getTextSize());
        paint.setStyle(Paint.Style.FILL);
    }
}
