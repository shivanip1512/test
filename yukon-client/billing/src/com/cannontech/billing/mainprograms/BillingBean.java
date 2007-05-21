package com.cannontech.billing.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 8:36:18 AM)
 * @author: 
 */ 
import java.util.Date;

import com.cannontech.billing.FileFormatBase;
import com.cannontech.billing.FileFormatTypes;
import com.cannontech.billing.format.BillingFormatter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.roles.yukon.BillingRole;

public class BillingBean implements java.util.Observer
{
	public static java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy");
		
	public static final String BILLING_VERSION = VersionTools.getYUKON_VERSION();
	private BillingFile billingFile = null;

	private int fileFormat = FileFormatTypes.INVALID;
	private int demandDaysPrev = -1;
	private int energyDaysPrev = -1;
	private String billGroup = "Default";
	private int billGroupType = DeviceMeterGroup.COLLECTION_GROUP;
	private String outputFile = null;
	private String inputFile = null;
	private Boolean removeMult = null;
	private Boolean appendToFile = null;
	private Date endDate = null;
	private int timer = 0;
	private String timerString = "";
	
	private LiteYukonUser liteYukonUser = null;

/**
 * BillingBean constructor comment.
 */
public BillingBean()
{
	super();
}
public String[] getValidBillGroups()
{
	getBillingDefaults().setBillGroupType(getBillGroupType());
	java.util.Vector valids = getBillingFile().retrieveAllBillGroupsVector();
	String [] validBillGroups = new String[valids.size()];
	for (int i = 0; i < valids.size(); i++)
	{
		validBillGroups[i] = valids.get(i).toString();
	}
	return validBillGroups;
}

/**
 * Insert the method's description here.
 * Creation date: (5/23/2002 11:25:28 AM)
 * @param enable boolean
 */
public void enableTimer(boolean enable)
{
	if( enable )
	{
		timerString = "0 sec";
		timer = 0;
	}
}
/**
 * Comment
 */
public void generateFile(java.io.OutputStream out) throws java.io.IOException
{
	setBillingFormatter(getBillingDefaults().getFormatID());

	// Gather new billing defaults and write them to the properties file.
	//FormatID, demandDays, energyDays, collectionGrpVector, outputFile, inputFile
	BillingFileDefaults defaults = new BillingFileDefaults(
	getFileFormat(),
	(new Integer( getDemandDaysPrev()).intValue()),
	(new Integer( getEnergyDaysPrev()).intValue()),
	getBillGroup(),getBillGroupType(),
	getOutputFile(),
	getRemoveMult(),
	getInputFile(),
	getEndDate(),
	getAppendToFile());

	if (defaults == null)
		return;
		
	setBillingDefaults(defaults);

	if( getBillingFormatter() != null || getFileFormatBase() != null )
	{
		Date timerStart = new Date();
		CTILogger.info("Started " + FileFormatTypes.getFormatType(getBillingDefaults().getFormatID()) +
					" format at: " + timerStart);

		//start our DB thread
		getBillingFile().encodeOutput(out);
	}
	else
	{
		CTILogger.info(getBillingDefaults().getFormatID() + " unrecognized file format id");
	}

//	getBillingDefaults().writeDefaultsFile();
}




private BillingFile getBillingFile()
{
	if(billingFile == null)
	{
		billingFile = new BillingFile();
		billingFile.setAllBillGroupsVector(getBillingFile().retrieveAllBillGroupsVector());
	}
	return billingFile;
}

public BillingFileDefaults getBillingDefaults()
{
	return getBillingFile().getBillingDefaults();
}
public int getFileFormat()
{
	if( fileFormat == FileFormatTypes.INVALID) {
		String format = DaoFactory.getRoleDao().getRolePropertyValue(getLiteYukonUser().getUserID(), BillingRole.DEFAULT_BILLING_FORMAT, null);
		fileFormat = FileFormatTypes.getFormatID(format);
	}
	return fileFormat;
}
public void setFileFormat(int newFileFormat)
{
	if( fileFormat != newFileFormat)
	{
		fileFormat = newFileFormat;
		getBillingDefaults().setFormatID(fileFormat);
	}
}

public Date getEndDate()
{
	if( endDate == null)
		endDate = getBillingDefaults().getEndDate();
	CTILogger.info(" Getting End Date! " + endDate);
	return endDate;
}
public void setEndDate(Date newEndDate)
{
	if( endDate.compareTo(newEndDate) != 0)
	{
		endDate = newEndDate;
		CTILogger.info("Changing End Date from: " + endDate +" to: " + newEndDate);
		getBillingDefaults().setEndDate(newEndDate);
	}
}
public void setEndDateStr(String newEndDateStr)
{
	try {
		setEndDate( dateFormat.parse(newEndDateStr));
	}
	catch (java.text.ParseException e) {
		CTILogger.error(e);
	}
}

	
public int getDemandDaysPrev()
{
	if( demandDaysPrev < 0)
		demandDaysPrev = Integer.valueOf(DaoFactory.getRoleDao().getRolePropertyValue(getLiteYukonUser().getUserID(), BillingRole.DEMAND_DAYS_PREVIOUS, null)).intValue();
	return demandDaysPrev;
}
/**
 * Returns the demandStartDate.
 * The minimum date (according to getDemandDaysPrev() and endDate) for valid demand readings.
 * @return java.util.Date
 */
public java.util.Date getDemandStartDate()
{
	return getBillingDefaults().getDemandStartDate();
}

public void setDemandDaysPrev(int newDemandDaysPrev)
{
	demandDaysPrev = newDemandDaysPrev;
	getBillingDefaults().setDemandDaysPrev(demandDaysPrev);
}

public int getEnergyDaysPrev()
{
	if (energyDaysPrev < 0)
		energyDaysPrev = Integer.valueOf(DaoFactory.getRoleDao().getRolePropertyValue(getLiteYukonUser().getUserID(), BillingRole.ENERGY_DAYS_PREVIOUS, null)).intValue();
	return energyDaysPrev;
}
public void setEnergyDaysPrev(int newEnergyDaysPrev)
{
	energyDaysPrev = newEnergyDaysPrev;
	getBillingDefaults().setEnergyDaysPrev(energyDaysPrev);
}

public boolean getAppendToFile()
{
	if( appendToFile == null)
		appendToFile = Boolean.valueOf(DaoFactory.getRoleDao().getRolePropertyValue(getLiteYukonUser().getUserID(), BillingRole.APPEND_TO_FILE, null));
	return appendToFile.booleanValue();
}
public void setAppendToFile(boolean isAppendToFile)
{
	appendToFile = Boolean.valueOf(isAppendToFile);
}

public boolean getRemoveMult()
{
	if (removeMult == null)
		removeMult = Boolean.valueOf(DaoFactory.getRoleDao().getRolePropertyValue(getLiteYukonUser().getUserID(), BillingRole.REMOVE_MULTIPLIER, null));
	return removeMult.booleanValue();
}
public void setRemoveMult(boolean isRemoveMult)
{
	removeMult = Boolean.valueOf(isRemoveMult);
}

public String getBillGroup()
{
	return billGroup;
}
public void setBillGroup(String billGroup)
{
	this.billGroup = billGroup;
}

public int getBillGroupType()
{
	return billGroupType;
}
public void setBillGroupType(int billGroupType)
{
	this.billGroupType = billGroupType;
	getBillingDefaults().setBillGroupType(billGroupType);
}

public String getOutputFile()
{
	return outputFile;
}
public void setOutputFile(String newOutputFile)
{
	outputFile = newOutputFile;
	
	getBillingDefaults().setOutputFileDir(outputFile);
}

/**
 * Returns the FileFormatBase object.  This is a legacey format object.
 * The billingFileFormatter should be used first, and only use this object
 * if the billingFileFormatter is null.
 * @return com.cannontech.billing.FileFormatBase
 */
public FileFormatBase getFileFormatBase()
{
	return getBillingFile().getFileFormatBase();
}

/**
 * Returns the BillingFormatter object
 * @return com.cannontech.billing.format.BillingFormatter
 */
public BillingFormatter getBillingFormatter()
{
    return getBillingFile().getBillingFormatter();
}

private void setBillingDefaults(BillingFileDefaults newDefaults)
{
	getBillingFile().setBillingDefaults(newDefaults);
}

/**
 * Set 
 * @param int formatID
 */
private void setBillingFormatter(int formatID)
{
	getBillingFile().setBillingFormatter(formatID);
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/2001 2:57:51 PM)
 * @param obs java.util.Observable
 * @param data java.lang.Object
 */

public synchronized void update(java.util.Observable obs, Object data) 
{
	if( obs instanceof BillingFile )
	{
		CTILogger.info("Done with Billing File Format.");

		BillingFile src =  (BillingFile)obs;
		src.deleteObserver( this );
		enableTimer(false);
	}
}
public LiteYukonUser getLiteYukonUser() {
	return liteYukonUser;
}
public void setLiteYukonUser(LiteYukonUser liteYukonUser) {
	this.liteYukonUser = liteYukonUser;
}
public String getInputFile() {
	if( inputFile == null)
		inputFile = DaoFactory.getRoleDao().getRolePropertyValue(getLiteYukonUser().getUserID(), BillingRole.INPUT_FILE, null);
	return inputFile;
}
public void setInputFile(String inputFile) {
	this.inputFile = inputFile;
}
}
