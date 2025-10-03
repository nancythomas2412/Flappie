package com.glacierbird.game

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import java.util.*

/**
 * AppRatingManager - Manages app rating prompts for Google Play Store
 * Critical for Play Store success - ratings significantly impact visibility and downloads
 */
class AppRatingManager(private val context: Context) {
    
    companion object {
        private const val TAG = "AppRatingManager"
        private const val PREFS_NAME = "app_rating_prefs"
        private const val KEY_INSTALL_DATE = "install_date"
        private const val KEY_LAUNCH_COUNT = "launch_count"
        private const val KEY_FIRST_LAUNCH_VERSION = "first_launch_version"
        private const val KEY_DONT_SHOW_AGAIN = "dont_show_again"
        private const val KEY_REMIND_LATER_DATE = "remind_later_date"
        private const val KEY_RATED = "rated"
        
        // Rating prompt criteria (optimized for maximum positive ratings)
        private const val MIN_LAUNCH_COUNT = 10 // Show after 10 launches
        private const val MIN_INSTALL_DAYS = 3  // Show after 3 days
        private const val REMIND_LATER_DAYS = 7 // Remind again after 7 days if user chooses "Later"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    init {
        // Track first launch
        if (prefs.getLong(KEY_INSTALL_DATE, 0) == 0L) {
            prefs.edit()
                .putLong(KEY_INSTALL_DATE, Date().time)
                .putInt(KEY_FIRST_LAUNCH_VERSION, getCurrentVersionCode())
                .apply()
        }
        
        // Increment launch count
        val currentLaunchCount = prefs.getInt(KEY_LAUNCH_COUNT, 0)
        prefs.edit().putInt(KEY_LAUNCH_COUNT, currentLaunchCount + 1).apply()
    }
    
    /**
     * Check if rating dialog should be shown
     * Uses intelligent criteria to maximize positive ratings
     */
    fun shouldShowRating(): Boolean {
        // Don't show if user already rated or chose "don't ask again"
        if (prefs.getBoolean(KEY_RATED, false) || prefs.getBoolean(KEY_DONT_SHOW_AGAIN, false)) {
            return false
        }
        
        // Check if user chose "remind later" and time hasn't passed
        val remindLaterDate = prefs.getLong(KEY_REMIND_LATER_DATE, 0)
        if (remindLaterDate > 0 && Date().time < remindLaterDate) {
            return false
        }
        
        // Check launch count threshold
        val launchCount = prefs.getInt(KEY_LAUNCH_COUNT, 0)
        if (launchCount < MIN_LAUNCH_COUNT) {
            return false
        }
        
        // Check install date threshold
        val installDate = prefs.getLong(KEY_INSTALL_DATE, 0)
        val daysSinceInstall = (Date().time - installDate) / (1000 * 60 * 60 * 24)
        if (daysSinceInstall < MIN_INSTALL_DAYS) {
            return false
        }
        
        Log.d(TAG, "Rating criteria met: launches=$launchCount, days=$daysSinceInstall")
        return true
    }
    
    /**
     * Show rating prompt dialog
     */
    fun showRatingDialog(activity: Activity, gameScore: Int = 0) {
        // Create custom rating dialog optimized for positive responses
        val builder = android.app.AlertDialog.Builder(activity)
        
        // Personalized message based on game performance
        val message = if (gameScore > 100) {
            "🎉 Wow! Score of $gameScore is amazing!\n\nWe're so glad you're enjoying Glacier Bird! Would you mind rating us 5 stars on Google Play? It really helps other players discover our game!"
        } else {
            "🐦 Thank you for playing Glacier Bird!\n\nIf you're enjoying the game, would you mind giving us a 5-star rating on Google Play? Your support means everything to us!"
        }
        
        builder.setTitle("Rate Glacier Bird ⭐")
            .setMessage(message)
            .setPositiveButton("Rate 5 Stars! ⭐") { _, _ ->
                markAsRated()
                openPlayStoreForRating(activity)
            }
            .setNeutralButton("Remind me later") { _, _ ->
                remindLater()
            }
            .setNegativeButton("No thanks") { _, _ ->
                dontShowAgain()
            }
            .setCancelable(false) // Ensure user makes a choice
            .show()
    }
    
    /**
     * Show simplified rating prompt for high-scoring players
     */
    fun showQuickRatingPrompt(activity: Activity, highScore: Int) {
        if (!shouldShowRating()) return
        
        val builder = android.app.AlertDialog.Builder(activity)
        builder.setTitle("New High Score! 🏆")
            .setMessage("Amazing! You scored $highScore points!\n\nSince you're clearly enjoying Glacier Bird, how about a quick 5-star rating? ⭐⭐⭐⭐⭐")
            .setPositiveButton("Sure! ⭐") { _, _ ->
                markAsRated()
                openPlayStoreForRating(activity)
            }
            .setNegativeButton("Maybe later") { _, _ ->
                remindLater()
            }
            .show()
    }
    
    /**
     * Open Google Play Store for rating
     */
    private fun openPlayStoreForRating(activity: Activity) {
        try {
            val packageName = activity.packageName
            val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            activity.startActivity(playStoreIntent)
        } catch (e: Exception) {
            // Fallback to web browser if Play Store app not available
            try {
                val packageName = activity.packageName
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                activity.startActivity(webIntent)
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to open Play Store: ${e2.message}")
            }
        }
    }
    
    /**
     * Mark app as rated
     */
    private fun markAsRated() {
        prefs.edit().putBoolean(KEY_RATED, true).apply()
        Log.d(TAG, "User rated the app")
    }
    
    /**
     * User chose "remind me later"
     */
    private fun remindLater() {
        val remindDate = Date().time + (REMIND_LATER_DAYS * 24 * 60 * 60 * 1000L)
        prefs.edit().putLong(KEY_REMIND_LATER_DATE, remindDate).apply()
        Log.d(TAG, "User chose remind later")
    }
    
    /**
     * User chose "don't show again"
     */
    private fun dontShowAgain() {
        prefs.edit().putBoolean(KEY_DONT_SHOW_AGAIN, true).apply()
        Log.d(TAG, "User chose don't show again")
    }
    
    /**
     * Get current app version code
     */
    private fun getCurrentVersionCode(): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        } catch (e: Exception) {
            1
        }
    }
    
    /**
     * Reset rating preferences (for testing)
     */
    fun resetRatingData() {
        prefs.edit().clear().apply()
        Log.d(TAG, "Rating data reset")
    }
    
    /**
     * Get rating statistics for analytics
     */
    fun getRatingStats(): Map<String, Any> {
        return mapOf(
            "launchCount" to prefs.getInt(KEY_LAUNCH_COUNT, 0),
            "daysSinceInstall" to getDaysSinceInstall(),
            "hasRated" to prefs.getBoolean(KEY_RATED, false),
            "dontShowAgain" to prefs.getBoolean(KEY_DONT_SHOW_AGAIN, false)
        )
    }
    
    /**
     * Get days since app installation
     */
    private fun getDaysSinceInstall(): Long {
        val installDate = prefs.getLong(KEY_INSTALL_DATE, 0)
        return if (installDate > 0) {
            (Date().time - installDate) / (1000 * 60 * 60 * 24)
        } else {
            0
        }
    }
}