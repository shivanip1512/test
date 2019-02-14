package com.cannontech.web.scheduledDataImport;

public class ScheduledDataImportException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ScheduledDataImportException() {
    }

    public ScheduledDataImportException(String message) {
        super(message);
    }

    public ScheduledDataImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScheduledDataImportException(Throwable cause) {
        super(cause);
    }
}
