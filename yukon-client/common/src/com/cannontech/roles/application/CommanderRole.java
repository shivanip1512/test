package com.cannontech.roles.application;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface CommanderRole {
	public static final int ROLEID = ApplicationRoleDefs.COMMANDER_ROLEID;
	
	public static final int COMMAND_MSG_PRIORITY = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE;
	public static final int VERSACOM_SERIAL_MODEL = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 1;
	public static final int EXPRESSCOM_SERIAL_MODEL = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 2;
	public static final int DCU_SA205_SERIAL_MODEL = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 3;
	public static final int DCU_SA305_SERIAL_MODEL = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 4;
	
//	public static final int LOG_LEVEL = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE;
}
