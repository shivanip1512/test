package com.cannontech.database.cache.functions;

import java.util.Vector;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class CommandFuncs {

	public static final char DEFAULT_VALUE_PROMPT = '?'; //the chars that follow the question mark(?), may be grouped by quotes(" or '), is the value 
														 //that will be displayed on a prompt.  The returned prompted value will replace the ?string value.

	/**
	 * PointFuncs constructor comment.
	 */
	private CommandFuncs() {
		super();
	}
	
	public static Vector getAllDevTypeCommands(String deviceType)
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		//Contains LiteDeviceTypeCommand values
		Vector commands = new Vector();
		synchronized (cache)
		{
			java.util.List devTypeCmds = cache.getAllDeviceTypeCommands();
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
	 
	 
	/**
	 * Returns Vector of LiteCommands for Category = category
	 * @param category
	 * @return
	 */
	public static Vector getAllCommandsByCategory(String category)
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		//Contains LiteCommands
		Vector commands = new Vector();

		synchronized (cache)
		{
			java.util.List cmds = cache.getAllCommands();
			LiteCommand liteCmd = null;

			for (int i = 0; i < cmds.size(); i++)
			{
				liteCmd = (LiteCommand)cmds.get(i);
				if( category.equalsIgnoreCase(liteCmd.getCategory()))
					commands.add(liteCmd);	
			}
		}
		java.util.Collections.sort(commands, LiteComparators.liteCommandComparator);
		return commands;
	}

	
	public static LiteCommand getCommand(int cmdID)
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		LiteCommand liteCommand = null;
		synchronized (cache)
		{
			liteCommand = (LiteCommand)cache.getAllCommandsMap().get(new Integer(cmdID));
		}

		return liteCommand;
	}
	/** 
	 * Replace the Default value prompt with the prompted input text.
	 * @param valueString
	 * @param parent
	 * @return
	 */
	public static String loadPromptValue(String valueString, java.awt.Component parent )
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
}
