# Third-party notices

DroidBridge LAN is an independent project by DNA Mobile.

## e4mc — required external dependency

Upstream: https://github.com/vgskye/e4mc-minecraft-architectury  
License: MIT  
Copyright: Copyright (c) 2024 Skye

DroidBridge LAN requires the official e4mc mod but **does not bundle, fork, or redistribute e4mc classes** in the DroidBridge LAN release JAR. e4mc is installed separately by the user and remains under its upstream license.

## Netty Incubator Codec QUIC 0.0.74.Final.e4mc.1

License: Apache License 2.0.

DroidBridge LAN bundles an Android ARM64 `libnetty_quiche.so` built from the Netty-Quiche fork/ABI used by e4mc 6.2.1. The relevant license and NOTICE files are included with the project and release JAR.

## Cloudflare quiche

License: BSD-style license. The complete license text is included as `quiche-BSD.txt`.

## BoringSSL / OpenSSL / SSLeay components

The Android Netty-Quiche native links code from BoringSSL. The license bundle supplied with the Netty QUIC source tree is included as `boringssl-LICENSE.txt`.

DroidBridge LAN is not affiliated with, sponsored by, or endorsed by e4mc, the Netty Project, Cloudflare, Google, Mojang/Microsoft, Fabric, or CurseForge.
