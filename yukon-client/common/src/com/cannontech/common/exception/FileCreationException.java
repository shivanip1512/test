package com.cannontech.common.exception;

public class FileCreationException extends RuntimeException {
	private static final long serialVersionUID = 3576572985800813826L;

	public FileCreationException() {
    }

    public FileCreationException(String message) {
        super(message);
    }

    public FileCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileCreationException(Throwable cause) {
        super(cause);
    }
}
