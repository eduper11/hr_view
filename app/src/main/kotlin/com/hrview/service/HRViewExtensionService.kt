package com.hrview.service

import android.util.Log
import com.hrview.datatype.HRViewDataType
import com.hrview.engine.HrStore
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.extension.KarooExtension

class HRViewExtensionService : KarooExtension("hrview", "1.0") {

    companion object { private const val TAG = "HRViewExtensionService" }

    private lateinit var karooSystem: KarooSystemService
    private val store = HrStore()

    override val types: List<DataTypeImpl> by lazy {
        listOf(HRViewDataType(applicationContext, store))
    }

    override fun onCreate() {
        super.onCreate()
        karooSystem = KarooSystemService(applicationContext)
        karooSystem.connect { connected ->
            Log.d(TAG, "Karoo system connected=$connected")
            if (connected) store.connect(karooSystem)
        }
    }

    override fun onDestroy() {
        if (::karooSystem.isInitialized) {
            store.disconnect(karooSystem)
            karooSystem.disconnect()
        }
        super.onDestroy()
    }
}
