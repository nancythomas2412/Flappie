package com.glacierbird.game

import android.graphics.*
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withClip
import kotlin.math.*

/**
 * HatRenderer - Beautiful hat selection and showcase UI
 * Achievement-based cosmetic system
 */
class HatRenderer(
    private val screenWidth: Int,
    private val screenHeight: Int
) {
    
    // Sizing methods - Now density-aware
    private fun getHatCardHeight(): Float = DensityUtils.Hats.getCardHeight()
    private fun getHatSpriteSize(): Float = DensityUtils.Hats.getSpriteSize()
    
    // Background and panels
    private val overlayPaint = Paint().apply {
        color = "#F0000000".toColorInt() // Slightly darker overlay for focus
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val panelPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
        setShadowLayer(20f, 0f, 10f, "#60000000".toColorInt())
    }
    
    // Title and text (density-aware sizes)
    private val titlePaint = Paint().apply {
        color = "#2C3E50".toColorInt()
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val hatNamePaint = Paint().apply {
        color = "#2C3E50".toColorInt()
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val hatDescPaint = Paint().apply {
        color = "#5D6D7E".toColorInt()
        typeface = Typeface.DEFAULT
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private fun ensureTextSizesSet() {
        // Set text sizes every time to ensure proper scaling
        titlePaint.textSize = DensityUtils.UI.getTextTitle()
        hatNamePaint.textSize = DensityUtils.Hats.getNameTextSize()
        hatDescPaint.textSize = DensityUtils.Hats.getDescriptionTextSize()
        buttonTextPaint.textSize = DensityUtils.UI.getTextSmall()
        equippedTextPaint.textSize = DensityUtils.UI.getTextSmall()
        closeButtonTextPaint.textSize = DensityUtils.UI.getTextLarge()
        rarityTextPaint.textSize = DensityUtils.UI.getTextSmall()
    }
    
    // Hat card backgrounds
    private val unlockedCardPaint = Paint().apply {
        color = "#FFFFFF".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
        setShadowLayer(8f, 0f, 4f, "#40000000".toColorInt())
    }
    
    private val selectedCardPaint = Paint().apply {
        color = "#E8F5E8".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
        setShadowLayer(8f, 0f, 4f, "#40000000".toColorInt())
    }
    
    private val lockedCardPaint = Paint().apply {
        color = "#F8F9FA".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    // Borders
    private val selectedBorderPaint = Paint().apply {
        color = "#27AE60".toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = DensityUtils.UI.getStrokeWidthThick()
        isAntiAlias = true
    }
    
    private val rarityBorderPaints = mapOf(
        HatRarity.COMMON to Paint().apply {
            color = HatRarity.COMMON.color.toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = DensityUtils.UI.getStrokeWidth()
            isAntiAlias = true
        },
        HatRarity.RARE to Paint().apply {
            color = HatRarity.RARE.color.toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = DensityUtils.UI.getStrokeWidth()
            isAntiAlias = true
        },
        HatRarity.EPIC to Paint().apply {
            color = HatRarity.EPIC.color.toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = DensityUtils.UI.getStrokeWidth()
            isAntiAlias = true
        },
        HatRarity.LEGENDARY to Paint().apply {
            color = HatRarity.LEGENDARY.color.toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = DensityUtils.UI.getStrokeWidth()
            isAntiAlias = true
        }
    )
    
    // Buttons
    private val selectButtonPaint = Paint().apply {
        color = "#27AE60".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val buyButtonPaint = Paint().apply {
        color = "#3498DB".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val buttonTextPaint = Paint().apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val equippedTextPaint = Paint().apply {
        color = Color.BLACK
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    // Close button (much larger)
    private val closeButtonPaint = Paint().apply {
        color = "#E74C3C".toColorInt()  // More visible red color
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val closeButtonTextPaint = Paint().apply {
        color = "#FFFFFF".toColorInt()
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    // Lock overlay
    private val lockOverlayPaint = Paint().apply {
        color = "#80000000".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    // Sprite painting - static Paint objects to avoid recreation
    private val bodyPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val outlinePaint = Paint().apply {
        color = "#2C3E50".toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = true
    }
    
    private val eyePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val pupilPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val beakPaint = Paint().apply {
        color = "#F39C12".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    // Special effects Paint objects
    private val sparklePaint = Paint().apply {
        color = "#FFFF00".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val magicPaint = Paint().apply {
        color = "#9400D3".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val gridPaint = Paint().apply {
        color = "#00FFFF".toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 1f
        isAntiAlias = true
        alpha = 128
    }
    
    private val runePaint = Paint().apply {
        color = "#4682B4".toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }
    
    // Rarity text paint (reusable for different colors)
    private val rarityTextPaint = Paint().apply {
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    /**
     * Draw hat selection screen
     */
    fun drawHatSelectionScreen(
        canvas: Canvas,
        hatInfoList: List<HatInfo>,
        collectionStats: HatCollectionStats,
        scrollOffset: Float = 0f
    ) {
        // Ensure text sizes are properly set
        ensureTextSizesSet()

        // Overlay
        canvas.drawColor(overlayPaint.color)
        
        // Larger main panel with more space
        val panelWidth = screenWidth * 0.95f  // Wider panel
        val panelHeight = screenHeight * 0.90f  // Taller panel
        val panelX = (screenWidth - panelWidth) / 2f
        val panelY = (screenHeight - panelHeight) / 2f
        
        val panelRect = RectF(panelX, panelY, panelX + panelWidth, panelY + panelHeight)
        canvas.drawRoundRect(panelRect, DensityUtils.scale(25f), DensityUtils.scale(25f), panelPaint)
        
        // Title with more space
        canvas.drawText("🎩 Hat Collection", screenWidth / 2f, panelY + DensityUtils.dp(50f), titlePaint)

        // Collection stats with better spacing
        val statsText = "${collectionStats.unlockedHats}/${collectionStats.totalHats} Collected (${collectionStats.collectionPercentage}%)"
        canvas.drawText(statsText, screenWidth / 2f, panelY + DensityUtils.dp(90f), hatDescPaint)
        
        // Close button (larger and properly positioned)
        drawCloseButton(canvas, panelX + panelWidth - DensityUtils.dp(60f), panelY + DensityUtils.dp(10f))
        
        // Content area with generous padding for larger cards
        val contentStartY = panelY + DensityUtils.dp(120f)     // Reduced space for title/stats
        val contentWidth = panelWidth - DensityUtils.dp(80f)   // Responsive side padding
        val contentX = panelX + DensityUtils.dp(40f)           // Responsive side padding
        
        // Apply scroll offset and clipping with optimized bounds
        canvas.withClip(
            panelX + 15f,
            contentStartY,
            panelX + panelWidth - 15f,
            panelY + panelHeight - 20f  // Reduced bottom margin
        ) {
            drawHatGrid(this, hatInfoList, contentX, contentStartY + scrollOffset, contentWidth)
        }
    }
    
    /**
     * Draw hat grid layout
     */
    private fun drawHatGrid(
        canvas: Canvas,
        hatInfoList: List<HatInfo>,
        startX: Float,
        startY: Float,
        gridWidth: Float
    ) {
        val itemsPerRow = 2  // Restored to original 2 items per row
        val cardWidth = (gridWidth - DensityUtils.dp(10f)) / itemsPerRow // Further increased card width
        val cardHeight = getHatCardHeight()  // Now uses responsive screen-based sizing
        val spacing = DensityUtils.dp(20f)      // Reduced spacing for larger cards
        
        var currentX = startX
        var currentY = startY
        var itemsInCurrentRow = 0
        
        hatInfoList.forEachIndexed { _, hatInfo ->
            drawHatCard(canvas, hatInfo, currentX, currentY, cardWidth, cardHeight)
            
            itemsInCurrentRow++
            
            if (itemsInCurrentRow >= itemsPerRow) {
                // Move to next row
                currentX = startX
                currentY += cardHeight + spacing
                itemsInCurrentRow = 0
            } else {
                // Move to next column
                currentX += cardWidth + spacing
            }
        }
    }
    
    /**
     * Draw individual hat card
     */
    private fun drawHatCard(
        canvas: Canvas,
        hatInfo: HatInfo,
        x: Float,
        y: Float,
        width: Float,
        @Suppress("SameParameterValue") height: Float
    ) {
        val cardRect = RectF(x, y, x + width, y + height)
        
        // Card background
        val backgroundPaint = when {
            hatInfo.isSelected -> selectedCardPaint
            hatInfo.isUnlocked -> unlockedCardPaint
            else -> lockedCardPaint
        }
        canvas.drawRoundRect(cardRect, DensityUtils.scale(16f), DensityUtils.scale(16f), backgroundPaint)
        
        // Rarity border
        rarityBorderPaints[hatInfo.type.rarity]?.let { borderPaint ->
            canvas.drawRoundRect(cardRect, DensityUtils.scale(16f), DensityUtils.scale(16f), borderPaint)
        }

        // Selected border
        if (hatInfo.isSelected) {
            canvas.drawRoundRect(cardRect, DensityUtils.scale(16f), DensityUtils.scale(16f), selectedBorderPaint)
        }
        
        // Bird sprite (positioned higher to make room for text)
        val spriteSize = getHatSpriteSize()
        val spriteX = x + width / 2f
        val spritePadding = height * 0.03f  // 3% padding from top
        val spriteY = y + height * 0.20f + spritePadding  // 20% down + padding from top
        
        if (hatInfo.isUnlocked) {
            // Draw actual hat sprite if available, otherwise use placeholder
            if (hatInfo.sprite != null) {
                drawActualHatSprite(canvas, spriteX, spriteY, spriteSize, hatInfo.sprite, hatType = hatInfo.type)
            } else {
                drawHatSpritePlaceholder(canvas, spriteX, spriteY, spriteSize, hatInfo.type)
            }
            
            // Add special effects for premium hats
            drawHatSpecialEffects(canvas, spriteX, spriteY, spriteSize, hatInfo.type)
        } else {
            // Draw locked placeholder - use sprite preview if available
            if (hatInfo.sprite != null) {
                drawActualHatSprite(canvas, spriteX, spriteY, spriteSize, hatInfo.sprite, isLocked = true, hatType = hatInfo.type)
            } else {
                drawHatSpritePlaceholder(canvas, spriteX, spriteY, spriteSize, hatInfo.type)
            }
            
            // Lock overlay (smaller size to show more hat details)
            val overlaySize = spriteSize * 0.4f  // 40% of sprite size - smaller to show more hat
            val lockRect = RectF(spriteX - overlaySize/2f, spriteY - overlaySize/2f, 
                                spriteX + overlaySize/2f, spriteY + overlaySize/2f)
            canvas.drawRoundRect(lockRect, DensityUtils.scale(6f), DensityUtils.scale(6f), lockOverlayPaint)
            
            // Lock icon sized to fit the overlay
            val smallLockPaint = Paint().apply {
                color = "#FFFFFF".toColorInt()
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            // Set text size dynamically to ensure proper density scaling
            ensureTextSizesSet()
            smallLockPaint.textSize = DensityUtils.UI.getTextSmall()
            canvas.drawText("🔒", spriteX, spriteY + DensityUtils.dp(6f), smallLockPaint)
        }
        
        // Text center position (padding is implicit in card design)
        val textCenter = x + width/2f

        // Hat name (with more generous spacing)
        canvas.drawText(hatInfo.type.displayName, textCenter, y + height * 0.46f, hatNamePaint)

        // Rarity (with increased spacing from hat name)
        rarityTextPaint.color = hatInfo.type.rarity.color.toColorInt()
        canvas.drawText(hatInfo.type.rarity.displayName, textCenter, y + height * 0.58f, rarityTextPaint)

        // Achievement requirement (with more spacing)
        val achievementText = when {
            hatInfo.isUnlocked -> "Unlocked!"
            hatInfo.type.price.startsWith("Score:") -> hatInfo.type.price
            else -> "Unlock: ${hatInfo.type.price}"
        }
        canvas.drawText(achievementText, textCenter, y + height * 0.67f, hatDescPaint)
        
        // Action button (with increased padding and proper spacing)
        val buttonPadding = width * 0.12f  // 12% padding from sides for more breathing room
        val buttonWidth = width - (buttonPadding * 2)  // Full width minus padding
        val buttonHeight = height * 0.18f  // Slightly taller height for better proportion
        val buttonX = x + buttonPadding
        val buttonY = y + height * 0.75f  // Start at 75% down, fitting better with new layout
        val buttonRect = RectF(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight)
        
        when {
            hatInfo.isSelected -> {
                // Selected - show checkmark
                canvas.drawRoundRect(buttonRect, DensityUtils.scale(15f), DensityUtils.scale(15f), selectedCardPaint)
                canvas.drawText("✓ EQUIPPED", buttonX + buttonWidth/2f, buttonY + buttonHeight * 0.65f, equippedTextPaint)
            }
            hatInfo.isUnlocked -> {
                // Unlocked - show select button
                canvas.drawRoundRect(buttonRect, DensityUtils.scale(15f), DensityUtils.scale(15f), selectButtonPaint)
                canvas.drawText("SELECT", buttonX + buttonWidth/2f, buttonY + buttonHeight * 0.65f, buttonTextPaint)
            }
            else -> {
                // Locked - show achievement requirement
                canvas.drawRoundRect(buttonRect, DensityUtils.scale(15f), DensityUtils.scale(15f), buyButtonPaint)
                val buttonText = if (hatInfo.type.price.startsWith("Score:")) "LOCKED" else hatInfo.type.price
                canvas.drawText(buttonText, buttonX + buttonWidth/2f, buttonY + buttonHeight * 0.65f, buttonTextPaint)
            }
        }
    }
    
    /**
     * Draw actual hat sprite or placeholder with hat-specific designs
     */
    private fun drawHatSpritePlaceholder(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        @Suppress("SameParameterValue") size: Float,
        hatType: HatType
    ) {
        val radius = size / 2f
        
        // Get hat-specific colors and effects
        val (bodyColor, accentColor, hasSpecialEffect) = getHatVisualData(hatType)
        
        // Bird body with hat-specific styling
        bodyPaint.color = bodyColor
        // Add gradient effect for premium hats
        if (hasSpecialEffect) {
            bodyPaint.setShadowLayer(8f, 0f, 0f, accentColor)
        } else {
            bodyPaint.clearShadowLayer()
        }
        canvas.drawCircle(centerX, centerY, radius * 0.8f, bodyPaint)
        
        // Bird outline
        canvas.drawCircle(centerX, centerY, radius * 0.8f, outlinePaint)
        
        // Simple wing
        val wingPath = Path().apply {
            moveTo(centerX - radius * 0.3f, centerY)
            lineTo(centerX - radius * 0.8f, centerY - radius * 0.3f)
            lineTo(centerX - radius * 0.6f, centerY + radius * 0.2f)
            close()
        }
        canvas.drawPath(wingPath, bodyPaint)
        canvas.drawPath(wingPath, outlinePaint)
        
        // Eye
        canvas.drawCircle(centerX + radius * 0.2f, centerY - radius * 0.2f, radius * 0.15f, eyePaint)
        
        // Pupil
        canvas.drawCircle(centerX + radius * 0.25f, centerY - radius * 0.2f, radius * 0.08f, pupilPaint)
        
        // Beak
        val beakPath = Path().apply {
            moveTo(centerX + radius * 0.7f, centerY)
            lineTo(centerX + radius * 1.1f, centerY - radius * 0.1f)
            lineTo(centerX + radius * 0.7f, centerY + radius * 0.1f)
            close()
        }
        canvas.drawPath(beakPath, beakPaint)
    }
    
    /**
     * Draw actual hat sprite from bitmap (no background)
     */
    private fun drawActualHatSprite(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        size: Float,
        sprite: Bitmap,
        isLocked: Boolean = false,
        hatType: HatType? = null
    ) {
        // Draw only the hat sprite, centered and properly sized
        val hatSize = if (hatType == HatType.PARTY_HAT) {
            size * 0.75f // Reduce party hat size by 25%
        } else {
            size // Use full size for other hats
        }
        val hatLeft = centerX - hatSize / 2f
        val hatTop = centerY - hatSize / 2f
        val hatRect = RectF(hatLeft, hatTop, hatLeft + hatSize, hatTop + hatSize)
        
        if (isLocked) {
            val paint = Paint().apply {
                alpha = 128 // Make locked hat translucent
                isAntiAlias = true
            }
            canvas.drawBitmap(sprite, null, hatRect, paint)
        } else {
            canvas.drawBitmap(sprite, null, hatRect, null)
        }
    }
    
    /**
     * Draw close button
     */
    private fun drawCloseButton(canvas: Canvas, x: Float, y: Float) {
        val buttonSize = DensityUtils.dp(50f)  // Smaller responsive close button
        val buttonRect = RectF(x, y, x + buttonSize, y + buttonSize)
        canvas.drawRoundRect(buttonRect, buttonSize/2f, buttonSize/2f, closeButtonPaint)

        canvas.drawText("✕", x + buttonSize/2f, y + buttonSize/2f + DensityUtils.dp(8f), closeButtonTextPaint)
    }
    
    /**
     * Get close button area for touch detection
     */
    @Suppress("unused")
    fun getCloseButtonArea(): RectF {
        // Use the same panel dimensions as in drawHatSelectionScreen
        val panelWidth = screenWidth * 0.95f
        val panelHeight = screenHeight * 0.90f
        val panelX = (screenWidth - panelWidth) / 2f
        val panelY = (screenHeight - panelHeight) / 2f
        
        return RectF(
            panelX + panelWidth - DensityUtils.dp(60f),  // Updated for smaller responsive button
            panelY + DensityUtils.dp(10f),
            panelX + panelWidth - DensityUtils.dp(10f),
            panelY + DensityUtils.dp(60f)                // Updated for smaller responsive button
        )
    }
    
    /**
     * Get hat card areas for touch detection
     */
    @Suppress("unused")
    fun getHatCardAreas(hatCount: Int): List<RectF> {
        val panelWidth = screenWidth * 0.95f
        val panelX = (screenWidth - panelWidth) / 2f
        val contentWidth = panelWidth - DensityUtils.dp(80f)  // Match drawing layout
        val contentX = panelX + DensityUtils.dp(40f)  // Match drawing layout
        val contentStartY = (screenHeight - screenHeight * 0.90f) / 2f + DensityUtils.dp(120f)  // Match updated layout
        
        val itemsPerRow = 2
        val cardWidth = (contentWidth - DensityUtils.dp(10f)) / itemsPerRow // Match drawing layout spacing - increased card width
        val cardHeight = getHatCardHeight()  // Match responsive card height
        val spacing = DensityUtils.dp(20f)      // Match drawing layout spacing
        
        val cardAreas = mutableListOf<RectF>()
        var currentX = contentX
        var currentY = contentStartY
        var itemsInCurrentRow = 0
        
        (0 until hatCount).forEach { _ ->
            val left: Float = currentX
            val top: Float = currentY
            val right: Float = currentX + cardWidth
            val bottom: Float = currentY + cardHeight
            cardAreas.add(RectF(left, top, right, bottom))
            
            itemsInCurrentRow++
            
            if (itemsInCurrentRow >= itemsPerRow) {
                currentX = contentX
                currentY += cardHeight + spacing
                itemsInCurrentRow = 0
            } else {
                currentX += cardWidth + spacing
            }
        }
        
        return cardAreas
    }
    
    /**
     * Get hat-specific visual data
     */
    private fun getHatVisualData(hatType: HatType): Triple<Int, Int, Boolean> {
        return when (hatType) {
            HatType.NONE -> Triple(
                "#87CEEB".toColorInt(), // Sky blue (bird color)
                "#4682B4".toColorInt(), // Steel blue
                false
            )
            HatType.BASEBALL_CAP -> Triple(
                "#87CEEB".toColorInt(), // Sky blue (bird color)
                "#FF0000".toColorInt(), // Red cap color
                false
            )
            HatType.CROWN -> Triple(
                "#87CEEB".toColorInt(), // Sky blue (bird color)
                "#FFD700".toColorInt(), // Gold crown
                true
            )
            HatType.WIZARD_HAT -> Triple(
                "#87CEEB".toColorInt(), // Sky blue (bird color)
                "#9400D3".toColorInt(), // Violet hat
                true
            )
            HatType.VIKING_HELMET -> Triple(
                "#87CEEB".toColorInt(), // Sky blue (bird color)
                "#4682B4".toColorInt(), // Steel blue helmet
                true
            )
            HatType.ROYAL_CROWN -> Triple(
                "#87CEEB".toColorInt(), // Sky blue (bird color)
                "#FFD700".toColorInt(), // Gold royal crown
                true
            )
            HatType.PARTY_HAT -> Triple(
                "#87CEEB".toColorInt(), // Sky blue (bird color)
                "#FF69B4".toColorInt(), // Pink party hat
                true
            )
            HatType.PIRATE_HAT -> Triple(
                "#87CEEB".toColorInt(), // Sky blue (bird color)
                "#2C1810".toColorInt(), // Dark brown pirate hat
                false
            )
        }
    }
    
    /**
     * Draw special effects for premium hats
     */
    private fun drawHatSpecialEffects(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        @Suppress("SameParameterValue") size: Float,
        hatType: HatType
    ) {
        val radius = size / 2f
        
        when (hatType) {
            HatType.CROWN, HatType.ROYAL_CROWN -> {
                // Golden sparkles for royal hats
                // Draw small sparkles around the hat area
                (0..5).forEach { i ->
                    val angle = (i * 60f) * (PI / 180f)
                    val sparkleX = centerX + (radius * 0.8f * cos(angle)).toFloat()
                    val sparkleY = centerY - radius * 0.5f + (radius * 0.3f * sin(angle)).toFloat()
                    canvas.drawCircle(sparkleX, sparkleY, 1.5f, sparklePaint)
                }
            }
            HatType.WIZARD_HAT -> {
                // Magical sparkles
                // Draw magical sparkles around wizard hat
                (0..3).forEach { i ->
                    val angle = (i * 90f + 45f) * (PI / 180f) // Offset by 45 degrees
                    val sparkleX = centerX + (radius * 0.7f * cos(angle)).toFloat()
                    val sparkleY = centerY - radius * 0.6f + (radius * 0.4f * sin(angle)).toFloat()
                    canvas.drawCircle(sparkleX, sparkleY, 2f, magicPaint)
                }
            }
            HatType.PARTY_HAT -> {
                // Digital grid effect
                // Draw grid lines
                val gridSize = size / 6f
                (-2..2).forEach { i ->
                    canvas.drawLine(
                        centerX + i * gridSize, centerY - radius,
                        centerX + i * gridSize, centerY + radius,
                        gridPaint
                    )
                    canvas.drawLine(
                        centerX - radius, centerY + i * gridSize,
                        centerX + radius, centerY + i * gridSize,
                        gridPaint
                    )
                }
            }
            HatType.VIKING_HELMET -> {
                // Nordic rune effects
                // Draw simple runic symbols around helmet
                canvas.drawLine(centerX - radius * 0.3f, centerY - radius * 0.2f, 
                               centerX + radius * 0.3f, centerY + radius * 0.2f, runePaint)
                canvas.drawLine(centerX, centerY - radius * 0.4f, 
                               centerX, centerY + radius * 0.1f, runePaint)
            }
            else -> {
                // No special effects for basic hats
            }
        }
    }
    
    /**
     * Get required scores map for unlock messages
     */
    fun getRequiredScoresMap(): Map<HatType, Int> {
        return mapOf(
            HatType.BASEBALL_CAP to 1000,
            HatType.CROWN to 2500,
            HatType.WIZARD_HAT to 5000,
            HatType.VIKING_HELMET to 10000,
            HatType.ROYAL_CROWN to 25000,
            HatType.PARTY_HAT to 15000,
            HatType.PIRATE_HAT to 7500
        )
    }
    
    /**
     * Draw unlock message popup
     */
    fun drawUnlockMessage(canvas: Canvas, hatType: HatType, requiredScore: Int, currentScore: Int) {
        // Ensure text sizes are properly set
        ensureTextSizesSet()

        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f
        
        // Semi-transparent background
        val backgroundPaint = Paint().apply {
            color = "#80000000".toColorInt() // Semi-transparent black
            isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), backgroundPaint)
        
        // Message panel (much larger and better proportioned)
        val panelWidth = screenWidth * 0.9f
        val panelHeight = screenHeight * 0.4f  // Responsive to screen height
        val panelX = centerX - panelWidth / 2f
        val panelY = centerY - panelHeight / 2f
        val panelRect = RectF(panelX, panelY, panelX + panelWidth, panelY + panelHeight)
        
        val panelPaint = Paint().apply {
            color = "#F5F5F5".toColorInt() // Light gray panel
            isAntiAlias = true
        }
        canvas.drawRoundRect(panelRect, DensityUtils.scale(20f), DensityUtils.scale(20f), panelPaint)
        
        // Panel border
        val borderPaint = Paint().apply {
            color = "#2C3E50".toColorInt() // Dark border
            style = Paint.Style.STROKE
            strokeWidth = DensityUtils.UI.getStrokeWidth()
            isAntiAlias = true
        }
        canvas.drawRoundRect(panelRect, DensityUtils.scale(20f), DensityUtils.scale(20f), borderPaint)
        
        // Title text
        val unlockTitlePaint = Paint().apply {
            color = "#2C3E50".toColorInt()
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        // Set text size dynamically to ensure proper density scaling
        ensureTextSizesSet()
        unlockTitlePaint.textSize = DensityUtils.UI.getTextTitle()
        // Title with proper spacing from top
        val titleY = panelY + panelHeight * 0.2f
        canvas.drawText("${hatType.displayName} - LOCKED", centerX, titleY, unlockTitlePaint)
        
        // Requirement text with generous spacing
        val unlockRequirementPaint = Paint().apply {
            color = "#7F8C8D".toColorInt() // Gray text
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        // Set text size dynamically to ensure proper density scaling
        unlockRequirementPaint.textSize = DensityUtils.UI.getTextMedium()
        val pointsNeeded = requiredScore - currentScore
        val requirementText = if (pointsNeeded > 0) {
            "Need $pointsNeeded more points to unlock"
        } else {
            "Requirements met! Achievement will unlock after next game."
        }
        val requirementY = panelY + panelHeight * 0.4f
        canvas.drawText(requirementText, centerX, requirementY, unlockRequirementPaint)

        // Score progress text with proper spacing
        val progressText = "Current: $currentScore / Required: $requiredScore"
        val progressY = panelY + panelHeight * 0.6f
        canvas.drawText(progressText, centerX, progressY, unlockRequirementPaint)
        
        // Tap to close hint at bottom
        val unlockHintPaint = Paint().apply {
            color = "#BDC3C7".toColorInt() // Light gray
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        // Set text size dynamically to ensure proper density scaling
        unlockHintPaint.textSize = DensityUtils.UI.getTextSmall()
        val hintY = panelY + panelHeight * 0.85f
        canvas.drawText("Tap anywhere to close", centerX, hintY, unlockHintPaint)
    }
}