package com.cannontech.common.util;

/**
 * This type was created in VisualAge.
 */

import java.io.FileWriter;
import java.io.PrintWriter;

public class FileMessageLog implements com.cannontech.common.util.MessageEventListener 
{
	private String fileName = "/message.log";
	private String filePath = CtiUtilities.getLogDirPath() + fileName;
/**
 * FileMessageLog constructor comment.
 */
public FileMessageLog() {
	super(); 
	initialize(filePath);
}
/**
 * FileMessageLog constructor comment.
 */
public FileMessageLog(String logFileName) {
	super();
	initialize(logFileName);
}
/**
 * This method was created in VisualAge.
 * @param out java.io.File
 */
private void initialize(String fileName) {

	this.filePath = fileName;
		
	write("\n" + new java.util.Date() + " - Starting logfile...");
}
/**
 * messageEvent method comment.
 */
public void messageEvent(com.cannontech.common.util.MessageEvent event) {

	write( event.toString() );
}
/**
 * This method was created in VisualAge.
 * @param str java.lang.String
 */
private void write(String str) {

	try
	{
		FileWriter fWriter = new FileWriter(this.filePath, true);
		PrintWriter pWriter = new PrintWriter(fWriter);
	
		pWriter.println(str);
		fWriter.close();
	}
	catch( java.io.IOException e )
	{
		try
		{
			com.cannontech.clientutils.CTILogger.info("Unable to write to " + filePath +", writting to " + fileName );
			FileWriter fWriter = new FileWriter(this.fileName, true);
			PrintWriter pWriter = new PrintWriter(fWriter);
		
			pWriter.println(str);
			fWriter.close();
		}
		catch(java.io.IOException ex )
		{
			com.cannontech.clientutils.CTILogger.info("Unable to write to " + fileName + ", not writting file.");
		}
	}
}
}
