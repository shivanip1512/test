package com.cannontech.yukon;

//import com.cannontech.database.Transaction;
import java.sql.Connection;

import com.cannontech.common.util.CommandExecutionException;

/**
 * Interface for adding,updating,deleting, and retrieving
 * an object to/from persistent storage.
 * @author alauinger
 */
public interface ISQLStatement 
{
/*   public static final int INSERT = Transaction.INSERT;
   public static final int UPDATE = Transaction.UPDATE;
   public static final int RETRIEVE = Transaction.RETRIEVE;
   public static final int DELETE = Transaction.DELETE;
   public static final int DELETE_PARTIAL = Transaction.DELETE_PARTIAL;
   public static final int ADD_PARTIAL = Transaction.ADD_PARTIAL;
*/

   /* Methods that need defining */
   public void setSQLString( String sql );
   public void setDBConnection( Connection conn );
   
	public void execute() throws CommandExecutionException;
	public Object[] getRow( int row );
	public int getRowCount();
}
