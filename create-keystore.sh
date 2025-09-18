#!/bin/bash

echo "🔐 FlappieGame Keystore Generator"
echo "================================"
echo ""
echo "This script will create your release keystore for Google Play Store."
echo "You'll need to provide some information for the signing certificate."
echo ""

# Create keystore directory
mkdir -p keystore
cd keystore

echo "Generating release keystore..."
echo ""
echo "You'll be prompted for:"
echo "1. Keystore password (remember this!)"
echo "2. Key password (can be same as keystore password)"
echo "3. Your name and organization details"
echo ""

# Generate keystore
keytool -genkey -v \
    -keystore flappie-release-key.keystore \
    -alias flappie-key \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000

echo ""
echo "✅ Keystore created successfully!"
echo ""
echo "IMPORTANT: Save the following information securely:"
echo "- Keystore file: $(pwd)/flappie-release-key.keystore"
echo "- Keystore password: (what you just entered)"
echo "- Key alias: flappie-key"
echo "- Key password: (what you just entered)"
echo ""
echo "⚠️  CRITICAL: Keep this keystore file safe!"
echo "   You'll need it for ALL future app updates!"
echo ""
