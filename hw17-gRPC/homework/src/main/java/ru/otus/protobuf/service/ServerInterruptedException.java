package ru.otus.protobuf.service;

public class ServerInterruptedException extends RuntimeException {
    public ServerInterruptedException(String message, Throwable ex) {
        super(message, ex);
    }
}
