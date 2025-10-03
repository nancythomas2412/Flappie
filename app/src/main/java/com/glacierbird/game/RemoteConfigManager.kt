package com.glacierbird.game

/**
 * Remote Config Manager - Stub implementation for when Firebase is disabled
 * Provides default configuration values without Firebase dependencies
 * TODO: Replace with actual Firebase implementation when Firebase is enabled
 */
@Suppress("unused")
object RemoteConfigManager {

    // Default values for remote config
    private val defaults = mapOf(
        "ads_enabled" to false,
        "show_interstitial_ads" to false,
        "show_banner_ads" to false,
        "show_rewarded_ads" to true,
        "interstitial_frequency" to 5,  // Show every 5 game overs
        "rewarded_coin_multiplier" to 2.0,
        "new_feature_enabled" to false,
        "maintenance_mode" to false
    )

    // Ad Configuration Methods - Return default values
    fun isAdsEnabled(): Boolean = defaults["ads_enabled"] as Boolean

    fun shouldShowInterstitialAds(): Boolean =
        isAdsEnabled() && (defaults["show_interstitial_ads"] as Boolean)

    fun shouldShowBannerAds(): Boolean =
        isAdsEnabled() && (defaults["show_banner_ads"] as Boolean)

    fun shouldShowRewardedAds(): Boolean =
        isAdsEnabled() && (defaults["show_rewarded_ads"] as Boolean)

    fun getInterstitialFrequency(): Int = defaults["interstitial_frequency"] as Int

    fun getRewardedCoinMultiplier(): Double = defaults["rewarded_coin_multiplier"] as Double

    // Feature Toggle Methods - Return default values
    fun isNewFeatureEnabled(): Boolean = defaults["new_feature_enabled"] as Boolean

    fun isMaintenanceMode(): Boolean = defaults["maintenance_mode"] as Boolean
}