package ru.otus.jdbc.exceptions;

public class WrongClassConfigException extends RuntimeException {
    public WrongClassConfigException(String message) {
        super(message);
    }
}
