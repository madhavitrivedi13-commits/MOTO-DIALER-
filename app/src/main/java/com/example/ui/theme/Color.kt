package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Brand accents: Motorola vibrant cyan/blue & emerald call green
val MotoBlue = Color(0xFF0070F3)
val MotoCyan = Color(0xFF00C7D7)
val MotoGreen = Color(0xFF10B981)
val MotoGreenDark = Color(0xFF059669)
val MotoGreenGlow = Color(0xFF34D399)
val MotoRed = Color(0xFFEF4444)
val MotoRedGlow = Color(0xFFF87171)
val MotoOrange = Color(0xFFF59E0B)

// Liquid Glass Specular Rims & Overlays
val GlassWhiteHigh = Color(0x33FFFFFF) // 20% white highlight for specular edges
val GlassWhiteMid = Color(0x1AFFFFFF)  // 10% white
val GlassWhiteLow = Color(0x0DFFFFFF)  // 5% white
val GlassDarkHigh = Color(0x40000000)
val GlassDarkMid = Color(0x26000000)

// Light Liquid Palette
val LightLiquidBackground = Color(0xFFF0F4F9)
val LightLiquidSurface = Color(0xE6FFFFFF)        // 90% translucent white
val LightLiquidSurfaceVariant = Color(0x99E2E8F0) // Frosted glass variant
val LightLiquidCard = Color(0xCCFFFFFF)          // 80% white glass
val LightOnBackground = Color(0xFF0F172A)
val LightOnSurface = Color(0xFF1E293B)
val LightOnSurfaceVariant = Color(0xFF64748B)
val LightKeypadLiquid = Color(0x80FFFFFF)
val LightKeypadLiquidPressed = Color(0xB3FFFFFF)
val LightLiquidBorder = Color(0x40CBD5E1)

// Dark Liquid Palette (Obsidian Frosted Glass)
val DarkLiquidBackground = Color(0xFF070B12)
val DarkLiquidSurface = Color(0xD90F1626)        // 85% translucent obsidian
val DarkLiquidSurfaceVariant = Color(0x99172238) // Frosted glass variant
val DarkLiquidCard = Color(0x80131E33)          // 50% obsidian glass
val DarkOnBackground = Color(0xFFF8FAFC)
val DarkOnSurface = Color(0xFFE2E8F0)
val DarkOnSurfaceVariant = Color(0xFF94A3B8)
val DarkKeypadLiquid = Color(0x331E293B)
val DarkKeypadLiquidPressed = Color(0x66334155)
val DarkLiquidBorder = Color(0x3338BDF8) // Subtle electric cyan glass border

// Liquid Avatar Palette with rich 2-stop gradients
val AvatarPalette = listOf(
    Pair(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
    Pair(Color(0xFF10B981), Color(0xFF047857)),
    Pair(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
    Pair(Color(0xFFF59E0B), Color(0xFFD97706)),
    Pair(Color(0xFFEC4899), Color(0xFFBE185D)),
    Pair(Color(0xFF06B6D4), Color(0xFF0E7490)),
    Pair(Color(0xFFF97316), Color(0xFFC2410C)),
    Pair(Color(0xFF6366F1), Color(0xFF4338CA))
)
