package com.cannontech.billing.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 8:36:18 AM)
 * @author: 
 */ 
import java.util.Date;

import com.cannontech.billing.FileFormatBase;
import com.cannontech.billing.FileFormatFactory;
import com.cannontech.billing.FileFormatTypes;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.util.ServletUtil;

public class BillingBean implements java.util.Observer
{
	public static java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy");
		
	public static final String BILLING_VERSION = VersionTools.getYUKON_VERSION();
	private BillingFile billingFile = null;

	private int fileFormat = 0;
	private int demandDaysPrev = 30;
	private int energyDaysPrev = 7;
	private String billGroup = "Default";
	private int billGroupType = DeviceMeterGroup.COLLECTION_GROUP;
	private String outputFile = "";
	private boolean removeMult = false;
	private boolean appendToFile = false;
	private Date endDate  = ServletUtil.getToday();
	private String endDateStr = null;

	private Date demandStartDate = null;
	
	private int timer = 0;
	private String timerString = "";

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
	setFileFormatBase( FileFormatFactory.createFileFormat(getBillingDefaults().getFormatID() ));

	// Gather new billing defaults and write them to the properties file.
	//FormatID, demandDays, energyDays, collectionGrpVector, outputFile, inputFile
	BillingFileDefaults defaults = new BillingFileDefaults(
	getFileFormat(),
	(new Integer( getDemandDaysPrev()).intValue()),
	(new Integer( getEnergyDaysPrev()).intValue()),
	getBillGroup(),getBillGroupType(),
	"c:/yukon/client/export/BeanTest.txt",
	getRemoveMult(),
	"",
	getEndDate(),
	getAppendToFile());

	if (defaults == null)
		return;
		
	setBillingDefaults(defaults);

	if( getFileFormatBase() != null )
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
	CTILogger.info(" Getting End Date! " + endDate);	
	return endDate;
}
public void setEndDate(Date newEndDate)
{
	if( endDate == null || endDate.compareTo((Object)newEndDate) != 0)
	{
		CTILogger.info("Changing End Date! " + endDate);
		endDate = newEndDate;
		getBillingDefaults().setEndDate(endDate);
	}
}
public void setEndDateStr(String newEndDateStr)
{
	endDateStr = newEndDateStr;
	try
	{
		setEndDate( dateFormat.parse(endDateStr));
	}
	catch (java.text.ParseException e)
	{
		e.printStackTrace();
	}
}

	
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
	return getBillingDefaults().getDemandStartDate();
}
	


public void setDemandDaysPrev(int newDemandDaysPrev)
{
	demandDaysPrev = newDemandDaysPrev;
	getBillingDefaults().setDemandDaysPrev(demandDaysPrev);
}

public int getEnergyDaysPrev()
{
	return energyDaysPrev;
}
public void setEnergyDaysPrev(int newEnergyDaysPrev)
{
	energyDaysPrev = newEnergyDaysPrev;
	getBillingDefaults().setEnergyDaysPrev(energyDaysPrev);
}

public boolean getAppendToFile()
{
	return appendToFile;
}
public void setAppendToFile(boolean isAppendToFile)
{
	appendToFile = isAppendToFile;
}

public boolean getRemoveMult()
{
	return removeMult;
}
public void setRemoveMult(boolean isRemoveMult)
{
	removeMult = isRemoveMult;
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
 * Insert the method's description here.
 * Creation date: (5/15/2002 9:39:47 AM)
 * @return com.cannontech.billing.FileFormatBase
 */
public FileFormatBase getFileFormatBase()
{
	return getBillingFile().getFileFormatBase();
}

private void setBillingDefaults(BillingFileDefaults newDefaults)
{
	getBillingFile().setBillingDefaults(newDefaults);
}

/**
 * Insert the method's description here.
 * Creation date: (5/14/2002 3:58:56 PM)
 * @param newFileFormatBase com.cannontech.billing.FileFormatBase
 */
private void setFileFormatBase(FileFormatBase newFileFormatBase)
{
	getBillingFile().setFileFormatBase(newFileFormatBase);
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
}
