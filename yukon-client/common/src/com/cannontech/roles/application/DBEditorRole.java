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
}
