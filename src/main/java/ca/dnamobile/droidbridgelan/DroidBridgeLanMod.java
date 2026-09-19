package ca.dnamobile.droidbridgelan;

import net.fabricmc.api.ModInitializer;

/**
 * DroidBridge compatibility add-on for the official e4mc mod.
 *
 * e4mc owns the tunnel, domain assignment, and Minecraft networking lifecycle.
 * DroidBridge LAN only provides the Android-native compatibility required by
 * DroidBridge and disables e4mc's Android-incompatible Dialtone/iroh path at
 * runtime. The installed e4mc JAR is not modified or redistributed.
 */
public final class DroidBridgeLanMod implements ModInitializer {
    public static final String MOD_ID = "droidbridge_lan";
    public static final String MOD_VERSION = "0.4.1";

    private static volatile boolean allowed;
    private static volatile boolean e4mcPresent;

    @Override
    public void onInitialize() {
        DroidBridgeGate.Verification verification = DroidBridgeGate.verify();
        allowed = verification.allowed;
        if (!allowed) {
            log("DroidBridge-only gate denied: " + verification.reason);
            return;
        }

        e4mcPresent = detectE4mc();
        if (!e4mcPresent) {
            // Fabric should normally prevent this because e4mc is a required dependency.
            log("required e4mc dependency was not detected");
            return;
        }

        boolean androidNativeReady = AndroidE4mcNativeBootstrap.ensureLoaded();
        disableE4mcDialtoneOnAndroid();

        log("DroidBridge-only gate passed package=" + verification.packageName);
        log("official e4mc dependency detected; upstream JAR remains external and unmodified");
        log("e4mc Android native prepared=" + androidNativeReady
                + (androidNativeReady ? " path=" + System.getProperty("link.e4mc.native_path") : ""));
    }

    public static void onLanStarted(int port, Object serverConnection) {
        if (!allowed) return;
        if (port <= 0 || port > 65535) {
            log("Ignoring invalid LAN port " + port);
            return;
        }

        if (e4mcPresent || detectE4mc()) {
            e4mcPresent = true;
            boolean nativeReady = AndroidE4mcNativeBootstrap.ensureLoaded();
            disableE4mcDialtoneOnAndroid();
            log("LAN opened on localhost:" + port
                    + "; official e4mc will create the public tunnel nativePrepared=" + nativeReady);
        }
    }

    /** Compatibility no-op retained for the mixin call site. */
    public static void stopTunnel() {
        // e4mc owns its own lifecycle and stops the public host when Minecraft closes LAN.
    }

    private static void disableE4mcDialtoneOnAndroid() {
        try {
            ClassLoader loader = DroidBridgeLanMod.class.getClassLoader();
            Class<?> configClass = Class.forName("link.e4mc.Config", true, loader);
            Object config = configClass.getField("INSTANCE").get(null);
            boolean hostDisabled = setTrackedOverrideFalse(configClass, config, "dialtoneHostEnabled");
            boolean playerDisabled = setTrackedOverrideFalse(configClass, config, "dialtonePlayerEnabled");
            log("e4mc Android compatibility: Dialtone/iroh disabled for this runtime"
                    + " host=" + hostDisabled + " player=" + playerDisabled
                    + "; classic Quiclime remains enabled");
        } catch (Throwable t) {
            log("e4mc Android compatibility: failed to disable Dialtone/iroh: " + summarize(t));
        }
    }

    private static boolean setTrackedOverrideFalse(Class<?> configClass, Object config, String fieldName) {
        try {
            Object trackedValue = configClass.getField(fieldName).get(config);
            Class<?> trackedValueClass = Class.forName(
                    "folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue",
                    false,
                    DroidBridgeLanMod.class.getClassLoader());
            trackedValueClass.getMethod("setOverride", Object.class).invoke(trackedValue, Boolean.FALSE);
            return true;
        } catch (Throwable t) {
            log("e4mc Android compatibility: could not override " + fieldName + ": " + summarize(t));
            return false;
        }
    }

    private static boolean detectE4mc() {
        try {
            ClassLoader loader = DroidBridgeLanMod.class.getClassLoader();
            Class.forName("link.e4mc.E4mcClient", false, loader);
            Class.forName("link.e4mc.QuiclimeSession", false, loader);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static String summarize(Throwable t) {
        String m = t.getMessage();
        return t.getClass().getSimpleName() + (m == null ? "" : ": " + m);
    }

    public static void log(String message) {
        DbLanLog.info(message);
    }
}
