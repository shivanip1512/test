package com.cannontech.roles.yukon;

import com.cannontech.roles.YukonRoleDefs;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface EnergyCompanyRole {
	public static final int ROLEID = YukonRoleDefs.ENERGY_COMPANY_ROLEID;
	
	public static final int ADMIN_EMAIL_ADDRESS = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE;
	public static final int OPTOUT_NOTIFICATION_RECIPIENTS = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 1;
	public static final int DEFAULT_TIME_ZONE = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 2;
	public static final int CUSTOMER_GROUP_IDS = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 5;
	public static final int OPERATOR_GROUP_IDS = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 6;
	public static final int TRACK_HARDWARE_ADDRESSING = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 7;
	public static final int SINGLE_ENERGY_COMPANY = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 8;
	// SA protocol on/off toggle, etc.
    public static final int OPTIONAL_PRODUCT_DEV = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 9;
    public static final int DEFAULT_TEMPERATURE_UNIT = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 10;
    public static final int METER_MCT_BASE_DESIGNATION = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 11;
    public static final int APPLICABLE_POINT_TYPE_KEY = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 12;
	public static final int INHERIT_PARENT_APP_CATS = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 14;
}

