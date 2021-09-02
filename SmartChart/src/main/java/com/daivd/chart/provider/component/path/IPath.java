package com.daivd.chart.provider.component.path;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

/**
 * Created by huang on 2017/11/30.
 */

public interface IPath {

     void drawPath(Canvas canvas, Rect rect, Path path, int perWidth, Paint paint, float progress);
}
