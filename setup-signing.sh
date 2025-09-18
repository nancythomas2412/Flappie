#!/bin/bash

echo "🔐 FlappieGame App Signing Setup"
echo "================================"
echo ""
echo "This will set up app signing for Google Play Store upload."
echo ""

# Check if keystore already exists
if [ -f "keystore/flappie-release-key.keystore" ]; then
    echo "⚠️  Keystore already exists at keystore/flappie-release-key.keystore"
    echo "   If you want to create a new one, delete the existing keystore first."
    echo ""
    echo "To build signed release:"
    echo "  ./gradlew bundleRelease"
    echo ""
    exit 1
fi

echo "Step 1: Creating keystore directory..."
mkdir -p keystore

echo "Step 2: Generating release keystore..."
echo ""
echo "⚠️  IMPORTANT: Remember your passwords! You'll need them forever."
echo "   Recommended: Use the same password for both keystore and key."
echo ""

cd keystore

# Generate the keystore
keytool -genkey -v \
    -keystore flappie-release-key.keystore \
    -alias flappie-key \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Keystore created successfully!"
    echo ""
    echo "📝 NEXT STEPS:"
    echo "1. Edit app/build.gradle (lines 31-33)"
    echo "2. Replace 'REPLACE_WITH_YOUR_*_PASSWORD' with your actual passwords"
    echo "3. Build signed release: ./gradlew bundleRelease"
    echo ""
    echo "🔒 SECURITY REMINDER:"
    echo "- Back up your keystore file to a secure location"
    echo "- Never share your keystore or passwords"
    echo "- Keep passwords in a secure password manager"
    echo ""
    echo "📱 Your keystore is ready for Google Play Store!"
else
    echo ""
    echo "❌ Keystore creation failed. Please try again."
    echo "Common issues:"
    echo "- Password too short (minimum 6 characters)"
    echo "- Interrupted during input"
    echo ""
fi

cd ..
