package com.hrview.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface

/**
 * HrFieldRenderer — dibuja el campo completo en un Bitmap (patron ki2).
 *
 * Tipografia: IBM Plex Sans Condensed Bold — LA MISMA fuente que usa Karoo OS
 * en sus campos de datos nativos (descubierto en los layouts de ki2, que la
 * referencian como familia de sistema "ibm-plex-sans-condensed"). La
 * empaquetamos en assets para no depender de que la familia exista por nombre.
 *
 * El baseline del numero grande de FC y el del valor de MED comparten la misma
 * coordenada Y (los digitos no tienen descendentes: quedan apoyados en la
 * misma linea, al pixel).
 */
class HrFieldRenderer(context: Context) {

    private val density = context.resources.displayMetrics.density
    private val plexBold: Typeface =
        Typeface.createFromAsset(context.assets, "fonts/ibm_plex_sans_condensed_bold.ttf")
    private val plexRegular: Typeface =
        Typeface.createFromAsset(context.assets, "fonts/ibm_plex_sans_condensed_regular.ttf")

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    // Numero grande en Regular (sin bold); etiquetas y valores pequenos en Bold,
    // que a tamanos reducidos necesitan el peso para leerse bien.
    private val bigPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = plexRegular
        textAlign = Paint.Align.CENTER
    }
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = plexBold
        textAlign = Paint.Align.RIGHT
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = plexBold
        textAlign = Paint.Align.RIGHT
    }

    /** Trazo del icono ECG (viewport 24x24). */
    private val ecgPath = Path().apply {
        moveTo(2f, 12f); lineTo(6f, 12f); lineTo(8f, 6f); lineTo(11f, 18f)
        lineTo(14f, 9f); lineTo(16f, 14f); lineTo(18f, 12f); lineTo(22f, 12f)
    }

    private fun dp(v: Float) = v * density

    fun render(
        bitmap: Bitmap,
        hr: String,
        avg: String,
        max: String,
        backgroundColor: Int,
        foregroundColor: Int,
        textSize: Float,
    ) {
        val canvas = Canvas(bitmap)
        bitmap.eraseColor(Color.TRANSPARENT)

        val w = bitmap.width.toFloat()
        val h = bitmap.height.toFloat()
        // Margen interior generoso, como los campos nativos del Karoo.
        val pad = dp(8f)

        // ── Fondo redondeado (8dp) ──
        bgPaint.color = backgroundColor
        canvas.drawRoundRect(RectF(0f, 0f, w, h), dp(8f), dp(8f), bgPaint)

        // ── Icono ECG arriba-izquierda (14dp) ──
        val iconSize = dp(14f)
        iconPaint.color = foregroundColor
        iconPaint.strokeWidth = 2f
        canvas.save()
        canvas.translate(pad, pad)
        canvas.scale(iconSize / 24f, iconSize / 24f)
        canvas.drawPath(ecgPath, iconPaint)
        canvas.restore()

        // ── Columna derecha: MAX / MED (estilo nativo, mas grande que antes) ──
        valuePaint.textSize = textSize * 0.65f
        labelPaint.textSize = textSize * 0.40f
        valuePaint.color = foregroundColor
        labelPaint.color = foregroundColor

        val xRight = w - pad
        val gap = dp(2f)
        val yAvgVal = h - pad
        val yAvgLbl = yAvgVal - valuePaint.textSize - gap
        val yMaxVal = yAvgLbl - labelPaint.textSize - gap * 2
        val yMaxLbl = yMaxVal - valuePaint.textSize - gap

        canvas.drawText(avg, xRight, yAvgVal, valuePaint)
        canvas.drawText("MED", xRight, yAvgLbl, labelPaint)
        canvas.drawText(max, xRight, yMaxVal, valuePaint)
        canvas.drawText("MAX", xRight, yMaxLbl, labelPaint)

        // ── Numero grande de FC: 1.9x (ajustado a ojo contra los campos
        //    nativos; 2.0x quedaba un pelin grande) y mismo baseline que MED ──
        bigPaint.textSize = textSize * 1.9f
        bigPaint.color = foregroundColor
        val rightColWidth = maxOf(
            valuePaint.measureText(max),
            valuePaint.measureText(avg),
            labelPaint.measureText("MAX"),
            labelPaint.measureText("MED"),
        )
        val leftAreaEnd = w - pad - rightColWidth - dp(6f)
        val xBig = pad + (leftAreaEnd - pad) / 2f
        canvas.drawText(hr, xBig, yAvgVal, bigPaint)
    }
}
