package ru.otus.protobuf.client;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.stream.NumbersStreamObserver;

public class GRPCClient {
    private static final Logger logger = LoggerFactory.getLogger(GRPCClient.class);

    public static void main(String[] args) {
        GRPCClientConfig config = GRPCClientConfig.defaultConfig(); // Или загрузить из файла/аргументов

        try (RemoteDBServiceClient client = new RemoteDBServiceClient(config.host(), config.port())) {
            NumbersStreamObserver observer = client.startStream(config.firstValue(), config.lastValue());

            long currentValue = 0;
            boolean interrupted = false;
            for (int i = 0; i < config.iterations(); i++) {
                long lastValue = observer.getLastServerValue();
                currentValue = currentValue + lastValue + 1;
                logger.info("Current value: {}", currentValue);

                if (!sleepAndCheckInterrupted(config.sleepMillis())) {
                    interrupted = true;
                    break; // Выходим из цикла, если поток прерван
                }
            }

            logger.info("Waiting for server stream to complete...");
            if (!interrupted) {
                if (observer.getCompletionLatch().await(config.streamTimeout(), TimeUnit.SECONDS)) {
                    logger.info("Server stream completed successfully or with error.");
                } else {
                    logger.info("Timed out waiting for server stream to complete.");
                }
            } else {
                logger.info("Main loop was interrupted, not waiting for stream completion.");
            }

        } catch (Exception e) { // Более общее исключение
            logger.error("Error occurred in GRPCClient", e);
            Thread.currentThread().interrupt();
        }
    }

    private static boolean sleepAndCheckInterrupted(long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Восстанавливаем флаг прерывания
            logger.warn("Sleep was interrupted");
            return false;
        }
    }
}
