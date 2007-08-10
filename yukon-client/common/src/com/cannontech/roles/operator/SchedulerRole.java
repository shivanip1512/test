package com.cannontech.roles.operator;

import com.cannontech.roles.OperatorRoleDefs;

/**
 * @author aaron
 */
public interface SchedulerRole {
	public static final int ROLEID = OperatorRoleDefs.SCHEDULER_ROLEID;	//-212
	
	public static final int ENABLE_DISABLE_SCHEDULE = OperatorRoleDefs.SCHEDULER_PROPERTYID_BASE - 0;	// -21200
	
	/* NOTE:  THESE PROPERTIES SHOULD BE SUPPORTED AT SOME TIME, BUT THE DATABASE STATEMENTS
	 * HAVE NOT BEEN ADDED YET.  HERE THEY ARE FOR WHEN YOU ARE READY!
	 *  
	//public static final int CREATE_SCHEDULE = OperatorRoleDefs.SCHEDULER_PROPERTYID_BASE - 1;	// -21201
    //public static final int EDIT_SCHEDULE = OperatorRoleDefs.SCHEDULER_PROPERTYID_BASE - 2;	// -21202
    //public static final int DELETE_SCHEDULE = OperatorRoleDefs.SCHEDULER_PROPERTYID_BASE - 3;	// -21203

	 * insert into YukonRoleProperty values(-21201,-212,'Create Schedule','true','Right to create a schedule'); 
	 * insert into YukonRoleProperty values(-21202,-212,'Edit Schedule','true','Right to edit a schedule');
	 * insert into YukonRoleProperty values(-21203,-212,'Delete Schedule','true','Right to delete a schedule'); 
	 */

}
