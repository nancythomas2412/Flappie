package com.flappie.game

import android.graphics.Canvas

/**
 * GameUpdatable - Interface for game views that can be updated and drawn
 * Allows GameThread to work with different game view implementations
 */
interface GameUpdatable {
    /**
     * Update game logic with delta time for frame-rate independent physics
     * @param deltaTime Time elapsed since last frame in seconds
     */
    fun update(deltaTime: Float = 1f/60f)

    /**
     * Legacy update method for compatibility
     */
    fun update() = update(1f/60f)

    /**
     * Draw the game to canvas
     */
    fun draw(canvas: Canvas)
}