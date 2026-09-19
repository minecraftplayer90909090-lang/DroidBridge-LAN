# GitHub repository setup

**Repository name:** `DroidBridge-LAN`  
**Visibility:** Public  
**Description:** `Host Minecraft Java LAN worlds publicly from DroidBridge on Android through an e4mc-compatible tunnel, with no router port forwarding.`

After extracting this package:

1. Run `python tools/set-github-owner.py YOUR_GITHUB_USERNAME_OR_ORG`.
2. Open the folder in GitHub Desktop and create/add the repository.
3. Commit the existing files as the initial public source release.
4. Publish the repository.
5. Open the Actions tab and make sure `Build DroidBridge LAN` succeeds.
6. Add the repository URL to the CurseForge project page.

The repository already contains build workflows, source, upstream modification diffs, native build source/workflow, issue templates, licensing notices, and release documentation.
