#!/usr/bin/env bash
# ArDev GB Compiler: Standalone APK Build Script with SameBoy Core
set -e
echo "====================================================="
echo "Building Standalone APK: POKEMON YELLOW"
echo "Emulation Core: SameBoy (MIT License)"
echo "====================================================="

chmod +x gradlew 2>/dev/null || true
./gradlew assembleRelease
