package com.cannontech.roles.operator;

import com.cannontech.roles.OperatorRoleDefs;

/**
 * @author aaron
 */
public interface DirectLoadcontrolRole {
	public static final int ROLEID = OperatorRoleDefs.DIRECT_LOADCONTROL_ROLEID;
	
	public static final int LOADCONTROL_LABEL = OperatorRoleDefs.DIRECT_LOADCONTROL_PROPERTYID_BASE;
	public static final int INDIVIDUAL_SWITCH = OperatorRoleDefs.DIRECT_LOADCONTROL_PROPERTYID_BASE - 1;
}
