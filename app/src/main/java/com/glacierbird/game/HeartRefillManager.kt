package com.glacierbird.game

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * HeartRefillManager - Manages the heart refill timer system
 * Hearts refill every 30 minutes when depleted, up to maximum of 3 hearts
 */
class HeartRefillManager(private val context: Context) {
    
    companion object {
        private const val TAG = "HeartRefillManager"
        private const val PREFS_NAME = "heart_refill_prefs"
        private const val KEY_LAST_REFILL_TIME = "last_refill_time"
        private const val KEY_HEARTS_WHEN_SAVED = "hearts_when_saved"
        private const val REFILL_INTERVAL_MS = 2 * 60 * 1000L // 2 minutes in milliseconds
        private const val MAX_HEARTS = 3
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Check and update hearts based on elapsed time since last refill
     * Returns the updated heart count
     */
    fun updateHearts(currentHearts: Int): Int {
        Log.d("HeartRefillDebug", "=== UPDATE HEARTS START ===")
        Log.d("HeartRefillDebug", "Input currentHearts: $currentHearts")
        
        if (currentHearts >= MAX_HEARTS) {
            // All hearts are full, clear any saved refill data
            Log.d("HeartRefillDebug", "Hearts already full ($currentHearts >= $MAX_HEARTS), clearing refill data")
            clearRefillData()
            return currentHearts
        }
        
        val currentTime = System.currentTimeMillis()
        val lastRefillTime = prefs.getLong(KEY_LAST_REFILL_TIME, 0L)
        val savedHearts = prefs.getInt(KEY_HEARTS_WHEN_SAVED, currentHearts)
        
        Log.d("HeartRefillDebug", "lastRefillTime: $lastRefillTime, savedHearts: $savedHearts")
        
        if (lastRefillTime == 0L) {
            // No previous refill data, start timer now
            Log.d("HeartRefillDebug", "No previous refill data, starting timer with $currentHearts hearts")
            startRefillTimer(currentHearts)
            return currentHearts
        }
        
        val elapsedTime = currentTime - lastRefillTime
        val heartsToAdd = (elapsedTime / REFILL_INTERVAL_MS).toInt()
        
        Log.d("HeartRefillDebug", "elapsedTime: ${elapsedTime/1000}s, heartsToAdd: $heartsToAdd")
        
        if (heartsToAdd > 0) {
            val newHearts = minOf(savedHearts + heartsToAdd, MAX_HEARTS)
            Log.d("HeartRefillDebug", "REFILLING HEARTS: $savedHearts + $heartsToAdd = $newHearts")
            Log.d(TAG, "Hearts refilled: $savedHearts -> $newHearts (elapsed: ${elapsedTime / 1000}s)")
            
            if (newHearts >= MAX_HEARTS) {
                Log.d("HeartRefillDebug", "Hearts now full, clearing refill data")
                clearRefillData()
            } else {
                // Update the saved time to account for the refills we just gave
                val newLastRefillTime = lastRefillTime + (heartsToAdd * REFILL_INTERVAL_MS)
                Log.d("HeartRefillDebug", "Updating refill time: $lastRefillTime -> $newLastRefillTime")
                prefs.edit()
                    .putLong(KEY_LAST_REFILL_TIME, newLastRefillTime)
                    .putInt(KEY_HEARTS_WHEN_SAVED, newHearts)
                    .apply()
            }
            
            Log.d("HeartRefillDebug", "Returning newHearts: $newHearts")
            return newHearts
        }
        
        Log.d("HeartRefillDebug", "No refill needed, returning currentHearts: $currentHearts")
        return currentHearts
    }
    
    /**
     * Start the refill timer when hearts are depleted
     */
    fun startRefillTimer(currentHearts: Int) {
        if (currentHearts >= MAX_HEARTS) return
        
        val currentTime = System.currentTimeMillis()
        prefs.edit()
            .putLong(KEY_LAST_REFILL_TIME, currentTime)
            .putInt(KEY_HEARTS_WHEN_SAVED, currentHearts)
            .apply()
        
        Log.d(TAG, "Started refill timer with $currentHearts hearts")
    }
    
    /**
     * Get the remaining time until next heart refill in milliseconds
     * Returns 0 if timer is not active or hearts are full
     */
    fun getTimeUntilNextRefill(currentHearts: Int): Long {
        if (currentHearts >= MAX_HEARTS) return 0L
        
        val lastRefillTime = prefs.getLong(KEY_LAST_REFILL_TIME, 0L)
        if (lastRefillTime == 0L) return 0L
        
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastRefillTime
        val timeForNextRefill = REFILL_INTERVAL_MS - (elapsedTime % REFILL_INTERVAL_MS)
        
        return if (timeForNextRefill == REFILL_INTERVAL_MS) 0L else timeForNextRefill
    }
    
    /**
     * Format remaining time as mm:ss string
     */
    fun formatTimeRemaining(timeMs: Long): String {
        if (timeMs <= 0) return "00:00"
        
        val totalSeconds = (timeMs / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    /**
     * Check if refill timer is active
     */
    fun isRefillTimerActive(currentHearts: Int): Boolean {
        return currentHearts < MAX_HEARTS && prefs.getLong(KEY_LAST_REFILL_TIME, 0L) > 0L
    }
    
    /**
     * Clear all refill data (when hearts are full)
     */
    private fun clearRefillData() {
        prefs.edit()
            .remove(KEY_LAST_REFILL_TIME)
            .remove(KEY_HEARTS_WHEN_SAVED)
            .apply()
        Log.d(TAG, "Cleared refill data - hearts are full")
    }
    
    /**
     * Reset the refill timer (for testing or administrative purposes)
     */
    fun resetRefillTimer() {
        clearRefillData()
        Log.d(TAG, "Refill timer reset")
    }
}