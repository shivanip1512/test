package com.cannontech.dbeditor;

/**
 * Insert the type's description here.
 * Creation date: (6/14/2002 3:25:50 PM)
 * @author: 
 */
public class DBDeletionWarn 
{
	private static StringBuffer theWarning = new StringBuffer("");
   
   //types of delete
	public static final int POINT_TYPE = 1;
	public static final int NOTIFICATION_TYPE = 2;
	public static final int STATEGROUP_TYPE = 3;
	public static final int PORT_TYPE = 4;
	public static final int DEVICE_TYPE = 5;

   //the return types of each possible delete
   public static final byte STATUS_ALLOW = 1;
   public static final byte STATUS_CONFIRM = 2;
   public static final byte STATUS_DISALLOW = 3;
   
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
private static byte createDeleteStringForCommPort(int portID) throws java.sql.SQLException
{
	Integer theID = new Integer( portID );

	if( com.cannontech.database.data.port.DirectPort.hasDevice( theID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a device.");
		return STATUS_DISALLOW;
	}

	//this point is deleteable
	return STATUS_ALLOW;
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:36:20 PM)
 * @return java.lang.String
 * @param pointID int
 */
private static byte createDeleteStringForDevice(int deviceID) throws java.sql.SQLException
{
	Integer theID = new Integer( deviceID );
   String str = null;   

   /* Some day we could consolidate all these seperate delete statements into one
    * statement. Do this when performance becomes an issue and put it into the
    * DeviceBase class 
    */
	if( (str = com.cannontech.database.data.device.DeviceBase.hasRoute(theID)) != null )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is utilized by the route named '"+ str + "'");
		return STATUS_DISALLOW;
	}

   if( (str = com.cannontech.database.db.capcontrol.DeviceCBC.usedCapBankController(theID)) != null )
   {
      theWarning.delete(0, theWarning.length());
      theWarning.append("\nbecause it is utilized by the Device named '" + str + "'");
      return STATUS_DISALLOW;
   }

   if( (str = com.cannontech.database.db.route.RepeaterRoute.isRepeaterUsed(theID)) != null )
   {
      theWarning.delete(0, theWarning.length());
      theWarning.append("\nbecause it is utilized by the route named '"+ str + "'");
      return STATUS_DISALLOW;
   }

	//this device is deleteable
	return STATUS_ALLOW;
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:36:20 PM)
 * @return java.lang.String
 * @param pointID int
 */
private static byte createDeleteStringForNotification(int noteID) throws java.sql.SQLException
{
	Integer theID = new Integer( noteID );

   /* Some day we could consolidate all these seperate delete statements into one
    * statement. Do this when performance becomes an issue and put it into the
    * NotificationBase class 
    */
	if( com.cannontech.database.data.notification.NotificationRecipient.hasPointAlarming( theID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a point alarm.");
      return STATUS_DISALLOW;
	}
	
	
	if( com.cannontech.database.data.notification.GroupNotification.hasAlarmCategory( theID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by an Alarm Category.");
		return STATUS_DISALLOW;
	}

	if( com.cannontech.database.data.notification.GroupNotification.hasPointAlarming( theID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a point alarm.");
		return STATUS_DISALLOW;
	}

	//this point is deleteable
	return STATUS_ALLOW;
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:36:20 PM)
 * @return java.lang.String
 * @param pointID int
 */
private static byte createDeleteStringForPoints(int pointID) throws java.sql.SQLException
{
	Integer ptID = new Integer( pointID );
   	      
   /* Some day we could consolidate all these seperate delete statements into one
    * statement. Do this when performance becomes an issue and put it into the
    * PointBase class 
    */
	if( com.cannontech.database.data.point.PointBase.hasCapControlSubstationBus( ptID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a CapControl Substation Bus.");
		return STATUS_DISALLOW;
	}

	if( com.cannontech.database.data.point.PointBase.hasCapBank( ptID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a CapBank Device.");
		return STATUS_DISALLOW;
	}

	if( com.cannontech.database.data.point.PointBase.hasLMTrigger( ptID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a LoadManagement Trigger.");
		return STATUS_DISALLOW;
	}

	if( com.cannontech.database.data.point.PointBase.hasLMGroup( ptID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a Load Group.");
		return STATUS_DISALLOW;
	}
	
   if( com.cannontech.database.data.point.PointBase.hasRawPointHistorys( ptID ) )
   {
      theWarning.delete(0, theWarning.length());
      theWarning.append("\nThis point has archived historical data that will be lost if removed.");
      return STATUS_CONFIRM;
   }
   
   if( com.cannontech.database.data.point.PointBase.hasSystemLogEntry( ptID ) )
   {
      theWarning.delete(0, theWarning.length());
      theWarning.append("\nThis point has system log data that will be lost if removed.");
      return STATUS_CONFIRM;
   }

	//this point is deleteable
	return STATUS_ALLOW;
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:36:20 PM)
 * @return java.lang.String
 * @param pointID int
 */
private static byte createDeleteStringForStateGroup(int sGroupID) throws java.sql.SQLException
{
	Integer theID = new Integer( sGroupID );

	if( com.cannontech.database.data.state.GroupState.hasPoint( theID ) )
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append("\nbecause it is used by a point.");
		return STATUS_DISALLOW;
	}

	//this point is deleteable
	return STATUS_ALLOW;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 5:28:42 PM)
 * @param event java.awt.event.ActionEvent
 */
public static byte deletionAttempted(int anID, int type) throws java.sql.SQLException
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
			return STATUS_DISALLOW;
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2002 4:48:31 PM)
 * @return java.lang.StringBuffer
 */
public static java.lang.StringBuffer getTheWarning() {
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
