package com.cannontech.yc.gui;

/**
 * Insert the type's description here.
 * Creation date: (5/17/2002 11:19:32 AM)
 * @author: 
 */
public class YCDefaults
{
	public static final String YC_DEFAULTS_FILENAME = "\\YCOptions.DAT";
	public static final String YC_DEFAULTS_DIRECTORY = com.cannontech.common.util.CtiUtilities.getConfigDirPath();

	private final int NUMBER_OF_PARAMETERS = 5;
	
	private int commandPriority = 14;
	private boolean queueExecuteCommand = false;
	private boolean showMessageLog = true;
	private boolean confirmCommandExecute = false;
	private String commandFileDirectory = null;
/**
 * YCDefaults constructor comment.
 */
public YCDefaults() {
	super();
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
	
	//parseDefaultsFile();
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
 * Insert the method's description here.
 * Creation date: (5/13/2002 9:53:47 AM)
 */
private void parseDefaultsFile()
{
	java.io.RandomAccessFile raFile = null;
	java.io.File inFile = new java.io.File( YC_DEFAULTS_DIRECTORY + YC_DEFAULTS_FILENAME);

	java.util.Vector fileParameters = new java.util.Vector( 15 );
	
	try
	{
		// open file		
		if( inFile.exists() )
		{
			raFile = new java.io.RandomAccessFile( inFile, "r" );
					
			long readLinePointer = 0;
			long fileLength = raFile.length();

			while ( readLinePointer < fileLength )  // loop until the end of the file
			{
					
				String line = raFile.readLine();  // read a line in
				fileParameters.addElement( line );

				// set our pointer to the new position in the file
				readLinePointer = raFile.getFilePointer();
			}
		}
		else
			return;

		// Close file
		raFile.close();						
	}
	catch(java.io.IOException ex)
	{
		System.out.print("IOException in parseDefaultsFile()");
		ex.printStackTrace();
	}
	finally
	{
		try
		{
			//Try to close the file, again.
			if( inFile.exists() )
				raFile.close();
		}
		catch( java.io.IOException ex )
		{}		
	}

	if( fileParameters.size() == NUMBER_OF_PARAMETERS )
	{
		// make sure we received all lines from the parameters file		
		updateFileDefaults( fileParameters);
	}
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
 * Insert the method's description here.
 * Creation date: (5/14/2002 1:54:19 PM)
 * @param infileDefaultsVector java.util.Vector
 */
private  void updateFileDefaults(java.util.Vector infileDefaultsVector)
{
	int index = 0;
	//*******priority**************//
	int p = new Integer((String)infileDefaultsVector.get(index++)).intValue();
	if( p < 1 )
		p = 14;
	setCommandPriority( p );
	
	//*******queue command**************//
	String boolValue = (String)infileDefaultsVector.get(index++);
	if(boolValue.equalsIgnoreCase("true") )
		setQueueExecuteCommand(true);
	else
		setQueueExecuteCommand(false);

	//*******show log**************//
	boolValue = (String)infileDefaultsVector.get(index++);
	if(boolValue.equalsIgnoreCase("true") )
		setShowMessageLog(true);
	else
		setShowMessageLog(false);

	//*******confirm command**************//
	boolValue = (String)infileDefaultsVector.get(index++);
	if(boolValue.equalsIgnoreCase("true") )
		setConfirmCommandExecute(true);
	else
		setConfirmCommandExecute(false);

	//*******command file dir**************//		
	setCommandFileDirectory((String)infileDefaultsVector.get(index++));
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2002 9:23:11 AM)
 */
public void writeDefaultsFile()
{
	try
	{
		java.io.File file = new java.io.File( YC_DEFAULTS_DIRECTORY );
		file.mkdirs();
		
		java.io.FileWriter writer = new java.io.FileWriter( file.getPath() + YC_DEFAULTS_FILENAME);

 		writer.write( String.valueOf( getCommandPriority() ) + "\r\n");

		writer.write( String.valueOf( getQueueExecuteCommand() ) + "\r\n" );
			
		writer.write( String.valueOf( getShowMessageLog()) + "\r\n" );

		writer.write( String.valueOf( getConfirmCommandExecute()) + "\r\n" );

		writer.write( getCommandFileDirectory() + "\r\n" );

		writer.close();
	}
	catch ( java.io.IOException e )
	{
		System.out.print(" IOException in writeBillingDefaultsFile");
		e.printStackTrace();
	}
	
}
}
