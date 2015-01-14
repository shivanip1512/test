package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.baseline.Baseline;
import com.cannontech.database.data.capcontrol.CapControlYukonPAOBase;
import com.cannontech.database.data.command.DeviceTypeCommand;
import com.cannontech.database.data.config.ConfigTwoWay;
import com.cannontech.database.data.customer.CICustomerBase;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.customer.Customer;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.data.holiday.HolidaySchedule;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.notification.NotificationGroup;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.season.SeasonSchedule;
import com.cannontech.database.data.state.GroupState;
import com.cannontech.database.data.state.StateFactory;
import com.cannontech.database.data.tou.TOUSchedule;
import com.cannontech.database.data.user.UserGroup;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.command.Command;
import com.cannontech.database.db.company.SettlementConfig;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.LMProgramConstraint;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.graph.GraphCustomerList;
import com.cannontech.database.db.notification.AlarmCategory;
import com.cannontech.database.db.state.YukonImage;
import com.cannontech.database.db.tags.Tag;
import com.cannontech.database.db.user.YukonGroup;
import com.cannontech.spring.YukonSpringHook;

public final class LiteFactory {

    public final static com.cannontech.database.db.DBPersistent createDBPersistent(LiteBase liteObject) {

        DBPersistent returnObject = null;

        int liteType = liteObject.getLiteType();

        switch (liteType) {
        case LiteTypes.YUKON_PAOBJECT:
            returnObject = PAOFactory.createPAObject((LiteYukonPAObject) liteObject);

            if (returnObject instanceof DeviceBase) {
                ((DeviceBase) returnObject).setDeviceID(new Integer(((LiteYukonPAObject) liteObject).getYukonID()));
                ((DeviceBase) returnObject).setPAOName(((LiteYukonPAObject) liteObject).getPaoName());
            } else if (returnObject instanceof DirectPort) {
                ((DirectPort) returnObject).setPortID(new Integer(((LiteYukonPAObject) liteObject).getYukonID()));
                ((DirectPort) returnObject).setPortName(((LiteYukonPAObject) liteObject).getPaoName());
            } else if (returnObject instanceof RouteBase) {
                ((RouteBase) returnObject).setRouteID(new Integer(((LiteYukonPAObject) liteObject).getYukonID()));
                ((RouteBase) returnObject).setRouteName(((LiteYukonPAObject) liteObject).getPaoName());
            } else if (returnObject instanceof CapControlYukonPAOBase) {
                ((CapControlYukonPAOBase) returnObject).setCapControlPAOID(new Integer(((LiteYukonPAObject) liteObject).getYukonID()));
                ((CapControlYukonPAOBase) returnObject).setName(((LiteYukonPAObject) liteObject).getPaoName());
            }
            break;
        case LiteTypes.CUSTOMER_CI:
            returnObject = new CICustomerBase();
            ((CICustomerBase) returnObject).setCustomerID(new Integer(((LiteCICustomer) liteObject).getCustomerID()));
            ((CICustomerBase) returnObject).getCiCustomerBase().setCompanyName(((LiteCICustomer) liteObject).getCompanyName());
            ((CICustomerBase) returnObject).getCiCustomerBase().setCurtailAmount(new Double(((LiteCICustomer) liteObject).getCurtailAmount()));
            ((CICustomerBase) returnObject).getCiCustomerBase().setCustDmdLevel(new Double(((LiteCICustomer) liteObject).getDemandLevel()));
            ((CICustomerBase) returnObject).getCiCustomerBase().setMainAddressID(new Integer(((LiteCICustomer) liteObject).getMainAddressID()));
            ((CICustomerBase) returnObject).getCustomer().setPrimaryContactID(new Integer(((LiteCICustomer) liteObject).getPrimaryContactID()));
            ((CICustomerBase) returnObject).getCustomer().setTimeZone(((LiteCICustomer) liteObject).getTimeZone());
            ((CICustomerBase) returnObject).getCiCustomerBase().setCICustType(((LiteCICustomer) liteObject).getCICustType());
            break;
        case LiteTypes.CUSTOMER:
            returnObject = new Customer();
            ((Customer) returnObject).setCustomerID(new Integer(((LiteCustomer) liteObject).getCustomerID()));
            ((Customer) returnObject).getCustomer().setPrimaryContactID(new Integer(((LiteCustomer) liteObject).getPrimaryContactID()));
            ((Customer) returnObject).getCustomer().setTimeZone(((LiteCustomer) liteObject).getTimeZone());
            ((Customer) returnObject).getCustomer().setAltTrackingNumber(((LiteCustomer) liteObject).getAltTrackingNumber());
            ((Customer) returnObject).getCustomer().setCustomerNumber(((LiteCustomer) liteObject).getCustomerNumber());
            ((Customer) returnObject).getCustomer().setCustomerTypeID(new Integer(((LiteCustomer) liteObject).getCustomerTypeID()));
            ((Customer) returnObject).getCustomer().setRateScheduleID(new Integer(((LiteCustomer) liteObject).getRateScheduleID()));
            ((Customer) returnObject).getCustomer().setTemperatureUnit(((LiteCustomer) liteObject).getTemperatureUnit());
            break;
        case LiteTypes.POINT:
            returnObject = PointFactory.createPoint(((LitePoint) liteObject).getPointType());
            ((PointBase) returnObject).setPointID(new Integer(((LitePoint) liteObject).getPointID()));
            ((PointBase) returnObject).getPoint().setPointName(((LitePoint) liteObject).getPointName());
            break;
        case LiteTypes.STATEGROUP:
            returnObject = StateFactory.createGroupState();
            ((GroupState) returnObject).setStateGroupID(new Integer(((LiteStateGroup) liteObject).getStateGroupID()));
            ((GroupState) returnObject).getStateGroup().setName(((LiteStateGroup) liteObject).getStateGroupName());
            break;
        case LiteTypes.GRAPHDEFINITION:
            returnObject = new GraphDefinition();
            ((GraphDefinition) returnObject).getGraphDefinition().setGraphDefinitionID(new Integer(((LiteGraphDefinition) liteObject).getGraphDefinitionID()));
            ((GraphDefinition) returnObject).getGraphDefinition().setName(((LiteGraphDefinition) liteObject).getName());
            break;
        case LiteTypes.GRAPH_CUSTOMER_LIST:
            returnObject = new GraphCustomerList();
            ((GraphCustomerList) returnObject).setCustomerID(new Integer(((LiteGraphCustomerList) liteObject).getCustomerID()));
            ((GraphCustomerList) returnObject).setGraphDefinitionID(new Integer(((LiteGraphCustomerList) liteObject).getGraphDefinitionID()));
            break;
        case LiteTypes.NOTIFICATION_GROUP:
            returnObject = new NotificationGroup();
            ((NotificationGroup) returnObject).setNotificatoinGroupID(new Integer(((LiteNotificationGroup) liteObject).getNotificationGroupID()));
            ((NotificationGroup) returnObject).getNotificationGroup().setGroupName(((LiteNotificationGroup) liteObject).getNotificationGroupName());
            break;
        case LiteTypes.ALARM_CATEGORIES:
            returnObject = new AlarmCategory();
            ((AlarmCategory) returnObject).setAlarmCategoryID(new Integer(((LiteAlarmCategory) liteObject).getAlarmCategoryId()));
            ((AlarmCategory) returnObject).setCategoryName(((LiteAlarmCategory) liteObject).getCategoryName());
            ((AlarmCategory) returnObject).setNotificationGroupID(new Integer(((LiteAlarmCategory) liteObject).getNotificationGroupID()));
            break;
        case LiteTypes.CONTACT:
            returnObject = new Contact();
            ((Contact) returnObject).setContactID(new Integer(((LiteContact) liteObject).getContactID()));
            ((Contact) returnObject).getContact().setContFirstName(((LiteContact) liteObject).getContFirstName());
            ((Contact) returnObject).getContact().setContLastName(((LiteContact) liteObject).getContLastName());
            ((Contact) returnObject).getContact().setLogInID(new Integer(((LiteContact) liteObject).getLoginID()));
            ((Contact) returnObject).getContact().setAddressID(new Integer(((LiteContact) liteObject).getAddressID()));
            break;
        case LiteTypes.DEVICE_METERNUMBER:
            returnObject = new DeviceMeterGroupBase();
            ((DeviceMeterGroupBase) returnObject).getDeviceMeterGroup().setDeviceID(new Integer(((LiteDeviceMeterNumber) liteObject).getDeviceID()));
            ((DeviceMeterGroupBase) returnObject).getDeviceMeterGroup().setMeterNumber(((LiteDeviceMeterNumber) liteObject).getMeterNumber());
            break;
        case LiteTypes.HOLIDAY_SCHEDULE:
            returnObject = new HolidaySchedule();
            ((HolidaySchedule) returnObject).setHolidayScheduleID(new Integer(((LiteHolidaySchedule) liteObject).getHolidayScheduleID()));
            ((HolidaySchedule) returnObject).setHolidayScheduleName(((LiteHolidaySchedule) liteObject).getHolidayScheduleName());
            break;
        case LiteTypes.SEASON_SCHEDULE:
            returnObject = new SeasonSchedule();
            ((SeasonSchedule) returnObject).setScheduleID(new Integer(((LiteSeasonSchedule) liteObject).getScheduleID()));
            ((SeasonSchedule) returnObject).setScheduleName(((LiteSeasonSchedule) liteObject).getScheduleName());
            break;
        case LiteTypes.TOU_SCHEDULE:
            returnObject = new TOUSchedule();
            ((TOUSchedule) returnObject).setScheduleID(new Integer(((LiteTOUSchedule) liteObject).getScheduleID()));
            ((TOUSchedule) returnObject).setScheduleName(((LiteTOUSchedule) liteObject).getScheduleName());
            break;
        case LiteTypes.BASELINE:
            returnObject = new Baseline();
            ((Baseline) returnObject).setBaselineID(new Integer(((LiteBaseline) liteObject).getBaselineID()));
            ((Baseline) returnObject).setBaselineName(((LiteBaseline) liteObject).getBaselineName());
            break;
        case LiteTypes.CONFIG:
            returnObject = new ConfigTwoWay();
            ((ConfigTwoWay) returnObject).setConfigID(new Integer(((LiteConfig) liteObject).getConfigID()));
            ((ConfigTwoWay) returnObject).setConfigName(((LiteConfig) liteObject).getConfigName());
            break;
        case LiteTypes.LMCONSTRAINT:
            returnObject = new LMProgramConstraint();
            ((LMProgramConstraint) returnObject).setConstraintID(new Integer(((LiteLMConstraint) liteObject).getConstraintID()));
            ((LMProgramConstraint) returnObject).setConstraintName(((LiteLMConstraint) liteObject).getConstraintName());
            break;
        case LiteTypes.GEAR:
            String method = ((LiteGear) liteObject).getGearType();
            returnObject = GearControlMethod.getGearControlMethod(method).createNewGear();
            ((LMProgramDirectGear) returnObject).setGearID(new Integer(liteObject.getLiteID()));
            ((LMProgramDirectGear) returnObject).setGearName(((LiteGear) liteObject).getGearName());
            break;
        case LiteTypes.TAG:
            returnObject = new Tag();
            ((Tag) returnObject).setTagID(new Integer(((LiteTag) liteObject).getTagId()));
            ((Tag) returnObject).setTagName(((LiteTag) liteObject).getTagName());
            ((Tag) returnObject).setTagLevel(new Integer(((LiteTag) liteObject).getTagLevel()));
            Character inhibit = new Character('N');
            if (((LiteTag) liteObject).isInhibit()) {
                inhibit = new Character('Y');
            }
            ((Tag) returnObject).setInhibit(inhibit);
            ((Tag) returnObject).setColorID(new Integer(((LiteTag) liteObject).getColorId()));
            ((Tag) returnObject).setImageID(new Integer(((LiteTag) liteObject).getImageId()));
            break;
        case LiteTypes.STATE_IMAGE:
            returnObject = new YukonImage();
            ((YukonImage) returnObject).setImageID(new Integer(((LiteYukonImage) liteObject).getImageID()));
            ((YukonImage) returnObject).setImageValue(((LiteYukonImage) liteObject).getImageValue());
            ((YukonImage) returnObject).setImageName(((LiteYukonImage) liteObject).getImageName());
            ((YukonImage) returnObject).setImageCategory(((LiteYukonImage) liteObject).getImageCategory());
            break;

        case LiteTypes.YUKON_USER:
            returnObject = new YukonUser();
            ((YukonUser) returnObject).setUserID(new Integer(((LiteYukonUser) liteObject).getUserID()));
            ((YukonUser) returnObject).getYukonUser().setUsername(((LiteYukonUser) liteObject).getUsername());
            break;
        case LiteTypes.YUKON_GROUP:
            returnObject = new com.cannontech.database.data.user.YukonGroup();
            ((com.cannontech.database.data.user.YukonGroup) returnObject).setGroupID(new Integer(((LiteYukonGroup) liteObject).getGroupID()));
            ((com.cannontech.database.data.user.YukonGroup) returnObject).getYukonGroup().setGroupName(((LiteYukonGroup) liteObject).getGroupName());
            ((com.cannontech.database.data.user.YukonGroup) returnObject).getYukonGroup().setGroupDescription(((LiteYukonGroup) liteObject).getGroupDescription());
            break;
        case LiteTypes.USER_GROUP:
            returnObject = new UserGroup();
            LiteUserGroup liteUserGroup = (LiteUserGroup) liteObject;
            ((UserGroup) returnObject).getUserGroup().setUserGroupId(liteUserGroup.getUserGroupId());
            ((UserGroup) returnObject).getUserGroup().setUserGroupName(liteUserGroup.getUserGroupName());
            ((UserGroup) returnObject).getUserGroup().setUserGroupDescription(liteUserGroup.getUserGroupDescription());
            break;
        case LiteTypes.DEVICE_TYPE_COMMAND:
            returnObject = new DeviceTypeCommand();
            ((DeviceTypeCommand) returnObject).setDeviceCommandID(new Integer(((LiteDeviceTypeCommand) liteObject).getDeviceCommandId()));
            ((DeviceTypeCommand) returnObject).setCommandID(new Integer(((LiteDeviceTypeCommand) liteObject).getCommandId()));
            ((DeviceTypeCommand) returnObject).setDeviceType(((LiteDeviceTypeCommand) liteObject).getDeviceType());
            ((DeviceTypeCommand) returnObject).setDisplayOrder(new Integer(((LiteDeviceTypeCommand) liteObject).getDisplayOrder()));
            ((DeviceTypeCommand) returnObject).setVisibleFlag(new Character(((LiteDeviceTypeCommand) liteObject).getVisibleFlag()));
            break;
        case LiteTypes.COMMAND:
            returnObject = new Command();
            ((Command) returnObject).setCommandID(new Integer(((LiteCommand) liteObject).getCommandId()));
            ((Command) returnObject).setCommand(((LiteCommand) liteObject).getCommand());
            ((Command) returnObject).setLabel(((LiteCommand) liteObject).getLabel());
            ((Command) returnObject).setCategory(((LiteCommand) liteObject).getCategory());
            break;
        /* TODO add SystemRole,YukonRoleProperty */
        /* TODO: add LiteTypes.TAG? */
        case LiteTypes.SETTLEMENT:
            returnObject = new SettlementConfig();
            ((SettlementConfig) returnObject).setConfigID(new Integer(((LiteSettlementConfig) liteObject).getConfigID()));
            ((SettlementConfig) returnObject).setFieldName(((LiteSettlementConfig) liteObject).getFieldName());
            ((SettlementConfig) returnObject).setFieldValue(((LiteSettlementConfig) liteObject).getFieldValue());
            ((SettlementConfig) returnObject).setDescription(((LiteSettlementConfig) liteObject).getDescription());
            break;
        default:
            returnObject = null;
            break;
        }

        if (returnObject == null) {
            throw new IllegalArgumentException("*** Unable to create a DBPersistant object from a givent Lite object in: createDBPersistent(LiteBase liteObject)");
        } else {
            return returnObject;
        }
    }

    /**
     * This method was created in VisualAge.
     */
    public final static LiteBase createLite(DBPersistent val) {
        LiteBase returnLite = null;

        if (val instanceof SmartMultiDBPersistent) {
            returnLite = createLite(((SmartMultiDBPersistent) val).getOwnerDBPersistent());
        } else if (val instanceof MultiDBPersistent) {
            returnLite = createLite(((MultiDBPersistent) val).getDBPersistentVector().get(0));
        } else if (val instanceof CICustomerBase) {
            returnLite = new LiteCICustomer(((CICustomerBase) val).getCustomerID());
            ((LiteCICustomer) returnLite).setCompanyName(((CICustomerBase) val).getCiCustomerBase().getCompanyName());
            ((LiteCICustomer) returnLite).setCICustType(((CICustomerBase) val).getCiCustomerBase().getCICustType());
            ((LiteCICustomer) returnLite).setCurtailAmount(((CICustomerBase) val).getCiCustomerBase().getCurtailAmount());
            ((LiteCICustomer) returnLite).setDemandLevel(((CICustomerBase) val).getCiCustomerBase().getCustDmdLevel());

        } else if (val instanceof Customer) {
            returnLite = new LiteCustomer(((Customer) val).getCustomerID());
        } else if (val instanceof PointBase) {
            if (((PointBase) val).getPoint().getPaoID() == null || 
                    ((PointBase) val).getPoint().getPaoID() == 0) {
                returnLite = new LitePoint(((PointBase) val).getPoint().getPointID(), ((PointBase) val).getPoint().getPointName());
            } else {
                PointBase pointBase = (PointBase) val;
                int pointType = PointType.getForString(pointBase.getPoint().getPointType()).getPointTypeId();
                returnLite = new LitePoint(pointBase.getPoint().getPointID(),
                                           pointBase.getPoint().getPointName(),
                                           pointType,
                                           pointBase.getPoint().getPaoID(),
                                           pointBase.getPoint().getPointOffset(),
                                           pointBase.getPoint().getStateGroupID());
            }

        } else if (val instanceof GroupState) {
            returnLite = new LiteStateGroup(((GroupState) val).getStateGroup().getStateGroupID(),
                                            ((GroupState) val).getStateGroup().getName());

        } else if (val instanceof HolidaySchedule) {
            returnLite = new LiteHolidaySchedule(((HolidaySchedule) val).getHolidayScheduleID(),
                                                 ((HolidaySchedule) val).getHolidayScheduleName());

        } else if (val instanceof SeasonSchedule) {
            returnLite = new LiteSeasonSchedule(((SeasonSchedule) val).getScheduleID(), 
                                                ((SeasonSchedule) val).getScheduleName());

        } else if (val instanceof TOUSchedule) {
            returnLite = new LiteTOUSchedule(((TOUSchedule) val).getScheduleID(),
                                             ((TOUSchedule) val).getScheduleName(),
                                             ((TOUSchedule) val).getDefaultRate());
        } else if (val instanceof Baseline) {
            returnLite = new LiteBaseline(((Baseline) val).getBaselineID(), ((Baseline) val).getBaselineName());

        } else if (val instanceof ConfigTwoWay) {
            returnLite = new LiteConfig(((ConfigTwoWay) val).getConfigID(), ((ConfigTwoWay) val).getConfigName());

        } else if (val instanceof LMProgramConstraint) {
            returnLite = new LiteLMConstraint(((LMProgramConstraint) val).getConstraintID(),
                                              ((LMProgramConstraint) val).getConstraintName());
        }
        else if (val instanceof Tag) {
            boolean temp = (((Tag) val).getInhibit().compareTo(new Character('Y')) == 0);

            returnLite = new LiteTag(((Tag) val).getTagID(), ((Tag) val).getTagName(), ((Tag) val).getTagLevel(), temp,
                                     ((Tag) val).getColorID(), ((Tag) val).getImageID());

        } else if (val instanceof GraphDefinition) {
            returnLite = new LiteGraphDefinition(((GraphDefinition) val).getGraphDefinition().getGraphDefinitionID(),
                                                 ((GraphDefinition) val).getGraphDefinition().getName());
        } else if (val instanceof NotificationGroup) {
            LiteNotificationGroup lGrp = new LiteNotificationGroup(((NotificationGroup) val).getNotificationGroup().getNotificationGroupID(),
                                                                   ((NotificationGroup) val).getNotificationGroup().getGroupName());

            lGrp.setDisabled(((NotificationGroup) val).getNotificationGroup().getDisableFlag().equalsIgnoreCase("Y"));

            returnLite = lGrp;
        } else if (val instanceof AlarmCategory) {
            returnLite = new LiteAlarmCategory(((AlarmCategory) val).getAlarmCategoryID(), 
                                               ((AlarmCategory) val).getCategoryName());

        } else if (val instanceof Contact) {
            returnLite = new LiteContact(((Contact) val).getContact().getContactID(),
                                         ((Contact) val).getContact().getContFirstName(),
                                         ((Contact) val).getContact().getContLastName());

        } else if (val instanceof DeviceMeterGroupBase) {
            YukonPao yukonPao = YukonSpringHook.getBean(PaoDao.class).getYukonPao(((DeviceMeterGroupBase) val).getDeviceMeterGroup().getDeviceID());
            returnLite = new LiteDeviceMeterNumber(((DeviceMeterGroupBase) val).getDeviceMeterGroup().getDeviceID(),
                                                   ((DeviceMeterGroupBase) val).getDeviceMeterGroup().getMeterNumber(),
                                                   yukonPao.getPaoIdentifier().getPaoType());

        } else if (val instanceof YukonUser) {
            com.cannontech.database.db.user.YukonUser user = ((YukonUser) val).getYukonUser();

            returnLite = new LiteYukonUser(user.getUserID(),
                                           user.getUsername(),
                                           user.getLoginStatus(),
                                           user.isForceReset(),
                                           user.getUserGroupId());
        } else if (val instanceof UserGroup) {
            returnLite = ((UserGroup) val).getUserGroup().getLiteUserGroup();
        } else if (val instanceof YukonGroup) {

            returnLite = new LiteYukonGroup(((YukonGroup) val).getGroupID(),
                                            ((YukonGroup) val).getGroupName());

            ((LiteYukonGroup) returnLite).setGroupDescription(((YukonGroup) val).getGroupDescription());
        } else if (val instanceof com.cannontech.database.data.user.YukonGroup) {
            returnLite = new LiteYukonGroup(((com.cannontech.database.data.user.YukonGroup) val).getGroupID(),
                                            ((com.cannontech.database.data.user.YukonGroup) val).getYukonGroup().getGroupName());

            ((LiteYukonGroup) returnLite).setGroupDescription(((com.cannontech.database.data.user.YukonGroup) val).getYukonGroup().getGroupDescription());
        }

        /* TODO add SystemRole,YukonRoleProperty */
        else if (val instanceof YukonPAObject) {
            YukonPAObject yukonPAObject = ((YukonPAObject) val);
            LiteYukonPAObject thisLite = new LiteYukonPAObject(yukonPAObject.getPAObjectID(),
                                                               yukonPAObject.getPAOName(),
                                                               yukonPAObject.getPaoType(),
                                                               yukonPAObject.getPAODescription(),
                                                               String.valueOf(yukonPAObject.getPAODisableFlag()));
            returnLite = thisLite;
        } else if (val instanceof YukonImage) {
            returnLite = new LiteYukonImage(((YukonImage) val).getImageID(),
                                            ((YukonImage) val).getImageName());
        } else if (val instanceof DeviceTypeCommand) {
            returnLite = new LiteDeviceTypeCommand(((DeviceTypeCommand) val).getDeviceCommandID(),
                                                   ((DeviceTypeCommand) val).getCommandID(),
                                                   ((DeviceTypeCommand) val).getDeviceType(),
                                                   ((DeviceTypeCommand) val).getDisplayOrder(),
                                                   ((DeviceTypeCommand) val).getVisibleFlag().charValue());
        } else if (val instanceof Command) {
            returnLite = new LiteCommand(((Command) val).getCommandID(),
                                         ((Command) val).getLabel(),
                                         ((Command) val).getCommand(),
                                         ((Command) val).getCategory());
        } else if (val instanceof SettlementConfig) {
            returnLite = new LiteSettlementConfig(((SettlementConfig) val).getConfigID(),
                                                  ((SettlementConfig) val).getFieldName(),
                                                  ((SettlementConfig) val).getFieldValue(),
                                                  ((SettlementConfig) val).getDescription(),
                                                  ((SettlementConfig) val).getRefEntryID());
        }

        return returnLite;
    }

    /***********************************************************************
     * This method returns the correct DBPersistent for any LiteBase. There may
     * be cases where we need a different DBPersistent than the default one
     * provided (ex: LiteDeviceMeterGroup), this method will allow that extra
     * layer to be implemented.
     *********************************************************************/
    public static DBPersistent convertLiteToDBPers(LiteBase lBase) {
        DBPersistent userObject = null;

        if (lBase instanceof LiteDeviceMeterNumber) {
            userObject = LiteFactory.createDBPersistent(YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(lBase.getLiteID()));
        } else {
            userObject = LiteFactory.createDBPersistent(lBase);
        }

        return userObject;
    }

    public static DBPersistent convertLiteToDBPersAndRetrieve(LiteBase lBase) {

        DBPersistent userObject = null;

        if (lBase instanceof LiteDeviceMeterNumber) {
            userObject = LiteFactory.createDBPersistent(YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(lBase.getLiteID()));
        } else {
            userObject = LiteFactory.createDBPersistent(lBase);
        }

        try {
            Transaction t = Transaction.createTransaction(Transaction.RETRIEVE,
                                                          userObject);
            userObject = t.execute();
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        }

        return userObject;
    }
}