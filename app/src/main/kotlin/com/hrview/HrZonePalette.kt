package com.hrview

import android.graphics.Color

data class ZoneColors(val background: Int, val foreground: Int)

/**
 * HrZonePalette — colores de fondo por zona de FC (5 zonas).
 *
 * Paleta calcada de la nativa de Karoo OS — MISMA FUENTE Y MISMOS TONOS que
 * PowerZonePalette (PowerView), para que ambas extensiones usen colores
 * consistentes. Sacada de Barberfish (extension open-source para Karoo que
 * replica el aspecto nativo), cuyo README describe su paleta "Karoo" como
 * la que iguala la pantalla de zonas nativa del dispositivo. Los 5 valores
 * se extrajeron del SVG de su documentacion
 * (docs/img/palette-hr-karoo.svg, fila "Fill mode" — colores de marca sin
 * ajustar por contraste): https://github.com/jpweytjens/Barberfish
 *
 * Nota: Z1-Z4 de FC usan EXACTAMENTE los mismos hex que Z1-Z4 de potencia
 * (Karoo reutiliza la misma familia de color en ambas escalas); Z5 de FC
 * coincide con Z6 de potencia (el rojo), ya que FC solo tiene 5 zonas.
 */
object HrZonePalette {
    private val DARK = Color.parseColor("#111111")
    private val LIGHT = Color.parseColor("#FFFFFF")

    private val Z1 = Color.parseColor("#1A8C3A") // reposo/recuperación — verde oscuro
    private val Z2 = Color.parseColor("#40D078") // base aeróbica — verde claro
    private val Z3 = Color.parseColor("#F0D800") // tempo — amarillo
    private val Z4 = Color.parseColor("#F08868") // umbral — salmón
    private val Z5 = Color.parseColor("#D01020") // VO2max/máximo — rojo

    fun forZone(zone: Int): ZoneColors = when (zone) {
        1    -> ZoneColors(Z1, LIGHT)
        2    -> ZoneColors(Z2, DARK)
        3    -> ZoneColors(Z3, DARK)
        4    -> ZoneColors(Z4, DARK)
        5    -> ZoneColors(Z5, LIGHT)
        else -> ZoneColors(LIGHT, DARK) // 0 = sin dato todavia (no confundir con Z1)
    }
}
