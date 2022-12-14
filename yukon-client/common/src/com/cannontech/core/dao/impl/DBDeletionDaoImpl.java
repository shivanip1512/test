package com.cannontech.core.dao.impl;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBDeleteResult;
import com.cannontech.core.dao.DBDeletionDao;
import com.cannontech.core.dao.MACScheduleDao;
import com.cannontech.database.data.config.ConfigTwoWay;
import com.cannontech.database.data.device.Rfn1200;
import com.cannontech.database.data.device.lm.LMProgramDirectBase;
import com.cannontech.database.data.holiday.HolidaySchedule;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.season.SeasonSchedule;
import com.cannontech.database.data.state.GroupState;
import com.cannontech.database.data.tou.TOUSchedule;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.baseline.Baseline;
import com.cannontech.database.db.device.lm.LMGroup;
import com.cannontech.database.db.device.lm.LMProgramConstraint;
import com.cannontech.stars.dr.program.dao.ProgramDao;

/**
 * Insert the type's description here.
 * Creation date: (6/14/2002 3:25:50 PM)
 * @author: jdayton
 */
public class DBDeletionDaoImpl implements DBDeletionDao
{
	private static final String CR_LF = System.getProperty("line.separator");
	@Autowired private MACScheduleDao macScheduleDao;
	@Autowired private ProgramDao programDao;
	
	/**
	 * DBDeletionWarn constructor comment.
	 */
	private DBDeletionDaoImpl() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/31/2001 2:36:20 PM)
	 * @return java.lang.String
	 * @param pointID int
	 */
    private byte createDeleteStringForCommPort(final DBDeleteResult dbRes)
	{
		Integer theID = new Integer( dbRes.getItemID() );
	
		if( com.cannontech.database.data.port.DirectPort.hasDevice( theID ) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is used by a device.") );
			return DBDeletionDao.STATUS_DISALLOW;
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
	private byte createDeleteStringForDevice(final DBDeleteResult dbRes) throws java.sql.SQLException
	{
		Integer theID = new Integer( dbRes.getItemID() );
		String str = null;
	
	   /* Some day we could consolidate all these seperate delete statements into one
	    * statement. Do this when performance becomes an issue and put it into the
	    * DeviceBase class
	    */
		if( (str = com.cannontech.database.data.device.DeviceBase.hasRoute(theID)) != null )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is utilized by the route named '"+ str + "'") );
			return DBDeletionDao.STATUS_DISALLOW;
		}
	
	   if( (str = com.cannontech.database.db.capcontrol.DeviceCBC.usedCapBankController(theID)) != null )
	   {
	      dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is utilized by the Device named '" + str + "'") );
	      return DBDeletionDao.STATUS_DISALLOW;
	   }
	
	   if( (str = com.cannontech.database.db.route.RepeaterRoute.isRepeaterUsed(theID)) != null )
	   {
	      dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is utilized by the route named '"+ str + "'"
	                                                         + CR_LF + "Please check the Repeater Setup tab for the route named '"+ str + "'") );
	      return DBDeletionDao.STATUS_DISALLOW;
	   }
	   
	   if( (str = LMGroup.isGroupUsed(theID)) != null )
	   {
		   dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is utilized by the LM program named '"+ str + "'") );
		   return DBDeletionDao.STATUS_DISALLOW;
	   }
	
		//this device is deleteable
		return STATUS_ALLOW;
	}

    private byte createDeleteStringForLmProgram(final DBDeleteResult dbRes) {
        int programId = dbRes.getItemID();
        if(programDao.getByProgramIds(Collections.singletonList(programId)).size() > 0) {
            dbRes.getDescriptionMsg().append(CR_LF + "because it is currently in use as a STARS assigned program."
                            + CR_LF + "Unassign it from all appliance categories and try again.");
            return STATUS_DISALLOW;
        }
        return STATUS_ALLOW;
    }

    private byte createDeleteStringForPaoType(final DBDeleteResult dbRes)
    {
        return STATUS_ALLOW;
    }

    private byte createDeleteStringForRoute(final DBDeleteResult dbRes)
	{
		Integer theID = new Integer( dbRes.getItemID() );
		String str = null;
		/* Some day we could consolidate all these seperate delete statements into one
		* statement. Do this when performance becomes an issue and put it into the
		* DeviceBase class
		*/
		if( (str = RouteBase.hasDevice(theID)) != null )
		{
			dbRes.getDescriptionMsg().append(CR_LF + "because it is utilized by the device named '"+ str + "'");
			return DBDeletionDao.STATUS_DISALLOW;
		}
		
		if( (str = RouteBase.inMacroRoute(theID)) != null )
	   	{
			dbRes.getDescriptionMsg().append(CR_LF + "If you continue, this route will be removed from \n the macro route '" + str + "'." +
				"  Delete anyway?");
			return STATUS_CONFIRM;
		}

		if( (str = RouteBase.hasLoadGroup(theID)) != null )
		{
			dbRes.getDescriptionMsg().append(CR_LF + "because it is utilized by the load group named '"+ str + "'");
			return DBDeletionDao.STATUS_DISALLOW;
		}
		
		if( (str = RouteBase.hasRepeater(theID)) != null )
		{
			dbRes.getDescriptionMsg().append(CR_LF + "because it is utilized by the repeater named '"+ str + "'"
			                                 + CR_LF + "Please check the Repeater Setup tab for this route.");
			return DBDeletionDao.STATUS_DISALLOW;
		}
		
		//this route is deleteable
		return STATUS_ALLOW;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (5/31/2001 2:36:20 PM)
	 * @return java.lang.String
	 * @param pointID int
	 */
	private byte createDeleteStringForNotifGroup(final DBDeleteResult dbRes) throws java.sql.SQLException
	{
		Integer theID = new Integer( dbRes.getItemID() );
	
	   /* Some day we could consolidate all these seperate delete statements into one
	    * statement. Do this when performance becomes an issue and put it into the
	    * NotificationBase class
	    */
		if( com.cannontech.database.data.notification.NotificationGroup.hasAlarmCategory( theID ) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is used by an Alarm Category.") );
			return DBDeletionDao.STATUS_DISALLOW;
		}
        
        if( com.cannontech.database.data.notification.NotificationGroup.hasPointAlarming( theID ) ) {
            dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is used by a point alarm.") );
            return DBDeletionDao.STATUS_DISALLOW;
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
    private byte createDeleteStringForContact(final DBDeleteResult dbRes)
	{
		Integer theID = new Integer( dbRes.getItemID() );
	
		if( com.cannontech.database.data.customer.Contact.isPrimaryContact(
				theID, CtiUtilities.getDatabaseAlias() ) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is used as a primary contact for a customer.") );
			return DBDeletionDao.STATUS_DISALLOW;
		}

		//this object is deleteable
		return STATUS_ALLOW;
	}
	
    private byte createDeleteStringForBaseline(final DBDeleteResult dbRes)
		{
			Integer theID = new Integer( dbRes.getItemID() );
	
			if( com.cannontech.database.data.point.CalculatedPoint.inUseByPoint(
					theID, CtiUtilities.getDatabaseAlias() ) )
			{
				dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is in use by a calculated point.") );
				return DBDeletionDao.STATUS_DISALLOW;
			}
	
			//this object is deleteable
			return STATUS_ALLOW;
		}
		
	private byte createDeleteStringForConfig(final DBDeleteResult dbRes) throws java.sql.SQLException
	{
		Integer theID = new Integer( dbRes.getItemID() );
	
		if( ConfigTwoWay.inUseByMCT(
				theID, CtiUtilities.getDatabaseAlias() ) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is in use by an MCT.") );
			return DBDeletionDao.STATUS_DISALLOW;
		}
	
		//this object is deleteable
		return STATUS_ALLOW;
	}
	
    private byte createDeleteStringForTOU(final DBDeleteResult dbRes)
	{
		/*Integer theID = new Integer( dbRes.getItemID() );
	
		if( TOUSchedule.inUseByDevice(
				theID, CtiUtilities.getDatabaseAlias() ) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is in use by a device.");
			return DBDeletionDao.STATUS_DISALLOW;
		}*/
	
		//this object is deleteable
		return STATUS_ALLOW;
	}
	
	private byte createDeleteStringForLMProgConst(final DBDeleteResult dbRes) throws java.sql.SQLException
	{
		if( LMProgramConstraint.inUseByProgram(dbRes.getItemID(), CtiUtilities.getDatabaseAlias()) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is in use by a Program.") );
			return DBDeletionDao.STATUS_DISALLOW;
		}
	
		//this object is deleteable
		return STATUS_ALLOW;
	}
	
	private byte createDeleteStringForSeasonSchedule(final DBDeleteResult dbRes) throws java.sql.SQLException
	{
		if( LMProgramConstraint.usesSeasonSchedule(dbRes.getItemID(), CtiUtilities.getDatabaseAlias()) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is in use by a Constraint.") );
			return DBDeletionDao.STATUS_DISALLOW;
		}
		
		if( Baseline.usesHolidaySchedule(dbRes.getItemID())) {
            dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is in use by a Baseline.") );
            return DBDeletionDao.STATUS_DISALLOW;
        }
		
		if( macScheduleDao.usesHolidaySchedule(dbRes.getItemID())) {
            dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is in use by a MAC Schedule.") );
            return DBDeletionDao.STATUS_DISALLOW;
        }

        if( SeasonSchedule.isSeasonAssignedToStrategy(dbRes.getItemID())) {
            dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is in use by a Cap Control Strategy.") );
            return DBDeletionDao.STATUS_DISALLOW;
        }
	
		//this object is deleteable
		return STATUS_ALLOW;
	}
	
	private byte createDeleteStringForHolidaySchedule(final DBDeleteResult dbRes) throws java.sql.SQLException
	{
		if( LMProgramConstraint.usesHolidaySchedule(dbRes.getItemID(), CtiUtilities.getDatabaseAlias()) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is in use by a Constraint.") );
			return DBDeletionDao.STATUS_DISALLOW;
		}
	
		//this object is deleteable
		return STATUS_ALLOW;
	}

    private byte createDeleteStringForTag(final DBDeleteResult dbRes)
	{
		dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "This tag and ALL references to it in the system will be removed.") );
		return STATUS_CONFIRM;
	 
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (5/31/2001 2:36:20 PM)
	 * @return java.lang.String
	 * @param pointID int
	 */
	private byte createDeleteStringForPoints(final DBDeleteResult dbRes) throws java.sql.SQLException
	{
		Integer ptID = new Integer( dbRes.getItemID() );
	   	      
	   /* Some day we could consolidate all these seperate delete statements into one
	    * statement. Do this when performance becomes an issue and put it into the
	    * PointBase class
	    */
		if( com.cannontech.database.data.point.PointBase.hasCapControlSubstationBus( ptID ) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is used by a CapControl Substation Bus.") );
			return DBDeletionDao.STATUS_DISALLOW;
		}
	
		if( com.cannontech.database.data.point.PointBase.hasCapBank( ptID ) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is used by a CapBank Device.") );
			return DBDeletionDao.STATUS_DISALLOW;
		}
	
		if( com.cannontech.database.data.point.PointBase.hasLMTrigger( ptID ) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is used by a LoadManagement Trigger.") );
			return DBDeletionDao.STATUS_DISALLOW;
		}
	
		if( com.cannontech.database.data.point.PointBase.hasLMGroup( ptID ) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is used by a Load Group.") );
			return DBDeletionDao.STATUS_DISALLOW;
		}
		
	   if( com.cannontech.database.data.point.PointBase.hasRawPointHistorys( ptID ) )
	   {
	      dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "This point has archived historical data.") );
	      return STATUS_CONFIRM;
	   }
	   
	   if( com.cannontech.database.data.point.PointBase.hasSystemLogEntry( ptID ) )
	   {
	      dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "This point has system log data.") );
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
	private byte createDeleteStringForStateGroup(final DBDeleteResult dbRes) throws java.sql.SQLException
	{
		Integer theID = new Integer( dbRes.getItemID() );
	
		if( GroupState.hasPoint( theID ) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is used by a point.") );
			return DBDeletionDao.STATUS_DISALLOW;
		} else if ( GroupState.hasMonitor(theID)) {
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is used by a monitor.") );
			return DBDeletionDao.STATUS_DISALLOW;
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
    private byte createDeleteStringForLogin(final DBDeleteResult dbRes)
	{
/*
		if( loginID == UserUtils.USER_YUKON_ID ) //this id is the default
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is reserved for system use.");
			return DBDeletionDao.STATUS_DISALLOW;
		}
*/
		if( YukonUser.isUsedByContact(dbRes.getItemID(), CtiUtilities.getDatabaseAlias()) )
		{
			dbRes.getDescriptionMsg().append( new StringBuffer(CR_LF + "because it is used by a contact.") );
			return DBDeletionDao.STATUS_DISALLOW;
		}
	
		//this login is deleteable
		return STATUS_ALLOW;
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.DBDeletionDao#getDeleteInfo(com.cannontech.database.db.DBPersistent, java.lang.String)
     */
    @Override
    public DBDeleteResult getDeleteInfo( DBPersistent toDelete, final String nodeName )
	{
		DBDeleteResult delRes = new DBDeleteResult();

		if (toDelete instanceof com.cannontech.database.data.point.PointBase)
		{
			delRes.getConfirmMessage().append("Are you sure you want to permanently Delete '" + nodeName +
						"'?" + CR_LF + CR_LF + "*All points history will also be deleted.");
			
			delRes.getUnableDelMsg().append("You cannot delete the point '" + nodeName + "'");
			delRes.setItemID( ((com.cannontech.database.data.point.PointBase) toDelete).getPoint().getPointID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.POINT_TYPE );
		}
		else if (toDelete instanceof com.cannontech.database.data.customer.Contact)
		{
         delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
			delRes.getUnableDelMsg().append("You cannot delete the contact '" + nodeName + "'");
			delRes.setItemID( ((com.cannontech.database.data.customer.Contact) toDelete).getContact().getContactID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.CONTACT_TYPE );
		}
		else if (toDelete instanceof com.cannontech.database.data.notification.NotificationGroup)
		{
			delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
			delRes.getUnableDelMsg().append("You cannot delete the notification group '" + nodeName + "'");
			delRes.setItemID( ((com.cannontech.database.data.notification.NotificationGroup) toDelete).getNotificationGroup().getNotificationGroupID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.NOTIF_GROUP_TYPE );
		}
		else if (toDelete instanceof com.cannontech.database.data.state.GroupState)
		{
			delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
			delRes.getUnableDelMsg().append("You cannot delete the state group '" + nodeName + "'");
			delRes.setItemID( ((com.cannontech.database.data.state.GroupState) toDelete).getStateGroup().getStateGroupID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.STATEGROUP_TYPE );
		}
		else if (toDelete instanceof com.cannontech.database.data.port.DirectPort)
		{
			delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + ((com.cannontech.database.data.port.DirectPort) toDelete).getPortName() + "?");
			delRes.getUnableDelMsg().append("You cannot delete the comm port '" + ((com.cannontech.database.data.port.DirectPort) toDelete).getPortName() + "'");
			delRes.setItemID( ((com.cannontech.database.data.port.DirectPort) toDelete).getCommPort().getPortID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.PORT_TYPE );
		}
        else if (toDelete instanceof Rfn1200) { // special comm port
            delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + ((Rfn1200) toDelete).getPAOName() + "?");
            delRes.getUnableDelMsg().append("You cannot delete the comm port '" + ((Rfn1200) toDelete).getPAOName() + "'");
            delRes.setItemID(((Rfn1200) toDelete).getCommPort().getPortID().intValue());
            // Normally this would be a DEVICE_TYPE however RFN1200 are special types of ports technically
            delRes.setDelType(DBDeletionDaoImpl.PORT_TYPE);
        }
		else if (toDelete instanceof com.cannontech.database.data.device.DeviceBase)
		{
			delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
			delRes.getUnableDelMsg().append("You cannot delete the device '" + nodeName + "'");
			delRes.setItemID( ((com.cannontech.database.data.device.DeviceBase) toDelete).getDevice().getDeviceID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.DEVICE_TYPE );
		}
		else if (toDelete instanceof com.cannontech.database.data.route.RouteBase)
		{
			delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
			delRes.getUnableDelMsg().append("You cannot delete the route '" + nodeName + "'");
			delRes.setItemID( ((com.cannontech.database.data.route.RouteBase) toDelete).getRouteID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.ROUTE_TYPE );
		}
		else if (toDelete instanceof com.cannontech.database.data.baseline.Baseline)
		{
			delRes.setItemID( ((com.cannontech.database.data.baseline.Baseline) toDelete).getBaseline().getBaselineID().intValue() );
			
			//this is crappy
			if( delRes.getItemID() == com.cannontech.database.data.baseline.Baseline.IDForDefaultBaseline.intValue())
			{
				delRes.getConfirmMessage().append("This default value was created by the system." + CR_LF + "Are you sure you want to permanently delete '" + nodeName + "'?");
				delRes.getUnableDelMsg().append("You cannot delete the baseline '" + nodeName + "'");
				delRes.setDelType( DBDeletionDaoImpl.BASELINE_TYPE );
			}
			else
			{
				delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
				delRes.getUnableDelMsg().append("You cannot delete the baseline '" + nodeName + "'");
				delRes.setDelType( DBDeletionDaoImpl.BASELINE_TYPE );
			}
		}
		
		else if (toDelete instanceof com.cannontech.database.data.config.ConfigTwoWay)
		{
			delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
			delRes.getUnableDelMsg().append("You cannot delete the two-way config '" + nodeName + "'");
			delRes.setItemID( ((ConfigTwoWay) toDelete).getConfigID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.CONFIG_TYPE );
		}
		
		else if (toDelete instanceof TOUSchedule)
		{
			delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
			delRes.getUnableDelMsg().append("You cannot delete the TOU Schedule '" + nodeName + "'");
			delRes.setItemID( ((TOUSchedule) toDelete).getScheduleID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.TOU_TYPE );
		}
		
		else if (toDelete instanceof com.cannontech.database.db.tags.Tag)
		{
			delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
			delRes.getUnableDelMsg().append("You cannot delete the tag '" + nodeName + "'");
			delRes.setItemID( ((com.cannontech.database.db.tags.Tag) toDelete).getTagID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.TAG_TYPE );
		}
		else if (toDelete instanceof LMProgramDirectBase) {
		    delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName +
		            "' and all of its points?" + CR_LF + CR_LF +
		            "*The delete process will take extra time if several points are present." + CR_LF +
		            "*All points history will also be deleted.");
		    
		    delRes.getUnableDelMsg().append("You cannot delete the load management program '" + nodeName + "'");
		    delRes.setItemID( ((com.cannontech.database.data.pao.YukonPAObject) toDelete).getPAObjectID().intValue() );
		    delRes.setDelType( DBDeletionDaoImpl.LM_PROGRAM);
		}
		else if( toDelete instanceof com.cannontech.database.data.pao.YukonPAObject )
		{
			delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName +
								"' and all of its points?" + CR_LF + CR_LF +
								"*The delete process will take extra time if several points are present." + CR_LF +
								"*All points history will also be deleted.");

			delRes.getUnableDelMsg().append("You cannot delete the point attachable object '" + nodeName + "'");
			delRes.setItemID( ((com.cannontech.database.data.pao.YukonPAObject) toDelete).getPAObjectID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.PAO_TYPE );
		}
/*
		else if( toDelete instanceof com.cannontech.database.db.notification.AlarmCategory )
		{
			delRes.getConfirmMessage().append("You can not delete alarm categories using the DatabaseEditor");
			delRes.getUnableDelMsg().append("You cannot delete the AlarmCategory '" + nodeName + "'");
			delRes.setItemID( ((com.cannontech.database.db.notification.AlarmCategory) toDelete).getAlarmCategoryID().intValue() );

			retValue = false;
		}
*/
		else if (toDelete instanceof com.cannontech.database.data.customer.Customer)
		{
         	delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?" + CR_LF + CR_LF +
         			"*All customer activity and settings will be deleted");

			delRes.getUnableDelMsg().append("You cannot delete the customer '" + nodeName + "'");
			delRes.setItemID( ((com.cannontech.database.data.customer.Customer) toDelete).getCustomerID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.CUSTOMER_TYPE );
		}
		else if (toDelete instanceof com.cannontech.database.data.user.YukonUser)
		{
         	delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
			delRes.getUnableDelMsg().append("You cannot delete the login '" + nodeName + "'");
			delRes.setItemID( ((com.cannontech.database.data.user.YukonUser) toDelete).getUserID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.LOGIN_TYPE );
		}
        else if (toDelete instanceof com.cannontech.database.data.user.UserGroup)
        {
            delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
            delRes.getUnableDelMsg().append("You cannot delete the user group '" + nodeName + "'");
            delRes.setItemID(((com.cannontech.database.data.user.UserGroup) toDelete).getUserGroup().getUserGroupId());
            delRes.setDelType( DBDeletionDaoImpl.USER_GROUP_TYPE );
        }
		else if (toDelete instanceof com.cannontech.database.data.user.YukonGroup)
		{
			delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
			delRes.getUnableDelMsg().append("You cannot delete the role group '" + nodeName + "'");
			delRes.setItemID( ((com.cannontech.database.data.user.YukonGroup) toDelete).getGroupID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.LOGIN_GRP_TYPE );
		}
		else if (toDelete instanceof HolidaySchedule)
		{
		 	delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
			delRes.getUnableDelMsg().append("You cannot delete the holiday schedule '" + nodeName + "'");
			delRes.setItemID( ((HolidaySchedule) toDelete).getHolidayScheduleID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.HOLIDAY_SCHEDULE );
		}
		else if (toDelete instanceof SeasonSchedule)
		{
		 	delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
			delRes.getUnableDelMsg().append("You cannot delete the season schedule '" + nodeName + "'");
			delRes.setItemID( ((SeasonSchedule) toDelete).getScheduleID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.SEASON_SCHEDULE );
		}
		else if (toDelete instanceof LMProgramConstraint)
		{
			delRes.getConfirmMessage().append("Are you sure you want to permanently delete '" + nodeName + "'?");
			delRes.getUnableDelMsg().append("You cannot delete the Program Constraint '" + nodeName + "'");
			delRes.setItemID( ((LMProgramConstraint) toDelete).getConstraintID().intValue() );
			delRes.setDelType( DBDeletionDaoImpl.LMPROG_CONSTR_TYPE );
		}
		else
		{
			delRes.getConfirmMessage().append("You can not delete this object using the DatabaseEditor");
			delRes.getUnableDelMsg().append("You cannot delete object named '" + nodeName + "'");
						
			delRes.setDeletable( false );
		}

		
		return delRes;
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.DBDeletionDao#deletionAttempted(com.cannontech.core.dao.DBDeleteResult)
     */
    @Override
    public byte deletionAttempted( final DBDeleteResult dbRes ) throws java.sql.SQLException
	{
		if (dbRes.getDelType() == POINT_TYPE) {
			return createDeleteStringForPoints(dbRes);
		}
		else if(dbRes.getDelType() == NOTIF_GROUP_TYPE) {
			return createDeleteStringForNotifGroup(dbRes);
		}
		else if(dbRes.getDelType() == STATEGROUP_TYPE) {
			return createDeleteStringForStateGroup(dbRes);
		}
		else if(dbRes.getDelType() == PORT_TYPE) {
			return createDeleteStringForCommPort(dbRes);
		}
		else if(dbRes.getDelType() == DEVICE_TYPE) {
			return createDeleteStringForDevice(dbRes);
		}
		else if(dbRes.getDelType() == ROUTE_TYPE) {
			return createDeleteStringForRoute(dbRes);
		}
		else if(dbRes.getDelType() == CONTACT_TYPE) {
			return createDeleteStringForContact(dbRes);
		}
		else if(dbRes.getDelType() == BASELINE_TYPE) {
			return createDeleteStringForBaseline(dbRes);
		}
		else if(dbRes.getDelType() == CONFIG_TYPE) {
				return createDeleteStringForConfig(dbRes);
		}
		else if(dbRes.getDelType() == TOU_TYPE) {
			return createDeleteStringForTOU(dbRes);
		}
		else if(dbRes.getDelType() == TAG_TYPE) {
			return createDeleteStringForTag(dbRes);
		}
		else if(dbRes.getDelType() == LMPROG_CONSTR_TYPE) {
			return createDeleteStringForLMProgConst(dbRes);
		}
		else if(dbRes.getDelType() == LOGIN_TYPE) {
			return createDeleteStringForLogin(dbRes);
		}
		else if(dbRes.getDelType() == SEASON_SCHEDULE) {
			return createDeleteStringForSeasonSchedule(dbRes);
		}
		else if(dbRes.getDelType() == HOLIDAY_SCHEDULE)	{
			return createDeleteStringForHolidaySchedule(dbRes);
		}
		else if(dbRes.getDelType() == LM_PROGRAM) {
		    return createDeleteStringForLmProgram(dbRes);
		}
        else if(dbRes.getDelType() == PAO_TYPE) {
            return createDeleteStringForPaoType(dbRes);
        }
		else if( dbRes.getDelType() == CUSTOMER_TYPE || dbRes.getDelType() == LOGIN_GRP_TYPE || dbRes.getDelType() == USER_GROUP_TYPE) {
			return STATUS_CONFIRM;
		}
		else {
			return DBDeletionDao.STATUS_DISALLOW;
		}
	}

}
