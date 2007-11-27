package com.cannontech.tdc.filter;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class InactiveAlarmFilter extends TDCFilterBase
{
	public InactiveAlarmFilter()
	{
		super();
		setFilterID( 2 );
		
		getConditions().set( ITDCFilter.COND_INACTIVE_ALARMS );
		getConditions().set( ITDCFilter.COND_HISTORY );
	}


	/* (non-Javadoc)
	 * @see com.cannontech.tdc.filter.ITDCFilter#getName()
	 */
	public String getName()
	{
		return "Alarm History";
	}

	/* (non-Javadoc)
	 * @see com.cannontech.tdc.filter.ITDCFilter#getDescription()
	 */
	public String getDescription()
	{
		return "History of alarms that have occurred in the system";
	}

}
