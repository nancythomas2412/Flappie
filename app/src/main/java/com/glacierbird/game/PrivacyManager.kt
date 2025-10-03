package com.glacierbird.game

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
        const val PRIVACY_POLICY_URL = "https://nancythomas2412.github.io/GlacierBird/privacy-policy.html"
        
        // Example URLs after hosting:
        // GitHub Pages: "https://yourusername.github.io/glacierbird-privacy/privacy-policy.html"
        // Firebase: "https://glacierbird-game-12345.web.app/privacy-policy.html"
        // Netlify: "https://glacierbird-privacy.netlify.app/privacy-policy.html"
        
        // Data we collect (for transparency)
        val COLLECTED_DATA = listOf(
            "Game scores and achievements (stored locally)",
            "Advertising data via Google AdMob",
            "Device information for ad compatibility",
            "Advertising ID for personalized ads",
            "General location for relevant ads (if permitted)"
        )

        val NOT_COLLECTED_DATA = listOf(
            "Personal information (name, email, phone)",
            "Precise location data",
            "Photos, files, or media",
            "Microphone or camera access",
            "Contacts or calendar data"
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

            Glacier Bird respects your privacy! Here's what you should know:

            ✅ What we collect:
            • Game scores and progress (stored locally)
            • Advertising data via Google AdMob
            • Device info for ad compatibility

            ✅ What ads collect:
            • Advertising ID for personalized ads
            • General location for relevant ads
            • Ad interaction data

            ❌ What we DON'T collect directly:
            • Personal information (name, email)
            • Photos, contacts, or files
            • Precise location data

            You can control ad personalization in device settings.
            Premium upgrade available to remove ads.

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
            GLACIER BIRD PRIVACY POLICY

            Last updated: ${java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date())}
            Developer: DeCoCo

            1. INFORMATION WE COLLECT
            • Game scores and achievements (stored locally)
            • Advertising data via Google AdMob
            • Device information for ad compatibility
            • Advertising ID for personalized ads
            • General location for relevant ads (if permitted)

            2. INFORMATION WE DON'T COLLECT DIRECTLY
            • Personal information (name, email, phone)
            • Precise location data
            • Photos, files, or media
            • Microphone or camera access

            3. HOW WE USE INFORMATION
            • To provide and improve the game experience
            • To display relevant advertisements
            • To measure ad performance and effectiveness
            • To fix bugs and improve stability

            4. DATA SHARING
            • Game data stays on your device
            • Advertising data shared with Google AdMob for ad serving
            • We do not sell your personal information

            5. ADVERTISING & CHILDREN'S PRIVACY
            • We comply with COPPA for users under 13
            • Ads are family-friendly and age-appropriate
            • You can control ad personalization in device settings
            • Premium upgrade available to remove ads

            6. YOUR CHOICES
            • Reset Advertising ID in device settings
            • Opt out of personalized ads
            • Purchase premium upgrade for ad-free experience

            7. CONTACT US
            Developer: DeCoCo
            For privacy questions, contact us through the app store.
            Full policy: nancythomas2412.github.io/GlacierBird/privacy-policy.html
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