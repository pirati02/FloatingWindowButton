package com.dev.baqari.floating_window_button;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final int SYSTEM_ALERT_REQUEST_CODE = 252;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startActivityForResult(myIntent, SYSTEM_ALERT_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(MainActivity.this, MusicFloatingService.class);
                        startService(intent);
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, MusicFloatingService.class);
                    startService(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SYSTEM_ALERT_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(MainActivity.this, MusicFloatingService.class);
                startService(intent);
            }
        }
    }
}
