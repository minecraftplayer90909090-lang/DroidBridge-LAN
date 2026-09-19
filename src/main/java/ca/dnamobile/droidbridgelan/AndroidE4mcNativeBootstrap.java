package ca.dnamobile.droidbridgelan;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Locale;

/**
 * Android native bootstrap for e4mc 6.2.1 / Netty-Quiche 0.0.74.Final.e4mc.1.
 *
 * DroidBridge LAN bundles only the Android/Bionic Netty-Quiche native that matches
 * e4mc 6.2.1. It does not bundle or modify e4mc itself.
 *
 * The native is staged before e4mc initializes and exposed through the
 * link.e4mc.native_path system property. DroidBridge's existing e4mc LibPatcher
 * bridge then owns the actual System.load from its system-classloader context.
 * This avoids loading the same JNI library from two different classloaders while
 * keeping the upstream e4mc/Quiche classes untouched.
 */
public final class AndroidE4mcNativeBootstrap {
    private static final String ABI_VERSION = "0.0.74.Final.e4mc.1";
    private static final String RESOURCE =
            "META-INF/droidbridge/android-arm64-v8a/libnetty_quiche.so";
    private static final String EXPECTED_SHA256 =
            "c6fa5084948d013d998d908ec4d5733f878c613107121a38594ef53e29f882fa";
    private static final Object LOCK = new Object();
    private static volatile boolean ready;

    private AndroidE4mcNativeBootstrap() {}

    public static boolean ensurePrepared() {
        if (ready) return true;
        synchronized (LOCK) {
            if (ready) return true;
            try {
                return prepareInternal();
            } catch (Throwable t) {
                DroidBridgeLanMod.log("e4mc Android native bootstrap failed: " + summarize(t));
                return false;
            }
        }
    }

    /**
     * Stage and verify the Android native, but deliberately do not call System.load().
     *
     * DroidBridge LibPatcher patches stock e4mc's Quiche.loadNativeLibrary() and
     * loads link.e4mc.native_path from the system-classloader side. Loading here
     * first would make Android reject LibPatcher's second load with
     * "Native Library ... is being loaded in another classloader", after which
     * the legacy fallback path would be selected.
     *
     * The historical method name is retained so existing call sites remain binary
     * compatible; a true return value means "verified native prepared".
     */
    public static boolean ensureLoaded() {
        return ensurePrepared();
    }

    private static boolean prepareInternal() throws Exception {
        // Respect an existing override only when it is exactly the pinned verified build.
        String override = System.getProperty("link.e4mc.native_path");
        if (override != null && !override.trim().isEmpty()) {
            File file = new File(override);
            if (isExpectedNative(file)) {
                ready = true;
                DroidBridgeLanMod.log("e4mc Android native override accepted path="
                        + file.getAbsolutePath() + " sha256=" + EXPECTED_SHA256);
                return true;
            }
            DroidBridgeLanMod.log("e4mc Android native override ignored because it does not match "
                    + "the pinned arm64-v8a build: " + override);
        }

        File dir = nativeDir();
        if (!dir.isDirectory() && !dir.mkdirs() && !dir.isDirectory()) {
            throw new IllegalStateException("cannot create native cache " + dir);
        }

        File nativeFile = new File(dir, "libnetty_quiche.so");
        if (isExpectedNative(nativeFile)) {
            configure(nativeFile);
            DroidBridgeLanMod.log("e4mc Android bundled native cache accepted path="
                    + nativeFile.getAbsolutePath() + " sha256=" + EXPECTED_SHA256);
            return true;
        }

        File part = new File(dir, "libnetty_quiche.so.part");
        if (part.exists() && !part.delete()) {
            throw new IllegalStateException("cannot replace stale native temp file " + part);
        }

        try (InputStream raw = AndroidE4mcNativeBootstrap.class.getClassLoader()
                     .getResourceAsStream(RESOURCE)) {
            if (raw == null) {
                throw new IllegalStateException("bundled Android native resource missing: " + RESOURCE);
            }
            try (BufferedInputStream in = new BufferedInputStream(raw);
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(part))) {
                byte[] buffer = new byte[32 * 1024];
                int read;
                long total = 0;
                while ((read = in.read(buffer)) != -1) {
                    total += read;
                    if (total > 64L * 1024L * 1024L) {
                        throw new IllegalStateException("bundled native exceeds 64 MiB");
                    }
                    out.write(buffer, 0, read);
                }
            }
        }

        if (!isAarch64Elf(part)) {
            part.delete();
            throw new IllegalStateException("bundled native is not ELF64 little-endian AArch64");
        }

        String actualHash = sha256(part);
        if (!EXPECTED_SHA256.equalsIgnoreCase(actualHash)) {
            part.delete();
            throw new SecurityException("bundled native SHA-256 mismatch expected="
                    + EXPECTED_SHA256 + " actual=" + actualHash);
        }

        try {
            Files.move(part.toPath(), nativeFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception atomicMoveFailed) {
            Files.move(part.toPath(), nativeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        configure(nativeFile);
        DroidBridgeLanMod.log("e4mc Android bundled native staged path="
                + nativeFile.getAbsolutePath() + " bytes=" + nativeFile.length()
                + " sha256=" + EXPECTED_SHA256);
        return true;
    }

    private static void configure(File nativeFile) {
        nativeFile.setReadable(true, false);
        nativeFile.setExecutable(true, false);
        System.setProperty("link.e4mc.native_path", nativeFile.getAbsolutePath());
        ready = true;
    }

    private static boolean isExpectedNative(File file) {
        if (!isAarch64Elf(file)) return false;
        try {
            return EXPECTED_SHA256.equalsIgnoreCase(sha256(file));
        } catch (Exception e) {
            return false;
        }
    }

    private static File nativeDir() {
        String base = System.getProperty("java.io.tmpdir");
        if (base == null || base.trim().isEmpty()) base = System.getProperty("user.home", ".");
        return new File(new File(base, "droidbridge-e4mc-native"), ABI_VERSION);
    }

    private static boolean isAarch64Elf(File file) {
        if (file == null || !file.isFile() || file.length() < 64) return false;
        byte[] h = new byte[20];
        try (FileInputStream in = new FileInputStream(file)) {
            if (in.read(h) != h.length) return false;
        } catch (Exception e) {
            return false;
        }
        if ((h[0] & 0xff) != 0x7f || h[1] != 'E' || h[2] != 'L' || h[3] != 'F') return false;
        if ((h[4] & 0xff) != 2 || (h[5] & 0xff) != 1) return false;
        int machine = (h[18] & 0xff) | ((h[19] & 0xff) << 8);
        return machine == 183;
    }

    private static String sha256(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            byte[] b = new byte[32 * 1024];
            int r;
            while ((r = in.read(b)) != -1) md.update(b, 0, r);
        }
        StringBuilder sb = new StringBuilder(64);
        for (byte b : md.digest()) sb.append(String.format(Locale.ROOT, "%02x", b & 0xff));
        return sb.toString();
    }

    private static String summarize(Throwable t) {
        String m = t.getMessage();
        return t.getClass().getSimpleName() + (m == null ? "" : ": " + m);
    }
}
