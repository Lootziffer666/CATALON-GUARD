package com.catalon.guard.presentation.theme

import androidx.compose.ui.graphics.Color

// Airbnb-inspired Catalon palette
val RauschRed = Color(0xFFFF5A5F)
val RauschRedDark = Color(0xFFD44B50)
val BabuTeal = Color(0xFF00A699)
val BabuTealDark = Color(0xFF008A7E)
val AccentAmber = Color(0xFFF7B731)
val DarkBackground = Color(0xFF1A1A1A)
val DarkSurface = Color(0xFF222222)
val DarkSurfaceVariant = Color(0xFF2E2E2E)
val LightBackground = Color(0xFFF5F5F5)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF0F0F0)

// Tier color indicators
val TierOneColor = Color(0xFF4CAF50)
val TierTwoColor = Color(0xFF2196F3)
val TierThreeColor = Color(0xFFFF9800)
val TierFourColor = Color(0xFF9E9E9E)
val ByokColor = Color(0xFFE91E63)

fun tierColor(tier: Int) = when (tier) {
    1 -> TierOneColor
    2 -> TierTwoColor
    3 -> TierThreeColor
    else -> TierFourColor
}
