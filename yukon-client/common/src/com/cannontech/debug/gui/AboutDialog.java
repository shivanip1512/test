package com.cannontech.debug.gui;

import java.util.Collection;

import com.cannontech.common.util.CtiUtilities;

/**
 * Insert the type's description here.
 * Creation date: (3/5/2001 10:36:05 AM)
 * @author: 
 */
public class AboutDialog extends com.cannontech.debug.gui.AbstractListDataViewer 
{
/**
 * ScheduleDebugViewer constructor comment.
 */
public AboutDialog() {
	super();
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 */
public AboutDialog(java.awt.Dialog owner) {
	super(owner);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 */
public AboutDialog(java.awt.Dialog owner, String title) {
	super(owner, title);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public AboutDialog(java.awt.Dialog owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 * @param modal boolean
 */
public AboutDialog(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 */
public AboutDialog(java.awt.Frame owner) {
	super(owner);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public AboutDialog(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public AboutDialog(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public AboutDialog(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 10:36:05 AM)
 */
public Object getValue() 
{
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 10:36:05 AM)
 */
public void setValue(Object obj) 
{
	java.util.Vector data = new java.util.Vector(20);
	setTitle("About");

	try
	{
		data.addElement( CtiUtilities.COPYRIGHT );
		data.addElement("Version      : " + com.cannontech.common.version.VersionTools.getYUKON_VERSION() );
		//data.addElement("Dev ID    : " + pointValue.getDeviceID());

	
		String[] pools = com.cannontech.database.PoolManager.getInstance().getAllPoolsStrings();
		for( int i = 0; i < pools.length; i++ )
			data.addElement("DB Pool ("+i+")  : " + pools[i] );


		
		//Strange-- prints out 2 drivers for each one that is loaded??
/*
		int k = 0;
		java.util.Enumeration enum = java.sql.DriverManager.getDrivers();
		while( enum.hasMoreElements() )
		{
	      java.sql.Driver driver = (java.sql.Driver)enum.nextElement();
	      data.addElement("DB Driver("+(k++)+") : " + driver.getClass().getName() + " : " +
	         driver.getMajorVersion() + "." +
	         driver.getMinorVersion() );
		}
*/

		
		
      data.addElement("JRE Version  : " + System.getProperty("java.version") );

		/* ALWAYS leave this as the last thing */
		com.cannontech.database.db.version.CTIDatabase db = com.cannontech.common.version.VersionTools.getDatabaseVersion();
		com.cannontech.clientutils.commonutils.ModifiedDate md = new com.cannontech.clientutils.commonutils.ModifiedDate( db.getDateApplied().getTime() );		
		data.addElement("DB Version   : " + db.getVersion() + "  on  " + md.getDateString() );
		data.addElement("DB Alias     : " + CtiUtilities.getDatabaseAlias() );

	}
	catch(Throwable t)  // Catch ALL things and just print them out
	{
		com.cannontech.clientutils.CTILogger.info("*** Throwable caught in + " + this.getClass() + " : " + t.getMessage() );
		data.addElement("(Exception occured while getting values from database)");
	}

	//if we wanted to add more items, do it here
	if( obj instanceof Collection )
		data.addAll( (Collection)obj );


	getJListInfo().setListData(data);
}
}
