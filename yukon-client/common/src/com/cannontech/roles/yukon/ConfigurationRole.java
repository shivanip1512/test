package com.cannontech.roles.yukon;

import com.cannontech.roles.YukonRoleDefs;

public interface ConfigurationRole {
	

	public static final int ROLEID = YukonRoleDefs.CONFIGURATION_ROLEID;
	
    public static final int DEVICE_DISPLAY_TEMPLATE = YukonRoleDefs.CONFIGURATION_PROPERTYID_BASE;
    public static final int ALERT_TIMEOUT_HOURS = YukonRoleDefs.CONFIGURATION_PROPERTYID_BASE - 1;
    public static final int CUSTOMER_INFO_IMPORTER_FILE_LOCATION = YukonRoleDefs.CONFIGURATION_PROPERTYID_BASE - 2;
    public static final int SYSTEM_TIMEZONE = YukonRoleDefs.CONFIGURATION_PROPERTYID_BASE - 3;
}
