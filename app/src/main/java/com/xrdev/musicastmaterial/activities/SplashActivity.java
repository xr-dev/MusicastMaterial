package com.xrdev.musicastmaterial.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xrdev.musicastmaterial.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button testActivityAButton = (Button) findViewById(R.id.btn_testA);


        testActivityAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityIntent = new Intent(SplashActivity.this, BaseActivity.class);
                startActivity(activityIntent);
            }
        });
    }
}
