package com.flappie.game

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.*
import java.text.SimpleDateFormat

/**
 * DailyBonusManager - Daily login rewards system
 * Critical for Google Play Store success - increases Daily Active Users (DAU)
 * Higher DAU = better app store ranking and visibility
 */
class DailyBonusManager(private val context: Context) {
    
    companion object {
        private const val TAG = "DailyBonusManager"
        private const val PREFS_NAME = "daily_bonus_prefs"
        private const val KEY_LAST_CLAIM_DATE = "last_claim_date"
        private const val KEY_CURRENT_STREAK = "current_streak"
        private const val KEY_LONGEST_STREAK = "longest_streak"
        private const val KEY_TOTAL_CLAIMS = "total_claims"
        private const val KEY_FIRST_LOGIN_DATE = "first_login_date"
        
        // Streak reset time (in hours) - 30 hours gives players flexibility
        private const val STREAK_RESET_HOURS = 30
        
        // Date format for consistent date comparison
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Check if daily bonus is available
     */
    fun isDailyBonusAvailable(): Boolean {
        val today = getTodayString()
        val lastClaimDate = prefs.getString(KEY_LAST_CLAIM_DATE, "")
        
        return lastClaimDate != today
    }
    
    /**
     * Get current login streak
     */
    fun getCurrentStreak(): Int {
        updateStreakIfNeeded()
        return prefs.getInt(KEY_CURRENT_STREAK, 0)
    }
    
    /**
     * Get today's bonus reward
     */
    fun getTodaysBonus(): DailyBonus {
        val streak = getCurrentStreak()
        val nextStreak = if (isDailyBonusAvailable()) streak + 1 else streak
        
        return getDailyBonusForStreak(nextStreak)
    }
    
    /**
     * Claim daily bonus
     */
    fun claimDailyBonus(): DailyBonus {
        if (!isDailyBonusAvailable()) {
            throw IllegalStateException("Daily bonus already claimed today")
        }
        
        val currentStreak = getCurrentStreak()
        val newStreak = currentStreak + 1
        val bonus = getDailyBonusForStreak(newStreak)
        
        // Update preferences
        val today = getTodayString()
        val longestStreak = prefs.getInt(KEY_LONGEST_STREAK, 0)
        val totalClaims = prefs.getInt(KEY_TOTAL_CLAIMS, 0)
        
        prefs.edit()
            .putString(KEY_LAST_CLAIM_DATE, today)
            .putInt(KEY_CURRENT_STREAK, newStreak)
            .putInt(KEY_LONGEST_STREAK, maxOf(longestStreak, newStreak))
            .putInt(KEY_TOTAL_CLAIMS, totalClaims + 1)
            .apply()
        
        // Set first login date if not set
        if (prefs.getString(KEY_FIRST_LOGIN_DATE, "")?.isEmpty() != false) {
            prefs.edit().putString(KEY_FIRST_LOGIN_DATE, today).apply()
        }
        
        Log.d(TAG, "Daily bonus claimed - Streak: $newStreak, Reward: ${bonus.coins} coins")
        return bonus
    }
    
    /**
     * Update streak based on time elapsed
     */
    private fun updateStreakIfNeeded() {
        val lastClaimDate = prefs.getString(KEY_LAST_CLAIM_DATE, "")
        if (lastClaimDate?.isEmpty() != false) return
        
        try {
            val lastClaim = DATE_FORMAT.parse(lastClaimDate)
            val now = Date()
            val hoursSinceLastClaim = (now.time - lastClaim!!.time) / (1000 * 60 * 60)
            
            // Reset streak if too much time has passed
            if (hoursSinceLastClaim > STREAK_RESET_HOURS) {
                val yesterday = getTodayString(-1)
                if (lastClaimDate != yesterday) {
                    // Streak broken
                    prefs.edit().putInt(KEY_CURRENT_STREAK, 0).apply()
                    Log.d(TAG, "Streak reset - too much time elapsed")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating streak: ${e.message}")
        }
    }
    
    /**
     * Get daily bonus rewards for specific streak day
     */
    private fun getDailyBonusForStreak(streak: Int): DailyBonus {
        return when (streak % 7) { // Weekly cycle
            1 -> DailyBonus(
                day = streak,
                coins = 50,
                extraLives = 0,
                powerUps = emptyList(),
                title = "Welcome Back! 🌅",
                description = "Start your day with 50 coins!"
            )
            2 -> DailyBonus(
                day = streak,
                coins = 75,
                extraLives = 0,
                powerUps = emptyList(),
                title = "Day 2 Bonus! 💰",
                description = "75 coins for staying consistent!"
            )
            3 -> DailyBonus(
                day = streak,
                coins = 100,
                extraLives = 1,
                powerUps = emptyList(),
                title = "Three Days Strong! ❤️",
                description = "100 coins + 1 extra life!"
            )
            4 -> DailyBonus(
                day = streak,
                coins = 125,
                extraLives = 0,
                powerUps = listOf("shield"),
                title = "Midweek Power! 🛡️",
                description = "125 coins + Shield power-up!"
            )
            5 -> DailyBonus(
                day = streak,
                coins = 150,
                extraLives = 0,
                powerUps = listOf("magnet"),
                title = "Almost There! 🧲",
                description = "150 coins + Magnet power-up!"
            )
            6 -> DailyBonus(
                day = streak,
                coins = 200,
                extraLives = 1,
                powerUps = listOf("slow_motion"),
                title = "Weekend Warrior! ⏰",
                description = "200 coins + Life + Slow Motion!"
            )
            0 -> DailyBonus( // Day 7 (streak % 7 == 0)
                day = streak,
                coins = 300,
                extraLives = 2,
                powerUps = listOf("shield", "magnet", "slow_motion"),
                title = "Week Complete! 🏆",
                description = "MEGA BONUS: 300 coins + 2 lives + All power-ups!"
            )
            else -> DailyBonus(
                day = streak,
                coins = 50,
                extraLives = 0,
                powerUps = emptyList(),
                title = "Daily Login ⭐",
                description = "Thanks for playing!"
            )
        }
    }
    
    /**
     * Get next 7 days preview
     */
    fun getWeeklyPreview(): List<DailyBonus> {
        val currentStreak = getCurrentStreak()
        val startStreak = if (isDailyBonusAvailable()) currentStreak + 1 else currentStreak
        
        return (0 until 7).map { dayOffset ->
            getDailyBonusForStreak(startStreak + dayOffset)
        }
    }
    
    /**
     * Get daily bonus statistics
     */
    fun getDailyBonusStats(): DailyBonusStats {
        val currentStreak = getCurrentStreak()
        val longestStreak = prefs.getInt(KEY_LONGEST_STREAK, 0)
        val totalClaims = prefs.getInt(KEY_TOTAL_CLAIMS, 0)
        val firstLoginDate = prefs.getString(KEY_FIRST_LOGIN_DATE, "")
        
        val daysSinceFirstLogin = if (firstLoginDate?.isNotEmpty() == true) {
            try {
                val firstLogin = DATE_FORMAT.parse(firstLoginDate)
                val today = Date()
                ((today.time - firstLogin!!.time) / (1000 * 60 * 60 * 24)).toInt()
            } catch (e: Exception) {
                0
            }
        } else {
            0
        }
        
        return DailyBonusStats(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalClaims = totalClaims,
            daysSinceFirstLogin = daysSinceFirstLogin,
            bonusAvailableToday = isDailyBonusAvailable()
        )
    }
    
    /**
     * Check if user qualifies for comeback bonus (haven't played in 3+ days)
     */
    fun getComebackBonus(): DailyBonus? {
        val lastClaimDate = prefs.getString(KEY_LAST_CLAIM_DATE, "")
        if (lastClaimDate?.isEmpty() != false) return null
        
        try {
            val lastClaim = DATE_FORMAT.parse(lastClaimDate)
            val now = Date()
            val daysSinceLastClaim = (now.time - lastClaim!!.time) / (1000 * 60 * 60 * 24)
            
            if (daysSinceLastClaim >= 3) {
                return DailyBonus(
                    day = -1, // Special indicator for comeback bonus
                    coins = 500,
                    extraLives = 3,
                    powerUps = listOf("shield", "magnet"),
                    title = "Welcome Back! 🎉",
                    description = "We missed you! Here's a special comeback bonus!"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating comeback bonus: ${e.message}")
        }
        
        return null
    }
    
    /**
     * Get hours until next bonus (if streak is maintained)
     */
    fun getHoursUntilNextBonus(): Int {
        if (isDailyBonusAvailable()) return 0
        
        val lastClaimDate = prefs.getString(KEY_LAST_CLAIM_DATE, "")
        if (lastClaimDate?.isEmpty() != false) return 0
        
        try {
            val lastClaim = DATE_FORMAT.parse(lastClaimDate)
            val tomorrow = Calendar.getInstance().apply {
                time = lastClaim!!
                add(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            
            val now = Date()
            val hoursUntilNext = ((tomorrow.timeInMillis - now.time) / (1000 * 60 * 60)).toInt()
            return maxOf(0, hoursUntilNext)
        } catch (e: Exception) {
            return 0
        }
    }
    
    /**
     * Reset daily bonus data (for testing)
     */
    fun resetDailyBonusData() {
        prefs.edit().clear().apply()
        Log.d(TAG, "Daily bonus data reset")
    }
    
    /**
     * Get today's date as string
     */
    private fun getTodayString(dayOffset: Int = 0): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, dayOffset)
        return DATE_FORMAT.format(calendar.time)
    }
}

/**
 * Daily bonus reward data
 */
data class DailyBonus(
    val day: Int,
    val coins: Int,
    val extraLives: Int,
    val powerUps: List<String>,
    val title: String,
    val description: String
) {
    val totalValue: Int
        get() = coins + (extraLives * 100) + (powerUps.size * 50)
}

/**
 * Daily bonus statistics
 */
data class DailyBonusStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalClaims: Int,
    val daysSinceFirstLogin: Int,
    val bonusAvailableToday: Boolean
)