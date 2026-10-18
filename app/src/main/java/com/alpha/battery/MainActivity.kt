package com.alpha.battery

import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.GradientDrawable
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Battery Temperature — live thermal readout from ACTION_BATTERY_CHANGED.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tempValue: TextView
    private lateinit var secondaryTemp: TextView
    private lateinit var statusChip: TextView
    private lateinit var levelValue: TextView
    private lateinit var voltageValue: TextView
    private lateinit var powerValue: TextView
    private lateinit var healthValue: TextView
    private lateinit var unitToggle: MaterialButtonToggleGroup
    private lateinit var refreshButton: MaterialButton

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

        tempValue = findViewById(R.id.tempValue)
        secondaryTemp = findViewById(R.id.secondaryTemp)
        statusChip = findViewById(R.id.statusChip)
        levelValue = findViewById(R.id.levelValue)
        voltageValue = findViewById(R.id.voltageValue)
        powerValue = findViewById(R.id.powerValue)
        healthValue = findViewById(R.id.healthValue)
        unitToggle = findViewById(R.id.unitToggle)
        refreshButton = findViewById(R.id.refreshButton)

        unitToggle.check(R.id.unitC)
        unitToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            useCelsius = checkedId == R.id.unitC
            renderTemp()
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
            tempValue.text = getString(R.string.temp_placeholder)
            statusChip.text = getString(R.string.status_unknown)
            secondaryTemp.text = ""
            return
        }

        val tempTenths = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        if (tempTenths == Int.MIN_VALUE) {
            tempValue.text = getString(R.string.temp_placeholder)
            statusChip.text = getString(R.string.status_unknown)
            secondaryTemp.text = ""
            lastTempC = null
        } else {
            lastTempC = tempTenths / 10.0
            renderTemp()
        }

        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
        val pct = if (level >= 0) (level * 100f / scale).roundToInt() else -1
        levelValue.text = if (pct >= 0) "$pct%" else "—"

        val voltageMv = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        voltageValue.text =
            if (voltageMv > 0) String.format(Locale.US, "%.2f V", voltageMv / 1000.0) else "—"

        val plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        powerValue.text = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_USB -> getString(R.string.charge_usb)
            BatteryManager.BATTERY_PLUGGED_AC -> getString(R.string.charge_ac)
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> getString(R.string.charge_wireless)
            else -> getString(R.string.charge_none)
        }

        val health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        healthValue.text = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> getString(R.string.health_good)
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> getString(R.string.health_overheat)
            BatteryManager.BATTERY_HEALTH_DEAD -> getString(R.string.health_dead)
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> getString(R.string.health_over_voltage)
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> getString(R.string.health_failure)
            BatteryManager.BATTERY_HEALTH_COLD -> getString(R.string.health_cold)
            else -> getString(R.string.health_unknown)
        }
    }

    private fun renderTemp() {
        val tempC = lastTempC ?: return
        val tempF = tempC * 9.0 / 5.0 + 32.0
        if (useCelsius) {
            tempValue.text = String.format(Locale.US, "%.1f°", tempC)
            secondaryTemp.text = String.format(Locale.US, "%.1f °F", tempF)
        } else {
            tempValue.text = String.format(Locale.US, "%.1f°", tempF)
            secondaryTemp.text = String.format(Locale.US, "%.1f °C", tempC)
        }

        val (statusRes, colorRes) = when {
            tempC >= 45.0 -> R.string.status_hot to R.color.status_hot
            tempC >= 38.0 -> R.string.status_warm to R.color.status_warm
            tempC <= 5.0 -> R.string.status_cold to R.color.status_cold
            else -> R.string.status_normal to R.color.status_normal
        }
        statusChip.text = getString(statusRes)
        val color = ContextCompat.getColor(this, colorRes)
        statusChip.setTextColor(ContextCompat.getColor(this, R.color.surface))
        val bg = GradientDrawable().apply {
            cornerRadius = 48f
            setColor(color)
        }
        statusChip.background = bg
    }

    companion object {
        private const val AUTO_REFRESH_MS = 3_000L
    }
}
