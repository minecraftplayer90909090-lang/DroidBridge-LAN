package ca.dnamobile.droidbridgelan;

final class DbLanLog {
    private DbLanLog() {}

    static void info(String message) {
        System.out.println("DroidBridgeLAN: " + message);
    }
}
