package com.cannontech.fdemulator.exception;

public class ThreadInterruptedException extends RuntimeException {
    public ThreadInterruptedException(InterruptedException ie) {
        super(ie);
    }
}
