package com.cannontech.roles.operator;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface AdministratorRole {
	public static final int ROLEID = OperatorRoleDefs.ADMINISTRATOR_ROLEID;
	
	public static final int ADMIN_CONFIG_ENERGY_COMPANY = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE;
	public static final int ADMIN_CREATE_ENERGY_COMPANY = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 1;
	public static final int ADMIN_DELETE_ENERGY_COMPANY = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 2;
	public static final int ADMIN_MANAGE_MEMBERS = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 3;
}
