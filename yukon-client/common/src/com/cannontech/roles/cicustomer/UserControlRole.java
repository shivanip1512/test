/*
 * Created on Dec 22, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.roles.cicustomer;

import com.cannontech.roles.CICustomerRoleDefs;

/**
 * @author snebben
 */
public interface UserControlRole
{
	public static final int ROLEID = CICustomerRoleDefs.USER_CONTROL_ROLEID;
	
	public static final int USER_CONTROL_LABEL = CICustomerRoleDefs.USER_CONTROL_PROPERTYID_BASE;
	public static final int AUTO = CICustomerRoleDefs.USER_CONTROL_PROPERTYID_BASE - 1;
	public static final int TIME_BASED = CICustomerRoleDefs.USER_CONTROL_PROPERTYID_BASE - 2;
	public static final int SWITCH_COMMAND = CICustomerRoleDefs.USER_CONTROL_PROPERTYID_BASE - 3;

}
