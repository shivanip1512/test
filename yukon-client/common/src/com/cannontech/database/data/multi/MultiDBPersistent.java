package com.cannontech.database.data.multi;

/**
 * This type was created in VisualAge.
 */

public class MultiDBPersistent extends CommonMulti implements com.cannontech.database.db.CTIDbChange
{
/**
 * DeviceBase constructor comment.
 */
public MultiDBPersistent() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public java.util.Vector getDBPersistentVector() 
{
	return super.getDBPersistentVector();
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	/* retrieve(): Don't do this, MultiDBPersistent's
		 are used for coping multiple Objects at one time */
		 
	com.cannontech.clientutils.CTILogger.info("************************************************");
	com.cannontech.clientutils.CTILogger.info("retrieve(): Don't do this; MultiDBPersistents");
	com.cannontech.clientutils.CTILogger.info("are used for copying multiple objects at one time");
	com.cannontech.clientutils.CTILogger.info("************************************************");
}
/**
 * This method was created in VisualAge.
 */
public void setDBPersistentVector(java.util.Vector newValue)
{
	super.setDBPersistentVector(newValue);
}
}
