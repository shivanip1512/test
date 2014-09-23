package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CommandDao {
    
    // The chars that follow the question mark(?), may be grouped by quotes(" or '), is the value 
    // that will be displayed on a prompt.  The returned prompted value will replace the ?string value.
    public final static char DEFAULT_VALUE_PROMPT = '?';
    
    List<LiteDeviceTypeCommand> getAllDevTypeCommands(String type);
    
    List<LiteDeviceTypeCommand> getAllDevTypeCommands(int commandId);
    
    List<LiteCommand> getAllCommandsByCategory(String category);
    
    LiteCommand getCommand(int commandId);
    
    /**
     * Filter list of commands down to the ones the user is authorized to execute. 
     */
    List<LiteCommand> filterCommandsForUser(Iterable<LiteCommand> commands, LiteYukonUser user);
    
    /** Retrieves all device type commands from the database. */
    List<LiteDeviceTypeCommand> getAllDeviceTypeCommands();
    
    /** Retrieves a device type command from the database. */
    LiteDeviceTypeCommand getDeviceTypeCommand(int deviceCommandId);
    
    /** Retrieves all commands from the database. */
    List<LiteCommand> getAllCommands();
    
}