package com.cannontech.database.db.device.lm;

import com.cannontech.common.util.CtiUtilities;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface ILMControlAreaTrigger
{
	public static final String TYPE_THRESHOLD = "Threshold";
	public static final String TYPE_STATUS = "Status";
	public static final int INVALID_INT_VALUE = 0;
	
	
	/* Projection specific defines */
	public static final String PROJ_TYPE_NONE		= CtiUtilities.STRING_NONE;
	public static final String PROJ_TYPE_LSF		= "LSF";	
	

}
