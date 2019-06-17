package com.BH44IT.volunteam.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.BH44IT.volunteam.R;

public class MainActivity extends AppCompatActivity {

    float x1, x2, y1, y2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1 < x2){
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }else if(x1 > x2){
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
            break;
        }
        return false;
    }
}
