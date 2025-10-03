# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ===== GLACIER BIRD GAME SPECIFIC RULES =====

# Keep all game classes to prevent gameplay issues
-keep class com.glacierbird.game.** { *; }

# Keep enum classes (used for game states, power-ups, etc.)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Google Play Billing - prevent billing issues
-keep class com.android.vending.billing.**
-keep class com.android.billingclient.api.** { *; }

# Keep data classes used for billing
-keep class com.glacierbird.game.BillingCallback { *; }
-keep class com.glacierbird.game.PurchaseData { *; }
-keep class com.glacierbird.game.ProductInfo { *; }

# Keep TouchCommand sealed class hierarchy
-keep class com.glacierbird.game.TouchCommand { *; }
-keep class com.glacierbird.game.TouchCommand$* { *; }

# Preserve sprite data classes
-keep class com.glacierbird.game.BirdSprites { *; }
-keep class com.glacierbird.game.HeartSprites { *; }
-keep class com.glacierbird.game.PowerUpSprites { *; }
-keep class com.glacierbird.game.CoinSprites { *; }

# Achievement and statistics data classes
-keep class com.glacierbird.game.Achievement { *; }
-keep class com.glacierbird.game.GameStatistics { *; }
-keep class com.glacierbird.game.PerformanceMetrics { *; }
-keep class com.glacierbird.game.DailyBonus { *; }
-keep class com.glacierbird.game.BirdSkinInfo { *; }

# Tutorial system
-keep class com.glacierbird.game.TutorialStep { *; }
-keep class com.glacierbird.game.TutorialHighlight { *; }

# Android components
-keep public class * extends android.app.Activity
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Remove logging in release builds for better performance
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# Keep SharedPreferences for game save data
-keep class android.content.SharedPreferences { *; }
-keep class android.content.SharedPreferences$Editor { *; }