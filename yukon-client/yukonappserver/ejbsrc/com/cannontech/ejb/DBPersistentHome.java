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
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.TransactionException;

/**
 * Home interface for DBPersistent. Lookup using {1}
 * @xdoclet-generated at Nov 4, 2002 5:13:03 PM
 */
public interface DBPersistentHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/DBPersistent";
   public static final String JNDI_NAME="jndi/DBPersistentBean";

   public com.cannontech.ejb.DBPersistent create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}
