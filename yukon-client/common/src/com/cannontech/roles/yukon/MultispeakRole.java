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
	
	//The Vendor Config property is a comma separated string in the following form:
	//<companyName>,<username>,<password>,<deviceName|meterNumber>,<webserviceURL>,<service=serviceEndpoint0>...<service=serviceEndpoint1>
	// where <deviceName|meterNumber> is the unique key between yukon and vendor
	
	//The Vendor services property is a comma separated string in the following form:
	//<service=serviceName> 
	//where service is the multispeak function (MR_CB for example) and serviceName is the service port name (MR_CBSoap for example)
	
	public static final int VENDOR_01_CONFIG = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE;
	public static final int VENDOR_02_CONFIG = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE -1;
	public static final int VENDOR_03_CONFIG = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE -2;
	public static final int VENDOR_04_CONFIG = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE -3;
	public static final int VENDOR_05_CONFIG = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE -4;
	public static final int VENDOR_06_CONFIG = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE -5;
	public static final int VENDOR_07_CONFIG = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE -6;
	public static final int VENDOR_08_CONFIG = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE -7;
	public static final int VENDOR_09_CONFIG = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE -8;
	public static final int VENDOR_10_CONFIG = YukonRoleDefs.MULTISPEAK_PROPERTYID_BASE -9;
}
