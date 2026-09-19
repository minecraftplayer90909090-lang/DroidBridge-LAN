# Component manifest

| Component | Version / target | How used | License / status |
|---|---|---|---|
| DroidBridge LAN | 0.4.0 | Android compatibility add-on | MIT |
| e4mc | 6.2.1+modern | Required external Fabric dependency | Upstream MIT; not bundled |
| Netty Incubator Codec QUIC fork | 0.0.74.Final.e4mc.1 | Source/ABI for Android native | Apache-2.0 + NOTICE |
| `libnetty_quiche.so` | Android API 21 / arm64-v8a | Bundled JNI native | SHA-256 `c6fa5084948d013d998d908ec4d5733f878c613107121a38594ef53e29f882fa` |
| Cloudflare quiche | pinned by Netty fork | Native QUIC implementation | BSD-style license |
| BoringSSL and incorporated components | pinned by Netty fork | TLS/crypto in native | Netty-supplied license bundle |

Because e4mc is installed separately, DroidBridge LAN does not redistribute e4mc's shaded Java dependencies.
