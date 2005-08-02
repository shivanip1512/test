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
	
	//The OMS vendor webservice URL
	public static final int OMS_WEBSERVICE_URL = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE;
	//The unique key between Yukon and OMS vendor (valid values: PaoName(DeviceName) | MeterNumber)
	public static final int OMS_UNIQUE_KEY = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE -1;
	public static final int OMS_USERNAME = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE - 2;
	public static final int OMS_PASSWORD = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE - 3;
	public static final int OMS_OA_OD_SERVICE_NAME = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE - 4;

	//The CIS vendor webservice URL
	public static final int CIS_WEBSERVICE_URL = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE - 10;
	//The unique key between Yukon and CIS vendor (valid values: PaoName(DeviceName) | MeterNumber)
	public static final int CIS_UNIQUE_KEY = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE - 11;
	public static final int CIS_USERNAME = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE - 12;
	public static final int CIS_PASSWORD = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE - 13;
	public static final int CIS_CB_MR_SERVICE_NAME = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE - 14;

}
