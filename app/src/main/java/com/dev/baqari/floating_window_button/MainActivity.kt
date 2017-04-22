package com.dev.baqari.floating_window_button

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById(R.id.button).setOnClickListener {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivityForResult(myIntent, SYSTEM_ALERT_REQUEST_CODE)
                } else {
                    val intent = Intent(this@MainActivity, MusicFloatingService::class.java)
                    startService(intent)
                }
            } else {
                val intent = Intent(this@MainActivity, MusicFloatingService::class.java)
                startService(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SYSTEM_ALERT_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                val intent = Intent(this@MainActivity, MusicFloatingService::class.java)
                startService(intent)
            }
        }
    }

    companion object {
        private val SYSTEM_ALERT_REQUEST_CODE = 252
        private val isAnimating = false
    }
}
