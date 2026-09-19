# Security policy

## Reporting

Report DroidBridge LAN security issues privately to the project owner before opening a public issue when practical.

## Native integrity

The Android Netty-Quiche native is verified at runtime using a pinned SHA-256 before use. The release process must update the expected hash only after rebuilding from the documented source/workflow and inspecting the resulting ELF/dependencies.

## Public hosting warning

Opening a world through e4mc intentionally makes that Minecraft LAN server reachable through a public hostname. Share that hostname only with people you intend to allow to connect.
