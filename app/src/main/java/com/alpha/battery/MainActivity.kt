package com.alpha.battery

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Battery Temperature — shows live battery temperature and status.
 * Original app (2024); reconstructed and improved as FOSS.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tempTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var detailsTextView: TextView
    private lateinit var refreshButton: MaterialButton
    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            showBatteryTemp()
            handler.postDelayed(this, AUTO_REFRESH_MS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tempTextView = findViewById(R.id.tempTextView)
        statusTextView = findViewById(R.id.statusTextView)
        detailsTextView = findViewById(R.id.detailsTextView)
        refreshButton = findViewById(R.id.refreshButton)

        refreshButton.setOnClickListener { showBatteryTemp() }
        showBatteryTemp()
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        handler.removeCallbacks(refreshRunnable)
        super.onPause()
    }

    private fun showBatteryTemp() {
        val batteryStatus = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        if (batteryStatus == null) {
            tempTextView.text = getString(R.string.temp_unavailable)
            statusTextView.text = getString(R.string.status_unknown)
            detailsTextView.text = ""
            return
        }

        // Android reports temperature in tenths of a degree Celsius
        val tempTenths = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        if (tempTenths == Int.MIN_VALUE) {
            tempTextView.text = getString(R.string.temp_unavailable)
            statusTextView.text = getString(R.string.status_unknown)
            detailsTextView.text = ""
            return
        }

        val tempC = tempTenths / 10.0
        val tempF = tempC * 9.0 / 5.0 + 32.0
        tempTextView.text = getString(
            R.string.temp_format,
            String.format(Locale.US, "%.1f", tempC),
            String.format(Locale.US, "%.1f", tempF)
        )

        val statusRes = when {
            tempC >= 45.0 -> R.string.status_hot
            tempC >= 38.0 -> R.string.status_warm
            tempC <= 0.0 -> R.string.status_cold
            else -> R.string.status_normal
        }
        statusTextView.text = getString(statusRes)
        statusTextView.setTextColor(
            when (statusRes) {
                R.string.status_hot -> 0xFFFF8A80.toInt()
                R.string.status_warm -> 0xFFFFD54F.toInt()
                R.string.status_cold -> 0xFF81D4FA.toInt()
                else -> 0xFFA5D6A7.toInt()
            }
        )

        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
        val pct = if (level >= 0) (level * 100f / scale).roundToInt() else -1
        val voltageMv = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        val plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val charging = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_USB -> getString(R.string.charge_usb)
            BatteryManager.BATTERY_PLUGGED_AC -> getString(R.string.charge_ac)
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> getString(R.string.charge_wireless)
            else -> getString(R.string.charge_none)
        }
        val levelText = if (pct >= 0) "$pct%" else "—"
        val voltText = if (voltageMv > 0) String.format(Locale.US, "%.2f V", voltageMv / 1000.0) else "—"
        detailsTextView.text = getString(R.string.details_format, levelText, voltText, charging)
    }

    companion object {
        private const val AUTO_REFRESH_MS = 5_000L
    }
}
