package com.cannontech.web.tools.commander.model;

import java.util.List;

public class CommandRequestException extends RuntimeException {

    private List<CommandRequest> requests;
    private CommandRequestExceptionType type;
    
    public CommandRequestException(String message, List<CommandRequest> requests, CommandRequestExceptionType type) {
        super(message);
        this.requests = requests;
        this.type = type;
    }

    public CommandRequestException(String message, Throwable cause, List<CommandRequest> requests, CommandRequestExceptionType type) {
        super(message, cause);
        this.requests = requests;
        this.type = type;
    }
    
    public List<CommandRequest> getRequests() {
        return requests;
    }
    
    public CommandRequestExceptionType getType() {
        return type;
    }
    
}