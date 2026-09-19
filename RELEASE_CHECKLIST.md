# Public release checklist

## Before the first CurseForge upload

- [ ] Replace every `REPLACE-WITH-YOUR-GITHUB` placeholder with the real repository owner/path.
- [ ] Verify the current CurseForge project/dependency rules before publishing; live web access was unavailable while this package was prepared.
- [ ] Set the official **e4mc** CurseForge project as a **required dependency**.
- [ ] Test the exact 0.4.0 JAR on a real DroidBridge device with the official, unmodified e4mc JAR installed separately.
- [ ] Confirm the e4mc public domain appears and a second Java client can join from another network.
- [ ] Run a longer remote session and test disconnect/reconnect/stop hosting.
- [ ] Confirm there are no `UnsatisfiedLinkError`, `libgcc_s.so.1`, iroh native, config-parse, or native-hash failures.

## Packaging checks

- [x] DroidBridge LAN has its own icon/branding.
- [x] The release JAR does not contain a nested e4mc JAR.
- [x] The release JAR does not contain `link/e4mc/*` classes.
- [x] `fabric.mod.json` declares e4mc as a required dependency.
- [x] Netty Apache-2.0 license + NOTICE are included for the bundled Android native.
- [x] quiche and BoringSSL license texts are included.
- [ ] Generate and publish the final JAR SHA-256.

## CurseForge page checks

- [ ] Project name is **DroidBridge LAN**.
- [ ] Short description identifies it as an Android compatibility add-on for e4mc.
- [ ] Upstream e4mc is credited and linked.
- [ ] e4mc is configured as a required dependency rather than bundled.
- [ ] Only genuinely tested Minecraft versions are selected.
