package com.cannontech.database;

/**
 * This type was created in VisualAge.
 */
public class SqlStatement extends com.cannontech.common.util.Command {

	//InnerSqlStatement extends DBPersistent in order that
	//it can work with Transaction objects
	//To the outside we want to appear to be a Command
	//object however
	class InnerSqlStatement extends com.cannontech.database.db.DBPersistent
	{
		private String sql;
		private String dbAlias;
		
		Object[][] rowData;
		
		public InnerSqlStatement(String sql)
		{
			super();
			this.sql = sql;
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
	     stmt.executeUpdate(this.sql);
	    }
	    else
	    {
	     rset = stmt.executeQuery(this.sql);

	     columnCount = rset.getMetaData().getColumnCount();

	     while( rset.next() )
	     {
	      java.util.Vector rowData = new java.util.Vector();
	      boolean nonNullRow = false;

	      for( int i = 1; i <= columnCount; i++ )
	      {
	       // Some times getObject will return null - is this a bug in the driver or am i missing something?
	       // why would rset.next() == true if there were no objects to rows to retrieve???
	       // So the bandaid is to check for null and then below only add the current row's data if
	       // something meaningful is in it.
	       Object o = rset.getObject(i);

	       if( o != null )
	        nonNullRow = true; // at least 1 value in the row is not null

	       rowData.addElement( o );
	      }

	      if( rowData.size() > 0 && nonNullRow )
	       rows.addElement( rowData );
	     }

	     this.rowData = new Object[ rows.size() ][columnCount];

	     for( int i = 0; i < rows.size(); i++ )
	     {
	      java.util.Vector temp = (java.util.Vector) rows.elementAt(i);

	      temp.copyInto( rowData[i] );
	     }

	    }
	   }
	   catch( java.sql.SQLException e )
	   {
	    e.printStackTrace();
	   }
	   finally
	   {
	    try
	    {
	     if( rset != null )
	      rset.close();

	     if( stmt != null )
	      stmt.close();
	    }
	    catch( java.sql.SQLException e2 )
	    {
	     //guess it didn't work
	    }
	   }
	  }

	  public Object[][] getRowData()
	  {
	   return this.rowData;
	  }
	};

	private String sql;
	private InnerSqlStatement innerSql;
	private String databaseAlias;	
/**
 * SqlStatement constructor comment.
 */
public SqlStatement(String sql, String databaseAlias) {
	super();
	
	this.innerSql = new InnerSqlStatement(sql);
	this.sql = sql;
	this.databaseAlias = databaseAlias;
}
/**
 * execute method comment.
 */
public void execute() throws com.cannontech.common.util.CommandExecutionException {

	java.util.StringTokenizer tok = new java.util.StringTokenizer(this.sql);

	//determine whether this is going to be an add, update, retrieve, or delete	
	int operation = Transaction.RETRIEVE; //default to retrieve i guess
	
	if( tok.hasMoreTokens() )
	{
		String opStr = tok.nextToken().toLowerCase();

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

	Transaction t = Transaction.createTransaction( operation, this.innerSql, this.databaseAlias );

	t.execute();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object[]
 * @param row int
 */
public Object[] getRow(int row) {

	Object[][] data = this.innerSql.getRowData();
	
	if( this.innerSql != null &&
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
 * This method was created in VisualAge.
 * @return int
 */
public int getRowCount() {

	if( this.innerSql == null )
		return 0;

	Object[][] data = this.innerSql.getRowData();

	if( data != null )
	{
		return data.length;
	}
	else
	{
		return 0;
	}
}
}
