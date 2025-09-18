package com.flappie.game

/**
 * Achievement - Represents a single achievement that can be unlocked
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String, // Emoji icon
    val targetValue: Int,
    val rewardCoins: Int,
    val type: AchievementType,
    var progress: Int = 0,
    var isUnlocked: Boolean = false,
    var isNewlyUnlocked: Boolean = false // For showing notifications
) {
    val progressPercentage: Float
        get() = (progress.toFloat() / targetValue.toFloat()).coerceIn(0f, 1f)
    
    val isCompleted: Boolean
        get() = progress >= targetValue
        
    fun updateProgress(newProgress: Int): Boolean {
        val wasCompleted = isCompleted
        progress = newProgress.coerceAtMost(targetValue)
        
        if (!wasCompleted && isCompleted && !isUnlocked) {
            isUnlocked = true
            isNewlyUnlocked = true
            return true // Achievement unlocked
        }
        return false
    }
    
    fun addProgress(amount: Int): Boolean {
        return updateProgress(progress + amount)
    }
}

/**
 * AchievementType - Categories of achievements
 */
enum class AchievementType {
    SCORE,          // Score-based achievements
    SURVIVAL,       // Time/distance survival
    COLLECTION,     // Coin/collectible gathering
    POWER_UP,       // Power-up usage
    GAMEPLAY,       // General gameplay milestones
    SOCIAL,         // Social interactions
    PROGRESSION     // Meta progression
}

/**
 * Achievement definitions - All available achievements in the game
 */
object Achievements {
    
    fun getAllAchievements(): List<Achievement> = listOf(
        // SCORE ACHIEVEMENTS
        Achievement(
            id = "first_flight",
            title = "First Flight",
            description = "Score your first point",
            icon = "🐣",
            targetValue = 1,
            rewardCoins = 25,
            type = AchievementType.SCORE
        ),
        Achievement(
            id = "century",
            title = "Century Club",
            description = "Score 100 points in a single run",
            icon = "💯",
            targetValue = 100,
            rewardCoins = 100,
            type = AchievementType.SCORE
        ),
        Achievement(
            id = "high_flyer",
            title = "High Flyer",
            description = "Score 500 points in a single run",
            icon = "🦅",
            targetValue = 500,
            rewardCoins = 250,
            type = AchievementType.SCORE
        ),
        Achievement(
            id = "ace_pilot",
            title = "Ace Pilot",
            description = "Score 1000 points in a single run",
            icon = "🏆",
            targetValue = 1000,
            rewardCoins = 500,
            type = AchievementType.SCORE
        ),
        
        // COLLECTION ACHIEVEMENTS
        Achievement(
            id = "coin_collector",
            title = "Coin Collector",
            description = "Collect 100 coins total",
            icon = "🪙",
            targetValue = 100,
            rewardCoins = 50,
            type = AchievementType.COLLECTION
        ),
        Achievement(
            id = "treasure_hunter",
            title = "Treasure Hunter",
            description = "Collect 500 coins total",
            icon = "💰",
            targetValue = 500,
            rewardCoins = 150,
            type = AchievementType.COLLECTION
        ),
        Achievement(
            id = "diamond_collector",
            title = "Diamond Collector",
            description = "Collect 10 diamond coins",
            icon = "💎",
            targetValue = 10,
            rewardCoins = 200,
            type = AchievementType.COLLECTION
        ),
        
        // POWER-UP ACHIEVEMENTS
        Achievement(
            id = "power_user",
            title = "Power User",
            description = "Use 20 power-ups",
            icon = "⚡",
            targetValue = 20,
            rewardCoins = 75,
            type = AchievementType.POWER_UP
        ),
        Achievement(
            id = "shield_master",
            title = "Shield Master",
            description = "Use shield 10 times",
            icon = "🛡️",
            targetValue = 10,
            rewardCoins = 100,
            type = AchievementType.POWER_UP
        ),
        Achievement(
            id = "time_manipulator",
            title = "Time Manipulator",
            description = "Use slow motion 15 times",
            icon = "⏰",
            targetValue = 15,
            rewardCoins = 100,
            type = AchievementType.POWER_UP
        ),
        
        // SURVIVAL ACHIEVEMENTS
        Achievement(
            id = "survivor",
            title = "Survivor",
            description = "Play for 5 minutes total",
            icon = "⏱️",
            targetValue = 300, // 5 minutes in seconds
            rewardCoins = 100,
            type = AchievementType.SURVIVAL
        ),
        Achievement(
            id = "marathon_flyer",
            title = "Marathon Flyer",
            description = "Play for 30 minutes total",
            icon = "🏃",
            targetValue = 1800, // 30 minutes in seconds
            rewardCoins = 300,
            type = AchievementType.SURVIVAL
        ),
        
        // GAMEPLAY ACHIEVEMENTS
        Achievement(
            id = "persistent",
            title = "Persistent",
            description = "Play 50 games",
            icon = "🎮",
            targetValue = 50,
            rewardCoins = 150,
            type = AchievementType.GAMEPLAY
        ),
        Achievement(
            id = "night_owl",
            title = "Night Owl",
            description = "Reach the night time (Score 650+)",
            icon = "🌙",
            targetValue = 650,
            rewardCoins = 200,
            type = AchievementType.SCORE
        ),
        Achievement(
            id = "shopaholic",
            title = "Shopaholic",
            description = "Buy 5 lives from the shop",
            icon = "🛒",
            targetValue = 5,
            rewardCoins = 125,
            type = AchievementType.PROGRESSION
        ),
        
        // PROGRESSION ACHIEVEMENTS
        Achievement(
            id = "rich_bird",
            title = "Rich Bird",
            description = "Accumulate 2000 coins",
            icon = "🤑",
            targetValue = 2000,
            rewardCoins = 300,
            type = AchievementType.COLLECTION
        ),
        Achievement(
            id = "comeback_king",
            title = "Comeback King",
            description = "Continue 10 times using heart refills",
            icon = "💪",
            targetValue = 10,
            rewardCoins = 250,
            type = AchievementType.PROGRESSION
        )
    )
}