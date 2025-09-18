package com.flappie.game

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
//import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import androidx.core.content.edit

/**
 * Professional SoundManager for Flappie Game
 * Handles all audio playback with proper resource management and user preferences
 */
class SoundManager(private val context: Context) {
    
    companion object {
        private const val TAG = "SoundManager"
        private const val PREFS_NAME = "sound_prefs"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_MUSIC_ENABLED = "music_enabled"
        private const val KEY_SOUND_VOLUME = "sound_volume"
        private const val KEY_MUSIC_VOLUME = "music_volume"
        
        // Sound priorities for interruption handling
        private const val PRIORITY_LOW = 1
        private const val PRIORITY_MEDIUM = 2
        private const val PRIORITY_HIGH = 3
    }
    
    enum class SoundType(val priority: Int) {
        JUMP(PRIORITY_HIGH),
        SCORE(PRIORITY_HIGH),
        COIN(PRIORITY_MEDIUM),
        POWERUP(PRIORITY_MEDIUM),
        LOSE_LIFE(PRIORITY_HIGH),
        GAME_OVER(PRIORITY_HIGH),
        ACHIEVEMENT(PRIORITY_MEDIUM),
        UI_CLICK(PRIORITY_LOW),
        SKIN_UNLOCK(PRIORITY_MEDIUM)
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // SoundPool for short sound effects (recommended for game sounds)
    private var soundPool: SoundPool? = null
    private var backgroundMusic: MediaPlayer? = null
    
    // Sound ID mappings
    private val soundIds = mutableMapOf<SoundType, Int>()
    private val streamIds = mutableMapOf<SoundType, Int>()
    
    // Audio preferences
    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) {
            prefs.edit { putBoolean(KEY_SOUND_ENABLED, value) }
            if (!value) stopAllSounds()
        }
    
    var isMusicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
        set(value) {
            prefs.edit { putBoolean(KEY_MUSIC_ENABLED, value)}
            if (value) {
                startBackgroundMusic()
            } else {
                pauseBackgroundMusic()
            }
        }
    
    var soundVolume: Float
        get() = prefs.getFloat(KEY_SOUND_VOLUME, 1.0f)
        set(value) {
            val clampedValue = value.coerceIn(0f, 1f)
            prefs.edit { putFloat(KEY_SOUND_VOLUME, clampedValue)}
            updateSoundVolume()
        }
    
    var musicVolume: Float
        get() = prefs.getFloat(KEY_MUSIC_VOLUME, 0.128f)
        set(value) {
            val clampedValue = value.coerceIn(0f, 1f)
            prefs.edit { putFloat(KEY_MUSIC_VOLUME, clampedValue) }
            updateMusicVolume()
        }
    
    init {
        initializeSoundSystem()
        loadSounds()
    }
    
    /**
     * Initialize the sound system with proper audio attributes
     */
    private fun initializeSoundSystem() {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            
            soundPool = SoundPool.Builder()
                .setMaxStreams(8) // Allow up to 8 simultaneous sounds
                .setAudioAttributes(audioAttributes)
                .build()
            
            Log.d(TAG, "SoundPool initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize SoundPool: ${e.message}")
        }
    }
    
    /**
     * Load all sound effects into memory
     */
    private fun loadSounds() {
        soundPool?.let { pool ->
            try {
                soundIds[SoundType.JUMP] = pool.load(context, R.raw.jump, 1)
                Log.d(TAG, "Loaded JUMP sound with ID: ${soundIds[SoundType.JUMP]}")
                soundIds[SoundType.SCORE] = pool.load(context, R.raw.score, 1)
                soundIds[SoundType.COIN] = pool.load(context, R.raw.coin, 1)
                soundIds[SoundType.POWERUP] = pool.load(context, R.raw.powerup, 1)
                soundIds[SoundType.LOSE_LIFE] = pool.load(context, R.raw.lose_life, 1)
                soundIds[SoundType.GAME_OVER] = pool.load(context, R.raw.game_over, 1)
                
                // Note: UI_CLICK, ACHIEVEMENT, SKIN_UNLOCK can reuse existing sounds or have dedicated files
                soundIds[SoundType.UI_CLICK] = soundIds[SoundType.COIN] ?: -1
                soundIds[SoundType.ACHIEVEMENT] = soundIds[SoundType.SCORE] ?: -1
                soundIds[SoundType.SKIN_UNLOCK] = soundIds[SoundType.POWERUP] ?: -1
                
                Log.d(TAG, "All sounds loaded successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load sounds: ${e.message}")
            }
        }
    }
    
    /**
     * Play a sound effect with intelligent management
     */
    fun playSound(soundType: SoundType) {
        Log.d(TAG, "playSound called for: $soundType, isSoundEnabled: $isSoundEnabled, soundVolume: $soundVolume")
        if (!isSoundEnabled) {
            Log.w(TAG, "Sound disabled - not playing $soundType")
            return
        }
        
        soundPool?.let { pool ->
            val soundId = soundIds[soundType] ?: run {
                Log.e(TAG, "Sound ID not found for: $soundType")
                return
            }
            Log.d(TAG, "Playing sound $soundType with ID: $soundId")
            
            try {
                // Stop any existing stream of this sound type if it's still playing
                streamIds[soundType]?.let { streamId ->
                    pool.stop(streamId)
                }
                
                // Play the sound with appropriate volume and priority
                // Boost volume for critical gameplay sounds, reduce for UI
                val effectiveVolume = when (soundType) {
                    SoundType.JUMP -> 1.0f // 100% volume for jump (maximum)
                    SoundType.SCORE -> 0.70f // 70% volume for score
                    SoundType.UI_CLICK -> soundVolume * 0.6f // 40% reduction for UI interactions
                    else -> soundVolume
                }

                val streamId = pool.play(
                    soundId,
                    effectiveVolume, // left volume (boosted for important sounds)
                    effectiveVolume, // right volume (boosted for important sounds)
                    soundType.priority,
                    0, // loop (0 = no loop)
                    1.0f // rate (playback speed)
                )
                if (streamId != 0) {
                    streamIds[soundType] = streamId
                    Log.v(TAG, "Playing sound: $soundType")
                } else {
                    Log.w(TAG, "Failed to get valid stream ID for sound: $soundType")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play sound $soundType: ${e.message}")
            }
        }
    }
    
    /**
     * Start background music with looping
     */
    fun startBackgroundMusic() {
        if (!isMusicEnabled) return
        
        try {
            // Stop any existing music
            stopBackgroundMusic()
            
            backgroundMusic = MediaPlayer.create(context, R.raw.background_music).apply {
                isLooping = true
                setVolume(musicVolume, musicVolume)
                
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    true
                }
                
                setOnPreparedListener {
                    start()
                    Log.d(TAG, "Background music started")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start background music: ${e.message}")
        }
    }
    
    /**
     * Pause background music (can be resumed)
     */
    fun pauseBackgroundMusic() {
        try {
            backgroundMusic?.let { music ->
                if (music.isPlaying) {
                    music.pause()
                    Log.d(TAG, "Background music paused")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to pause background music: ${e.message}")
        }
    }
    
    /**
     * Resume background music
     */
    fun resumeBackgroundMusic() {
        if (!isMusicEnabled) return
        
        try {
            backgroundMusic?.let { music ->
                if (!music.isPlaying) {
                    music.start()
                    Log.d(TAG, "Background music resumed")
                }
            } ?: run {
                startBackgroundMusic() // Start if not initialized
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resume background music: ${e.message}")
        }
    }
    
    /**
     * Stop and release background music
     */
    fun stopBackgroundMusic() {
        try {
            backgroundMusic?.let { music ->
                if (music.isPlaying) {
                    music.stop()
                }
                music.release()
                backgroundMusic = null
                Log.d(TAG, "Background music stopped and released")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop background music: ${e.message}")
        }
    }
    
    /**
     * Stop all currently playing sounds
     */
    fun stopAllSounds() {
        try {
            soundPool?.let { pool ->
                streamIds.values.forEach { streamId ->
                    pool.stop(streamId)
                }
                streamIds.clear()
                Log.d(TAG, "All sounds stopped")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop all sounds: ${e.message}")
        }
    }
    
    /**
     * Pause all currently playing sound effects
     */
    fun pauseAllSounds() {
        try {
            soundPool?.let { pool ->
                streamIds.forEach { (_, streamId) ->
                    pool.pause(streamId)
                }
                Log.d(TAG, "All sounds paused")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to pause all sounds: ${e.message}")
        }
    }
    
    /**
     * Resume all paused sound effects
     */
    fun resumeAllSounds() {
        try {
            soundPool?.let { pool ->
                streamIds.forEach { (_, streamId) ->
                    pool.resume(streamId)
                }
                Log.d(TAG, "All sounds resumed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resume all sounds: ${e.message}")
        }
    }
    
    /**
     * Update volume for all currently playing sounds
     */
    private fun updateSoundVolume() {
        try {
            soundPool?.let { pool ->
                streamIds.forEach { (_, streamId) ->
                    pool.setVolume(streamId, soundVolume, soundVolume)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update sound volume: ${e.message}")
        }
    }
    
    /**
     * Update background music volume
     */
    private fun updateMusicVolume() {
        try {
            backgroundMusic?.setVolume(musicVolume, musicVolume)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update music volume: ${e.message}")
        }
    }
    
    /**
     * Handle app resume (called from Activity.onResume())
     */
    fun onResume() {
        if (isMusicEnabled) {
            resumeBackgroundMusic()
        }
        Log.d(TAG, "Audio resumed for app lifecycle")
    }
    
    /**
     * Clean up all audio resources (called when app is destroyed)
     */
    fun release() {
        try {
            stopBackgroundMusic()
            stopAllSounds()
            
            soundPool?.release()
            soundPool = null
            
            soundIds.clear()
            streamIds.clear()
            
            Log.d(TAG, "SoundManager released successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing SoundManager: ${e.message}")
        }
    }
    
}