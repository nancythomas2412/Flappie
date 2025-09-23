package com.flappie.game

import android.graphics.Canvas
import android.view.SurfaceHolder

/**
 * GameThread - Handles the game loop in a separate thread
 * Provides smooth 60fps gameplay experience
 */
class GameThread(
    private val surfaceHolder: SurfaceHolder,
    private val gameView: GameUpdatable
) : Thread() {

    // Thread control
    private var running = false

    // Target FPS for smooth gameplay
    private val targetFPS = GameConstants.TARGET_FPS
    private val targetTime = GameConstants.TARGET_FRAME_TIME

    // Frame timing for frame-rate independent physics
    private var lastFrameTime = System.currentTimeMillis()
    private var deltaTime = 0f

    /**
     * Set whether the thread should continue running
     */
    fun setRunning(isRunning: Boolean) {
        running = isRunning
    }

    /**
     * Check if thread is currently running
     */
    fun isRunning(): Boolean = running

    /**
     * Main game loop - runs continuously while game is active
     */
    override fun run() {
        var canvas: Canvas?

        while (running) {
            val startTime = System.currentTimeMillis()
            canvas = null

            // Calculate delta time for frame-rate independent physics
            deltaTime = (startTime - lastFrameTime) / 1000f
            deltaTime = deltaTime.coerceIn(0f, 0.05f) // Cap at 50ms to prevent large jumps
            lastFrameTime = startTime

            try {
                // Lock the canvas for drawing
                canvas = surfaceHolder.lockCanvas()

                if (canvas != null) {
                    synchronized(surfaceHolder) {
                        // Update game logic with delta time
                        gameView.update(deltaTime)

                        // Draw everything to canvas
                        gameView.draw(canvas)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // Always unlock canvas when done
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // Control frame rate
            val frameTime = System.currentTimeMillis() - startTime
            if (frameTime < targetTime) {
                try {
                    sleep(targetTime - frameTime)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
    }
}