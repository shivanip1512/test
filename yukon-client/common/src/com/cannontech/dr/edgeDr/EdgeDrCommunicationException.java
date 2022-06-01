package com.cannontech.dr.edgeDr;

/**
 * Exception thrown when there is an Edge DR communication error.
 */
public class EdgeDrCommunicationException extends RuntimeException {

    public EdgeDrCommunicationException(String message) {
        super(message);
    }

    public EdgeDrCommunicationException(String message, Throwable t) {
        super(message, t);
    }
}
