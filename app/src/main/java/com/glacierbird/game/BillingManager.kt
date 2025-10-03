package com.glacierbird.game

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*

/**
 * BillingManager - Handles Google Play Billing for in-app purchases
 */
class BillingManager(
    private val context: Context,
    private val billingCallback: BillingCallback
) {
    
    companion object {
        private const val TAG = "BillingManager"
        
        // Product IDs - configure these in Google Play Console
        const val REMOVE_ADS = "remove_ads"
        const val PREMIUM_UPGRADE = "premium_upgrade" 
        const val COINS_SMALL = "coins_100"
        const val COINS_MEDIUM = "coins_500" 
        const val COINS_LARGE = "coins_1000"
        const val EXTRA_LIVES_PACK = "extra_lives_5"
        const val EXTRA_LIFE_PREMIUM = "extra_life_premium"
        const val BIRD_SKIN_GOLDEN = "bird_golden"
        const val BIRD_SKIN_RAINBOW = "bird_rainbow"
        const val BIRD_SKIN_NINJA = "bird_ninja"
        const val BIRD_SKIN_CYBER = "bird_cyber"
        const val BIRD_SKIN_PHOENIX = "bird_phoenix"
        const val BIRD_SKIN_CRYSTAL = "bird_crystal"
        const val BIRD_SKIN_SHADOW = "bird_shadow"
        const val BIRD_SKIN_VALENTINE = "bird_valentine"
        const val VIP_SUBSCRIPTION = "vip_monthly"
    }
    
    // Simulated billing state (replace with actual Google Play Billing Library)
    private var billingClientReady = false
    private val purchasedItems = mutableSetOf<String>()
    
    /**
     * Initialize billing client
     * In real implementation, this would initialize Google Play Billing Library
     */
    fun initializeBilling() {
        Log.d(TAG, "Initializing billing client...")
        
        // TODO: Replace with actual Google Play Billing initialization
        // billingClient = BillingClient.newBuilder(context)
        //     .setListener(purchaseUpdateListener)
        //     .enablePendingPurchases()
        //     .build()
        
        // Simulate successful connection
        simulateConnection()
    }
    
    /**
     * Simulate billing connection for demonstration
     */
    private fun simulateConnection() {
        billingClientReady = true
        billingCallback.onBillingSetupFinished(true)
        Log.d(TAG, "Billing client connected (simulated)")
    }
    
    /**
     * Purchase a product
     */
    fun purchaseProduct(@Suppress("UNUSED_PARAMETER") activity: Activity, productId: String) {
        if (!billingClientReady) {
            billingCallback.onPurchaseError("Billing not ready")
            return
        }
        
        Log.d(TAG, "Initiating purchase for: $productId")
        
        // TODO: Replace with actual billing flow launch
        // val billingFlowParams = BillingFlowParams.newBuilder()
        //     .setSkuDetails(skuDetails)
        //     .build()
        // billingClient.launchBillingFlow(activity, billingFlowParams)
        
        // Simulate purchase flow
        simulatePurchaseFlow(productId)
    }
    
    /**
     * Simulate purchase flow for demonstration
     */
    private fun simulatePurchaseFlow(productId: String) {
        // Simulate successful purchase after short delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            purchasedItems.add(productId)
            
            val purchase = PurchaseData(
                productId = productId,
                purchaseToken = "simulated_token_${System.currentTimeMillis()}",
                isAcknowledged = true,
                purchaseTime = System.currentTimeMillis()
            )
            
            billingCallback.onPurchaseSuccess(purchase)
            Log.d(TAG, "Simulated purchase successful: $productId")
        }, 1500) // 1.5 second delay to simulate real purchase flow
    }
    
    /**
     * Check if a product is purchased
     */
    fun isPurchased(productId: String): Boolean {
        return purchasedItems.contains(productId)
    }
    
    /**
     * Get all available products with prices
     */
    fun getAvailableProducts(): List<ProductInfo> {
        return listOf(
            ProductInfo(
                productId = REMOVE_ADS,
                title = "Remove Ads",
                description = "Enjoy Glacier Bird without any advertisements!",
                price = "$2.99",
                priceAmountMicros = 2990000L
            ),
            ProductInfo(
                productId = PREMIUM_UPGRADE,
                title = "Premium Upgrade",
                description = "Unlock all bird skins + remove ads + bonus coins!",
                price = "$4.99", 
                priceAmountMicros = 4990000L
            ),
            ProductInfo(
                productId = COINS_SMALL,
                title = "100 Coins",
                description = "Small coin pack for extra lives and power-ups",
                price = "$0.99",
                priceAmountMicros = 990000L
            ),
            ProductInfo(
                productId = COINS_MEDIUM,
                title = "500 Coins",
                description = "Medium coin pack - best value!",
                price = "$3.99",
                priceAmountMicros = 3990000L
            ),
            ProductInfo(
                productId = COINS_LARGE,
                title = "1000 Coins",
                description = "Large coin pack for serious players",
                price = "$6.99",
                priceAmountMicros = 6990000L
            ),
            ProductInfo(
                productId = EXTRA_LIVES_PACK,
                title = "5 Extra Lives",
                description = "Continue playing immediately with 5 bonus lives",
                price = "$1.99",
                priceAmountMicros = 1990000L
            ),
            ProductInfo(
                productId = EXTRA_LIFE_PREMIUM,
                title = "Extra Life",
                description = "Purchase an extra life to continue playing",
                price = "$4.99",
                priceAmountMicros = 4990000L
            ),
            ProductInfo(
                productId = BIRD_SKIN_GOLDEN,
                title = "Golden Bird",
                description = "Exclusive golden bird skin with sparkle effects",
                price = "$1.99",
                priceAmountMicros = 1990000L
            ),
            ProductInfo(
                productId = BIRD_SKIN_RAINBOW,
                title = "Rainbow Bird", 
                description = "Colorful rainbow bird with trail effects",
                price = "$2.49",
                priceAmountMicros = 2490000L
            ),
            ProductInfo(
                productId = BIRD_SKIN_CYBER,
                title = "Cyber Bird",
                description = "Futuristic neon bird with digital glitch effects",
                price = "$3.99",
                priceAmountMicros = 3990000L
            ),
            ProductInfo(
                productId = BIRD_SKIN_PHOENIX,
                title = "Phoenix Bird",
                description = "Rise from the ashes with fire trail effects",
                price = "$3.99",
                priceAmountMicros = 3990000L
            ),
            ProductInfo(
                productId = BIRD_SKIN_CRYSTAL,
                title = "Crystal Bird",
                description = "Crystalline beauty with prismatic light effects",
                price = "$2.99",
                priceAmountMicros = 2990000L
            ),
            ProductInfo(
                productId = BIRD_SKIN_SHADOW,
                title = "Shadow Bird",
                description = "Master of darkness with shadowy afterimage effects",
                price = "$1.99",
                priceAmountMicros = 1990000L
            ),
            ProductInfo(
                productId = BIRD_SKIN_VALENTINE,
                title = "Love Bird",
                description = "Spread the love with this heart-themed bird",
                price = "$1.99",
                priceAmountMicros = 1990000L
            ),
            ProductInfo(
                productId = VIP_SUBSCRIPTION,
                title = "VIP Membership",
                description = "Monthly subscription: unlimited lives + exclusive skins + no ads",
                price = "$4.99/month",
                priceAmountMicros = 4990000L
            )
        )
    }
    
    /**
     * Restore purchases (important for users switching devices)
     */
    fun restorePurchases() {
        if (!billingClientReady) {
            billingCallback.onPurchaseError("Billing not ready")
            return
        }
        
        Log.d(TAG, "Restoring purchases...")
        
        // TODO: Replace with actual purchase restoration
        // billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchases ->
        //     if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
        //         purchases?.forEach { purchase ->
        //             handlePurchase(purchase)
        //         }
        //     }
        // }
        
        // Simulate restoration
        billingCallback.onPurchasesRestored(purchasedItems.size)
    }
    
    /**
     * Handle purchase verification and fulfillment
     */
    private fun handlePurchase(purchase: PurchaseData) {
        when (purchase.productId) {
            REMOVE_ADS -> {
                // Remove ads from the game
                billingCallback.onProductUnlocked(REMOVE_ADS, "Ads removed!")
            }
            PREMIUM_UPGRADE -> {
                // Unlock premium features
                purchasedItems.add(REMOVE_ADS) // Includes ad removal
                billingCallback.onProductUnlocked(PREMIUM_UPGRADE, "Premium features unlocked!")
            }
            COINS_SMALL -> {
                // Add 100 coins
                billingCallback.onCoinsGranted(100)
            }
            COINS_MEDIUM -> {
                // Add 500 coins  
                billingCallback.onCoinsGranted(500)
            }
            COINS_LARGE -> {
                // Add 1000 coins
                billingCallback.onCoinsGranted(1000)
            }
            EXTRA_LIVES_PACK -> {
                // Grant 5 extra lives
                billingCallback.onLivesGranted(5)
            }
            BIRD_SKIN_GOLDEN, BIRD_SKIN_RAINBOW, BIRD_SKIN_NINJA, 
            BIRD_SKIN_CYBER, BIRD_SKIN_PHOENIX, BIRD_SKIN_CRYSTAL,
            BIRD_SKIN_SHADOW, BIRD_SKIN_VALENTINE -> {
                // Unlock bird skin
                billingCallback.onProductUnlocked(purchase.productId, "New bird skin unlocked!")
            }
            VIP_SUBSCRIPTION -> {
                // Activate VIP features
                billingCallback.onProductUnlocked(VIP_SUBSCRIPTION, "VIP membership activated!")
            }
        }
    }
    
    /**
     * Check if ads should be shown
     */
    fun shouldShowAds(): Boolean {
        return !isPurchased(REMOVE_ADS) && !isPurchased(PREMIUM_UPGRADE) && !isPurchased(VIP_SUBSCRIPTION)
    }
    
    /**
     * Check if user has premium features
     */
    fun isPremiumUser(): Boolean {
        return isPurchased(PREMIUM_UPGRADE) || isPurchased(VIP_SUBSCRIPTION)
    }
    
    /**
     * Get monetization statistics
     */
    fun getMonetizationStats(): Map<String, Any> {
        return mapOf(
            "totalPurchases" to purchasedItems.size,
            "isPremium" to isPremiumUser(),
            "adsRemoved" to !shouldShowAds(),
            "purchasedProducts" to purchasedItems.toList()
        )
    }
    
    /**
     * Clean up billing client
     */
    fun cleanup() {
        // TODO: Clean up actual billing client
        // billingClient?.endConnection()
        billingClientReady = false
        Log.d(TAG, "Billing client cleaned up")
    }
}

/**
 * Billing callback interface
 */
interface BillingCallback {
    fun onBillingSetupFinished(success: Boolean)
    fun onPurchaseSuccess(purchase: PurchaseData)
    fun onPurchaseError(error: String)
    fun onPurchasesRestored(count: Int)
    fun onProductUnlocked(productId: String, message: String)
    fun onCoinsGranted(amount: Int)
    fun onLivesGranted(amount: Int)
}

/**
 * Purchase data class
 */
data class PurchaseData(
    val productId: String,
    val purchaseToken: String,
    val isAcknowledged: Boolean,
    val purchaseTime: Long
)

/**
 * Product information class
 */
data class ProductInfo(
    val productId: String,
    val title: String,
    val description: String,
    val price: String,
    val priceAmountMicros: Long
)