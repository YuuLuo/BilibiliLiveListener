package com.yuuluo.bilibililivelistener

import android.content.Intent
import androidx.preference.PreferenceFragmentCompat
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.preference.SwitchPreferenceCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // 获取 SwitchPreferenceCompat 实例
        val switchForegroundService = findPreference<SwitchPreferenceCompat>("foreground_service")
        switchForegroundService?.setOnPreferenceChangeListener { preference, newValue ->
            val isEnabled = newValue as Boolean
            if (isEnabled) {
                startForegroundService()
            } else {
                stopForegroundService()
            }
            true
        }

    }

    private fun startForegroundService() {
        if (activity != null) {
            val serviceIntent = Intent(activity, ForegroundService::class.java)
            ContextCompat.startForegroundService(requireActivity(), serviceIntent)
        }
    }

    private fun stopForegroundService() {
        if (activity != null) {
            val serviceIntent = Intent(activity, ForegroundService::class.java)
            requireActivity().stopService(serviceIntent)
        }
    }
}

