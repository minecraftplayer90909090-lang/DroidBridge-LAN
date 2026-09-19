# DroidBridge LAN

**DroidBridge LAN** is an Android compatibility add-on for the official **e4mc** mod when running Minecraft Java through DroidBridge.

Open a single-player world to LAN normally and e4mc will provide a public address that other Java Edition players can use to connect from another network — without manual port forwarding.

## Features

- Makes e4mc's QUIC networking work on Android ARM64 through DroidBridge
- Uses the official e4mc mod as a required dependency
- Does not bundle or replace e4mc
- No manual port forwarding required
- Friends connect through normal Minecraft Java multiplayer
- Recovers a damaged/truncated e4mc config after an abrupt Android shutdown

## Requirements

- DroidBridge Launcher
- Minecraft Java Edition 26.1.x
- Fabric Loader 0.19.5+
- Fabric API
- **e4mc 6.2.1+modern or a compatible newer release**
- Android ARM64

Install **DroidBridge LAN** and the official **e4mc** mod in the same Fabric instance. Then use **Open to LAN** normally.

## e4mc credit

DroidBridge LAN is an independent compatibility add-on for the open-source **e4mc** project by Skye and its contributors.

Upstream project: https://github.com/vgskye/e4mc-minecraft-architectury

DroidBridge LAN does not redistribute or modify the installed e4mc JAR. e4mc remains a separate project under its upstream MIT License.

DroidBridge LAN is not affiliated with or endorsed by the e4mc maintainers.

## Source

DroidBridge LAN is open source. The repository contains the Android compatibility code and the source/build workflow used for the bundled Android Netty-Quiche native.
