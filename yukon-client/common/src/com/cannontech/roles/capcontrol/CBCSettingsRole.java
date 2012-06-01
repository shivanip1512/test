package com.cannontech.roles.capcontrol;

import com.cannontech.roles.ApplicationRoleDefs;
import com.cannontech.roles.CapControlRoleDefs;

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
	//public static final int ALLOW_CONTROLS = CapControlRoleDefs.CBC_PROPERTYID_BASE - 1;
	public static final int HIDE_REPORTS = CapControlRoleDefs.CBC_PROPERTYID_BASE - 2;
	public static final int HIDE_GRAPHS = CapControlRoleDefs.CBC_PROPERTYID_BASE - 3;
	public static final int HIDE_ONELINE = CapControlRoleDefs.CBC_PROPERTYID_BASE - 4;

	public static final int CAP_CONTROL_INTERFACE = CapControlRoleDefs.CBC_PROPERTYID_BASE - 5;
	public static final int CBC_CREATION_NAME = CapControlRoleDefs.CBC_PROPERTYID_BASE - 6;
	public static final int PFACTOR_DECIMAL_PLACES = CapControlRoleDefs.CBC_PROPERTYID_BASE - 7;
	public static final int CBC_ALLOW_OVUV = CapControlRoleDefs.CBC_PROPERTYID_BASE - 8;
	public static final int CBC_DATABASE_EDIT = CapControlRoleDefs.CBC_PROPERTYID_BASE - 10;
    public static final int SHOW_FLIP_COMMAND = ApplicationRoleDefs.CBC_PROPERTYID_BASE - 11;
    public static final int SHOW_CB_ADDINFO = ApplicationRoleDefs.CBC_PROPERTYID_BASE - 12;
    public static final int AVAILABLE_DEFINITION = ApplicationRoleDefs.CBC_PROPERTYID_BASE - 13;
    public static final int UNAVAILABLE_DEFINITION = ApplicationRoleDefs.CBC_PROPERTYID_BASE - 14;
    public static final int TRIPPED_DEFINITION = ApplicationRoleDefs.CBC_PROPERTYID_BASE - 15;
    public static final int CLOSED_DEFINITION = ApplicationRoleDefs.CBC_PROPERTYID_BASE - 16;
    public static final int ADD_COMMENTS = ApplicationRoleDefs.CBC_PROPERTYID_BASE - 17;
    public static final int MODIFY_COMMENTS = ApplicationRoleDefs.CBC_PROPERTYID_BASE - 18;
    public static final int SYSTEM_WIDE_CONTROLS = ApplicationRoleDefs.CBC_PROPERTYID_BASE - 19;
    public static final int FORCE_COMMENTS = ApplicationRoleDefs.CBC_PROPERTYID_BASE - 20;
    public static final int ALLOW_AREA_CONTROLS = CapControlRoleDefs.CBC_PROPERTYID_BASE - 21;
    public static final int ALLOW_SUBSTATION_CONTROLS = CapControlRoleDefs.CBC_PROPERTYID_BASE - 22;
    public static final int ALLOW_SUBBUS_CONTROLS = CapControlRoleDefs.CBC_PROPERTYID_BASE - 23;
    public static final int ALLOW_FEEDER_CONTROLS = CapControlRoleDefs.CBC_PROPERTYID_BASE - 24;
    public static final int ALLOW_CAPBANK_CONTROLS = CapControlRoleDefs.CBC_PROPERTYID_BASE - 25;
    public static final int CONTROL_WARNING = CapControlRoleDefs.CBC_PROPERTYID_BASE - 26;
    public static final int CAP_CONTROL_IMPORTER = CapControlRoleDefs.CBC_PROPERTYID_BASE - 27;
}