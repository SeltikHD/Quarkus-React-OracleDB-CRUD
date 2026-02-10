#!/bin/bash
# =============================================================================
# DOCKER ENTRYPOINT SCRIPT FOR AUTOFLEX BACKEND
# =============================================================================
# This script handles Oracle Wallet setup before starting the Quarkus application.
#
# ENVIRONMENT VARIABLES:
#   WALLET_BASE64    - Base64-encoded ZIP file containing Oracle Wallet files
#   ORACLE_JDBC_URL  - Will be updated to include TNS_ADMIN path
#
# The Oracle Wallet is required for secure connections to Oracle Cloud databases
# (Autonomous Transaction Processing, Autonomous Data Warehouse, etc.)
# =============================================================================

set -e

WALLET_DIR="/app/wallet"
WALLET_ZIP="/tmp/wallet.zip"

echo "=== Autoflex Backend Entrypoint ==="
echo "Timestamp: $(date -u +"%Y-%m-%dT%H:%M:%SZ")"

# =============================================================================
# ORACLE WALLET SETUP
# =============================================================================
# Render.com and similar PaaS providers handle secrets via environment variables.
# Since Oracle Wallet consists of multiple files (cwallet.sso, tnsnames.ora, etc.),
# we encode the entire wallet directory as a ZIP file in Base64.
#
# To create the WALLET_BASE64 value:
#   cd /path/to/wallet_directory
#   zip -r wallet.zip .
#   base64 -w 0 wallet.zip > wallet_base64.txt
#   # Copy content of wallet_base64.txt to WALLET_BASE64 env var in Render
# =============================================================================

if [ -n "$WALLET_BASE64" ]; then
    echo "Oracle Wallet detected. Decoding and extracting..."
    
    # Create wallet directory
    mkdir -p "$WALLET_DIR"
    
    # Decode Base64 to ZIP file
    echo "$WALLET_BASE64" | base64 -d > "$WALLET_ZIP"
    
    # Extract wallet files
    unzip -o "$WALLET_ZIP" -d "$WALLET_DIR"
    
    # Cleanup temporary ZIP
    rm -f "$WALLET_ZIP"
    
    # Set appropriate permissions (wallet files should be read-only)
    chmod 600 "$WALLET_DIR"/*
    
    echo "Wallet files extracted to: $WALLET_DIR"
    echo "Wallet contents:"
    ls -la "$WALLET_DIR"
    
    # Update JDBC URL to include TNS_ADMIN if not already present
    if [ -n "$ORACLE_JDBC_URL" ] && [[ "$ORACLE_JDBC_URL" != *"TNS_ADMIN"* ]]; then
        # Append TNS_ADMIN parameter to JDBC URL
        if [[ "$ORACLE_JDBC_URL" == *"?"* ]]; then
            export ORACLE_JDBC_URL="${ORACLE_JDBC_URL}&TNS_ADMIN=${WALLET_DIR}"
        else
            export ORACLE_JDBC_URL="${ORACLE_JDBC_URL}?TNS_ADMIN=${WALLET_DIR}"
        fi
        echo "Updated ORACLE_JDBC_URL with TNS_ADMIN path"
    fi
    
    # Also set as Java system property for additional compatibility
    export JAVA_OPTS="${JAVA_OPTS} -Doracle.net.tns_admin=${WALLET_DIR}"
    
    echo "Oracle Wallet setup complete."
else
    echo "No Oracle Wallet detected (WALLET_BASE64 not set)."
    echo "Proceeding with standard JDBC connection..."
fi

# =============================================================================
# JAVA OPTIONS FOR QUARKUS
# =============================================================================
# Default memory settings optimized for containerized environments
# Render provides at least 512MB RAM for free tier
# =============================================================================

if [ -z "$JAVA_OPTS_APPEND" ]; then
    export JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
fi

echo "Starting Quarkus application..."
echo "JAVA_OPTS: $JAVA_OPTS"
echo "JAVA_OPTS_APPEND: $JAVA_OPTS_APPEND"

# =============================================================================
# START APPLICATION
# =============================================================================
# Execute the Quarkus runner JAR
# Using exec replaces this shell process with Java, ensuring proper signal handling
# =============================================================================

exec java $JAVA_OPTS $JAVA_OPTS_APPEND -jar /app/quarkus-run.jar
