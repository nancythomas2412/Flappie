package com.flappie.game

import android.graphics.*
import androidx.core.graphics.toColorInt

/**
 * MenuRenderer - Handles main menu and game over screen rendering
 */
class MenuRenderer(
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val textPaint: Paint,
    private val smallTextPaint: Paint
) {
    
    private val logoRenderer = LogoRenderer(screenWidth, screenHeight)
    
    /**
     * Draw main menu screen
     */
    fun draw(canvas: Canvas, bestScore: Int, bird: Bird, canPlay: Boolean = true, heartRefillTime: String? = null, totalCoins: Int = 0, currentLives: Int = 3, heartSprites: HeartSprites? = null) {
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f
        
        // Professional animated logo
        logoRenderer.drawLogo(canvas, centerX, screenHeight / 3.5f)

        // Draw hearts in top left corner (same position as in gameplay)
        drawMenuHearts(canvas, currentLives, GameConstants.MAX_LIVES, heartSprites)

        // Instructions or heart refill message
        if (canPlay) {
            canvas.drawText("Tap to Play", centerX, centerY, smallTextPaint)
        } else {
            // Create heart refill timer paint
            val heartRefillPaint = Paint().apply {
                color = Color.BLACK
                textSize = 48f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            
            canvas.drawText("💔 No Hearts Left", centerX, centerY - 30f, heartRefillPaint)
            
            if (heartRefillTime != null) {
                val timerPaint = Paint().apply {
                    color = "#FFD700".toColorInt() // Yellow color for better visibility
                    textSize = 44f // Increased size for better visibility
                    typeface = Typeface.DEFAULT_BOLD
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("Next heart in: $heartRefillTime", centerX, centerY + 20f, timerPaint)
            }
        }
        
        // Best score
        canvas.drawText("Best: $bestScore", centerX, centerY + 100f, smallTextPaint)

        // Draw bird at center
        val birdX = centerX
        val birdY = centerY + 200f
        bird.x = birdX
        bird.y = birdY
        bird.draw(canvas)

        // Buy Life button on menu screen (positioned below bird)
        val menuBuyLifeY = centerY + 320f
        drawBuyLifeButton(canvas, centerX, menuBuyLifeY, totalCoins)

        // Premium Buy Life button below the coin button
        val premiumBuyLifeY = menuBuyLifeY + 100f
        drawPremiumBuyLifeButton(canvas, centerX, premiumBuyLifeY, currentLives == 0)
    }

    /**
     * Draw hearts in menu screen (same position as in gameplay)
     */
    private fun drawMenuHearts(canvas: Canvas, currentLives: Int, maxLives: Int, heartSprites: HeartSprites?) {
        val heartSize = screenWidth * GameConstants.HEART_SIZE_RATIO
        val heartSpacing = screenWidth * GameConstants.HEART_SPACING_RATIO
        val heartX = GameConstants.HEART_MARGIN_X + heartSize / 2f
        val heartY = GameConstants.HEART_MARGIN_Y + heartSize / 2f

        for (i in 0 until maxLives) {
            val x = heartX + i * heartSpacing

            if (heartSprites != null) {
                val sprite = if (i < currentLives) heartSprites.full else heartSprites.empty
                if (sprite != null) {
                    val rect = RectF(
                        x - heartSize/2, heartY - heartSize/2,
                        x + heartSize/2, heartY + heartSize/2
                    )
                    canvas.drawBitmap(sprite, null, rect, null)
                } else {
                    // Fallback to colored circles
                    drawHeartFallback(canvas, x, heartY, i < currentLives, heartSize)
                }
            } else {
                // Fallback to colored circles
                drawHeartFallback(canvas, x, heartY, i < currentLives, heartSize)
            }
        }
    }

    /**
     * Draw game over screen
     */
    fun drawGameOver(
        canvas: Canvas, 
        score: Int, 
        totalCoins: Int, 
        bestScore: Int, 
        sadBirdSprite: Bitmap?,
        bird: Bird,
        canPlay: Boolean = true,
        heartRefillTime: String? = null,
        currentLives: Int = 0,
        maxLives: Int = 3,
        heartSprites: HeartSprites? = null
    ) {
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f - 100f
        val birdY = screenHeight / 2f - 250f  // Move sad bird higher

        // Draw sad bird or regular bird
        if (sadBirdSprite != null) {
            val birdSize = GameConstants.BIRD_RADIUS * 1.815f // Increased by 10% more (1.65 * 1.1 = 1.815)
            val birdRect = RectF(
                centerX - birdSize, birdY - birdSize,
                centerX + birdSize, birdY + birdSize
            )
            canvas.drawBitmap(sadBirdSprite, null, birdRect, null)
        } else {
            // Fallback: draw regular bird at higher position
            bird.x = centerX
            bird.y = birdY
            bird.draw(canvas)
        }
        
        // Game over text
        canvas.drawText("GAME OVER", centerX, screenHeight / 3f, textPaint)
        
        // Scores and stats
        canvas.drawText("Score: $score", centerX, centerY + 50f, smallTextPaint)
        canvas.drawText("Coins: $totalCoins", centerX, centerY + 110f, smallTextPaint)
        canvas.drawText("Best: $bestScore", centerX, centerY + 170f, smallTextPaint)
        
        // Draw hearts display
        drawHeartsDisplay(canvas, currentLives, maxLives, heartSprites, centerX, centerY + 200f)
        
        // Game actions based on available lives
        if (canPlay) {
            if (currentLives > 0) {
                // Player has refilled hearts - show continue option
                val continuePaint = Paint().apply {
                    color = "#4CAF50".toColorInt()  // Green
                    textSize = 44f
                    typeface = Typeface.DEFAULT_BOLD
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("Tap to Continue", centerX, centerY + 270f, continuePaint)
            } else {
                canvas.drawText("Tap to Restart", centerX, centerY + 270f, smallTextPaint)
            }
        } else {
            // Heart refill timer in middle position
            if (heartRefillTime != null) {
                val timerPaint = Paint().apply {
                    color = "#FFD700".toColorInt()
                    textSize = 36f
                    typeface = Typeface.DEFAULT_BOLD
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("Next heart in: $heartRefillTime", centerX, centerY + 270f, timerPaint)
            }
        }

        // Always show Buy Life button regardless of canPlay status
        val buyLifeY = if (canPlay && currentLives > 0) {
            centerY + 370f  // Position lower when "Tap to Continue" is shown
        } else {
            centerY + 400f  // Default position - moved down
        }
        drawBuyLifeButton(canvas, centerX, buyLifeY, totalCoins)

        // Premium Buy Life button below the coin button
        val premiumBuyLifeY = buyLifeY + 100f
        drawPremiumBuyLifeButton(canvas, centerX, premiumBuyLifeY, currentLives == 0)
    }
    
    /**
     * Draw hearts display on game over screen
     */
    private fun drawHeartsDisplay(canvas: Canvas, currentLives: Int, maxLives: Int, heartSprites: HeartSprites?, centerX: Float, y: Float) {
        val heartSize = 60f
        val heartSpacing = 80f
        val totalWidth = (maxLives - 1) * heartSpacing
        val startX = centerX - totalWidth / 2f
        
        for (i in 0 until maxLives) {
            val heartX = startX + i * heartSpacing
            
            if (heartSprites != null) {
                val sprite = if (i < currentLives) heartSprites.full else heartSprites.empty
                if (sprite != null) {
                    val rect = android.graphics.RectF(
                        heartX - heartSize/2, y - heartSize/2,
                        heartX + heartSize/2, y + heartSize/2
                    )
                    canvas.drawBitmap(sprite, null, rect, null)
                } else {
                    // Fallback to colored circles
                    drawHeartFallback(canvas, heartX, y, i < currentLives, heartSize)
                }
            } else {
                // Fallback to colored circles
                drawHeartFallback(canvas, heartX, y, i < currentLives, heartSize)
            }
        }
    }
    
    /**
     * Fallback heart drawing
     */
    private fun drawHeartFallback(canvas: Canvas, x: Float, y: Float, filled: Boolean, size: Float) {
        val paint = Paint().apply {
            style = Paint.Style.FILL
            color = if (filled) Color.RED else Color.GRAY
            isAntiAlias = true
        }
        canvas.drawCircle(x, y, size/2, paint)
        
        // Draw heart symbol
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = size * 0.6f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("♥", x, y + size * 0.2f, textPaint)
    }

    /**
     * Draw clean and attractive buy life button
     */
    private fun drawBuyLifeButton(canvas: Canvas, centerX: Float, y: Float, totalCoins: Int) {
        val buttonWidth = 380f  // Increased from 340f
        val buttonHeight = 80f  // Increased from 70f  
        val canAfford = totalCoins >= 500
        
        // Button background
        val buttonRect = RectF(
            centerX - buttonWidth/2, y - buttonHeight/2,
            centerX + buttonWidth/2, y + buttonHeight/2
        )
        
        val buttonPaint = Paint().apply {
            color = if (canAfford) Color.parseColor("#FF4444") else Color.parseColor("#CCCCCC") // Red for affordable, gray for not
            style = Paint.Style.FILL
            setShadowLayer(8f, 2f, 4f, Color.parseColor("#40000000"))
            isAntiAlias = true
        }
        canvas.drawRoundRect(buttonRect, 35f, 35f, buttonPaint)
        
        // Button border
        val borderPaint = Paint().apply {
            color = if (canAfford) Color.parseColor("#CC0000") else Color.parseColor("#999999")
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        canvas.drawRoundRect(buttonRect, 35f, 35f, borderPaint)
        
        if (canAfford) {
            // Heart icon on left
            val heartPaint = Paint().apply {
                textSize = 40f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("❤️", centerX - 80f, y + 10f, heartPaint)
            
            // Main text
            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = 28f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
                setShadowLayer(2f, 1f, 1f, Color.parseColor("#80000000"))
            }
            canvas.drawText("Buy Life", centerX - 40f, y - 5f, textPaint)
            
            // Price
            val pricePaint = Paint().apply {
                color = Color.parseColor("#FFE0E0")
                textSize = 24f
                typeface = Typeface.DEFAULT
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
                setShadowLayer(1f, 1f, 1f, Color.parseColor("#80000000"))
            }
            canvas.drawText("500 coins", centerX - 40f, y + 18f, pricePaint)
            
        } else {
            // Grayed out version
            val grayHeartPaint = Paint().apply {
                textSize = 40f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
                alpha = 128
            }
            canvas.drawText("💔", centerX - 80f, y + 10f, grayHeartPaint)
            
            val grayTextPaint = Paint().apply {
                color = Color.parseColor("#666666")
                textSize = 28f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
            }
            canvas.drawText("Buy Life", centerX - 40f, y - 5f, grayTextPaint)
            
            val needMorePaint = Paint().apply {
                color = Color.parseColor("#999999")
                textSize = 22f
                typeface = Typeface.DEFAULT
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
            }
            canvas.drawText("Need 500 coins", centerX - 40f, y + 18f, needMorePaint)
        }
    }

    /**
     * Draw premium buy life button for $5 real money
     */
    private fun drawPremiumBuyLifeButton(canvas: Canvas, centerX: Float, y: Float, enabled: Boolean = true) {
        val buttonWidth = 380f
        val buttonHeight = 80f

        // Green button background
        val buttonRect = RectF(
            centerX - buttonWidth/2, y - buttonHeight/2,
            centerX + buttonWidth/2, y + buttonHeight/2
        )

        val buttonPaint = Paint().apply {
            color = if (enabled) Color.parseColor("#4CAF50") else Color.parseColor("#CCCCCC") // Green or gray
            style = Paint.Style.FILL
            setShadowLayer(if (enabled) 8f else 4f, 2f, 4f, Color.parseColor("#40000000"))
            isAntiAlias = true
        }
        canvas.drawRoundRect(buttonRect, 35f, 35f, buttonPaint)

        // Button border
        val borderPaint = Paint().apply {
            color = if (enabled) Color.parseColor("#388E3C") else Color.parseColor("#999999") // Darker green or gray
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        canvas.drawRoundRect(buttonRect, 35f, 35f, borderPaint)

        // Heart icon on left
        val heartPaint = Paint().apply {
            textSize = 40f
            color = if (enabled) Color.WHITE else Color.parseColor("#666666")
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("❤️", centerX - 80f, y + 10f, heartPaint)

        // Main text
        val textPaint = Paint().apply {
            color = if (enabled) Color.WHITE else Color.parseColor("#666666")
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
            setShadowLayer(if (enabled) 2f else 0f, 1f, 1f, Color.parseColor("#80000000"))
        }
        canvas.drawText("Buy Life", centerX - 40f, y - 5f, textPaint)

        // Price
        val pricePaint = Paint().apply {
            color = if (enabled) Color.WHITE else Color.parseColor("#666666")
            textSize = 24f
            typeface = Typeface.DEFAULT
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
            setShadowLayer(if (enabled) 1f else 0f, 1f, 1f, Color.parseColor("#80000000"))
        }
        canvas.drawText("$5.00", centerX - 40f, y + 18f, pricePaint)
    }
}