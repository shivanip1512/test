package com.cannontech.database.data.lite;

/**
 * This type was created in VisualAge.
 */
public final class LiteFactory {
/**
 * This method was created in VisualAge.
 */
public final static com.cannontech.database.db.DBPersistent createDBPersistent(LiteBase liteObject) {

	try
	{ 
		com.cannontech.database.db.DBPersistent returnObject = null;

		int liteType = liteObject.getLiteType();
		
		switch( liteType )
		{
			case LiteTypes.YUKON_PAOBJECT:
			//case LiteTypes.DEVICE:
			//case LiteTypes.ROUTE:
			//case LiteTypes.PORT:
				returnObject = com.cannontech.database.data.pao.PAOFactory.createPAObject( (LiteYukonPAObject)liteObject );

				if( returnObject instanceof com.cannontech.database.data.device.DeviceBase )
				{
					((com.cannontech.database.data.device.DeviceBase)returnObject).setDeviceID(new Integer( ((LiteYukonPAObject)liteObject).getYukonID()) );
					((com.cannontech.database.data.device.DeviceBase)returnObject).setPAOName( ((LiteYukonPAObject)liteObject).getPaoName());
				}
				else if( returnObject instanceof com.cannontech.database.data.port.DirectPort )
				{	
					((com.cannontech.database.data.port.DirectPort)returnObject).setPortID( new Integer(((LiteYukonPAObject)liteObject).getYukonID()) );
					((com.cannontech.database.data.port.DirectPort)returnObject).setPortName( ((LiteYukonPAObject)liteObject).getPaoName() );
				}
				else if( returnObject instanceof com.cannontech.database.data.route.RouteBase )
				{	
					((com.cannontech.database.data.route.RouteBase)returnObject).setRouteID( new Integer(((LiteYukonPAObject)liteObject).getYukonID()) );
					((com.cannontech.database.data.route.RouteBase)returnObject).setRouteName( ((LiteYukonPAObject)liteObject).getPaoName() );
				}
				else if( returnObject instanceof com.cannontech.database.data.customer.CICustomerBase )
				{	
					((com.cannontech.database.data.customer.CICustomerBase)returnObject).setCustomerID( new Integer(((LiteYukonPAObject)liteObject).getYukonID()) );
					((com.cannontech.database.data.customer.CICustomerBase)returnObject).setCustomerName( ((LiteYukonPAObject)liteObject).getPaoName() );
				}
				else if( returnObject instanceof com.cannontech.database.data.capcontrol.CapControlYukonPAOBase )
				{	
					((com.cannontech.database.data.capcontrol.CapControlYukonPAOBase)returnObject).setCapControlPAOID( new Integer(((LiteYukonPAObject)liteObject).getYukonID()) );
					((com.cannontech.database.data.capcontrol.CapControlYukonPAOBase)returnObject).setName( ((LiteYukonPAObject)liteObject).getPaoName() );
				}
				break;
				
			case LiteTypes.POINT:
				returnObject = com.cannontech.database.data.point.PointFactory.createPoint(((LitePoint)liteObject).getPointType());
				((com.cannontech.database.data.point.PointBase)returnObject).setPointID(new Integer(((LitePoint)liteObject).getPointID()));
				((com.cannontech.database.data.point.PointBase)returnObject).getPoint().setPointName(((LitePoint)liteObject).getPointName());
				break;
			case LiteTypes.STATEGROUP:
				returnObject = com.cannontech.database.data.state.StateFactory.createGroupState();
				((com.cannontech.database.data.state.GroupState)returnObject).setStateGroupID(new Integer(((LiteStateGroup)liteObject).getStateGroupID()));
				((com.cannontech.database.data.state.GroupState)returnObject).getStateGroup().setName(((LiteStateGroup)liteObject).getStateGroupName());
				break;
			case LiteTypes.GRAPHDEFINITION:
				returnObject = new com.cannontech.database.data.graph.GraphDefinition();
				((com.cannontech.database.data.graph.GraphDefinition)returnObject).getGraphDefinition().setGraphDefinitionID(new Long(((LiteGraphDefinition)liteObject).getGraphDefinitionID()));
				((com.cannontech.database.data.graph.GraphDefinition)returnObject).getGraphDefinition().setName(((LiteGraphDefinition)liteObject).getName());
				break;
			case LiteTypes.NOTIFICATION_GROUP:
				returnObject = com.cannontech.database.data.notification.NotificationGroupFactory.createGroupNotification();
				((com.cannontech.database.data.notification.GroupNotification)returnObject).setNotificatoinGroupID(new Integer(((LiteNotificationGroup)liteObject).getNotificationGroupID()));
				((com.cannontech.database.data.notification.GroupNotification)returnObject).getNotificationGroup().setGroupName(((LiteNotificationGroup)liteObject).getNotificationGroupName());
				break;
			case LiteTypes.GROUP_RECIPIENT:
				returnObject = com.cannontech.database.data.notification.GroupRecipientFactory.createNotificationRecipient();
				((com.cannontech.database.data.notification.NotificationRecipient)returnObject).setRecipientID(new Integer(((LiteNotificationRecipient)liteObject).getRecipientID()));
				((com.cannontech.database.data.notification.NotificationRecipient)returnObject).getNotificationRecipient().setRecipientName(((LiteNotificationRecipient)liteObject).getRecipientName());
				break;
			case LiteTypes.ALARM_CATEGORIES:
				returnObject = new com.cannontech.database.db.notification.AlarmCategory();
				((com.cannontech.database.db.notification.AlarmCategory)returnObject).setAlarmCategoryID(new Integer(((LiteAlarmCategory)liteObject).getAlarmStateID()));
				((com.cannontech.database.db.notification.AlarmCategory)returnObject).setCategoryName(((LiteAlarmCategory)liteObject).getCategoryName());
				((com.cannontech.database.db.notification.AlarmCategory)returnObject).setNotificationGroupID( new Integer( ((LiteAlarmCategory)liteObject).getNotificationGroupID()) );
				break;
			case LiteTypes.CUSTOMER_CONTACT:
				returnObject = new com.cannontech.database.data.customer.CustomerContact();
				((com.cannontech.database.data.customer.CustomerContact)returnObject).getCustomerContact().setContactID( new Integer(((LiteCustomerContact)liteObject).getContactID()) );
				((com.cannontech.database.data.customer.CustomerContact)returnObject).getCustomerContact().setContFirstName( ((LiteCustomerContact)liteObject).getContFirstName() );
				((com.cannontech.database.data.customer.CustomerContact)returnObject).getCustomerContact().setContLastName( ((LiteCustomerContact)liteObject).getContLastName() );
				((com.cannontech.database.data.customer.CustomerContact)returnObject).setLogInID( new Integer( ((LiteCustomerContact)liteObject).getLoginID() ) );
				break;
			case LiteTypes.DEVICE_METERNUMBER:
				returnObject = new com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase();
				((com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase)returnObject).getDeviceMeterGroup().setDeviceID(new Integer(((LiteDeviceMeterNumber)liteObject).getDeviceID()) );
				((com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase)returnObject).getDeviceMeterGroup().setMeterNumber(((LiteDeviceMeterNumber)liteObject).getMeterNumber() );
				break;				
			case LiteTypes.HOLIDAY_SCHEDULE:
				returnObject = new com.cannontech.database.data.holiday.HolidaySchedule();
				((com.cannontech.database.data.holiday.HolidaySchedule)returnObject).setHolidayScheduleID(new Integer(((LiteHolidaySchedule)liteObject).getHolidayScheduleID()) );
				((com.cannontech.database.data.holiday.HolidaySchedule)returnObject).setHolidayScheduleName( ((LiteHolidaySchedule)liteObject).getHolidayScheduleName() );
				break;				

			default:
				returnObject = null;
		}

		if( returnObject == null )
			throw new IllegalArgumentException("*** Unable to create a DBPersistant object from a givent Lite object in: createDBPersistent(LiteBase liteObject)");
		else
			return returnObject;
	}
	catch(java.sql.SQLException e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return null;
	}
}
/**
 * This method was created in VisualAge.
 */
public final static LiteBase createLite(com.cannontech.database.db.DBPersistent val) 
{ 
	LiteBase returnLite = null;

	if( val instanceof com.cannontech.database.data.port.DirectPort )
	{
		returnLite = new LiteYukonPAObject( 
			((com.cannontech.database.data.port.DirectPort)val).getPAObjectID().intValue() );
	}
	else if( val instanceof com.cannontech.database.data.route.RouteBase )
	{
		//returnLite = new LiteRoute( 
			//((com.cannontech.database.data.route.RouteBase)val).getRoute().getRouteID().intValue() );
		returnLite = new LiteYukonPAObject( 
			((com.cannontech.database.data.route.RouteBase)val).getPAObjectID().intValue() );
	}
	else if( val instanceof com.cannontech.database.data.customer.CICustomerBase )
	{
		returnLite = new LiteYukonPAObject( 
			((com.cannontech.database.data.customer.CICustomerBase)val).getPAObjectID().intValue() );
	}
	else if( val instanceof com.cannontech.database.data.capcontrol.CapControlYukonPAOBase )
	{
		returnLite = new LiteYukonPAObject( ((com.cannontech.database.data.capcontrol.CapControlYukonPAOBase)val).getPAObjectID().intValue() );
	}
	else if( val instanceof com.cannontech.database.data.point.PointBase )
	{
		returnLite = new LitePoint( 
			((com.cannontech.database.data.point.PointBase)val).getPoint().getPointID().intValue() );
	}
	else if( val instanceof com.cannontech.database.data.state.GroupState )
	{
		returnLite = new LiteStateGroup( 
			((com.cannontech.database.data.state.GroupState)val).getStateGroup().getStateGroupID().intValue() );
	}
	else if( val instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
	{
		returnLite = createLite( ((com.cannontech.database.db.DBPersistent)(((com.cannontech.database.data.multi.SmartMultiDBPersistent)val).getOwnerDBPersistent())) );
	}
	else if( val instanceof com.cannontech.database.data.holiday.HolidaySchedule )
	{
		returnLite = new LiteHolidaySchedule( 
			((com.cannontech.database.data.holiday.HolidaySchedule)val).getHolidayScheduleID().intValue() );
	}
	else if( val instanceof com.cannontech.database.data.multi.MultiDBPersistent )
	{
		returnLite = createLite( ((com.cannontech.database.db.DBPersistent)(((com.cannontech.database.data.multi.MultiDBPersistent)val).getDBPersistentVector().get(0))) );
	}
	else if( val instanceof com.cannontech.database.data.graph.GraphDefinition )
	{
		returnLite = new LiteGraphDefinition( ((com.cannontech.database.data.graph.GraphDefinition)val).getGraphDefinition().getGraphDefinitionID().intValue(),
											  ((com.cannontech.database.data.graph.GraphDefinition)val).getGraphDefinition().getName()	);
	}
	else if( val instanceof com.cannontech.database.data.notification.GroupNotification )
		returnLite = new LiteNotificationGroup( ((com.cannontech.database.data.notification.GroupNotification)val).getNotificationGroup().getNotificationGroupID().intValue() );		
	else if( val instanceof com.cannontech.database.data.notification.NotificationRecipient )
		returnLite = new LiteNotificationRecipient( ((com.cannontech.database.data.notification.NotificationRecipient)val).getNotificationRecipient().getRecipientID().intValue() );
	else if( val instanceof com.cannontech.database.db.notification.AlarmCategory )
		returnLite = new LiteAlarmCategory( ((com.cannontech.database.db.notification.AlarmCategory)val).getAlarmCategoryID().intValue() );
	else if( val instanceof com.cannontech.database.data.customer.CustomerContact )
		returnLite = new LiteCustomerContact( ((com.cannontech.database.data.customer.CustomerContact)val).getCustomerContact().getContactID().intValue() );
	else if( val instanceof com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase)
		returnLite = new LiteDeviceMeterNumber( ((com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase)val).getDeviceMeterGroup().getDeviceID().intValue(),
												((com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase)val).getDeviceMeterGroup().getMeterNumber());
	else if( val instanceof com.cannontech.database.data.pao.YukonPAObject )
	{

	//if( val instanceof com.cannontech.database.data.device.DeviceBase )
		returnLite = new LiteYukonPAObject( 
			((com.cannontech.database.data.pao.YukonPAObject)val).getPAObjectID().intValue() );
	}

		
	return returnLite;
}
}
