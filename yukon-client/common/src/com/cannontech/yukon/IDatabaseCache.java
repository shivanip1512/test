package com.cannontech.yukon;

import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.database.data.lite.LiteBase;

import com.cannontech.database.cache.DBChangeListener;

/**
 * Interface to Yukon data.
 *
 * @author alauinger
 */
public interface IDatabaseCache {

   public void addDBChangeListener(DBChangeListener listener);
   public DBChangeMsg[] createDBChangeMessages( com.cannontech.database.db.CTIDbChange newItem, int changeType );
   public java.util.List getAllAlarmCategories();
   public java.util.List getAllYukonImages();
   public java.util.List getAllCapControlFeeders();
   public java.util.List getAllCapControlSubBuses();
   public java.util.List getAllCustomerContacts();
   public java.util.List getAllCustomers();
   public java.util.List getAllDeviceMeterGroups();
   public java.util.List getAllDevices();
   public java.util.List getAllGraphDefinitions();

   // This cache is derive from the point cache
   //public java.util.List getAllGraphTaggedPoints();

   public java.util.List getAllHolidaySchedules();
   public java.util.List getAllLMPrograms();
   public java.util.List getAllLoadManagement();
   public java.util.List getAllNotificationGroups();
   public java.util.List getAllNotificationRecipients();
   public java.util.List getAllPoints();
   public java.util.List getAllPointsUnits();
   public java.util.List getAllPointLimits();
   public java.util.HashMap getAllPointidMultiplierHashMap();
   public java.util.List getAllPorts();
   public java.util.List getAllRoutes();
   public java.util.List getAllStateGroups();
   public java.util.List getAllUnitMeasures();

   public java.util.List getAllYukonUsers();
   public java.util.List getAllYukonGroups();
   public java.util.List getAllYukonRoles();
   
   //Map<LiteYukonUser,List<LiteYukonGroup>> 
   public java.util.Map getAllYukonUserGroupMap();
   
   //Map<LiteYukonGroup,List<LiteYukonUser>>
   public java.util.Map getAllYukonGroupUserMap();
      
   //Map<LiteYukonUser,List<Pair<LiteYukonRole,String(value>>> 
   public java.util.Map getAllYukonUserRoleMap();
   
   //Map<LiteYukonUser,List<Pair<LiteYukonRole,String(value)>>>
   public java.util.Map getAllYukonGroupRoleMap();	
   
   //Map<LiteYukonUser,Map<String(rolename),Pair<LiteYukonRole,String(value)>>
   //Provided as a means to efficiently obtain a role and its value
   public java.util.Map getAllYukonUserRoleLookupMap();
        
   public java.util.List getAllEnergyCompanies();
   public java.util.Map getAllUserEnergyCompanies();
   
   // This cache is derive from the Device cache
   public java.util.List getAllUnusedCCDevices();

   public java.util.List getAllYukonPAObjects();
   
//   public IDatabaseCache getInstance();

   /**
    *  Returns the LiteBase object that was added,deleted or updated, 
    *    else null is returned.
    */
   public LiteBase handleDBChangeMessage(com.cannontech.message.dispatch.message.DBChangeMsg dbChangeMsg);

   public void loadAllCache();
   public void releaseAllCache();
   public void releaseAllAlarmCategories();
   public void releaseAllCustomerContacts();
   public void releaseAllDeviceMeterGroups();
   public void releaseAllYukonImages();
   public void releaseAllGraphDefinitions();
   public void releaseAllHolidaySchedules();
   public void releaseAllNotificationGroups();
   public void releaseAllNotificationRecipients();
   public void releaseAllPoints();
   public void releaseAllStateGroups();
   public void releaseAllUnitMeasures();
   public void releaseAllYukonPAObjects();
   public void removeDBChangeListener(DBChangeListener listener);
   public void setDatabaseAlias(String newAlias);

}
