package com.cannontech.roles.application;

import com.cannontech.roles.ApplicationRoleDefs;

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
	
	public static final int COMMANDS_GROUP = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 5;

    public static final int READ_DEVICE = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 6;
    public static final int WRITE_TO_DEVICE = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 7;
    //DEPRECATED 4.0.0 public static final int READ_DISCONNECT_DEVICE = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 8;
    public static final int CONTROL_DEVICE = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 9;

    public static final int READ_LM_DEVICE = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 10;
    public static final int WRITE_TO_LM_DEVICE = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 11;
    public static final int CONTROL_LM_DEVICE = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 12;

    public static final int READ_CAP_CONTROL_DEVICE = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 13;
    public static final int WRITE_TO_CAP_CONTROL_DEVICE = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 14;
    public static final int CONTROL_CAP_CONTROL_DEVICE = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 15;

    public static final int EXECUTE_UNKNOWN_COMMAND = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 16;
    public static final int EXECUTE_MANUAL_COMMAND = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE - 17;
	
//	public static final int LOG_LEVEL = ApplicationRoleDefs.COMMANDER_PROPERTYID_BASE;
}
