package com.cannontech.tdc.filter;

import java.util.BitSet;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ITDCFilter
{
	//archived filter
	public static final int COND_HISTORY         = 1;


	//alarm filters
	public static final int COND_ACTIVE_ALARMS   = 10;
	public static final int COND_INACTIVE_ALARMS = 11;
	public static final int COND_UNACKED_ALARMS  = 12;



	
	int getFilterID();
	String getName();
	String getDescription();

	BitSet getConditions();
	void setConditions( BitSet conditions_ );

	Object[] getValues();
}
