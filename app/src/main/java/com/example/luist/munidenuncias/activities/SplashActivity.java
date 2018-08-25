package com.example.luist.munidenuncias.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.luist.munidenuncias.R;

public class SplashActivity extends AppCompatActivity {

    private final static int DURATION = 3000;

    private TextView v_text_name_empresa;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        v_text_name_empresa = (TextView) findViewById(R.id.text_name_empresa);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        v_text_name_empresa.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                finish();
            }
        },DURATION);

    }
}
