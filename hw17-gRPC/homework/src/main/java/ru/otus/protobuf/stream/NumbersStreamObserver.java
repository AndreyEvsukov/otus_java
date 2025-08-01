package ru.otus.protobuf.stream;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.generated.ServerResponse;

public class NumbersStreamObserver implements StreamObserver<ServerResponse> {
    private static final Logger logger = LoggerFactory.getLogger(NumbersStreamObserver.class);
    // Для хранения последнего полученного значения
    private final AtomicLong lastServerValue = new AtomicLong(0);
    // Для ожидания завершения стрима
    private final CountDownLatch completionLatch = new CountDownLatch(1);

    @Override
    public void onNext(ServerResponse serverResponse) {
        logger.info("Last value: {}", serverResponse.getServerValue());
        lastServerValue.set(serverResponse.getServerValue());
    }

    @Override
    public void onError(Throwable t) {
        logger.error("Error: {}", t.getMessage());
        completionLatch.countDown();
    }

    @Override
    public void onCompleted() {
        logger.info("Completed");
        completionLatch.countDown();
    }

    public long getLastServerValue() {
        return this.lastServerValue.getAndSet(0);
    }

    public CountDownLatch getCompletionLatch() {
        return completionLatch;
    }
}
