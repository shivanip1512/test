package com.cannontech.billing.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 8:36:18 AM)
 * @author: 
 */ 
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.billing.SimpleBillingFormat;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.yukon.BillingRole;
import com.cannontech.spring.YukonSpringHook;

public class BillingBean implements java.util.Observer
{
	public static java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy");
    private DeviceGroupProviderDao deviceGroupDao = YukonSpringHook.getBean("deviceGroupDao", DeviceGroupProviderDao.class);
		
	public static final String BILLING_VERSION = VersionTools.getYUKON_VERSION();
	private BillingFile billingFile = null;
	private BillingFileDefaults billingFileDefaults = null;

	private int fileFormat = FileFormatTypes.INVALID;
	private int demandDaysPrev = -1;
	private int energyDaysPrev = -1;
	private List<String> billGroup = Collections.singletonList("/Meters");
	private String outputFile = null;
	private String inputFile = null;
	private Boolean removeMult = null;
	private Boolean appendToFile = null;
	private Date endDate = null;
	private int timer = 0;
	private String timerString = "";
	

/**
 * BillingBean constructor comment.
 */
public BillingBean()
{
	super();
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
	// Gather new billing defaults and write them to the properties file.
	//FormatID, demandDays, energyDays, collectionGrpVector, outputFile, inputFile
	BillingFileDefaults defaults = new BillingFileDefaults(
	getFileFormat(),
	(new Integer( getDemandDaysPrev()).intValue()),
	(new Integer( getEnergyDaysPrev()).intValue()),
	getBillGroup(),
	getOutputFile(),
	getRemoveMult(),
	getInputFile(),
	getEndDate(),
	getAppendToFile());

	if (defaults == null)
		return;

	defaults.setLiteYukonUser(getLiteYukonUser());
    setBillingFormatter(defaults);

	if( getSimpleBillingFormat() != null)
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

public List<String> getAvailableGroups() {
    List<? extends DeviceGroup> allGroups = deviceGroupDao.getAllGroups();
    List<String> mappingList = new MappingList<DeviceGroup, String>(allGroups, new ObjectMapper<DeviceGroup, String>() {
        public String map(DeviceGroup from) {
            return from.getFullName();
        }
    });
    return mappingList;
}


private BillingFile getBillingFile()
{
	if(billingFile == null)
	{
		billingFile = new BillingFile();
	}
	return billingFile;
}

public BillingFileDefaults getBillingDefaults() {
	if (this.billingFileDefaults == null) {
		this.billingFileDefaults = new BillingFileDefaults();
	}
	return billingFileDefaults;
}

public int getFileFormat()
{
	if( fileFormat == FileFormatTypes.INVALID) {
		String format = DaoFactory.getAuthDao().getRolePropertyValue(getLiteYukonUser(), BillingRole.DEFAULT_BILLING_FORMAT);
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
		demandDaysPrev = Integer.valueOf(DaoFactory.getAuthDao().getRolePropertyValue(getLiteYukonUser(), BillingRole.DEMAND_DAYS_PREVIOUS)).intValue();
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
		energyDaysPrev = Integer.valueOf(DaoFactory.getAuthDao().getRolePropertyValue(getLiteYukonUser(), BillingRole.ENERGY_DAYS_PREVIOUS)).intValue();
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
		appendToFile = Boolean.valueOf(DaoFactory.getAuthDao().getRolePropertyValue(getLiteYukonUser(), BillingRole.APPEND_TO_FILE));
	return appendToFile.booleanValue();
}
public void setAppendToFile(boolean isAppendToFile)
{
	appendToFile = Boolean.valueOf(isAppendToFile);
}

public boolean getRemoveMult()
{
	if (removeMult == null)
		removeMult = Boolean.valueOf(DaoFactory.getAuthDao().getRolePropertyValue(getLiteYukonUser(), BillingRole.REMOVE_MULTIPLIER));
	return removeMult.booleanValue();
}
public void setRemoveMult(boolean isRemoveMult)
{
	removeMult = Boolean.valueOf(isRemoveMult);
}

public List<String> getBillGroup()
{
	return billGroup;
}
public void setBillGroup(List<String> billGroup)
{
	this.billGroup = billGroup;
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
 * Returns the SimpleBillingFormat object.
 * @return SimpleBillingFormat
 */
public SimpleBillingFormat getSimpleBillingFormat()
{
	return getBillingFile().getSimpleBillingFormat();
}

private void setBillingDefaults(BillingFileDefaults newDefaults) {
	this.billingFileDefaults = newDefaults;
}

/**
 * Set 
 * @param int formatID
 */
private void setBillingFormatter(BillingFileDefaults billingFileDefaults)
{
	getBillingFile().setBillingFormatter(billingFileDefaults);
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
	return getBillingDefaults().getLiteYukonUser();
}
public void setLiteYukonUser(LiteYukonUser liteYukonUser) {
	getBillingDefaults().setLiteYukonUser(liteYukonUser);
}
public String getInputFile() {
	if( inputFile == null)
		inputFile = DaoFactory.getAuthDao().getRolePropertyValue(getLiteYukonUser(), BillingRole.INPUT_FILE);
	return inputFile;
}
public void setInputFile(String inputFile) {
	this.inputFile = inputFile;
}
}
