package com.flappie.game

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * SplashActivity - Shows company branding before launching main game
 * Displays "Developed by DeCoCo" for 2.5 seconds
 */
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_DURATION = 2500L // 2.5 seconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up fullscreen before content
        setupFullscreen()

        // Set the splash screen layout
        setContentView(R.layout.activity_splash)

        // Automatically transition to MainActivity after duration
        Handler(Looper.getMainLooper()).postDelayed({
            startMainActivity()
        }, SPLASH_DURATION)
    }

    /**
     * Configure fullscreen mode for splash screen
     */
    private fun setupFullscreen() {
        // Hide system UI for immersive experience
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        supportActionBar?.hide()
    }

    /**
     * Launch the main game activity
     */
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)

        // Modern transition animation using ActivityOptions
        val options = ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        startActivity(intent, options.toBundle())
        finish() // Remove splash from back stack
    }

    override fun onBackPressed() {
        // Disable back button during splash screen
        // Do nothing - let splash complete naturally
    }
}