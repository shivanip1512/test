/*
 * Generated file - Do not edit!
 */
package com.cannontech.ejb;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

/**
 * Utility class for TimedDatabaseCache.
 * @xdoclet-generated at Nov 5, 2002 2:19:54 PM
 */
public class TimedDatabaseCacheUtil
{
   // Home interface lookup methods

   /**
    * Obtain remote home interface from default initial context
    * @return Home interface for TimedDatabaseCache. Lookup using COMP_NAME
    */
   public static com.cannontech.ejb.TimedDatabaseCacheHome getHome() throws NamingException
   {
      InitialContext initialContext = new InitialContext();
      try {
         java.lang.Object objRef = initialContext.lookup(com.cannontech.ejb.TimedDatabaseCacheHome.COMP_NAME);
         com.cannontech.ejb.TimedDatabaseCacheHome home = (com.cannontech.ejb.TimedDatabaseCacheHome)PortableRemoteObject.narrow(objRef, com.cannontech.ejb.TimedDatabaseCacheHome.class);
         return home;
      } finally {
         initialContext.close();
      }
   }

   /**
    * Obtain remote home interface from parameterised initial context
    * @param environment Parameters to use for creating initial context
    * @return Home interface for TimedDatabaseCache. Lookup using COMP_NAME
    */
   public static com.cannontech.ejb.TimedDatabaseCacheHome getHome( Hashtable environment ) throws NamingException
   {
      InitialContext initialContext = new InitialContext(environment);
      try {
         java.lang.Object objRef = initialContext.lookup(com.cannontech.ejb.TimedDatabaseCacheHome.COMP_NAME);
         com.cannontech.ejb.TimedDatabaseCacheHome home = (com.cannontech.ejb.TimedDatabaseCacheHome)PortableRemoteObject.narrow(objRef, com.cannontech.ejb.TimedDatabaseCacheHome.class);
         return home;
      } finally {
         initialContext.close();
      }
   }

}
