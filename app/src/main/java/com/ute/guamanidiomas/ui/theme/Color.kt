package com.ute.guamanidiomas.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// 🎨 JumpUp UTE — Sistema de Diseño Premium (Blue & White Edition)
// ═══════════════════════════════════════════════════════════════

// ── Azul Principal (Brand Colors) ──────────────────────────────
val PrimaryBlue     = Color(0xFF1E40AF)  // Azul Real Profundo
val DarkBlue        = Color(0xFF1E3A8A)  // Azul Marino
val LightBlue       = Color(0xFFDBEAFE)  // Azul Cielo Muy Claro
val AccentBlue      = Color(0xFF3B82F6)  // Azul Eléctrico
val SoftBlue        = Color(0xFFEFF6FF)  // Fondo Azul Sutil

// ── Superficies y Fondos ───────────────────────────────────────
val BackgroundColor = Color(0xFFF8FAFC)  // Blanco Grisáceo muy suave
val SurfaceColor    = Color(0xFFFFFFFF)  // Blanco puro
val SurfaceElevated = Color(0xFFFFFFFF)
val PageBg          = Color(0xFFF1F5F9)  // Gris azulado claro

// ── Textos ─────────────────────────────────────────────────────
val TextPrimary     = Color(0xFF0F172A)  // Slate 900 (Casi negro azulado)
val TextSecondary   = Color(0xFF475569)  // Slate 600 (Gris oscuro)
val TextTertiary    = Color(0xFF94A3B8)  // Slate 400 (Gris medio)
val TextOnDark      = Color(0xFFFFFFFF)

// ── Semánticos (Ajustados para armonía) ────────────────────────
val Success         = Color(0xFF10B981)  // Esmeralda
val ErrorColor      = Color(0xFFEF4444)  // Rojo suave
val Warning         = Color(0xFFF59E0B)  // Ámbar
val Info            = Color(0xFF3B82F6)  // Azul Info
val PremiumGold     = Color(0xFFFFD700)  // Dorado Premium
val PrimaryRed      = Color(0xFFE11D48)  // Rojo Intenso para acentos (Rose 600)

// ── Bordes ─────────────────────────────────────────────────────
val Border          = Color(0xFFE2E8F0)
val BorderFocus     = Color(0xFF3B82F6)
val Divider         = Color(0xFFF1F5F9)

// ── Admin Light Theme ──────────────────────────────────────────
val AdminBg         = BackgroundColor
val AdminCard       = SurfaceColor
val AdminBorder     = Border
val AdminText       = TextPrimary
val AdminMuted      = TextSecondary

// ── Aliases de compatibilidad ──────────────────────────────────
val LightRed        = LightBlue
val DarkRed         = DarkBlue
val TextFaint       = TextTertiary
val SkyBlue         = AccentBlue

val GoldPrimary     = PremiumGold
val GoldDark        = Color(0xFFB8860B)
val GoldLight       = Color(0xFFFFFACD)
val GoldSecondary   = Color(0xFFDAA520)
val DeepDark        = TextPrimary
val SurfaceDark     = SurfaceColor
val Background      = BackgroundColor
val Surface         = SurfaceColor

// ── Gradientes ─────────────────────────────────────────────────
val BlueGradient    = listOf(PrimaryBlue, AccentBlue)
val SoftBlueGradient = listOf(LightBlue, SoftBlue)
val HeroGradient    = listOf(PrimaryBlue, DarkBlue)
val GoldGradient    = listOf(PremiumGold, Color(0xFFDAA520))
val DarkGradient    = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
