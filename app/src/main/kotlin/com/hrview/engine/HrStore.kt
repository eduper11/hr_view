package com.hrview.engine

import android.util.Log
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.OnStreamState
import io.hammerhead.karooext.models.RideState
import io.hammerhead.karooext.models.StreamState
import kotlin.math.roundToInt

/**
 * HrStore — cache del estado de FC con gestion de bateria.
 *
 * Los streams de FC se suscriben y sueltan segun el estado de la actividad:
 *  - RideState.Recording  -> subscribeSensors()   (activamos los 4 streams)
 *  - RideState.Paused     -> mantenemos streams    (el campo sigue mostrando valores)
 *  - RideState.Idle       -> unsubscribeSensors()  (soltamos todo, cero consumo)
 *
 * Mientras no hay grabacion activa el sistema no necesita procesar ni entregar
 * ningun dato de FC: el campo mostrara "--" y el render loop dormira mas tiempo.
 */
class HrStore {

    companion object { private const val TAG = "HrStore" }

    @Volatile var hr: Int?    = null; private set
    @Volatile var avgHr: Int? = null; private set
    @Volatile var maxHr: Int? = null; private set
    @Volatile var zone: Int   = 0;    private set

    /** True solo mientras RideState.Recording (no paused). */
    @Volatile var rideActive: Boolean = false; private set
    @Volatile var paused: Boolean     = false; private set

    /** ID del consumidor de RideState (siempre activo mientras el servicio vive). */
    private var rideConsumerId: String? = null

    /** IDs de los 4 consumidores de FC (solo existen durante la grabacion). */
    private val sensorConsumerIds = mutableListOf<String>()

    fun connect(ks: KarooSystemService) {
        // RideState: consumidor barato basado en eventos, siempre activo.
        rideConsumerId = ks.addConsumer { state: RideState ->
            when (state) {
                is RideState.Recording -> {
                    paused = false
                    if (!rideActive) {
                        rideActive = true
                        resetStats()
                        subscribeSensors(ks)
                        Log.d(TAG, "ride started — sensors subscribed")
                    }
                }
                is RideState.Paused -> {
                    paused = true
                    // Mantenemos sensorConsumerIds: el campo sigue mostrando el
                    // ultimo valor conocido y la zona. Solo dejamos de animar.
                }
                is RideState.Idle -> {
                    rideActive = false
                    paused = false
                    unsubscribeSensors(ks)
                    resetStats()
                    Log.d(TAG, "ride idle — sensors released")
                }
            }
        }
    }

    fun disconnect(ks: KarooSystemService) {
        unsubscribeSensors(ks)
        rideConsumerId?.let { ks.removeConsumer(it) }
        rideConsumerId = null
    }

    // ─── Sensor subscriptions ────────────────────────────────────────────────

    private fun subscribeSensors(ks: KarooSystemService) {
        if (sensorConsumerIds.isNotEmpty()) return   // ya suscritos

        sensorConsumerIds += ks.addConsumer(
            OnStreamState.StartStreaming(DataType.Type.HEART_RATE)
        ) { e: OnStreamState -> hr = e.intOrNull() }

        sensorConsumerIds += ks.addConsumer(
            OnStreamState.StartStreaming(DataType.Type.AVERAGE_HR)
        ) { e: OnStreamState -> avgHr = e.intOrNull() }

        sensorConsumerIds += ks.addConsumer(
            OnStreamState.StartStreaming(DataType.Type.MAX_HR)
        ) { e: OnStreamState -> maxHr = e.intOrNull() }

        sensorConsumerIds += ks.addConsumer(
            OnStreamState.StartStreaming(DataType.Type.HR_ZONE)
        ) { e: OnStreamState -> zone = (e.intOrNull() ?: 0).coerceIn(0, 5) }
    }

    private fun unsubscribeSensors(ks: KarooSystemService) {
        sensorConsumerIds.forEach { ks.removeConsumer(it) }
        sensorConsumerIds.clear()
    }

    private fun resetStats() {
        hr = null; avgHr = null; maxHr = null; zone = 0
    }

    // ─── Util ────────────────────────────────────────────────────────────────

    private fun OnStreamState.intOrNull(): Int? {
        val s = state
        return if (s is StreamState.Streaming)
            s.dataPoint.singleValue?.takeIf { it.isFinite() }?.roundToInt()
        else null
    }
}
