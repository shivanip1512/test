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
	public static final int ROLEID = YukonRoleDefs.ENERGY_COMPANY_ROLDID;
	
	public static final int ADMIN_EMAIL_ADDRESS = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE;
	public static final int OPTOUT_NOTIFICATION_RECIPIENTS = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 1;
	public static final int DEFAULT_TIME_ZONE = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 2;
	public static final int SWITCH_COMMAND_FILE = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 3;
	public static final int OPTOUT_COMMAND_FILE = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 4;
	public static final int CUSTOMER_GROUP_NAME = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 5;

}
