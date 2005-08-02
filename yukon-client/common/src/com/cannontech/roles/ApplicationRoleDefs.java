package com.cannontech.roles;

/**
 * Role ID definitions for the Application category of roles.
 * @author aaron
 */
public interface ApplicationRoleDefs extends RoleDefs {
	
	public static final int DATABASE_EDITOR_ROLEID = APPLICATION_ROLEID_BASE;	
	public static final int TABULAR_DISPLAY_CONSOLE_ROLEID = APPLICATION_ROLEID_BASE - 1;	
	public static final int TRENDING_ROLEID = APPLICATION_ROLEID_BASE -2;	
	public static final int COMMANDER_ROLEID = APPLICATION_ROLEID_BASE - 3;	
	public static final int CALC_HISTORICAL_ROLEID = APPLICATION_ROLEID_BASE - 4;	
	public static final int  WEB_GRAPH_ROLEID = APPLICATION_ROLEID_BASE - 5;	
	public static final int  BILLING_ROLEID = APPLICATION_ROLEID_BASE - 6;	
	public static final int ESUBSTATION_EDITOR_ROLEID = APPLICATION_ROLEID_BASE - 7;	
	public static final int WEB_CLIENT_ROLEID = APPLICATION_ROLEID_BASE - 8;
	public static final int REPORTING_ROLEID = APPLICATION_ROLEID_BASE - 9;
		
	static final int DATABASE_EDITOR_PROPERTYID_BASE = APPLICATION_PROPERTYID_BASE;
	static final int TABULAR_DISPLAY_CONSOLE_PROPERTYID_BASE = APPLICATION_PROPERTYID_BASE - 100;
	static final int TRENDING_PROPERTYID_BASE = APPLICATION_PROPERTYID_BASE - 200;
	static final int COMMANDER_PROPERTYID_BASE = APPLICATION_PROPERTYID_BASE - 300;
	static final int CALC_HISTORICAL_PROPERTYID_BASE = APPLICATION_PROPERTYID_BASE - 400;
	static final int WEB_GRAPH_PROPERTYID_BASE = APPLICATION_PROPERTYID_BASE - 500;
	static final int BILLING_PROPERTYID_BASE = APPLICATION_PROPERTYID_BASE - 600;
	static final int ESUBSTATION_EDITOR_PROPERTYID_BASE = APPLICATION_PROPERTYID_BASE - 700;
	static final int WEB_CLIENT_PROPERTYID_BASE = APPLICATION_PROPERTYID_BASE - 800;
	static final int REPORTING_PROPERTYID_BASE = APPLICATION_PROPERTYID_BASE - 900;
}
