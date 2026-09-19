# Changelog

## 0.4.1

- Fix Android Netty-Quiche startup with the official external e4mc mod.
- DroidBridge LAN now stages and verifies `libnetty_quiche.so` without loading it from the Fabric/Knot classloader.
- DroidBridge LibPatcher remains the single owner of the actual native load, avoiding Android's cross-classloader duplicate-load failure and the obsolete glibc fallback path.

## 0.4.0

- Reworked DroidBridge LAN into a standalone e4mc compatibility add-on.
- e4mc is now a required external dependency instead of a nested modified JAR.
- Preloads the verified Android ARM64 Netty-Quiche native before stock e4mc initializes.
- Keeps e4mc's installed classes untouched.
- Retains Android-only runtime disabling of Dialtone/iroh while keeping classic Quiclime enabled.
- Moves malformed `e4mc.toml` recovery ahead of e4mc initialization.
- Keeps the DroidBridge LAN project icon separate from e4mc branding.
