@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.foundation.layout.height
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun AppleStyleSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Deteksi saat disentuh/digeser
    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()
    val haptic = LocalHapticFeedback.current

    // 2. Animasi Spring: Thumb membesar saat digeser, mengecil saat dilepas
    val thumbScale by animateFloatAsState(
        targetValue = if (isDragged) 1.5f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, // Efek memantul khas iOS
            stiffness = Spring.StiffnessLow
        ),
        label = "thumbScale"
    )

    // 3. Animasi Garis: Garis menebal saat digeser
    val trackHeight by animateFloatAsState(
        targetValue = if (isDragged) 8f else 4f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "trackHeight"
    )

    var lastHapticValue by remember { mutableFloatStateOf(0f) }

    Slider(
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)
            // 4. Getaran Halus (Haptic) tiap kelipatan 5%
            if (abs(newValue - lastHapticValue) >= 0.05f) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                lastHapticValue = newValue
            }
        },
        onValueChangeFinished = onValueChangeFinished,
        interactionSource = interactionSource,
        modifier = modifier,
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                modifier = Modifier.scale(thumbScale) // Terapkan efek membesar
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                modifier = Modifier.height(trackHeight.dp), // Terapkan efek menebal
                drawStopIndicator = null
            )
        }
    )
}
