package com.cannontech.roles.operator;

import com.cannontech.roles.OperatorRoleDefs;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface InventoryRole {
	public static final int ROLEID = OperatorRoleDefs.INVENTORY_ROLEID;
	
	public static final int INVENTORY_SHOW_ALL = OperatorRoleDefs.INVENTORY_PROPERTYID_BASE;
	public static final int SN_ADD_RANGE = OperatorRoleDefs.INVENTORY_PROPERTYID_BASE - 1;
	public static final int SN_UPDATE_RANGE = OperatorRoleDefs.INVENTORY_PROPERTYID_BASE - 2;
	public static final int SN_CONFIG_RANGE = OperatorRoleDefs.INVENTORY_PROPERTYID_BASE - 3;
	public static final int SN_DELETE_RANGE = OperatorRoleDefs.INVENTORY_PROPERTYID_BASE - 4;
	public static final int INVENTORY_CREATE_HARDWARE = OperatorRoleDefs.INVENTORY_PROPERTYID_BASE - 5;
	public static final int INVENTORY_CREATE_MCT = OperatorRoleDefs.INVENTORY_PROPERTYID_BASE - 6;

}
