package com.cannontech.web.exceptions;

public class EmptyImportFileException extends Exception {
    public EmptyImportFileException() {
    }

    public EmptyImportFileException(String message) {
        super(message);
    }

    public EmptyImportFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyImportFileException(Throwable cause) {
        super(cause);
    }
}
