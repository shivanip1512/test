package com.cannontech.dbeditor;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * @author rneuharth
 *
 * A simple class to handle DB changes for a PropertyPanel
 */
public class DBChangeGUIHandler 
{
	private PropertyPanel thePanel = null;
	private StringBuffer txtMsg = null;



	public static final int[] HANDLED_DBS =
	{ 
		DBChangeMsg.CHANGE_PAO_DB,
		DBChangeMsg.CHANGE_POINT_DB,
		DBChangeMsg.CHANGE_STATE_GROUP_DB,
		DBChangeMsg.CHANGE_GRAPH_DB,
		DBChangeMsg.CHANGE_NOTIFICATION_GROUP_DB,
		DBChangeMsg.CHANGE_ALARM_CATEGORY_DB,
		DBChangeMsg.CHANGE_CONTACT_DB
	};
		

	/**
	 * Constructor for DBChangeGUIHandler.
	 */
	public DBChangeGUIHandler( PropertyPanel panel_, StringBuffer txtMsg_ ) 
	{
		super();

		thePanel = panel_;
		txtMsg = txtMsg_;
	}
	
	private static boolean isHandled( int dbType_ )
	{
		for( int i = 0; i < HANDLED_DBS.length; i++ )
			if( HANDLED_DBS[i] == dbType_ )
				return true;
		
		return false;
	}
	
	
	public void handleGUIChange( DBChangeMsg msg )
	{
		LiteBase userLite = null;
		
		if( thePanel.getOriginalObjectToEdit() instanceof DBPersistent )
			userLite = LiteFactory.createLite( (DBPersistent)thePanel.getOriginalObjectToEdit() );
		else
			throw new IllegalArgumentException("Trying to handle a non DBPersitent class for a DBChange in the GUI"); 


		if( userLite.getLiteID() == msg.getId()
			 && (msg.getTypeOfChange() == msg.CHANGE_TYPE_DELETE
			 	  || msg.getTypeOfChange() == msg.CHANGE_TYPE_UPDATE) )
		{
			if( !isHandled(msg.getDatabase()) )
			{
				CTILogger.info("**** Unable to find matching object for the DBChangeMsg = " + msg.getDatabase() +
						" and the lite object type is = " + userLite.getLiteType() );

				return;
			}
			
			
			if( msg.getDatabase() == msg.CHANGE_POINT_DB )
				txtMsg.append(". Editing of '"+
					com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(userLite.getLiteID()) + "/" +
					userLite.toString() + "' was canceled." );
			else
				txtMsg.append(". Editing of '" + userLite.toString() + "' was canceled.");

			thePanel.fireCancelButtonPressed();			
		}



		/* For YukonPAOBjects and PointBase objects, we care about ALL change types since
		   they both have a DISABLE field */
/*
		if( msg.getDatabase() == msg.CHANGE_PAO_DB
			 && userObject instanceof com.cannontech.database.data.pao.YukonPAObject )
		{
			boolean hasDisable = 
							(userObject instanceof com.cannontech.database.data.device.DeviceBase) 
							|| msg.getTypeOfChange() == msg.CHANGE_TYPE_DELETE;
			
			com.cannontech.database.data.pao.YukonPAObject obj = (com.cannontech.database.data.pao.YukonPAObject)userObject;
			if( obj.getPAOCategory().equalsIgnoreCase(msg.getCategory())
				 && obj.getPAOType().equalsIgnoreCase(msg.getObjectType())
				 && obj.getPAObjectID().intValue() == msg.getId()
				 && hasDisable )
			{
				txtMsg.append(". Editing of '" + obj.getPAOName() + "' was canceled.");
				thePanel.fireCancelButtonPressed();
			}
		}
		else if( msg.getDatabase() == msg.CHANGE_POINT_DB 
					&& userObject instanceof com.cannontech.database.data.point.PointBase )
		{
			com.cannontech.database.data.point.PointBase obj = (com.cannontech.database.data.point.PointBase)userObject;
			if( obj.getPoint().getPointType().equalsIgnoreCase(msg.getObjectType())
				 && obj.getPoint().getPointID().intValue() == msg.getId() )
			{
				
				txtMsg.append( ". Editing of '"+
					com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(obj.getPoint().getPaoID().intValue()) + "/" +
					obj.getPoint().getPointName() + "' was canceled." );

				thePanel.fireCancelButtonPressed();
			}
		}

		// WE ONLY CARE ABOUT THE DELETE CHANGE TYPE FOR THE ITEMS BELOW
		else if( msg.getDatabase() == msg.CHANGE_STATE_GROUP_DB 
					&& userObject instanceof com.cannontech.database.data.state.GroupState )
		{
			com.cannontech.database.data.state.GroupState obj = (com.cannontech.database.data.state.GroupState)userObject;
			if( obj.getStateGroup().getStateGroupID().intValue() == msg.getId()
				 && msg.getTypeOfChange() == msg.CHANGE_TYPE_DELETE )
			{
				txtMsg.append( ". Editing of '"+
					obj.getStateGroup().getName() + "' was canceled." );

				thePanel.fireCancelButtonPressed();
			}			
		}
		else if( msg.getDatabase() == msg.CHANGE_GRAPH_DB 
					&& userObject instanceof com.cannontech.database.data.graph.GraphDefinition )
		{
			com.cannontech.database.data.graph.GraphDefinition obj = (com.cannontech.database.data.graph.GraphDefinition)userObject;
			if( obj.getGraphDefinition().getGraphDefinitionID().intValue() == msg.getId()
				 && msg.getTypeOfChange() == msg.CHANGE_TYPE_DELETE )
			{
				txtMsg.append( ". Editing of '"+
					obj.getGraphDefinition().getName() + "' was canceled." );

				thePanel.fireCancelButtonPressed();
			}
		}
		else if( msg.getDatabase() == msg.CHANGE_NOTIFICATION_GROUP_DB 
					&& userObject instanceof com.cannontech.database.data.notification.GroupNotification )
		{
			com.cannontech.database.data.notification.GroupNotification obj = (com.cannontech.database.data.notification.GroupNotification)userObject;
			if( obj.getNotificationGroup().getNotificationGroupID().intValue() == msg.getId()
				 && msg.getTypeOfChange() == msg.CHANGE_TYPE_DELETE )
			{
				txtMsg.append( ". Editing of '"+
					obj.getNotificationGroup().getGroupName() + "' was canceled." );

				thePanel.fireCancelButtonPressed();
			}			
		}
		else if( msg.getDatabase() == msg.CHANGE_ALARM_CATEGORY_DB 
					&& userObject instanceof com.cannontech.database.db.notification.AlarmCategory )
		{
			com.cannontech.database.db.notification.AlarmCategory obj = (com.cannontech.database.db.notification.AlarmCategory)userObject;
			if( obj.getAlarmCategoryID().intValue() == msg.getId()
				 && msg.getTypeOfChange() == msg.CHANGE_TYPE_DELETE )
			{
				txtMsg.append( ". Editing of '"+
					obj.getCategoryName()+ "' was canceled." );

				thePanel.fireCancelButtonPressed();
			}			
		}		
		else if( msg.getDatabase() == msg.CHANGE_CONTACT_DB 
					&& userObject instanceof com.cannontech.database.data.customer.Contact )
		{
			com.cannontech.database.data.customer.Contact obj = (com.cannontech.database.data.customer.Contact)userObject;
			if( obj.getContact().getContactID().intValue() == msg.getId()
				 && msg.getTypeOfChange() == msg.CHANGE_TYPE_DELETE )
			{
				txtMsg.append( ". Editing of '"+
					obj.getContact().getContFirstName() + " " +
					obj.getContact().getContLastName() + "' was canceled." );

				thePanel.fireCancelButtonPressed();
			}			
		}
*/
		
/*
					else if( msg.getDatabase() == msg.CHANGE_NOTIFICATION_RECIPIENT_DB 
								&& userObject instanceof com.cannontech.database.data.notification.ContactNotification )
					{	
						com.cannontech.database.data.notification.ContactNotification obj = (com.cannontech.database.data.notification.ContactNotification)userObject;
						if( obj.getContactNotification().getRecipientID().intValue() == msg.getId()
							 && msg.getTypeOfChange() == msg.CHANGE_TYPE_DELETE )
						{
							txtMsg.append( ". Editing of '"+
								obj.getContactNotification().getRecipientName() + "' was canceled." );

							current.fireCancelButtonPressed();
						}			
					}

		
		else
			com.cannontech.clientutils.CTILogger.info("**** Unable to find matching object for the DBChangeMsg = " + msg.getDatabase() +
						" and the object is = " + userObject.getClass().getName() );
*/		
		
	}
	
	

}
