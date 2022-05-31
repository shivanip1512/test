package com.cannontech.dr.edgeDr;

/**
 * Exception thrown when there is an Edge DR communication error.
 */
public class EdgeDrCommunicationException extends RuntimeException {
    private String DEFAULT_ERROR = "A communication error has occurred. Please see logs for more details";
    private String message;

    public EdgeDrCommunicationException() {

    }

    public EdgeDrCommunicationException(Throwable t) {
        super(t);
    }
    
    public EdgeDrCommunicationException(String message) {
        this.message = message;
    }

    public EdgeDrCommunicationException(Throwable t, String message) {
        super(t);
        this.message = message;
    }

    public String getMessage() {
        if (message.isBlank()) {
            return DEFAULT_ERROR;
        }

        return message;
    }

}
