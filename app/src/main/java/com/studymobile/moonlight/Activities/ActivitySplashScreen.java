package com.studymobile.moonlight.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.studymobile.moonlight.Analytics.AnalyticsManager;
import com.studymobile.moonlight.R;

public class ActivitySplashScreen extends AppCompatActivity
{
    private TextView m_TxtViewSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        m_TxtViewSlogan = findViewById(R.id.txtView_slogan);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/PlomPraeng.ttf");
        m_TxtViewSlogan.setTypeface(typeface);
        AnalyticsManager.GetInstance().Init(getApplicationContext());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(ActivitySplashScreen.this, ActivityLogin.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
