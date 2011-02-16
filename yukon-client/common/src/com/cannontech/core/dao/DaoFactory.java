package com.cannontech.core.dao;

import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;

public class DaoFactory {
    
    public static AddressDao getAddressDao() {
        return YukonSpringHook.getBean("addressDao", AddressDao.class);
    }
    
    public static AlarmCatDao getAlarmCatDao() {
        return (AlarmCatDao) YukonSpringHook.getBean("alarmCatDao");
    }
    
    public static AlarmDao getAlarmDao() {
        return (AlarmDao) YukonSpringHook.getBean("alarmDao");
    }
    
    public static AuthDao getAuthDao() {
        return (AuthDao) YukonSpringHook.getBean("authDao");
    }
    
    public static CommandDao getCommandDao() {
        return (CommandDao) YukonSpringHook.getBean("commandDao");
    }
    
    public static ContactDao getContactDao() {
        return (ContactDao) YukonSpringHook.getBean("contactDao");
    }
    
    public static ContactNotificationDao getContactNotificationDao() {
        return (ContactNotificationDao) YukonSpringHook.getBean("contactNotificationDao");
    }
    
    public static CustomerDao getCustomerDao() {
        return (CustomerDao) YukonSpringHook.getBean("customerDao");
    }
    
    public static DBDeleteResult getDbDeleteResultDao() {
        return (DBDeleteResult) YukonSpringHook.getBean("dbDeleteResultDao");
    }
    
    public static DBDeletionDao getDbDeletionDao() {
        return (DBDeletionDao) YukonSpringHook.getBean("dbDeletionDao");
    }
    
    public static DBPersistentDao getDbPersistentDao() {
        return (DBPersistentDao) YukonSpringHook.getBean("dbPersistentDao");
    }
    
    public static DeviceDao getDeviceDao() {
        return (DeviceDao) YukonSpringHook.getBean("deviceDao");
    }
    
    public static EnergyCompanyDao getEnergyCompanyDao() {
        return (EnergyCompanyDao) YukonSpringHook.getBean("energyCompanyDao");
    }
    
    public static GraphDao getGraphDao() {
        return (GraphDao) YukonSpringHook.getBean("graphDao");
    }
    
    public static LMDao getLmDao() {
        return (LMDao) YukonSpringHook.getBean("lmDao");
    }
    
    public static NotificationGroupDao getNotificationGroupDao() {
        return (NotificationGroupDao) YukonSpringHook.getBean("notificationGroupDao");
    }
    
    public static PaoDao getPaoDao() {
        return (PaoDao) YukonSpringHook.getBean("paoDao");
    }
    
    public static PointDao getPointDao() {
        return (PointDao) YukonSpringHook.getBean("pointDao");
    }
    
    public static RoleDao getRoleDao() {
        return (RoleDao) YukonSpringHook.getBean("roleDao");
    }
    
    public static SimplePointAccessDao getSimplePointAccessDao() {
        return (SimplePointAccessDao) YukonSpringHook.getBean("simplePointAccessDao");
    }
    
    public static StateDao getStateDao() {
        return (StateDao) YukonSpringHook.getBean("stateDao");
    }
    
    public static TagDao getTagDao() {
        return (TagDao) YukonSpringHook.getBean("tagDao");
    }
    
    public static UnitMeasureDao getUnitMeasureDao() {
        return (UnitMeasureDao) YukonSpringHook.getBean("unitMeasureDao");
    }
    
    public static YukonImageDao getYukonImageDao() {
        return (YukonImageDao) YukonSpringHook.getBean("yukonImageDao");
    }
    
    public static YukonListDao getYukonListDao() {
        return YukonSpringHook.getBean("yukonListDao", YukonListDao.class);
    }
    
    public static YukonUserDao getYukonUserDao() {
        return (YukonUserDao) YukonSpringHook.getBean("yukonUserDao");
    }
    
    public static YukonGroupDao getYukonGroupDao() {
        return (YukonGroupDao) YukonSpringHook.getBean("yukonGroupDao");
    }
    
    public static CapControlDao getCapControlDao () {
        return  (CapControlDao) YukonSpringHook.getBean("capControlDao");
    }
    
    public static SeasonScheduleDao getSeasonSchedule () {
        return  (SeasonScheduleDao) YukonSpringHook.getBean("seasonScheduleDao");
    }

}
