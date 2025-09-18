package com.flappie.game

import android.graphics.*
import androidx.core.graphics.toColorInt

/**
 * OverlayRenderer - Handles overlay menus (pause, settings, shop)
 */
class OverlayRenderer(
    private val screenWidth: Int,
    private val screenHeight: Int
) {
    
    // Paint objects moved to local scope where needed
    
    /**
     * Draw pause menu overlay
     */
    fun drawPauseMenu(canvas: Canvas) {
        // Semi-transparent overlay
        canvas.drawColor("#80000000".toColorInt())
        
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f
        
        // Title paint
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = GameConstants.getLargeTextSize(screenWidth, screenHeight)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val titleOffset = GameConstants.scaledSize(150f, screenWidth, screenHeight)
        canvas.drawText("PAUSED", centerX, centerY - titleOffset, titlePaint)
        
        // Button paint
        val buttonPaint = Paint().apply {
            color = GameConstants.BUTTON_COLOR.toColorInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        
        // Button text paint
        val buttonTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = GameConstants.getMediumTextSize(screenWidth, screenHeight)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        
        // Measure text to ensure buttons are always larger than content
        val resumeTextWidth = buttonTextPaint.measureText("RESUME")
        val statisticsTextWidth = buttonTextPaint.measureText("STATISTICS")
        val textHeight = buttonTextPaint.textSize
        
        // Button dimensions - always larger than text with padding (responsive)
        val textPadding = GameConstants.scaledSize(40f, screenWidth, screenHeight)
        val buttonWidth = maxOf(resumeTextWidth, statisticsTextWidth) + textPadding * 2
        val buttonHeight = textHeight + textPadding
        val buttonSpacing = GameConstants.scaledSize(35f, screenWidth, screenHeight)
        
        // Resume button
        val playY = centerY - GameConstants.scaledSize(80f, screenWidth, screenHeight)
        val playRect = RectF(
            centerX - buttonWidth/2f, playY,
            centerX + buttonWidth/2f, playY + buttonHeight
        )
        canvas.drawRoundRect(playRect, 15f, 15f, buttonPaint)
        canvas.drawText("RESUME", centerX, playY + buttonHeight/2f + textHeight/4f, buttonTextPaint)
        
        // Statistics button
        val statsY = playY + buttonHeight + buttonSpacing
        val statsRect = RectF(
            centerX - buttonWidth/2f, statsY,
            centerX + buttonWidth/2f, statsY + buttonHeight
        )
        canvas.drawRoundRect(statsRect, 15f, 15f, buttonPaint)
        canvas.drawText("STATISTICS", centerX, statsY + buttonHeight/2f + textHeight/4f, buttonTextPaint)
        
        // Exit Game button (square) - measure exit text
        val exitTextSize = buttonTextPaint.measureText("✕")
        val exitButtonSize = maxOf(exitTextSize + textPadding, textHeight + textPadding)
        val exitY = statsY + buttonHeight + buttonSpacing
        val exitRect = RectF(
            centerX - exitButtonSize/2f, exitY,
            centerX + exitButtonSize/2f, exitY + exitButtonSize
        )
        val redButtonPaint = Paint().apply {
            color = GameConstants.RED_BUTTON_COLOR.toColorInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(exitRect, 15f, 15f, redButtonPaint)
        
        val whiteButtonTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = GameConstants.getMediumTextSize(screenWidth, screenHeight)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("✕", centerX, exitY + exitButtonSize/2f + textHeight/4f, whiteButtonTextPaint)
    }
    
    /**
     * Draw inline audio settings without panel
     */
    fun drawSettings(canvas: Canvas, soundEnabled: Boolean, musicEnabled: Boolean) {
        // Semi-transparent overlay
        canvas.drawColor("#80000000".toColorInt())
        
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f
        
        // Settings header (larger)
        val headerPaint = Paint().apply {
            color = Color.WHITE
            textSize = GameConstants.getLargeTextSize(screenWidth, screenHeight)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Audio Settings", centerX, centerY - 140f, headerPaint)
        
        // Section text paint (larger)
        val sectionPaint = Paint().apply {
            color = Color.WHITE
            textSize = GameConstants.getMediumTextSize(screenWidth, screenHeight)
            typeface = Typeface.DEFAULT
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        
        // Button text paint (larger)
        val buttonTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = GameConstants.getSmallTextSize(screenWidth, screenHeight)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        
        // Sound Effects section
        canvas.drawText("Sound Effects", centerX, centerY - 80f, sectionPaint)
        
        // Larger sound button (200px wide x 70px high)
        val soundButtonRect = RectF(centerX - 100f, centerY - 35f, centerX + 100f, centerY + 35f)
        val soundButtonPaint = Paint().apply {
            color = if (soundEnabled) "#27AE60".toColorInt() else "#E74C3C".toColorInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(soundButtonRect, 20f, 20f, soundButtonPaint)
        canvas.drawText(
            if (soundEnabled) "ON" else "OFF", 
            centerX, centerY + 12f, buttonTextPaint
        )
        
        // Background Music section (more spacing)
        canvas.drawText("Background Music", centerX, centerY + 90f, sectionPaint)
        
        // Larger music button (200px wide x 70px high)
        val musicButtonRect = RectF(centerX - 100f, centerY + 125f, centerX + 100f, centerY + 195f)
        val musicButtonPaint = Paint().apply {
            color = if (musicEnabled) "#27AE60".toColorInt() else "#E74C3C".toColorInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(musicButtonRect, 20f, 20f, musicButtonPaint)
        canvas.drawText(
            if (musicEnabled) "ON" else "OFF", 
            centerX, centerY + 172f, buttonTextPaint
        )
        
        // Much larger close button (moved further down)
        val closeRect = RectF(centerX - 120f, centerY + 280f, centerX + 120f, centerY + 350f)
        val closeButtonPaint = Paint().apply {
            color = "#E74C3C".toColorInt()  // More visible red color
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(closeRect, 25f, 25f, closeButtonPaint)
        canvas.drawText("✕", centerX, centerY + 325f, buttonTextPaint)
    }
    
    
    /**
     * Draw enhanced shop menu overlay
     */
    fun drawShop(canvas: Canvas, totalCoins: Int) {
        // Enhanced dark gradient overlay
        val overlayPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, screenHeight.toFloat(),
                intArrayOf("#D0000000".toColorInt(), "#90000000".toColorInt()),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), overlayPaint)
        
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f
        
        // Enhanced main shop panel with gradient background
        val panelRect = RectF(
            centerX - 320f, centerY - 280f,
            centerX + 320f, centerY + 350f
        )
        
        // Panel gradient background
        val panelGradientPaint = Paint().apply {
            shader = LinearGradient(
                panelRect.left, panelRect.top, panelRect.right, panelRect.bottom,
                intArrayOf("#FFFFFF".toColorInt(), "#F8F9FA".toColorInt(), "#E9ECEF".toColorInt()),
                null, Shader.TileMode.CLAMP
            )
            style = Paint.Style.FILL
            isAntiAlias = true
            setShadowLayer(20f, 0f, 10f, "#60000000".toColorInt())
        }
        canvas.drawRoundRect(panelRect, 30f, 30f, panelGradientPaint)
        
        // Panel border
        val borderPaint = Paint().apply {
            color = "#D4AF37".toColorInt() // Gold border
            style = Paint.Style.STROKE
            strokeWidth = 4f
            isAntiAlias = true
        }
        canvas.drawRoundRect(panelRect, 30f, 30f, borderPaint)
        
        // Clean shop header without glow effect
        val headerPaint = Paint().apply {
            color = "#FFD700".toColorInt() // Gold color
            textSize = 48f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("🛍️ PREMIUM SHOP", centerX, centerY - 200f, headerPaint)
        
        // Enhanced coin balance with premium styling
        drawEnhancedCoinBalance(canvas, centerX, centerY, totalCoins)
        
        // Premium "Coming Soon" section - removed unused paint
        
        // Coming soon banner background
        val bannerRect = RectF(centerX - 150f, centerY - 85f, centerX + 150f, centerY - 45f)
        val bannerPaint = Paint().apply {
            shader = LinearGradient(
                bannerRect.left, bannerRect.top, bannerRect.right, bannerRect.bottom,
                intArrayOf("#A29BFE".toColorInt(), "#6C5CE7".toColorInt()),
                null, Shader.TileMode.CLAMP
            )
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(bannerRect, 20f, 20f, bannerPaint)
        
        val bannerTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("✨ COMING SOON ✨", centerX, centerY - 58f, bannerTextPaint)
        
        // Enhanced feature list
        drawEnhancedShopFeatures(canvas, centerX, centerY, totalCoins)
        
        // Enhanced close button
        drawEnhancedShopCloseButton(canvas, centerX, centerY)
    }
    
    /**
     * Draw enhanced coin balance display
     */
    private fun drawEnhancedCoinBalance(canvas: Canvas, centerX: Float, centerY: Float, totalCoins: Int) {
        val coinBgRect = RectF(centerX - 140f, centerY - 155f, centerX + 140f, centerY - 115f)
        
        // Gradient background for coin display
        val coinBgPaint = Paint().apply {
            shader = LinearGradient(
                coinBgRect.left, coinBgRect.top, coinBgRect.right, coinBgRect.bottom,
                intArrayOf("#FFE082".toColorInt(), "#FFD54F".toColorInt(), "#FFC107".toColorInt()),
                null, Shader.TileMode.CLAMP
            )
            style = Paint.Style.FILL
            isAntiAlias = true
            setShadowLayer(6f, 0f, 3f, "#80000000".toColorInt())
        }
        canvas.drawRoundRect(coinBgRect, 20f, 20f, coinBgPaint)
        
        // Coin border
        val coinBorderPaint = Paint().apply {
            color = "#F57F17".toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawRoundRect(coinBgRect, 20f, 20f, coinBorderPaint)
        
        val coinHeaderPaint = Paint().apply {
            color = "#BF360C".toColorInt()
            textSize = 36f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            setShadowLayer(3f, 0f, 1f, "#FFFFFF".toColorInt())
        }
        canvas.drawText("💰 $totalCoins COINS", centerX, centerY - 125f, coinHeaderPaint)
    }

    /**
     * Draw enhanced shop feature list
     */
    private fun drawEnhancedShopFeatures(canvas: Canvas, centerX: Float, centerY: Float, totalCoins: Int = 0) {
        val itemWidth = 320f
        val itemHeight = 55f
        var itemY = centerY + 30f
        val itemSpacing = 60f
        
        val features = listOf(
            Pair("❤️", "Buy Life - 500 Coins"),
            Pair("🎨", "Bird Skins & Themes"),
            Pair("⚡", "Power-up Upgrades"),
            Pair("✨", "Visual Effects")
        )
        
        features.forEachIndexed { index, feature ->
            val (emoji, text) = feature
            val isBuyLife = index == 0 // First item is buy life
            val canAfford = totalCoins >= 500
            
            // Centered item background with gradient
            val itemBgRect = RectF(
                centerX - itemWidth/2, itemY - itemHeight/2,
                centerX + itemWidth/2, itemY + itemHeight/2
            )
            
            // Special styling for buy life option
            val itemGradientPaint = Paint().apply {
                shader = if (isBuyLife && canAfford) {
                    LinearGradient(
                        itemBgRect.left, itemBgRect.top, itemBgRect.right, itemBgRect.bottom,
                        intArrayOf("#4CAF50".toColorInt(), "#388E3C".toColorInt()), // Green for affordable
                        null, Shader.TileMode.CLAMP
                    )
                } else if (isBuyLife) { // When not affordable
                    LinearGradient(
                        itemBgRect.left, itemBgRect.top, itemBgRect.right, itemBgRect.bottom,
                        intArrayOf("#FFB74D".toColorInt(), "#F57C00".toColorInt()), // Orange for not affordable
                        null, Shader.TileMode.CLAMP
                    )
                } else {
                    LinearGradient(
                        itemBgRect.left, itemBgRect.top, itemBgRect.right, itemBgRect.bottom,
                        intArrayOf("#F8F9FA".toColorInt(), "#E9ECEF".toColorInt()),
                        null, Shader.TileMode.CLAMP
                    )
                }
                style = Paint.Style.FILL
                isAntiAlias = true
                setShadowLayer(3f, 0f, 1f, "#40000000".toColorInt())
            }
            canvas.drawRoundRect(itemBgRect, 15f, 15f, itemGradientPaint)
            
            // Emoji positioned from center
            val emojiPaint = Paint().apply {
                textSize = 32f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(emoji, centerX - itemWidth/2 + 30f, itemY + 8f, emojiPaint)
            
            // Feature text positioned from center
            val textPaint = Paint().apply {
                color = if (isBuyLife && canAfford) Color.WHITE else if (isBuyLife) "#8D6E63".toColorInt() else "#2C3E50".toColorInt()
                textSize = if (isBuyLife) 26f else 28f
                typeface = if (isBuyLife) Typeface.DEFAULT_BOLD else Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
                setShadowLayer(if (isBuyLife && canAfford) 2f else 0f, 0f, 1f, "#000000".toColorInt())
            }
            canvas.drawText(text, centerX - itemWidth/2 + 70f, itemY + 8f, textPaint)
            
            itemY += itemSpacing
        }
    }

    /**
     * Draw enhanced shop close button
     */
    private fun drawEnhancedShopCloseButton(canvas: Canvas, centerX: Float, centerY: Float) {
        val closeBtnSize = 70f
        val closeRect = RectF(
            centerX + 240f, centerY - 260f,
            centerX + 240f + closeBtnSize, centerY - 260f + closeBtnSize
        )
        
        // Enhanced close button with gradient
        val closeBtnPaint = Paint().apply {
            shader = LinearGradient(
                closeRect.left, closeRect.top, closeRect.right, closeRect.bottom,
                intArrayOf("#FF6B6B".toColorInt(), "#E74C3C".toColorInt()),
                null, Shader.TileMode.CLAMP
            )
            style = Paint.Style.FILL
            isAntiAlias = true
            setShadowLayer(12f, 0f, 6f, "#80000000".toColorInt())
        }
        canvas.drawRoundRect(closeRect, 35f, 35f, closeBtnPaint)
        
        // Close button border
        val closeBorderPaint = Paint().apply {
            color = "#C0392B".toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        canvas.drawRoundRect(closeRect, 35f, 35f, closeBorderPaint)
        
        val closeTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 40f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            setShadowLayer(4f, 0f, 2f, "#000000".toColorInt())
        }
        canvas.drawText("✕", closeRect.centerX(), closeRect.centerY() + 14f, closeTextPaint)
    }
    
    // Removed unused legacy methods
    
    // Removed unused button area functions - touch detection is handled in TouchHandler with inline coordinates
}