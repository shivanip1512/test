package com.cannontech.roles.yukon;

import com.cannontech.roles.*;

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

}
