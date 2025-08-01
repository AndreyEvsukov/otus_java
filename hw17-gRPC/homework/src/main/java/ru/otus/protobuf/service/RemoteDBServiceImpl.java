package ru.otus.protobuf.service;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.generated.ClientRequest;
import ru.otus.protobuf.generated.RemoteDBServiceGrpc;
import ru.otus.protobuf.generated.ServerResponse;

public class RemoteDBServiceImpl extends RemoteDBServiceGrpc.RemoteDBServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(RemoteDBServiceImpl.class);

    @Override
    public void getServerResponse(ClientRequest request, StreamObserver<ServerResponse> responseObserver) {
        final var firstValue = request.getFirstValue();
        final var lastValue = request.getLastValue();

        logger.info("Client wants to get values from {} to {}", firstValue, lastValue);
        for (long i = firstValue; i <= lastValue; i++) {
            responseObserver.onNext(getServerResponse(i));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ServerInterruptedException("Server has been interrupted", e);
            }
        }
        responseObserver.onCompleted();
    }

    private ServerResponse getServerResponse(long responseValue) {
        return ServerResponse.newBuilder().setServerValue(responseValue).build();
    }
}
