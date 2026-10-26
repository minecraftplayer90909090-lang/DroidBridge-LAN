package ca.dnamobile.droidbridgelan;

import java.net.URL;
import java.util.Locale;

/**
 * Keeps DroidBridge-specific native/bootstrap behavior out of unrelated Java runtimes.
 */
final class DroidBridgeGate {
    static final String RELEASE_PACKAGE = "com.abhaythemaster.masterixplus";
    static final String DEBUG_PACKAGE = "com.abhaythemaster.masterixplus.debug";

    private DroidBridgeGate() {}

    static Verification verify() {
        String os = value("os.name").toLowerCase(Locale.ROOT);
        if (!os.contains("linux")) {
            return Verification.fail("not an Android/Linux JVM");
        }

        final Class<?> agentClass;
        try {
            agentClass = Class.forName("com.mio.libpatcher.MainAgent", false, ClassLoader.getSystemClassLoader());
        } catch (Throwable ignored) {
            return Verification.fail("DroidBridgeLibPatcher javaagent is not attached");
        }

        String agentSource = "";
        try {
            if (agentClass.getProtectionDomain() != null
                    && agentClass.getProtectionDomain().getCodeSource() != null) {
                URL location = agentClass.getProtectionDomain().getCodeSource().getLocation();
                agentSource = location == null ? "" : location.toString();
            }
        } catch (Throwable ignored) {
        }
        if (agentSource.isEmpty()) {
            try {
                URL resource = ClassLoader.getSystemResource("com/mio/libpatcher/MainAgent.class");
                agentSource = resource == null ? "" : resource.toString();
            } catch (Throwable ignored) {
            }
        }
        if (!agentSource.contains("DroidBridgeLibPatcher")) {
            return Verification.fail("javaagent is not DroidBridgeLibPatcher");
        }

        String runtimePaths = value("java.io.tmpdir") + "\n"
                + value("user.home") + "\n"
                + value("java.library.path") + "\n"
                + value("user.dir") + "\n"
                + agentSource;

        boolean release = runtimePaths.contains("/" + RELEASE_PACKAGE + "/")
                || runtimePaths.contains("/" + RELEASE_PACKAGE + "-");
        boolean debug = runtimePaths.contains("/" + DEBUG_PACKAGE + "/")
                || runtimePaths.contains("/" + DEBUG_PACKAGE + "-");
        if (!release && !debug) {
            return Verification.fail("JVM is not inside the DroidBridge Android app sandbox");
        }
        return Verification.ok(debug ? DEBUG_PACKAGE : RELEASE_PACKAGE, agentSource);
    }

    private static String value(String key) {
        try {
            return String.valueOf(System.getProperty(key, ""));
        } catch (Throwable ignored) {
            return "";
        }
    }

    static final class Verification {
        final boolean allowed;
        final String packageName;
        final String agentSource;
        final String reason;

        private Verification(boolean allowed, String packageName, String agentSource, String reason) {
            this.allowed = allowed;
            this.packageName = packageName;
            this.agentSource = agentSource;
            this.reason = reason;
        }

        static Verification ok(String packageName, String agentSource) {
            return new Verification(true, packageName, agentSource, "ok");
        }

        static Verification fail(String reason) {
            return new Verification(false, "", "", reason);
        }
    }
}
