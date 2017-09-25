package com.cannontech.common.exception;

public class NoImportFileException extends FileImportException {
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
