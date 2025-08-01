package ru.otus.protobuf.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.generated.ClientRequest;
import ru.otus.protobuf.generated.RemoteDBServiceGrpc;
import ru.otus.protobuf.stream.NumbersStreamObserver;

public class RemoteDBServiceClient implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(RemoteDBServiceClient.class);

    private final ManagedChannel channel;
    private final RemoteDBServiceGrpc.RemoteDBServiceStub asyncStub;

    public RemoteDBServiceClient(String host, int port) {
        this.channel =
                ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.asyncStub = RemoteDBServiceGrpc.newStub(channel);
    }

    public NumbersStreamObserver startStream(int firstValue, int lastValue) throws InterruptedException {
        ClientRequest request = ClientRequest.newBuilder()
                .setFirstValue(firstValue)
                .setLastValue(lastValue)
                .build();

        CountDownLatch latch = new CountDownLatch(1); // Используем для ожидания начала стрима или ошибки
        NumbersStreamObserver observer = new NumbersStreamObserver();

        asyncStub.getServerResponse(request, observer);

        boolean latchReachedZero = latch.await(5, TimeUnit.SECONDS);
        if (latchReachedZero) {
            logger.info("Initial response or stream termination signal received within timeout.");
        } else {
            logger.info(
                    "Timeout waiting for initial response or stream termination signal from server for range {} to {}",
                    firstValue,
                    lastValue);
        }

        return observer;
    }

    @Override
    public void close() {
        try {
            logger.info("Shutting down gRPC channel...");
            if (channel != null && !channel.isShutdown()) {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                logger.info("gRPC channel shut down successfully.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Interrupted while shutting down gRPC channel", e);
            if (!channel.isTerminated()) {
                channel.shutdownNow();
            }
        }
    }
}
