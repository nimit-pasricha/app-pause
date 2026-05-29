package com.nimitpasricha.pause.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nimitpasricha.pause.R

/**
 * Fraunces — the characterful serif that carries the pause screen. The contrast
 * between elegant type and slightly absurd words is part of the joke.
 *
 * Bundled as the variable font; we pin a high optical size and a soft/wonky
 * setting so the hero line reads warm rather than severe. (Variable-font
 * variation settings require API 26+; our minSdk is 29.)
 */
@OptIn(ExperimentalTextApi::class)
val Fraunces: FontFamily = FontFamily(
    Font(
        R.font.fraunces,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(420),
            FontVariation.opticalSizing(72f.sp),
            FontVariation.Setting("SOFT", 60f),
            FontVariation.Setting("WONK", 1f),
        ),
    ),
    Font(
        R.font.fraunces,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600),
            FontVariation.opticalSizing(72f.sp),
            FontVariation.Setting("SOFT", 60f),
            FontVariation.Setting("WONK", 1f),
        ),
    ),
)
