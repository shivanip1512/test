package com.cannontech.database.db.command;

import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.command.DeviceTypeCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class Command extends DBPersistent implements com.cannontech.database.db.CTIDbChange
{
	private Integer commandID = null;
	private String command = null;
	private String label = null;
	private String category = PAOGroups.STRING_CAT_DEVICE;	//default
	
	public static final String[] SETTER_COLUMNS = 
	{ 
		"Command", "Label", "Category"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "CommandID" };
	
	public static final String TABLE_NAME = "Command";
	
	private static final String allSql =
		"SELECT COMMANDID FROM " + TABLE_NAME;
	
/**
 * Command constructor comment.
 */
public Command() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	if (getCommandID() == null)
		setCommandID( getNextID(CtiUtilities.getDatabaseAlias()) );
	
	Object[] addValues = 
	{ 
		getCommandID(),
		getCommand(),
		getLabel(),
		getCategory()
	};

	//if any of the values are null, return
	if( !isValidValues(addValues) )
		return;
	
	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	deleteDeviceTypeCommands();
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getCommandID() );
}

/**
 * Delete all of the DeviceTypeCommands with this commandID
 */
private void deleteDeviceTypeCommands() throws java.sql.SQLException
{
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List devTypeCmds = cache.getAllDeviceTypeCommands();
		
		for( int i = 0; i < devTypeCmds.size(); i++ )
		{
			LiteDeviceTypeCommand ldtc = (LiteDeviceTypeCommand)devTypeCmds.get(i);

			if( ldtc.getCommandID() == getCommandID().intValue() )
			{
				DeviceTypeCommand dbP = (DeviceTypeCommand)LiteFactory.createDBPersistent( ldtc );
				dbP.setDbConnection( getDbConnection() );
				dbP.delete();
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/00 4:28:38 PM)
 * @return java.lang.String
 */
public boolean equals(Object o)
{
	if( o instanceof Command )	
		return ((Command)o).getCommandID().equals( getCommandID() )
			? true 
			: false;
	else
		return false;
}

/**
 * @param dbAlias java.lang.String
 */
public static long[] getAllCommandIDs() {
	return getAllCommandIDs(CtiUtilities.getDatabaseAlias());
}
/**
 * @param dbAlias java.lang.String
 */
public static long[] getAllCommandIDs(String dbAlias) {
	
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = PoolManager.getInstance().getConnection(dbAlias);
		stmt = conn.createStatement();
		rset = stmt.executeQuery(allSql);

		java.util.ArrayList idList = new java.util.ArrayList();	
		while( rset.next() )
		{
			idList.add( new Long(rset.getLong(1)) );
		}

		long[] retIDs = new long[idList.size()];
		for( int i = 0; i < idList.size(); i++ )
		{
			retIDs[i] = ((Long) idList.get(i)).longValue();
		}
		
		return retIDs;			
	}
	catch(java.sql.SQLException e)
	{
		CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( stmt != null ) stmt.close();
			if( conn != null ) conn.close();
		}
		catch( java.sql.SQLException e2 )
		{
			CTILogger.error( e2.getMessage(), e2 );
		}
	}

	// An exception must have occured
	return new long[0];
}

/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 12:52:15 PM)
 * @ret int
 */
public static final Command[] getCommands(java.sql.Connection conn)
{
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	java.util.ArrayList list = new java.util.ArrayList();
	
	String sql = "SELECT COMMANDID, COMMAND, LABEL, CATEGORY " + 
						" FROM " + TABLE_NAME;

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());			
			rset = pstmt.executeQuery();
	
			while( rset.next() )
			{
				Command ex = new Command();
				ex.setCommandID( new Integer(rset.getInt(CONSTRAINT_COLUMNS[0])) );
				ex.setCommand(rset.getString(SETTER_COLUMNS[0]) );
				ex.setLabel( rset.getString(SETTER_COLUMNS[1]));
				ex.setCategory(rset.getString(SETTER_COLUMNS[2]));

				list.add( ex );
			}
		}		
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null ) pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	Command[] cmpys = new Command[ list.size() ];	
	return (Command[])list.toArray( cmpys );
}

/**
 * @return java.lang.Integer
 */
public java.lang.Integer getCommandID() {
	return commandID;
}
/**
 * Returns the label, the user-friendly string.
 * @return java.lang.String
 */
public java.lang.String getLabel() {
	return label;
}
/**
 * @return java.lang.Integer
 */
public static final Integer getNextID(String databaseAlias)
{
	Integer result = new Integer(1);	//default in case nothing returns?
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	java.sql.Connection conn = null;

	String sql = "SELECT MAX(COMMANDID)+1 FROM " + TABLE_NAME + " WHERE COMMANDID >= 0 ";

	try
	{
		conn = PoolManager.getInstance().getConnection(databaseAlias );

		pstmt = conn.prepareStatement(sql.toString());		
		rset = pstmt.executeQuery();							

		while( rset.next() )
			result = new Integer(rset.getInt(1));
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null ) pstmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			CTILogger.error( e2.getMessage(), e2 ); //something is up
		}	
	}

	return result;
}
/**
 * @return boolean
 */
private boolean isValidValues( Object[] values ) 
{
	if( values == null )
		return false;

	for( int i = 0; i < values.length; i++ )
		if( values[i] == null )
			return false;


	return true;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getCommandID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setCommand( (String) results[0] );
		setLabel( (String)results[1] );
		setCategory((String)results[2]);
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * @param newCommandID java.lang.Integer
 */
public void setCommandID(java.lang.Integer newCommandID) {
	commandID = newCommandID;
}
/**
 * @param newLabel java.lang.String
 */
public void setLabel(java.lang.String newLabel) {
	label = newLabel;
}
/**
 * toString() override
 */
public String toString()
{
	return getLabel() + " : " + getCommand() + " : " + getCategory();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getCommand(),
		getLabel(),
		getCategory()
	};

	//if any of the values are null, return
	if( !isValidValues(setValues) )
		return;

	
	Object[] constraintValues =  { getCommandID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
	/**
	 * Returns the command, the actual command being requested.
	 * @return String
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Sets the command.
	 * @param command_ The command to set
	 */
	public void setCommand(String command_)
	{
		command = command_;
	}

	/**
	 * Returns the category of command (not used as of 9/4/04)
	 * @return
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the category
	 * @param String
	 */
	public void setCategory(String category_)
	{
		category = category_;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.database.db.CTIDbChange#getDBChangeMsgs(int)
	 */
	public DBChangeMsg[] getDBChangeMsgs(int typeOfChange)
	{
		ArrayList list = new ArrayList(10);

		//add the basic change method
		list.add( new DBChangeMsg(
						getCommandID().intValue(),
						DBChangeMsg.CHANGE_COMMAND_DB,
						DBChangeMsg.CAT_COMMAND,
						DBChangeMsg.CAT_COMMAND,
						typeOfChange ) );
 
		DBChangeMsg[] dbChange = new DBChangeMsg[list.size()];
		return (DBChangeMsg[])list.toArray( dbChange );
	}

}
