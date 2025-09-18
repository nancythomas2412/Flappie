package com.flappie.game

/**
 * PowerUpStates - Container for active power-up information
 * Separated from UIRenderer to avoid circular import issues
 */
data class PowerUpStates(
    val shieldActive: Boolean = false,
    val shieldTimer: Int = 0,
    val slowMotionActive: Boolean = false,
    val slowMotionTimer: Int = 0,
    val scoreMultiplierActive: Boolean = false,
    val scoreMultiplierTimer: Int = 0,
    val magnetActive: Boolean = false,
    val magnetTimer: Int = 0,
    val shieldUsesThisGame: Int = 0,
    val magnetUsesThisGame: Int = 0,
    val slowMotionUsesThisGame: Int = 0
)