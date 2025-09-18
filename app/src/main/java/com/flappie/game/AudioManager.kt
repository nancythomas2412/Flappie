package com.flappie.game

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log

/**
 * AudioManager - Centralized audio management system
 * Handles sound effects and background music with proper error handling
 */
class AudioManager(
    private val context: Context,
    private val resourceManager: ResourceManager
) {
    
    companion object {
        private const val TAG = "AudioManager"
    }
    
    // Audio components
    private var soundPool: SoundPool? = null
    private var backgroundMusic: MediaPlayer? = null
    
    // Sound effect IDs - resource IDs from ResourceManager
    private var resourceIds: SoundEffectIds? = null
    
    // Loaded sound pool IDs - different from resource IDs
    private var loadedSoundIds: LoadedSoundIds? = null
    private var backgroundMusicId: Int = 0
    
    // Audio settings
    private var soundEnabled = true
    private var musicEnabled = true
    
    // Volume settings
    private val soundVolume = GameConstants.SOUND_VOLUME
    private val scoreVolume = GameConstants.SCORE_VOLUME
    private val musicVolume = GameConstants.MUSIC_VOLUME
    
    /**
     * Initialize audio system with fallback for missing assets
     */
    fun initialize() {
        try {
            initializeSoundPool()
            initializeBackgroundMusic()
            Log.d(TAG, "Audio system initialized successfully")
        } catch (e: Exception) {
            Log.w(TAG, "Audio initialization failed - running in silent mode: ${e.message}")
            // Game continues to function without audio
        }
    }
    
    /**
     * Initialize SoundPool for sound effects
     */
    private fun initializeSoundPool() {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            
            soundPool = SoundPool.Builder()
                .setMaxStreams(GameConstants.MAX_AUDIO_STREAMS)
                .setAudioAttributes(audioAttributes)
                .build()
            
            // Load sound effects
            resourceIds = resourceManager.loadSoundEffectIds()
            loadSoundEffects()
            
            Log.d(TAG, "SoundPool initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize SoundPool: ${e.message}", e)
        }
    }
    
    /**
     * Load sound effects into SoundPool with graceful fallback
     */
    private fun loadSoundEffects() {
        soundPool?.let { pool ->
            resourceIds?.let { ids ->
                try {
                    // Load sounds and store the loaded IDs (0 if resource doesn't exist)
                    loadedSoundIds = LoadedSoundIds(
                        jump = loadSoundSafely(pool, ids.jump),
                        score = loadSoundSafely(pool, ids.score),
                        loseLife = loadSoundSafely(pool, ids.loseLife),
                        gameOver = loadSoundSafely(pool, ids.gameOver),
                        powerUp = loadSoundSafely(pool, ids.powerUp),
                        coin = loadSoundSafely(pool, ids.coin)
                    )
                    
                    Log.d(TAG, "Sound effects loaded (with fallbacks for missing assets)")
                } catch (e: Exception) {
                    Log.w(TAG, "Some sound effects failed to load - continuing without audio: ${e.message}")
                    // Create empty sound IDs so game continues to work
                    loadedSoundIds = LoadedSoundIds(0, 0, 0, 0, 0, 0)
                }
            } ?: run {
                Log.w(TAG, "No sound resource IDs available - running in silent mode")
                loadedSoundIds = LoadedSoundIds(0, 0, 0, 0, 0, 0)
            }
        }
    }
    
    /**
     * Safely load a sound resource with fallback
     */
    private fun loadSoundSafely(pool: SoundPool, resourceId: Int): Int {
        return try {
            if (resourceId != 0) {
                pool.load(context, resourceId, 1)
            } else {
                0 // No resource available
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load sound resource $resourceId: ${e.message}")
            0 // Return 0 to indicate no sound loaded
        }
    }
    
    /**
     * Initialize background music
     */
    private fun initializeBackgroundMusic() {
        try {
            backgroundMusicId = resourceManager.loadBackgroundMusicId()
            
            if (backgroundMusicId != 0) {
                backgroundMusic = MediaPlayer.create(context, backgroundMusicId)
                backgroundMusic?.let { music ->
                    music.isLooping = true
                    music.setVolume(musicVolume, musicVolume)
                    Log.d(TAG, "Background music initialized successfully")
                }
            } else {
                Log.w(TAG, "Background music resource not found")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize background music: ${e.message}", e)
        }
    }
    
    /**
     * Play a sound effect
     */
    fun playSound(soundType: SoundType) {
        if (!soundEnabled) {
            return
        }
        
        val soundId = getSoundId(soundType)
        if (soundId != 0) {
            val volume = if (soundType == SoundType.SCORE) scoreVolume else soundVolume
            
            try {
                soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play sound ${soundType.name}: ${e.message}", e)
            }
        }
    }
    
    /**
     * Get sound ID for a specific sound type
     */
    private fun getSoundId(soundType: SoundType): Int {
        return loadedSoundIds?.let { ids ->
            when (soundType) {
                SoundType.JUMP -> ids.jump
                SoundType.SCORE -> ids.score
                SoundType.LOSE_LIFE -> ids.loseLife
                SoundType.GAME_OVER -> ids.gameOver
                SoundType.POWER_UP -> ids.powerUp
                SoundType.COIN -> ids.coin
            }
        } ?: 0
    }
    
    /**
     * Start background music
     */
    fun startBackgroundMusic() {
        if (!musicEnabled) {
            return
        }
        
        backgroundMusic?.let { music ->
            try {
                if (!music.isPlaying) {
                    music.start()
                    Log.d(TAG, "Background music started")
                } else {
                    // Music is already playing
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start background music: ${e.message}", e)
            }
        }
    }
    
    /**
     * Pause background music
     */
    fun pauseBackgroundMusic() {
        backgroundMusic?.let { music ->
            try {
                if (music.isPlaying) {
                    music.pause()
                    Log.d(TAG, "Background music paused")
                } else {
                    // Music is not playing
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to pause background music: ${e.message}", e)
            }
        }
    }
    
    /**
     * Stop background music
     */
    fun stopBackgroundMusic() {
        backgroundMusic?.let { music ->
            try {
                if (music.isPlaying) {
                    music.stop()
                    music.prepare()
                    Log.d(TAG, "Background music stopped")
                } else {
                    // Music is not playing
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop background music: ${e.message}", e)
            }
        }
    }
    
    /**
     * Update audio settings
     */
    fun updateAudioSettings(soundEnabled: Boolean, musicEnabled: Boolean) {
        this.soundEnabled = soundEnabled
        this.musicEnabled = musicEnabled
        
        if (!musicEnabled) {
            pauseBackgroundMusic()
        } else {
            startBackgroundMusic()
        }
        
        Log.d(TAG, "Audio settings updated - Sound: $soundEnabled, Music: $musicEnabled")
    }
    
    /**
     * Cleanup audio resources
     */
    fun cleanup() {
        try {
            // Release SoundPool
            soundPool?.release()
            soundPool = null
            
            // Release MediaPlayer
            backgroundMusic?.let { music ->
                if (music.isPlaying) {
                    music.stop()
                }
                music.release()
            }
            backgroundMusic = null
            
            Log.d(TAG, "Audio resources cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "Error during audio cleanup: ${e.message}", e)
        }
    }
    
    /**
     * Resume audio after pause (recreate MediaPlayer if needed)
     */
    fun resume() {
        // Check if MediaPlayer needs to be recreated
        if (backgroundMusic == null && backgroundMusicId != 0) {
            try {
                backgroundMusic = MediaPlayer.create(context, backgroundMusicId)
                backgroundMusic?.let { music ->
                    music.isLooping = true
                    music.setVolume(musicVolume, musicVolume)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to recreate MediaPlayer on resume: ${e.message}", e)
            }
        }
        
        if (musicEnabled) {
            startBackgroundMusic()
        }
    }
}

/**
 * SoundType enum for different sound effects
 */
enum class SoundType {
    JUMP,
    SCORE,
    LOSE_LIFE,
    GAME_OVER,
    POWER_UP,
    COIN
}

/**
 * LoadedSoundIds - Holds the loaded SoundPool IDs (different from resource IDs)
 */
data class LoadedSoundIds(
    val jump: Int,
    val score: Int,
    val loseLife: Int,
    val gameOver: Int,
    val powerUp: Int,
    val coin: Int
)

