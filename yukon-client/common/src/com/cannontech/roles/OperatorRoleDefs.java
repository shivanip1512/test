package com.cannontech.roles;

/**
 * @author aaron
 */
public interface OperatorRoleDefs extends RoleDefs {
	public static final int ADMINISTRATOR_ROLEID = OPERATOR_ROLEID_BASE;
	public static final int CONSUMER_INFO_ROLEID = OPERATOR_ROLEID_BASE - 1;
	public static final int COMMERCIAL_METERING_ROLEID = OPERATOR_ROLEID_BASE - 2;
	public static final int DIRECT_LOADCONTROL_ROLEID = OPERATOR_ROLEID_BASE - 3;
	public static final int DIRECT_CURTAILMENT_ROLEID = OPERATOR_ROLEID_BASE - 4;
	public static final int ENERGY_BUYBACK_ROLEID = OPERATOR_ROLEID_BASE - 5;
	
	static final int ADMINISTRATOR_PROPERTYID_BASE = OPERATOR_PROPERTYID_BASE;
	static final int CONSUMER_INFO_PROPERTYID_BASE = OPERATOR_PROPERTYID_BASE - 100;
	static final int COMMERCIAL_METERING_PROPERTYID_BASE = OPERATOR_PROPERTYID_BASE - 200;
	static final int DIRECT_LOADCONTROL_PROPERTYID_BASE = OPERATOR_PROPERTYID_BASE - 300;
	static final int DIRECT_CURTAILMENT_PROPERTYID_BASE = OPERATOR_PROPERTYID_BASE - 400;
	static final int ENERGY_BUYBACK_PROPERTYID_BASE = OPERATOR_PROPERTYID_BASE - 500;
}
