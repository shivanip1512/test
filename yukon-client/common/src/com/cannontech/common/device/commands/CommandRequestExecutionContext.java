package com.cannontech.common.device.commands;

import com.cannontech.database.data.lite.LiteYukonUser;

public class CommandRequestExecutionContext {

    private int id;
    private CommandRequestExecutionType type;
    private LiteYukonUser user;
    private boolean noqueue;
    private int priority;
    
    public CommandRequestExecutionContext(int id, CommandRequestExecutionType type, LiteYukonUser user) {
        this.id = id;
        this.type = type;
        this.user = user;
        this.noqueue = type.isNoqueue();
        this.priority = type.getPriority();
    }
    
    public CommandRequestExecutionContext withNoqueue(Boolean noqueue) {
        
        CommandRequestExecutionContext newContext = this.clone();
        newContext.noqueue = noqueue;

        return newContext;
    }
    
    public CommandRequestExecutionContext withPriority(int priority) {
        
        CommandRequestExecutionContext newContext = this.clone();
        newContext.priority = priority;

        return newContext;
    }
    
    public CommandRequestExecutionContext clone() {
        
        CommandRequestExecutionContext newContext = new CommandRequestExecutionContext(this.id, this.type, this.user);
        newContext.noqueue = this.noqueue;
        newContext.priority = this.priority;
        
        return newContext;
    }
    
    public int getId() {
        return id;
    }
    public CommandRequestExecutionType getType() {
        return type;
    }
    public LiteYukonUser getUser() {
        return user;
    }
    public boolean isNoqueue() {
        return noqueue;
    }
    public int getPriority() {
        return priority;
    };
   
}
