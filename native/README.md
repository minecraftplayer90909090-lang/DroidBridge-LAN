# Android Netty-Quiche native

DroidBridge LAN bundles an Android/Bionic arm64-v8a build of `libnetty_quiche.so` for the **exact** Netty QUIC fork used by e4mc 6.2.1:

`0.0.74.Final.e4mc.1`

The source snapshot used for the tested native is vendored at:

`native/netty-incubator-codec-quic-0.0.74.Final.e4mc.1/`

The GitHub Actions workflow `.github/workflows/build-android-quiche-arm64.yml` builds this snapshot for Android API 21 / arm64-v8a with NDK 25.2.9519653. The workflow contains compatibility fixes needed by this older Netty build (legacy Android Maven metadata and the current cargo-ndk `--platform` spelling).

## Tested native

SHA-256:

`c6fa5084948d013d998d908ec4d5733f878c613107121a38594ef53e29f882fa`

The native is expected to be an ELF64 little-endian AArch64 Android library and to depend on Android/Bionic libraries rather than glibc (`libc.so`, not `libc.so.6`).

## Rebuilding

Use **Actions → Build e4mc Android arm64 Quiche → Run workflow**. The workflow uploads an artifact containing `libnetty_quiche.so` and `build-info.txt`.

Do not replace the release native solely because a newer Netty/Quiche build exists. The JNI ABI must match the Java classes bundled by the e4mc version being shipped. A different e4mc/Netty version should be treated as a new port and rebuilt/tested separately.

## Upstream licensing

This source snapshot retains its upstream `LICENSE.txt`, `NOTICE.txt`, and `license/` directory. The public mod repository also carries the relevant license copies under `/licenses` and inside the release JAR.
