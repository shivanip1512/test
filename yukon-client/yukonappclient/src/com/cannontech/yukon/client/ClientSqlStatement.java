package com.cannontech.yukon.client;
import java.sql.Connection;

import javax.naming.InitialContext;

import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.ejb.SqlStatement;
import com.cannontech.ejb.SqlStatementBean;
import com.cannontech.ejb.SqlStatementHome;
import com.cannontech.yukon.ISQLStatement;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ClientSqlStatement implements ISQLStatement 
{
	private SqlStatement sqlStatBean = null;
	private InitialContext initialContext = null;
	
	
	
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
         
         sqlStatBean = (SqlStatement)createISQLStatement();
      }
      catch( Exception e ) 
      {
         com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      }
      
   }

	private SqlStatement getSqlStatement()
	{
		return sqlStatBean;
	}
	
   public ISQLStatement createISQLStatement()
   {
            
      try
      {
         return (ISQLStatement)
                  ((SqlStatementHome)initialContext.lookup(
                           com.cannontech.ejb.DBPersistentHome.JNDI_NAME) ).create();
      }
      catch( Exception e ) 
      {
         com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      }
       
      return null;                    
   }

	/**
	 * @see com.cannontech.yukon.ISQLStatement#setSQLString(java.lang.String)
	 */
	public void setSQLString(String sql) 
	{
      try
      {
         getSqlStatement().setSQLString( sql );
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }		
	}

	/**
	 * @see com.cannontech.yukon.ISQLStatement#setDBConnection(java.sql.Connection)
	 */
	public void setDBConnection(Connection conn) 
	{
		//do nothing
	}

	/**
	 * @see com.cannontech.yukon.ISQLStatement#execute()
	 */
	public void execute() throws CommandExecutionException 
	{
      try
      {
         getSqlStatement().execute();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }				
	}

	/**
	 * @see com.cannontech.yukon.ISQLStatement#getRow(int)
	 */
	public Object[] getRow(int row) 
	{
      try
      {
         return getSqlStatement().getRow( row );
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }		
      
      return null;
	}

	/**
	 * @see com.cannontech.yukon.ISQLStatement#getRowCount()
	 */
	public int getRowCount() 
	{
      try
      {
         getSqlStatement().getRowCount();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
      
      return 0;		
	}

}
