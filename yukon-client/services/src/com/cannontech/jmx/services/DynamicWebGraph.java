package com.cannontech.jmx.services;

import java.lang.reflect.Method;
import java.util.GregorianCalendar;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.jmx.CTIBaseDynMBean;
import com.cannontech.graph.WebGraph;

/**
 * @author rneuharth
 *
 * Wrapper for the WebGraph to allow plugability into a JMX server
 * 
 */
public class DynamicWebGraph extends CTIBaseDynMBean
{
	private WebGraph webGraph = null;


	/**
	 * Main constructor
	 */
	public DynamicWebGraph()
	{
		super();
		
		webGraph = new WebGraph();		
		initialize();		
	}

	
	/**
	 * Starts the WebGraph Service
	 */
	public void start()
	{
		webGraph.start();
	}

	/**
	 * Stops the WebGraph Service
	 */
	public void stop()
	{
		webGraph.stop();
	}

	/**
	 * Gets the running state
	 */
	public boolean isRunning()
	{
		return webGraph.isRunning();
	}

	/**
	 * Next time to run
	 */
	public String getNextRunTime()
	{
		try
		{
			Class cls = webGraph.getClass();
			Method method = cls.getDeclaredMethod("getNextRunTime", new Class[0]);
			method.setAccessible(true);
			GregorianCalendar result = (GregorianCalendar)method.invoke(webGraph, new Object[0]);
			
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
			new MBeanAttributeInfo("NextRunTime", GregorianCalendar.class.getName(), "The next time the Service will create the graphs", true, false, false)
		};
	}

	protected MBeanOperationInfo[] createMBeanOperationInfo()
	{
		return new MBeanOperationInfo[]
		{
			new MBeanOperationInfo( 
				"start", "Start the Notification Service", 
				new MBeanParameterInfo[0], "void", MBeanOperationInfo.ACTION),

			new MBeanOperationInfo( 
				"stop", "Stop the Notification Service", 
				new MBeanParameterInfo[0], "void", MBeanOperationInfo.ACTION)
		};
	}

}
