package com.cannontech.roles.yukon;

import com.cannontech.roles.YukonRoleDefs;

public interface SystemRole 
{
	public static final int ROLEID = YukonRoleDefs.SYSTEM_ROLEID;
	
	public static final int DISPATCH_MACHINE = YukonRoleDefs.YUKON_PROPERTYID_BASE;
	public static final int DISPATCH_PORT = YukonRoleDefs.YUKON_PROPERTYID_BASE - 1;
	public static final int PORTER_MACHINE = YukonRoleDefs.YUKON_PROPERTYID_BASE - 2;
	public static final int PORTER_PORT = YukonRoleDefs.YUKON_PROPERTYID_BASE - 3;
	public static final int MACS_MACHINE = YukonRoleDefs.YUKON_PROPERTYID_BASE - 4;
	public static final int MACS_PORT = YukonRoleDefs.YUKON_PROPERTYID_BASE - 5;
	public static final int CAP_CONTROL_MACHINE = YukonRoleDefs.YUKON_PROPERTYID_BASE - 6;
	public static final int CAP_CONTROL_PORT = YukonRoleDefs.YUKON_PROPERTYID_BASE - 7;
	public static final int LOADCONTROL_MACHINE = YukonRoleDefs.YUKON_PROPERTYID_BASE - 8;
	public static final int LOADCONTROL_PORT = YukonRoleDefs.YUKON_PROPERTYID_BASE - 9;
	
	public static final int SMTP_HOST = YukonRoleDefs.YUKON_PROPERTYID_BASE - 10;
	public static final int MAIL_FROM_ADDRESS = YukonRoleDefs.YUKON_PROPERTYID_BASE - 11;	
	
	public static final int STARS_PRELOAD_DATA = YukonRoleDefs.YUKON_PROPERTYID_BASE - 13;	
    public static final int WEB_LOGO_URL = YukonRoleDefs.YUKON_PROPERTYID_BASE - 14;
    public static final int NOTIFICATION_HOST = YukonRoleDefs.YUKON_PROPERTYID_BASE - 16;
    public static final int NOTIFICATION_PORT = YukonRoleDefs.YUKON_PROPERTYID_BASE - 17;
    public static final int BATCHED_SWITCH_COMMAND_TOGGLE = YukonRoleDefs.YUKON_PROPERTYID_BASE - 19;
    public static final int STARS_ACTIVATION = YukonRoleDefs.YUKON_PROPERTYID_BASE - 20;
	
    public static final int BULK_IMPORTER_COMMUNICATIONS_ENABLED = YukonRoleDefs.YUKON_PROPERTYID_BASE -21;
    
}
