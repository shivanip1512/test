/*
 * Created on Jul 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.roles.yukon;

import com.cannontech.roles.YukonRoleDefs;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface MultispeakRole {
	

	public static final int ROLEID = YukonRoleDefs.MULTISPEAK_ROLEID;
	
    public static final int MSP_PAONAME_ALIAS = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE;
    public static final int MSP_PRIMARY_CB_VENDORID = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE - 1;
    public static final int MSP_BILLING_CYCLE_PARENT_DEVICEGROUP = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE - 2;
}
