package com.cannontech.core.dao;

import java.util.List;
import java.util.Vector;

import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CommandDao {

    public Vector<LiteDeviceTypeCommand> getAllDevTypeCommands(String deviceType);

    public Vector<LiteDeviceTypeCommand> getAllDevTypeCommands(int commandID);

    /**
     * Returns Vector of LiteCommands for Category = category
     * @param category
     * @return
     */
    public Vector<LiteCommand> getAllCommandsByCategory(String category);
    
    /**
     * Returns Vector of LiteCommands for Category = category
     * Use sorted parameter to turn off default sorting based on LiteComparators.liteCommandComparator
     * and use ordering defined in database.
     * @param category
     * @param sorted
     * @return
     */
    public Vector<LiteCommand> getAllCommandsByCategory(String category, boolean sorted);

    public LiteCommand getCommand(int cmdID);

    public LiteDeviceTypeCommand getDeviceTypeCommand(int devTypeCmdID);

    /** 
     * Replace the Default value prompt with the prompted input text.
     * @param valueString
     * @param parent
     * @return
     */
    public String loadPromptValue(String valueString, java.awt.Component parent);

    /**
     * Get list of commands the user is authorized to execute. 
     * @param user
     * @return
     */
    public List<LiteCommand> getAuthorizedCommands(List<LiteCommand> possibleCommands, LiteYukonUser user);
}