package com.cannontech.billing.mainprograms;

import com.cannontech.database.db.device.DeviceMeterGroup;;

/**
 * Insert the type's description here.
 * Creation date: (5/6/2002 10:55:56 AM)
 * @author: 
*/

public class BillingFileDefaults
{
	public static final String BILLING_DEFAULTS_FILENAME = "\\BillingDefaultSetting.DAT";
	public static final String BILLING_DEFAULTS_DIRECTORY = com.cannontech.common.util.CtiUtilities.getConfigDirPath();

	//Number of members in this class.
	private final int NUMBER_OF_PARAMETERS = 7;

	private int formatID = com.cannontech.billing.FileFormatTypes.CTICSV;
	private int demandDaysPrev = 30;
	private int energyDaysPrev = 7;
	private java.util.Vector billGroup = null;
	private String outputFileDir = null;
	private String billGroupType = DeviceMeterGroup.validBillGroupTypeStrings[DeviceMeterGroup.COLLECTION_GROUP];
	private boolean removeMultiplier = false;

	private boolean appendToFile = false;

	//USED BUT NOT STORED IN THE FILE BECAUSE WE CAN'T PREDICT THIS, YET?
	private String inputFileDir = null;
	private java.util.Date endDate = null;
	private java.util.Date energyStartDate = null;
	private java.util.Date demandStartDate = null;
	
	/**
	 * DynamicBilling constructor comment.
	 */
	public BillingFileDefaults()
	{
		super();
		parseDefaultsFile();
	}
	/**
	 * BillingFileDefaults constructor comment.
	 */
	public BillingFileDefaults(int formatID, int demandDaysPrev, int energyDaysPrev, 
					String billGroupString, int billGroupType, String outputFileDir,
					boolean removeMultiplier, String inputFile, java.util.Date endDate,
					boolean appendToFile)
	{
		super();
		this.formatID = formatID;
		this.demandDaysPrev = demandDaysPrev;
		this.energyDaysPrev = energyDaysPrev;
		setBillGroup( billGroupString);
		setBillGroupType( billGroupType );
		this.outputFileDir = outputFileDir;
		this.removeMultiplier = removeMultiplier;
		this.inputFileDir = inputFile;
		this.endDate = endDate;
		this.appendToFile = appendToFile;
	}
	/**
	 * BillingFileDefaults constructor comment.
	 */
	public BillingFileDefaults(int formatID, int demandDaysPrev, int energyDaysPrev, 
					java.util.Vector billGroupVector, int billGroupType, String outputFileDir, 
					boolean removeMultiplier, String inputFileDir, java.util.Date endDate,
					boolean appendToFile)
	{
		super();
		this.formatID = formatID;
		this.demandDaysPrev =  demandDaysPrev;
		this.energyDaysPrev = energyDaysPrev;
		this.billGroup = billGroupVector;
		setBillGroupType( billGroupType);
		this.outputFileDir = outputFileDir;
		this.removeMultiplier = removeMultiplier;
		
		this.inputFileDir = inputFileDir;
		this.endDate = endDate;
		this.appendToFile = appendToFile;
	}
	
	/**
	 * Returns the billGroupTypeDisplayString for index (this.billGroupTypeID).
	 * @return java.lang.String
	 */
	public static String getBillGroupTypeDisplayString(int index)
	{
		return DeviceMeterGroup.validBillGroupTypeDisplayStrings[index];
	}

	/**
	 * Returns the billGroup(s).
	 * Group name(s) from Yukon.DeviceMeterGroup.[collectiongroup|testcollectiongroup|billinggroup].
	 * @return java.util.Vector
	 */
	public java.util.Vector getBillGroup()
	{
		return billGroup;
	}

	/**
	 * Returns the billGroupType.
	 * Valid billGroupType(s) are columns from Yukon.DeviceMeterGroup [collectiongroup|testcollectiongroup|billinggroup].
	 * @return java.lang.String
	 */
	public String getBillGroupType()
	{
		return billGroupType;
	}

	/**
	 * Returns the respective validBillGroupTypeIDs for validBillGroupTypes.
	 * @return int
	 */
	public int getBillGroupTypeID()
	{
		for (int i = 0; i < DeviceMeterGroup.validBillGroupTypeStrings.length; i++)
		{
			if (getBillGroupType() == DeviceMeterGroup.validBillGroupTypeStrings[i])
				return DeviceMeterGroup.validBillGroupTypeIDs[i];
		}
		return DeviceMeterGroup.COLLECTION_GROUP;
	}

	/**
	 * Returns the demandDaysPrev.
	 * The number of days previous to endDate for valid demand readings.
	 * @return int
	 */
	public int getDemandDaysPrev()
	{
		return demandDaysPrev;
	}

	/**
	 * Returns the demandStartDate.
	 * The minimum date (according to getDemandDaysPrev() and endDate) for valid demand readings.
	 * @return java.util.Date
	 */
	public java.util.Date getDemandStartDate()
	{
//		if( demandStartDate == null)
		{
			demandStartDate = new java.util.Date();
			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			
			//Need to subtract one day because start date is at least one date value less than endDate.
			//Ex.  0 days previous is >= today 00:00:00 and < tomorrow 00:00:00
			long daysPrev = 86400000  * new Integer(getDemandDaysPrev()  + 1 ).longValue();
			long time = getEndDate().getTime() - daysPrev;
		
			demandStartDate.setTime(time);
			cal.setTime(demandStartDate);
		
			cal.set(java.util.Calendar.HOUR_OF_DAY,0);
			cal.set(java.util.Calendar.MINUTE,0);
			cal.set(java.util.Calendar.SECOND,0);
			cal.set(java.util.Calendar.MILLISECOND,0);
	
			demandStartDate = cal.getTime();
		}
		return demandStartDate;
	}
	
	/**
	 * Returns the energyDaysPrev.
	 * The number of days previous to endDate for valid energy readings.
	 * @return int
	 */
	public int getEnergyDaysPrev()
	{
		return energyDaysPrev;
	}
	
	/**
	 * Returns the energyStartDate.
	 * The minimum date (according to getDemandDaysPrev() and endDate) for valid energy readings.
	 * @return java.util.Date
	 */	
	public java.util.Date getEnergyStartDate()
	{
//		if( energyStartDate == null)
		{
			energyStartDate = new java.util.Date();
			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			
			//Need to subtract one day because start date is at least one date value less than endDate.
			//Ex.  0 days previous is >= today 00:00:00 and < tomorrow 00:00:00
			long daysPrev = 86400000  * new Integer(getEnergyDaysPrev() + 1).longValue();
			long time = getEndDate().getTime() - daysPrev;
		
			energyStartDate.setTime(time);
			cal.setTime(energyStartDate);
		
			cal.set(java.util.Calendar.HOUR_OF_DAY,0);
			cal.set(java.util.Calendar.MINUTE,0);
			cal.set(java.util.Calendar.SECOND,0);
			cal.set(java.util.Calendar.MILLISECOND,0);
			energyStartDate = cal.getTime();
		}	
		return energyStartDate;
	}

	/**
	 * Returns the earliest possible startDate for valid readings.
	 * The minimum date of getEnergyStartDate() and getDemandStartDate().
	 * @return java.util.Date
	 */
	public java.util.Date getEarliestStartDate()
	{
		java.util.Date compareDate1 = getDemandStartDate();
		java.util.Date compareDate2 = getEnergyStartDate();
		if( compareDate1.before(compareDate2))
			return compareDate1;
		else
			return compareDate2;
	}

	/**
	 * Returns the endDate.
	 * The maximum date for valid readings.
	 * @return java.util.Date
	 */
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
	
	/**
	 * Returns the formatID.
	 * The billing file format id(com.cannontech.billing.FileFormatTypes.validFormatID).
	 * @return int
	 */
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

	/**
	 * Returns the outputFileDir.
	 * The filename and directory of the output file..
	 * @return java.lang.String
	 */
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
	 * Returns the inputFileDir.
	 * The filename and directory of the input file..
	 * @return java.lang.String
	 */
	public String getInputFileDir()
	{
		if( inputFileDir == null)
		{
			String directory = com.cannontech.common.util.CtiUtilities.getCanonicalFile(com.cannontech.common.util.CtiUtilities.getExportDirPath());
			inputFileDir = directory + "inFile.txt";
		}
		return inputFileDir;
	}
	
	/**
	 * Returns the removeMultiplier.
	 * True when the reading requires the multiplier to be factored out.
	 * @return boolean
	 */
	public boolean isRemoveMultiplier()
	{
		return removeMultiplier ;
	}
	
	/**
	 * Sets the billGroup.
	 * billGroup may be a single billGroup or a string of comma separted billGroup(s).
	 * @param billGroup The billGroup to set
	 */
	public void setBillGroup(String billGroupString)
	{
		java.util.Vector tempBillGrp = null;
		int endIndex = billGroupString.indexOf(",");
	
		if( endIndex < 0)	//only one billing group selected in dat file.
		{
			tempBillGrp = new java.util.Vector(1);
			tempBillGrp.add(billGroupString);
		}
		else
		{	
			tempBillGrp = new java.util.Vector();
			int count = 0;
			
			while ( endIndex > 0)			//returns -1 "," not found
			{
				int beginIndex = 0;			
				String temp = billGroupString.substring(beginIndex, endIndex);
	
				tempBillGrp.add(count++, temp);
				beginIndex = endIndex + 1;
	
				billGroupString = billGroupString.substring(beginIndex);	
				endIndex = billGroupString.indexOf(",");
			}
		}
		
		if( tempBillGrp != null)
			setBillGroup(tempBillGrp);
	}

	/**
	 * Sets the billGroup.
	 * @param billGroup The billGroup to set
	 */
	private void setBillGroup(java.util.Vector billGroup)
	{
		this.billGroup = billGroup;
	}

	/**
	 * Sets the billGroupSQLString.
	 * The Yukon.DeviceMeterGroup db table column to query for billingGroup.
	 * @param billGroupSQLIndex The validBillGroupTypeID of validbillGroupSQLStrings to set billGroupSQLString.
	 */
	public void setBillGroupType(int validBillGroupTypeID)
	{
		this.billGroupType = DeviceMeterGroup.validBillGroupTypeStrings[validBillGroupTypeID];
	}

	/**
	 * Sets the billGroupSQLString.
	 * The Yukon.DeviceMeterGroup db table column to query for billingGroup.
	 * @param billGroupSQLString The billGroupSQLString to set.
	 */
	public void setBillGroupType(String billGroupSQLString)
	{
		this.billGroupType = billGroupSQLString;
	}

	/**
	 * Sets the demandDaysPrev.
	 * Number of days from endDate to accept valid demand data.
	 * @param demandDaysPrev The demandDaysPrev to set.
	 */
	public void setDemandDaysPrev(int demandDaysPrev)
	{
		this.demandDaysPrev = demandDaysPrev;
	}

	/**
	 * Sets the energyDaysPrev.
	 * @param energyDaysPrev The demandDaysPrev to set.
	 */
	public void setEnergyDaysPrev(int energyDaysPrev)
	{
		this.energyDaysPrev = energyDaysPrev;
	}

	/**
	 * Sets the endDate.
	 * @param endDate The endDate to set.
	 */
	public void setEndDate(java.util.Date endDate)
	{
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		// add a whole day to now so we can increment the Date field
		long day = 86400000;
		long time = endDate.getTime() + day;
	
		this.endDate = new java.util.Date(time);
		cal.setTime(endDate);
	
		cal.set(java.util.Calendar.HOUR_OF_DAY,0);
		cal.set(java.util.Calendar.MINUTE,0);
		cal.set(java.util.Calendar.SECOND,0);
		cal.set(java.util.Calendar.MILLISECOND,0);
	
		this.endDate = cal.getTime();
		getDemandStartDate();
		getEnergyStartDate();
	}
	
	/**
	 * Sets the formatID.
	 * @param formatID The formatID to set.
	 */
	public void setFormatID(int formatID)
	{
		this.formatID = formatID;
	}

	/**
	 * Sets the imputFileDir.
	 * @param inputFileDir The inputFileDir to set.
	 */
	public void setInputFileDir(String inputFileDir)
	{
		this.inputFileDir = inputFileDir;
	}
	
	/**
	 * Sets the outputFileDir.
	 * @param outputFileDir The outputFileDir to set.
	 */
	public void setOutputFileDir(String outputFileDir)
	{
		this.outputFileDir = outputFileDir;
	}

	/**
	 * Sets the removeMultiplier.
	 * @param removeMultiplier The value to set.
	 */
	public void setRemoveMultiplier(boolean removeMultiplier)
	{
		this.removeMultiplier = removeMultiplier;
	}
	
	/**
	 * Method initialize.
	 */
	public void initialize() 
	{
		
	}
	
	/**
	 * Method parseDefaultsFile.
	 * Read file BILLING_DEFAULTS_FILENAME and parse for parameter defaults.
	 * Calls updateFileDefaults(file read parameter Vector).
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

	/**
	 * Method updateFileDefaults.
	 * Sets default parameter values from infileDefaultsVector.  *Order is critical*
	 * @param infileDefaultsVector
	 */
	public void updateFileDefaults(java.util.Vector infileDefaultsVector)
	{
		int index = 0;
		setFormatID( (new Integer((String)infileDefaultsVector.get(index++))).intValue());
		setDemandDaysPrev((new Integer((String)infileDefaultsVector.get(index++))).intValue());
		setEnergyDaysPrev((new Integer((String)infileDefaultsVector.get(index++))).intValue());
		setBillGroup((String)infileDefaultsVector.get(index++));
		setOutputFileDir((String)infileDefaultsVector.get(index++));
		
		//	if( index < infileDefaultsVector.size())
		setBillGroupType((String)infileDefaultsVector.get(index++));
		setRemoveMultiplier(new Boolean((String)infileDefaultsVector.get(index++)).booleanValue());
		//setInputFile((String)infileDefaultsVector.get(index++));
	}
	
	/**
	 * Method writeDefaultsFile.
	 * Write parameter values to BILLING_DEFAULTS_DIRECTORY.  *Order is critical*
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
			writer.write( getBillGroupType() + "\r\n" );
			
			writer.write( String.valueOf(isRemoveMultiplier()));
	
			writer.close();
		}
		catch ( java.io.IOException e )
		{
			System.out.print(" IOException in writeBillingDefaultsFile");
			e.printStackTrace();
		}
		
	}
	/**
	 * Returns the appendToFile.
	 * @return boolean
	 */
	public boolean isAppendToFile()
	{
		return appendToFile;
	}

	/**
	 * Sets the appendToFile.
	 * @param appendToFile The appendToFile to set
	 */
	public void setAppendToFile(boolean appendToFile)
	{
		this.appendToFile = appendToFile;
	}
}
