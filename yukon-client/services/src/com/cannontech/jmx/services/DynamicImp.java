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

import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.importer.BulkImporter;

/**
 * @author jdayton
 *
 * Wrapper for the BulkImporter to allow plugability into a JMX server
 * 
 */
public class DynamicImp
{
	private BulkImporter bulkImp = null;

	/**
	 * Main constructor
	 */
	public DynamicImp()
	{
		super();
		
		bulkImp = new BulkImporter();		
	}

	
	/**
	 * Starts the BulkImp Service
	 */
	public void start()
	{
		bulkImp.start();
	}

	/**
	 * Stops the BulkImp Service
	 */
	public void stop()
	{
		bulkImp.stop();
	}

	/**
	 * Gets the running state
	 */
	public boolean isRunning()
	{
		return bulkImp.isRunning();
	}

	/**
	 * Next time to run
	 */
	public String getNextImportTime()
	{
		try
		{
			Class cls = bulkImp.getClass();
			Method method = cls.getDeclaredMethod("getNextImportTime", new Class[0]);
			method.setAccessible(true);
			GregorianCalendar result = (GregorianCalendar)method.invoke(bulkImp, new Object[0]);
			
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
