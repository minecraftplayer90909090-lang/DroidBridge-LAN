# Building DroidBridge LAN

## Requirements

- JDK 17
- Git
- Internet access for Gradle to retrieve Fabric Loader/Mixin compile-only APIs

The project does **not** vendor or patch an e4mc JAR. e4mc is a required runtime dependency installed separately by users.

## IntelliJ IDEA

1. Clone/open the repository root.
2. Open it as a Gradle project.
3. Use a JDK 17 Gradle JVM.
4. Let Gradle sync.
5. Run the `build` task or `./gradlew clean build`.

Output:

`build/libs/DroidBridge-LAN-Fabric-26.1.x-0.4.0.jar`

## Native library

The Android native is pinned by SHA-256 in `AndroidE4mcNativeBootstrap.java`:

`c6fa5084948d013d998d908ec4d5733f878c613107121a38594ef53e29f882fa`

The corresponding Netty-Quiche source snapshot and native build workflow remain under `native/` and `.github/workflows/` for reproducibility.
