/*
 * Created on Oct 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.roles.operator;

import com.cannontech.roles.OperatorRoleDefs;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkOrderRole {
	public static final int ROLEID = OperatorRoleDefs.WORK_ORDER_ROLEID;
	
	public static final int WORK_ORDER_SHOW_ALL = OperatorRoleDefs.WORK_ORDER_PROPERTYID_BASE;
	public static final int WORK_ORDER_CREATE_NEW = OperatorRoleDefs.WORK_ORDER_PROPERTYID_BASE - 1;
	public static final int WORK_ORDER_REPORT = OperatorRoleDefs.WORK_ORDER_PROPERTYID_BASE - 2; 

}
