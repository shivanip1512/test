package com.cannontech.yukon.client;

import java.sql.SQLException;

import com.cannontech.database.TransactionException;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.yukon.IDBPersistent;

/**
 * @author rneuharth
 * Sep 26, 2002 at 4:03:29 PM
 * 
 * A undefined generated comment
 */
public class ClientDBPersistent implements IDBPersistent
{
   private com.cannontech.ejb.DBPersistent dbBean = null;
   private javax.naming.InitialContext initialContext = null;
   
   
	/**
	 * Constructor for ClientDBPersistent.
	 */
	public ClientDBPersistent()
	{
		super();
	}

   private void initialize()
   {      
      /* THIS NEEDS TO CHANGE AFTER WE GET ROLLING!!! ---RWN */
      java.util.Hashtable props = new java.util.Hashtable();
      //----------------------JBOSS
      props.put(javax.naming.InitialContext.INITIAL_CONTEXT_FACTORY,
               "org.jnp.interfaces.NamingContextFactory");
                  
      props.put(javax.naming.InitialContext.PROVIDER_URL,
               "jnp://127.0.0.1:1099");
      //----------------------JBOSS         
      
      try
      {         
         initialContext = new javax.naming.InitialContext(props);
         
         dbBean = (com.cannontech.ejb.DBPersistent)createIDBPersistent();
      }
      catch( Exception e ) 
      {
         com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      }
      
   }

   public com.cannontech.yukon.IDBPersistent createIDBPersistent()
   {
            
      try
      {
         return (com.cannontech.yukon.IDBPersistent)
                  ((com.cannontech.ejb.DBPersistentHome)initialContext.lookup(
                           com.cannontech.ejb.DBPersistentHome.JNDI_NAME) ).create();
      }
      catch( Exception e ) 
      {
         com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      }
       
      return null;                    
   }

   private com.cannontech.ejb.DBPersistent getDBPersistent()
   {
      return dbBean;
   }

   public void add( String tableName, Object[] values ) throws SQLException 
   {
      try
      {
         getDBPersistent().add( tableName, values );
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   };
   
   public void delete( String tableName, String columnNames[], String columnValues[] ) throws SQLException 
   {
      try
      {
         getDBPersistent().delete( tableName, columnNames, columnValues );
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   };
   
   public void delete( String tableName, String columnName, String columnValue ) throws SQLException
   {
      try
      {
         getDBPersistent().delete( tableName, columnName, columnValue );
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   };
   
   public Object[][] retrieve(String[] selectColumns, String tableName, String[] constraintColumns, String[] constraintValues, boolean multipleReturn) throws SQLException 
   { 
      try
      {
         return getDBPersistent().retrieve( selectColumns, tableName, constraintColumns, constraintValues, multipleReturn);
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new Object[0][0];
      }
   };
   
   public Object[] retrieve(String selectColumnNames[], String tableName, String keyColumnNames[], String keyColumnValues[]) throws SQLException 
   { 
      try
      {
         return getDBPersistent().retrieve( selectColumnNames, tableName, keyColumnNames, keyColumnValues );
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new Object[0];
      }
   };
   
   public void update( String tableName, String setColumnName[], Object setColumnValue[],
                     String constraintColumnName[], Object constraintColumnValue[]) throws SQLException 
   {
      try
      {
         getDBPersistent().update( tableName, setColumnName, setColumnValue, constraintColumnName, constraintColumnValue );
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   };

   public com.cannontech.database.db.DBPersistent
             execute( int operation, com.cannontech.database.db.DBPersistent obj ) throws com.cannontech.database.TransactionException
   {
      try
      {
         return getDBPersistent().execute( operation, obj );
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return null;
      }
   };


   public void setDbConnection( java.sql.Connection conn )
   {
      //no affect
   };

   public java.sql.Connection getDbConnection() throws SQLException
   {
      //no affect
      return null;
   };

}
