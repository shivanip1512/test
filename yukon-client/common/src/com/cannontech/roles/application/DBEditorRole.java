package com.cannontech.roles.application;

import com.cannontech.roles.*;

/**
 * Database Editor Role Definition
 * @author aaron
 */
public interface DBEditorRole {
	public static final int ROLEID = ApplicationRoleDefs.DATABASE_EDITOR_ROLEID;		
	
	public static final int POINT_ID_EDIT = ApplicationRoleDefs.DATABASE_EDITOR_PROPERTYID_BASE;
	public static final int DBEDITOR_CORE = ApplicationRoleDefs.DATABASE_EDITOR_PROPERTYID_BASE - 1;
	public static final int DBEDITOR_LM = ApplicationRoleDefs.DATABASE_EDITOR_PROPERTYID_BASE - 2;
	public static final int DBEDITOR_CAP_CONTROL = ApplicationRoleDefs.DATABASE_EDITOR_PROPERTYID_BASE - 3;
	public static final int DBEDITOR_SYSTEM = ApplicationRoleDefs.DATABASE_EDITOR_PROPERTYID_BASE - 4;
	public static final int UTILITY_ID_RANGE = ApplicationRoleDefs.DATABASE_EDITOR_PROPERTYID_BASE - 5;
//	public static final int LOG_LEVEL = ApplicationRoleDefs.DATABASE_EDITOR_PROPERTYID_BASE - 6;
	public static final int TRANS_EXCLUSION = ApplicationRoleDefs.DATABASE_EDITOR_PROPERTYID_BASE - 7;
	public static final int PERMIT_LOGIN_EDIT = ApplicationRoleDefs.DATABASE_EDITOR_PROPERTYID_BASE - 8;
	public static final int ALLOW_USER_ROLES = ApplicationRoleDefs.DATABASE_EDITOR_PROPERTYID_BASE - 9;
	//Minnkota RIPPLE panel, SA protocol on/off toggle, etc.
	public static final int OPTIONAL_PRODUCT_DEV = ApplicationRoleDefs.DATABASE_EDITOR_PROPERTYID_BASE - 10;
	//LM Direct Programs have a Member Management tab if this is true
	public static final int ALLOW_MEMBER_PROGRAMS = ApplicationRoleDefs.DATABASE_EDITOR_PROPERTYID_BASE - 11;
}
