package com.cannontech.dbeditor;

/**
 * Insert the type's description here.
 * Creation date: (6/14/2002 3:25:50 PM)
 * @author: 
 */
public class DBDeletionWarn {

	private static StringBuffer theWarning = new StringBuffer("");

	public static final int POINT_TYPE = 1;
	public static final int NOTIFICATION_TYPE = 2;
	public static final int STATEGROUP_TYPE = 3;
	public static final int PORT_TYPE = 4;
	public static final int DEVICE_TYPE = 5;
	
/**
 * DBDeletionWarn constructor comment.
 */
private DBDeletionWarn() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:36:20 PM)
 * @return java.lang.String
 * @param pointID int
 */
private static boolean createDeleteStringForCommPort(int portID) throws java.sql.SQLException
{
	Integer theID = new Integer( portID );

	if( com.cannontech.database.data.port.DirectPort.hasDevice( theID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a device.");
		return false;
	}

	//this point is deleteable
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:36:20 PM)
 * @return java.lang.String
 * @param pointID int
 */
private static boolean createDeleteStringForDevice(int deviceID) throws java.sql.SQLException
{
	Integer theID = new Integer( deviceID );

	if( com.cannontech.database.data.device.DeviceBase.hasRoute( theID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is utilized by a route.");
		return false;
	}

	//this point is deleteable
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:36:20 PM)
 * @return java.lang.String
 * @param pointID int
 */
private static boolean createDeleteStringForNotification(int noteID) throws java.sql.SQLException
{
	Integer theID = new Integer( noteID );

	if( com.cannontech.database.data.notification.NotificationRecipient.hasPointAlarming( theID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a point alarm.");
		return false;
	}
	
	
	if( com.cannontech.database.data.notification.GroupNotification.hasAlarmCategory( theID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by an Alarm Category.");
		return false;
	}
	if( com.cannontech.database.data.notification.GroupNotification.hasPointAlarming( theID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a point alarm.");
		return false;
	}

	//this point is deleteable
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:36:20 PM)
 * @return java.lang.String
 * @param pointID int
 */
private static boolean createDeleteStringForPoints(int pointID) throws java.sql.SQLException
{
	Integer ptID = new Integer( pointID );

	if( com.cannontech.database.data.point.PointBase.hasRawPointHistorys( ptID ) || com.cannontech.database.data.point.PointBase.hasSystemLogEntry( ptID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nThis Point has historical data that will be lost if removed.");
		return false;
	}
		
	if( com.cannontech.database.data.point.PointBase.hasCapControlSubstationBus( ptID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a CapControl Substation Bus.");
		return false;
	}

	if( com.cannontech.database.data.point.PointBase.hasCapBank( ptID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a CapBank Device.");
		return false;
	}

	if( com.cannontech.database.data.point.PointBase.hasLMTrigger( ptID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a LoadManagement Trigger.");
		return false;
	}

	if( com.cannontech.database.data.point.PointBase.hasLMGroup( ptID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a Load Group.");
		return false;
	}
	
	
	//this point is deleteable
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:36:20 PM)
 * @return java.lang.String
 * @param pointID int
 */
private static boolean createDeleteStringForStateGroup(int sGroupID) throws java.sql.SQLException
{
	Integer theID = new Integer( sGroupID );

	if( com.cannontech.database.data.state.GroupState.hasPoint( theID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a point.");
		return false;
	}

	//this point is deleteable
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 5:28:42 PM)
 * @param event java.awt.event.ActionEvent
 */
protected static boolean deletionAttempted(int anID, int type) throws java.sql.SQLException
{
	
		theWarning.delete(0, theWarning.length());	
	
		if (type == POINT_TYPE)
			return createDeleteStringForPoints(anID);

		else if(type == NOTIFICATION_TYPE)
			return createDeleteStringForNotification(anID);

		else if(type == STATEGROUP_TYPE)
			return createDeleteStringForStateGroup(anID);

		else if(type == PORT_TYPE)
			return createDeleteStringForCommPort(anID);

		else if(type == DEVICE_TYPE)
			return createDeleteStringForDevice(anID);

		else
		{
			return false;
		}
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2002 4:48:31 PM)
 * @return java.lang.StringBuffer
 */
protected static java.lang.StringBuffer getTheWarning() {
	return theWarning;
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2002 4:48:31 PM)
 * @param newTheWarning java.lang.StringBuffer
 */
void setTheWarning(java.lang.StringBuffer newTheWarning) {
	theWarning = newTheWarning;
}
}
