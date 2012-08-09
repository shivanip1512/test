package com.cannontech.database.data.lite;

import java.util.GregorianCalendar;

import org.joda.time.Instant;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.command.DeviceTypeCommand;
import com.cannontech.database.data.customer.CICustomerBase;
import com.cannontech.database.data.customer.Customer;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase;
import com.cannontech.database.data.notification.NotificationGroup;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.user.UserGroup;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.command.Command;
import com.cannontech.database.db.company.SettlementConfig;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.database.db.user.YukonGroup;

/**
 * This type was created in VisualAge.
 */
public final class LiteFactory {
/**
 * This method was created in VisualAge.
 */
public final static com.cannontech.database.db.DBPersistent createDBPersistent(LiteBase liteObject) {

	com.cannontech.database.db.DBPersistent returnObject = null;

	int liteType = liteObject.getLiteType();
	
	switch( liteType )
	{
		case LiteTypes.YUKON_PAOBJECT:
			returnObject = com.cannontech.database.data.pao.PAOFactory.createPAObject( (LiteYukonPAObject)liteObject );

			if( returnObject instanceof com.cannontech.database.data.device.DeviceBase )
			{
				((DeviceBase)returnObject).setDeviceID(new Integer( ((LiteYukonPAObject)liteObject).getYukonID()) );
				((DeviceBase)returnObject).setPAOName( ((LiteYukonPAObject)liteObject).getPaoName());

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
			else if( returnObject instanceof com.cannontech.database.data.capcontrol.CapControlYukonPAOBase )
			{	
				((com.cannontech.database.data.capcontrol.CapControlYukonPAOBase)returnObject).setCapControlPAOID( new Integer(((LiteYukonPAObject)liteObject).getYukonID()) );
				((com.cannontech.database.data.capcontrol.CapControlYukonPAOBase)returnObject).setName( ((LiteYukonPAObject)liteObject).getPaoName() );
			}
			break;
			
		case LiteTypes.CUSTOMER_CI:
			returnObject = new CICustomerBase(); 
			((CICustomerBase)returnObject).setCustomerID( new Integer(((LiteCICustomer)liteObject).getCustomerID()) );
			((CICustomerBase)returnObject).getCiCustomerBase().setCompanyName( ((LiteCICustomer)liteObject).getCompanyName() );
			((CICustomerBase)returnObject).getCiCustomerBase().setCurtailAmount( new Double(((LiteCICustomer)liteObject).getCurtailAmount()) );
			((CICustomerBase)returnObject).getCiCustomerBase().setCustDmdLevel( new Double(((LiteCICustomer)liteObject).getDemandLevel()) );
			((CICustomerBase)returnObject).getCiCustomerBase().setMainAddressID( new Integer(((LiteCICustomer)liteObject).getMainAddressID()) );
			((CICustomerBase)returnObject).getCustomer().setPrimaryContactID( new Integer(((LiteCICustomer)liteObject).getPrimaryContactID()) );
			((CICustomerBase)returnObject).getCustomer().setTimeZone( ((LiteCICustomer)liteObject).getTimeZone() );
			((CICustomerBase)returnObject).getCiCustomerBase().setCICustType( ((LiteCICustomer)liteObject).getCICustType() );
			break;
		case LiteTypes.CUSTOMER:
			returnObject = new Customer(); 
			((Customer)returnObject).setCustomerID( new Integer(((LiteCustomer)liteObject).getCustomerID()) );
			((Customer)returnObject).getCustomer().setPrimaryContactID( new Integer(((LiteCustomer)liteObject).getPrimaryContactID()) );
			((Customer)returnObject).getCustomer().setTimeZone( ((LiteCustomer)liteObject).getTimeZone() );
			((Customer)returnObject).getCustomer().setAltTrackingNumber( ((LiteCustomer)liteObject).getAltTrackingNumber() );
			((Customer)returnObject).getCustomer().setCustomerNumber( ((LiteCustomer)liteObject).getCustomerNumber() );
			((Customer)returnObject).getCustomer().setCustomerTypeID( new Integer(((LiteCustomer)liteObject).getCustomerTypeID() ));
			((Customer)returnObject).getCustomer().setRateScheduleID( new Integer(((LiteCustomer)liteObject).getRateScheduleID() ));
			((Customer)returnObject).getCustomer().setTemperatureUnit( ((LiteCustomer)liteObject).getTemperatureUnit() );
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
			((com.cannontech.database.data.graph.GraphDefinition)returnObject).getGraphDefinition().setGraphDefinitionID(new Integer(((LiteGraphDefinition)liteObject).getGraphDefinitionID()));
			((com.cannontech.database.data.graph.GraphDefinition)returnObject).getGraphDefinition().setName(((LiteGraphDefinition)liteObject).getName());
			break;
		case LiteTypes.GRAPH_CUSTOMER_LIST:
			returnObject = new com.cannontech.database.db.graph.GraphCustomerList();
			((com.cannontech.database.db.graph.GraphCustomerList)returnObject).setCustomerID(new Integer(((LiteGraphCustomerList)liteObject).getCustomerID()));
			((com.cannontech.database.db.graph.GraphCustomerList)returnObject).setGraphDefinitionID(new Integer(((LiteGraphCustomerList)liteObject).getGraphDefinitionID()));
			break;
		case LiteTypes.NOTIFICATION_GROUP:
			returnObject = new com.cannontech.database.data.notification.NotificationGroup();
			((com.cannontech.database.data.notification.NotificationGroup)returnObject).setNotificatoinGroupID(new Integer(((LiteNotificationGroup)liteObject).getNotificationGroupID()));
			((com.cannontech.database.data.notification.NotificationGroup)returnObject).getNotificationGroup().setGroupName(((LiteNotificationGroup)liteObject).getNotificationGroupName());
			break;
		case LiteTypes.ALARM_CATEGORIES:
			returnObject = new com.cannontech.database.db.notification.AlarmCategory();
			((com.cannontech.database.db.notification.AlarmCategory)returnObject).setAlarmCategoryID(new Integer(((LiteAlarmCategory)liteObject).getAlarmStateID()));
			((com.cannontech.database.db.notification.AlarmCategory)returnObject).setCategoryName(((LiteAlarmCategory)liteObject).getCategoryName());
			((com.cannontech.database.db.notification.AlarmCategory)returnObject).setNotificationGroupID( new Integer( ((LiteAlarmCategory)liteObject).getNotificationGroupID()) );
			break;
		case LiteTypes.CONTACT:
			returnObject = new com.cannontech.database.data.customer.Contact();
			((com.cannontech.database.data.customer.Contact)returnObject).setContactID( new Integer(((LiteContact)liteObject).getContactID()) );
			((com.cannontech.database.data.customer.Contact)returnObject).getContact().setContFirstName( ((LiteContact)liteObject).getContFirstName() );
			((com.cannontech.database.data.customer.Contact)returnObject).getContact().setContLastName( ((LiteContact)liteObject).getContLastName() );
			((com.cannontech.database.data.customer.Contact)returnObject).getContact().setLogInID( new Integer( ((LiteContact)liteObject).getLoginID() ) );
			((com.cannontech.database.data.customer.Contact)returnObject).getContact().setAddressID( new Integer(((LiteContact)liteObject).getAddressID()) );				
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
		case LiteTypes.SEASON_SCHEDULE:
			returnObject = new com.cannontech.database.data.season.SeasonSchedule();
			((com.cannontech.database.data.season.SeasonSchedule)returnObject).setScheduleID(new Integer(((LiteSeasonSchedule)liteObject).getScheduleID()) );
			((com.cannontech.database.data.season.SeasonSchedule)returnObject).setScheduleName( ((LiteSeasonSchedule)liteObject).getScheduleName() );
			break;
		case LiteTypes.TOU_SCHEDULE:
			returnObject = new com.cannontech.database.data.tou.TOUSchedule();
			((com.cannontech.database.data.tou.TOUSchedule)returnObject).setScheduleID(new Integer(((LiteTOUSchedule)liteObject).getScheduleID()) );
			((com.cannontech.database.data.tou.TOUSchedule)returnObject).setScheduleName( ((LiteTOUSchedule)liteObject).getScheduleName() );
			break;
		case LiteTypes.BASELINE:
			returnObject = new com.cannontech.database.data.baseline.Baseline();
			((com.cannontech.database.data.baseline.Baseline)returnObject).setBaselineID(new Integer(((LiteBaseline)liteObject).getBaselineID()) );
			((com.cannontech.database.data.baseline.Baseline)returnObject).setBaselineName( ((LiteBaseline)liteObject).getBaselineName() );
			break;
		case LiteTypes.CONFIG:
			returnObject = new com.cannontech.database.data.config.ConfigTwoWay();
			((com.cannontech.database.data.config.ConfigTwoWay)returnObject).setConfigID(new Integer(((LiteConfig)liteObject).getConfigID()) );
			((com.cannontech.database.data.config.ConfigTwoWay)returnObject).setConfigName( ((LiteConfig)liteObject).getConfigName() );
			break;
		case LiteTypes.LMCONSTRAINT:
			returnObject = new com.cannontech.database.db.device.lm.LMProgramConstraint();
			((com.cannontech.database.db.device.lm.LMProgramConstraint)returnObject).setConstraintID(new Integer(((LiteLMConstraint)liteObject).getConstraintID()));
			((com.cannontech.database.db.device.lm.LMProgramConstraint)returnObject).setConstraintName(((LiteLMConstraint)liteObject).getConstraintName());
			break;
		case LiteTypes.GEAR:
			String method = ((LiteGear)liteObject).getGearType();
			returnObject = GearControlMethod.getGearControlMethod(method).createNewGear();
			((LMProgramDirectGear)returnObject).setGearID(new Integer(liteObject.getLiteID()));
			((LMProgramDirectGear)returnObject).setGearName(((LiteGear)liteObject).getGearName());
			break;
		/*case LiteTypes.LMSCENARIO:
			returnObject = new com.cannontech.database.db.device.lm.LMControlScenarioProgram();
			((com.cannontech.database.db.device.lm.LMControlScenarioProgram)returnObject).setScenarioID(new Integer(((LiteLMScenario)liteObject).getScenarioID()));
			((com.cannontech.database.db.device.lm.LMControlScenarioProgram)returnObject).setScenarioName(((LiteLMScenario)liteObject).getScenarioName());
			break;*/
		case LiteTypes.TAG:
			returnObject = new com.cannontech.database.db.tags.Tag();
			((com.cannontech.database.db.tags.Tag)returnObject).setTagID(new Integer(((LiteTag)liteObject).getTagID()) );
			((com.cannontech.database.db.tags.Tag)returnObject).setTagName( ((LiteTag)liteObject).getTagName() );
			((com.cannontech.database.db.tags.Tag)returnObject).setTagLevel( new Integer(((LiteTag)liteObject).getTagLevel()));
			Character inhibit = new Character('N');
			if(((LiteTag)liteObject).isInhibit())
				inhibit = new Character('Y');
			((com.cannontech.database.db.tags.Tag)returnObject).setInhibit( inhibit );
			((com.cannontech.database.db.tags.Tag)returnObject).setColorID(new Integer(((LiteTag)liteObject).getColorID()) );
			((com.cannontech.database.db.tags.Tag)returnObject).setImageID(new Integer(((LiteTag)liteObject).getImageID()) );
			break;							
     case LiteTypes.STATE_IMAGE:
        returnObject = new com.cannontech.database.db.state.YukonImage();
        ((com.cannontech.database.db.state.YukonImage)returnObject).setImageID(new Integer(((LiteYukonImage)liteObject).getImageID()) );
        ((com.cannontech.database.db.state.YukonImage)returnObject).setImageValue( ((LiteYukonImage)liteObject).getImageValue() );
        ((com.cannontech.database.db.state.YukonImage)returnObject).setImageName( ((LiteYukonImage)liteObject).getImageName() );
        ((com.cannontech.database.db.state.YukonImage)returnObject).setImageCategory( ((LiteYukonImage)liteObject).getImageCategory() );
        break;            
     
		case LiteTypes.YUKON_USER:
			returnObject = new com.cannontech.database.data.user.YukonUser();
	 		((com.cannontech.database.data.user.YukonUser)returnObject).setUserID( new Integer(((LiteYukonUser)liteObject).getUserID()) );
	 		((com.cannontech.database.data.user.YukonUser)returnObject).getYukonUser().setUsername( ((LiteYukonUser)liteObject).getUsername() );
	 		((com.cannontech.database.data.user.YukonUser)returnObject).getYukonUser().setAuthType( ((LiteYukonUser)liteObject).getAuthType() );
	 		break;
		case LiteTypes.YUKON_GROUP:
			returnObject = new com.cannontech.database.data.user.YukonGroup();
			((com.cannontech.database.data.user.YukonGroup)returnObject).setGroupID( new Integer(((LiteYukonGroup)liteObject).getGroupID()) );
			((com.cannontech.database.data.user.YukonGroup)returnObject).getYukonGroup().setGroupName( ((LiteYukonGroup)liteObject).getGroupName() );
			((com.cannontech.database.data.user.YukonGroup)returnObject).getYukonGroup().setGroupDescription( ((LiteYukonGroup)liteObject).getGroupDescription() );
			break;
        case LiteTypes.USER_GROUP:
            returnObject = new com.cannontech.database.data.user.UserGroup();
            LiteUserGroup liteUserGroup = (LiteUserGroup)liteObject;
            ((UserGroup)returnObject).getLiteUserGroup().setUserGroupId(liteUserGroup.getUserGroupId());
            ((UserGroup)returnObject).getLiteUserGroup().setUserGroupName(liteUserGroup.getUserGroupName());
            ((UserGroup)returnObject).getLiteUserGroup().setUserGroupDescription(liteUserGroup.getUserGroupDescription());
            break;
		case LiteTypes.DEVICE_TYPE_COMMAND:
			returnObject = new DeviceTypeCommand();
			((DeviceTypeCommand)returnObject).setDeviceCommandID(new Integer(((LiteDeviceTypeCommand)liteObject).getDeviceCommandID()));
			((DeviceTypeCommand)returnObject).setCommandID(new Integer(((LiteDeviceTypeCommand)liteObject).getCommandID()));				
			((DeviceTypeCommand)returnObject).setDeviceType(((LiteDeviceTypeCommand)liteObject).getDeviceType());
			((DeviceTypeCommand)returnObject).setDisplayOrder(new Integer(((LiteDeviceTypeCommand)liteObject).getDisplayOrder()));
			((DeviceTypeCommand)returnObject).setVisibleFlag(new Character(((LiteDeviceTypeCommand)liteObject).getVisibleFlag()));
			break;
		case LiteTypes.COMMAND:
			returnObject = new Command();
			((Command)returnObject).setCommandID(new Integer(((LiteCommand)liteObject).getCommandID()));				
			((Command)returnObject).setCommand(((LiteCommand)liteObject).getCommand());
			((Command)returnObject).setLabel(((LiteCommand)liteObject).getLabel());
			((Command)returnObject).setCategory(((LiteCommand)liteObject).getCategory());
			break;
/* TODO add SystemRole,YukonRoleProperty */		 		
	 	case LiteTypes.ENERGY_COMPANY:
	 		returnObject = new com.cannontech.database.data.company.EnergyCompanyBase();
	 		((com.cannontech.database.data.company.EnergyCompanyBase)returnObject).setEnergyCompanyID(new Integer(((LiteEnergyCompany)liteObject).getLiteID()));
	 		((com.cannontech.database.data.company.EnergyCompanyBase)returnObject).setName( ((LiteEnergyCompany)liteObject).getName());
	 		break;
/* TODO: add LiteTypes.TAG? */
		case LiteTypes.RAWPOINTHISTORY:
			returnObject = new RawPointHistory();
			((RawPointHistory)returnObject).setChangeID(new Integer(((LiteRawPointHistory)liteObject).getLiteID()));
			((RawPointHistory)returnObject).setPointID(new Integer(((LiteRawPointHistory)liteObject).getPointID()));
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTimeInMillis( ((LiteRawPointHistory)liteObject).getTimeStamp());
			((RawPointHistory)returnObject).setTimeStamp(cal);
			((RawPointHistory)returnObject).setValue(new Double(((LiteRawPointHistory)liteObject).getValue()));
			((RawPointHistory)returnObject).setQuality(new Integer(((LiteRawPointHistory)liteObject).getQuality()));					 		
			break;
		case LiteTypes.SETTLEMENT:
			returnObject = new SettlementConfig();
			((SettlementConfig)returnObject).setConfigID(new Integer(((LiteSettlementConfig)liteObject).getConfigID()));				
			((SettlementConfig)returnObject).setFieldName(((LiteSettlementConfig)liteObject).getFieldName());
			((SettlementConfig)returnObject).setFieldValue(((LiteSettlementConfig)liteObject).getFieldValue());
			((SettlementConfig)returnObject).setDescription(((LiteSettlementConfig)liteObject).getDescription());
			break;
		default:
			returnObject = null;
			break;
	}

	if( returnObject == null )
		throw new IllegalArgumentException("*** Unable to create a DBPersistant object from a givent Lite object in: createDBPersistent(LiteBase liteObject)");
	else
		return returnObject;
}
/**
 * This method was created in VisualAge.
 */
public final static LiteBase createLite(com.cannontech.database.db.DBPersistent val) 
{ 
	LiteBase returnLite = null;

	if( val instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
	{
		returnLite = createLite( ((com.cannontech.database.db.DBPersistent)(((com.cannontech.database.data.multi.SmartMultiDBPersistent)val).getOwnerDBPersistent())) );
	}
	else if( val instanceof com.cannontech.database.data.multi.MultiDBPersistent )
	{
		returnLite = createLite( ((com.cannontech.database.db.DBPersistent)(((com.cannontech.database.data.multi.MultiDBPersistent)val).getDBPersistentVector().get(0))) );
	}
	else if( val instanceof CICustomerBase )
	{
		returnLite = new LiteCICustomer(
				((CICustomerBase)val).getCustomerID().intValue(),
				((CICustomerBase)val).getCiCustomerBase().getCompanyName() );
		((LiteCICustomer)returnLite).setCICustType(((CICustomerBase)val).getCiCustomerBase().getCICustType().intValue());
		((LiteCICustomer)returnLite).setCurtailAmount(((CICustomerBase)val).getCiCustomerBase().getCurtailAmount().doubleValue());
		((LiteCICustomer)returnLite).setDemandLevel(((CICustomerBase)val).getCiCustomerBase().getCustDmdLevel().doubleValue());
		
	}
	else if( val instanceof Customer)
	{
		returnLite = new LiteCustomer(
				((Customer)val).getCustomerID().intValue());
	}
	else if( val instanceof com.cannontech.database.data.point.PointBase )
	{
	    if ( ((com.cannontech.database.data.point.PointBase)val).getPoint().getPaoID() == null || 
	         ((com.cannontech.database.data.point.PointBase)val).getPoint().getPaoID() == 0 ) {
    		returnLite = new LitePoint(
    			((com.cannontech.database.data.point.PointBase)val).getPoint().getPointID().intValue(),
    			((com.cannontech.database.data.point.PointBase)val).getPoint().getPointName() );
	    } 
	    else {
	        PointBase pointBase = (PointBase)val;
	        int pointType = PointType.getForString(pointBase.getPoint().getPointType()).getPointTypeId();
    	    returnLite = new LitePoint(
    	                               pointBase.getPoint().getPointID().intValue(),
    	                               pointBase.getPoint().getPointName(),
    	                               pointType,
    	                               pointBase.getPoint().getPaoID(),
    	                               pointBase.getPoint().getPointOffset(),
    	                               pointBase.getPoint().getStateGroupID());
	    }
			
	}
	else if( val instanceof com.cannontech.database.data.state.GroupState )
	{
		returnLite = new LiteStateGroup(
			((com.cannontech.database.data.state.GroupState)val).getStateGroup().getStateGroupID().intValue(),
			((com.cannontech.database.data.state.GroupState)val).getStateGroup().getName() );
			
	}
	else if( val instanceof com.cannontech.database.data.holiday.HolidaySchedule )
	{
		returnLite = new LiteHolidaySchedule(
			((com.cannontech.database.data.holiday.HolidaySchedule)val).getHolidayScheduleID().intValue(),
			((com.cannontech.database.data.holiday.HolidaySchedule)val).getHolidayScheduleName() );
			
	}
	else if( val instanceof com.cannontech.database.data.season.SeasonSchedule )
	{
		returnLite = new LiteSeasonSchedule(
			((com.cannontech.database.data.season.SeasonSchedule)val).getScheduleID().intValue(),
			((com.cannontech.database.data.season.SeasonSchedule)val).getScheduleName() );
			
	}	
	else if( val instanceof com.cannontech.database.data.tou.TOUSchedule )
	{
		returnLite = new LiteTOUSchedule(
			((com.cannontech.database.data.tou.TOUSchedule)val).getScheduleID().intValue(),
			((com.cannontech.database.data.tou.TOUSchedule)val).getScheduleName(),
			((com.cannontech.database.data.tou.TOUSchedule)val).getDefaultRate() );
	}
	else if( val instanceof com.cannontech.database.data.baseline.Baseline )
	{
		returnLite = new LiteBaseline(
			((com.cannontech.database.data.baseline.Baseline)val).getBaselineID().intValue(),
			((com.cannontech.database.data.baseline.Baseline)val).getBaselineName() );
			
	}
	else if( val instanceof com.cannontech.database.data.config.ConfigTwoWay )
	{
		returnLite = new LiteConfig(
			((com.cannontech.database.data.config.ConfigTwoWay)val).getConfigID().intValue(),
			((com.cannontech.database.data.config.ConfigTwoWay)val).getConfigName() );
			
	}
	else if( val instanceof com.cannontech.database.db.device.lm.LMProgramConstraint )
	{
		returnLite = new LiteLMConstraint(
			((com.cannontech.database.db.device.lm.LMProgramConstraint)val).getConstraintID().intValue(),
			((com.cannontech.database.db.device.lm.LMProgramConstraint)val).getConstraintName() );
	}
	/*else if( val instanceof com.cannontech.database.db.device.lm.LMControlScenarioProgram )
	{
		returnLite = new LiteLMScenario(
			((com.cannontech.database.db.device.lm.LMControlScenarioProgram)val).getScenarioID().intValue(),
			((com.cannontech.database.db.device.lm.LMControlScenarioProgram)val).getScenarioName() );
	}*/
	else if( val instanceof com.cannontech.database.db.tags.Tag )
	{
		boolean temp = (((com.cannontech.database.db.tags.Tag)val).getInhibit().compareTo(new Character('Y')) == 0);
		
		returnLite = new LiteTag(
			((com.cannontech.database.db.tags.Tag)val).getTagID().intValue(),
			((com.cannontech.database.db.tags.Tag)val).getTagName(),
			((com.cannontech.database.db.tags.Tag)val).getTagLevel().intValue(),
			temp,
			((com.cannontech.database.db.tags.Tag)val).getColorID().intValue(),
			((com.cannontech.database.db.tags.Tag)val).getImageID().intValue());
			
	}
	else if( val instanceof com.cannontech.database.data.graph.GraphDefinition )
	{
		returnLite = new LiteGraphDefinition( 
				((com.cannontech.database.data.graph.GraphDefinition)val).getGraphDefinition().getGraphDefinitionID().intValue(),
				((com.cannontech.database.data.graph.GraphDefinition)val).getGraphDefinition().getName()	);
	}
	else if( val instanceof com.cannontech.database.data.notification.NotificationGroup )
	{
		LiteNotificationGroup lGrp = new LiteNotificationGroup( 
				((NotificationGroup)val).getNotificationGroup().getNotificationGroupID().intValue(),
				((NotificationGroup)val).getNotificationGroup().getGroupName() );
				
		lGrp.setDisabled( ((NotificationGroup)val).getNotificationGroup().getDisableFlag().equalsIgnoreCase("Y") );
				
		returnLite = lGrp;
	}		
	else if( val instanceof com.cannontech.database.db.notification.AlarmCategory )
	{
		returnLite = new LiteAlarmCategory( 
				((com.cannontech.database.db.notification.AlarmCategory)val).getAlarmCategoryID().intValue(),
				((com.cannontech.database.db.notification.AlarmCategory)val).getCategoryName() );

	}
	else if( val instanceof com.cannontech.database.data.customer.Contact )
	{
		returnLite = new LiteContact(
				((com.cannontech.database.data.customer.Contact)val).getContact().getContactID().intValue(),
				((com.cannontech.database.data.customer.Contact)val).getContact().getContFirstName(),
				((com.cannontech.database.data.customer.Contact)val).getContact().getContLastName() );

	}
	else if (val instanceof DeviceMeterGroupBase) {
        YukonPao yukonPao = DaoFactory.getPaoDao().getYukonPao(((DeviceMeterGroupBase)val).getDeviceMeterGroup().getDeviceID().intValue());
		returnLite = new LiteDeviceMeterNumber(
				((DeviceMeterGroupBase)val).getDeviceMeterGroup().getDeviceID().intValue(),
				((DeviceMeterGroupBase)val).getDeviceMeterGroup().getMeterNumber(),
				yukonPao.getPaoIdentifier().getPaoType());

	} else if( val instanceof YukonUser ) {
	       com.cannontech.database.db.user.YukonUser user = ((YukonUser)val).getYukonUser();

	        returnLite = new LiteYukonUser(user.getUserID(), user.getUsername(), user.getLoginStatus(), user.getAuthType(), 
	                                       new Instant(user.getLastChangedDate()), user.isForceReset(), user.getUserGroupId());

	} else if( val instanceof YukonGroup ) {

	    returnLite = new LiteYukonGroup(
			((YukonGroup)val).getGroupID().intValue(),
			((YukonGroup)val).getGroupName() );
		
		((LiteYukonGroup)returnLite).setGroupDescription( ((YukonGroup)val).getGroupDescription() );
	}	
	else if( val instanceof com.cannontech.database.data.user.YukonGroup )
	{
		returnLite = new LiteYukonGroup(
			((com.cannontech.database.data.user.YukonGroup)val).getGroupID().intValue(),
			((com.cannontech.database.data.user.YukonGroup)val).getYukonGroup().getGroupName() );
		
		((LiteYukonGroup)returnLite).setGroupDescription( 
			((com.cannontech.database.data.user.YukonGroup)val).getYukonGroup().getGroupDescription() );
	}	


	/* TODO add SystemRole,YukonRoleProperty */
	else if( val instanceof com.cannontech.database.data.pao.YukonPAObject )
	{
		YukonPAObject yukonPAObject = ((com.cannontech.database.data.pao.YukonPAObject)val);
        LiteYukonPAObject thisLite = new LiteYukonPAObject(yukonPAObject.getPAObjectID().intValue(),
        													yukonPAObject.getPAOName(), 
        													PaoType.getForDbString(yukonPAObject.getPAOType()),
        													yukonPAObject.getPAODescription(),
        													String.valueOf(yukonPAObject.getPAODisableFlag()));
        returnLite = thisLite;
	}
   else if( val instanceof com.cannontech.database.db.state.YukonImage )
   {
      returnLite = new LiteYukonImage(
         ((com.cannontech.database.db.state.YukonImage)val).getImageID().intValue(),
         ((com.cannontech.database.db.state.YukonImage)val).getImageName() );
   }
   else if( val instanceof DeviceTypeCommand)
   {
	  returnLite = new LiteDeviceTypeCommand(
	  		((DeviceTypeCommand)val).getDeviceCommandID().intValue(), 
			((DeviceTypeCommand)val).getCommandID().intValue(),
			((DeviceTypeCommand)val).getDeviceType(),
			((DeviceTypeCommand)val).getDisplayOrder().intValue(),
			((DeviceTypeCommand)val).getVisibleFlag().charValue());
   }
   else if( val instanceof Command)
   {
	  returnLite = new LiteCommand(
	  		((Command)val).getCommandID().intValue(),
			((Command)val).getLabel(),
			((Command)val).getCommand(),
			((Command)val).getCategory());
   }
	else if ( val instanceof RawPointHistory)
	{
		returnLite = new LiteRawPointHistory(
			((RawPointHistory)val).getChangeID().intValue(),
			((RawPointHistory)val).getPointID().intValue(),
			((RawPointHistory)val).getTimeStamp().getTimeInMillis(),
			((RawPointHistory)val).getQuality().intValue(),
			((RawPointHistory)val).getValue().doubleValue());	
   	}
	else if( val instanceof SettlementConfig)
	{
	   returnLite = new LiteSettlementConfig(
			 ((SettlementConfig)val).getConfigID().intValue(),
			 ((SettlementConfig)val).getFieldName(),
			 ((SettlementConfig)val).getFieldValue(),
			 ((SettlementConfig)val).getDescription(), 
			 ((SettlementConfig)val).getRefEntryID().intValue());
	}

	return returnLite;
}




/***********************************************************************
 * This method returns the correct DBPersistent for any LiteBase.  There
 * may be cases where we need a different DBPersistent than the default
 * one provided (ex: LiteDeviceMeterGroup), this method will allow that
 * extra layer to be implemented.
 * 
 * @param lBase
 * @return DBPersistent
 * 
 * *********************************************************************/
public static DBPersistent convertLiteToDBPers( LiteBase lBase )
{
	com.cannontech.database.db.DBPersistent userObject = null;

	if( lBase instanceof LiteDeviceMeterNumber )
	{					
		userObject = com.cannontech.database.data.lite.LiteFactory.createDBPersistent(
			DaoFactory.getPaoDao().getLiteYukonPAO( lBase.getLiteID() ) );
	}
	else
 		userObject = com.cannontech.database.data.lite.LiteFactory.createDBPersistent( lBase );

	return userObject;
}

public static DBPersistent convertLiteToDBPersAndRetrieve( LiteBase lBase ) {
	
	com.cannontech.database.db.DBPersistent userObject = null;

	if( lBase instanceof LiteDeviceMeterNumber )
	{					
		userObject = com.cannontech.database.data.lite.LiteFactory.createDBPersistent(
			DaoFactory.getPaoDao().getLiteYukonPAO( lBase.getLiteID() ) );
	}
	else
 		userObject = com.cannontech.database.data.lite.LiteFactory.createDBPersistent( lBase );
	
	try
    {
       Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, userObject);
       userObject = t.execute();	
    }
    catch (Exception e)
    {
       com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
    }

	return userObject;
	
}

/*
 * This guy is useful when we need to retrieve for either a LiteCICustomer or a regular LiteCustomer.
 */
public static LiteCustomer createCICustomerOrLiteCustomer(String dbAlias, int customerID, int customerType)
{
    if(customerType == CustomerTypes.CUSTOMER_CI)
    {
        LiteCICustomer theCustomer = new LiteCICustomer(customerID);
        theCustomer.retrieve(dbAlias);
        return theCustomer;
    }
    else
    {
        LiteCustomer theCustomer = new LiteCustomer(customerID);
        theCustomer.retrieve(dbAlias);
        return theCustomer;
    }
}

}