package com.example.util

import kotlin.math.roundToInt

object PaperCalculator {

    /**
     * Estimates sheet caliper (thickness of 1 sheet) in microns (мкм).
     */
    fun estimateCaliperMicrons(densityGsm: Int, paperType: String): Double {
        val typeLower = paperType.lowercase()
        val factor = when {
            typeLower.contains("глян") || typeLower.contains("gloss") -> 0.82
            typeLower.contains("мат") || typeLower.contains("matte") -> 0.92
            typeLower.contains("офсет") || typeLower.contains("offset") -> 1.25
            typeLower.contains("картон") || typeLower.contains("cardboard") -> 1.40
            typeLower.contains("самоклей") || typeLower.contains("adhesive") -> 1.10
            else -> 1.00
        }
        return (densityGsm * factor).coerceAtLeast(40.0)
    }

    /**
     * Calculates estimated sheets count from stack height in cm.
     */
    fun calculateSheetsFromCm(thicknessCm: Double, densityGsm: Int, paperType: String, customCaliperMicrons: Double? = null): Int {
        if (thicknessCm <= 0) return 0
        val caliper = customCaliperMicrons?.takeIf { it > 0 } ?: estimateCaliperMicrons(densityGsm, paperType)
        val heightMicrons = thicknessCm * 10000.0 // 1 cm = 10,000 microns
        return (heightMicrons / caliper).roundToInt().coerceAtLeast(0)
    }

    /**
     * Calculates stack height in cm from sheets count.
     */
    fun calculateCmFromSheets(sheetsCount: Int, densityGsm: Int, paperType: String, customCaliperMicrons: Double? = null): Double {
        if (sheetsCount <= 0) return 0.0
        val caliper = customCaliperMicrons?.takeIf { it > 0 } ?: estimateCaliperMicrons(densityGsm, paperType)
        val heightMicrons = sheetsCount * caliper
        val cm = heightMicrons / 10000.0
        return (kotlin.math.round(cm * 10.0) / 10.0) // round to 1 decimal place
    }
}
