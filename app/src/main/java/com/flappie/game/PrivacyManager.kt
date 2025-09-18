package com.flappie.game

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log

/**
 * PrivacyManager - Handles privacy compliance for Google Play Store
 * Required for Play Store approval - manages COPPA, data collection, and privacy policy
 */
class PrivacyManager(private val context: Context) {
    
    companion object {
        private const val TAG = "PrivacyManager"
        private const val PREFS_NAME = "privacy_prefs"
        private const val KEY_PRIVACY_ACCEPTED = "privacy_accepted"
        private const val KEY_FIRST_PRIVACY_SHOWN = "first_privacy_shown"
        private const val KEY_DATA_COLLECTION_CONSENT = "data_collection_consent"
        private const val KEY_AGE_VERIFIED = "age_verified"
        private const val KEY_IS_CHILD = "is_child"
        
        // Privacy Policy URL - GitHub Pages hosted
        const val PRIVACY_POLICY_URL = "https://nancythomas2412.github.io/Flappie/privacy-policy.html"
        
        // Example URLs after hosting:
        // GitHub Pages: "https://yourusername.github.io/flappie-privacy/privacy-policy.html"
        // Firebase: "https://flappie-game-12345.web.app/privacy-policy.html"
        // Netlify: "https://flappie-privacy.netlify.app/privacy-policy.html"
        
        // Data we collect (for transparency)
        val COLLECTED_DATA = listOf(
            "Game scores and achievements",
            "App usage statistics (anonymous)",
            "Device information (for compatibility)",
            "Crash reports (to improve stability)"
        )
        
        val NOT_COLLECTED_DATA = listOf(
            "Personal information",
            "Contact details", 
            "Location data",
            "Photos or files",
            "Microphone or camera access"
        )
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Check if privacy policy needs to be shown
     */
    fun shouldShowPrivacyDialog(): Boolean {
        return !prefs.getBoolean(KEY_PRIVACY_ACCEPTED, false)
    }
    
    /**
     * Check if age verification is needed (COPPA compliance)
     */
    fun needsAgeVerification(): Boolean {
        return !prefs.getBoolean(KEY_AGE_VERIFIED, false)
    }
    
    /**
     * Show privacy policy dialog
     */
    fun showPrivacyDialog(activity: android.app.Activity, onAccepted: () -> Unit) {
        val builder = android.app.AlertDialog.Builder(activity)
        
        val message = """
            🔒 Privacy & Data Protection
            
            Flappie respects your privacy! Here's what you should know:
            
            ✅ What we collect:
            • Game scores and progress
            • Anonymous usage statistics  
            • Device compatibility info
            
            ❌ What we DON'T collect:
            • Personal information
            • Location data
            • Photos or contacts
            
            All data stays on your device or is anonymized.
            
            By playing, you agree to our privacy practices.
        """.trimIndent()
        
        builder.setTitle("Privacy Notice")
            .setMessage(message)
            .setPositiveButton("I Agree") { _, _ ->
                acceptPrivacyPolicy()
                onAccepted()
            }
            .setNeutralButton("Privacy Policy") { _, _ ->
                openPrivacyPolicy(activity)
                // Show dialog again after they return
                showPrivacyDialog(activity, onAccepted)
            }
            .setNegativeButton("Exit App") { _, _ ->
                activity.finish()
            }
            .setCancelable(false)
            .show()
    }
    
    /**
     * Show age verification dialog (COPPA compliance)
     */
    fun showAgeVerificationDialog(activity: android.app.Activity, onVerified: (isChild: Boolean) -> Unit) {
        val builder = android.app.AlertDialog.Builder(activity)
        
        val message = """
            👶 Age Verification
            
            To comply with privacy laws, we need to verify your age.
            
            This helps us provide appropriate content and protections.
            
            Are you 13 years old or older?
        """.trimIndent()
        
        builder.setTitle("Age Verification")
            .setMessage(message)
            .setPositiveButton("Yes, I'm 13+") { _, _ ->
                setAgeVerified(false)
                onVerified(false)
            }
            .setNegativeButton("I'm under 13") { _, _ ->
                setAgeVerified(true)
                onVerified(true)
            }
            .setCancelable(false)
            .show()
    }
    
    /**
     * Accept privacy policy
     */
    private fun acceptPrivacyPolicy() {
        prefs.edit()
            .putBoolean(KEY_PRIVACY_ACCEPTED, true)
            .putBoolean(KEY_FIRST_PRIVACY_SHOWN, true)
            .putLong("privacy_accepted_date", System.currentTimeMillis())
            .apply()
        
        Log.d(TAG, "Privacy policy accepted")
    }
    
    /**
     * Set age verification status
     */
    private fun setAgeVerified(isChild: Boolean) {
        prefs.edit()
            .putBoolean(KEY_AGE_VERIFIED, true)
            .putBoolean(KEY_IS_CHILD, isChild)
            .putLong("age_verified_date", System.currentTimeMillis())
            .apply()
        
        Log.d(TAG, "Age verified - isChild: $isChild")
    }
    
    /**
     * Open privacy policy URL
     */
    private fun openPrivacyPolicy(activity: android.app.Activity) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
            activity.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open privacy policy: ${e.message}")
            
            // Fallback: show privacy policy text in dialog
            showPrivacyPolicyText(activity)
        }
    }
    
    /**
     * Show privacy policy as text (fallback)
     */
    private fun showPrivacyPolicyText(activity: android.app.Activity) {
        val privacyText = """
            FLAPPIE PRIVACY POLICY
            
            Last updated: ${java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date())}
            
            1. INFORMATION WE COLLECT
            • Game scores and achievements (stored locally)
            • Anonymous usage statistics
            • Device information for compatibility
            • Crash reports to improve stability
            
            2. INFORMATION WE DON'T COLLECT
            • Personal information or contact details
            • Location data
            • Photos, files, or media
            • Microphone or camera access
            
            3. HOW WE USE INFORMATION
            • To provide and improve the game
            • To fix bugs and crashes
            • To understand how players engage with the game
            
            4. DATA SHARING
            We do not sell, trade, or share your personal information with third parties.
            
            5. CHILDREN'S PRIVACY
            We comply with COPPA and do not knowingly collect personal information from children under 13.
            
            6. DATA SECURITY
            Game data is stored locally on your device. We implement security measures to protect any transmitted data.
            
            7. CONTACT US
            Questions about this privacy policy? Contact us through the app store.
        """.trimIndent()
        
        val builder = android.app.AlertDialog.Builder(activity)
        builder.setTitle("Privacy Policy")
            .setMessage(privacyText)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    /**
     * Check if user is a child (for COPPA compliance)
     */
    fun isChild(): Boolean {
        return prefs.getBoolean(KEY_IS_CHILD, false)
    }
    
    /**
     * Check if data collection is allowed
     */
    fun isDataCollectionAllowed(): Boolean {
        // If user is a child, limit data collection
        return prefs.getBoolean(KEY_PRIVACY_ACCEPTED, false) && !isChild()
    }
    
    /**
     * Get privacy compliance status
     */
    fun getPrivacyStatus(): Map<String, Any> {
        return mapOf(
            "privacyAccepted" to prefs.getBoolean(KEY_PRIVACY_ACCEPTED, false),
            "ageVerified" to prefs.getBoolean(KEY_AGE_VERIFIED, false),
            "isChild" to prefs.getBoolean(KEY_IS_CHILD, false),
            "dataCollectionAllowed" to isDataCollectionAllowed()
        )
    }
    
    /**
     * Reset privacy settings (for testing)
     */
    fun resetPrivacySettings() {
        prefs.edit().clear().apply()
        Log.d(TAG, "Privacy settings reset")
    }
    
    /**
     * Show data deletion dialog (GDPR compliance)
     */
    fun showDataDeletionDialog(activity: android.app.Activity) {
        val builder = android.app.AlertDialog.Builder(activity)
        
        builder.setTitle("Delete My Data")
            .setMessage("This will delete all your game progress, achievements, and settings. This action cannot be undone.\n\nAre you sure?")
            .setPositiveButton("Yes, Delete All") { _, _ ->
                deleteAllUserData()
                
                // Show confirmation
                android.app.AlertDialog.Builder(activity)
                    .setTitle("Data Deleted")
                    .setMessage("All your data has been deleted. The app will now restart.")
                    .setPositiveButton("OK") { _, _ ->
                        // Restart the app
                        val intent = activity.packageManager.getLaunchIntentForPackage(activity.packageName)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        activity.startActivity(intent)
                        activity.finish()
                    }
                    .setCancelable(false)
                    .show()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    /**
     * Delete all user data (GDPR compliance)
     */
    private fun deleteAllUserData() {
        try {
            // Clear all shared preferences
            val allPrefsFiles = listOf(
                PREFS_NAME,
                "game_config_prefs",
                "achievements_prefs", 
                "heart_refill_prefs",
                "app_rating_prefs",
                "tutorial_prefs"
            )
            
            allPrefsFiles.forEach { prefsFile ->
                context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()
            }
            
            Log.d(TAG, "All user data deleted")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user data: ${e.message}")
        }
    }
}