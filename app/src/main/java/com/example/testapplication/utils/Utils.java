package com.example.testapplication.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.WindowManager;

public class Utils {
    public static final int sINTERVAL_UPDATE_DATA = 1000;
    public static int sINTERVAL_UPDATE_CHART = 16;

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

    public static final int sTYPE_CHART_LINE = 0;
    public static final int sTYPE_CHART_BAR = 1;
    public static final int sTYPE_CHART_PIE = 2;

    public static final String sTYPE_CHART_INTENT = "chart_type";
    public static final String sTYPE_CHART_COMMAND_INTENT = "chart_command";
    public static final String sTYPE_CHART_DATA_INTENT = "chart_data_type";
    public static final String sVALUE_LINE_CHART_RULER_INTENT = "chart_ruler_value";
    public static final String sDATA_CHART_INTENT =  "chart_data";

    public static final int sCOMMAND_INIT_CHART_INTENT = 0;
    public static final int sCOMMAND_UPDATE_CHART_INTENT = 1;

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
    
    public static boolean arrayEmpty(double[] data) {
        return data == null || data.length == 0;
    }

    public static boolean arrayEmpty(int[] data) {
        return data == null || data.length == 0;
    }

    public static int getRefreshInterval(WindowManager windowManager) {
        if (windowManager == null) {
            throw new IllegalArgumentException("WindowManager in getRefreshInterval() should not be null.");
        }

        return (int) (1000 / windowManager.getDefaultDisplay().getRefreshRate());
    }

    public static double getDistance(MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return -1;
        }

        return Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2)
                + Math.pow(event.getY(0) - event.getY(1), 2));
    }

    public static boolean isMainThread() {
        return Thread.currentThread().getName().equals("main");
    }
}
