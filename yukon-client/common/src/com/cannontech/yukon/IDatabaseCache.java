package com.cannontech.yukon;

import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.database.data.lite.LiteBase;

import com.cannontech.database.cache.DBChangeListener;

/**
 * Interface to Yukon data.
 *
 * @author alauinger
 */
public interface IDatabaseCache 
{
   public void addDBChangeListener(DBChangeListener listener);
   public DBChangeMsg[] createDBChangeMessages( com.cannontech.database.db.CTIDbChange newItem, int changeType );
   public java.util.List getAllAlarmCategories();
   public java.util.List getAllYukonImages();
   public java.util.List getAllCapControlFeeders();
   public java.util.List getAllCapControlSubBuses();

   public java.util.List getAllContactNotificationGroups();
   public java.util.List getAllContacts();

	public java.util.List getAllCICustomers();
	   
   public java.util.List getAllDeviceMeterGroups();
   public java.util.List getAllDevices();
   public java.util.List getAllGraphDefinitions();

   // This cache is derive from the point cache
   //public java.util.List getAllGraphTaggedPoints();

   public java.util.List getAllHolidaySchedules();
   public java.util.List getAllBaselines();
   public java.util.List getAllLMPrograms();
   public java.util.List getAllLoadManagement();
   //public java.util.List getAllNotificationGroups();

   public java.util.List getAllPoints();
   public java.util.List getAllPointsUnits();
   public java.util.List getAllPointLimits();
   public java.util.HashMap getAllPointidMultiplierHashMap();
   //Map<Integer,Integer>
   public java.util.Map getAllPointIDOffsetMap();
   public java.util.List getAllPorts();
   public java.util.List getAllRoutes();
   public java.util.List getAllStateGroups();
   public java.util.List getAllUnitMeasures();

   public java.util.List getAllYukonUsers();
   public java.util.List getAllYukonGroups();
   public java.util.List getAllYukonRoles();
   public java.util.List getAllYukonRoleProperties();
   
   //Map<LiteYukonUser,List<LiteYukonGroup>> 
   public java.util.Map getYukonUserGroupMap();
   
   //Map<LiteYukonGroup,List<LiteYukonUser>>
   public java.util.Map getYukonGroupUserMap();
      
   //Map<LiteYukonUser, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String(value)>>>
   public java.util.Map getYukonUserRolePropertyMap();
   
   //Map<LiteYukonUser,List<Pair<LiteYukonRole,String(value)>>> 
   //public java.util.Map getAllYukonUserRoleMap();
     
   //Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String(value)>>>
   public java.util.Map getYukonGroupRolePropertyMap();
   
   //Map<LiteYukonUser,List<Pair<LiteYukonRole,String(value)>>>
   //public java.util.Map getAllYukonGroupRoleMap();	
   
   //Map<LiteYukonUser, Map<Integer(rolepropertyid), Pair<LiteYukonRoleProperty, String(value)>>>
   //Provided as a means to efficiently obtain a roleproperty and its value
   public java.util.Map getYukonUserRolePropertyIDLookupMap();
  
  //Map<LiteYukonUser, Map<Integer(roleid), LiteYukonRole>>
  //Provided as a means to efficiently obtain a role
  public java.util.Map getYukonUserRoleIDLookupMap();
           
   //Map<LiteYukonUser,Map<Integer(roleid),Pair<LiteYukonRole,String(value)>>
   //Provided as a means to efficiently obtain a role and its value
//   public java.util.Map getAllYukonUserRoleIDLookupMap(); 
   
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

   public void releaseAllCache();
   public void releaseAllAlarmCategories();
   public void releaseAllCustomerContacts();
   public void releaseAllDeviceMeterGroups();
   public void releaseAllYukonImages();
   public void releaseAllGraphDefinitions();
   public void releaseAllHolidaySchedules();
   public void releaseAllBaselines();
   public void releaseAllNotificationGroups();
   public void releaseAllContactNotifications();
   public void releaseAllPoints();
   public void releaseAllStateGroups();
   public void releaseAllUnitMeasures();
   public void releaseAllYukonPAObjects();
   public void removeDBChangeListener(DBChangeListener listener);
   public void setDatabaseAlias(String newAlias);

}
