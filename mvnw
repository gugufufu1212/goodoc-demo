#!/bin/sh
# Maven Wrapper Script
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements. See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.

set -e

# OS specific support.
darwin=false
case "$(uname)" in
  Darwin*) darwin=true ;;
esac

# Attempt to set JAVA_HOME if it's not set
if [ -z "$JAVA_HOME" ]; then
  if $darwin; then
    export JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null || echo "")"
  fi
fi

if [ -z "$JAVA_HOME" ]; then
  if command -v java >/dev/null 2>&1; then
    JAVA_BIN="$(command -v java)"
    export JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$JAVA_BIN" 2>/dev/null || echo "$JAVA_BIN")")")"
  fi
fi

if [ -z "$JAVA_HOME" ]; then
  echo "Error: JAVA_HOME is not set and java could not be found." >&2
  echo "Please install Java 17+ and set JAVA_HOME." >&2
  exit 1
fi

JAVA_CMD="$JAVA_HOME/bin/java"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
MAVEN_WRAPPER_PROPERTIES="$SCRIPT_DIR/.mvn/wrapper/maven-wrapper.properties"
MAVEN_WRAPPER_JAR="$SCRIPT_DIR/.mvn/wrapper/maven-wrapper.jar"
MAVEN_USER_HOME="${MAVEN_USER_HOME:-$HOME/.m2}"

# Parse maven-wrapper.properties
DISTRIBUTION_URL=$(grep "^distributionUrl=" "$MAVEN_WRAPPER_PROPERTIES" | cut -d= -f2-)
WRAPPER_URL=$(grep "^wrapperUrl=" "$MAVEN_WRAPPER_PROPERTIES" | cut -d= -f2-)

DISTRIBUTION_FILENAME="${DISTRIBUTION_URL##*/}"
DISTRIBUTION_ID="${DISTRIBUTION_FILENAME%.zip}"
MAVEN_HOME="$MAVEN_USER_HOME/wrapper/dists/$DISTRIBUTION_ID"

# Download Maven if needed
if [ ! -d "$MAVEN_HOME" ] || [ -z "$(ls -A "$MAVEN_HOME" 2>/dev/null)" ]; then
  echo "Downloading Maven $DISTRIBUTION_ID ..."
  mkdir -p "$MAVEN_HOME"
  TMP_FILE="$MAVEN_HOME/$DISTRIBUTION_FILENAME"

  if command -v curl >/dev/null 2>&1; then
    curl -fsSL -o "$TMP_FILE" "$DISTRIBUTION_URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$TMP_FILE" "$DISTRIBUTION_URL"
  else
    echo "Error: curl or wget is required to download Maven." >&2
    exit 1
  fi

  unzip -q "$TMP_FILE" -d "$MAVEN_HOME"
  rm -f "$TMP_FILE"
  echo "Maven downloaded."
fi

# Find the extracted Maven directory
MAVEN_BIN_DIR=$(find "$MAVEN_HOME" -maxdepth 4 -name "mvn" -type f | head -1)
if [ -z "$MAVEN_BIN_DIR" ]; then
  echo "Error: Could not find mvn in $MAVEN_HOME" >&2
  exit 1
fi

MAVEN_EXEC="$MAVEN_BIN_DIR"
chmod +x "$MAVEN_EXEC"

exec "$MAVEN_EXEC" "$@"
