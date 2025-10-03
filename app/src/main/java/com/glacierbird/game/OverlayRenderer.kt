package com.glacierbird.game

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
        
        // Exit Game button - measure exit text
        val exitTextWidth = buttonTextPaint.measureText("Exit Game")
        val exitButtonWidth = exitTextWidth + textPadding * 2
        val exitButtonHeight = textHeight + textPadding
        val exitY = statsY + buttonHeight + buttonSpacing
        val exitRect = RectF(
            centerX - exitButtonWidth/2f, exitY,
            centerX + exitButtonWidth/2f, exitY + exitButtonHeight
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
        canvas.drawText("Exit Game", centerX, exitY + exitButtonHeight/2f + textHeight/4f, whiteButtonTextPaint)
    }
    
    /**
     * Draw audio settings with panel background
     */
    fun drawSettings(canvas: Canvas, soundEnabled: Boolean, musicEnabled: Boolean) {
        // Semi-transparent overlay
        canvas.drawColor("#80000000".toColorInt())

        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f

        // Draw panel background
        val panelWidth = DensityUtils.UI.getMaxPanelWidth() * 0.8f // 80% of max panel width
        val panelHeight = DensityUtils.dp(490f) // Reduced height for audio settings
        val panelX = (screenWidth - panelWidth) / 2f
        val panelY = (screenHeight - panelHeight) / 2f // Center panel exactly on screen

        val panelPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
            setShadowLayer(15f, 0f, 8f, "#40000000".toColorInt())
        }

        val panelRect = RectF(panelX, panelY, panelX + panelWidth, panelY + panelHeight)
        canvas.drawRoundRect(panelRect, 20f, 20f, panelPaint)
        
        // Settings header (larger) - dark text for white panel
        val headerPaint = Paint().apply {
            color = "#2C3E50".toColorInt() // Dark blue-gray for white background
            textSize = GameConstants.getLargeTextSize(screenWidth, screenHeight)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Audio Settings", centerX, centerY - DensityUtils.dp(140f), headerPaint)

        // Section text paint (larger) - dark text for white panel
        val sectionPaint = Paint().apply {
            color = "#34495E".toColorInt() // Slightly lighter dark gray
            textSize = GameConstants.getMediumTextSize(screenWidth, screenHeight)
            typeface = Typeface.DEFAULT
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Button text paint - white text on colored buttons
        val buttonTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = DensityUtils.UI.getTextMedium() // Consistent with button responsive approach
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        
        // Sound Effects section
        canvas.drawText("Sound Effects", centerX, centerY - DensityUtils.dp(80f), sectionPaint)
        
        // Responsive sound button - optimized size for better visibility
        val soundButtonWidth = DensityUtils.dp(124f) // Optimized width for better visibility
        val soundButtonHeight = DensityUtils.dp(36f) // Optimized height for better visibility
        val soundButtonRect = RectF(
            centerX - soundButtonWidth/2, centerY - DensityUtils.dp(30f) - soundButtonHeight/2,
            centerX + soundButtonWidth/2, centerY - DensityUtils.dp(30f) + soundButtonHeight/2
        )
        val soundButtonPaint = Paint().apply {
            color = if (soundEnabled) "#27AE60".toColorInt() else "#E74C3C".toColorInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(soundButtonRect, 20f, 20f, soundButtonPaint)
        canvas.drawText(
            if (soundEnabled) "ON" else "OFF", 
            centerX, centerY - DensityUtils.dp(22f), buttonTextPaint
        )
        
        // Background Music section (more spacing)
        canvas.drawText("Background Music", centerX, centerY + DensityUtils.dp(20f), sectionPaint)
        
        // Responsive music button - maintains original 200px × 70px appearance
        val musicButtonY = centerY + DensityUtils.dp(60f) // Center Y position for music button
        val musicButtonRect = RectF(
            centerX - soundButtonWidth/2, musicButtonY - soundButtonHeight/2,
            centerX + soundButtonWidth/2, musicButtonY + soundButtonHeight/2
        )
        val musicButtonPaint = Paint().apply {
            color = if (musicEnabled) "#27AE60".toColorInt() else "#E74C3C".toColorInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(musicButtonRect, 20f, 20f, musicButtonPaint)
        canvas.drawText(
            if (musicEnabled) "ON" else "OFF",
            centerX, musicButtonY + DensityUtils.dp(8f), buttonTextPaint
        )
        
        // Responsive close button - square shape
        val closeButtonWidth = DensityUtils.dp(50f) // Square dimensions
        val closeButtonHeight = DensityUtils.dp(50f) // Square dimensions
        val closeButtonY = centerY + DensityUtils.dp(160f) // Center Y position for close button
        val closeRect = RectF(
            centerX - closeButtonWidth/2, closeButtonY - closeButtonHeight/2,
            centerX + closeButtonWidth/2, closeButtonY + closeButtonHeight/2
        )
        val closeButtonPaint = Paint().apply {
            color = "#E74C3C".toColorInt()  // More visible red color
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(closeRect, 25f, 25f, closeButtonPaint)
        canvas.drawText("✕", centerX, closeButtonY + 12f, buttonTextPaint)
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
        
        // Enhanced main shop panel with responsive sizing
        val panelWidth = DensityUtils.UI.getMaxPanelWidth() * 0.85f // 85% of max panel width
        val panelHeight = DensityUtils.dp(500f) // Responsive height
        val panelX = (screenWidth - panelWidth) / 2f
        val panelY = (screenHeight - panelHeight) / 2f
        val panelRect = RectF(panelX, panelY, panelX + panelWidth, panelY + panelHeight)
        
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
        
        // Clean shop header with responsive sizing
        val headerPaint = Paint().apply {
            color = "#FFD700".toColorInt() // Gold color
            textSize = DensityUtils.UI.getTextTitle() // Responsive text size
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
        drawEnhancedShopCloseButton(canvas)
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
        val itemWidth = DensityUtils.dp(240f) // Responsive width
        val itemHeight = DensityUtils.dp(50f) // Responsive height
        var itemY = centerY + DensityUtils.dp(30f) // Responsive positioning
        val itemSpacing = DensityUtils.dp(60f) // Responsive spacing
        
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
            
            // Emoji positioned from center - responsive sizing
            val emojiPaint = Paint().apply {
                textSize = DensityUtils.UI.getTextLarge() // Responsive text size
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(emoji, centerX - itemWidth/2 + DensityUtils.dp(25f), itemY + DensityUtils.dp(8f), emojiPaint)
            
            // Feature text positioned from center - responsive sizing
            val textPaint = Paint().apply {
                color = if (isBuyLife && canAfford) Color.WHITE else if (isBuyLife) "#8D6E63".toColorInt() else "#2C3E50".toColorInt()
                textSize = DensityUtils.UI.getTextMedium() // Responsive text size
                typeface = if (isBuyLife) Typeface.DEFAULT_BOLD else Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
                setShadowLayer(if (isBuyLife && canAfford) 2f else 0f, 0f, 1f, "#000000".toColorInt())
            }
            canvas.drawText(text, centerX - itemWidth/2 + DensityUtils.dp(50f), itemY + DensityUtils.dp(8f), textPaint)
            
            itemY += itemSpacing
        }
    }

    /**
     * Draw enhanced shop close button
     */
    private fun drawEnhancedShopCloseButton(canvas: Canvas) {
        val closeBtnSize = DensityUtils.dp(60f) // Responsive size
        val panelWidth = DensityUtils.UI.getMaxPanelWidth() * 0.85f
        val panelHeight = DensityUtils.dp(500f)
        val panelX = (screenWidth - panelWidth) / 2f
        val panelY = (screenHeight - panelHeight) / 2f

        // Position close button at top-right of panel
        val closeX = panelX + panelWidth - closeBtnSize - DensityUtils.dp(15f)
        val closeY = panelY + DensityUtils.dp(15f)
        val closeRect = RectF(closeX, closeY, closeX + closeBtnSize, closeY + closeBtnSize)
        
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