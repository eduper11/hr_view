package com.hrview.datatype

import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import com.hrview.HrZonePalette
import com.hrview.R
import com.hrview.engine.HrStore
import com.hrview.render.HrFieldRenderer
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.ShowCustomStreamState
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.UpdateGraphicConfig
import io.hammerhead.karooext.models.ViewConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * HRViewDataType — campo grafico de FC renderizado como bitmap.
 *
 * El campo se pinta con HrFieldRenderer (tipografia IBM Plex Sans Condensed Bold,
 * baselines alineados al pixel) sobre un Bitmap del tamano exacto de la celda
 * (config.viewSize) y se envia con setImageViewBitmap — el patron de ki2.
 *
 * Cadencia: 1 s grabando, 3 s en pausa, 10 s sin actividad; y solo se repinta
 * y reenvia cuando algun valor visible ha cambiado.
 */
class HRViewDataType(
    private val applicationContext: Context,
    private val store: HrStore,
) : DataTypeImpl("hrview", "hrview") {

    private val scope = CoroutineScope(Dispatchers.Default)

    private data class Snapshot(val hr: Int?, val avg: Int?, val max: Int?, val zone: Int)

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        emitter.onNext(UpdateGraphicConfig(showHeader = false))
        // Blanquea el contenedor numerico estandar que Karoo pinta DEBAJO del
        // campo grafico; sin esto, con fondo transparente asoma el valor del
        // stream (mismo fix que aplica ki2 en RenderedVisualDataType).
        emitter.onNext(ShowCustomStreamState(message = "", color = null))

        val renderer = HrFieldRenderer(applicationContext)
        val width = config.viewSize.first.takeIf { it > 0 } ?: 220
        val height = config.viewSize.second.takeIf { it > 0 } ?: 110
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        if (config.preview) {
            val job: Job = scope.launch { runPreview(emitter, config, renderer, bitmap) }
            emitter.setCancellable { job.cancel() }
            return
        }

        val job: Job = scope.launch {
            var last: Snapshot? = null
            while (isActive) {
                val snap = Snapshot(store.hr, store.avgHr, store.maxHr, store.zone)
                if (snap != last) {
                    emitter.updateView(buildView(snap, config, renderer, bitmap))
                    last = snap
                }
                delay(when {
                    store.rideActive && !store.paused -> 1_000L
                    store.paused                      -> 3_000L
                    else                              -> 10_000L
                })
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    override fun startStream(emitter: Emitter<StreamState>) {
        val job = scope.launch {
            while (isActive) {
                store.hr?.let { hr ->
                    emitter.onNext(
                        StreamState.Streaming(
                            DataPoint(
                                dataTypeId = dataTypeId,
                                values = mapOf(DataType.Field.SINGLE to hr.toDouble()),
                            )
                        )
                    )
                }
                delay(1000L)
            }
        }
        emitter.setCancellable { job.cancel() }
    }

    // ─── Render ──────────────────────────────────────────────────────────────

    private fun buildView(
        s: Snapshot,
        config: ViewConfig,
        renderer: HrFieldRenderer,
        bitmap: Bitmap,
    ): RemoteViews {
        val pal = HrZonePalette.forZone(s.zone)
        renderer.render(
            bitmap = bitmap,
            hr = s.hr?.toString() ?: "--",
            avg = s.avg?.toString() ?: "--",
            max = s.max?.toString() ?: "--",
            backgroundColor = pal.background,
            foregroundColor = pal.foreground,
            textSize = config.textSize.toFloat(),
        )
        val v = RemoteViews(applicationContext.packageName, R.layout.view_hrview)
        v.setImageViewBitmap(R.id.iv_field, bitmap)
        return v
    }

    private suspend fun runPreview(
        emitter: ViewEmitter,
        config: ViewConfig,
        renderer: HrFieldRenderer,
        bitmap: Bitmap,
    ) {
        val demoByZone = intArrayOf(95, 120, 142, 158, 172)
        var i = 0
        while (true) {
            val zone = (i % 5) + 1
            emitter.updateView(
                buildView(
                    Snapshot(hr = demoByZone[zone - 1], avg = 138, max = 176, zone = zone),
                    config, renderer, bitmap,
                )
            )
            delay(900L)
            i++
        }
    }
}
