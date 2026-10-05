package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun AlphabetIndexRail(
    availableLetters: Set<Char>,
    activeLetter: Char?,
    onLetterSelected: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    val alphabet = remember { ('A'..'Z').toList() + listOf('#') }
    var railHeight by remember { mutableStateOf(1) }
    var isDragging by remember { mutableStateOf(false) }
    var selectedLetter by remember { mutableStateOf<Char?>(null) }
    var touchY by remember { mutableStateOf(0f) }

    fun processTouch(y: Float) {
        if (railHeight <= 0) return
        val clampedY = y.coerceIn(0f, railHeight.toFloat() - 1f)
        val index = ((clampedY / railHeight) * alphabet.size).toInt().coerceIn(0, alphabet.lastIndex)
        val letter = alphabet[index]
        selectedLetter = letter
        touchY = clampedY
        onLetterSelected(letter)
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(28.dp)
            .padding(vertical = 8.dp)
            .onSizeChanged { railHeight = it.height }
            .pointerInput(alphabet) {
                detectTapGestures(
                    onPress = { offset ->
                        isDragging = true
                        processTouch(offset.y)
                        tryAwaitRelease()
                        isDragging = false
                        selectedLetter = null
                    }
                )
            }
            .pointerInput(alphabet) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        processTouch(offset.y)
                    },
                    onDragEnd = {
                        isDragging = false
                        selectedLetter = null
                    },
                    onDragCancel = {
                        isDragging = false
                        selectedLetter = null
                    },
                    onVerticalDrag = { change, _ ->
                        change.consume()
                        processTouch(change.position.y)
                    }
                )
            },
        contentAlignment = Alignment.CenterEnd
    ) {
        // Vertical letter column
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            alphabet.forEach { char ->
                val isAvailable = availableLetters.contains(char)
                val isCurrent = char == (selectedLetter ?: activeLetter)

                Text(
                    text = char.toString(),
                    fontSize = 9.sp,
                    fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                    color = when {
                        isCurrent -> MaterialTheme.colorScheme.primary
                        isAvailable -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    },
                    textAlign = TextAlign.Center,
                    lineHeight = 10.sp
                )
            }
        }

        // Floating Magnifier Indicator Bubble
        AnimatedVisibility(
            visible = isDragging && selectedLetter != null,
            enter = fadeIn() + scaleIn(spring(stiffness = 500f)),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = -90,
                        y = (touchY - (railHeight / 2f)).roundToInt().coerceIn(-railHeight / 2 + 30, railHeight / 2 - 30)
                    )
                }
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .size(54.dp)
                    .testTag("alphabet_bubble")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = selectedLetter?.toString() ?: "",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
