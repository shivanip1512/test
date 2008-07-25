package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class CommandDaoImpl implements CommandDao {

	public final char DEFAULT_VALUE_PROMPT = '?'; //the chars that follow the question mark(?), may be grouped by quotes(" or '), is the value 
												  //that will be displayed on a prompt.  The returned prompted value will replace the ?string value.
    private IDatabaseCache databaseCache;
    private PaoCommandAuthorizationService paoCommandAuthorizationService;
    
	/**
	 * PointFuncs constructor comment.
	 */
	private CommandDaoImpl() {
		super();
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.CommandDao#getAllDevTypeCommands(java.lang.String)
     */
	public Vector<LiteDeviceTypeCommand> getAllDevTypeCommands(String deviceType)
	{
		//Contains LiteDeviceTypeCommand values
		Vector<LiteDeviceTypeCommand> commands = new Vector<LiteDeviceTypeCommand>();
		synchronized (databaseCache)
		{
			List<LiteDeviceTypeCommand> devTypeCmds = databaseCache.getAllDeviceTypeCommands();
			LiteDeviceTypeCommand liteDevTypeCmd = null;
	
			for (int i = 0; i < devTypeCmds.size(); i++)
			{
				liteDevTypeCmd = (LiteDeviceTypeCommand)devTypeCmds.get(i);
				if (liteDevTypeCmd.getDeviceType().equalsIgnoreCase(deviceType))
					commands.add( liteDevTypeCmd);
			}
		}
		java.util.Collections.sort(commands, LiteComparators.liteDeviceTypeCommandComparator);	
		return commands;
	}
	 
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.CommandDao#getAllDevTypeCommands(int)
     */
	public Vector<LiteDeviceTypeCommand> getAllDevTypeCommands(int commandID)
	{
		//Contains LiteDeviceTypeCommand values
		Vector<LiteDeviceTypeCommand> commands = new Vector<LiteDeviceTypeCommand>();
		synchronized (databaseCache)
		{
			List<LiteDeviceTypeCommand> devTypeCmds = databaseCache.getAllDeviceTypeCommands();
			LiteDeviceTypeCommand liteDevTypeCmd = null;
	
			for (int i = 0; i < devTypeCmds.size(); i++)
			{
				liteDevTypeCmd = (LiteDeviceTypeCommand)devTypeCmds.get(i);
				if (liteDevTypeCmd.getCommandID() == commandID )
					commands.add( liteDevTypeCmd);
			}
		}
		return commands;
	}	
	 
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.CommandDao#getAllCommandsByCategory(java.lang.String)
     */
	public Vector<LiteCommand> getAllCommandsByCategory(String category)
	{
		return getAllCommandsByCategory(category, true);
	}

	public Vector<LiteCommand> getAllCommandsByCategory(String category, boolean sorted)
    {
        //Contains LiteCommands
        Vector<LiteCommand> commands = new Vector<LiteCommand>();

        synchronized (databaseCache)
        {
            List<LiteCommand> cmds = databaseCache.getAllCommands();
            LiteCommand liteCmd = null;

            for (int i = 0; i < cmds.size(); i++)
            {
                liteCmd = (LiteCommand)cmds.get(i);
                if( category.equalsIgnoreCase(liteCmd.getCategory()))
                    commands.add(liteCmd);  
            }
        }
        if (sorted) {
            java.util.Collections.sort(commands, LiteComparators.liteCommandComparator);
        }
        return commands;
    }
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.CommandDao#getCommand(int)
     */
	public LiteCommand getCommand(int cmdID)
	{
		LiteCommand liteCommand = null;
		synchronized (databaseCache)
		{
			liteCommand = (LiteCommand)databaseCache.getAllCommandsMap().get(new Integer(cmdID));
		}

		return liteCommand;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.CommandDao#getDeviceTypeCommand(int)
     */
	public LiteDeviceTypeCommand getDeviceTypeCommand(int devTypeCmdID)
	{
		synchronized (databaseCache)
		{
			List<LiteDeviceTypeCommand> devTypeCmds = databaseCache.getAllDeviceTypeCommands();
			LiteDeviceTypeCommand ldtc = null;

			for (int i = 0; i < devTypeCmds.size(); i++)
			{
				ldtc = (LiteDeviceTypeCommand)devTypeCmds.get(i);
				if( devTypeCmdID == ldtc.getDeviceCommandID())
					return ldtc;	
			}
		}

		return null;
	}
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.CommandDao#loadPromptValue(java.lang.String, java.awt.Component)
     */
	public String loadPromptValue(String valueString, java.awt.Component parent )
	{
		int promptIndex = valueString.indexOf(DEFAULT_VALUE_PROMPT);
		while (promptIndex > -1)
		{
			int endIndex = -1;
			String promptString = valueString.substring(promptIndex+1).trim();	//clear all blanks at the begining of the prompt text
			String stringEnding = "";
			if( promptString.charAt(0) == '\'' || promptString.charAt(0) == '\"')//quoted prompt string
			{
				promptString = promptString.substring(1);	//remove quote from begining of strng
				endIndex = promptString.indexOf('\'');	//locate ending quote
				if( endIndex < 0 )
					endIndex = promptString.indexOf('\"');
			}
			else
			{
				endIndex = promptString.indexOf(" ");	//locate string end, space is separator
					
				if( promptString.indexOf('\'') > 0 && (promptString.indexOf('\'')< endIndex || endIndex < 0))
					endIndex = promptString.indexOf('\'');
				else if( promptString.indexOf('\"') > 0 && (promptString.indexOf('\"')< endIndex || endIndex < 0))
					endIndex = promptString.indexOf('\"');
					
				if( endIndex > 0)
					stringEnding +=promptString.charAt(endIndex);	//add the char back to the begining of the command.
			}
					
			if( endIndex > 0)
			{
				stringEnding += promptString.substring(endIndex+1);	//Store the end of the command string.
				promptString = promptString.substring(0, endIndex);	//truncate the end of the string to get just the prompt value.
			}
					
			String value = javax.swing.JOptionPane.showInputDialog(parent, promptString + ": ", "Enter the parameter value", javax.swing.JOptionPane.QUESTION_MESSAGE );
					
			if( value != null)
			{
				valueString = (valueString.substring(0, promptIndex) + value + stringEnding).trim();
				promptIndex = valueString.trim().indexOf(DEFAULT_VALUE_PROMPT);	//look for another prompt value
			}
			else
				return null;	//CANCEL
		}
		return valueString;
	}
	
	public List<LiteCommand> getAuthorizedCommands(List<LiteCommand> possibleCommands, LiteYukonUser user) {
		
        List<LiteCommand> authorizedCommands = new ArrayList<LiteCommand>();
        for (LiteCommand command : possibleCommands) {
        	if (paoCommandAuthorizationService.isAuthorized(user, command.getCommand())) {
        		authorizedCommands.add(command);
        	}
        }
        
        return authorizedCommands;
	}

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    @Required
	public void setPaoCommandAuthorizationService(PaoCommandAuthorizationService paoCommandAuthorizationService) {
		this.paoCommandAuthorizationService = paoCommandAuthorizationService;
	}

  
}
