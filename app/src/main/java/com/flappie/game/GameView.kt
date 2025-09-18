package com.flappie.game

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import androidx.core.graphics.toColorInt
import java.util.ArrayList
import kotlin.math.sin
import kotlin.random.Random

/**
 * GameView - Clean, modular game view using modern component architecture
 * Features separated concerns, modern APIs, and professional structure
 */
class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, GameUpdatable {
    
    // Core components
    private val resourceManager = ResourceManager(context)
    private val gameConfigManager = GameConfigManager(context)
    private val soundManager = SoundManager(context)
    private val animationManager = AnimationManager()
    private val achievementManager = AchievementManager(context)
    // Core engagement and compliance systems
    private val tutorial = Tutorial(context)
    private val appRatingManager = AppRatingManager(context)
    private val privacyManager = PrivacyManager(context)
    private val dailyBonusManager = DailyBonusManager(context)
    private val statisticsManager = StatisticsManager(context)
    private val hatManager = HatManager(context)
    
    // Billing manager (requires Google Play Billing Library dependency)
    private var billingManager: BillingManager? = null
    
    // Billing callback for handling purchases
    private val billingCallback = object : BillingCallback {
        override fun onBillingSetupFinished(success: Boolean) {
            android.util.Log.d("Billing", "Setup finished: $success")
        }
        
        override fun onPurchaseSuccess(purchase: PurchaseData) {
            android.util.Log.d("Billing", "Purchase success: ${purchase.productId}")

            when (purchase.productId) {
                BillingManager.EXTRA_LIFE_PREMIUM -> {
                    // Grant extra life
                    val config = gameConfigManager.getCurrentConfig()
                    val newLives = minOf(config.playerLives + 1, 3)
                    gameConfigManager.setLives(newLives)

                    // Update local playerLives if currently playing
                    if (gameState == GameState.PLAYING) {
                        playerLives = newLives
                    }

                    // Play success sound
                    soundManager.playSound(SoundManager.SoundType.POWERUP)
                    android.util.Log.d("Billing", "Extra life granted successfully")
                }
                else -> {
                    android.util.Log.d("Billing", "Purchase processed for: ${purchase.productId}")
                }
            }
        }
        
        override fun onPurchaseError(error: String) {
            android.util.Log.e("Billing", "Purchase error: $error")
            soundManager.playSound(SoundManager.SoundType.LOSE_LIFE)
        }
        
        override fun onPurchasesRestored(count: Int) {
            android.util.Log.d("Billing", "Restored $count purchases")
        }
        
        override fun onProductUnlocked(productId: String, message: String) {
            android.util.Log.d("Billing", "Product unlocked: $productId - $message")
        }
        
        override fun onCoinsGranted(amount: Int) {
            val config = gameConfigManager.getCurrentConfig()
            gameConfigManager.updateCoins(config.totalCoins + amount)
            soundManager.playSound(SoundManager.SoundType.COIN)
        }
        
        override fun onLivesGranted(amount: Int) {
            val config = gameConfigManager.getCurrentConfig()
            gameConfigManager.setLives(minOf(config.playerLives + amount, GameConstants.MAX_LIVES))
            soundManager.playSound(SoundManager.SoundType.COIN)
        }
    }
    
    // Game thread
    private var gameThread: GameThread? = null
    
    // Screen dimensions
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    
    // Component managers
    private lateinit var touchHandler: TouchHandler
    private lateinit var uiRenderer: UIRenderer
    private lateinit var achievementRenderer: AchievementRenderer
    private lateinit var tutorialRenderer: TutorialRenderer
    private lateinit var statisticsRenderer: StatisticsRenderer
    private lateinit var hatRenderer: HatRenderer

    // Game objects
    private lateinit var bird: Bird
    private val birdAnimator = BirdAnimator()
    
    // Game state
    private var gameState = GameState.MENU
    private var score = 0
    private var totalScore = 0 // Accumulate total score across all lives
    private var finalScore = 0 // Preserve score for Game Over screen
    private var gamePaused = false
    
    // Score popup for visual feedback
    private var scorePopupTimer = 0
    private var scorePopupText = ""
    
    // Lives system
    private var playerLives = GameConstants.MAX_LIVES
    private var invulnerableTime = 0
    
    // Game objects collections
    private val pipes = ArrayList<Pipe>()
    private val powerUps = ArrayList<PowerUp>()
    private val coins = ArrayList<Coin>()
    
    // Spawn timers
    private var pipeSpawnTimer = 0
    private var powerUpSpawnTimer = 0
    private var coinSpawnTimer = 0
    private var powerUpSpawnDelay = Random.nextInt(
        GameConstants.POWERUP_SPAWN_MIN, 
        GameConstants.POWERUP_SPAWN_MAX + 1
    )
    
    // Loaded resources
    private var birdSprites: BirdSprites? = null
    private var heartSprites: HeartSprites? = null
    private var powerUpSprites: PowerUpSprites? = null
    private var coinSprites: CoinSprites? = null
    
    // Power-up states
    private var powerUpStates = PowerUpStates()
    
    // Power-up timers
    private var shieldTimer = 0
    private var slowMotionTimer = 0
    private var scoreMultiplierTimer = 0
    private var magnetTimer = 0
    
    // UI states
    private var showPauseMenu = false
    private var showSettings = false
    private var showShop = false
    private var showAchievements = false
    private var showTutorial = false
    private var showStatistics = false
    private var statisticsScrollOffset = 0f
    private var showBirdSkins = false
    private var hatScrollOffset = 0f
    
    // Unlock message state
    private var showUnlockMessage = false
    private var unlockMessageHat: HatType = HatType.NONE
    private var unlockMessageRequiredScore = 0
    
    // Tutorial state
    private var currentTutorialStep = 0
    private val tutorialSteps = TutorialSteps.getAllSteps()
    private var tutorialAnimationTimer = 0
    private val tutorialAnimationDuration = 30 // 0.5 seconds at 60fps
    
    // Achievement notification
    private var achievementNotification: Achievement? = null
    private var notificationTimer = 0
    private val notificationDuration = 180 // 3 seconds at 60fps
    
    // Game session tracking
    private var gameStartTime = 0L
    private var currentSessionCoins = 0
    
    // Statistics for game config
    private var jumpCount = 0
    private var collectiblesCollected = 0
    
    init {
        holder.addCallback(this)
        getScreenDimensions()
        isFocusable = true
    }
    
    private fun getScreenDimensions() {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.currentWindowMetrics.bounds
            screenWidth = bounds.width()
            screenHeight = bounds.height()
        } else {
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            val size = Point()
            @Suppress("DEPRECATION")
            display.getSize(size)
            screenWidth = size.x
            screenHeight = size.y
        }
    }
    
    override fun surfaceCreated(holder: SurfaceHolder) {
        initializeGame()
        gameThread = GameThread(holder, this)
        gameThread?.setRunning(true)
        gameThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        screenWidth = width
        screenHeight = height

        // Re-initialize density-aware scaling system with correct dimensions
        DensityUtils.initialize(context, screenWidth, screenHeight)

        // Re-initialize ALL screen-dependent components with correct dimensions
        touchHandler = TouchHandler(screenWidth, screenHeight)
        uiRenderer = UIRenderer(screenWidth, screenHeight)
        achievementRenderer = AchievementRenderer(screenWidth, screenHeight)
        tutorialRenderer = TutorialRenderer(screenWidth, screenHeight)
        statisticsRenderer = StatisticsRenderer(screenWidth, screenHeight)
        hatRenderer = HatRenderer(screenWidth, screenHeight)

        // Re-create bird with correct screen dimensions and proper scaling
        bird = Bird(screenWidth / 4f, screenHeight / 2f, context, hatManager)

        // Refresh bird's hat cache since it was re-created
        bird.refreshHatCache()
    }
    
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        gameThread?.setRunning(false)
        while (retry) {
            try {
                gameThread?.join()
                retry = false
            } catch (_: InterruptedException) {
                // Will try again
            }
        }
    }
    
    /**
     * Initialize game components
     */
    private fun initializeGame() {
        // Initialize DensityUtils with safe default values
        if (screenWidth == 0 || screenHeight == 0) {
            DensityUtils.initialize(context, 1080, 1920) // Standard phone dimensions
        }

        // Initialize components with fallback dimensions
        touchHandler = TouchHandler(screenWidth.takeIf { it > 0 } ?: 1080, screenHeight.takeIf { it > 0 } ?: 1920)
        uiRenderer = UIRenderer(screenWidth.takeIf { it > 0 } ?: 1080, screenHeight.takeIf { it > 0 } ?: 1920)
        achievementRenderer = AchievementRenderer(screenWidth.takeIf { it > 0 } ?: 1080, screenHeight.takeIf { it > 0 } ?: 1920)
        tutorialRenderer = TutorialRenderer(screenWidth.takeIf { it > 0 } ?: 1080, screenHeight.takeIf { it > 0 } ?: 1920)
        statisticsRenderer = StatisticsRenderer(screenWidth.takeIf { it > 0 } ?: 1080, screenHeight.takeIf { it > 0 } ?: 1920)
        hatRenderer = HatRenderer(screenWidth.takeIf { it > 0 } ?: 1080, screenHeight.takeIf { it > 0 } ?: 1920)

        // Create bird with safe dimensions
        bird = Bird((screenWidth.takeIf { it > 0 } ?: 1080) / 4f, (screenHeight.takeIf { it > 0 } ?: 1920) / 2f, context, hatManager)

        // Load initial config
        val config = gameConfigManager.getCurrentConfig()
        soundManager.isSoundEnabled = config.soundEnabled
        soundManager.isMusicEnabled = config.musicEnabled

        // Initialize audio
        soundManager.startBackgroundMusic()

        // Initialize billing manager
        billingManager = BillingManager(context, billingCallback)
        billingManager?.initializeBilling()

        // Check privacy compliance on startup
        if (privacyManager.shouldShowPrivacyDialog()) {
            (context as? Activity)?.let { activity ->
                privacyManager.showPrivacyDialog(activity) {
                    // Privacy accepted, continue with app
                }
            }
        }

        // Check for daily bonus availability
        if (dailyBonusManager.isDailyBonusAvailable()) {
            // TODO: Show notification or indicator for daily bonus
            android.util.Log.d("GameView", "Daily bonus available")
        }

        // Setup auto-update mechanism
        // TODO: Implement auto-update functionality

        // Load game resources
        birdSprites = hatManager.getCurrentBirdSprites() ?: resourceManager.loadBirdSprites()
        heartSprites = resourceManager.loadHeartSprites()
    }

    /**
     * Load gameplay resources when starting a game
     */
    private fun loadGameplayResources() {
        if (powerUpSprites == null) {
            powerUpSprites = resourceManager.loadPowerUpSprites()
        }
        if (coinSprites == null) {
            coinSprites = resourceManager.loadCoinSprites()
        }
    }
    
    /**
     * Handle touch events using TouchHandler
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val command = touchHandler.handleTouch(
                event.x, event.y, gameState,
                showPauseMenu, showSettings, showShop, showAchievements, showStatistics, showBirdSkins, showTutorial,
                gameConfigManager.getCurrentConfig().playerLives,
                gamePaused
            )
            
            // Handle unlock message tap to close (should consume the touch event)
            if (showUnlockMessage) {
                showUnlockMessage = false
                // No sound when closing unlock message
                performClick() // For accessibility
                return true  // Return early, don't process other commands
            }
            
            // If no UI command was processed and we're showing bird skins, check for hat card touches
            if (command == null && showBirdSkins) {
                val hatInfoList = hatManager.getAllHatsWithStatus()
                val requiredScoresMap = hatRenderer.getRequiredScoresMap()
                val hatCardCommand = touchHandler.checkHatCardTouch(event.x, event.y, hatInfoList, requiredScoresMap)
                hatCardCommand?.let { executeCommand(it) }
            } else {
                command?.let { executeCommand(it) }
            }
            performClick() // For accessibility
        }
        return true
    }
    
    /**
     * Required for accessibility when overriding onTouchEvent
     */
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
    
    /**
     * Execute touch commands
     */
    private fun executeCommand(command: TouchCommand) {
        when (command) {
            is TouchCommand.Jump -> {
                if (gameState == GameState.PLAYING && !gamePaused && 
                    !showPauseMenu && !showSettings && !showShop && !showAchievements && 
                    !showStatistics && !showBirdSkins && !showTutorial && !showUnlockMessage) {
                    bird.jump()
                    birdAnimator.addTrailEffect(bird.x, bird.y)
                    soundManager.playSound(SoundManager.SoundType.JUMP)
                    jumpCount++
                }
            }
            is TouchCommand.StartGame -> {
                if (gameState == GameState.MENU && gameConfigManager.canPlayWithoutRefill()) {
                    animationManager.onButtonPress("start_game")
                    soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                    startGame()
                    soundManager.startBackgroundMusic()
                }
            }
            is TouchCommand.RestartGame -> {
                if (gameState == GameState.GAME_OVER && gameConfigManager.canPlayWithoutRefill()) {
                    animationManager.onButtonPress("restart_game")
                    soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                    restartGame()
                    soundManager.startBackgroundMusic()
                }
            }
            is TouchCommand.ContinueGame -> {
                if (gameState == GameState.GAME_OVER && gameConfigManager.canPlay()) {
                    animationManager.onButtonPress("continue_game")
                    soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                    continueGame()
                    soundManager.startBackgroundMusic()
                }
            }
            is TouchCommand.TogglePause -> {
                animationManager.onButtonPress("pause")
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                togglePause()
            }
            is TouchCommand.Resume -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                showPauseMenu = false
                gamePaused = false
                soundManager.startBackgroundMusic()
                soundManager.resumeAllSounds()
            }
            is TouchCommand.ExitGame -> {
                (context as? Activity)?.finish()
            }
            is TouchCommand.GoToMenu -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                gameState = GameState.MENU
                showPauseMenu = false
                gamePaused = false
                resetGameState() // Reset the game state when going to menu
                soundManager.stopBackgroundMusic()
            }
            is TouchCommand.ShowSettings -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                showSettings = true
                gamePaused = true
                soundManager.pauseBackgroundMusic()
                soundManager.pauseAllSounds()
            }
            is TouchCommand.CloseSettings -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                showSettings = false
                gamePaused = false
                if (gameState == GameState.PLAYING) {
                    soundManager.startBackgroundMusic()
                }
                soundManager.resumeAllSounds()
            }
            is TouchCommand.ShowShop -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                showShop = true
                gamePaused = true
                soundManager.pauseBackgroundMusic()
                soundManager.pauseAllSounds()
            }
            is TouchCommand.CloseShop -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                showShop = false
                gamePaused = false
                if (gameState == GameState.PLAYING) {
                    soundManager.startBackgroundMusic()
                }
                soundManager.resumeAllSounds()
            }
            is TouchCommand.ShowAchievements -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                showAchievements = true
                gamePaused = true
                soundManager.pauseBackgroundMusic()
                soundManager.pauseAllSounds()
            }
            is TouchCommand.CloseAchievements -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                showAchievements = false
                gamePaused = false
                if (gameState == GameState.PLAYING) {
                    soundManager.startBackgroundMusic()
                }
                soundManager.resumeAllSounds()
            }
            is TouchCommand.ShowStatistics -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                showStatistics = true
                statisticsScrollOffset = 0f
                gamePaused = true
                // CRITICAL: Close pause menu when showing statistics
                showPauseMenu = false
                soundManager.pauseBackgroundMusic()
                soundManager.pauseAllSounds()
            }
            is TouchCommand.CloseStatistics -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                showStatistics = false
                gamePaused = false
                if (gameState == GameState.PLAYING) {
                    soundManager.startBackgroundMusic()
                }
                soundManager.resumeAllSounds()
            }
            is TouchCommand.ShowBirdSkins -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                showBirdSkins = true
                hatScrollOffset = 0f
                gamePaused = true
                soundManager.pauseBackgroundMusic()
                soundManager.pauseAllSounds()
            }
            is TouchCommand.CloseBirdSkins -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                showBirdSkins = false
                gamePaused = false
                if (gameState == GameState.PLAYING) {
                    soundManager.startBackgroundMusic()
                }
                soundManager.resumeAllSounds()
            }
            is TouchCommand.SelectHat -> {
                if (hatManager.selectHat(command.hatType)) {
                    // Reload bird sprites (hat is overlay)
                    birdSprites = hatManager.getCurrentBirdSprites()
                    soundManager.playSound(SoundManager.SoundType.COIN)
                }
            }
            is TouchCommand.UnlockHat -> {
                // Hats are unlocked through achievements, not purchases
                if (hatManager.processHatUnlock(command.hatType)) {
                    soundManager.playSound(SoundManager.SoundType.COIN)
                }
            }
            is TouchCommand.NextTutorialStep -> {
                if (showTutorial) {
                    soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                    currentTutorialStep++
                    tutorialAnimationTimer = 0
                    
                    if (currentTutorialStep >= tutorialSteps.size) {
                        // Tutorial completed
                        showTutorial = false
                        tutorial.markTutorialCompleted()
                        soundManager.playSound(SoundManager.SoundType.ACHIEVEMENT)
                    }
                }
            }
            is TouchCommand.SkipTutorial -> {
                if (showTutorial) {
                    soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                    showTutorial = false
                    tutorial.markTutorialCompleted()
                }
            }
            is TouchCommand.BuyLife -> {
                val config = gameConfigManager.getCurrentConfig()
                if (config.totalCoins >= 500 && config.playerLives < 3) {
                    soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                    // Deduct coins and add life
                    val newCoins = config.totalCoins - 500
                    val newLives = minOf(config.playerLives + 1, 3)
                    
                    gameConfigManager.updateCoins(newCoins)
                    gameConfigManager.setLives(newLives)
                    
                    // Update local playerLives if currently playing
                    if (gameState == GameState.PLAYING) {
                        playerLives = newLives
                    }
                    
                    // Play success sound
                    soundManager.playSound(SoundManager.SoundType.POWERUP)
                } else if (config.totalCoins < 500) {
                    // Play error sound when can't afford
                    soundManager.playSound(SoundManager.SoundType.LOSE_LIFE)
                }
            }
            is TouchCommand.BuyLifePremium -> {
                val config = gameConfigManager.getCurrentConfig()
                if (config.playerLives < 3) {
                    soundManager.playSound(SoundManager.SoundType.UI_CLICK)

                    // Use BillingManager to handle the premium purchase
                    billingManager?.purchaseProduct(context as Activity, BillingManager.EXTRA_LIFE_PREMIUM)
                } else {
                    // Already at max lives
                    soundManager.playSound(SoundManager.SoundType.LOSE_LIFE)
                }
            }
            is TouchCommand.ToggleSound -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                val config = gameConfigManager.getCurrentConfig()
                gameConfigManager.updateAudioSettings(!config.soundEnabled, config.musicEnabled)
                val newConfig = gameConfigManager.getCurrentConfig()
                soundManager.isSoundEnabled = newConfig.soundEnabled
                soundManager.isMusicEnabled = newConfig.musicEnabled
            }
            is TouchCommand.ToggleMusic -> {
                soundManager.playSound(SoundManager.SoundType.UI_CLICK)
                val config = gameConfigManager.getCurrentConfig()
                gameConfigManager.updateAudioSettings(config.soundEnabled, !config.musicEnabled)
                val newConfig = gameConfigManager.getCurrentConfig()
                soundManager.isSoundEnabled = newConfig.soundEnabled
                soundManager.isMusicEnabled = newConfig.musicEnabled
            }
            is TouchCommand.PurchaseItem -> {
                // Shop functionality - to be implemented
            }
            is TouchCommand.ShowUnlockMessage -> {
                showUnlockMessage = true
                unlockMessageHat = command.hatType
                unlockMessageRequiredScore = command.requiredScore
                // No sound when opening unlock message
            }
        }
    }
    
    /**
     * Toggle pause state
     */
    private fun togglePause() {
        if (gameState == GameState.PLAYING) {
            gamePaused = !gamePaused
            showPauseMenu = gamePaused
            
            if (gamePaused) {
                soundManager.pauseBackgroundMusic()
                soundManager.pauseAllSounds()
            } else {
                soundManager.startBackgroundMusic()
                soundManager.resumeAllSounds()
            }
        }
    }
    
    /**
     * Start a new game
     */
    private fun startGame() {

        // Load gameplay resources only when needed
        loadGameplayResources()

        gameState = GameState.PLAYING
        resetGameState()

        // Sync local lives with GameConfigManager (no auto-refill)
        playerLives = gameConfigManager.getCurrentConfig().playerLives
        
        // Track game session start
        gameStartTime = System.currentTimeMillis()
        currentSessionCoins = 0
        statisticsManager.startSession()
        
        // Check if tutorial should be shown
        if (tutorial.shouldShowTutorial()) {
            showTutorial = true
            currentTutorialStep = 0
        }
        
        soundManager.startBackgroundMusic()
    }
    
    /**
     * Restart the game
     */
    private fun restartGame() {
        gameState = GameState.PLAYING
        resetGameState()

        // Sync local lives with GameConfigManager (no auto-refill during gameplay)
        playerLives = gameConfigManager.getCurrentConfig().playerLives
    }
    
    /**
     * Continue game with refilled hearts (resume from death point)
     */
    private fun continueGame() {
        gameState = GameState.PLAYING
        
        // DON'T reset score - preserve current session score and totalScore
        // score and totalScore remain as they were when player died
        
        // Reset bird position and physics only
        val birdX = screenWidth * 0.25f
        val birdY = screenHeight / 2f
        bird.x = birdX
        bird.y = birdY
        bird.reset(birdX, birdY)
        birdAnimator.reset()
        
        // DON'T clear game objects - keep pipes, coins, powerUps as they were
        // This preserves the game state from when player died
        
        // Reset only power-up active states (but keep the objects)
        powerUpStates = PowerUpStates()
        shieldTimer = 0
        slowMotionTimer = 0
        scoreMultiplierTimer = 0
        magnetTimer = 0
        
        // DON'T reset spawn timers - keep natural progression
        
        // Sync local lives with GameConfigManager (get refilled hearts)
        playerLives = gameConfigManager.getCurrentConfig().playerLives
    }
    
    /**
     * Reset game state for new game
     */
    private fun resetGameState() {
        score = 0
        totalScore = 0
        finalScore = 0
        // Don't reset playerLives here - it should be managed by GameConfigManager
        invulnerableTime = 0
        gamePaused = false
        jumpCount = 0
        collectiblesCollected = 0
        scorePopupTimer = 0
        scorePopupText = ""
        
        bird.reset(screenWidth / 4f, screenHeight / 2f)
        birdAnimator.reset()
        
        pipes.clear()
        powerUps.clear()
        coins.clear()
        
        pipeSpawnTimer = 0
        powerUpSpawnTimer = 0
        coinSpawnTimer = 0
        
        resetPowerUpStates()
    }
    
    /**
     * Reset game state for continuing with remaining lives (preserves life count)
     */
    private fun continueGameWithRemainingLives() {
        score = 0
        // Don't reset totalScore or finalScore here - we want to accumulate across lives
        invulnerableTime = 0
        gamePaused = false
        jumpCount = 0
        collectiblesCollected = 0
        scorePopupTimer = 0
        scorePopupText = ""
        
        bird.reset(screenWidth / 4f, screenHeight / 2f)
        birdAnimator.reset()
        
        pipes.clear()
        powerUps.clear()
        coins.clear()
        
        pipeSpawnTimer = 0
        powerUpSpawnTimer = 0
        coinSpawnTimer = 0
        
        resetPowerUpStates()
    }
    
    /**
     * Reset all power-up states
     */
    private fun resetPowerUpStates() {
        powerUpStates = PowerUpStates()
        shieldTimer = 0
        slowMotionTimer = 0
        scoreMultiplierTimer = 0
        magnetTimer = 0
    }
    
    /**
     * Main game update method
     */
    override fun update() {
        if (gamePaused || showTutorial || showPauseMenu || showSettings || showShop || 
            showAchievements || showStatistics || showBirdSkins) return
        
        when (gameState) {
            GameState.PLAYING -> updateGameplay()
            else -> {
                // Update power-up timers even in menu states to prevent stuck effects
                updatePowerUpTimers()
            }
        }
    }
    
    /**
     * Update gameplay logic
     */
    private fun updateGameplay() {
        // Update bird
        bird.update()
        birdAnimator.update(bird.velocityY)
        
        // Update game objects
        updatePipes()
        updateCollectibleSpawning()
        updateCollectibles()
        updatePowerUpTimers()
        
        // Update invulnerability timer
        if (invulnerableTime > 0) {
            invulnerableTime--
        }
        
        // Update score popup timer
        if (scorePopupTimer > 0) {
            scorePopupTimer--
        }
        
        // Update achievement notification timer
        if (notificationTimer > 0) {
            notificationTimer--
            if (notificationTimer == 0) {
                achievementNotification = null
            }
        }
        
        // Update tutorial animation
        if (showTutorial && tutorialAnimationTimer < tutorialAnimationDuration) {
            tutorialAnimationTimer++
        }
        
        // Check collisions
        checkCollisions()
    }
    
    /**
     * Update pipes with slow motion consideration and consistent global speed
     */
    private fun updatePipes() {
        if (!powerUpStates.slowMotionActive || Random.nextFloat() < 0.5f) {
            val currentSpawnDelay = calculatePipeSpacing(score)

            pipeSpawnTimer++
            if (pipeSpawnTimer >= currentSpawnDelay) {
                pipes.add(Pipe(screenWidth.toFloat(), screenHeight, GameConstants.GROUND_HEIGHT, score))
                pipeSpawnTimer = 0
            }

            // Calculate global pipe speed based on current score
            val globalPipeSpeed = calculateGlobalPipeSpeed(score)

            val pipesToRemove = ArrayList<Pipe>()
            for (pipe in pipes) {
                pipe.update(globalPipeSpeed) // All pipes use same speed

                if (pipe.isOffScreen()) {
                    pipesToRemove.add(pipe)
                }
                
                // Check if bird passed pipe (increase score)
                if (pipe.hasBirdPassed(bird) && !pipe.passed) {
                    pipe.passed = true
                    val points = if (powerUpStates.scoreMultiplierActive) {
                        GameConstants.SCORE_MULTIPLIER_BONUS
                    } else {
                        GameConstants.SCORE_PER_PIPE
                    }
                    score += points
                    
                    // Track score achievements
                    val newAchievements = achievementManager.trackScore(score)
                    if (newAchievements.isNotEmpty() && achievementNotification == null) {
                        achievementNotification = newAchievements.first()
                        notificationTimer = notificationDuration
                        soundManager.playSound(SoundManager.SoundType.ACHIEVEMENT)
                        // Achievement celebration effect
                        animationManager.celebrateAchievement(screenWidth / 2f, screenHeight / 3f)
                        
                        // Award coin rewards for newly unlocked achievements
                        newAchievements.forEach { achievement ->
                            val config = gameConfigManager.getCurrentConfig()
                            gameConfigManager.updateCoins(config.totalCoins + achievement.rewardCoins)
                        }
                    }
                    soundManager.playSound(SoundManager.SoundType.SCORE)
                    // Create visual feedback for scoring
                    scorePopupTimer = 30 // Show for ~0.5 seconds at 60fps
                    scorePopupText = "+$points"
                }
            }
            pipes.removeAll(pipesToRemove)
        }
    }
    
    /**
     * Calculate pipe spawn spacing based on score
     */
    private fun calculatePipeSpacing(score: Int): Int {
        return when {
            score <= 15 -> GameConstants.PipeSpacing.EASY
            score <= 30 -> GameConstants.PipeSpacing.NORMAL
            score <= 45 -> GameConstants.PipeSpacing.MEDIUM
            score <= 60 -> GameConstants.PipeSpacing.HARD
            score <= 80 -> GameConstants.PipeSpacing.HARDER
            score <= 100 -> GameConstants.PipeSpacing.HARDEST
            else -> GameConstants.PipeSpacing.INSANE
        }
    }

    /**
     * Calculate global pipe speed that all pipes use (prevents spacing changes)
     */
    private fun calculateGlobalPipeSpeed(score: Int): Float {
        val baseSpeed = GameConstants.PIPE_BASE_SPEED
        val maxSpeed = GameConstants.PIPE_MAX_SPEED
        val speedIncrease = GameConstants.PIPE_SPEED_INCREASE

        val increase = (score / GameConstants.PIPE_SCORE_INTERVAL) * speedIncrease
        return maxSpeed.coerceAtMost(baseSpeed + increase)
    }
    
    /**
     * Update collectible spawning
     */
    private fun updateCollectibleSpawning() {
        // Power-up spawning
        powerUpSpawnTimer++
        if (powerUpSpawnTimer >= powerUpSpawnDelay && score >= GameConstants.COLLECTIBLE_SPAWN_SCORE_THRESHOLD) {
            spawnPowerUp()
        }
        
        // Coin spawning
        coinSpawnTimer++
        if (coinSpawnTimer >= GameConstants.COIN_SPAWN_DELAY && score >= GameConstants.COLLECTIBLE_SPAWN_SCORE_THRESHOLD) {
            spawnCoins()
        }
    }
    
    /**
     * Spawn a random power-up
     */
    private fun spawnPowerUp() {
        val powerUpTypes = PowerUpType.entries.toTypedArray()
        val randomType = powerUpTypes[Random.nextInt(powerUpTypes.size)]
        
        // Ensure powerup sprites are loaded, reload if necessary
        if (powerUpSprites == null) {
            powerUpSprites = resourceManager.loadPowerUpSprites()
        }
        
        val sprite = powerUpSprites?.let { sprites ->
            val selectedSprite = when (randomType) {
                PowerUpType.SHIELD -> sprites.shield
                PowerUpType.SLOW_MOTION -> sprites.slow
                PowerUpType.SCORE_MULTIPLIER -> sprites.multiplier
                PowerUpType.MAGNET -> sprites.magnet
                PowerUpType.EXTRA_LIFE -> sprites.life
            }
            selectedSprite
        }
        
        val spawnY = Random.nextInt(screenHeight / 4, (screenHeight * 0.75f).toInt()).toFloat()
        powerUps.add(PowerUp(screenWidth.toFloat() + 100f, spawnY, randomType, sprite))
        
        powerUpSpawnDelay = Random.nextInt(
            GameConstants.POWERUP_SPAWN_MIN,
            GameConstants.POWERUP_SPAWN_MAX + 1
        )
        powerUpSpawnTimer = 0
    }
    
    /**
     * Spawn coins (single or chain)
     */
    private fun spawnCoins() {
        val isChain = Random.nextFloat() < GameConstants.CHAIN_COIN_PROBABILITY
        
        if (isChain) {
            val chainLength = Random.nextInt(
                GameConstants.CHAIN_LENGTH_MIN,
                GameConstants.CHAIN_LENGTH_MAX + 1
            )
            val startY = Random.nextInt(screenHeight / 4, (screenHeight * 0.75f).toInt()).toFloat()
            
            for (i in 0 until chainLength) {
                val coinType = if (Random.nextFloat() < GameConstants.DIAMOND_COIN_PROBABILITY) {
                    CoinType.DIAMOND
                } else {
                    CoinType.GOLD
                }
                val sprite = coinSprites?.let { sprites ->
                    if (coinType == CoinType.GOLD) sprites.gold else sprites.diamond
                }
                val coinX = screenWidth.toFloat() + 100f + (i * 80f)
                val coinY = startY + (sin(i * 0.5f) * 30f)
                
                coins.add(Coin(coinX, coinY, coinType, sprite))
            }
        } else {
            val coinType = if (Random.nextFloat() < GameConstants.GOLD_COIN_PROBABILITY) {
                CoinType.GOLD
            } else {
                CoinType.DIAMOND
            }
            val sprite = coinSprites?.let { sprites ->
                if (coinType == CoinType.GOLD) sprites.gold else sprites.diamond
            }
            val coinY = Random.nextInt(screenHeight / 4, (screenHeight * 0.75f).toInt()).toFloat()
            
            coins.add(Coin(screenWidth.toFloat() + 100f, coinY, coinType, sprite))
        }
        
        coinSpawnTimer = 0
    }
    
    /**
     * Update all collectibles
     */
    private fun updateCollectibles() {
        updatePowerUps()
        updateCoins()
    }
    
    /**
     * Update power-ups
     */
    private fun updatePowerUps() {
        val powerUpsToRemove = ArrayList<PowerUp>()
        for (powerUp in powerUps) {
            powerUp.update(bird.x, bird.y, powerUpStates.magnetActive)
            
            if (CollisionUtils.checkBirdCollectibleCollision(
                bird, powerUp.x, powerUp.y, GameConstants.POWERUP_SIZE
            )) {
                activatePowerUp(powerUp.type)
                
                // Add power-up collection effects
                val powerUpColor = when (powerUp.type) {
                    PowerUpType.SHIELD -> "#4169E1".toColorInt() // Royal blue
                    PowerUpType.SLOW_MOTION -> "#9932CC".toColorInt() // Dark orchid
                    PowerUpType.SCORE_MULTIPLIER -> "#FF6347".toColorInt() // Tomato red
                    PowerUpType.MAGNET -> "#32CD32".toColorInt() // Lime green
                    PowerUpType.EXTRA_LIFE -> "#FFD700".toColorInt() // Gold
                }
                animationManager.createParticleExplosion(powerUp.x, powerUp.y, powerUpColor, 12)
                animationManager.addFloatingText("${powerUp.type.displayName}!", powerUp.x, powerUp.y - 40f, powerUpColor, 40f, 2000L)
                animationManager.addScreenShake(6f, 200L)
                
                powerUpsToRemove.add(powerUp)
                collectiblesCollected++
            }
            
            if (powerUp.isOffScreen()) {
                powerUpsToRemove.add(powerUp)
            }
        }
        powerUps.removeAll(powerUpsToRemove)
    }
    
    /**
     * Update coins
     */
    private fun updateCoins() {
        val coinsToRemove = ArrayList<Coin>()
        for (coin in coins) {
            coin.update(bird.x, bird.y, powerUpStates.magnetActive)
            
            if (CollisionUtils.checkBirdCollectibleCollision(
                bird, coin.x, coin.y, GameConstants.COIN_SIZE
            )) {
                score += coin.type.value
                val config = gameConfigManager.getCurrentConfig()
                gameConfigManager.updateCoins(config.totalCoins + coin.type.value)
                soundManager.playSound(SoundManager.SoundType.COIN)
                
                // Add coin collection particle effect
                val coinColor = if (coin.type.value >= 50) "#00BFFF".toColorInt() else "#FFD700".toColorInt()
                animationManager.createParticleExplosion(coin.x, coin.y, coinColor, 8)
                animationManager.addFloatingText("+${coin.type.value}", coin.x, coin.y - 30f, coinColor, 36f, 1200L)
                
                // Track coin collection for achievements
                val coinAchievements = achievementManager.trackCoinCollected(coin.type.value < 50)
                if (coinAchievements.isNotEmpty() && achievementNotification == null) {
                    achievementNotification = coinAchievements.first()
                    notificationTimer = notificationDuration
                    soundManager.playSound(SoundManager.SoundType.ACHIEVEMENT)
                    animationManager.celebrateAchievement(coin.x, coin.y - 50f)
                    
                    // Award coin rewards for newly unlocked achievements
                    coinAchievements.forEach { achievement ->
                        val currentConfig = gameConfigManager.getCurrentConfig()
                        gameConfigManager.updateCoins(currentConfig.totalCoins + achievement.rewardCoins)
                    }
                }
                
                coinsToRemove.add(coin)
                collectiblesCollected++
                currentSessionCoins += coin.type.value
            }
            
            if (coin.isOffScreen()) {
                coinsToRemove.add(coin)
            }
        }
        coins.removeAll(coinsToRemove)
    }
    
    /**
     * Activate a power-up
     */
    private fun activatePowerUp(type: PowerUpType) {
        // Track power-up usage for achievements
        val powerUpAchievements = achievementManager.trackPowerUpUsed(type)
        if (powerUpAchievements.isNotEmpty() && achievementNotification == null) {
            achievementNotification = powerUpAchievements.first()
            notificationTimer = notificationDuration
            soundManager.playSound(SoundManager.SoundType.ACHIEVEMENT)
            animationManager.celebrateAchievement(screenWidth / 2f, screenHeight / 3f)
            
            // Award coin rewards for newly unlocked achievements
            powerUpAchievements.forEach { achievement ->
                val config = gameConfigManager.getCurrentConfig()
                gameConfigManager.updateCoins(config.totalCoins + achievement.rewardCoins)
            }
        }
        
        when (type) {
            PowerUpType.SHIELD -> {
                powerUpStates = powerUpStates.copy(
                    shieldActive = true,
                    shieldUsesThisGame = powerUpStates.shieldUsesThisGame + 1
                )
                shieldTimer = GameConstants.PowerUpDuration.SHIELD
            }
            PowerUpType.SLOW_MOTION -> {
                powerUpStates = powerUpStates.copy(
                    slowMotionActive = true,
                    slowMotionUsesThisGame = powerUpStates.slowMotionUsesThisGame + 1
                )
                slowMotionTimer = GameConstants.PowerUpDuration.SLOW_MOTION
            }
            PowerUpType.SCORE_MULTIPLIER -> {
                powerUpStates = powerUpStates.copy(scoreMultiplierActive = true)
                scoreMultiplierTimer = GameConstants.PowerUpDuration.SCORE_MULTIPLIER
            }
            PowerUpType.MAGNET -> {
                powerUpStates = powerUpStates.copy(
                    magnetActive = true,
                    magnetUsesThisGame = powerUpStates.magnetUsesThisGame + 1
                )
                magnetTimer = GameConstants.PowerUpDuration.MAGNET
            }
            PowerUpType.EXTRA_LIFE -> {
                if (playerLives < GameConstants.MAX_LIVES) {
                    playerLives++
                    // IMPORTANT: Also update GameConfigManager so loseLife() works correctly
                    gameConfigManager.setLives(playerLives)
                } else {
                    // Player already has max lives, save the extra life for later use
                    gameConfigManager.addSavedExtraLife()
                }
            }
        }
        
        soundManager.playSound(SoundManager.SoundType.POWERUP)
    }
    
    /**
     * Update power-up timers
     */
    private fun updatePowerUpTimers() {
        if (shieldTimer > 0) {
            shieldTimer--
            if (shieldTimer <= 0) {
                powerUpStates = powerUpStates.copy(shieldActive = false)
            }
        }
        
        if (slowMotionTimer > 0) {
            slowMotionTimer--
            if (slowMotionTimer <= 0) {
                powerUpStates = powerUpStates.copy(slowMotionActive = false)
            }
        }
        
        if (scoreMultiplierTimer > 0) {
            scoreMultiplierTimer--
            if (scoreMultiplierTimer <= 0) {
                powerUpStates = powerUpStates.copy(scoreMultiplierActive = false)
            }
        }
        
        if (magnetTimer > 0) {
            magnetTimer--
            if (magnetTimer <= 0) {
                powerUpStates = powerUpStates.copy(magnetActive = false)
            }
        }
        
        // Update power-up states with current timer values
        powerUpStates = powerUpStates.copy(
            shieldTimer = shieldTimer,
            slowMotionTimer = slowMotionTimer,
            scoreMultiplierTimer = scoreMultiplierTimer,
            magnetTimer = magnetTimer
        )
    }
    
    /**
     * Check all collision scenarios
     */
    private fun checkCollisions() {
        // Ground collision
        if (bird.y + GameConstants.BIRD_COLLISION_RADIUS >= screenHeight - GameConstants.GROUND_HEIGHT) {
            if (!powerUpStates.shieldActive) loseLife()
        }

        // Ceiling collision
        if (bird.y - GameConstants.BIRD_COLLISION_RADIUS <= 0) {
            if (!powerUpStates.shieldActive) loseLife()
        }
        
        // Pipe collisions
        if (invulnerableTime <= 0 && !powerUpStates.shieldActive) {
            for (pipe in pipes) {
                if (pipe.collidesWith(bird)) {
                    loseLife()
                    break
                }
            }
        }
    }
    
    /**
     * Handle losing a life
     */
    private fun loseLife() {
        if (invulnerableTime > 0) return

        invulnerableTime = GameConstants.INVULNERABLE_DURATION
        
        // Add screen shake effect for collision impact
        animationManager.addScreenShake(12f, 500L)
        
        bird.y = screenHeight / 2f
        bird.velocityY = 0f
        
        // Use only GameConfigManager for life tracking - no double decrement
        val remainingLives = gameConfigManager.loseLife()
        playerLives = remainingLives

        if (remainingLives <= 0) {
            // Check if we can use a saved extra life
            if (gameConfigManager.useSavedExtraLife()) {
                playerLives = 1 // Sync local lives with the restored life
                soundManager.playSound(SoundManager.SoundType.POWERUP) // Special sound for saved life usage

                // TODO: Could add visual message here later - for now audio feedback is enough
            } else {
                gameOver()
            }
        } else {
            soundManager.playSound(SoundManager.SoundType.LOSE_LIFE)
        }
    }
    
    /**
     * Handle game over
     */
    private fun gameOver() {
        // Add current run's score to total score
        totalScore += score

        // Life was already decremented in loseLife() - no double decrement
        val remainingLives = playerLives

        // Always show game over when no lives remain
        if (remainingLives <= 0) {
            // Save the total accumulated score as final score
            finalScore = totalScore
            
            gameState = GameState.GAME_OVER
            soundManager.playSound(SoundManager.SoundType.GAME_OVER)
            soundManager.stopBackgroundMusic()
            
            // Update best score and record game completion using final score
            val oldHighScore = gameConfigManager.getCurrentConfig().bestScore
            gameConfigManager.updateBestScore(finalScore)
            gameConfigManager.recordGameCompleted(jumpCount, collectiblesCollected)
            
            // Check for newly unlocked hats based on high score
            val newHighScore = gameConfigManager.getCurrentConfig().bestScore
            if (newHighScore > oldHighScore) {
                val newlyUnlockedHats = hatManager.checkAndUnlockHatsForScore(newHighScore)
                if (newlyUnlockedHats.isNotEmpty()) {
                    // TODO: Show notification to user about new unlocks
                    android.util.Log.d("GameView", "Unlocked ${newlyUnlockedHats.size} new hats")
                }
            }
            
            // Record statistics
            val powerUpsUsedThisGame = mapOf(
                "shield" to powerUpStates.shieldUsesThisGame,
                "magnet" to powerUpStates.magnetUsesThisGame,
                "slow_motion" to powerUpStates.slowMotionUsesThisGame
            )
            
            statisticsManager.recordGameCompletion(
                score = finalScore,
                pipesPassedThisGame = score, // Score is essentially pipes passed
                coinsCollectedThisGame = currentSessionCoins,
                powerUpsUsedThisGame = powerUpsUsedThisGame,
                died = true,
                usedContinue = false // TODO: Track continue usage
            )
            
            // Track game completion for achievements
            val gameSessionSeconds = ((System.currentTimeMillis() - gameStartTime) / 1000).toInt()
            val gameAchievements = achievementManager.trackGamePlayed(gameSessionSeconds)
            gameAchievements.forEach { achievement ->
                val config = gameConfigManager.getCurrentConfig()
                gameConfigManager.updateCoins(config.totalCoins + achievement.rewardCoins)
            }
            
            // Check if should show rating dialog
            if (appRatingManager.shouldShowRating()) {
                (context as? Activity)?.let { activity ->
                    appRatingManager.showRatingDialog(activity)
                }
            }
        } else {
            // Still have lives, continue with remaining lives
            continueGameWithRemainingLives()
            soundManager.playSound(SoundManager.SoundType.LOSE_LIFE) // Sound for life lost
        }
    }
    
    /**
     * Main draw method using UIRenderer
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        // Use current score during gameplay for real-time background changes
        // Use fixed darker color for menu and game over screens
        if (gameState == GameState.PLAYING) {
            uiRenderer.drawBackground(canvas, score)
        } else {
            uiRenderer.drawMenuBackground(canvas)
        }
        
        // Apply screen shake if active
        val shakeOffset = animationManager.getScreenShakeOffset()
        canvas.translate(shakeOffset.x, shakeOffset.y)
        
        when (gameState) {
            GameState.MENU -> drawMenu(canvas)
            GameState.PLAYING -> drawGame(canvas)
            GameState.GAME_OVER -> drawGameOver(canvas)
        }
        
        // Reset shake translation
        canvas.translate(-shakeOffset.x, -shakeOffset.y)
        
        // Draw animation effects (particles and floating text)
        animationManager.updateAndDrawParticles(canvas)
        animationManager.updateAndDrawFloatingText(canvas)
        
        // Always draw UI buttons (pause only during gameplay, settings and shop always)
        uiRenderer.drawUIButtons(canvas, touchHandler.getButtonAreas(), gameState)
        
        // Draw overlays
        if (showPauseMenu) uiRenderer.drawPauseMenu(canvas)
        if (showSettings) {
            val config = gameConfigManager.getCurrentConfig()
            uiRenderer.drawSettings(canvas, config.soundEnabled, config.musicEnabled)
        }
        if (showShop) {
            val config = gameConfigManager.getCurrentConfig()
            uiRenderer.drawShop(canvas, config.totalCoins)
        }
        if (showAchievements) {
            achievementRenderer.drawAchievementsScreen(canvas, achievementManager.getAllAchievements())
        }
        if (showStatistics) {
            val stats = statisticsManager.getStatistics()
            val metrics = statisticsManager.getPerformanceMetrics()
            statisticsRenderer.drawStatisticsScreen(canvas, stats, metrics, statisticsScrollOffset)
        }
        if (showBirdSkins) {
            val hatInfoList = hatManager.getAllHatsWithStatus()
            val collectionStats = hatManager.getHatCollectionStats()
            hatRenderer.drawHatSelectionScreen(canvas, hatInfoList, collectionStats, hatScrollOffset)
        }
        if (showUnlockMessage) {
            val currentScore = gameConfigManager.getCurrentConfig().bestScore
            hatRenderer.drawUnlockMessage(canvas, unlockMessageHat, unlockMessageRequiredScore, currentScore)
        }
        if (showTutorial && tutorialSteps.isNotEmpty()) {
            val currentStep = tutorialSteps.getOrNull(currentTutorialStep)
            currentStep?.let { step ->
                val animationProgress = if (tutorialAnimationTimer < tutorialAnimationDuration) {
                    tutorialAnimationTimer.toFloat() / tutorialAnimationDuration.toFloat()
                } else {
                    1.0f
                }
                tutorialRenderer.drawTutorialStep(canvas, step, currentTutorialStep + 1, tutorialSteps.size, animationProgress)
            }
        }
        
        // Draw achievement notification if active
        achievementNotification?.let { notification ->
            val progress = 1.0f - (notificationTimer.toFloat() / notificationDuration.toFloat())
            achievementRenderer.drawAchievementNotification(canvas, notification, progress)
        }
    }
    
    /**
     * Draw main menu
     */
    private fun drawMenu(canvas: Canvas) {
        val config = gameConfigManager.getCurrentConfig()
        // Use canPlay() only for menu display (this will update refill timer when appropriate)
        val canPlay = gameConfigManager.canPlay()
        val heartRefillTime = if (gameConfigManager.isHeartRefillActive()) {
            gameConfigManager.formatHeartRefillTime()
        } else null
        
        uiRenderer.drawMenu(canvas, config.bestScore, bird, canPlay, heartRefillTime, config.totalCoins, config.playerLives, heartSprites)
    }
    
    /**
     * Draw game screen
     */
    private fun drawGame(canvas: Canvas) {
        // Draw game objects
        for (pipe in pipes) pipe.draw(canvas)
        for (coin in coins) coin.draw(canvas)
        for (powerUp in powerUps) powerUp.draw(canvas)
        
        // Draw bird with effects
        if (invulnerableTime <= 0 || (invulnerableTime / 5) % 2 == 0) {
            bird.draw(canvas)
            if (powerUpStates.shieldActive) {
                uiRenderer.drawShieldEffect(canvas, bird)
            }
        }
        
        // Draw UI
        val config = gameConfigManager.getCurrentConfig()
        heartSprites?.let { hearts ->
            uiRenderer.drawGameUI(canvas, score, config.totalCoins, playerLives, GameConstants.MAX_LIVES, hearts)
        }
        uiRenderer.drawPowerUpIndicators(canvas, powerUpStates, bird.x, bird.y)
        
        // Draw score popup
        if (scorePopupTimer > 0) {
            uiRenderer.drawScorePopup(canvas, scorePopupText, screenWidth / 2f, screenHeight / 2f)
        }
    }
    
    /**
     * Draw game over screen
     */
    private fun drawGameOver(canvas: Canvas) {
        val config = gameConfigManager.getCurrentConfig()
        val sadBirdSprite = birdSprites?.sad
        // Use canPlay() for game over display (this will update refill timer when appropriate)
        val canPlay = gameConfigManager.canPlay()
        val heartRefillTime = if (gameConfigManager.isHeartRefillActive()) {
            gameConfigManager.formatHeartRefillTime()
        } else null
        
        uiRenderer.drawGameOver(
            canvas, 
            finalScore, 
            config.totalCoins, 
            config.bestScore, 
            sadBirdSprite, 
            bird, 
            canPlay, 
            heartRefillTime,
            config.playerLives,
            3,
            heartSprites
        )
    }
    
    /**
     * Resume game activity
     */
    fun resume() {
        if (gameThread?.isRunning() != true) {
            gameThread = GameThread(holder, this)
            gameThread?.setRunning(true)
            gameThread?.start()
        }
        
        soundManager.onResume()
    }
    
    /**
     * Pause game activity
     */
    fun pause() {
        gameThread?.setRunning(false)
        try {
            gameThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        soundManager.pauseBackgroundMusic()
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        pause()
        animationManager.cleanup()
        soundManager.release()
    }
}

/**
 * GameState - Represents different states of the game
 */
enum class GameState {
    MENU,
    PLAYING,
    GAME_OVER
}