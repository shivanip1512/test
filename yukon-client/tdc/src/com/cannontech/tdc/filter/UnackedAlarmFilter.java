package com.cannontech.tdc.filter;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UnackedAlarmFilter extends TDCFilterBase
{
	public UnackedAlarmFilter()
	{
		super();
		setFilterID( 3 );
		
		getConditions().set( ITDCFilter.COND_UNACKED_ALARMS );
	}


	/* (non-Javadoc)
	 * @see com.cannontech.tdc.filter.ITDCFilter#getName()
	 */
	public String getName()
	{
		return "Unacked. Alarms";
	}

	/* (non-Javadoc)
	 * @see com.cannontech.tdc.filter.ITDCFilter#getDescription()
	 */
	public String getDescription()
	{
		return "All the unacknowledged alarms in the system";
	}

}
