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

/**
 * Remote interface for SqlStatement.
 * @xdoclet-generated at Nov 4, 2002 4:47:09 PM
 */
public interface SqlStatement
   extends javax.ejb.EJBObject
{

   public void execute(  ) throws com.cannontech.common.util.CommandExecutionException, java.rmi.RemoteException;

   public java.lang.Object[] getRow( int row ) throws java.rmi.RemoteException;

   public int getRowCount(  ) throws java.rmi.RemoteException;

   public void setDBConnection( java.sql.Connection conn_ ) throws java.rmi.RemoteException;

   public void setSQLString( java.lang.String sql_ ) throws java.rmi.RemoteException;

}
