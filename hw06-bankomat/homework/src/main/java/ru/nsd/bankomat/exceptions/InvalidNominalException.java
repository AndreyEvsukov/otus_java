package ru.nsd.bankomat.exceptions;

public class InvalidNominalException extends RuntimeException {
    public InvalidNominalException(int nominal) {
        super("Не могу принять банкноту номинала " + nominal);
    }
}
