# DroidBridge LAN

**DroidBridge LAN** is an Android compatibility add-on that lets the official **e4mc** mod work with Minecraft Java running through DroidBridge.

Open a single-player world to LAN and e4mc provides the public address as usual. Friends can connect from another network using normal Minecraft Java multiplayer without manual port forwarding.

## Requirements

- DroidBridge Launcher
- Minecraft Java Edition 26.1.x
- Fabric Loader 0.19.5 or newer
- Fabric API
- **e4mc 6.2.1+modern or newer compatible release**
- Android ARM64

DroidBridge LAN does **not** bundle e4mc. Install the official e4mc mod alongside DroidBridge LAN.

## What DroidBridge LAN adds

- Android ARM64 Netty-Quiche native compatible with e4mc 6.2.1
- Verified native staging so DroidBridge LibPatcher performs the Android QUIC load exactly once
- Android-only runtime compatibility for e4mc's unsupported Dialtone/iroh path
- Recovery from a truncated `e4mc.toml` after an abrupt device/process shutdown
- DroidBridge-only runtime gate so the Android compatibility path stays out of normal desktop Minecraft

## Installation

Place both mods in the same Fabric instance:

```text
mods/
├── DroidBridge-LAN-Fabric-26.1.x-0.4.1.jar
└── e4mc-fabric-6.2.1-modern.jar
```

Then launch Minecraft through DroidBridge and use **Open to LAN** normally. e4mc will display the public address in chat.

## Building

Open the repository in IntelliJ IDEA and run the Gradle `build` task, or run:

```bash
./gradlew build
```

The output is written to `build/libs/`.

## e4mc credit

DroidBridge LAN is an independent compatibility add-on for the open-source **e4mc** project by Skye and its contributors.

Upstream: https://github.com/vgskye/e4mc-minecraft-architectury

DroidBridge LAN does not redistribute or modify the installed e4mc JAR. e4mc remains under its upstream MIT License and is maintained separately from this project.

DroidBridge LAN is not affiliated with or endorsed by the e4mc maintainers.

## License

DroidBridge LAN source code is released under the MIT License. The bundled Android Netty-Quiche native and its dependencies retain their respective upstream licenses; see `THIRD_PARTY_NOTICES.md`.
