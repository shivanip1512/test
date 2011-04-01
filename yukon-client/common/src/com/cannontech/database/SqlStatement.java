package com.cannontech.database;

import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.ISQLStatement;

/**
 * This type was created in VisualAge.
 */
public class SqlStatement implements com.cannontech.common.util.Command, ISQLStatement 
{
	private ISQLStatement sqlStatement = null;
	
	private synchronized ISQLStatement getSqlStatement() {
		if (sqlStatement == null) {
			sqlStatement = YukonSpringHook.getBean("sqlStatementBean", ISQLStatement.class);
		}

		return sqlStatement;
	}
		
	/**
	 * SqlStatement constructor comment.
	 */
	public SqlStatement(String sql, String databaseAlias) 
	{
		super();
	
		getSqlStatement().setSQLString( sql );	
	}
	
	/**
	 * SqlStatement constructor comment.
	 */
	public SqlStatement(String sql, java.sql.Connection conn ) 
	{
	   super();
	   
		getSqlStatement().setSQLString( sql );	
		getSqlStatement().setDBConnection( conn );	
	}


   public void setDBConnection( java.sql.Connection conn_ )
   {
   	getSqlStatement().setDBConnection( conn_ );
   }

   public void setSQLString( String str_ )
   {
   	getSqlStatement().setSQLString( str_ );
   }
	
	/**
	 * execute method comment.
	 */
	public void execute() throws com.cannontech.common.util.CommandExecutionException 
	{
		getSqlStatement().execute();
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object[]
	 * @param row int
	 */
	public Object[] getRow(int row) 
	{
		return getSqlStatement().getRow( row );
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return int
	 */
	public int getRowCount() 
	{
		return getSqlStatement().getRowCount();
	}

}
