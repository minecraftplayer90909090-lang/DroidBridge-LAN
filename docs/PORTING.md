# Porting to other Minecraft versions

Do not treat the Android native as universally compatible.

For each target Minecraft/e4mc version:

1. Identify the exact e4mc release used for that Minecraft version.
2. Identify its exact `netty-incubator-codec-quic` fork/version.
3. Compare the Quiche Java/JNI ABI with `0.0.74.Final.e4mc.1`.
4. If the ABI differs, build a matching Android arm64-v8a native rather than reusing this `.so`.
5. Verify the e4mc Config fields used for Dialtone/Quiclime still exist.
6. Verify the Minecraft LAN listener method targeted by the optional logging mixin.
7. Test broker selection, domain assignment, remote login, movement/chunk loading, disconnect/reconnect, stop-hosting, and a 15-30 minute session.

Suggested order after 26.1.x:

- 26.2 Fabric
- 26.3 Fabric
- 1.21.6-1.21.11 Fabric
- 1.21.1-1.21.5 Fabric
- NeoForge targets after Fabric is stable

Never advertise a version on CurseForge until that version has passed a real remote-network test.
