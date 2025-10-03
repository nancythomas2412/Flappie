package com.glacierbird.game

import android.graphics.RectF
import kotlin.math.sqrt

/**
 * CollisionUtils - Centralized collision detection utilities
 * Provides common collision detection methods used throughout the game
 */
object CollisionUtils {
    
    /**
     * Check collision between two rectangular bounds
     * @param bounds1 First rectangle bounds [left, top, right, bottom]
     * @param bounds2 Second rectangle bounds [left, top, right, bottom]
     * @return true if rectangles intersect
     */
    fun checkRectangleCollision(bounds1: FloatArray, bounds2: FloatArray): Boolean {
        return !(bounds1[2] < bounds2[0] ||  // bounds1.right < bounds2.left
                bounds1[0] > bounds2[2] ||   // bounds1.left > bounds2.right
                bounds1[3] < bounds2[1] ||   // bounds1.bottom < bounds2.top
                bounds1[1] > bounds2[3])     // bounds1.top > bounds2.bottom
    }
    
    /**
     * Check collision between two rectangular bounds with forgiveness margin
     * @param bounds1 First rectangle bounds [left, top, right, bottom]
     * @param bounds2 Second rectangle bounds [left, top, right, bottom]
     * @param margin Margin to reduce collision box size (makes collision more forgiving)
     * @return true if rectangles intersect
     */
    fun checkRectangleCollisionWithMargin(
        bounds1: FloatArray, 
        bounds2: FloatArray, 
        margin: Float = GameConstants.PIPE_COLLISION_FORGIVENESS
    ): Boolean {
        val adjustedBounds1 = floatArrayOf(
            bounds1[0] + margin,  // left + margin
            bounds1[1] + margin,  // top + margin
            bounds1[2] - margin,  // right - margin
            bounds1[3] - margin   // bottom - margin
        )
        
        return checkRectangleCollision(adjustedBounds1, bounds2)
    }
    
    /**
     * Check collision between a circle and a rectangle
     * @param centerX Circle center X
     * @param centerY Circle center Y
     * @param radius Circle radius
     * @param rectLeft Rectangle left edge
     * @param rectTop Rectangle top edge
     * @param rectRight Rectangle right edge
     * @param rectBottom Rectangle bottom edge
     * @return true if circle intersects rectangle
     */
    fun checkCircleRectangleCollision(
        centerX: Float,
        centerY: Float,
        radius: Float,
        rectLeft: Float,
        rectTop: Float,
        rectRight: Float,
        rectBottom: Float
    ): Boolean {
        // Find the closest point on the rectangle to the circle center
        val closestX = centerX.coerceIn(rectLeft, rectRight)
        val closestY = centerY.coerceIn(rectTop, rectBottom)
        
        // Calculate distance from circle center to closest point
        val deltaX = centerX - closestX
        val deltaY = centerY - closestY
        val distanceSquared = deltaX * deltaX + deltaY * deltaY
        
        return distanceSquared <= radius * radius
    }
    
    /**
     * Check collision between two circles
     * @param x1 First circle center X
     * @param y1 First circle center Y
     * @param radius1 First circle radius
     * @param x2 Second circle center X
     * @param y2 Second circle center Y
     * @param radius2 Second circle radius
     * @return true if circles intersect
     */
    fun checkCircleCollision(
        x1: Float, y1: Float, radius1: Float,
        x2: Float, y2: Float, radius2: Float
    ): Boolean {
        val deltaX = x1 - x2
        val deltaY = y1 - y2
        val distanceSquared = deltaX * deltaX + deltaY * deltaY
        val radiusSum = radius1 + radius2
        
        return distanceSquared <= radiusSum * radiusSum
    }
    
    /**
     * Check if a point is inside a rectangle
     * @param x Point X coordinate
     * @param y Point Y coordinate
     * @param rect Rectangle bounds
     * @return true if point is inside rectangle
     */
    fun isPointInRectangle(x: Float, y: Float, rect: RectF): Boolean {
        return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom
    }
    
    /**
     * Check if a point is inside a circle
     * @param pointX Point X coordinate
     * @param pointY Point Y coordinate
     * @param centerX Circle center X
     * @param centerY Circle center Y
     * @param radius Circle radius
     * @return true if point is inside circle
     */
    fun isPointInCircle(
        pointX: Float, 
        pointY: Float, 
        centerX: Float, 
        centerY: Float, 
        radius: Float
    ): Boolean {
        val deltaX = pointX - centerX
        val deltaY = pointY - centerY
        val distanceSquared = deltaX * deltaX + deltaY * deltaY
        
        return distanceSquared <= radius * radius
    }
    
    /**
     * Calculate distance between two points
     * @param x1 First point X
     * @param y1 First point Y
     * @param x2 Second point X
     * @param y2 Second point Y
     * @return Distance between points
     */
    fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val deltaX = x1 - x2
        val deltaY = y1 - y2
        return sqrt(deltaX * deltaX + deltaY * deltaY)
    }
    
    /**
     * Check if bird collides with pipe (specialized collision for game logic)
     * @param bird The bird object
     * @param pipeX Pipe X position
     * @param pipeWidth Pipe width
     * @param gapTop Y position of gap top
     * @param gapBottom Y position of gap bottom
     * @param screenHeight Screen height for ground collision
     * @return true if bird collides with pipe
     */
    fun checkBirdPipeCollision(
        bird: Bird,
        pipeX: Float,
        pipeWidth: Float,
        gapTop: Float,
        gapBottom: Float,
        @Suppress("UNUSED_PARAMETER") screenHeight: Int
    ): Boolean {
        val birdBounds = bird.getBounds()
        
        // Check if bird is in pipe's horizontal range
        if (birdBounds[2] > pipeX && birdBounds[0] < pipeX + pipeWidth) {
            // Check collision with top pipe (including cap)
            if (birdBounds[1] < gapTop) {
                return true
            }
            // Check collision with bottom pipe (including cap)
            if (birdBounds[3] > gapBottom) {
                return true
            }
        }
        
        return false
    }
    
    /**
     * Check if bird collides with collectible (power-up or coin)
     * @param bird The bird object
     * @param collectibleX Collectible X position
     * @param collectibleY Collectible Y position
     * @param collectibleSize Size of the collectible
     * @return true if bird collides with collectible
     */
    fun checkBirdCollectibleCollision(
        bird: Bird,
        collectibleX: Float,
        collectibleY: Float,
        collectibleSize: Float
    ): Boolean {
        val birdBounds = bird.getBounds()
        val distance = collectibleSize * 0.8f  // Slightly smaller collision area
        
        return (collectibleX > birdBounds[0] - distance && 
                collectibleX < birdBounds[2] + distance &&
                collectibleY > birdBounds[1] - distance && 
                collectibleY < birdBounds[3] + distance)
    }
}