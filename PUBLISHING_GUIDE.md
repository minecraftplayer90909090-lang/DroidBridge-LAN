# Publishing guide

## 1. Set the GitHub owner

```bash
python tools/set-github-owner.py YOUR_GITHUB_USERNAME_OR_ORG
```

Repository name: `DroidBridge-LAN`.

## 2. Build

Open the root in IntelliJ IDEA as a Gradle project using JDK 17, or run:

```bash
./gradlew clean build
```

## 3. Install the required upstream dependency for testing

Use the official e4mc 6.2.1+modern JAR separately from DroidBridge LAN. Do not place a patched/nested DroidBridge e4mc build in the instance.

## 4. Test 0.4.1

- Launch Minecraft 26.1.2 Fabric in DroidBridge.
- Confirm Mod Menu lists **DroidBridge LAN** and **e4mc** as separate mods with separate icons.
- Open a world to LAN and wait for the `*.e4mc.link` hostname.
- Join from another Java account on a different network such as LTE/5G.
- Test disconnect/reconnect and stop hosting.
- Check the log for the DroidBridge LAN prelaunch native staging and runtime Dialtone-disable messages.

## 5. CurseForge

Use `CURSEFORGE_DESCRIPTION.md` and `CURSEFORGE_UPLOAD_FIELDS.md`.

Configure the official **e4mc** project as a **required dependency** rather than uploading or bundling it with DroidBridge LAN.

Before publishing, verify the current CurseForge rules/fields in the website UI, since those rules could not be checked live while this package was prepared.
