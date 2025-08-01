package ru.otus.protobuf.client;

public record GRPCClientConfig(
        String host, int port, int firstValue, int lastValue, int iterations, long sleepMillis, int streamTimeout) {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;
    private static final int FIRST_VALUE = 0;
    private static final int LAST_VALUE = 30;
    private static final int CLIENT_ITERATIONS = 50;
    private static final long SLEEP_MILLIS = 1000;
    private static final int STREAM_TIMEOUT_SECONDS = 10;

    public static GRPCClientConfig defaultConfig() {
        return new GRPCClientConfig(
                SERVER_HOST,
                SERVER_PORT,
                FIRST_VALUE,
                LAST_VALUE,
                CLIENT_ITERATIONS,
                SLEEP_MILLIS,
                STREAM_TIMEOUT_SECONDS);
    }
}
