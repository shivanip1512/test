/*
 * Created on Feb 2, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.jmx.services;

import java.lang.reflect.Method;
import java.util.GregorianCalendar;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import com.cannontech.yimp.importer.BulkImporter410;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.jmx.CTIBaseDynMBean;

/**
 * @author jdayton
 *
 * Wrapper for the BulkImporter410 to allow plugability into a JMX server
 * 
 */
public class DynamicImp extends CTIBaseDynMBean
{
	private BulkImporter410 bulkImp410 = null;

	/**
	 * Main constructor
	 */
	public DynamicImp()
	{
		super();
		
		bulkImp410 = new BulkImporter410();		
		initialize();		
	}

	
	/**
	 * Starts the BulkImp410 Service
	 */
	public void start()
	{
		bulkImp410.start();
	}

	/**
	 * Stops the BulkImp410 Service
	 */
	public void stop()
	{
		bulkImp410.stop();
	}

	/**
	 * Gets the running state
	 */
	public boolean isRunning()
	{
		return bulkImp410.isRunning();
	}

	/**
	 * Next time to run
	 */
	public String getNextImportTime()
	{
		try
		{
			Class cls = bulkImp410.getClass();
			Method method = cls.getDeclaredMethod("getNextImportTime", new Class[0]);
			method.setAccessible(true);
			GregorianCalendar result = (GregorianCalendar)method.invoke(bulkImp410, new Object[0]);
			
			return new ModifiedDate( result.getTime().getTime() ).toString();
		}
		catch (Exception ignored)
		{
			ignored.printStackTrace();
			return "Unknown";
		}		
	}

	//
	// JMX Specific Part
	//
	protected MBeanAttributeInfo[] createMBeanAttributeInfo()
	{
		return new MBeanAttributeInfo[]
		{
			new MBeanAttributeInfo("Running", "boolean", "The running status of the Service", true, false, true),
			new MBeanAttributeInfo("NextImportTime", GregorianCalendar.class.getName(), "The next time the Service will import values", true, false, false)
		};
	}

	protected MBeanOperationInfo[] createMBeanOperationInfo()
	{
		return new MBeanOperationInfo[]
		{
			new MBeanOperationInfo( 
				"start", "Start the Bulk Importer Service", 
				new MBeanParameterInfo[0], "void", MBeanOperationInfo.ACTION),

			new MBeanOperationInfo( 
				"stop", "Stop the Bulk Importer Service", 
				new MBeanParameterInfo[0], "void", MBeanOperationInfo.ACTION)
		};
	}

}
