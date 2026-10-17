package com.alpha.battery

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * First app — Battery Temperature (2024).
 * Reads ACTION_BATTERY_CHANGED and shows temperature in °C.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tempTextView: TextView
    private lateinit var refreshButton: Button
    private lateinit var batteryImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tempTextView = findViewById(R.id.tempTextView)
        refreshButton = findViewById(R.id.refreshButton)
        batteryImage = findViewById(R.id.batteryImage)

        showBatteryTemp()
        refreshButton.setOnClickListener { showBatteryTemp() }
    }

    private fun showBatteryTemp() {
        val batteryStatus: Intent? = registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        if (batteryStatus != null) {
            // Android reports temperature in tenths of a degree Celsius
            val tempC = batteryStatus.getIntExtra("temperature", 0) / 10.0
            tempTextView.text = "Battery Temp: $tempC °C"
        }
    }
}
