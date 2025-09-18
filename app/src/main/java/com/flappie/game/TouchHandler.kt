package com.flappie.game

import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface

/**
 * TouchHandler - Centralized touch input management
 * Handles all touch events and converts them to game actions
 */
class TouchHandler(private val screenWidth: Int, private val screenHeight: Int) {
    
    // UI button areas
    private val pauseButtonArea = RectF()
    private val settingsButtonArea = RectF()
    private val shopButtonArea = RectF()
    private val exitButtonArea = RectF()
    private val homeButtonArea = RectF()
    
    init {
        setupUIButtons()
    }
    
    /**
     * Set up UI button areas based on screen dimensions
     */
    private fun setupUIButtons() {
        val buttonSize = screenWidth * GameConstants.BUTTON_SIZE_RATIO
        val buttonSpacing = screenWidth * GameConstants.BUTTON_SPACING_RATIO
        val marginX = GameConstants.BUTTON_MARGIN_X
        val marginY = GameConstants.BUTTON_MARGIN_Y
        
        // Pause button (bottom left)
        pauseButtonArea.set(
            marginX,
            screenHeight - marginY - buttonSize,
            marginX + buttonSize,
            screenHeight - marginY
        )
        
        // Settings button (bottom left, next to pause)
        settingsButtonArea.set(
            marginX + buttonSpacing,
            screenHeight - marginY - buttonSize,
            marginX + buttonSpacing + buttonSize,
            screenHeight - marginY
        )
        
        // Shop button (bottom left, next to settings)
        shopButtonArea.set(
            marginX + (buttonSpacing * 2),
            screenHeight - marginY - buttonSize,
            marginX + (buttonSpacing * 2) + buttonSize,
            screenHeight - marginY
        )
        
        // Exit button (bottom left, same position as pause button)
        exitButtonArea.set(
            marginX,
            screenHeight - marginY - buttonSize,
            marginX + buttonSize,
            screenHeight - marginY
        )
        
        // Home button (bottom left, next to shop button)
        homeButtonArea.set(
            marginX + (buttonSpacing * 3),
            screenHeight - marginY - buttonSize,
            marginX + (buttonSpacing * 3) + buttonSize,
            screenHeight - marginY
        )
    }
    
    /**
     * Handle touch input and return appropriate command
     * @param x Touch X coordinate
     * @param y Touch Y coordinate
     * @param gameState Current game state
     * @param showPauseMenu Whether pause menu is shown
     * @param showSettings Whether settings menu is shown
     * @param showShop Whether shop menu is shown
     * @return TouchCommand representing the action to take
     */
    fun handleTouch(
        x: Float,
        y: Float,
        gameState: GameState,
        showPauseMenu: Boolean = false,
        showSettings: Boolean = false,
        showShop: Boolean = false,
        showAchievements: Boolean = false,
        showStatistics: Boolean = false,
        showBirdSkins: Boolean = false,
        showTutorial: Boolean = false,
        currentLives: Int = 0,
        gamePaused: Boolean = false
    ): TouchCommand? {
        android.util.Log.e("FLAPPIE_DEBUG", "🎮 HANDLE TOUCH: showStatistics=$showStatistics, showPause=$showPauseMenu, showSettings=$showSettings")
        android.util.Log.d("TouchHandler", "handleTouch called: showStatistics=$showStatistics, x=$x, y=$y")

        // Check UI button touches first (accessible based on game state)
        if (!showPauseMenu && !showSettings && !showShop && !showAchievements && !showStatistics && !showBirdSkins && !showTutorial) {
            when {
                // Pause button only available during gameplay
                gameState == GameState.PLAYING && CollisionUtils.isPointInRectangle(x, y, pauseButtonArea) -> 
                    return TouchCommand.TogglePause
                // Settings and shop buttons available in all states
                CollisionUtils.isPointInRectangle(x, y, settingsButtonArea) -> 
                    return TouchCommand.ShowSettings
                CollisionUtils.isPointInRectangle(x, y, shopButtonArea) -> 
                    return TouchCommand.ShowBirdSkins
                // Exit button available on menu and game over screens
                (gameState == GameState.MENU || gameState == GameState.GAME_OVER) && 
                    CollisionUtils.isPointInRectangle(x, y, exitButtonArea) -> 
                    return TouchCommand.ExitGame
                // Home button only available on game over screen
                gameState == GameState.GAME_OVER && 
                    CollisionUtils.isPointInRectangle(x, y, homeButtonArea) -> 
                    return TouchCommand.GoToMenu
            }
        }
        
        // Handle menu interactions
        val result = when {
            showPauseMenu -> handlePauseMenuTouch(x, y)
            showSettings -> handleSettingsTouch(x, y)
            showShop -> handleShopTouch(x, y)
            showAchievements -> handleAchievementsTouch(x, y)
            showStatistics -> {
                android.util.Log.d("TouchHandler", "Routing to handleStatisticsTouch")
                val command = handleStatisticsTouch(x, y)
                android.util.Log.d("TouchHandler", "Statistics touch returned: $command")
                command
            }
            showBirdSkins -> handleBirdSkinsTouch(x, y)
            showTutorial -> handleTutorialTouch(x, y)
            else -> handleGameStateTouch(x, y, gameState, currentLives, gamePaused)
        }

        android.util.Log.d("TouchHandler", "Final handleTouch result: $result")
        return result
    }
    
    /**
     * Handle touches in pause menu
     */
    private fun handlePauseMenuTouch(x: Float, y: Float): TouchCommand? {
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f
        
        // Calculate button dimensions matching the renderer
        val testPaint = Paint().apply {
            textSize = GameConstants.getMediumTextSize(screenWidth, screenHeight)
            typeface = Typeface.DEFAULT_BOLD
        }
        val resumeTextWidth = testPaint.measureText("RESUME")
        val statisticsTextWidth = testPaint.measureText("STATISTICS")
        val textHeight = testPaint.textSize
        
        val textPadding = GameConstants.scaledSize(40f, screenWidth, screenHeight)
        val buttonWidth = maxOf(resumeTextWidth, statisticsTextWidth) + textPadding * 2
        val buttonHeight = textHeight + textPadding
        val buttonSpacing = GameConstants.scaledSize(35f, screenWidth, screenHeight)
        
        // Resume button area
        val playY = centerY - GameConstants.scaledSize(80f, screenWidth, screenHeight)
        if (x > centerX - buttonWidth/2f && x < centerX + buttonWidth/2f &&
            y > playY && y < playY + buttonHeight) {
            return TouchCommand.Resume
        }
        
        // Statistics button area
        val statsY = playY + buttonHeight + buttonSpacing
        if (x > centerX - buttonWidth/2f && x < centerX + buttonWidth/2f &&
            y > statsY && y < statsY + buttonHeight) {
            return TouchCommand.ShowStatistics
        }
        
        // Exit button area (square) - exit to main menu
        val exitTextSize = testPaint.measureText("✕")
        val exitButtonSize = maxOf(exitTextSize + textPadding, textHeight + textPadding)
        val exitY = statsY + buttonHeight + buttonSpacing
        if (x > centerX - exitButtonSize/2f && x < centerX + exitButtonSize/2f &&
            y > exitY && y < exitY + exitButtonSize) {
            return TouchCommand.GoToMenu
        }
        
        return null
    }
    
    /**
     * Handle touches in settings menu
     */
    private fun handleSettingsTouch(x: Float, y: Float): TouchCommand? {
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f
        
        // Sound toggle button area (200px wide, 70px high, centered)
        if (x > centerX - 100f && x < centerX + 100f &&
            y > centerY - 35f && y < centerY + 35f) {
            return TouchCommand.ToggleSound
        }
        
        // Music toggle button area (200px wide, 70px high, centered)
        if (x > centerX - 100f && x < centerX + 100f &&
            y > centerY + 125f && y < centerY + 195f) {
            return TouchCommand.ToggleMusic
        }
        
        // Close settings button (240px wide, 70px high, moved down)
        if (x > centerX - 120f && x < centerX + 120f &&
            y > centerY + 280f && y < centerY + 350f) {
            return TouchCommand.CloseSettings
        }
        
        return null
    }
    
    /**
     * Handle touches in shop menu
     */
    private fun handleShopTouch(x: Float, y: Float): TouchCommand? {
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f
        
        // Close button coordinates match the drawing coordinates
        val closeBtnSize = 60f
        val closeLeft = centerX + 220f
        val closeTop = centerY - 230f
        val closeRight = closeLeft + closeBtnSize
        val closeBottom = closeTop + closeBtnSize
        
        // Close shop button
        if (x >= closeLeft && x <= closeRight &&
            y >= closeTop && y <= closeBottom) {
            return TouchCommand.CloseShop
        }
        
        // Buy Life button (first item in shop)
        val itemWidth = 320f
        val itemHeight = 55f
        val itemY = centerY + 30f // Same as in OverlayRenderer
        val buyLifeLeft = centerX - itemWidth/2
        val buyLifeTop = itemY - itemHeight/2
        val buyLifeRight = centerX + itemWidth/2
        val buyLifeBottom = itemY + itemHeight/2
        
        if (x >= buyLifeLeft && x <= buyLifeRight &&
            y >= buyLifeTop && y <= buyLifeBottom) {
            return TouchCommand.BuyLife
        }
        
        return null
    }
    
    /**
     * Handle touches in achievements screen
     */
    private fun handleAchievementsTouch(x: Float, y: Float): TouchCommand? {
        // For now, just handle close button
        // Close button coordinates match AchievementRenderer
        val panelPadding = 40f
        val closeButtonSize = 60f
        val closeButtonX = screenWidth - panelPadding - closeButtonSize
        val closeButtonY = 120f
        
        if (x >= closeButtonX && x <= closeButtonX + closeButtonSize &&
            y >= closeButtonY && y <= closeButtonY + closeButtonSize) {
            return TouchCommand.CloseAchievements
        }
        
        return null
    }
    
    /**
     * Handle touches in statistics screen
     */
    private fun handleStatisticsTouch(x: Float, y: Float): TouchCommand? {
        android.util.Log.d("TouchHandler", "=== STATISTICS TOUCH DEBUG ===")
        android.util.Log.e("FLAPPIE_DEBUG", "🔥🔥🔥 STATISTICS TOUCH HANDLER 🔥🔥🔥")
        android.util.Log.d("TouchHandler", "🔥 handleStatisticsTouch called with ($x, $y)")
        android.util.Log.d("TouchHandler", "🔥 This confirms statistics touch handling is working!")

        // Close button coordinates - MUST match StatisticsRenderer exactly
        val panelWidth = screenWidth * 0.95f
        val panelHeight = screenHeight * 0.9f
        val panelX = (screenWidth - panelWidth) / 2f
        val panelY = (screenHeight - panelHeight) / 2f

        // Close button coordinates - MUST MATCH StatisticsRenderer EXACTLY
        val buttonSize = 180f // MUST MATCH StatisticsRenderer - NOW MUCH LARGER
        val closeButtonX = screenWidth / 2f - buttonSize / 2f // EXACT MATCH to StatisticsRenderer
        val closeButtonY = panelY + panelHeight - buttonSize - 120f // EXACT MATCH to StatisticsRenderer

        android.util.Log.e("FLAPPIE_DEBUG", "📐 TOUCH CALC: screen=${screenWidth}x$screenHeight, panel=${panelWidth}x$panelHeight at ($panelX,$panelY)")
        android.util.Log.e("FLAPPIE_DEBUG", "📐 TOUCH BUTTON: ($closeButtonX, $closeButtonY) size=${buttonSize}x$buttonSize")

        android.util.Log.d("TouchHandler", "🎯 TOUCH BOUNDS CHECK:")
        android.util.Log.d("TouchHandler", "   Button: ($closeButtonX, $closeButtonY) to (${closeButtonX + buttonSize}, ${closeButtonY + buttonSize})")
        android.util.Log.d("TouchHandler", "   Touch:  ($x, $y)")
        android.util.Log.d("TouchHandler", "   Size:   ${buttonSize}x$buttonSize")

        // Check if touch is anywhere in the panel first
        val touchInPanel = x >= panelX && x <= panelX + panelWidth && y >= panelY && y <= panelY + panelHeight
        android.util.Log.d("TouchHandler", "Touch in panel: $touchInPanel")

        // SIMPLE SOLUTION: Any touch in the statistics screen closes it
        android.util.Log.e("FLAPPIE_DEBUG", "✅ ANY TOUCH CLOSES STATISTICS - Simple and user-friendly!")
        return TouchCommand.CloseStatistics
    }
    
    /**
     * Handle touches in bird skins screen
     */
    private fun handleBirdSkinsTouch(x: Float, y: Float): TouchCommand? {
        // Close button coordinates match HatRenderer - updated dimensions and positioning
        val panelWidth = screenWidth * 0.95f
        val panelHeight = screenHeight * 0.90f
        val panelX = (screenWidth - panelWidth) / 2f
        val panelY = (screenHeight - panelHeight) / 2f
        
        // Updated close button coordinates to match HatRenderer.drawCloseButton()
        val closeButtonX = panelX + panelWidth - 100f
        val closeButtonY = panelY + 30f
        val closeButtonSize = 80f
        
        if (x >= closeButtonX && x <= closeButtonX + closeButtonSize &&
            y >= closeButtonY && y <= closeButtonY + closeButtonSize) {
            return TouchCommand.CloseBirdSkins
        }
        
        // For now, return null to indicate no UI button was pressed
        // Hat card touch detection should be handled separately with hat data
        return null
    }
    
    /**
     * Handle touches in tutorial screen
     */
    private fun handleTutorialTouch(@Suppress("UNUSED_PARAMETER") x: Float, @Suppress("UNUSED_PARAMETER") y: Float): TouchCommand? {
        // Tutorial takes up most of screen, any touch advances
        // Could add specific skip button area if needed
        return TouchCommand.NextTutorialStep
    }
    
    /**
     * Handle touches based on current game state
     */
    private fun handleGameStateTouch(x: Float, y: Float, gameState: GameState, currentLives: Int = 0, gamePaused: Boolean = false): TouchCommand? {
        return when (gameState) {
            GameState.MENU -> handleMenuTouch(x, y, currentLives)
            GameState.PLAYING -> if (!gamePaused) TouchCommand.Jump else null
            GameState.GAME_OVER -> handleGameOverTouch(x, y, currentLives)
        }
    }

    /**
     * Handle touches on menu screen (including buy life button)
     */
    private fun handleMenuTouch(x: Float, y: Float, currentLives: Int = 0): TouchCommand? {
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f

        // Buy Life button (matches MenuRenderer positioning)
        val buttonWidth = 380f
        val buttonHeight = 80f
        val menuBuyLifeY = centerY + 320f  // Same as in MenuRenderer
        val buttonLeft = centerX - buttonWidth/2
        val buttonTop = menuBuyLifeY - buttonHeight/2
        val buttonRight = centerX + buttonWidth/2
        val buttonBottom = menuBuyLifeY + buttonHeight/2

        // Check if touch is on Buy Life button
        if (x >= buttonLeft && x <= buttonRight &&
            y >= buttonTop && y <= buttonBottom) {
            return TouchCommand.BuyLife
        }

        // Premium Buy Life button (same size, positioned below)
        val premiumBuyLifeY = menuBuyLifeY + 100f
        val premiumButtonTop = premiumBuyLifeY - buttonHeight/2
        val premiumButtonBottom = premiumBuyLifeY + buttonHeight/2

        // Check if touch is on Premium Buy Life button (only enabled when no lives left)
        if (x >= buttonLeft && x <= buttonRight &&
            y >= premiumButtonTop && y <= premiumButtonBottom &&
            currentLives == 0) {
            return TouchCommand.BuyLifePremium
        }

        // Default menu behavior: start game
        return TouchCommand.StartGame
    }

    /**
     * Handle touches on game over screen (including buy life button and continue)
     */
    private fun handleGameOverTouch(x: Float, y: Float, currentLives: Int = 0): TouchCommand? {
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f - 100f // Same offset as in MenuRenderer

        // Buy Life button (matches MenuRenderer positioning) - always present
        val buttonWidth = 380f  // Updated to match new size
        val buttonHeight = 80f  // Updated to match new size
        // Simple logic: position button lower if player has lives (when "Tap to Continue" shows)
        val buttonY = if (currentLives > 0) {
            centerY + 370f  // Position lower when "Tap to Continue" is shown
        } else {
            centerY + 400f  // Default position - moved down
        }
        val buttonLeft = centerX - buttonWidth/2
        val buttonTop = buttonY - buttonHeight/2
        val buttonRight = centerX + buttonWidth/2
        val buttonBottom = buttonY + buttonHeight/2

        if (x >= buttonLeft && x <= buttonRight &&
            y >= buttonTop && y <= buttonBottom) {
            return TouchCommand.BuyLife
        }

        // Premium Buy Life button (positioned below regular Buy Life button)
        val premiumBuyLifeY = buttonY + 100f
        val premiumButtonTop = premiumBuyLifeY - buttonHeight/2
        val premiumButtonBottom = premiumBuyLifeY + buttonHeight/2

        if (x >= buttonLeft && x <= buttonRight &&
            y >= premiumButtonTop && y <= premiumButtonBottom &&
            currentLives == 0) {
            return TouchCommand.BuyLifePremium
        }

        // Return continue if player has lives, otherwise restart
        if (currentLives > 0) {
            return TouchCommand.ContinueGame
        }
        return TouchCommand.RestartGame
    }
    
    /**
     * Get button areas for drawing
     */
    fun getButtonAreas(): ButtonAreas {
        return ButtonAreas(pauseButtonArea, settingsButtonArea, shopButtonArea, exitButtonArea, homeButtonArea)
    }
    
    /**
     * Check if touch coordinates are within a hat card and return appropriate command
     */
    fun checkHatCardTouch(x: Float, y: Float, hatInfoList: List<HatInfo>, requiredScores: Map<HatType, Int>): TouchCommand? {
        val panelWidth = screenWidth * 0.95f
        val panelX = (screenWidth - panelWidth) / 2f
        val contentWidth = panelWidth - 80f
        val contentX = panelX + 40f
        val contentStartY = (screenHeight - screenHeight * 0.90f) / 2f + 180f
        
        val itemsPerRow = 2
        val cardWidth = (contentWidth - 40f) / itemsPerRow
        val cardHeight = GameConstants.scaledSize(360f, screenWidth, screenHeight) // Match HatRenderer responsive card height
        val spacing = 40f // Increased spacing to prevent touch overlap
        
        var currentX = contentX
        var currentY = contentStartY
        var itemsInCurrentRow = 0
        
        hatInfoList.forEachIndexed { _, hatInfo ->
            val cardRect = RectF(currentX, currentY, currentX + cardWidth, currentY + cardHeight)
            
            if (x >= cardRect.left && x <= cardRect.right && y >= cardRect.top && y <= cardRect.bottom) {
                // Touch is within this hat card
                if (hatInfo.isUnlocked) {
                    if (hatInfo.isSelected) {
                        // Already selected, no action needed
                        return null
                    }
                    // Select this hat
                    return TouchCommand.SelectHat(hatInfo.type)
                }
                // Show unlock requirements for locked hat
                val requiredScore = requiredScores[hatInfo.type] ?: 0
                return TouchCommand.ShowUnlockMessage(hatInfo.type, requiredScore)
            }
            
            // Update position for next card
            itemsInCurrentRow++
            if (itemsInCurrentRow >= itemsPerRow) {
                currentX = contentX
                currentY += cardHeight + spacing
                itemsInCurrentRow = 0
            } else {
                currentX += cardWidth + spacing
            }
        }
        
        return null
    }
}

/**
 * TouchCommand - Represents different actions that can be triggered by touch
 */
sealed class TouchCommand {
    // Game actions
    object Jump : TouchCommand()
    object StartGame : TouchCommand()
    object RestartGame : TouchCommand()
    object ContinueGame : TouchCommand()
    
    // UI actions
    object TogglePause : TouchCommand()
    object Resume : TouchCommand()
    object ExitGame : TouchCommand()
    object GoToMenu : TouchCommand()
    object ShowSettings : TouchCommand()
    object CloseSettings : TouchCommand()
    object ShowShop : TouchCommand()
    object CloseShop : TouchCommand()
    object BuyLife : TouchCommand()
    object BuyLifePremium : TouchCommand()
    object ShowAchievements : TouchCommand()
    object CloseAchievements : TouchCommand()
    object ShowStatistics : TouchCommand()
    object CloseStatistics : TouchCommand()
    object ShowBirdSkins : TouchCommand()
    object CloseBirdSkins : TouchCommand()
    data class SelectHat(val hatType: HatType) : TouchCommand()
    data class UnlockHat(val hatType: HatType) : TouchCommand()
    data class ShowUnlockMessage(val hatType: HatType, val requiredScore: Int) : TouchCommand()
    object NextTutorialStep : TouchCommand()
    object SkipTutorial : TouchCommand()
    
    // Settings actions
    object ToggleSound : TouchCommand()
    object ToggleMusic : TouchCommand()
    
    // Shop actions (for future expansion)
    data class PurchaseItem(val itemId: String) : TouchCommand()
}

/**
 * ButtonAreas - Container for UI button rectangles
 */
data class ButtonAreas(
    val pauseButton: RectF,
    val settingsButton: RectF,
    val shopButton: RectF,
    val exitButton: RectF,
    val homeButton: RectF
)