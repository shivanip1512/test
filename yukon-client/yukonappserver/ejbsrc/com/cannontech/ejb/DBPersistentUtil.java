/*
 * Generated file - Do not edit!
 */
package com.cannontech.ejb;

import java.lang.*;
import java.rmi.RemoteException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import com.cannontech.yukon.IDBPersistent;
import java.sql.SQLException;
import com.cannontech.database.TransactionException;

import javax.rmi.PortableRemoteObject;
import javax.naming.NamingException;
import javax.naming.InitialContext;

import java.util.Hashtable;

/**
 * Utility class for DBPersistent.
 * @xdoclet-generated at Oct 2, 2002 11:11:45 AM
 */
public class DBPersistentUtil
{
   // Home interface lookup methods

   /**
    * Obtain remote home interface from default initial context
    * @return Home interface for DBPersistent. Lookup using COMP_NAME
    */
   public static com.cannontech.ejb.DBPersistentHome getHome() throws NamingException
   {
      InitialContext initialContext = new InitialContext();
      try {
         java.lang.Object objRef = initialContext.lookup(com.cannontech.ejb.DBPersistentHome.COMP_NAME);
         com.cannontech.ejb.DBPersistentHome home = (com.cannontech.ejb.DBPersistentHome)PortableRemoteObject.narrow(objRef, com.cannontech.ejb.DBPersistentHome.class);
         return home;
      } finally {
         initialContext.close();
      }
   }

   /**
    * Obtain remote home interface from parameterised initial context
    * @param environment Parameters to use for creating initial context
    * @return Home interface for DBPersistent. Lookup using COMP_NAME
    */
   public static com.cannontech.ejb.DBPersistentHome getHome( Hashtable environment ) throws NamingException
   {
      InitialContext initialContext = new InitialContext(environment);
      try {
         java.lang.Object objRef = initialContext.lookup(com.cannontech.ejb.DBPersistentHome.COMP_NAME);
         com.cannontech.ejb.DBPersistentHome home = (com.cannontech.ejb.DBPersistentHome)PortableRemoteObject.narrow(objRef, com.cannontech.ejb.DBPersistentHome.class);
         return home;
      } finally {
         initialContext.close();
      }
   }

}
