/*
 * Generated file - Do not edit!
 */
package com.cannontech.ejb;

import java.lang.*;

/**
 * Remote interface for DatabaseCache.
 * @xdoclet-generated at Nov 5, 2002 2:19:53 PM
 */
public interface DatabaseCache
   extends javax.ejb.EJBObject
{

   public void addDBChangeListener( com.cannontech.database.cache.DBChangeListener listener ) throws java.rmi.RemoteException;

   public com.cannontech.message.dispatch.message.DBChangeMsg[] createDBChangeMessages( com.cannontech.database.db.CTIDbChange newItem,int changeType ) throws java.rmi.RemoteException;

   public java.util.List getAllAlarmCategories(  ) throws java.rmi.RemoteException;

   public java.util.List getAllCapControlFeeders(  ) throws java.rmi.RemoteException;

   public java.util.List getAllContactNotificationGroups(  ) throws java.rmi.RemoteException;

   public java.util.List getAllCapControlSubBuses(  ) throws java.rmi.RemoteException;

   public java.util.List getAllContacts(  ) throws java.rmi.RemoteException;

   public java.util.List getAllCICustomers(  ) throws java.rmi.RemoteException;

   public java.util.List getAllDeviceMeterGroups(  ) throws java.rmi.RemoteException;

   public java.util.List getAllDevices(  ) throws java.rmi.RemoteException;
   
   public java.util.List getAllMCTs() throws java.rmi.RemoteException;

   public java.util.List getAllGraphDefinitions(  ) throws java.rmi.RemoteException;

   public java.util.List getAllHolidaySchedules(  ) throws java.rmi.RemoteException;
   
   public java.util.List getAllBaselines(  ) throws java.rmi.RemoteException;
   
   public java.util.List getAllConfigs(  ) throws java.rmi.RemoteException;

   public java.util.List getAllLMPrograms(  ) throws java.rmi.RemoteException;

   public java.util.List getAllLoadManagement(  ) throws java.rmi.RemoteException;

   public java.util.List getAllNotificationRecipients(  ) throws java.rmi.RemoteException;

   public java.util.List getAllPointLimits(  ) throws java.rmi.RemoteException;

   public java.util.HashMap getAllPointidMultiplierHashMap(  ) throws java.rmi.RemoteException;

   public java.util.Map getAllPointIDOffsetMap() throws java.rmi.RemoteException;
   
   public java.util.List getAllPoints(  ) throws java.rmi.RemoteException;

	public java.util.Map getAllPointsMaps(  ) throws java.rmi.RemoteException;

	public java.util.Map getAllPAOsMap(  ) throws java.rmi.RemoteException;

   public java.util.List getAllPointsUnits(  ) throws java.rmi.RemoteException;

   public java.util.List getAllPorts(  ) throws java.rmi.RemoteException;

   public java.util.List getAllRoutes(  ) throws java.rmi.RemoteException;

   public java.util.List getAllStateGroups(  ) throws java.rmi.RemoteException;

   public java.util.List getAllUnitMeasures(  ) throws java.rmi.RemoteException;

   public java.util.List getAllUnusedCCDevices(  ) throws java.rmi.RemoteException;

   public java.util.List getAllYukonImages(  ) throws java.rmi.RemoteException;

   public java.util.List getAllYukonPAObjects(  ) throws java.rmi.RemoteException;
   
   public java.util.List getAllYukonUsers() throws java.rmi.RemoteException;;
   
   public java.util.List getAllYukonGroups() throws java.rmi.RemoteException;;
   
   public java.util.List getAllYukonRoles() throws java.rmi.RemoteException;;
   
   public java.util.List getAllYukonRoleProperties() throws java.rmi.RemoteException;;
    
   public java.util.Map getYukonUserGroupMap() throws java.rmi.RemoteException;;
   
   public java.util.Map getYukonGroupUserMap() throws java.rmi.RemoteException;;
       
   public java.util.Map getYukonUserRolePropertyMap() throws java.rmi.RemoteException;;
   
   public java.util.Map getYukonGroupRolePropertyMap() throws java.rmi.RemoteException;;	
 
   public java.util.Map getYukonUserRoleIDLookupMap() throws java.rmi.RemoteException;;
   
   public java.util.Map getYukonUserRolePropertyIDLookupMap() throws java.rmi.RemoteException;;
   
   public java.util.List getAllEnergyCompanies() throws java.rmi.RemoteException;
   
   public java.util.Map getAllUserEnergyCompanies() throws java.rmi.RemoteException;
   
   public com.cannontech.database.data.lite.LiteBase handleDBChangeMessage( com.cannontech.message.dispatch.message.DBChangeMsg dbChangeMsg ) throws java.rmi.RemoteException;

   public void loadAllCache(  ) throws java.rmi.RemoteException;

   public void releaseAllAlarmCategories(  ) throws java.rmi.RemoteException;

   public void releaseAllCache(  ) throws java.rmi.RemoteException;

   public void releaseAllCustomerContacts(  ) throws java.rmi.RemoteException;

   public void releaseAllDeviceMeterGroups(  ) throws java.rmi.RemoteException;

   public void releaseAllGraphDefinitions(  ) throws java.rmi.RemoteException;

   public void releaseAllHolidaySchedules(  ) throws java.rmi.RemoteException;
   
   public void releaseAllBaselines(  ) throws java.rmi.RemoteException;
   
   public void releaseAllConfigs(  ) throws java.rmi.RemoteException;

   public void releaseAllNotificationGroups(  ) throws java.rmi.RemoteException;

   public void releaseAllNotificationRecipients(  ) throws java.rmi.RemoteException;

   public void releaseAllPoints(  ) throws java.rmi.RemoteException;

   public void releaseAllStateGroups(  ) throws java.rmi.RemoteException;

   public void releaseAllUnitMeasures(  ) throws java.rmi.RemoteException;

   public void releaseAllYukonImages(  ) throws java.rmi.RemoteException;

   public void releaseAllYukonPAObjects(  ) throws java.rmi.RemoteException;

   public void removeDBChangeListener( com.cannontech.database.cache.DBChangeListener listener ) throws java.rmi.RemoteException;

   public void setDatabaseAlias( java.lang.String newAlias ) throws java.rmi.RemoteException;

}
