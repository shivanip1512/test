package com.cannontech.ejb;
import java.rmi.RemoteException;
import java.sql.Connection;

import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.yukon.ISQLStatement;

/**
 * @ejb:bean name="SqlStatement"
 *	jndi-name="jndi/SqlStatementBean"
 *	type="Stateful" 
**/
public class SqlStatementBean implements ISQLStatement
{
	private String sql = null;
	private InnerSqlStatement innerSql = null;
   private java.sql.Connection dbConn = null;
	private String databaseAlias = CtiUtilities.getDatabaseAlias();	

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void setSQLString( String sql_ )
   {
   	sql = sql_;
   	innerSql = new InnerSqlStatement(sql);
   }
   

   public void setDBConnection( Connection conn_ )
   {
   	dbConn = conn_;
   }

	private boolean isStatementValid()
	{
		return( sql != null );
	}   

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public void execute() throws com.cannontech.common.util.CommandExecutionException 
	{
		if( !isStatementValid() )
			throw new IllegalStateException( this.getClass().getName() + 
				" must have all fields defined before executing this object."
				+ " : " + this.hashCode() );

		java.util.StringTokenizer tok = new java.util.StringTokenizer(sql);
	
		//determine whether this is going to be an add, update, retrieve, or delete	
		int operation = Transaction.RETRIEVE; //default to retrieve i guess
		String opStr = null;
		
		if( tok.hasMoreTokens() )
		{
			opStr = tok.nextToken().toLowerCase();
	
			if( opStr.equals("select") )
			{
				operation = Transaction.RETRIEVE;
			}
			else
			if( opStr.equals("insert") )
			{
				operation = Transaction.INSERT;
			}
			else
			if( opStr.equals("delete") )
			{
				operation = Transaction.DELETE;
			}
			else
			if( opStr.equals("update") )
			{
				operation = Transaction.UPDATE;
			}		
		}
	
	   if( dbConn != null )
	      innerSql.setDbConnection( dbConn );
	
	   try {
	   com.cannontech.clientutils.CTILogger.debug( 
	      "   DB: SQLStatement execute (" + opStr + ") " +
	      (innerSql.getDbConnection() != null ? String.valueOf(innerSql.getDbConnection().hashCode()) :
	      (dbConn != null ? String.valueOf(dbConn.hashCode()) : " ")) + 
			" : " + innerSql.sql );
	   } catch( Throwable t ) {}
	
		Transaction t = Transaction.createTransaction( operation, innerSql, databaseAlias );
		innerSql = (InnerSqlStatement)t.execute();
	}
	
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public Object[] getRow(int row) 
	{
		if( !isStatementValid() )
			return null;


		Object[][] data = innerSql.getRowData();
		
		if( innerSql != null &&
			data != null &&
			data[row] != null )
		{
			return data[row];
		}
		else
		{
			return null;
		}
			
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public int getRowCount() 
	{	
		if( !isStatementValid() )
			return 0;
	
		Object[][] data = innerSql.getRowData();
	
		if( data != null )
		{
			return data.length;
		}
		else
		{
			return 0;
		}
	}



	//InnerSqlStatement extends DBPersistent in order that
	//it can work with Transaction objects
	//To the outside we want to appear to be a Command
	//object however
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	class InnerSqlStatement extends com.cannontech.database.db.DBPersistent
	{
		private String sql;
		
		Object[][] rowData;
		
		public InnerSqlStatement(String sql_)
		{
			super();
			sql = sql_;
		}

		//No imp
		public void add()
		{
			execute(false);
		}

		//No imp
		public void update()
		{
			execute(false);
		}

		//Use retrieve for the sql - any type of statement should still work
		//fine - the decision to use retrieve is arbitrary
		public void retrieve()
		{
			execute(true);			
		}

		public void delete()
		{
			execute(false);
		}

		//results determines whether it is expected
	  //that results should be returned via ResultSet
		private void execute(boolean results)
		{
			java.sql.Statement stmt = null;
			java.sql.ResultSet rset = null;
			java.util.Vector rows = new java.util.Vector();
			int columnCount = 0;

			try
			{
				stmt = getDbConnection().createStatement();


				if( !results )
				{
					stmt.executeUpdate(sql);
				}
				else
				{
					rset = stmt.executeQuery(sql);

					columnCount = rset.getMetaData().getColumnCount();
					
					while( rset.next() )
					{
						java.util.Vector rowData = new java.util.Vector();
						boolean nonNullRow = false;

						for( int i = 1; i <= columnCount; i++ )
						{
							// Some times getObject will return null - is this a bug in the 
							// driver or am i missing something? why would rset.next() == true
							// if there were no objects to rows to retrieve??? So the bandaid 
							// is to check for null and then below only add the current row's 
							// data if something meaningful is in it.
							Object o = rset.getObject(i);
							
							if( o != null )
								nonNullRow = true; // at least 1 value in the row is not null


							if( o instanceof java.math.BigDecimal )
							{
								// >>>>>>>>>> Watch this - synchronize with above!
								if( rset.getMetaData().getPrecision(i) == DBPersistentBean.ORACLE_FLOAT_PRECISION )
									o = new Double( ((java.math.BigDecimal)o).doubleValue() );
								//else
									//o = new Integer( ((java.math.BigDecimal)o).intValue() );
							}
							
							rowData.addElement( o );
						}

						if( rowData.size() > 0 && nonNullRow )
							rows.addElement( rowData );
					}
					
					
					rowData = new Object[ rows.size() ][columnCount];

					for( int i = 0; i < rows.size(); i++ )
					{
						java.util.Vector temp = (java.util.Vector) rows.elementAt(i);

						temp.copyInto( rowData[i] );
					}

				}
			}
			catch( java.sql.SQLException e )
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
			finally
			{			
				try
				{
					if( rset != null ) rset.close();
					if( stmt != null ) stmt.close();
				}
				catch( java.sql.SQLException e2 )
				{
					//guess it didn't work
				}
			}
		}

	  public Object[][] getRowData()
	  {
	   return rowData;
	  }
	};

}





/*  This is at: http://sourceforge.net/forum/forum.php?thread_id=707260&forum_id=174174 
 
By: objectlearn ( ObjectLearn ) 
 RE: Final word...   
2002-09-12 23:58  
Apologies for not responding earlier, but trying to follow the thread to see what is different. 

XDoclet ant task for EJB code generation has been very unreliable, and we are not able to duplicate it on our machines. 

However, I will ask you to try yet few more things to control what is special here: 

Step -I ) External tools ANT prefereneces: Do NOT include any tools.jar, ant.jar, log4.jar libraries. 

Step - II) Make sure that your project has tools.jar (incase it does not add it). 

Step - III) Make sure that you are using the JDK not the JRE (Prefs-Java-Installed VMs) 

Step - IV) Make sure that Javadoc is in your path. 

If these do not fix your problem (it did in 95% of the cases), Can you please zip a sample project that does not work and your .metadata directory and email it to us (support@objectlearn.com) 

Thank you for your very patient trying to make things work.  
*/