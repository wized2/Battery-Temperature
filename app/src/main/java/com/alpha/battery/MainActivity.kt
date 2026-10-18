package com.alpha.battery

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Battery Temperature — live battery thermal + power details.
 * Original app (2024); reconstructed and improved as FOSS.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tempTextView: TextView
    private lateinit var tempSecondaryView: TextView
    private lateinit var statusChip: Chip
    private lateinit var detailsTextView: TextView
    private lateinit var refreshButton: MaterialButton
    private lateinit var unitToggle: MaterialButtonToggleGroup
    private lateinit var tempCard: MaterialCardView

    private var useCelsius = true
    private var lastTempC: Double? = null

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
        tempSecondaryView = findViewById(R.id.tempSecondaryView)
        statusChip = findViewById(R.id.statusChip)
        detailsTextView = findViewById(R.id.detailsTextView)
        refreshButton = findViewById(R.id.refreshButton)
        unitToggle = findViewById(R.id.unitToggle)
        tempCard = findViewById(R.id.tempCard)

        unitToggle.check(R.id.unitC)
        unitToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            useCelsius = checkedId == R.id.unitC
            renderTemperature()
        }

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
            lastTempC = null
            tempTextView.text = getString(R.string.temp_unavailable)
            tempSecondaryView.text = getString(R.string.status_unknown)
            statusChip.text = getString(R.string.status_unknown)
            detailsTextView.text = getString(R.string.details_placeholder)
            return
        }

        val tempTenths = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        if (tempTenths == Int.MIN_VALUE) {
            lastTempC = null
            tempTextView.text = getString(R.string.temp_unavailable)
            statusChip.text = getString(R.string.status_unknown)
            return
        }

        lastTempC = tempTenths / 10.0
        renderTemperature()

        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
        val pct = if (level >= 0) (level * 100f / scale).roundToInt() else -1
        val voltageMv = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        val plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)

        val charging = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_USB -> getString(R.string.charge_usb)
            BatteryManager.BATTERY_PLUGGED_AC -> getString(R.string.charge_ac)
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> getString(R.string.charge_wireless)
            else -> getString(R.string.charge_none)
        }
        val healthText = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> getString(R.string.health_good)
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> getString(R.string.health_overheat)
            BatteryManager.BATTERY_HEALTH_DEAD -> getString(R.string.health_dead)
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> getString(R.string.health_over_voltage)
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> getString(R.string.health_failure)
            BatteryManager.BATTERY_HEALTH_COLD -> getString(R.string.health_cold)
            else -> getString(R.string.health_unknown)
        }
        val levelText = if (pct >= 0) "$pct%" else "—"
        val voltText = if (voltageMv > 0) {
            String.format(Locale.US, "%.2f V", voltageMv / 1000.0)
        } else {
            "—"
        }
        detailsTextView.text = getString(R.string.details_format, levelText, voltText, charging, healthText)
    }

    private fun renderTemperature() {
        val tempC = lastTempC
        if (tempC == null) {
            tempTextView.text = getString(R.string.temp_unavailable)
            return
        }
        val tempF = tempC * 9.0 / 5.0 + 32.0
        val primary = if (useCelsius) tempC else tempF
        val secondary = if (useCelsius) tempF else tempC
        val primaryUnit = if (useCelsius) "°C" else "°F"
        val secondaryUnit = if (useCelsius) "°F" else "°C"

        tempTextView.text = String.format(Locale.US, "%.1f%s", primary, primaryUnit)
        tempSecondaryView.text = String.format(Locale.US, "%.1f%s", secondary, secondaryUnit)

        val (statusRes, bgRes, fgRes) = when {
            tempC >= 45.0 -> Triple(R.string.status_hot, R.color.status_hot_bg, R.color.status_hot_fg)
            tempC >= 38.0 -> Triple(R.string.status_warm, R.color.status_warm_bg, R.color.status_warm_fg)
            tempC <= 0.0 -> Triple(R.string.status_cold, R.color.status_cold_bg, R.color.status_cold_fg)
            else -> Triple(R.string.status_normal, R.color.status_normal_bg, R.color.status_normal_fg)
        }
        statusChip.text = getString(statusRes)
        statusChip.chipBackgroundColor =
            android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, bgRes))
        statusChip.setTextColor(ContextCompat.getColor(this, fgRes))
    }

    companion object {
        private const val AUTO_REFRESH_MS = 3_000L
    }
}
