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
 * Remote interface for DBPersistent.
 * @xdoclet-generated at Nov 4, 2002 5:13:03 PM
 */
public interface DBPersistent
   extends javax.ejb.EJBObject
{

   public void add( java.lang.String tableName,java.lang.Object[] values ) throws java.sql.SQLException, java.rmi.RemoteException;

   public void delete( java.lang.String tableName,java.lang.String[] columnNames,java.lang.String[] columnValues ) throws java.sql.SQLException, java.rmi.RemoteException;

   public void delete( java.lang.String tableName,java.lang.String columnName,java.lang.String columnValue ) throws java.sql.SQLException, java.rmi.RemoteException;

   public com.cannontech.database.db.DBPersistent execute( int operation,com.cannontech.database.db.DBPersistent obj ) throws com.cannontech.database.TransactionException, java.rmi.RemoteException;

   public java.lang.Object[][] retrieve( java.lang.String[] selectColumns,java.lang.String tableName,java.lang.String[] constraintColumns,java.lang.String[] constraintValues,boolean multipleReturn ) throws java.sql.SQLException, java.rmi.RemoteException;

   public java.lang.Object[] retrieve( java.lang.String[] selectColumnNames,java.lang.String tableName,java.lang.String[] keyColumnNames,java.lang.String[] keyColumnValues ) throws java.sql.SQLException, java.rmi.RemoteException;

   public void update( java.lang.String tableName,java.lang.String[] setColumnName,java.lang.Object[] setColumnValue,java.lang.String[] constraintColumnName,java.lang.Object[] constraintColumnValue ) throws java.sql.SQLException, java.rmi.RemoteException;

}
