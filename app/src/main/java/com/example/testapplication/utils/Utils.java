package com.example.testapplication.utils;

import android.content.Context;

public class Utils {
    public static int dip2px(Context context, float dipValue) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null!");
        }

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null!");
        }

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
