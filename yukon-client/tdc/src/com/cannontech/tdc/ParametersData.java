package com.cannontech.tdc;

/**
 * Insert the type's description here.
 * Creation date: (2/9/00 5:22:02 PM)
 * @author: 
 */
import java.util.Vector;

import com.cannontech.tdc.utils.TDCDefines;

public class ParametersData 
{
	/* CHANGE THIS VALUE EVERY TIME A NEW PARAMETER IS ADDED!!!!!! */
	public static final int NUMBER_OF_PARAMETERS = 12;
	
	private Vector fileParameters = null;
	private boolean parametersSuccessfull = true;
	private int vectorIndex = 0;

	// parameters
	private String displayName = null;
	private String displayType = null;
	private int frameX = 0;
	private int frameY = 0;
	private int frameWidth = 0;
	private int frameHeight = 0;		
	private String fontName = null;
	private int fontSize = 0;
	private boolean hGridLine = false;
	private boolean vGridLine = false;
	private boolean messageLog = false;
	private boolean toolBox = false;
/**
 * ParametersData constructor comment.
 */
public ParametersData() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/00 9:21:28 AM)
 * @return java.lang.String
 */
public String getDisplayName() {
	return displayName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/00 10:33:32 AM)
 * @return java.lang.String
 */
public java.lang.String getDisplayType() {
	return displayType;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/00 9:21:28 AM)
 * @return java.lang.String
 */
public String getFonName() {
	return fontName;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/00 9:21:28 AM)
 * @return java.lang.String
 */
public int getFontSize() {
	return fontSize;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/00 9:21:28 AM)
 * @return java.lang.String
 */
public int getFrameHeight() {
	return frameHeight;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/00 9:21:28 AM)
 * @return java.lang.String
 */
public int getFrameWidth() {
	return frameWidth;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/00 9:21:28 AM)
 * @return java.lang.String
 */
public int getFrameX() {
	return frameX;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/00 9:21:28 AM)
 * @return java.lang.String
 */
public int getFrameY() {
	return frameY;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/00 9:21:28 AM)
 * @return java.lang.String
 */
public boolean getHGridLine() 
{
	return hGridLine;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/00 9:21:28 AM)
 * @return java.lang.String
 */
public boolean getMessageLog() 
{
	return messageLog;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/00 9:21:28 AM)
 * @return java.lang.String
 */
public boolean getToolBox() 
{
	return toolBox;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/00 9:21:28 AM)
 * @return java.lang.String
 */
public boolean getVGridLine() 
{
	return vGridLine;
}
/**
 * Insert the method's description here.
 * Creation date: (2/9/00 5:22:33 PM)
 */
private void initialize() 
{

	if( parseInputFile() )
	{
		displayName = fileParameters.elementAt( vectorIndex++ ).toString();
		displayType = fileParameters.elementAt( vectorIndex++ ).toString();
		
		initializeFrameAppearance();
		initializeFont();
		initializeCheckItems();		
	}
	else
		parametersSuccessfull = false;
}
/**
 * Insert the method's description here.
 * Creation date: (2/9/00 5:28:32 PM)
 */
private void initializeCheckItems() 
{
	hGridLine = new Boolean( fileParameters.elementAt(vectorIndex++).toString() ).booleanValue();
	vGridLine = new Boolean( fileParameters.elementAt(vectorIndex++).toString() ).booleanValue();
	messageLog = new Boolean( fileParameters.elementAt(vectorIndex++).toString() ).booleanValue();
	toolBox = new Boolean( fileParameters.elementAt(vectorIndex++).toString() ).booleanValue();
}
/**
 * Insert the method's description here.
 * Creation date: (2/9/00 5:28:32 PM)
 */
private void initializeFont() 
{
	fontName = fileParameters.elementAt(vectorIndex++).toString();
	fontSize = new Integer( fileParameters.elementAt(vectorIndex++).toString() ).intValue();
}
/**
 * Insert the method's description here.
 * Creation date: (2/9/00 5:28:32 PM)
 */
private void initializeFrameAppearance() 
{
	frameX = Integer.parseInt( fileParameters.elementAt(vectorIndex++).toString() );
	frameY = Integer.parseInt( fileParameters.elementAt(vectorIndex++).toString() );
	frameWidth = Integer.parseInt( fileParameters.elementAt(vectorIndex++).toString() );
	frameHeight = Integer.parseInt( fileParameters.elementAt(vectorIndex++).toString() );	
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/00 11:03:30 AM)
 * @return boolean
 */
public boolean parametersExist() 
{
	return parametersSuccessfull;
}
/**
 * This method was created in VisualAge.
 */
private boolean parseInputFile() 
{

	java.io.RandomAccessFile file = null;
	
	fileParameters = new Vector( 40 );
	
	java.io.File checkFile = new java.io.File( TDCDefines.OUTPUT_FILE_NAME );
	
	try
	{
		// open file		
		if( checkFile.exists() )
		{
			file = new java.io.RandomAccessFile( checkFile, "r" );
					
			long filePointer = 0;
			long length = file.length();

				
			while ( filePointer < length )  // loop until the end of the file
			{
					
				String line = file.readLine();  // read a line in

				fileParameters.addElement( line );

				// set our pointer to the new position in the file
				filePointer = file.getFilePointer();
			}
		}
		else
			return false;

		// Close file
		file.close();						
	}
	catch(java.io.IOException ex)
	{		
		return false;
	}
	finally
	{
		try
		{
			if( checkFile.exists() )
				file.close();
		}
		catch( java.io.IOException ex )
		{}		
	}

	// make sure we received all lines from the parameters file
	if( fileParameters.size() != NUMBER_OF_PARAMETERS )
		return false;
	else
		return true;
}
}
