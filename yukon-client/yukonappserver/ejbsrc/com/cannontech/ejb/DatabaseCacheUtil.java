/*
 * Generated file - Do not edit!
 */
package com.cannontech.ejb;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

/**
 * Utility class for DatabaseCache.
 * @xdoclet-generated at Nov 5, 2002 2:19:54 PM
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
