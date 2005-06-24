package com.cannontech.roles.ivr;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface OutboundCallingRole {
	public static final int ROLEID = IVRRoleDefs.OUTBOUND_CALLING_ROLEID;

	//property that originated from a different role
	public static final int VOICE_APP = YukonRoleDefs.IVR_PROPERTYID_BASE;


	public static final int NUMBER_OF_CHANNELS = IVRRoleDefs.IVR_PROPERTYID_BASE;
	public static final int TEMPLATE_ROOT = IVRRoleDefs.IVR_PROPERTYID_BASE - 1;	


}
