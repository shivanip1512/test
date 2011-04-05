package com.cannontech.device.range;

public class InvalidRangeException extends RuntimeException{

    public InvalidRangeException() {
        super("Error parsing range string");
    }
}
