package com.cannontech.database.db.company;

import com.cannontech.common.util.CtiUtilities;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class EnergyCompany extends com.cannontech.database.db.DBPersistent 
{
	private Integer energyCompanyID = null;
	private String name = null;
	private Integer primaryContactID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer userID = new Integer(CtiUtilities.NONE_ZERO_ID);
	
	public static final String[] SETTER_COLUMNS = 
	{ 
		"Name", "PrimaryContactID", "UserID"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "EnergyCompanyID" };
	
	public static final String TABLE_NAME = "EnergyCompany";
	
	

	private static final String allSql =
	"SELECT EnergyCompanyID FROM EnergyCompany";
	
/**
 * EnergyCompany constructor comment.
 */
public EnergyCompany() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	if (getEnergyCompanyID() == null)
		setEnergyCompanyID( getNextEnergyCompanyID() );
	
	Object[] addValues = 
	{ 
		getEnergyCompanyID(),
		getName(),
		getPrimaryContactID(),
		getUserID()
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
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getEnergyCompanyID() );
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/00 4:28:38 PM)
 * @return java.lang.String
 */
public boolean equals(Object o)
{
	if( o instanceof EnergyCompany )	
		return ((EnergyCompany)o).getEnergyCompanyID().equals( getEnergyCompanyID() )
			? true 
			: false;
	else
		return false;
}

/**
 * Creation date: (6/11/2001 3:38:14 PM)
 * @return com.cannontech.database.db.web.EnergyCompany
 * @param dbAlias java.lang.String
 */
public static long[] getAllEnergyCompanyIDs() {
	return getAllEnergyCompanyIDs("yukon");
}
/**
 * Creation date: (6/11/2001 3:38:14 PM)
 * @return com.cannontech.database.db.web.EnergyCompany
 * @param dbAlias java.lang.String
 */
public static long[] getAllEnergyCompanyIDs(String dbAlias) {
	
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
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
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
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
public static final EnergyCompany[] getEnergyCompanies(java.sql.Connection conn)
{
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	java.util.ArrayList list = new java.util.ArrayList();
	
	String sql = "SELECT EnergyCompanyID," + 
						"Name, PrimaryContactID, UserID" +
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
				EnergyCompany ex = new EnergyCompany();
				ex.setEnergyCompanyID( new Integer(rset.getInt(CONSTRAINT_COLUMNS[0])) );
				ex.setName( rset.getString(SETTER_COLUMNS[0]) );
				ex.setPrimaryContactID( new Integer(rset.getInt(SETTER_COLUMNS[1])) );
				ex.setUserID(new Integer(rset.getInt(SETTER_COLUMNS[2])));

				list.add( ex );
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
			if( pstmt != null ) pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	EnergyCompany[] cmpys = new EnergyCompany[ list.size() ];	
	return (EnergyCompany[])list.toArray( cmpys );
}

/**
 * Insert the method's description here.
 * Creation date: (2/28/2002 10:21:44 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getEnergyCompanyID() {
	return energyCompanyID;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2002 10:21:44 AM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public static final Integer getNextEnergyCompanyID()
{
	Integer result = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	java.sql.Connection conn = null;

	String sql = "SELECT MAX(EnergyCompanyID)+1 FROM " + TABLE_NAME;

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

		pstmt = conn.prepareStatement(sql.toString());		
		rset = pstmt.executeQuery();							

		while( rset.next() )
			result = new Integer(rset.getInt(1));
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 ); //something is up
		}	
	}

	return result;
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 10:30:24 AM)
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
	Object[] constraintValues =  { getEnergyCompanyID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setName( (String) results[0] );
		setPrimaryContactID( (Integer)results[1] );
		setUserID((Integer)results[2]);
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2002 10:21:44 AM)
 * @param newEnergyCompanyID java.lang.Integer
 */
public void setEnergyCompanyID(java.lang.Integer newEnergyCompanyID) {
	energyCompanyID = newEnergyCompanyID;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2002 10:21:44 AM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * toString() override
 */
public String toString()
{
	return getName();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getName(),
		getPrimaryContactID(),
		getUserID()
	};

	//if any of the values are null, return
	if( !isValidValues(setValues) )
		return;

	
	Object[] constraintValues =  { getEnergyCompanyID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
	/**
	 * Returns the primaryContactID.
	 * @return Integer
	 */
	public Integer getPrimaryContactID() {
		return primaryContactID;
	}

	/**
	 * Sets the primaryContactID.
	 * @param primaryContactID The primaryContactID to set
	 */
	public void setPrimaryContactID(Integer primaryContactID) {
		this.primaryContactID = primaryContactID;
	}

	/**
	 * @return
	 */
	public Integer getUserID() {
		return userID;
	}

	/**
	 * @param integer
	 */
	public void setUserID(Integer integer) {
		userID = integer;
	}

}
