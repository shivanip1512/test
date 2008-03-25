package com.cannontech.billing.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (5/7/2002 2:16:34 PM)
 * @author: 
 */
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.cannontech.billing.BillingDao;
import com.cannontech.billing.FileFormatBase;
import com.cannontech.billing.FileFormatFactory;
import com.cannontech.billing.FileFormatTypes;
import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.billing.format.BillingFormatter;
import com.cannontech.billing.format.BillingFormatterFactory;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.service.FixedDeviceGroups;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;

public class BillingFile extends java.util.Observable implements Runnable
{
	private BillingFileDefaults billingDefaults = null;
	private FileFormatBase fileFormatBase = null;
    
    private BillingFormatter billingFormatter = null;

    /**
	 * BillingFile constructor comment.
	 */
	public BillingFile() {
		super();
	}

	/**
	 * Method main.
	 * Takes args in format of key=value for batch running.
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
            System.setProperty("cti.app.name", "BillingFile"); 
            CTILogger.info("BillingFile starting...");
            YukonSpringHook.setDefaultContext("com.cannontech.context.billing");

			char argDel = '=';
			BillingFile billingFile = new BillingFile();
			
			for ( int i = 0; i < args.length; i++)
			{
				if( i == 0)	//first loop through, verify the char '=' is our delimiter, else try ':'
				{
					if( args[i].toLowerCase().indexOf(argDel) < 0)
						argDel = ':';
				}

				String arg = args[i];
				String argLowerCase = arg.toLowerCase();
				
				// Check the delimiter of '=', if not found check ':'
				int startIndex = argLowerCase.indexOf('=');
				if( startIndex < 0)
					startIndex = argLowerCase.indexOf(':');
				startIndex += 1;
				
				if( argLowerCase.startsWith("format") || argLowerCase.startsWith("type"))
				{//BillingFileDefaults.formatID
					String subString = arg.substring(startIndex);
					if( subString.length() > 2 )	//we accept int values of 0 - 12ish...so far.  Anything longer must be the string value.
						billingFile.getBillingDefaults().setFormatID(FileFormatTypes.getFormatID(subString));
					else
						billingFile.getBillingDefaults().setFormatID(Integer.valueOf(subString).intValue());
				}
				else if( argLowerCase.startsWith("dem"))
				{//BillingFileDefaults.demandDaysPrevious
					String subString = arg.substring(startIndex);
					billingFile.getBillingDefaults().setDemandDaysPrev(Integer.valueOf(subString).intValue());
				}
				else if( argLowerCase.startsWith("ene"))
				{//BillingFileDefaults.energyDaysPrevious
					String subString = arg.substring(startIndex);
					billingFile.getBillingDefaults().setEnergyDaysPrev(Integer.valueOf(subString).intValue());
				}
				else if( argLowerCase.startsWith("coll") ||  argLowerCase.startsWith("group") )
				{//BillingFileDefaults.billGroupTypeString=COLLECTIONGROUP
				 //BillingFileDefaults.billGroup
				    String group;
					String subString = args[i].substring(startIndex);
                    if (subString.startsWith("/")) {
                        group = subString;
                    } else {
                        group = FixedDeviceGroups.COLLECTIONGROUP.getGroup(subString);
                    }
					billingFile.getBillingDefaults().setDeviceGroups(Collections.singletonList(group));
				}
				else if( argLowerCase.startsWith("test") ||  argLowerCase.startsWith("alt") )
				{//BillingFileDefaults.billGroupTypeString=TESTCOLLECTIONGROUP
				 //BillingFileDefaults.billGroup
                    String group;
                    String subString = args[i].substring(startIndex);
                    if (subString.startsWith("/")) {
                        group = subString;
                    } else {
                        group = FixedDeviceGroups.TESTCOLLECTIONGROUP.getGroup(subString);
                    }
                    billingFile.getBillingDefaults().setDeviceGroups(Collections.singletonList(group));
				}
				else if( argLowerCase.startsWith("bill") )
				{//BillingFileDefaults.billGroupTypeString.BILLINGGROUP
				 //BillingFileDefaults.billGroup
                    String group;
                    String subString = args[i].substring(startIndex);
                    if (subString.startsWith("/")) {
                        group = subString;
                    } else {
                        group = FixedDeviceGroups.BILLINGGROUP.getGroup(subString);
                    }
                    billingFile.getBillingDefaults().setDeviceGroups(Collections.singletonList(group));
				}
				else if( argLowerCase.startsWith("end"))
				{//BillingFileDefaults.endDate
					String subString = arg.substring(startIndex);
					com.cannontech.util.ServletUtil.parseDateStringLiberally(subString);
					billingFile.getBillingDefaults().setEndDate(ServletUtil.parseDateStringLiberally(subString));
				}
				else if( argLowerCase.startsWith("file") || argLowerCase.startsWith("dir"))
				{//BillingFileDefaults.outputFileDir
					String subString = arg.substring(startIndex);
					if( subString.indexOf(':') > 0)	//they remembered the whole directory
						billingFile.getBillingDefaults().setOutputFileDir(subString);
					else if( subString.indexOf("\\") > -1 || subString.indexOf("//") > -1)	//they are trying to use a UNC name
						billingFile.getBillingDefaults().setOutputFileDir(subString);					
					else	//try to help out and default the directory
						billingFile.getBillingDefaults().setOutputFileDir(CtiUtilities.getExportDirPath() + subString);
				}
				else if( argLowerCase.startsWith("mult"))
				{//BillingFileDefuaults.removeMultiplier
					String subString = arg.substring(startIndex);
					if( subString.startsWith("t"))
						billingFile.getBillingDefaults().setRemoveMultiplier(true);
					else
						billingFile.getBillingDefaults().setRemoveMultiplier(false);
				}
				else if( argLowerCase.startsWith("app"))
				{
					String subString = arg.substring(startIndex);
					if( subString.startsWith("t"))
						billingFile.getBillingDefaults().setAppendToFile(true);
					else 
						billingFile.getBillingDefaults().setAppendToFile(false);
				}
			}
			
            billingFile.setBillingFormatter(billingFile.getBillingDefaults().getFormatID());
			billingFile.run();
			billingFile.getBillingDefaults().writeDefaultsFile();
		} 
		catch (Throwable exception) {
			CTILogger.error(exception);
		}
	}

	/**
	 * Retrieve all billingData and writeTofile for a formatBase.
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
	{
		if (billingFormatter != null) {

            CTILogger.info("Valid entries are for meter data where: ");
            CTILogger.info("  DEMAND readings > "
                    + getBillingDefaults().getDemandStartDate() + " AND <= "
                    + getBillingDefaults().getEndDate());
            CTILogger.info("  ENERGY readings > "
                    + getBillingDefaults().getEnergyStartDate() + " AND <= "
                    + getBillingDefaults().getEndDate());

            List<BillableDevice> deviceList = null;

            if (!getBillingDefaults().getDeviceGroups().isEmpty()) {

                deviceList = BillingDao.retrieveBillingData(getBillingDefaults());
            }
            try {
                int validReadings = 0;
                if (deviceList != null) {
                    try {
                        validReadings = billingFormatter.writeBillingFile(deviceList);
                    } catch (IllegalArgumentException e) {
                        setChanged();
                        notify(e.getMessage());

                    }
                } else {
                    setChanged();
                    notify("Unsuccessfull database query");
                }

                setChanged();
                notify("Successfully created the file : " + getBillingDefaults().getOutputFileDir()
                        + "\n" + validReadings + " Valid Readings Reported.");
            } 
            catch (java.io.IOException ioe) {
                setChanged();
                notify("Unsuccessfull reading of file : " + getBillingDefaults().getOutputFileDir());
                CTILogger.error(ioe);
            }
        }
		else if( fileFormatBase != null ) {
		    fileFormatBase.setBillingDefaults(getBillingDefaults());
		    
		    CTILogger.info("Valid entries are for meter data where: ");
		    CTILogger.info("  DEMAND readings > " + getBillingDefaults().getDemandStartDate() + " AND <= " + getBillingDefaults().getEndDate());
		    CTILogger.info("  ENERGY readings > " + getBillingDefaults().getEnergyStartDate() + " AND <= " + getBillingDefaults().getEndDate());
		    
		    boolean success = false;
		    
		    if (getBillingDefaults().getDeviceGroups().isEmpty())
		        success = false;
		    else
		        success = fileFormatBase.retrieveBillingData();
		    
		    try
		    {
		        if( success ) {
		            fileFormatBase.writeToFile();
		        }
		        else {
		            setChanged();
		            notify("Unsuccessfull database query" );
		        }
		        
		        setChanged();
		        notify("Successfully created the file : " + fileFormatBase.getOutputFileName() + "\n" + fileFormatBase.getRecordCount() + " Valid Readings Reported.");
		    }
		    catch(java.io.IOException ioe) {
		        setChanged();
		        notify("Unsuccessfull reading of file : " + fileFormatBase.getOutputFileName() );
		        CTILogger.error(ioe);
		    }
		}
	}

	/**
	 * Method encodeOutput.
	 * Retrieve all billingData and write to outputStream for a formatBase.
	 * @param out
	 * @throws IOException
	 */
	public void encodeOutput(java.io.OutputStream out) throws java.io.IOException
	{
        if( billingFormatter != null )
        {
            billingFormatter.setBillingFileDefaults(getBillingDefaults());
    
            CTILogger.info("Valid entries are for meter data where: ");
            CTILogger.info("  DEMAND readings > " + getBillingDefaults().getDemandStartDate() + " AND <= " + getBillingDefaults().getEndDate());
            CTILogger.info("  ENERGY readings > " + getBillingDefaults().getEnergyStartDate() + " AND <= " + getBillingDefaults().getEndDate());
    
            boolean success = false;
            
            List<BillableDevice> deviceList = null;
            if (!getBillingDefaults().getDeviceGroups().isEmpty()) {

                deviceList = BillingDao.retrieveBillingData(getBillingDefaults());

                //a 0 sized deviceList can still be a success.
                success = deviceList.size() > 0;
            }
            
            int validReadings = 0;
            if (success) {
                try {
                    validReadings = billingFormatter.writeBillingFile(deviceList, out);
                } catch (IllegalArgumentException e) {
                    setChanged();
                    notify(e.getMessage());
                }
            } else {
                setChanged();
                notify("Unsuccessfull database query");
            }

            setChanged();
            notify("Successfully created the file : " + getBillingDefaults().getOutputFileDir()
                    + "\n" + validReadings + " Valid Readings Reported.");
            
        } 
        else if( fileFormatBase != null ) {
			fileFormatBase.setBillingDefaults(getBillingDefaults());
	
			CTILogger.info("Valid entries are for meter data where: ");
			CTILogger.info("  DEMAND readings > " + getBillingDefaults().getDemandStartDate() + " AND <= " + getBillingDefaults().getEndDate());
			CTILogger.info("  ENERGY readings > " + getBillingDefaults().getEnergyStartDate() + " AND <= " + getBillingDefaults().getEndDate());
	
			boolean success = false;
			
			if (getBillingDefaults().getDeviceGroups().isEmpty())
				success = false;
			else
				success = fileFormatBase.retrieveBillingData( );	

			try
			{
				if( success ) {
					fileFormatBase.writeToFile(out);
				}
				else  {
					notify("Unsuccessfull database query" );
				}
			}
			catch(java.io.IOException ioe) {
				CTILogger.error(ioe);
			}
		}
	}
	
	/**
	 * Notify observers and write to CTILogger the notifyString.
	 * @param notifyString
	 */
	private void notify(String notifyString)
	{
		this.notifyObservers(notifyString );
		CTILogger.info(notifyString);
	}

	/**
	 * Returns the billingDefaults.
	 * If null, value is instantiated.
	 * @return BillingFileDefaults
	 */
	public BillingFileDefaults getBillingDefaults()
	{
		if( billingDefaults == null)
			billingDefaults = new BillingFileDefaults();
		return billingDefaults;
	}

	/**
	 * Returns the fileFormatBase.
	 * @return FileFormatBase
	 */
	public FileFormatBase getFileFormatBase()
	{
		return fileFormatBase;
	}

	/**
	 * Sets the billingDefaults.
	 * @param billingDefaults The billingDefaults to set
	 */
	public void setBillingDefaults(BillingFileDefaults billingDefaults)
	{
		this.billingDefaults = billingDefaults;
	}

	/**
	 * Sets the billingFormatter
	 * @return
	 */
    public BillingFormatter getBillingFormatter() {
        return billingFormatter;
    }

    /**
     * Sets the billingFormatter.  If the formatID is a legacy format, then 
     * the fileFormatBase object will be set instead.
     * Sets the billingDefaults will be populated for the format object being used.
     * @param formatID
     */
    public void setBillingFormatter(int formatID) {
        this.billingFormatter = BillingFormatterFactory.createFileFormat(formatID);
        
        if (billingFormatter != null)
            this.billingFormatter.setBillingFileDefaults(getBillingDefaults());
        
        else {  //we have an older format, use the legacy FileFormatBase object
            this.fileFormatBase = FileFormatFactory.createFileFormat(formatID);
            this.fileFormatBase.setBillingDefaults(getBillingDefaults());
        }
    }
}
