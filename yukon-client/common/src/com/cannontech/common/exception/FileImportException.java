package com.cannontech.common.exception;

public class FileImportException extends Exception {
    public FileImportException() {
    }

    public FileImportException(String message) {
        super(message);
    }

    public FileImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileImportException(Throwable cause) {
        super(cause);
    }

}
