package com.cannontech.billing.mainprograms;

import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (5/6/2002 10:55:56 AM)
 * @author: 
*/
//import java.util.Vector;
public class BillingFileDefaults
{
	public static final String BILLING_DEFAULTS_FILENAME = "\\BillingDefaultSetting.DAT";
	public static final String BILLING_DEFAULTS_DIRECTORY = com.cannontech.common.util.CtiUtilities.getConfigDirPath();
	//Number of members in this class.
	private final int NUMBER_OF_PARAMETERS = 6;

	private int formatID = -1;
	private int demandDaysPrev = 30;
	private int energyDaysPrev = 7;
	//private String[] collectionGroup = null;
	private java.util.Vector billGroup = null;
	private String outputFileDir = null;

	//USED BUT NOT STORED IN THE FILE BECAUSE WE CAN'T PREDICT THIS, YET?
	private String inputFileDir = null;
	private java.util.Date endDate = null;
	private java.util.Date energyStartDate = null;
	private java.util.Date demandStartDate = null;


	private String billGroupColumn = "COLLECTIONGROUP";
/**
 * DynamicBilling constructor comment.
 */
public BillingFileDefaults()
{
	super();
	parseDefaultsFile();
}
/**
 * DynamicBilling constructor comment.
 */
public BillingFileDefaults(int newFormatID, int newDemandDays, int newEnergyDays, 
				String newSingleBillGrp, String newBillGrpColumn, String newOutFile, 
				String newInFile, java.util.Date newEndDate)
{
	super();
	setFormatID( newFormatID );
	setDemandDaysPrev( newDemandDays );
	setEnergyDaysPrev( newEnergyDays );
	setBillGroup( newSingleBillGrp );
	setBillGroupColumn( newBillGrpColumn );
	setOutputFile( newOutFile );
	
	setInputFile( newInFile );
	setEndDate( newEndDate );
}
/**
 * DynamicBilling constructor comment.
 */
public BillingFileDefaults(int newFormatID, int newDemandDays, int newEnergyDays, 
				java.util.Vector newBillGrp, String newBillGrpColumn, String newOutFile, 
				String newInFile, java.util.Date newEndDate)
{
	super();
	setFormatID( newFormatID );
	setDemandDaysPrev( newDemandDays );
	setEnergyDaysPrev( newEnergyDays );
	setBillGroup( newBillGrp );
	setBillGroupColumn( newBillGrpColumn);
	setOutputFile( newOutFile );

	setInputFile( newInFile );
	setEndDate( newEndDate );
}
public java.util.Vector getBillGroup()
{
	return billGroup;
}
public String getBillGroupColumn()
{
	return billGroupColumn;
}
public java.util.Date getBillingEndDate()
{
	if( endDate == null)
	{
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.set(java.util.GregorianCalendar.HOUR, 0);
		cal.set(java.util.GregorianCalendar.MINUTE, 0);
		cal.set(java.util.GregorianCalendar.SECOND, 0);
		cal.set(java.util.GregorianCalendar.MILLISECOND, 0);
		
		endDate = new java.util.Date();
		endDate = cal.getTime();
	}
	return endDate;
}
public int getDemandDaysPrev()
{
	return demandDaysPrev;
}
public java.util.Date getDemandStartDate()
{
	demandStartDate = new java.util.Date();
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	
	//need to subtract one day because start date is at least one
	// date value less than endDate.
	// Ex.  0 days previous is >= today 00:00:00 and < tomorrow 00:00:00
	long daysPrev = 86400000  * new Integer(getDemandDaysPrev()  + 1 ).longValue();
	long time = getEndDate().getTime() - daysPrev;

	demandStartDate.setTime(time);
	cal.setTime(demandStartDate);

	cal.set(java.util.Calendar.HOUR_OF_DAY,0);
	cal.set(java.util.Calendar.MINUTE,0);
	cal.set(java.util.Calendar.SECOND,0);
	cal.set(java.util.Calendar.MILLISECOND,0);

	demandStartDate = cal.getTime();
	//System.out.println(" DEMAND START DATE = " + demandStartDate);
	//System.out.println(" $  AFTER END DATE = " + endDate);

	return demandStartDate;
}
public java.util.Date getEndDate()
{
	if( endDate == null)
	{
		endDate = new java.util.Date();
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();

		// add a whole day to now so we can increment the Date field
		long time = endDate.getTime() + 86400000;
		endDate.setTime(time);

		cal.setTime(endDate);
			
		cal.set(java.util.Calendar.HOUR_OF_DAY,0);
		cal.set(java.util.Calendar.MINUTE,0);
		cal.set(java.util.Calendar.SECOND,0);
		cal.set(java.util.Calendar.MILLISECOND,0);

		endDate = cal.getTime();
	}
	return endDate;
}
public int getEnergyDaysPrev()
{
	return energyDaysPrev;
}
public java.util.Date getEnergyStartDate()
{
	energyStartDate = new java.util.Date();
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	
	//need to subtract one day because start date is at least one
	// date value less than endDate.
	// Ex.  0 days previous is >= today 00:00:00 and < tomorrow 00:00:00
	long daysPrev = 86400000  * new Integer(getEnergyDaysPrev() + 1).longValue();
	long time = getEndDate().getTime() - daysPrev;

	energyStartDate.setTime(time);
	cal.setTime(energyStartDate);

	cal.set(java.util.Calendar.HOUR_OF_DAY,0);
	cal.set(java.util.Calendar.MINUTE,0);
	cal.set(java.util.Calendar.SECOND,0);
	cal.set(java.util.Calendar.MILLISECOND,0);
	energyStartDate= cal.getTime();
	//System.out.println(" ENERGY START DATE = " + energyStartDate);
	//System.out.println(" END DATE = " + endDate);
	
	return energyStartDate;
}
public int getFormatID ()
{
	if( formatID < 0)
	{
		//set default value
		if( com.cannontech.billing.FileFormatTypes.getValidFormatIDs().length > 0 )
			formatID = com.cannontech.billing.FileFormatTypes.getValidFormatIDs()[0];
			
	}
	return formatID;
}
public String getInputFileDir()
{
	if( inputFileDir == null)
	{
		String directory = com.cannontech.common.util.CtiUtilities.getCanonicalFile(com.cannontech.common.util.CtiUtilities.getExportDirPath());
		inputFileDir = directory + "inFile.txt";
	}
	return inputFileDir;
}
public String getOutputFileDir()
{
	if (outputFileDir == null)
	{
		String directory = com.cannontech.common.util.CtiUtilities.getCanonicalFile(com.cannontech.common.util.CtiUtilities.getExportDirPath());
		outputFileDir = directory + "outFile.txt";
	}
	return outputFileDir;
}
/**
 * Insert the method's description here.
 * Creation date: (5/8/2002 3:22:40 PM)
 */
public void initialize() 
{
	
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2002 9:53:47 AM)
 */
public void parseDefaultsFile()
{
	java.io.RandomAccessFile raFile = null;
	java.io.File inFile = new java.io.File( BILLING_DEFAULTS_DIRECTORY + BILLING_DEFAULTS_FILENAME);

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
//We are attempting to parse a string that may have more than one collection
// group in it, separated by commas and saving them in the collectionGroup string array
// These are the collection groups the user last used.
// Ex.  collection1,collection2,collection3...

private void setBillGroup(String newBillGrpString)
{
	int beginIndex = 0;
	int endIndex = newBillGrpString.indexOf(",");

	String temp = newBillGrpString.substring(beginIndex, endIndex);

	if( endIndex < 0)
	{
		billGroup = new java.util.Vector(1);
		billGroup.add(newBillGrpString);
	}
	else
	{	
		billGroup = new java.util.Vector();
		int count = 0;
		
		while ( endIndex > 0)			//returns -1 "," not found
		{
			temp = newBillGrpString.substring(beginIndex, endIndex);

			billGroup.add(count++, temp);
			beginIndex = endIndex + 1;

			newBillGrpString = newBillGrpString.substring(beginIndex);			
			endIndex = newBillGrpString.indexOf(",");

		}
	}
}
private void setBillGroup(java.util.Vector newBillGroup)
{
	billGroup = newBillGroup;
}
public void setBillGroupColumn(String newBillGroupColumn)
{
	billGroupColumn = newBillGroupColumn;
}
public void setDemandDaysPrev(int newDemandDaysPrev)
{
	demandDaysPrev = newDemandDaysPrev;
}
private void setDemandStartDate(java.util.Date newDemandStartDate)
{
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();

	// add a whole day to now so we can increment the Date field
	long time = newDemandStartDate.getTime() + 86400000;
	newDemandStartDate.setTime(time);
	
	cal.setTime(newDemandStartDate);

	cal.set(java.util.Calendar.HOUR_OF_DAY,0);
	cal.set(java.util.Calendar.MINUTE,0);
	cal.set(java.util.Calendar.SECOND,0);
	cal.set(java.util.Calendar.MILLISECOND,0);

	newDemandStartDate = cal.getTime();

	System.out.println(" end date = " + getDemandStartDate());
	//getDemandStartDate();
}
public void setEndDate(java.util.Date newEndDate)
{
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	// add a whole day to now so we can increment the Date field
	long day = 86400000;
	long time = newEndDate.getTime() + day;

	endDate = new java.util.Date(time);
	cal.setTime(endDate);

	cal.set(java.util.Calendar.HOUR_OF_DAY,0);
	cal.set(java.util.Calendar.MINUTE,0);
	cal.set(java.util.Calendar.SECOND,0);
	cal.set(java.util.Calendar.MILLISECOND,0);

	endDate = cal.getTime();
	getDemandStartDate();
	getEnergyStartDate();
}
public void setEnergyDaysPrev(int newEnergyDaysPrev)
{
	energyDaysPrev = newEnergyDaysPrev;
}
public void setFormatID(int newFormatID)
{
	formatID = newFormatID;
}
public void setInputFile(String newInputFile)
{
	inputFileDir = newInputFile;
}
public void setOutputFile(String newOutputFile)
{
	outputFileDir = newOutputFile;
}
/**
 * Insert the method's description here.
 * Creation date: (5/14/2002 1:54:19 PM)
 * @param infileDefaultsVector java.util.Vector
 */
public void updateFileDefaults(java.util.Vector infileDefaultsVector)
{
	int index = 0;
	setFormatID( (new Integer((String)infileDefaultsVector.get(index++))).intValue());
	//setEndDate(new java.util.Date((String)infileDefaultsVector.get(index++)));
	setDemandDaysPrev((new Integer((String)infileDefaultsVector.get(index++))).intValue());
	setEnergyDaysPrev((new Integer((String)infileDefaultsVector.get(index++))).intValue());
	setBillGroup((String)infileDefaultsVector.get(index++));
	setOutputFile((String)infileDefaultsVector.get(index++));
	
	if( index < infileDefaultsVector.size())
		setBillGroupColumn((String)infileDefaultsVector.get(index++));
	//setInputFile((String)infileDefaultsVector.get(index++));
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2002 9:23:11 AM)
 */
public void writeDefaultsFile()
{
	try
	{
		java.io.File file = new java.io.File( BILLING_DEFAULTS_DIRECTORY );
		file.mkdirs();
		
		java.io.FileWriter writer = new java.io.FileWriter( file.getPath() + BILLING_DEFAULTS_FILENAME);
		//java.io.File outPutFile = new java.io.File( BILLING_DEFAULTS_FILENAME );
		// write the current formatID
 		writer.write( String.valueOf( getFormatID() ) + "\r\n");

 		//NOT USED YET?ANYMORE?
		// write the current default end date 
		//writer.write( getEndDate() + "\r\n" );

		// write the demand days prev
		writer.write( String.valueOf( getDemandDaysPrev() ) + "\r\n" );
			
		// write the energy days prev
		writer.write( String.valueOf( getEnergyDaysPrev() ) + "\r\n" );

		String collGrpString = new String();
		// write the last collection group
		for (int i = 0; i < getBillGroup().size(); i++)
		{
			collGrpString += getBillGroup().get(i) + ",";
		}			
		writer.write( collGrpString + "\r\n" );

		// write the default output file dir
		writer.write( getOutputFileDir() + "\r\n" );

		// write the type of bill Group being used (MUST BE A DB COLUMN FROM DEVICEMETERGROUP table)
		writer.write( getBillGroupColumn() + "\r\n" );

		writer.close();
	}
	catch ( java.io.IOException e )
	{
		System.out.print(" IOException in writeBillingDefaultsFile");
		e.printStackTrace();
	}
	
}
}
