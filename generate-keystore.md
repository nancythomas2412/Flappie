# Release Signing Setup for Flappie Game

## Generate Production Keystore

**IMPORTANT**: You must create a release keystore before publishing to Google Play Store.

### Step 1: Generate Keystore File

Open terminal in the project directory and run:

```bash
keytool -genkey -v -keystore flappie-release-key.keystore -alias flappie-key -keyalg RSA -keysize 2048 -validity 10000
```

This will prompt you for:
- Keystore password (SAVE THIS SECURELY!)
- Key password (SAVE THIS SECURELY!)
- Your name, organization, city, state, country

### Step 2: Store Keystore Securely

1. **Move keystore to secure location** (NOT in your project folder)
2. **Backup the keystore file and passwords**
3. **Never commit keystore to version control**

### Step 3: Update build.gradle

Replace the release signing config in `app/build.gradle`:

```gradle
release {
    storeFile file('/path/to/your/flappie-release-key.keystore')
    storePassword 'YOUR_KEYSTORE_PASSWORD'
    keyAlias 'flappie-key'
    keyPassword 'YOUR_KEY_PASSWORD'
}
```

### Step 4: Environment Variables (Recommended)

For better security, use environment variables:

```gradle
release {
    storeFile file(System.getenv("KEYSTORE_FILE") ?: "path/to/keystore")
    storePassword System.getenv("KEYSTORE_PASSWORD")
    keyAlias System.getenv("KEY_ALIAS")
    keyPassword System.getenv("KEY_PASSWORD")
}
```

### Step 5: Build Release APK/AAB

```bash
# Build APK
./gradlew assembleRelease

# Build AAB (recommended for Play Store)
./gradlew bundleRelease
```

## Security Checklist

- [ ] Keystore file is in secure location outside project
- [ ] Passwords are stored securely (password manager)
- [ ] Keystore file is backed up
- [ ] build.gradle does not contain hardcoded passwords
- [ ] .keystore files are in .gitignore

## Files Created

The release files will be in:
- APK: `app/build/outputs/apk/release/app-release.apk`
- AAB: `app/build/outputs/bundle/release/app-release.aab`

**Upload the AAB file to Google Play Console for publication.**

## Current Status

✅ Debug signing configured
⚠️  Release signing configured with fallback to debug
❌ Production keystore needs to be generated

**Note**: The current setup uses debug signing for release builds as a fallback. This MUST be changed before Play Store publication.