package com.cannontech.dbeditor;

import Acme.RefInt;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.config.ConfigTwoWay;
import com.cannontech.database.data.holiday.HolidaySchedule;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.lm.LMProgramConstraint;


/**
 * Insert the type's description here.
 * Creation date: (6/14/2002 3:25:50 PM)
 * @author: jdayton
 */
public class DBDeletionFuncs 
{
	private static StringBuffer theWarning = new StringBuffer("");
	private static final String CR_LF = System.getProperty("line.separator");
   

   //types of delete
	public static final int POINT_TYPE = 1;
	public static final int NOTIF_GROUP_TYPE = 2;
	public static final int STATEGROUP_TYPE = 3;
	public static final int PORT_TYPE = 4;
	public static final int DEVICE_TYPE = 5;
	public static final int PAO_TYPE	= 6;
	public static final int CONTACT_TYPE = 7;
	public static final int CUSTOMER_TYPE = 8;
	public static final int LOGIN_TYPE = 9;
	public static final int HOLIDAY_SCHEDULE = 10;
	public static final int LOGIN_GRP_TYPE = 11;
	public static final int BASELINE_TYPE = 12;
	public static final int CONFIG_TYPE = 13;
	public static final int TAG_TYPE = 14;
	public static final int LMPROG_CONSTR_TYPE = 15;

   //the return types of each possible delete
   public static final byte STATUS_ALLOW = 1;
   public static final byte STATUS_CONFIRM = 2;
   public static final byte STATUS_DISALLOW = 3;
   
	/**
	 * DBDeletionWarn constructor comment.
	 */
	private DBDeletionFuncs() {
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
			theWarning.append(CR_LF + "because it is used by a device.");
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
			theWarning.append(CR_LF + "because it is utilized by the route named '"+ str + "'");
			return STATUS_DISALLOW;
		}
	
	   if( (str = com.cannontech.database.db.capcontrol.DeviceCBC.usedCapBankController(theID)) != null )
	   {
	      theWarning.delete(0, theWarning.length());
	      theWarning.append(CR_LF + "because it is utilized by the Device named '" + str + "'");
	      return STATUS_DISALLOW;
	   }
	
	   if( (str = com.cannontech.database.db.route.RepeaterRoute.isRepeaterUsed(theID)) != null )
	   {
	      theWarning.delete(0, theWarning.length());
	      theWarning.append(CR_LF + "because it is utilized by the route named '"+ str + "'");
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
	private static byte createDeleteStringForNotifGroup(int noteID) throws java.sql.SQLException
	{
		Integer theID = new Integer( noteID );
	
	   /* Some day we could consolidate all these seperate delete statements into one
	    * statement. Do this when performance becomes an issue and put it into the
	    * NotificationBase class 
	    */
		if( com.cannontech.database.data.notification.GroupNotification.hasAlarmCategory( theID ) )
		{
			theWarning.delete(0, theWarning.length());
			theWarning.append(CR_LF + "because it is used by an Alarm Category.");
			return STATUS_DISALLOW;
		}
	
		if( com.cannontech.database.data.notification.GroupNotification.hasPointAlarming( theID ) )
		{
			theWarning.delete(0, theWarning.length());
			theWarning.append(CR_LF + "because it is used by a point alarm.");
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
	private static byte createDeleteStringForContact(int contactID) throws java.sql.SQLException
	{
		Integer theID = new Integer( contactID );
	
		if( com.cannontech.database.data.customer.Contact.isPrimaryContact(
				theID, CtiUtilities.getDatabaseAlias() ) )
		{
			theWarning.delete(0, theWarning.length());
			theWarning.append(CR_LF + "because it is used as a primary contact for a customer.");
			return STATUS_DISALLOW;
		}


		if( com.cannontech.database.data.customer.Contact.isUsedInPointAlarming(
				theID, CtiUtilities.getDatabaseAlias() ) )
		{
			theWarning.delete(0, theWarning.length());
			theWarning.append(CR_LF + "because it is used as notifications for point alarms.");
			return STATUS_DISALLOW;
		}

		//this object is deleteable
		return STATUS_ALLOW;
	}
	
	private static byte createDeleteStringForBaseline(int baselineID) throws java.sql.SQLException
		{
			Integer theID = new Integer( baselineID );
	
			if( com.cannontech.database.data.point.CalculatedPoint.inUseByPoint(
					theID, CtiUtilities.getDatabaseAlias() ) )
			{
				theWarning.delete(0, theWarning.length());
				theWarning.append(CR_LF + "because it is in use by a calculated point.");
				return STATUS_DISALLOW;
			}
	
			//this object is deleteable
			return STATUS_ALLOW;
		}
		
	private static byte createDeleteStringForConfig(int conID) throws java.sql.SQLException
	{
		Integer theID = new Integer( conID );
	
		if( ConfigTwoWay.inUseByMCT(
				theID, CtiUtilities.getDatabaseAlias() ) )
		{
			theWarning.delete(0, theWarning.length());
			theWarning.append(CR_LF + "because it is in use by an MCT.");
			return STATUS_DISALLOW;
		}
	
		//this object is deleteable
		return STATUS_ALLOW;
	}
	
	private static byte createDeleteStringForLMProgConst(int constrID) throws java.sql.SQLException
	{
		if( LMProgramConstraint.inUseByProgram(constrID, CtiUtilities.getDatabaseAlias()) )
		{
			theWarning.delete(0, theWarning.length());
			theWarning.append(CR_LF + "because it is in use by a Program.");
			return STATUS_DISALLOW;
		}
	
		//this object is deleteable
		return STATUS_ALLOW;
	}
	
	private static byte createDeleteStringForTag(int tagID) throws java.sql.SQLException
	{
		theWarning.delete(0, theWarning.length());
		theWarning.append(CR_LF + "This tag and ALL references to it in the system will be removed.");
		return STATUS_CONFIRM;
	 
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
			theWarning.append(CR_LF + "because it is used by a CapControl Substation Bus.");
			return STATUS_DISALLOW;
		}
	
		if( com.cannontech.database.data.point.PointBase.hasCapBank( ptID ) )
		{
			theWarning.delete(0, theWarning.length());
			theWarning.append(CR_LF + "because it is used by a CapBank Device.");
			return STATUS_DISALLOW;
		}
	
		if( com.cannontech.database.data.point.PointBase.hasLMTrigger( ptID ) )
		{
			theWarning.delete(0, theWarning.length());
			theWarning.append(CR_LF + "because it is used by a LoadManagement Trigger.");
			return STATUS_DISALLOW;
		}
	
		if( com.cannontech.database.data.point.PointBase.hasLMGroup( ptID ) )
		{
			theWarning.delete(0, theWarning.length());
			theWarning.append(CR_LF + "because it is used by a Load Group.");
			return STATUS_DISALLOW;
		}
		
	   if( com.cannontech.database.data.point.PointBase.hasRawPointHistorys( ptID ) )
	   {
	      theWarning.delete(0, theWarning.length());
	      theWarning.append(CR_LF + "This point has archived historical data that will be lost if removed.");
	      return STATUS_CONFIRM;
	   }
	   
	   if( com.cannontech.database.data.point.PointBase.hasSystemLogEntry( ptID ) )
	   {
	      theWarning.delete(0, theWarning.length());
	      theWarning.append(CR_LF + "This point has system log data that will be lost if removed.");
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
			theWarning.append(CR_LF + "because it is used by a point.");
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
	private static byte createDeleteStringForLogin(int loginID) throws java.sql.SQLException
	{
/*
		if( loginID == UserUtils.USER_YUKON_ID ) //this id is the default
		{
			theWarning.delete(0, theWarning.length());
			theWarning.append(CR_LF + "because it is reserved for system use.");
			return STATUS_DISALLOW;
		}
*/
		if( YukonUser.isUsedByContact(loginID, CtiUtilities.getDatabaseAlias()) )
		{
			theWarning.delete(0, theWarning.length());
			theWarning.append(CR_LF + "because it is used by a contact.");
			return STATUS_DISALLOW;
		}
	
		//this login is deleteable
		return STATUS_ALLOW;
	}

	public static boolean getDeleteInfo( DBPersistent toDelete, RefInt delID, 
			RefInt delType, StringBuffer message, StringBuffer unableDel, final String nodeName )
	{
		int anID = 0, deletionType = 0;
		message.setLength(0);
		unableDel.setLength(0);
		boolean retValue = true;
		
		if (toDelete instanceof com.cannontech.database.data.point.PointBase)
		{
			message.append("Are you sure you want to Permanently Delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the point '" + nodeName + "'");
			anID = ((com.cannontech.database.data.point.PointBase) toDelete).getPoint().getPointID().intValue();
			deletionType = DBDeletionFuncs.POINT_TYPE;
		}
		else if (toDelete instanceof com.cannontech.database.data.customer.Contact)
		{
         message.append("Are you sure you want to permanently delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the contact '" + nodeName + "'");
			anID = ((com.cannontech.database.data.customer.Contact) toDelete).getContact().getContactID().intValue();
			deletionType = DBDeletionFuncs.CONTACT_TYPE;
		}		
		else if (toDelete instanceof com.cannontech.database.data.notification.GroupNotification)
		{
			message.append("Are you sure you want to permanently delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the notification group '" + nodeName + "'");
			anID = ((com.cannontech.database.data.notification.GroupNotification) toDelete).getNotificationGroup().getNotificationGroupID().intValue();
			deletionType = DBDeletionFuncs.NOTIF_GROUP_TYPE;
		}
		else if (toDelete instanceof com.cannontech.database.data.state.GroupState)
		{
			message.append("Are you sure you want to permanently delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the state group '" + nodeName + "'");
			anID = ((com.cannontech.database.data.state.GroupState) toDelete).getStateGroup().getStateGroupID().intValue();
			deletionType = DBDeletionFuncs.STATEGROUP_TYPE;
		}
		else if (toDelete instanceof com.cannontech.database.data.port.DirectPort)
		{
			message.append("Are you sure you want to permanently delete '" + ((com.cannontech.database.data.port.DirectPort) toDelete).getPortName() + "?");
			unableDel.append("You cannot delete the comm port '" + ((com.cannontech.database.data.port.DirectPort) toDelete).getPortName() + "'");
			anID = ((com.cannontech.database.data.port.DirectPort) toDelete).getCommPort().getPortID().intValue();
			deletionType = DBDeletionFuncs.PORT_TYPE;
		}
		else if (toDelete instanceof com.cannontech.database.data.device.DeviceBase)
		{
			message.append("Are you sure you want to permanently delete '" + nodeName + "?");
			unableDel.append("You cannot delete the device '" + nodeName + "'");
			anID = ((com.cannontech.database.data.device.DeviceBase) toDelete).getDevice().getDeviceID().intValue();
			deletionType = DBDeletionFuncs.DEVICE_TYPE;
		}
		else if (toDelete instanceof com.cannontech.database.data.baseline.Baseline)
		{
			anID = ((com.cannontech.database.data.baseline.Baseline) toDelete).getBaseline().getBaselineID().intValue();
			
			//this is crappy
			if(anID == com.cannontech.database.data.baseline.Baseline.IDForDefaultBaseline.intValue())
			{
				message.append("This default value was created by the system." + CR_LF + "Are you sure you want to permanently delete '" + nodeName + "'?");
				unableDel.append("You cannot delete the baseline '" + nodeName + "'");
				deletionType = DBDeletionFuncs.BASELINE_TYPE;
			}
			else
			{	
				message.append("Are you sure you want to permanently delete '" + nodeName + "'?");
				unableDel.append("You cannot delete the baseline '" + nodeName + "'");
				deletionType = DBDeletionFuncs.BASELINE_TYPE;
			}
		}
		
		else if (toDelete instanceof com.cannontech.database.data.config.ConfigTwoWay)
		{
			message.append("Are you sure you want to permanently delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the two-way config '" + nodeName + "'");
			anID = ((ConfigTwoWay) toDelete).getConfigID().intValue();
			deletionType = DBDeletionFuncs.CONFIG_TYPE;
		}
		else if (toDelete instanceof com.cannontech.database.db.tags.Tag)
		{
			message.append("Are you sure you want to permanently delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the tag '" + nodeName + "'");
			anID = ((com.cannontech.database.db.tags.Tag) toDelete).getTagID().intValue();
			deletionType = DBDeletionFuncs.TAG_TYPE;
		}				
		else if( toDelete instanceof com.cannontech.database.data.pao.YukonPAObject )
		{
			message.append("Are you sure you want to permanently delete '" + nodeName + 
								"' and all of its points?" + CR_LF + CR_LF +
								"*The delete process will take extra time if several points are present." + CR_LF +
								"*All points history will also be deleted.");

			unableDel.append("You cannot delete the point attachable object '" + nodeName + "'");
			anID = ((com.cannontech.database.data.pao.YukonPAObject) toDelete).getPAObjectID().intValue();
			deletionType = DBDeletionFuncs.PAO_TYPE;
		}
/*
		else if( toDelete instanceof com.cannontech.database.db.notification.AlarmCategory )
		{
			message.append("You can not delete alarm categories using the DatabaseEditor"); 
			unableDel.append("You cannot delete the AlarmCategory '" + nodeName + "'");
			anID = ((com.cannontech.database.db.notification.AlarmCategory) toDelete).getAlarmCategoryID().intValue();

			retValue = false;
		}
*/
		else if (toDelete instanceof com.cannontech.database.data.customer.Customer)
		{
         message.append("Are you sure you want to permanently delete '" + nodeName + "'?" + CR_LF + CR_LF +
         			"*All customer activity and settings will be deleted");

			unableDel.append("You cannot delete the customer '" + nodeName + "'");
			anID = ((com.cannontech.database.data.customer.Customer) toDelete).getCustomerID().intValue();
			deletionType = DBDeletionFuncs.CUSTOMER_TYPE;
	 	}		
		else if (toDelete instanceof com.cannontech.database.data.user.YukonUser)
		{
         message.append("Are you sure you want to permanently delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the login '" + nodeName + "'");
			anID = ((com.cannontech.database.data.user.YukonUser) toDelete).getUserID().intValue();
			deletionType = DBDeletionFuncs.LOGIN_TYPE;
		}
		else if (toDelete instanceof com.cannontech.database.data.user.YukonGroup)
		{
			message.append("Are you sure you want to permanently delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the login group '" + nodeName + "'");
			anID = ((com.cannontech.database.data.user.YukonGroup) toDelete).getGroupID().intValue();
			deletionType = DBDeletionFuncs.LOGIN_GRP_TYPE;
		}
		else if (toDelete instanceof HolidaySchedule)
		{
		 message.append("Are you sure you want to permanently delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the holiday schedule '" + nodeName + "'");
			anID = ((HolidaySchedule) toDelete).getHolidayScheduleID().intValue();
			deletionType = DBDeletionFuncs.HOLIDAY_SCHEDULE;
		}	
		else if (toDelete instanceof LMProgramConstraint)
		{
			message.append("Are you sure you want to permanently delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the Program Constraint '" + nodeName + "'");
			anID = ((LMProgramConstraint) toDelete).getConstraintID().intValue();
			deletionType = DBDeletionFuncs.LMPROG_CONSTR_TYPE;
		}		
		else
		{
			message.append("You can not delete this object using the DatabaseEditor"); 
			unableDel.append("You cannot delete object named '" + nodeName + "'");
						
			retValue = false;
		}

	
	
		delID.val = anID;
		delType.val = deletionType;
		return retValue;
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
	
			else if(type == NOTIF_GROUP_TYPE)
				return createDeleteStringForNotifGroup(anID);
	
			else if(type == STATEGROUP_TYPE)
				return createDeleteStringForStateGroup(anID);
	
			else if(type == PORT_TYPE)
				return createDeleteStringForCommPort(anID);
	
			else if(type == DEVICE_TYPE)
				return createDeleteStringForDevice(anID);
	
			else if(type == CONTACT_TYPE)
				return createDeleteStringForContact(anID);
				
			else if(type == BASELINE_TYPE)
				return createDeleteStringForBaseline(anID);
	
			else if(type == CONFIG_TYPE)
					return createDeleteStringForConfig(anID);
					
			else if(type == TAG_TYPE)
				return createDeleteStringForTag(anID);

			else if(type == LMPROG_CONSTR_TYPE)
				return createDeleteStringForLMProgConst(anID);

			else if(type == LOGIN_TYPE)
				return createDeleteStringForLogin(anID);
	
			else if( type == CUSTOMER_TYPE
						 || type == PAO_TYPE 
						 || type == HOLIDAY_SCHEDULE
						 || type == LOGIN_GRP_TYPE )
			{
				return STATUS_CONFIRM;
			}
	
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



}
