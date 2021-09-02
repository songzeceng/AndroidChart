package com.example.testapplication.service;

import android.app.Presentation;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.testapplication.IChartDrawAidlInterface;
import com.example.testapplication.LineChart.LineChartView;
import com.example.testapplication.R;
import com.example.testapplication.interfaces.IPresentationCallback;
import com.example.testapplication.utils.DataHelper;
import com.example.testapplication.utils.Utils;

import static com.example.testapplication.utils.Utils.sTYPE_MEMORY;

public class ChartDrawService extends Service {
    private Presentation mPresentation;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int mChartType = 0;
    private IPresentationCallback mPresentationCallback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mChartType = intent.getIntExtra("chart_type", 0);
        return new IChartDrawAidlInterface.Stub() {
            @Override
            public void viewCreate(Surface surface) throws RemoteException {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            return;
                        }

                        DisplayManager manager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
                        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                        VirtualDisplay virtualDisplay = manager.createVirtualDisplay("sub process",
                                displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi,
                                surface, 0);


                        mPresentation = new Presentation(ChartDrawService.this, virtualDisplay.getDisplay()) {
                            @Override
                            public void onDisplayRemoved() {
                                super.onDisplayRemoved();
                                if (mPresentationCallback != null) {
                                    mPresentationCallback.onDisplayRemoved();
                                }
                            }
                        };
                        FrameLayout container = new FrameLayout(ChartDrawService.this);
                        container.setBackgroundColor(Color.parseColor("#ff3399"));

                        switch (mChartType) {
                            case 0:
                                mPresentation.setContentView(R.layout.activity_line);
                                mPresentationCallback = new LineChartController(mPresentation);
                                break;
                            case 1:
                                mPresentation.setContentView(R.layout.activity_bar);
                                mPresentationCallback = new LineChartController(mPresentation);
                                break;
                            case 2:
                                mPresentation.setContentView(R.layout.activity_pie);
                                mPresentationCallback = new LineChartController(mPresentation);
                                break;
                        }
//                        mPresentation.setContentView(container);
                    }
                });
            }
        };
    }
}
