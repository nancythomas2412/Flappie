package com.flappie.game

import android.graphics.Canvas

/**
 * GameUpdatable - Interface for game views that can be updated and drawn
 * Allows GameThread to work with different game view implementations
 */
interface GameUpdatable {
    /**
     * Update game logic
     */
    fun update()
    
    /**
     * Draw the game to canvas
     */
    fun draw(canvas: Canvas)
}