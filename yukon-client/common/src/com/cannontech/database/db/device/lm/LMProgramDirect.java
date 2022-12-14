package com.cannontech.database.db.device.lm;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */
public class LMProgramDirect extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private Integer notifyActiveOffset = new Integer(-1);
	private Integer notifyInactiveOffset = new Integer(-1);
    private Integer notifyAdjust = NOTIFY_ADJUST_DISABLED;
    private Integer enableSchedule = NOTIFY_SCHEDULE_DISABLED;
	private String heading = CtiUtilities.STRING_NONE;
	private String messageHeader = CtiUtilities.STRING_NONE;
	private String messageFooter = CtiUtilities.STRING_NONE;
	private Double triggerOffset = new Double(0.0);
	private Double restoreOffset = new Double(0.0);
	

	public static final String SETTER_COLUMNS[] = 
	{ 
		"NOTIFYACTIVEOFFSET", "HEADING", "MESSAGEHEADER", "MESSAGEFOOTER", "TRIGGEROFFSET", 
		"RESTOREOFFSET", "NOTIFYINACTIVEOFFSET", "NOTIFYADJUST", "NOTIFYSCHEDULE"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMProgramDirect";
	
    public static final Integer NOTIFY_ADJUST_ENABLED = 1;
	public static final Integer NOTIFY_ADJUST_DISABLED = -1;

	public static final Integer NOTIFY_SCHEDULE_ENABLED = 1;
    public static final Integer NOTIFY_SCHEDULE_DISABLED = -1;

/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMProgramDirect() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getNotifyActiveOffset(), getHeading(),
						   getMessageHeader(), getMessageFooter(), getTriggerOffset(), 
						   getRestoreOffset(), getNotifyInactiveOffset(), getNotifyAdjust(), 
						   shouldNotifyWhenScheduled() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "DeviceID", getDeviceID() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2001 2:42:06 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDeviceID() {
	return deviceID;
}

public Integer getNotifyActiveOffset() {
	return notifyActiveOffset;
}

public Integer getNotifyAdjust() {
    return notifyAdjust;
}

/**
 * Get Enable Schedule flag
 * 
 * @return Send notification of scheduled curtailments
 */
public Integer shouldNotifyWhenScheduled() {
    return enableSchedule;
}

public String getHeading() {
	return heading;
}

public String getMessageHeader() {
	return messageHeader;
}

public String getMessageFooter() {
	return messageFooter;
}

public Double getTriggerOffset() {
	return triggerOffset;
}

/**
 * @return Returns the restoreOffset.
 */
public Double getRestoreOffset() {
	return restoreOffset;
}

public static final LMDirectNotificationGroupList[] getAllNotificationGroupsList(Integer programDeviceID, java.sql.Connection conn) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	
	String sql = "select l.ProgramID, l.NotificationGrpID " +
				 "from " +
				 LMDirectNotificationGroupList.TABLE_NAME + " l " +
				 "where l.ProgramID = ? " +
				 "order by l.NotificationGrpID";

	try
	{		
		//conn = com.cannontech.database.PoolManager.getInstance().getConnection(
							//com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, programDeviceID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				LMDirectNotificationGroupList group = new LMDirectNotificationGroupList();
				
				group.setDeviceID( new Integer(rset.getInt("ProgramID")) );
				group.setNotificationGrpID( new Integer(rset.getInt("NotificationGrpID")) );

				tmpList.add( group );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt);
	}


	LMDirectNotificationGroupList retVal[] = new LMDirectNotificationGroupList[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}

/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getDeviceID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	if( results.length == SETTER_COLUMNS.length )
	{
		setNotifyActiveOffset( (Integer) results[0] );
		setHeading( (String) results[1] );
		setMessageHeader( (String) results[2] );
		setMessageFooter( (String) results[3] );
		setTriggerOffset( (Double) results[4] );
		setRestoreOffset( (Double) results[5] );
		setNotifyInactiveOffset( (Integer) results[6]);
        setNotifyAdjust( (Integer) results[7]);
        setEnableSchedule( (Integer) results[8]);
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2001 2:42:06 PM)
 * @param newDeviceID java.lang.Integer
 */
public void setDeviceID(java.lang.Integer newDeviceID) {
	deviceID = newDeviceID;
}

public void setHeading(String newHeading) {
	heading = newHeading;
}

public void setMessageHeader(String newHeader) {
	messageHeader = newHeader;
}

public void setMessageFooter(String newFooter) {
	messageFooter = newFooter;
}

public void setTriggerOffset(Double newTriggerOffset) {
	triggerOffset = newTriggerOffset;
}

public void setRestoreOffset(Double restoreOffset) {
	this.restoreOffset = restoreOffset;
}

/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getNotifyActiveOffset(), getHeading(), getMessageHeader(),
							getMessageFooter(), getTriggerOffset(), getRestoreOffset(), 
							getNotifyInactiveOffset(), getNotifyAdjust(), shouldNotifyWhenScheduled()};

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );

}
public Integer getNotifyInactiveOffset() {
	return notifyInactiveOffset;
}
public void setNotifyInactiveOffset(Integer notifyInactiveOffset) {
	this.notifyInactiveOffset = notifyInactiveOffset;
}
public void setNotifyActiveOffset(Integer notifyActiveOffset) {
	this.notifyActiveOffset = notifyActiveOffset;
}
public void setNotifyAdjust(Integer notifyAdjust) {
    this.notifyAdjust = notifyAdjust;
}

/**
 * Set Enable Schedule flag
 * 
 * @param enableSchedule Send notification of scheduled curtailments
 */
public void setEnableSchedule(Integer enableSchedule) {
    this.enableSchedule = enableSchedule;
}

}
