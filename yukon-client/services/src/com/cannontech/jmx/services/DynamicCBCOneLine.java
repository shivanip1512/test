package com.cannontech.jmx.services;


import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import com.cannontech.cbc.web.OneLineSubs;
import com.cannontech.jmx.CTIBaseDynMBean;

/**
 * @author rneuharth
 *
 * Wrapper for the OneLineSubs to allow plugability into a JMX server
 * 
 */
public class DynamicCBCOneLine extends CTIBaseDynMBean
{
	private OneLineSubs oneLine = null;


	/**
	 * Main constructor
	 */
	public DynamicCBCOneLine()
	{
		super();
		
		oneLine = new OneLineSubs();		
		initialize();		
	}

	
	/**
	 * Starts the OneLineSub Service
	 */
	public void start()
	{
        oneLine.start();
	}

	/**
	 * Stops the OneLineSub Service
	 */
	public void stop()
	{
        oneLine.stop();
	}

	/**
	 * Gets the running state
	 */
	public boolean isRunning()
	{
		return oneLine.isRunning();
	}

    /**
     * Gets the location for generated files to be placed
     */
    public String getOutputPath()
    {
        return oneLine.getDirBase();
    }
    
	//
	// JMX Specific Part
	//
	protected MBeanAttributeInfo[] createMBeanAttributeInfo()
	{
		return new MBeanAttributeInfo[]
		{
			new MBeanAttributeInfo("Running", "boolean", "The running status of the Service", true, false, true),
            new MBeanAttributeInfo("OutputPath", String.class.getName(), "Location of where the generated files are to be placed", true, true, false),            
		};
	}

	protected MBeanOperationInfo[] createMBeanOperationInfo()
	{
		return new MBeanOperationInfo[]
		{
			new MBeanOperationInfo( 
				"start", "Start the OneLine Generation Service", 
				new MBeanParameterInfo[0], "void", MBeanOperationInfo.ACTION),

			new MBeanOperationInfo( 
				"stop", "Stop the OneLine Generation Service", 
				new MBeanParameterInfo[0], "void", MBeanOperationInfo.ACTION)
		};
	}

}
