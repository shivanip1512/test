package com.cannontech.roles.cicustomer;

import com.cannontech.roles.CICustomerRoleDefs;

/**
 * @author aaron
 */
public interface DirectCurtailmentRole {
	public static final int ROLEID = CICustomerRoleDefs.DIRECT_CURTAILMENT_ROLEID;
	
	public static final int CURTAILMENT_LABEL = CICustomerRoleDefs.DIRECT_CURTAILMENT_PROPERTYID_BASE;
	public static final int CURTAILMENT_PROVIDER = CICustomerRoleDefs.DIRECT_CURTAILMENT_PROPERTYID_BASE - 1;
}
