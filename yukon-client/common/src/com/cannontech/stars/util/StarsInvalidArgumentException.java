package com.cannontech.stars.util;

import java.util.Map;
import java.util.Map.Entry;

import com.cannontech.stars.util.StarsClientRequestException;

public class StarsInvalidArgumentException extends StarsClientRequestException {

    // name, value pairs for arguments
    private Map<String, String> arguments;

    public StarsInvalidArgumentException() {
        super("Invalid argument");
    }

    public StarsInvalidArgumentException(String message) {
        super(message);
    }

    public StarsInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public StarsInvalidArgumentException(Map<String, String> arguments) {
        super(getFormattedMessage(arguments));
        this.arguments = arguments;
    }

    public StarsInvalidArgumentException(Map<String, String> arguments,
            String message) {
        super(message);
        this.arguments = arguments;
    }

    public StarsInvalidArgumentException(Map<String, String> arguments,
            Throwable cause) {
        super(getFormattedMessage(arguments), cause);
        this.arguments = arguments;
    }

    public StarsInvalidArgumentException(Map<String, String> arguments,
            String message, Throwable cause) {
        super(message, cause);
        this.arguments = arguments;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    private static String getFormattedMessage(Map<String, String> arguments) {

        StringBuffer fmtMessage = new StringBuffer("Invalid argument: ");

        if (arguments != null) {
            for (Entry<String, String> arg : arguments.entrySet()) {
                fmtMessage.append(arg.getKey() + "=[" + arg.getValue() + "] ");
            }
        }
        return fmtMessage.toString();

    }
}
