/*
 * Generated file - Do not edit!
 */
package com.cannontech.ejb;

import java.lang.*;

/**
 * Remote interface for DatabaseCache.
 * @xdoclet-generated at Nov 5, 2002 2:19:53 PM
 */
public interface TimedDatabaseCache
   extends javax.ejb.EJBObject
{

   public java.util.List getAllPeakPointHistory(  ) throws java.rmi.RemoteException;

   public void loadAllTimedCache(  ) throws java.rmi.RemoteException;

   public void releaseAllPeakPointHistory(  ) throws java.rmi.RemoteException;

   public void setDatabaseAlias( java.lang.String newAlias ) throws java.rmi.RemoteException;
   
   public void setUpdateTimeInMillis( long millis) throws java.rmi.RemoteException;

}
