package com.cannontech.yc.gui;

import com.cannontech.common.util.KeysAndValues;

/**
 * Insert the type's description here.
 * Creation date: (5/17/2002 11:19:32 AM)
 * @author: 
 */
public class YCDefaults
{
	public static final String YC_DEFAULTS_FILENAME = "/YCOptions.DAT";
	public static final String YC_DEFAULTS_DIRECTORY = com.cannontech.common.util.CtiUtilities.getConfigDirPath();

	private final String COMMAND_PRIORITY_KEY = "PRIORITY";
	private final String QUEUE_COMMAND_KEY = "QUEUE";
	private final String SHOW_MESSAGE_LOG_KEY = "SHOWLOG";
	private final String CONFIRM_COMMAND_KEY = "CONFIRM";
	private final String COMMAND_FILE_DIR_KEY = "FILEDIR";
	private final String OUTPUT_DIVIDER_LOC_KEY = "DIVIDER";
		
	private int commandPriority = 14;
	private boolean queueExecuteCommand = false;
	private boolean showMessageLog = true;
	private boolean confirmCommandExecute = false;
	private String commandFileDirectory = null;
	private int outputDividerLoc = 190;
	
//	private final String VALID_TEXT = "Valid Text";
//	private final String INVALID_TEXT = "Invalid Text";
//	private final String DISPLAY_TEXT = "Display Text";
	
	private java.awt.Color validTextColor = java.awt.Color.blue;
	private java.awt.Color invalidTextColor = java.awt.Color.red;
	private java.awt.Color displayTextColor = java.awt.Color.black;
	
	/**
	 * YCDefaults constructor comment.
	 */
	public YCDefaults() {
		this(false);
	}
	public YCDefaults(boolean loadFromFile) {
		super();
		if( loadFromFile)
			parseDefaultsFile();
	}
	/**
	 * YCDefaults constructor comment.
	 */
	public YCDefaults(int priority, boolean queueCommand, boolean showLog, boolean confirmExecute, String commandFileDir) 
	{
		super();
		setCommandPriority(priority);
		setQueueExecuteCommand(queueCommand);
		setShowMessageLog(showLog);
		setConfirmCommandExecute(confirmExecute);
		setCommandFileDirectory(commandFileDir);
	}
	/**
	 * YCDefaults constructor comment.
	 */
	public YCDefaults(int priority, boolean queueCommand, boolean showLog, boolean confirmExecute, String commandFileDir, int divLoc) 
	{
		super();
		setCommandPriority(priority);
		setQueueExecuteCommand(queueCommand);
		setShowMessageLog(showLog);
		setConfirmCommandExecute(confirmExecute);
		setCommandFileDirectory(commandFileDir);
		setOutputDividerLoc(divLoc);
	}
	public String getCommandFileDirectory()
	{
		if( commandFileDirectory == null)
		{
			String dirPath = com.cannontech.common.util.CtiUtilities.getCommandsDirPath();
			commandFileDirectory = com.cannontech.common.util.CtiUtilities.getCanonicalFile(dirPath); 
		}
		return commandFileDirectory;
	}
	public int getCommandPriority()
	{
		return commandPriority;
	}
	public boolean getConfirmCommandExecute ()
	{
		return confirmCommandExecute;
	}
	public boolean getQueueExecuteCommand ()
	{
		return queueExecuteCommand;
	}
	public boolean getShowMessageLog ()
	{
		return showMessageLog;
	}
	/**
	 * Parse the properties file (YC_DEFAULTS_DIRECTORY+YC_DEFAULTS_FILENAME, if exists)
	 *  for properties last saved values.
	 */
	private void parseDefaultsFile()
	{
		com.cannontech.common.util.KeysAndValuesFile kavFile = new com.cannontech.common.util.KeysAndValuesFile(YC_DEFAULTS_DIRECTORY + YC_DEFAULTS_FILENAME);
		com.cannontech.common.util.KeysAndValues keysAndValues = kavFile.getKeysAndValues();
		
		if( keysAndValues != null )
		{
			String keys[] = keysAndValues.getKeys();
			String values[] = keysAndValues.getValues();
			for (int i = 0; i < keys.length; i++)
			{
				if( keys[i] == "")
				{
					keys[i] = getYCKey(i);	//Temporary fix to get key if it doesn't exist.
				}
				
				if(keys[i].equalsIgnoreCase(COMMAND_PRIORITY_KEY))
				{
					setCommandPriority(Integer.parseInt(values[i].toString()));
				}
				else if( keys[i].equalsIgnoreCase(QUEUE_COMMAND_KEY))
				{
					setQueueExecuteCommand(Boolean.valueOf(values[i].toString()).booleanValue());
				}
				else if( keys[i].equalsIgnoreCase(SHOW_MESSAGE_LOG_KEY))
				{
					setShowMessageLog(Boolean.valueOf(values[i].toString()).booleanValue());
				}
				else if( keys[i].equalsIgnoreCase(CONFIRM_COMMAND_KEY))
				{
					setConfirmCommandExecute(Boolean.valueOf(values[i].toString()).booleanValue());
				}
				else if( keys[i].equalsIgnoreCase(COMMAND_FILE_DIR_KEY))
				{
					setCommandFileDirectory(values[i].toString());
				}
				else if( keys[i].equalsIgnoreCase(OUTPUT_DIVIDER_LOC_KEY))
				{
					setOutputDividerLoc(Integer.parseInt(values[i].toString()));
				}
			}
		}
		com.cannontech.clientutils.CTILogger.info( " LOADED commander properties from file.");
	}
	
	public String getYCKey(int index)
	{
		String key = "";
		switch (index)
		{
			case 0 :
				return COMMAND_PRIORITY_KEY;
			case 1 :
				return QUEUE_COMMAND_KEY;
			case 2 :
				return SHOW_MESSAGE_LOG_KEY;
			case 3 :
				return CONFIRM_COMMAND_KEY;
			case 4 :
				return COMMAND_FILE_DIR_KEY;
			case 5 :
				return OUTPUT_DIVIDER_LOC_KEY;
		}
		return key;
	}
	private void setCommandFileDirectory(String newDirectory)
	{
		commandFileDirectory = newDirectory;
	}
	private void  setCommandPriority(int newPriority)
	{
		if( newPriority > 14)
			commandPriority = 14;
		else if ( newPriority < 1)
			commandPriority = 1;
		else 
			commandPriority = newPriority;
	}
	private void setConfirmCommandExecute (boolean isConfirming)
	{
		confirmCommandExecute = isConfirming;
	}
	private void setQueueExecuteCommand (boolean isQueuing)
	{
		queueExecuteCommand = isQueuing;
	}
	private void setShowMessageLog (boolean isShowing)
	{
		showMessageLog = isShowing;
	}
	/**
	 * Write the TrendProperties to a file, TREND_DEFAULTS_DIRECTORY+TREND_DEFAULTS_FILENAME, 
	 * in a Keys=Values format (com.cannontech.common.util.KeysAndValues)
	 */
	public void writeDefaultsFile()
	{
		try
		{
			java.io.File file = new java.io.File(YC_DEFAULTS_DIRECTORY);
			file.mkdirs();

			java.io.FileWriter writer = new java.io.FileWriter( file.getPath() + YC_DEFAULTS_FILENAME);
			KeysAndValues keysAndValues = buildKeysAndValues();
			
			String keys[] = keysAndValues.getKeys();
			String values[] = keysAndValues.getValues();

			String endline = "\r\n";

			for (int i = 0; i < keys.length; i++)
			{
				writer.write(keys[i] + "=" + values[i] + endline);
			}
			writer.close();
		}
		catch ( java.io.IOException e )
		{
			System.out.print(" IOException in writeDatFile");
			e.printStackTrace();
		}
	}		
	
	/**
	 * Builds a KeysAndValues class with all ycProperties and their current values.
	 * Keys=Values format (com.cannontech.common.util.KeysAndValues)
	 * @return KeysAndValues
	 */
	private KeysAndValues buildKeysAndValues()
	{
		java.util.Vector keys = new java.util.Vector(10);
		java.util.Vector values = new java.util.Vector(10);

		keys.add(COMMAND_PRIORITY_KEY); 
		values.add(String.valueOf(getCommandPriority()));
	
		keys.add(QUEUE_COMMAND_KEY); 
		values.add(String.valueOf(getQueueExecuteCommand()));

		keys.add(SHOW_MESSAGE_LOG_KEY); 
		values.add(String.valueOf(getShowMessageLog()));

		keys.add(CONFIRM_COMMAND_KEY); 
		values.add(String.valueOf(getConfirmCommandExecute()));

		keys.add(COMMAND_FILE_DIR_KEY); 
		values.add(getCommandFileDirectory());

		keys.add(OUTPUT_DIVIDER_LOC_KEY);
		values.add(String.valueOf(getOutputDividerLoc()));
		
		String[] keysArray = new String[keys.size()];
		keys.toArray(keysArray);
		String[] valuesArray = new String[values.size()];
		values.toArray(valuesArray);
	
		KeysAndValues keysAndValues = new KeysAndValues(keysArray, valuesArray);

		return keysAndValues;
	}

	/**
	 * Returns the displayTextColor.
	 * @return java.awt.Color
	 */
	public java.awt.Color getDisplayTextColor()
	{
		return displayTextColor;
	}

	/**
	 * Returns the invalidTextColor.
	 * @return java.awt.Color
	 */
	public java.awt.Color getInvalidTextColor()
	{
		return invalidTextColor;
	}

	/**
	 * Returns the validTextColor.
	 * @return java.awt.Color
	 */
	public java.awt.Color getValidTextColor()
	{
		return validTextColor;
	}

	/**
	 * Sets the displayTextColor.
	 * @param displayTextColor The displayTextColor to set
	 */
	public void setDisplayTextColor(java.awt.Color displayTextColor)
	{
		this.displayTextColor = displayTextColor;
	}

	/**
	 * Sets the invalidTextColor.
	 * @param invalidTextColor The invalidTextColor to set
	 */
	public void setInvalidTextColor(java.awt.Color invalidTextColor)
	{
		this.invalidTextColor = invalidTextColor;
	}

	/**
	 * Sets the validTextColor.
	 * @param validTextColor The validTextColor to set
	 */
	public void setValidTextColor(java.awt.Color validTextColor)
	{
		this.validTextColor = validTextColor;
	}
	
	/**
	 * @return
	 */
	public int getOutputDividerLoc()
	{
		return outputDividerLoc;
	}

	/**
	 * @param i
	 */
	public void setOutputDividerLoc(int i)
	{
		outputDividerLoc = i;
	}

}
