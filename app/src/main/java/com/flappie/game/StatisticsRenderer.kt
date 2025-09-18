package com.flappie.game

import android.graphics.*
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withClip

/**
 * StatisticsRenderer - Beautiful statistics screen with comprehensive player analytics
 * Essential for player engagement and retention
 */
class StatisticsRenderer(
    private val screenWidth: Int,
    private val screenHeight: Int
) {
    
    // Background and panels
    private val overlayPaint = Paint().apply {
        color = "#F0000000".toColorInt() // Slightly transparent overlay
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val panelPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
        setShadowLayer(15f, 0f, 8f, "#40000000".toColorInt())
    }
    
    // Text paints (lazy initialization for proper density-aware sizing)
    private val titlePaint = Paint().apply {
        color = "#2C3E50".toColorInt()
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val sectionTitlePaint = Paint().apply {
        color = "#34495E".toColorInt()
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
    }

    private val statLabelPaint = Paint().apply {
        color = "#5D6D7E".toColorInt()
        typeface = Typeface.DEFAULT
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
    }

    private val statValuePaint = Paint().apply {
        color = "#2C3E50".toColorInt()
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.RIGHT
        isAntiAlias = true
    }

    // Highlight paints for important stats
    private val highScorePaint = Paint().apply {
        color = "#E74C3C".toColorInt()
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.RIGHT
        isAntiAlias = true
    }

    private val achievementPaint = Paint().apply {
        color = "#F39C12".toColorInt()
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.RIGHT
        isAntiAlias = true
    }

    private fun ensureTextSizesSet() {
        // Set compact text sizes for statistics screen
        titlePaint.textSize = DensityUtils.UI.getTextTitle()
        sectionTitlePaint.textSize = DensityUtils.UI.getStatsTextLarge()
        statLabelPaint.textSize = DensityUtils.UI.getStatsTextMedium()
        statValuePaint.textSize = DensityUtils.UI.getStatsTextLarge()
        highScorePaint.textSize = DensityUtils.UI.getStatsTextLarge()
        achievementPaint.textSize = DensityUtils.UI.getStatsTextLarge()
    }

    private val sectionBackgroundPaint = Paint().apply {
        color = "#F8F9FA".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    
    
    /**
     * Draw complete statistics screen
     */
    fun drawStatisticsScreen(
        canvas: Canvas,
        stats: GameStatistics,
        @Suppress("UNUSED_PARAMETER") metrics: PerformanceMetrics,
        scrollOffset: Float = 0f
    ) {
        // Ensure text sizes are properly set
        ensureTextSizesSet()

        // Overlay
        canvas.drawColor(overlayPaint.color)
        
        // Main panel
        val panelWidth = screenWidth * 0.95f
        val panelHeight = screenHeight * 0.9f
        val panelX = (screenWidth - panelWidth) / 2f
        val panelY = (screenHeight - panelHeight) / 2f
        
        val panelRect = RectF(panelX, panelY, panelX + panelWidth, panelY + panelHeight)
        canvas.drawRoundRect(panelRect, 20f, 20f, panelPaint)
        
        // Title - positioned more towards center with better spacing
        val titleY = panelY + panelHeight * 0.15f // 15% from top of panel
        canvas.drawText("📊 Statistics", screenWidth / 2f, titleY, titlePaint)


        // Content area with proper spacing and centering
        val contentStartY = titleY + 60f // More space after title
        val contentWidth = panelWidth - 60f // More side padding
        val contentX = panelX + 30f
        
        // Reserve space for close button at bottom
        val buttonSize = 180f // MUCH LARGER button for easier tapping
        val bottomButtonSpace = buttonSize + 120f // Much more space for button moved way up

        // Apply scroll offset - leave space for close button at bottom
        canvas.withClip(panelX, contentStartY, panelX + panelWidth, panelY + panelHeight - bottomButtonSpace) {
            val currentY = contentStartY + scrollOffset

            // Draw everything in one compact section instead of multiple sections
            drawCompactStats(this, stats, contentX, currentY, contentWidth)
        }

        // Draw close button at bottom center, outside the clipped content area - moved WAY up
        val closeButtonX = screenWidth / 2f - buttonSize / 2f // Center horizontally
        val closeButtonY = panelY + panelHeight - buttonSize - 120f // WAY higher from bottom
        android.util.Log.e("FLAPPIE_DEBUG", "📐 RENDERER CALC: screen=${screenWidth}x$screenHeight, panel=${panelWidth}x$panelHeight at ($panelX,$panelY)")
        android.util.Log.e("FLAPPIE_DEBUG", "📐 RENDERER BUTTON: ($closeButtonX, $closeButtonY) size=${buttonSize}x$buttonSize")
        drawCloseButton(canvas, closeButtonX, closeButtonY)
    }
    
    /**
     * Draw comprehensive statistics in organized sections
     */
    private fun drawCompactStats(canvas: Canvas, stats: GameStatistics, x: Float, startY: Float, width: Float): Float {
        var currentY = startY
        val sectionSpacing = 50f // Much more space between sections to prevent overlap

        // Game Overview Section
        currentY = drawStatsSection(canvas, "🏆 Game Overview", listOf(
            "High Score" to Pair(formatNumber(stats.highScore), highScorePaint),
            "Games Played" to Pair(formatNumber(stats.gamesPlayed), statValuePaint),
            "Average Score" to Pair(formatNumber(stats.averageScore), statValuePaint),
            "Best Win Streak" to Pair(formatNumber(stats.longestWinStreak), achievementPaint)
        ), x, currentY, width)

        currentY += sectionSpacing

        // Collection & Progress Section
        currentY = drawStatsSection(canvas, "💰 Collection & Progress", listOf(
            "Total Coins" to Pair(formatNumber(stats.totalCoinsCollected), achievementPaint),
            "Pipes Passed" to Pair(formatNumber(stats.totalPipesPassed), statValuePaint),
            "Power-ups Used" to Pair(formatNumber(stats.totalPowerUpsUsed), statValuePaint),
            "Play Time" to Pair(formatPlaytime(stats.totalPlaytimeSeconds / 60), statValuePaint)
        ), x, currentY, width)

        currentY += sectionSpacing

        // Detailed Breakdown Section
        currentY = drawStatsSection(canvas, "📊 Detailed Stats", listOf(
            "Shield Uses" to Pair(formatNumber(stats.shieldUses), statValuePaint),
            "Magnet Uses" to Pair(formatNumber(stats.magnetUses), statValuePaint),
            "Slow Motion Uses" to Pair(formatNumber(stats.slowMotionUses), statValuePaint),
            "Current Streak" to Pair("${stats.currentWinStreak} games", if (stats.currentWinStreak > 0) achievementPaint else statValuePaint)
        ), x, currentY, width)

        return currentY
    }

    /**
     * Draw a statistics section with title and items - dramatically improved spacing
     */
    private fun drawStatsSection(
        canvas: Canvas,
        title: String,
        items: List<Pair<String, Pair<String, Paint>>>,
        x: Float,
        startY: Float,
        width: Float
    ): Float {
        var currentY = startY
        val padding = 50f // Even larger padding for more breathing room
        val itemHeight = 65f // Much more space between each line of content
        val titleSpacing = 85f // Even more space after title
        val topBottomPadding = 45f // More padding at top and bottom for better visual separation

        // Section background with much better proportions
        val sectionHeight = titleSpacing + (items.size * itemHeight) + topBottomPadding + 20f
        val sectionRect = RectF(x, currentY, x + width, currentY + sectionHeight)
        canvas.drawRoundRect(sectionRect, 15f, 15f, sectionBackgroundPaint)

        currentY += topBottomPadding // Much more top padding within section

        // Section title with much more space
        canvas.drawText(title, x + padding, currentY + 30f, sectionTitlePaint)
        currentY += titleSpacing

        // Draw items with dramatically more spacing
        items.forEach { (label, valuePair) ->
            val (value, paint) = valuePair
            // Left-aligned label with much more margin from edges
            canvas.drawText(label, x + padding + 15f, currentY, statLabelPaint)
            // Right-aligned value with much more margin from edges
            canvas.drawText(value, x + width - padding - 15f, currentY, paint)
            currentY += itemHeight
        }

        return startY + sectionHeight
    }
    
    /**
     * Draw close button
     */
    private fun drawCloseButton(canvas: Canvas, x: Float, y: Float) {
        val buttonSize = 180f // Keep size for positioning but no background

        // Draw instruction text only - no background
        canvas.drawText("TAP ANYWHERE TO CLOSE", x + buttonSize/2f, y + buttonSize/2f + buttonSize*0.1f, Paint().apply {
            color = Color.BLACK
            textSize = buttonSize * 0.12f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        })
    }

    /**
     * Format numbers for display
     */
    private fun formatNumber(number: Int): String {
        return when {
            number >= 1_000_000 -> "${number / 1_000_000}.${(number % 1_000_000) / 100_000}M"
            number >= 1_000 -> "${number / 1_000}.${(number % 1_000) / 100}K"
            else -> number.toString()
        }
    }
    
    private fun formatPlaytime(minutes: Long): String {
        return when {
            minutes >= 1440 -> "${minutes / 1440}d ${(minutes % 1440) / 60}h" // days and hours
            minutes >= 60 -> "${minutes / 60}h ${minutes % 60}m" // hours and minutes
            else -> "${minutes}m" // just minutes
        }
    }
}