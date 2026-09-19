package ca.dnamobile.droidbridgelan;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

/**
 * Runs before normal Fabric mod initializers so stock e4mc sees a healthy config
 * and a verified Android Netty-Quiche native staged for DroidBridge LibPatcher.
 */
public final class DroidBridgeLanPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        DroidBridgeGate.Verification verification = DroidBridgeGate.verify();
        if (!verification.allowed) {
            DroidBridgeLanMod.log("prelaunch compatibility skipped: " + verification.reason);
            return;
        }

        E4mcConfigRecovery.recoverMalformedConfig();
        boolean prepared = AndroidE4mcNativeBootstrap.ensureLoaded();
        DroidBridgeLanMod.log("prelaunch Android e4mc compatibility nativePrepared=" + prepared);
    }
}
