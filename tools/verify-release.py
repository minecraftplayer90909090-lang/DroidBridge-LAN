#!/usr/bin/env python3
from pathlib import Path
import hashlib
import json
import struct
import sys
import zipfile

EXPECTED_NATIVE_SHA256 = "c6fa5084948d013d998d908ec4d5733f878c613107121a38594ef53e29f882fa"

if len(sys.argv) != 2:
    raise SystemExit("Usage: python tools/verify-release.py <DroidBridge-LAN.jar>")
jar = Path(sys.argv[1])
if not jar.is_file():
    raise SystemExit(f"Not a file: {jar}")

required = {
    "fabric.mod.json",
    "droidbridge-lan.mixins.json",
    "assets/droidbridgelan/icon.png",
    "META-INF/LICENSE-DroidBridge-LAN.txt",
    "META-INF/THIRD_PARTY_NOTICES.md",
    "META-INF/droidbridge/android-arm64-v8a/libnetty_quiche.so",
    "META-INF/licenses/netty-Apache-2.0.txt",
    "META-INF/licenses/netty-NOTICE.txt",
    "META-INF/licenses/quiche-BSD.txt",
    "META-INF/licenses/boringssl-LICENSE.txt",
}

with zipfile.ZipFile(jar) as z:
    names = set(z.namelist())
    missing = required - names
    if missing:
        raise SystemExit("Missing release entries:\n  " + "\n  ".join(sorted(missing)))

    if any(n.startswith("link/e4mc/") for n in names):
        raise SystemExit("Release unexpectedly redistributes e4mc classes")
    if any(n.startswith("META-INF/jars/e4mc") for n in names):
        raise SystemExit("Release unexpectedly bundles an e4mc JAR")
    if any(n.startswith("net/fabricmc/api/ModInitializer") or n.startswith("org/spongepowered/asm/mixin/") for n in names):
        raise SystemExit("Compile-only Fabric/Mixin stubs leaked into the release JAR")

    meta = json.loads(z.read("fabric.mod.json").decode("utf-8"))
    if "e4mc" not in meta.get("depends", {}):
        raise SystemExit("fabric.mod.json does not require e4mc")
    pre = meta.get("entrypoints", {}).get("preLaunch", [])
    if "ca.dnamobile.droidbridgelan.DroidBridgeLanPreLaunch" not in pre:
        raise SystemExit("preLaunch compatibility entrypoint is missing")

    native = z.read("META-INF/droidbridge/android-arm64-v8a/libnetty_quiche.so")
    native_sha = hashlib.sha256(native).hexdigest()
    if native_sha != EXPECTED_NATIVE_SHA256:
        raise SystemExit(f"Native SHA mismatch: {native_sha}")
    if len(native) < 20 or native[:4] != b"\x7fELF" or native[4] != 2 or native[5] != 1:
        raise SystemExit("Native is not ELF64 little-endian")
    machine = struct.unpack_from("<H", native, 18)[0]
    if machine != 183:
        raise SystemExit(f"Native e_machine is {machine}, expected AArch64 183")

print(f"OK: {jar.name}")
print(f"JAR SHA-256: {hashlib.sha256(jar.read_bytes()).hexdigest()}")
print(f"Native SHA-256: {EXPECTED_NATIVE_SHA256}")
