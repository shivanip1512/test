/*
 * Generated file - Do not edit!
 */
package com.cannontech.ejb;

import java.lang.*;
import com.cannontech.database.TransactionException;
import javax.ejb.SessionBean;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.RemoveException;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.CacheDBChangeListener;
import com.cannontech.yukon.server.cache.ServerDatabaseCache;

/**
 * Remote interface for DatabaseCache.
 * @xdoclet-generated at Oct 2, 2002 11:11:44 AM
 */
public interface DatabaseCache
   extends javax.ejb.EJBObject
{

   public void addDBChangeListener( com.cannontech.database.cache.DBChangeListener listener ) throws java.rmi.RemoteException;

   public com.cannontech.message.dispatch.message.DBChangeMsg[] createDBChangeMessages( com.cannontech.database.db.CTIDbChange newItem,int changeType ) throws java.rmi.RemoteException;

   public java.util.List getAllAlarmCategories(  ) throws java.rmi.RemoteException;

   public java.util.List getAllCapControlFeeders(  ) throws java.rmi.RemoteException;

   public java.util.List getAllCapControlSubBuses(  ) throws java.rmi.RemoteException;

   public java.util.List getAllCustomerContacts(  ) throws java.rmi.RemoteException;

   public java.util.List getAllCustomers(  ) throws java.rmi.RemoteException;

   public java.util.List getAllDeviceMeterGroups(  ) throws java.rmi.RemoteException;

   public java.util.List getAllDevices(  ) throws java.rmi.RemoteException;

   public java.util.List getAllGraphDefinitions(  ) throws java.rmi.RemoteException;

   public java.util.List getAllGraphTaggedPoints(  ) throws java.rmi.RemoteException;

   public java.util.List getAllHolidaySchedules(  ) throws java.rmi.RemoteException;

   public java.util.List getAllLMPrograms(  ) throws java.rmi.RemoteException;

   public java.util.List getAllLoadManagement(  ) throws java.rmi.RemoteException;

   public java.util.List getAllNotificationGroups(  ) throws java.rmi.RemoteException;

   public java.util.List getAllNotificationRecipients(  ) throws java.rmi.RemoteException;

   public java.util.HashMap getAllPointidMultiplierHashMap(  ) throws java.rmi.RemoteException;

   public java.util.List getAllPoints(  ) throws java.rmi.RemoteException;

   public java.util.List getAllPointsUnits(  ) throws java.rmi.RemoteException;

   public java.util.List getAllPorts(  ) throws java.rmi.RemoteException;

   public java.util.List getAllRoutes(  ) throws java.rmi.RemoteException;

   public java.util.List getAllStateGroups(  ) throws java.rmi.RemoteException;

   public java.util.List getAllUnitMeasures(  ) throws java.rmi.RemoteException;

   public java.util.List getAllUnusedCCDevices(  ) throws java.rmi.RemoteException;

   public java.util.List getAllYukonImages(  ) throws java.rmi.RemoteException;

   public java.util.List getAllYukonPAObjects(  ) throws java.rmi.RemoteException;

   public com.cannontech.database.data.lite.LiteBase handleDBChangeMessage( com.cannontech.message.dispatch.message.DBChangeMsg dbChangeMsg ) throws java.rmi.RemoteException;

   public void loadAllCache(  ) throws java.rmi.RemoteException;

   public void releaseAllAlarmCategories(  ) throws java.rmi.RemoteException;

   public void releaseAllCache(  ) throws java.rmi.RemoteException;

   public void releaseAllCustomerContacts(  ) throws java.rmi.RemoteException;

   public void releaseAllDeviceMeterGroups(  ) throws java.rmi.RemoteException;

   public void releaseAllGraphDefinitions(  ) throws java.rmi.RemoteException;

   public void releaseAllHolidaySchedules(  ) throws java.rmi.RemoteException;

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
