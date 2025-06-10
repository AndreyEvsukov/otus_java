package ru.otus.jdbc.exceptions;

public class CustomJdbcMappingException extends RuntimeException {
    public CustomJdbcMappingException(String message) {
        super(message);
    }

    public CustomJdbcMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
