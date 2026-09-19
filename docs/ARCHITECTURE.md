# Architecture

## Runtime path

```text
Minecraft Java on Android / DroidBridge
        |
        | Open to LAN
        v
e4mc 6.2.1 classic Quiclime
        |
        | Netty QUIC using Android/Bionic libnetty_quiche.so
        v
e4mc broker + selected relay
        |
        v
public *.e4mc.link hostname
        |
        v
remote Minecraft Java client
```

DroidBridge LAN does not forward the Minecraft TCP socket through a DNA Mobile server in the public configuration. e4mc owns the public domain and relay path.

## Android native bootstrap

`AndroidE4mcNativeBootstrap` extracts the bundled native into app-private temporary/cache storage, checks that it is ELF64 little-endian AArch64, validates its SHA-256, and sets `link.e4mc.native_path` before Quiche loads. DroidBridge LAN deliberately does not call `System.load()` itself; the existing DroidBridge LibPatcher e4mc bridge owns the actual load so Android sees a single native-library classloader owner.

## Dialtone/iroh

e4mc 6.2.1 can additionally use Dialtone/iroh. Its supplied Linux ARM64 JNI native is not Android/Bionic compatible. DroidBridge LAN therefore disables the host/player Dialtone flags in memory on DroidBridge and keeps classic Quiclime enabled.

## Config recovery

An abrupt device reboot can interrupt a config write and leave `config/e4mc/e4mc.toml` malformed. On DroidBridge, the pre-launch compatibility layer validates that TOML before e4mc/Kaleido initializes. If parsing fails with a known config parse exception, the file is renamed to a `.broken-*` backup and e4mc creates fresh defaults.
