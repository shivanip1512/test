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
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.yukon.ISQLStatement;

/**
 * Home interface for SqlStatement. Lookup using {1}
 * @xdoclet-generated at Nov 5, 2002 2:19:53 PM
 */
public interface SqlStatementHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/SqlStatement";
   public static final String JNDI_NAME="jndi/SqlStatementBean";

	public com.cannontech.ejb.SqlStatement create() throws javax.ejb.CreateException, java.rmi.RemoteException;	
}
