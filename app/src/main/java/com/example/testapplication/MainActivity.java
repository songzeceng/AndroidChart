package com.example.testapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.testapplication.BarChart.BarActivity;
import com.example.testapplication.LineChart.LineChartActivity;
import com.example.testapplication.PieChart.PieChartActivity;

public class MainActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        findViewById(R.id.btn_line).setOnClickListener(this);
        findViewById(R.id.btn_bar).setOnClickListener(this);
        findViewById(R.id.btn_pie).setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Class dest = null;
        switch (v.getId()) {
            case R.id.btn_line:
                dest = LineChartActivity.class;
                break;
            case R.id.btn_bar:
                dest = BarActivity.class;
                break;
            case R.id.btn_pie:
                dest = PieChartActivity.class;
                break;
        }

        if (dest != null) {
            startActivity(new Intent(this, dest));
        }
    }
}
