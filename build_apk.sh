#!/usr/bin/env bash
# Script to build APK files for Paper Inventory / Учет Бумаги
set -e

echo "=========================================="
echo "  Building Android APK (Paper Inventory)  "
echo "=========================================="

if [ -f "./gradlew" ]; then
    GRADLE_CMD="./gradlew"
else
    GRADLE_CMD="gradle"
fi

echo "Running assembleRelease assembleDebug..."
$GRADLE_CMD assembleRelease assembleDebug

echo ""
echo "=========================================="
echo "  BUILD SUCCESSFUL!                       "
echo "=========================================="
echo "APKs generated at:"
echo "  Release APK: app/build/outputs/apk/release/app-release.apk"
echo "  Debug APK:   app/build/outputs/apk/debug/app-debug.apk"
echo "=========================================="
