package com.cannontech.jmx;

import mx4j.AbstractDynamicMBean;

/**
 * @author rneuharth
 *
 * A base class with common functionalities for all DynamicMBeans
 */
public abstract class CTIBaseDynMBean extends AbstractDynamicMBean
{
	public abstract boolean isRunning();
	public abstract void start();
	public abstract void stop();

	/**
	 * A default constructor
	 */
	public CTIBaseDynMBean()
	{
		super();
		
	}

	/**
	 * Always call this method inside the childs constructor!!!
	 * For future use.
	 */
	protected void initialize()
	{		
	}

}
