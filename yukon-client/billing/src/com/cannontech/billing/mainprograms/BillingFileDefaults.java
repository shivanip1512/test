package com.cannontech.billing.mainprograms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.service.FixedDeviceGroups;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
;

/**
 * Insert the type's description here.
 * Creation date: (5/6/2002 10:55:56 AM)
 * @author: 
*/

public class BillingFileDefaults
{
	public static final String BILLING_DEFAULTS_FILENAME_v1 = "BillingDefaultSetting.DAT";
	public static final String BILLING_DEFAULTS_FILENAME_v2 = "BillingDefaultSetting.properties";
	public static final String BILLING_DEFAULTS_DIRECTORY = CtiUtilities.getConfigDirPath();

    // These are the old names, should only be used for converting.
    public static final String[] validBillGroupTypeStrings = 
        {"COLLECTIONGROUP", "TESTCOLLECTIONGROUP", "BILLINGGROUP"};
    
    //Number of members in this class.
	private final int NUMBER_OF_PARAMETERS = 8;

	private int formatID = com.cannontech.billing.FileFormatTypes.CTICSV;
	private int demandDaysPrev = 30;
	private int energyDaysPrev = 7;
	private java.util.Vector <String> billGroup = null;
	private String outputFileDir = null;
	private boolean removeMultiplier = false;
	private String token = null;

	private boolean appendToFile = false;

	//USED BUT NOT STORED IN THE FILE BECAUSE WE CAN'T PREDICT THIS, YET?
	private String inputFileDir = null;
	private java.util.Date endDate = null;
	private java.util.Date energyStartDate = null;
	private java.util.Date demandStartDate = null;
    private List<String> deviceGroups;
    private LiteYukonUser liteYukonUser = null;
    
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
        processOldBillGroup(validBillGroupTypeStrings[billGroupType], billGroupString);
		this.outputFileDir = outputFileDir;
		this.removeMultiplier = removeMultiplier;
		this.inputFileDir = inputFile;
		setEndDate(endDate);
		this.appendToFile = appendToFile;
	}
	/**
	 * BillingFileDefaults constructor comment.
	 */
	public BillingFileDefaults(int formatID, int demandDaysPrev, int energyDaysPrev, 
					List<String> deviceGroups, String outputFileDir, 
					boolean removeMultiplier, String inputFileDir, java.util.Date endDate,
					boolean appendToFile)
	{
		super();
		this.formatID = formatID;
		this.demandDaysPrev =  demandDaysPrev;
		this.energyDaysPrev = energyDaysPrev;
        this.deviceGroups = deviceGroups;
		this.outputFileDir = outputFileDir;
		this.removeMultiplier = removeMultiplier;
		this.inputFileDir = inputFileDir;
		setEndDate(endDate);
		this.appendToFile = appendToFile;
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
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.setTime(getEndDate());
		//Need to subtract one day because start date is at least one date value less than endDate.
		//Ex.  0 days previous is >= today 00:00:00 and < tomorrow 00:00:00
        cal.add(Calendar.DATE, -(getDemandDaysPrev()  + 1) );
		demandStartDate = cal.getTime();
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
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        cal.setTime(getEndDate());
		//Need to subtract one day because start date is at least one date value less than endDate.
		//Ex.  0 days previous is >= today 00:00:00 and < tomorrow 00:00:00
        cal.add(Calendar.DATE, -(getEnergyDaysPrev()  + 1) );           
		energyStartDate = cal.getTime();
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
			endDate = ServletUtil.getToday();
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
            int[] validFormatIDs = FileFormatTypes.getValidFormatIDs();
            if (validFormatIDs.length > 0) {
                formatID = validFormatIDs[0];
            }
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
			String directory = CtiUtilities.getCanonicalFile(CtiUtilities.getExportDirPath());
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
			String directory = CtiUtilities.getCanonicalFile(CtiUtilities.getExportDirPath());
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
		cal.setTime(endDate);
		cal.add(java.util.Calendar.DATE,1);
	
		this.endDate = cal.getTime();
		endDate = cal.getTime();
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
	    File fileVersion_1 = new File( BILLING_DEFAULTS_DIRECTORY, BILLING_DEFAULTS_FILENAME_v1);
	    File fileVersion_2 = new File( BILLING_DEFAULTS_DIRECTORY, BILLING_DEFAULTS_FILENAME_v2);

	    // open file		
	    if (fileVersion_2.exists()) {
	        parseDefaultsFile_2(fileVersion_2);
	    } else if (fileVersion_1.exists()) {
	        parseDefaultsFile_1(fileVersion_1);
        }
	}
    private void parseDefaultsFile_2(File fileVersion_2) {
        Properties props = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(fileVersion_2);
            props.load(fileInputStream);
            String version = props.getProperty("VERSION");
            if (version == null) {
                throw new RuntimeException("VERSION property was null");
            }
            
            if (version.equals("2")) {
                setFormatID(Integer.parseInt(props.getProperty("formatID")));
                setDemandDaysPrev(Integer.parseInt(props.getProperty("demandDaysPrev")));
                setEnergyDaysPrev(Integer.parseInt(props.getProperty("energyDaysPrev")));
                
                String deviceGroupsStr = props.getProperty("deviceGroups");
                String[] strings = deviceGroupsStr.split(";");
                setDeviceGroups(Arrays.asList(strings));
                
                setOutputFileDir(props.getProperty("outputFileDir"));
                setRemoveMultiplier(BooleanUtils.toBoolean(props.getProperty("removeMultiplier")));
                setAppendToFile(BooleanUtils.toBoolean(props.getProperty("appendToFile")));
            }
        } catch (FileNotFoundException e) {
            CTILogger.error(e);
        } catch (IOException e) {
            CTILogger.error(e);
        }
    }
    
    public void setDeviceGroups(List<String> deviceGroups) {
        this.deviceGroups = deviceGroups;
    }
    
    public List<String> getDeviceGroups() {
        return deviceGroups;
    }
    
    private void parseDefaultsFile_1(java.io.File inFile) {
        java.io.RandomAccessFile raFile;
        try {
            raFile = new java.io.RandomAccessFile( inFile, "r" );
            String[] fileParameters = new String[NUMBER_OF_PARAMETERS];

            long readLinePointer = 0;
            long fileLength = raFile.length();
            int index = 0;
            while ( readLinePointer < fileLength ) { // loop until the end of the file
                String line = raFile.readLine();  // read a line in
                fileParameters[index++] = line;

                // set our pointer to the new position in the file
                readLinePointer = raFile.getFilePointer();
            }
            raFile.close();	
            
            
            int index1 = 0;
            setFormatID( (new Integer(fileParameters[index1++])).intValue());
            setDemandDaysPrev((new Integer(fileParameters[index1++])).intValue());
            setEnergyDaysPrev((new Integer(fileParameters[index1++])).intValue());
            String billGroup = fileParameters[index1++];
            setOutputFileDir(fileParameters[index1++]);
            
            String billType = fileParameters[index1++];
            if( fileParameters[index1]!= null) {
            	setRemoveMultiplier(new Boolean(fileParameters[index1++]).booleanValue());
            	if( fileParameters[index1] != null)
            		setAppendToFile(new Boolean(fileParameters[index1++]).booleanValue());
            }
            
            processOldBillGroup(billType, billGroup);
            //setInputFile((String)infileDefaultsVector.get(index++));
        } catch (FileNotFoundException e) {
            CTILogger.error(e);
        } catch (IOException e) {
            CTILogger.error(e);
        }
    }


	private void processOldBillGroup(String billType, String billGroupStr) {
        FixedDeviceGroups groupType = FixedDeviceGroups.resolveGroup(billType);
        
        String[] groups = billGroupStr.split(",");
        List<String> result = new ArrayList<String>(groups.length);
        for (String group : groups) {
            String fullGroup = groupType.getGroup(group);
            result.add(fullGroup);
        }
        setDeviceGroups(result);
        
    }
    /**
	 * Method writeDefaultsFile.
	 * Write parameter values to BILLING_DEFAULTS_DIRECTORY.  *Order is critical*
	 */
	public void writeDefaultsFile()
	{
		try
		{
			File billingDir = new java.io.File( BILLING_DEFAULTS_DIRECTORY );
            billingDir.mkdirs();
            File defaultsFile = new File(billingDir, BILLING_DEFAULTS_FILENAME_v2);
			
            Properties props = new Properties();
            props.put("VERSION", "2");
            props.put("formatID", ObjectUtils.toString(getFormatID()));
	
            props.put("demandDaysPrev", ObjectUtils.toString(getDemandDaysPrev()));
				
            props.put("energyDaysPrev", ObjectUtils.toString(getEnergyDaysPrev()));
	
            String deviceGroupsStr = StringUtils.join(getDeviceGroups(), ";");
            props.put("deviceGroups", deviceGroupsStr);
	
            props.put("outputFileDir", getOutputFileDir());
	
            props.put("removeMultiplier", BooleanUtils.toStringTrueFalse(isRemoveMultiplier()));
			
            props.put("appendToFile", BooleanUtils.toStringTrueFalse(isAppendToFile()));
            
            FileOutputStream fileOutputStream = new FileOutputStream(defaultsFile);
            props.store(fileOutputStream, "Parsed by " + this.getClass());
            fileOutputStream.close();
		}
		catch (IOException e) {
			CTILogger.error(e);
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
	
	public void setLiteYukonUser(LiteYukonUser liteYukonUser) {
		this.liteYukonUser = liteYukonUser;
	}
	
	public LiteYukonUser getLiteYukonUser() {
		return liteYukonUser;
	}
	
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}