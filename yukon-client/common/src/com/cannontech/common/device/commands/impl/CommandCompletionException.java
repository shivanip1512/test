package com.cannontech.common.device.commands.impl;

public class CommandCompletionException extends Exception {

    public CommandCompletionException(String string, Throwable cause) {
        super(string,cause);
    }

    public CommandCompletionException(String string) {
        super(string);
    }

}
