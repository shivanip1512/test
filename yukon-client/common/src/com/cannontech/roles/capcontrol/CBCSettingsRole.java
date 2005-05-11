package com.cannontech.roles.capcontrol;

import com.cannontech.roles.*;

/**
 * @author ryan
 * 
insert into YukonRole values (-700,'CapControl Settings','CapBank Control','Allows the user to control access and display settings of the CapControl module.');

insert into YukonRoleProperty values(-70000,-700,'Access','false','Sets accessibility to the CapControl module.');
insert into YukonRoleProperty values(-70001,-700,'Allow Control','true','Enables or disables field and local controls for the given user');
insert into YukonRoleProperty values(-70002,-700,'Hide Reports','false','Sets the visibility of reports.');
insert into YukonRoleProperty values(-70003,-700,'Hide Graphs','false','Sets the visibility of graphs.');
insert into YukonRoleProperty values(-70004,-700,'Hide One-Lines','false','Sets the visibility of one-line displays.');

 */
public interface CBCSettingsRole
{
	public static final int ROLEID = CapControlRoleDefs.CBC_SETTINGS_ROLEID;

	public static final int ACCESS = CapControlRoleDefs.CBC_PROPERTYID_BASE;
	public static final int ALLOW_CONTROLS = CapControlRoleDefs.CBC_PROPERTYID_BASE - 1;
	public static final int HIDE_REPORTS = CapControlRoleDefs.CBC_PROPERTYID_BASE - 2;
	public static final int HIDE_GRAPHS = CapControlRoleDefs.CBC_PROPERTYID_BASE - 3;
	public static final int HIDE_ONELINE = CapControlRoleDefs.CBC_PROPERTYID_BASE - 4;

	public static final int CAP_CONTROL_INTERFACE = CapControlRoleDefs.CBC_PROPERTYID_BASE - 5;
	public static final int CBC_CREATION_NAME = CapControlRoleDefs.CBC_PROPERTYID_BASE - 6;
	public static final int PFACTOR_DECIMAL_PLACES = CapControlRoleDefs.CBC_PROPERTYID_BASE - 7;
	public static final int CBC_ALLOW_OVUV = CapControlRoleDefs.CBC_PROPERTYID_BASE - 8;
	public static final int CBC_REFRESH_RATE = CapControlRoleDefs.CBC_PROPERTYID_BASE - 9;
}