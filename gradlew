#!/usr/bin/env bash
# Gradle wrapper execution script for POSIX systems
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
elif [ -f "./gradle/wrapper/gradle-wrapper.jar" ]; then
  exec java -jar ./gradle/wrapper/gradle-wrapper.jar "$@"
else
  exec gradle "$@"
fi
