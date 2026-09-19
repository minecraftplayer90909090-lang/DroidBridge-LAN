# Publication status

DroidBridge LAN 0.4.0 is prepared as a standalone compatibility add-on for the official e4mc mod.

The previously tested embedded compatibility build proved the Android networking path end-to-end. Version 0.4.0 changes the packaging so the installed e4mc JAR remains separate and unmodified; this exact standalone build still needs one final real-device regression test before public upload.

Before publishing:

1. Test 0.4.0 with the official e4mc JAR installed separately.
2. Set e4mc as a required CurseForge dependency.
3. Verify the current CurseForge rules/fields in the website UI, since live web access was unavailable while this package was prepared.
4. Publish only the Minecraft versions actually tested.

Because e4mc is no longer bundled, DroidBridge LAN does not redistribute e4mc's shaded Java dependencies or need to present them as components of this project.
