package com.example.testapplication.utils;

import android.content.Context;

public class Utils {
    public static final int sINTERVAL_UPDATE_DATA = 1000;
    public static final int sINTERVAL_UPDATE_PIE = 2 * sINTERVAL_UPDATE_DATA;
    public static final int sINTERVAL_UPDATE_CHART = 30;

    public static final int sINIT_DATA_COUNT = 10;
    public static final int sTYPE_MEMORY = 0;
    public static final int sTYPE_FPS = 1;
    public static final int sTYPE_CPU = 2;
    public static final int sTYPE_UI = 3;
    public static final int sTYPE_GPU = 4;
    public static final int sTYPE_TEMPERATURE = 5;
    public static final int sTYPE_CURRENT = 6;
    public static final int sTYPE_NETWORK_STATUS = 7;
    public static final int sTYPE_CPU_SPEED = 8;

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
