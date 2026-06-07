package com.ute.guamanidiomas.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// 🎨 JumpUp UTE — Sistema de Diseño Premium
// ═══════════════════════════════════════════════════════════════

// ── Azul Principal (Brand Colors - siempre fijos) ──────────────
val PrimaryBlue     = Color(0xFF1E40AF)
val DarkBlue        = Color(0xFF1E3A8A)
val LightBlue       = Color(0xFFDBEAFE)
val AccentBlue      = Color(0xFF3B82F6)
val SoftBlue        = Color(0xFFEFF6FF)

// ── Semánticos (fijos) ─────────────────────────────────────────
val Success         = Color(0xFF10B981)
val ErrorColor      = Color(0xFFEF4444)
val Warning         = Color(0xFFF59E0B)
val Info            = Color(0xFF3B82F6)
val PremiumGold     = Color(0xFFFFD700)
val PrimaryRed      = Color(0xFFE11D48)

// ── Valores Light (usados en lightColorScheme) ─────────────────
private val BackgroundLight = Color(0xFFF8FAFC)
private val SurfaceLight    = Color(0xFFFFFFFF)
private val PageBgLight     = Color(0xFFF1F5F9)
private val TextPrimaryLight   = Color(0xFF0F172A)
private val TextSecondaryLight = Color(0xFF475569)
private val TextTertiaryLight  = Color(0xFF94A3B8)
private val BorderLight     = Color(0xFFE2E8F0)
private val DividerLight    = Color(0xFFF1F5F9)

// ── Valores Dark ───────────────────────────────────────────────
private val BackgroundDark  = Color(0xFF0F172A)
private val SurfaceDark     = Color(0xFF1E293B)
private val PageBgDark      = Color(0xFF1E293B)
private val TextPrimaryDark    = Color(0xFFF1F5F9)
private val TextSecondaryDark  = Color(0xFF94A3B8)
private val TextTertiaryDark   = Color(0xFF64748B)
private val BorderDark      = Color(0xFF475569)
private val DividerDark     = Color(0xFF334155)

// ── Colores adaptables (leen del LocalIsDarkTheme) ─────────────
val BackgroundColor: Color
    @Composable @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) BackgroundDark else BackgroundLight

val SurfaceColor: Color
    @Composable @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) SurfaceDark else SurfaceLight

val SurfaceElevated: Color
    @Composable @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) Color(0xFF334155) else SurfaceLight

val PageBg: Color
    @Composable @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) PageBgDark else PageBgLight

val TextPrimary: Color
    @Composable @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) TextPrimaryDark else TextPrimaryLight

val TextSecondary: Color
    @Composable @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) TextSecondaryDark else TextSecondaryLight

val TextTertiary: Color
    @Composable @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) TextTertiaryDark else TextTertiaryLight

val TextOnDark = Color(0xFFFFFFFF)

val Border: Color
    @Composable @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) BorderDark else BorderLight

val BorderFocus = Color(0xFF3B82F6)

val Divider: Color
    @Composable @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) DividerDark else DividerLight

// ── Admin Theme ────────────────────────────────────────────────
val AdminBg: Color @Composable @ReadOnlyComposable get() = BackgroundColor
val AdminCard: Color @Composable @ReadOnlyComposable get() = SurfaceColor
val AdminBorder: Color @Composable @ReadOnlyComposable get() = Border
val AdminText: Color @Composable @ReadOnlyComposable get() = TextPrimary
val AdminMuted: Color @Composable @ReadOnlyComposable get() = TextSecondary

// ── Aliases de compatibilidad ──────────────────────────────────
val LightRed        = LightBlue
val DarkRed         = DarkBlue
val TextFaint: Color @Composable @ReadOnlyComposable get() = TextTertiary
val SkyBlue         = AccentBlue

val GoldPrimary     = PremiumGold
val GoldDark        = Color(0xFFB8860B)
val GoldLight       = Color(0xFFFFFACD)
val GoldSecondary   = Color(0xFFDAA520)
val DeepDark: Color @Composable @ReadOnlyComposable get() = TextPrimary
// Note: estos no pueden ser @Composable porque se usan en contextos no composable en algunos sitios
// Para "Surface" y "Background" como alias globales, usamos las versiones composable
val Background: Color @Composable @ReadOnlyComposable get() = BackgroundColor
val Surface: Color @Composable @ReadOnlyComposable get() = SurfaceColor

// ── Gradientes ─────────────────────────────────────────────────
val BlueGradient    = listOf(PrimaryBlue, AccentBlue)
val SoftBlueGradient = listOf(LightBlue, SoftBlue)
val HeroGradient    = listOf(PrimaryBlue, DarkBlue)
val GoldGradient    = listOf(PremiumGold, Color(0xFFDAA520))
val DarkGradient    = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
