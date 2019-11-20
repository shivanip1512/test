package com.cannontech.database.db.command;

import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class DeviceTypeCommand extends DBPersistent implements CTIDbChange
{
	public static int DEFAULT_COMMANDS_GROUP_ID = -1;
	private Integer deviceCommandID = null;
	private Integer commandID = null;
	
	//com.cannontech.database.data.pao.DeviceType String or CommandCategory string
	private String deviceType = null;
	
	private Integer displayOrder = new Integer(99);	//default to "end of the list"ish area
	private Character visibleFlag = new Character('Y');	//default on
	
	private Integer commandGroupID = DEFAULT_COMMANDS_GROUP_ID;
	
	public static final String[] SETTER_COLUMNS = 
	{ 
		"CommandID", "DeviceType", "DisplayOrder", "VisibleFlag", "CommandGroupID"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "DeviceCommandID" };
	
	public static final String TABLE_NAME = "DeviceTypeCommand";
	
/**
 * Command constructor comment.
 */
public DeviceTypeCommand() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	if (getDeviceCommandID() == null)
		setCommandID( getNextID(CtiUtilities.getDatabaseAlias()) );
		
	Object[] addValues = 
	{ 
		getDeviceCommandID(),
		getCommandID(),
		getDeviceType(),
		getDisplayOrder(),
		getVisibleFlag(),
		getCommandGroupID()
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
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceCommandID() );
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/00 4:28:38 PM)
 * @return java.lang.String
 */
public boolean equals(Object o)
{
	if( o instanceof DeviceTypeCommand )	
		return ((DeviceTypeCommand)o).getDeviceCommandID().equals( getDeviceCommandID() )
			? true 
			: false;
	else
		return false;
}


/**
 * @param dbAlias java.lang.String
 */
public static long[] getAllCommandIDsForType(String dbAlias, String deviceType_){
	
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		String allSql = "SELECT COMMANDID FROM " + TABLE_NAME + " WHERE DEVICETYPE = " + deviceType_;
						
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
		SqlUtils.close(rset, stmt, conn );
	}

	// An exception must have occurred
	return new long[0];
}


/**
 * @return java.lang.Integer
 */
public java.lang.Integer getCommandGroupID() {
	return commandGroupID;
}
/**
 * @return java.lang.Integer
 */
public java.lang.Integer getCommandID() {
	return commandID;
}
/**
 * Returns the DeviceType.  This should be a valid com.cannontech.database.data.pao.DeviceType string. 
 * @return java.lang.String
 */
public java.lang.String getDeviceType() {
	return deviceType;
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
	Object[] constraintValues =  { getDeviceCommandID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setCommandID( (Integer) results[0]);
		setDeviceType( (String) results[1] );
		setDisplayOrder( (Integer)results[2] );
		String temp = (String) results[3];
		if( temp != null )
			setVisibleFlag( new  Character( temp.charAt(0) ) );
			
		setCommandGroupID( (Integer) results[4]);
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * @param newCommandGroupID java.lang.Integer
 */
public void setCommandGroupID(java.lang.Integer newCommandGroupID) {
	commandGroupID = newCommandGroupID;
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
public void setDeviceType(java.lang.String newDeviceType) {
	deviceType = newDeviceType;
}
/**
 * toString() override
 */
public String toString()
{
	return getCommandID()+ " - " + getDeviceType();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getCommandID(),
		getDeviceType(),
		getDisplayOrder(),
		getVisibleFlag(),
		getCommandGroupID()
	};

	//if any of the values are null, return
	if( !isValidValues(setValues) )
		return;

	
	Object[] constraintValues =  { getDeviceCommandID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
	/**
	 * Returns the display order.
	 * @return Integer
	 */
	public Integer getDisplayOrder() {
		return displayOrder;
	}

	/**
	 * Sets the display order.
	 * If orders are duplicated then tough tooties.
	 * @param order_ The display order to set
	 */
	public void setDisplayOrder(Integer order_)
	{
		displayOrder = order_;
	}

	/**
	 * Returns the visibility of an item.
	 * @return 
	 */
	public Character getVisibleFlag() {
		return visibleFlag;
	}

	/**
	 * Sets the visibility of an item
	 * @param Character
	 */
	public void setVisibleFlag(Character visible_)
	{
		visibleFlag = visible_;
	}

	/**
	 * @return
	 */
	public Integer getDeviceCommandID()
	{
		return deviceCommandID;
	}

	/**
	 * @param integer
	 */
	public void setDeviceCommandID(Integer integer)
	{
		deviceCommandID = integer;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/99 10:31:33 AM)
	 * @return java.lang.Long
	 */
	public static synchronized Integer getNextID(String databaseAlias) {

		SqlStatement stmt =	new SqlStatement("SELECT MAX(DeviceCommandID) FROM " + TABLE_NAME,
														databaseAlias );

		Integer returnVal = null;
	 														
		try
		{
			stmt.execute();

			if( stmt.getRowCount() > 0 )
			{
				returnVal = new Integer( ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue() + 1);
			}		

		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}

		if( returnVal == null )
			returnVal = new Integer(1);
		
		return returnVal;
	}

	public boolean isVisible()
	{
		if( visibleFlag.charValue() == 'Y' )
			return true;
		return false;
	}
    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
        ArrayList<DBChangeMsg> list = new ArrayList<DBChangeMsg>(10);

        //add the basic change method
        list.add( new DBChangeMsg(
                                        getDeviceCommandID().intValue(),
                                        DBChangeMsg.CHANGE_DEVICETYPE_COMMAND_DB,
                                        DBChangeMsg.CAT_DEVICETYPE_COMMAND,
                                        dbChangeType) );
         
        DBChangeMsg[] dbChange = new DBChangeMsg[list.size()];
        return list.toArray( dbChange );
    }
}
