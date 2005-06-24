package com.cannontech.roles.yukon;

import com.cannontech.roles.*;

/**
 * @author ryan
 *
 */
public interface VoiceServerRole
{	
	public static final int ROLEID = YukonRoleDefs.VOICE_SERVER_ROLEID;
	
	public static final int CALL_PREFIX = YukonRoleDefs.VOICE_PROPERTYID_BASE;
	public static final int CALL_TIMEOUT = YukonRoleDefs.VOICE_PROPERTYID_BASE - 1;
	public static final int CALL_RESPONSE_TIMEOUT = YukonRoleDefs.VOICE_PROPERTYID_BASE - 2;
}
