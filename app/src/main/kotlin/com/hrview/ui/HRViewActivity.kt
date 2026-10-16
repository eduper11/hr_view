package com.hrview.ui

import android.app.Activity
import android.os.Bundle
import com.hrview.R

/**
 * Pantalla de entrada minima. La Companion app de Hammerhead necesita una
 * LAUNCHER activity para completar la instalacion via sideload.
 */
class HRViewActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hrview)
    }
}
