# ❄️ Glacier Bird

A modern, feature-rich bird flying game built with Kotlin for Android. Navigate through ice caves with smooth gameplay, power-ups, achievements, customizable birds, and a lives system!

## 🎮 Game Features

### Core Gameplay
- **Smooth 60 FPS gameplay** with responsive controls
- **Progressive difficulty** that increases with score
- **Lives system** with heart refill mechanics
- **Day-night cycle** background that changes based on score

### Power-ups & Collectibles
- **🛡️ Shield** - Temporary invincibility
- **⏰ Slow Motion** - Slows down pipes for easier navigation
- **✨ Score Multiplier** - Double points for limited time
- **🧲 Magnet** - Attracts coins automatically
- **❤️ Extra Life** - Gain an additional heart

### Customization
- **Bird Collection** - Unlock different bird styles
- **Hat System** - Customize your bird with various hats
- **Responsive Design** - Adapts to all screen sizes and densities

### Progression & Stats
- **Achievement System** - 25+ achievements to unlock
- **Comprehensive Statistics** - Track your gaming journey
- **Best Score Tracking** - Personal records and milestones
- **Coin Collection** - Earn and spend in-game currency

## Setup Instructions

### 1. Import Project
1. Open Android Studio
2. File → Open → Select this "Glacier Bird" folder
3. Wait for Gradle sync to complete

### 2. Build and Run
1. Connect Android device or start emulator
2. Click "Run" button (green triangle)
3. Game should install and launch automatically

### 3. Game Controls
- **Tap anywhere** on screen to make bird jump
- **Avoid hitting** the ground or ceiling
- **Score increases** the longer you survive
- **Tap to restart** when game over

## Project Structure
```
Glacier Bird/
├── app/src/main/java/com/glacierbird/game/
│   ├── MainActivity.kt       # Entry point
│   ├── GameView.kt          # Main game logic
│   ├── GameThread.kt        # Game loop thread
│   └── Bird.kt             # Player character
├── app/src/main/res/        # Resources
└── build.gradle files      # Build configuration
```

## Technical Details
- **Language**: Kotlin
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15)
- **Architecture**: Clean, modular design
- **Rendering**: Canvas-based with SurfaceView
- **Threading**: Separate game thread for smooth performance

## Google Play Store Ready
- Meets all current Play Store requirements
- Proper permissions and features declared
- Optimized for app bundle distribution
- Ready for monetization (AdMob, IAP)

## Next Steps
1. Test on multiple devices
2. Add more features (obstacles, power-ups)
3. Integrate AdMob for monetization
4. Create Play Store listing materials
5. Upload to Play Console for review

## License
Free to use and modify for personal or commercial projects.
