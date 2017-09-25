package com.cannontech.common.exception;

public class EmptyImportFileException extends FileImportException {
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
