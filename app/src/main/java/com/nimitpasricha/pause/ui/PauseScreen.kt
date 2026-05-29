package com.nimitpasricha.pause.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nimitpasricha.pause.theme.Fraunces
import com.nimitpasricha.pause.theme.Theme

/**
 * The whole product, on one screen: a calm pause that subtly wants you to
 * leave. Type-forward — the hero serif line carries it — with a small,
 * gently-draining ring, a judgment-free context line, and a button hierarchy
 * that nudges toward backing out.
 *
 * @param heroLine the line to display right now (the activity swaps a two-beat
 *   setup for its payoff partway through the timer).
 * @param contextLabel e.g. "Instagram · 7th visit today" — stated plainly.
 * @param progress 1f at the start of the wait, draining to 0f.
 * @param secondsRemaining whole seconds left until "Open anyway" unlocks.
 * @param openUnlocked true once the timer has reached zero.
 */
@Composable
fun PauseScreen(
    theme: Theme,
    heroLine: String,
    contextLabel: String,
    progress: Float,
    secondsRemaining: Int,
    openUnlocked: Boolean,
    onBackOut: () -> Unit,
    onOpenAnyway: () -> Unit,
) {
    val palette = theme.palette
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.surface),
    ) {
        // Soft, edge-framed decoration behind everything.
        theme.background(Modifier.matchParentSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Top spacer keeps the hero block optically centered above the buttons.
            Box(Modifier.size(1.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Crossfade(targetState = heroLine, label = "heroLine") { line ->
                    Text(
                        text = line,
                        fontFamily = Fraunces,
                        fontSize = 32.sp,
                        lineHeight = 42.sp,
                        color = palette.heroText,
                        textAlign = TextAlign.Center,
                    )
                }

                DrainingRing(
                    progress = progress,
                    secondsRemaining = secondsRemaining,
                    ringColor = palette.ring,
                    trackColor = palette.ringTrack,
                    numberColor = palette.contextText,
                    modifier = Modifier.padding(top = 36.dp),
                )

                Text(
                    text = contextLabel,
                    fontSize = 14.sp,
                    color = palette.contextText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 24.dp),
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // The prominent, beautiful one. Always available.
                Button(
                    onClick = onBackOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.primaryButton,
                        contentColor = palette.onPrimaryButton,
                    ),
                ) {
                    Text(
                        text = "Actually, never mind",
                        fontSize = 17.sp,
                        modifier = Modifier.padding(vertical = 6.dp),
                    )
                }

                // Deliberately quiet. Shows the countdown until it unlocks.
                TextButton(
                    onClick = onOpenAnyway,
                    enabled = openUnlocked,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = palette.quietButtonText,
                        disabledContentColor = palette.quietButtonText.copy(alpha = 0.5f),
                    ),
                ) {
                    Text(
                        text = if (openUnlocked) "Open anyway" else "Open anyway in ${secondsRemaining}s",
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

/** A small ring that drains as the wait passes — informs without nagging. */
@Composable
private fun DrainingRing(
    progress: Float,
    secondsRemaining: Int,
    ringColor: Color,
    trackColor: Color,
    numberColor: Color,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 250),
        label = "ringProgress",
    )
    Box(modifier = modifier.size(72.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 6.dp.toPx()
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(inset, inset)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = -360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        Text(
            text = secondsRemaining.toString(),
            fontSize = 18.sp,
            color = numberColor,
            modifier = Modifier.alpha(0.8f),
        )
    }
}
