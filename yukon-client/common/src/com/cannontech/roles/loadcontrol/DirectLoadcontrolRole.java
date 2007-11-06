package com.cannontech.roles.loadcontrol;

import com.cannontech.roles.LMRoleDefs;
/**
 * @author aaron
 */
public interface DirectLoadcontrolRole {
	public static final int ROLEID = LMRoleDefs.DIRECT_LOADCONTROL_ROLEID;
	
	public static final int LOADCONTROL_LABEL = LMRoleDefs.DIRECT_LOADCONTROL_PROPERTYID_BASE;
	public static final int INDIVIDUAL_SWITCH = LMRoleDefs.DIRECT_LOADCONTROL_PROPERTYID_BASE - 1;
	public static final int THREE_TIER_DIRECT = LMRoleDefs.DIRECT_LOADCONTROL_PROPERTYID_BASE - 2;
	public static final int DIRECT_CONTROL = LMRoleDefs.DIRECT_LOADCONTROL_PROPERTYID_BASE - 3;
	public static final int ALLOW_CHECK_CONSTRAINTS = LMRoleDefs.DIRECT_LOADCONTROL_PROPERTYID_BASE - 4;
	public static final int ALLOW_OBSERVE_CONSTRAINTS = LMRoleDefs.DIRECT_LOADCONTROL_PROPERTYID_BASE - 5;
	public static final int ALLOW_OVERRIDE_CONSTRAINT = LMRoleDefs.DIRECT_LOADCONTROL_PROPERTYID_BASE - 6;
	public static final int DEFAULT_CONSTRAINT_SELECTION = LMRoleDefs.DIRECT_LOADCONTROL_PROPERTYID_BASE - 7;
    public static final int ALLOW_STOP_GEAR_ACCESS = LMRoleDefs.DIRECT_LOADCONTROL_PROPERTYID_BASE - 8;
}
