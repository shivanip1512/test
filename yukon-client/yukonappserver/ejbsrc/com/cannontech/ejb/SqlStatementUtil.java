/*
 * Generated file - Do not edit!
 */
package com.cannontech.ejb;

import java.lang.*;
import java.rmi.RemoteException;
import java.sql.Connection;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.yukon.ISQLStatement;

import javax.rmi.PortableRemoteObject;
import javax.naming.NamingException;
import javax.naming.InitialContext;

import java.util.Hashtable;

/**
 * Utility class for SqlStatement.
 * @xdoclet-generated at Nov 4, 2002 5:13:04 PM
 */
public class SqlStatementUtil
{
   // Home interface lookup methods

   /**
    * Obtain remote home interface from default initial context
    * @return Home interface for SqlStatement. Lookup using COMP_NAME
    */
   public static com.cannontech.ejb.SqlStatementHome getHome() throws NamingException
   {
      InitialContext initialContext = new InitialContext();
      try {
         java.lang.Object objRef = initialContext.lookup(com.cannontech.ejb.SqlStatementHome.COMP_NAME);
         com.cannontech.ejb.SqlStatementHome home = (com.cannontech.ejb.SqlStatementHome)PortableRemoteObject.narrow(objRef, com.cannontech.ejb.SqlStatementHome.class);
         return home;
      } finally {
         initialContext.close();
      }
   }

   /**
    * Obtain remote home interface from parameterised initial context
    * @param environment Parameters to use for creating initial context
    * @return Home interface for SqlStatement. Lookup using COMP_NAME
    */
   public static com.cannontech.ejb.SqlStatementHome getHome( Hashtable environment ) throws NamingException
   {
      InitialContext initialContext = new InitialContext(environment);
      try {
         java.lang.Object objRef = initialContext.lookup(com.cannontech.ejb.SqlStatementHome.COMP_NAME);
         com.cannontech.ejb.SqlStatementHome home = (com.cannontech.ejb.SqlStatementHome)PortableRemoteObject.narrow(objRef, com.cannontech.ejb.SqlStatementHome.class);
         return home;
      } finally {
         initialContext.close();
      }
   }

}
