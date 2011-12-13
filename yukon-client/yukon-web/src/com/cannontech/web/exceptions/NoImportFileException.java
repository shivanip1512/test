package com.cannontech.web.exceptions;

public class NoImportFileException extends Exception {
    public NoImportFileException() {
    }

    public NoImportFileException(String message) {
        super(message);
    }

    public NoImportFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoImportFileException(Throwable cause) {
        super(cause);
    }
}
