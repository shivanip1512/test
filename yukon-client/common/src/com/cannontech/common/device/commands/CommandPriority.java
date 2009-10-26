package com.cannontech.common.device.commands;


public class CommandPriority {
    public static final int minPriority = 1;
    public static final int maxPriority = 14;
    
    public static boolean isCommandPriorityValid(int commandPriority){
        if(commandPriority >= minPriority &&
           commandPriority <= maxPriority)
            return true;
        
        return false;
    }
    
}