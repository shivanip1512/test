package com.cannontech.roles.yukon;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
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
	public static final int PRINT_INSERTS_SQL = YukonRoleDefs.YUKON_PROPERTYID_BASE - 12;
	public static final int STARS_SOAP_SERVER = YukonRoleDefs.YUKON_PROPERTYID_BASE - 13;
	
    public static final int WEB_LOGO_URL = YukonRoleDefs.YUKON_PROPERTYID_BASE - 14;
    public static final int VOICE_HOST = YukonRoleDefs.YUKON_PROPERTYID_BASE - 15;
    public static final int NOTIFICATION_HOST = YukonRoleDefs.YUKON_PROPERTYID_BASE - 16;
    public static final int NOTIFICATION_PORT = YukonRoleDefs.YUKON_PROPERTYID_BASE - 17;
	
}
