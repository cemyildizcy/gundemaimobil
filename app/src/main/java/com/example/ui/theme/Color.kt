package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

// Geometric Balance X/Instagram Lights-Out Aesthetic Colors
val DarkBackground = Color(0xFF000000)    // Pure lights-out OLED black
val DarkSurface = Color(0xFF0D121F)       // Extremely dark slate/indigo for main card containers
val DarkSurfaceVariant = Color(0xFF16223F) // Muted deep indigo/slate for inner containers / inputs
val DarkPrimary = Color(0xFF1D9BF0)       // Modern Twitter Electric Blue
val DarkSecondary = Color(0xFFF91880)     // Premium Instagram Magenta/Pink accent
val DarkTertiary = Color(0xFF00BA7C)      // Modern Green (Success/Verified)
val DarkError = Color(0xFFF4212E)         // Alert Red
val DarkTextPrimary = Color(0xFFE7E9EA)   // High-contrast clean silver-white
val DarkTextSecondary = Color(0xFF71767B) // Muted elegant grey

// Gradients
val BrandGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF1D9BF0), Color(0xFFF91880))
)
val BrandTextGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF1D9BF0), Color(0xFFF91880))
)
val CardGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF0D121F), Color(0xFF16223F))
)

// Premium Light Theme Colors (Clean Minimal Instagram layout)
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFF7F9F9)
val LightSurfaceVariant = Color(0xFFEFF3F4)
val LightPrimary = Color(0xFF1D9BF0)
val LightSecondary = Color(0xFFF91880)
val LightTertiary = Color(0xFF00BA7C)
val LightError = Color(0xFFF4212E)
val LightTextPrimary = Color(0xFF0F1419)
val LightTextSecondary = Color(0xFF536471)

// Helper States
val PremiumGold = Color(0xFFFFD700)       // Rich gold for premium elements
val AlertBlue = Color(0xFF3B82F6)         // Accent electric blue
val SoftGray = Color(0xFF2F3336)          // Thin border grey for list items
