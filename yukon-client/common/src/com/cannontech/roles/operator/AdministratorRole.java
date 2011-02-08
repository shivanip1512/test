package com.cannontech.roles.operator;

import com.cannontech.roles.OperatorRoleDefs;

/**
 * @author aaron
 */
public interface AdministratorRole {
	public static final int ROLEID = OperatorRoleDefs.ADMINISTRATOR_ROLEID;
	
	public static final int ADMIN_CONFIG_ENERGY_COMPANY = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE;
	public static final int ADMIN_CREATE_ENERGY_COMPANY = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 1;
	public static final int ADMIN_DELETE_ENERGY_COMPANY = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 2;
	public static final int ADMIN_MANAGE_MEMBERS = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 3;
	public static final int ADMIN_VIEW_BATCH_COMMANDS = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 4;
	public static final int ADMIN_VIEW_OPT_OUT_EVENTS = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 5;
	public static final int ADMIN_MEMBER_LOGIN_CNTRL = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 6;
	public static final int ADMIN_MEMBER_ROUTE_SELECT = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 7;
    public static final int ADMIN_ALLOW_DESIGNATION_CODES = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 8;
    public static final int ADMIN_MULTI_WAREHOUSE = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 9;
    public static final int ADMIN_AUTO_PROCESS_BATCH_COMMANDS = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 10;
    public static final int ADMIN_MULTISPEAK_SETUP = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 11;
    public static final int ADMIN_LM_USER_ASSIGN = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 12;
    public static final int ADMIN_EDIT_CONFIG = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 13;
    public static final int ADMIN_VIEW_CONFIG = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 14;
    public static final int ADMIN_MANAGE_INDEXES = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 15;
    public static final int ADMIN_VIEW_LOGS = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 16;
    public static final int ADMIN_DATABASE_MIGRATION = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 17;
    public static final int ADMIN_EVENT_LOGS = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 18;
    public static final int ADMIN_ENERGY_COMPANY_SUPER_USER = OperatorRoleDefs.ADMINISTRATOR_PROPERTYID_BASE - 19;
}
