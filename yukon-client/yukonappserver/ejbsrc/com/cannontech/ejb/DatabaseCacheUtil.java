/*
 * Generated file - Do not edit!
 */
package com.cannontech.ejb;

import java.lang.*;
import com.cannontech.database.TransactionException;
import java.util.List;
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

import javax.rmi.PortableRemoteObject;
import javax.naming.NamingException;
import javax.naming.InitialContext;

import java.util.Hashtable;

/**
 * Utility class for DatabaseCache.
 * @xdoclet-generated at Nov 4, 2002 5:13:04 PM
 */
public class DatabaseCacheUtil
{
   // Home interface lookup methods

   /**
    * Obtain remote home interface from default initial context
    * @return Home interface for DatabaseCache. Lookup using COMP_NAME
    */
   public static com.cannontech.ejb.DatabaseCacheHome getHome() throws NamingException
   {
      InitialContext initialContext = new InitialContext();
      try {
         java.lang.Object objRef = initialContext.lookup(com.cannontech.ejb.DatabaseCacheHome.COMP_NAME);
         com.cannontech.ejb.DatabaseCacheHome home = (com.cannontech.ejb.DatabaseCacheHome)PortableRemoteObject.narrow(objRef, com.cannontech.ejb.DatabaseCacheHome.class);
         return home;
      } finally {
         initialContext.close();
      }
   }

   /**
    * Obtain remote home interface from parameterised initial context
    * @param environment Parameters to use for creating initial context
    * @return Home interface for DatabaseCache. Lookup using COMP_NAME
    */
   public static com.cannontech.ejb.DatabaseCacheHome getHome( Hashtable environment ) throws NamingException
   {
      InitialContext initialContext = new InitialContext(environment);
      try {
         java.lang.Object objRef = initialContext.lookup(com.cannontech.ejb.DatabaseCacheHome.COMP_NAME);
         com.cannontech.ejb.DatabaseCacheHome home = (com.cannontech.ejb.DatabaseCacheHome)PortableRemoteObject.narrow(objRef, com.cannontech.ejb.DatabaseCacheHome.class);
         return home;
      } finally {
         initialContext.close();
      }
   }

}
