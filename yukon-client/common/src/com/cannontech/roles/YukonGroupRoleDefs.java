package com.cannontech.roles;

/**
 * @author rneuharth
 *
 * Used to define the default not removable RoleGroups
 * 
 *  AuthFuncs.getGroup( operGroupName );
 */
public interface YukonGroupRoleDefs
{
	//general yukon reserved groups
	public static final int GRP_YUKON = -1;
	public static final int GRP_SYS_ADMIN = -2;
	public static final int GRP_OPERATORS = -100;
	
	
	//esub reserved groups
	public static final int GRP_ESUB_USERS = -200;
	public static final int GRP_ESUB_OPERATORS = -201;


	//stars reserved groups
	public static final int GRP_RESIDENTIAL_CUSTOMERS = -300;
	public static final int GRP_WEB_CLIENT_OPERATORS = -301;
	public static final int GRP_WEB_CLIENT_CUSTOMERS = -302;

}
