package com.yuseob.chaenggimdol.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorContrastTest {
    @Test
    fun errorColorMeetsNormalTextContrastOnSoftSage() {
        assertTrue(
            "Error text must meet 4.5:1 contrast on Soft Sage",
            contrastRatio(ErrorRed, SoftSage) >= 4.5,
        )
    }

    @Test
    fun pineTealMeetsNormalTextContrastOnSoftSage() {
        assertTrue(
            "Pine Teal must meet 4.5:1 contrast on Soft Sage",
            contrastRatio(PineTeal, SoftSage) >= 4.5,
        )
    }

    private fun contrastRatio(
        first: Color,
        second: Color,
    ): Double {
        val lighter = maxOf(first.relativeLuminance(), second.relativeLuminance())
        val darker = minOf(first.relativeLuminance(), second.relativeLuminance())
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun Color.relativeLuminance(): Double {
        fun channel(value: Float): Double {
            val normalized = value.toDouble()
            return if (normalized <= 0.03928) {
                normalized / 12.92
            } else {
                Math.pow((normalized + 0.055) / 1.055, 2.4)
            }
        }
        return 0.2126 * channel(red) +
            0.7152 * channel(green) +
            0.0722 * channel(blue)
    }
}
