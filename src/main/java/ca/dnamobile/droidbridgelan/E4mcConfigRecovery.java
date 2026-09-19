package ca.dnamobile.droidbridgelan;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

/**
 * Protects stock e4mc from a truncated TOML left behind by an abrupt Android
 * process/device shutdown. No e4mc classes are initialized here.
 */
final class E4mcConfigRecovery {
    private static final String KALEIDO_TOML_PARSER =
            "folk.sisby.kaleido.lib.nightconfig.toml.TomlParser";

    private E4mcConfigRecovery() {}

    static void recoverMalformedConfig() {
        try {
            Path config = resolveConfigDir().resolve("e4mc").resolve("e4mc.toml");
            if (!Files.isRegularFile(config)) return;

            String text = Files.readString(config, StandardCharsets.UTF_8);
            Validation validation = validate(text);
            if (validation.valid) return;

            Path backup = nextBackup(config);
            Files.createDirectories(config.getParent());
            try {
                Files.move(config, backup, StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception atomicMoveFailed) {
                Files.move(config, backup, StandardCopyOption.REPLACE_EXISTING);
            }
            DroidBridgeLanMod.log("recovered malformed e4mc config before upstream initialization; "
                    + "preserved broken file at " + backup + " reason=" + validation.reason);
        } catch (Throwable t) {
            DroidBridgeLanMod.log("e4mc config preflight could not complete: " + summarize(t));
        }
    }

    private static Path resolveConfigDir() {
        // Prefer Fabric Loader without taking a compile-time dependency in this helper.
        try {
            Class<?> loaderClass = Class.forName("net.fabricmc.loader.api.FabricLoader", false,
                    E4mcConfigRecovery.class.getClassLoader());
            Object loader = loaderClass.getMethod("getInstance").invoke(null);
            Object result = loaderClass.getMethod("getConfigDir").invoke(loader);
            if (result instanceof Path) return (Path) result;
        } catch (Throwable ignored) {
        }

        String userDir = System.getProperty("user.dir", ".");
        return Paths.get(userDir).resolve("config").toAbsolutePath().normalize();
    }

    private static Validation validate(String text) {
        // Use the exact parser shaded by e4mc/Kaleido when it is already available
        // on Fabric's class path. Parsing alone is side-effect free and does not
        // register link.e4mc.Config, avoiding the initializer poisoning seen in 0.2.3.
        try {
            Class<?> parserClass = Class.forName(KALEIDO_TOML_PARSER, true,
                    E4mcConfigRecovery.class.getClassLoader());
            Object parser = parserClass.getDeclaredConstructor().newInstance();
            Method parse = parserClass.getMethod("parse", Reader.class);
            parse.invoke(parser, new StringReader(text));
            return Validation.ok();
        } catch (ClassNotFoundException unavailable) {
            return fallbackValidate(text);
        } catch (InvocationTargetException parseFailure) {
            Throwable cause = parseFailure.getCause() == null ? parseFailure : parseFailure.getCause();
            return Validation.fail(summarize(cause));
        } catch (Throwable parserProblem) {
            // Parser infrastructure itself failed. Fall back to a conservative check
            // rather than renaming a valid user config due to an unrelated class issue.
            return fallbackValidate(text);
        }
    }

    /** Conservative fallback for the flat e4mc TOML used by 6.2.1. */
    private static Validation fallbackValidate(String text) {
        boolean inMultilineString = false;
        String[] lines = text.split("\\R", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            if (line.contains("\"\"\"")) {
                int count = occurrences(line, "\"\"\"");
                if ((count & 1) == 1) inMultilineString = !inMultilineString;
                continue;
            }
            if (inMultilineString) continue;

            int eq = line.indexOf('=');
            if (eq <= 0 || eq == line.length() - 1) {
                return Validation.fail("incomplete TOML assignment at line " + (i + 1));
            }
            String value = stripComment(line.substring(eq + 1).trim());
            if (value.isEmpty()) {
                return Validation.fail("empty TOML value at line " + (i + 1));
            }
            if (value.startsWith("\"") && !hasClosingQuote(value)) {
                return Validation.fail("unterminated TOML string at line " + (i + 1));
            }
        }
        if (inMultilineString) return Validation.fail("unterminated multiline TOML string");
        return Validation.ok();
    }

    private static String stripComment(String value) {
        boolean quoted = false;
        boolean escaped = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\' && quoted) {
                escaped = true;
                continue;
            }
            if (c == '"') quoted = !quoted;
            if (c == '#' && !quoted) return value.substring(0, i).trim();
        }
        return value.trim();
    }

    private static boolean hasClosingQuote(String value) {
        boolean escaped = false;
        for (int i = 1; i < value.length(); i++) {
            char c = value.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') return true;
        }
        return false;
    }

    private static int occurrences(String text, String needle) {
        int count = 0;
        int at = 0;
        while ((at = text.indexOf(needle, at)) >= 0) {
            count++;
            at += needle.length();
        }
        return count;
    }

    private static Path nextBackup(Path config) {
        Path dir = config.getParent();
        String stamp = Long.toString(Instant.now().toEpochMilli());
        Path candidate = dir.resolve("e4mc.toml.broken-" + stamp);
        int suffix = 1;
        while (Files.exists(candidate)) {
            candidate = dir.resolve("e4mc.toml.broken-" + stamp + "-" + suffix++);
        }
        return candidate;
    }

    private static String summarize(Throwable t) {
        String m = t.getMessage();
        return t.getClass().getSimpleName() + (m == null ? "" : ": " + m);
    }

    private static final class Validation {
        final boolean valid;
        final String reason;

        private Validation(boolean valid, String reason) {
            this.valid = valid;
            this.reason = reason;
        }

        static Validation ok() { return new Validation(true, "ok"); }
        static Validation fail(String reason) { return new Validation(false, reason); }
    }
}
