package com.glacierbird.game

import android.content.Context
import android.content.SharedPreferences

/**
 * Tutorial - Manages the tutorial system for first-time players
 * Critical for Google Play Store success - reduces uninstall rates significantly
 */
class Tutorial(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "tutorial_prefs"
        private const val KEY_TUTORIAL_COMPLETED = "tutorial_completed"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_TIMES_PLAYED = "times_played"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Check if this is the first time the app has been launched
     */
    fun isFirstLaunch(): Boolean {
        val isFirst = !prefs.getBoolean(KEY_FIRST_LAUNCH, false)
        if (isFirst) {
            prefs.edit().putBoolean(KEY_FIRST_LAUNCH, true).apply()
        }
        return isFirst
    }
    
    /**
     * Check if tutorial should be shown
     */
    fun shouldShowTutorial(): Boolean {
        val timesPlayed = prefs.getInt(KEY_TIMES_PLAYED, 0)
        val tutorialCompleted = prefs.getBoolean(KEY_TUTORIAL_COMPLETED, false)
        
        // Show tutorial if not completed and player has played less than 3 times
        return !tutorialCompleted && timesPlayed < 3
    }
    
    /**
     * Mark tutorial as completed
     */
    fun markTutorialCompleted() {
        prefs.edit().putBoolean(KEY_TUTORIAL_COMPLETED, true).apply()
    }
    
    /**
     * Increment play count (for tutorial logic)
     */
    fun incrementPlayCount() {
        val currentCount = prefs.getInt(KEY_TIMES_PLAYED, 0)
        prefs.edit().putInt(KEY_TIMES_PLAYED, currentCount + 1).apply()
    }
    
    /**
     * Reset tutorial (for testing)
     */
    fun resetTutorial() {
        prefs.edit()
            .putBoolean(KEY_TUTORIAL_COMPLETED, false)
            .putInt(KEY_TIMES_PLAYED, 0)
            .apply()
    }
}

/**
 * TutorialStep - Represents each step in the tutorial
 */
data class TutorialStep(
    val id: Int,
    val title: String,
    val instruction: String,
    val highlightArea: TutorialHighlight? = null,
    val waitForAction: Boolean = false,
    val actionType: TutorialAction = TutorialAction.TAP_TO_CONTINUE
)

/**
 * TutorialHighlight - Defines an area to highlight during tutorial
 */
data class TutorialHighlight(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val isCircle: Boolean = false
)

/**
 * TutorialAction - Types of actions required in tutorial steps
 */
enum class TutorialAction {
    TAP_TO_CONTINUE,
    TAP_TO_JUMP,
    WAIT_FOR_SCORE,
    WAIT_FOR_COLLISION,
    TAP_SPECIFIC_AREA
}

/**
 * Tutorial steps definition - designed for maximum retention
 */
object TutorialSteps {
    
    fun getAllSteps(): List<TutorialStep> = listOf(
        TutorialStep(
            id = 1,
            title = "Welcome to Glacier Bird! 🐦",
            instruction = "Help your bird fly through the ice caves and collect coins!\n\nTap anywhere to continue",
            actionType = TutorialAction.TAP_TO_CONTINUE
        ),
        
        TutorialStep(
            id = 2, 
            title = "How to Fly",
            instruction = "TAP the screen to make your bird jump up\n\nGravity will pull it down\n\nTry it now!",
            waitForAction = true,
            actionType = TutorialAction.TAP_TO_JUMP
        ),
        
        TutorialStep(
            id = 3,
            title = "Great! 🎉",
            instruction = "Perfect! Keep tapping to stay in the air.\n\nNow let's learn about scoring...",
            actionType = TutorialAction.TAP_TO_CONTINUE
        ),
        
        TutorialStep(
            id = 4,
            title = "Score Points 📊",
            instruction = "Fly through the gaps between pipes to score!\n\nEach gap = +5 points\n\nReady to try?",
            actionType = TutorialAction.TAP_TO_CONTINUE
        ),
        
        TutorialStep(
            id = 5,
            title = "Collect Coins 🪙",
            instruction = "Collect gold coins for +10 points\nDiamond coins give +50 points!\n\nCoins help you buy extra lives",
            actionType = TutorialAction.TAP_TO_CONTINUE
        ),
        
        TutorialStep(
            id = 6,
            title = "Power-ups ⚡",
            instruction = "Look for special power-ups:\n🛡️ Shield protects you\n⏰ Slow motion helps timing\n🧲 Magnet attracts coins\n2️⃣ Score multiplier (2X points)\n❤️ Extra life (+1 heart)",
            actionType = TutorialAction.TAP_TO_CONTINUE
        ),
        
        TutorialStep(
            id = 7,
            title = "Lives System ❤️",
            instruction = "You have 3 hearts (lives)\nLose a heart when you hit pipes\nHearts refill automatically over time!",
            actionType = TutorialAction.TAP_TO_CONTINUE
        ),
        
        TutorialStep(
            id = 8,
            title = "Ready to Play! 🚀",
            instruction = "You're all set! Remember:\n🐦 Tap to fly\n🚫 Avoid pipes\n💎 Collect everything\n\nGood luck!",
            actionType = TutorialAction.TAP_TO_CONTINUE
        )
    )
}