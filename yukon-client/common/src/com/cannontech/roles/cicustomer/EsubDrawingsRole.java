package com.cannontech.roles.cicustomer;

import com.cannontech.roles.CICustomerRoleDefs;

/**
 * @author aaron
 */
public interface EsubDrawingsRole {
	public static final int ROLEID = CICustomerRoleDefs.ESUBSTATION_DRAWINGS_ROLEID;
	
	public static final int VIEW = CICustomerRoleDefs.ESUBSTATION_DRAWINGS_PROPERTYID_BASE;
	public static final int EDIT = CICustomerRoleDefs.ESUBSTATION_DRAWINGS_PROPERTYID_BASE - 1;
	public static final int CONTROL = CICustomerRoleDefs.ESUBSTATION_DRAWINGS_PROPERTYID_BASE - 2;
	
}
