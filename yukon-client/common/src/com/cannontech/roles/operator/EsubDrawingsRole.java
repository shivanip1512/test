/*
 * Created on May 7, 2003
 */
package com.cannontech.roles.operator;

import com.cannontech.roles.OperatorRoleDefs;

/**
 * @author alauinger
  */
public interface EsubDrawingsRole {
	public static final int ROLEID = OperatorRoleDefs.ESUBSTATION_DRAWINGS_ROLEID;
	
	public static final int VIEW = OperatorRoleDefs.ESUBSTATION_DRAWINGS_PROPERTYID_BASE;
	public static final int EDIT = OperatorRoleDefs.ESUBSTATION_DRAWINGS_PROPERTYID_BASE - 1;
	public static final int CONTROL = OperatorRoleDefs.ESUBSTATION_DRAWINGS_PROPERTYID_BASE - 2;
}
