package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/13/2001 11:11:50 AM)
 * @author: 
 */
public class CurtailHistory extends BaseHistory {
/**
 * CurtailHistory constructor comment.
 */
public CurtailHistory() {
	super();
}
/**
 * CurtailHistory constructor comment.
 * @param dbAlias java.lang.String
 */
public CurtailHistory(String dbAlias) {
	super(dbAlias);
}
/**
 * Insert the method's description here.
 * Creation date: (7/13/2001 11:28:36 AM)
 * @return com.cannontech.web.history.HCurtailactivityActivity[]
 */
public HCurtailProgramActivity[] getCurtailProgramActivities() {
	String queryStr = "SELECT * FROM LMCURTAILPROGRAMACTIVITY ORDER BY CURTAILMENTSTARTTIME DESC";
	
	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);
	
		java.util.Date now = new java.util.Date();
	
		java.util.ArrayList activityList = new java.util.ArrayList();
		while (rset.next()) {
			HCurtailProgramActivity activity = new HCurtailProgramActivity();
			activity.setStatement(stmt);
			activity.setDeviceId( rset.getLong(1) );
			activity.setCurtailReferenceId( rset.getLong(2) );
			activity.setActionDateTime( rset.getTimestamp(3) );
			activity.setNotificationDateTime( rset.getTimestamp(4) );
			activity.setCurtailmentStartTime( rset.getTimestamp(5) );
			activity.setCurtailmentStopTime( rset.getTimestamp(6) );
			activity.setRunStatus( rset.getString(7) );
			activity.setAdditionalInfo( rset.getString(8) );

			activityList.add(activity);
		}

		HCurtailProgramActivity[] ret = new HCurtailProgramActivity[activityList.size()];
		activityList.toArray(ret);
	
		return ret;
	}
	catch (java.sql.SQLException e) {
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return new HCurtailProgramActivity[0];
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/13/2001 3:58:28 PM)
 * @return com.cannontech.web.history.HCurtailProgram[]
 */
public HCurtailProgram[] getCurtailPrograms() {
	String queryStr = "SELECT LMPROGRAMCURTAILMENT.DEVICEID,PAONAME FROM LMPROGRAMCURTAILMENT, YUKONPAOBJECT ";
	queryStr += "WHERE LMPROGRAMCURTAILMENT.DEVICEID = YUKONPAOBJECT.PAOBJECTID ";
	queryStr += "ORDER BY LMPROGRAMCURTAILMENT.DEVICEID";
	
	try {
		java.sql.ResultSet rset = stmt.executeQuery(queryStr);
	
		java.util.ArrayList programList = new java.util.ArrayList();
		while (rset.next()) {
			HCurtailProgram program = new HCurtailProgram();
			program.setStatement(stmt);
			program.setDeviceId( rset.getLong(1) );
			program.setProgramName( rset.getString(2) );

			programList.add(program);
		}

		HCurtailProgram[] ret = new HCurtailProgram[programList.size()];
		programList.toArray(ret);
	
		return ret;
	}
	catch (java.sql.SQLException e) {
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return new HCurtailProgram[0];
	}
}
}
