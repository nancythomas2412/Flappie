package com.glacierbird.game

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Main Activity - Entry point of the Glacier Bird game
 * Sets up the game view and handles system UI
 */
class MainActivity : AppCompatActivity() {

    private lateinit var gameView: GameView
    // private lateinit var remoteConfigManager: RemoteConfigManager // Disabled until Firebase setup
    // private lateinit var adsManager: AdsManager // Disabled until Firebase setup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set fullscreen mode for immersive gaming experience
        setupFullscreen()

        // Initialize Firebase Remote Config and Ads (disabled until setup)
        // initializeFirebaseFeatures()

        // Initialize and set the game view
        gameView = GameView(this)
        // Pass ads manager to game view for ad integration (disabled until setup)
        // gameView.setAdsManager(adsManager)
        setContentView(gameView)
    }

    /**
     * Configure fullscreen mode and keep screen on during gameplay
     */
    private fun setupFullscreen() {
        // Keep screen on during gameplay
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        @Suppress("DEPRECATION")
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Enable edge-to-edge content
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Hide system UI for immersive experience using modern API
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            // Hide all system bars including status bar
            hide(WindowInsetsCompat.Type.systemBars())
            hide(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            // Set immersive sticky behavior
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        
        // Additional fullscreen setup
        supportActionBar?.hide()
    }

    /**
     * Initialize Firebase Remote Config and Ads Manager (disabled until setup)
     */
    /*private fun initializeFirebaseFeatures() {
        // Initialize remote config manager
        remoteConfigManager = RemoteConfigManager(this)

        // Initialize ads manager with remote config control
        adsManager = AdsManager(this, remoteConfigManager)

        // Log current configuration for debugging
        android.util.Log.d("MainActivity", "Remote Config Values: ${remoteConfigManager.getAllConfigValues()}")
    }

    /**
     * Get ads manager instance (for GameView integration)
     */
    fun getAdsManager(): AdsManager = adsManager

    /**
     * Get remote config manager instance
     */
    fun getRemoteConfigManager(): RemoteConfigManager = remoteConfigManager*/

    override fun onResume() {
        super.onResume()
        // Ensure fullscreen mode is maintained
        setupFullscreen()
        
        // Resume game when activity becomes visible
        if (::gameView.isInitialized) {
            gameView.resume()
        }

        // Resume ads (disabled until setup)
        /*if (::adsManager.isInitialized) {
            adsManager.resumeAds()
        }*/
    }

    override fun onPause() {
        super.onPause()
        // Pause game when activity is not visible
        if (::gameView.isInitialized) {
            gameView.pause()
        }

        // Pause ads (disabled until setup)
        /*if (::adsManager.isInitialized) {
            adsManager.pauseAds()
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up game resources
        if (::gameView.isInitialized) {
            gameView.cleanup()
        }

        // Clean up ads (disabled until setup)
        /*if (::adsManager.isInitialized) {
            adsManager.destroyAds()
        }*/
    }
}
