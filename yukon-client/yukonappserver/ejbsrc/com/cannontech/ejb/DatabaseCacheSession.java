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
 * Session layer for DatabaseCache.
 * @xdoclet-generated at Sep 27, 2002 4:47:51 PM
 */
public class DatabaseCacheSession
   extends com.cannontech.ejb.DatabaseCacheBean
   implements javax.ejb.SessionBean
{

   static final long serialVersionUID = -8770929681541039679L;

   public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException
   {
      super.ejbActivate();
   }

   public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException
   {
      super.ejbPassivate();
   }

   public void setSessionContext(javax.ejb.SessionContext ctx) throws javax.ejb.EJBException, java.rmi.RemoteException
   {
      super.setSessionContext(ctx);
   }

   public void unsetSessionContext() 
   {
   }

   public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException
   {
      super.ejbRemove();
   }

   public void ejbCreate() throws javax.ejb.CreateException
   {
      super.ejbCreate();
   }

}
