package com.cannontech.roles;

/**
 * @author aaron
 */
public interface CICustomerRoleDefs extends RoleDefs {
	public static final int DIRECT_CURTAILMENT_ROLEID = RoleDefs.CICUSTOMER_ROLEID_BASE - 1;
	public static final int ENERGY_BUYBACK_ROLEID = RoleDefs.CICUSTOMER_ROLEID_BASE - 2;
	
	public static final int USER_CONTROL_ROLEID = RoleDefs.CICUSTOMER_ROLEID_BASE - 6;
	
	static final int DIRECT_CURTAILMENT_PROPERTYID_BASE = RoleDefs.CICUSTOMER_PROPERTYID_BASE - 100;
	static final int ENERGY_BUYBACK_PROPERTYID_BASE = RoleDefs.CICUSTOMER_PROPERTYID_BASE - 200;
	
	static final int USER_CONTROL_PROPERTYID_BASE = RoleDefs.CICUSTOMER_PROPERTYID_BASE - 600;
}
