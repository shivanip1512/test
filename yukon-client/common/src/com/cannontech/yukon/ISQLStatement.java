package com.cannontech.yukon;

import java.sql.Connection;

import com.cannontech.common.util.CommandExecutionException;

/**
 * Interface for adding,updating,deleting, and retrieving
 * an object to/from persistent storage.
 * @author alauinger
 */
public interface ISQLStatement 
{
	/* Methods that need defining */
	public void setSQLString( String sql );
	public void setDBConnection( Connection conn );
   
	public void execute() throws CommandExecutionException;
	public Object[] getRow( int row );
	public int getRowCount();
}
