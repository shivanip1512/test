package com.cannontech.tdc.filter;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ActiveAlarmFilter extends TDCFilterBase
{
	public ActiveAlarmFilter()
	{
		super();
		setFilterID( 1 );
		
		getConditions().set( ITDCFilter.COND_ACTIVE_ALARMS );
	}


	/* (non-Javadoc)
	 * @see com.cannontech.tdc.filter.ITDCFilter#getName()
	 */
	public String getName()
	{
		return "Active Alarms";
	}

	/* (non-Javadoc)
	 * @see com.cannontech.tdc.filter.ITDCFilter#getDescription()
	 */
	public String getDescription()
	{
		return "All the active alarms in the system";
	}

}
