package com.cannontech.clientutils.tags;

import com.cannontech.common.util.CtiUtilities;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IAlarmDefs
{
	public static final int MIN_CONDITION_ID = CtiUtilities.NONE_ZERO_ID;

	public static final String[] STATUS_ALARM_STATES =
	{
		"Non-updated",
		"Abnormal",
		"Uncommanded State Change",
		"Command Failure"
	};

	public static final String[] OTHER_ALARM_STATES =
	{
		"Non-updated",
		"Rate Of Change",
		"Limit Set 1",
		"Limit Set 2",
		"High Reasonability",
		"Low Reasonability"
	};
	
}
