package com.cannontech.roles.yukon;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface SystemRole {
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
}
