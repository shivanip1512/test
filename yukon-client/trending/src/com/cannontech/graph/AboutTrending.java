package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (3/5/2001 10:36:05 AM)
 * @author: 
 */
public class AboutTrending extends com.cannontech.debug.gui.AbstractListDataViewer 
{
/**
 * ScheduleDebugViewer constructor comment.
 */
public AboutTrending() {
	super();
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 */
public AboutTrending(java.awt.Dialog owner) {
	super(owner);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 */
public AboutTrending(java.awt.Dialog owner, String title) {
	super(owner, title);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public AboutTrending(java.awt.Dialog owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 * @param modal boolean
 */
public AboutTrending(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 */
public AboutTrending(java.awt.Frame owner) {
	super(owner);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public AboutTrending(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public AboutTrending(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public AboutTrending(java.awt.Frame owner, boolean modal) {
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
public void setValue(Object o) 
{
	java.util.Vector data = new java.util.Vector(20);
	try
	{
		data.addElement("Copyright (C)1999-2001 Cannon Technologies");
		data.addElement("Version          : " + com.cannontech.graph.GraphClient.getVersion() );

		com.cannontech.database.db.version.CTIDatabase db = com.cannontech.common.version.VersionTools.getDatabaseVersion();
		data.addElement("DB Version   : " + db.getVersion() + "  Build:  " + db.getBuild() );
	}
	catch(Throwable t)  // Catch ALL things and just print them out
	{
		com.cannontech.clientutils.CTILogger.info("*** Throwable caught in + " + this.getClass() + " : " + t.getMessage() );
		data.addElement("(Exception occured while getting value)");
	}
	getJListInfo().setListData(data);
}
}
