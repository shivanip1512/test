package com.cannontech.database.db.device.lm;

import com.cannontech.common.util.CtiUtilities;
/**
 * This type was created in VisualAge.
 */
public class LMGroupExpressComAddress extends com.cannontech.database.db.DBPersistent 
{
	private Integer addressID = null;
	private String addressType = null;
	private Integer address = new Integer(0);
	private String addressName = CtiUtilities.STRING_NONE; //default none
	
	//default value for all addresses
	public static final LMGroupExpressComAddress NONE_ADDRESS = 
			new LMGroupExpressComAddress( IlmDefines.NONE_ADDRESS_ID, CtiUtilities.STRING_NONE );


	public static final String SETTER_COLUMNS[] = 
	{ 
		"AddressType", "Address", "AddressName"
	};

	
	public static final String CONSTRAINT_COLUMNS[] = { "AddressID" };

	public static final String TABLE_NAME = "LMGroupExpressComAddress";
/**
 * LMGroupVersacom constructor comment.
 */
private LMGroupExpressComAddress() {
	super();
}
/**
 * LMGroupVersacom constructor comment.
 */
public LMGroupExpressComAddress( Integer addressID_, String addType_ )
{
	super();

	setAddressID( addressID_ );
	setAddressType( addType_ );
}
/**
 * LMGroupVersacom constructor comment.
 */
public LMGroupExpressComAddress( String addType )
{
	super();
	setAddressType( addType );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	//only add addresses that do not have an address id
	if( getAddressID() == null )
	{
		setAddressID( new Integer( getNextAddressID(getDbConnection()) ) );
		
		Object addValues[] = { getAddressID(), getAddressType(), 
			getAddress(), getAddressName() };

		add( TABLE_NAME, addValues );
	}
	
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	//don not ever delete our NONE row
	if( getAddressID().intValue() > IlmDefines.NONE_ADDRESS_ID.intValue() )
	{
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getAddressID() );
	}

}
/**
 * retrieve method comment.
 */
public boolean equals( Object o )
{
	return ( o != null
				&& o instanceof LMGroupExpressComAddress
				&& ((LMGroupExpressComAddress)o).getAddressID().equals(getAddressID()) );
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:05:42 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAddress() {
	return address;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:05:42 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAddressID() {
	return addressID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:05:42 AM)
 * @return java.lang.String
 */
public java.lang.String getAddressName() {
	return addressName;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:05:42 AM)
 * @return java.lang.String
 */
public java.lang.String getAddressType() {
	return addressType;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public final static LMGroupExpressComAddress[] getAllExpressCommAddress( String type )
{
	java.util.ArrayList list = new java.util.ArrayList(20);
	java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection(
					com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
	
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
		
	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection can not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement("select " + 
						"AddressID,AddressType,Address,AddressName " +
						"from " +
						TABLE_NAME + " where AddressType = " + type + " and AddressID > " + 
						IlmDefines.NONE_ADDRESS_ID + " order by AddressName" );

			rset = pstmt.executeQuery();							

			while( rset.next() )
			{	
				LMGroupExpressComAddress comm = new LMGroupExpressComAddress();
				comm.setAddressID( new Integer(rset.getInt(1)) );
				comm.setAddressType( rset.getString(2) );
				comm.setAddress( new Integer(rset.getInt(3)) );
				comm.setAddressName( rset.getString(4) );
				
				list.add( comm );
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


	LMGroupExpressComAddress[] retVal = new LMGroupExpressComAddress[list.size()];
	return (LMGroupExpressComAddress[])list.toArray( retVal );
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public final static LMGroupExpressComAddress[] getAllExpressCommAddressWithNames()
{
	java.util.ArrayList list = new java.util.ArrayList(20);
	java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection(
					com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
	
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
		
	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection can not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement("select " + 
						"AddressID,AddressType,Address,AddressName " +
						"from " +
						TABLE_NAME + 
						" where AddressName != '" + 
						CtiUtilities.STRING_NONE + "' order by AddressName" );

			rset = pstmt.executeQuery();							

			while( rset.next() )
			{
				String name = rset.getString(4);

				//only add address if they have a name
				if( name.length() > 0 )
				{				
					LMGroupExpressComAddress comm = new LMGroupExpressComAddress();
					comm.setAddressID( new Integer(rset.getInt(1)) );
					comm.setAddressType( rset.getString(2) );
					comm.setAddress( new Integer(rset.getInt(3)) );
					comm.setAddressName( name );

					list.add( comm );
				}
				
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
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	LMGroupExpressComAddress[] retVal = new LMGroupExpressComAddress[list.size()];
	return (LMGroupExpressComAddress[])list.toArray( retVal );
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public final static int getNextAddressID( java.sql.Connection conn )
{
	int retVal = 0;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
		
	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection can not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement("select max(AddressID) AS maxid from " + TABLE_NAME );
			rset = pstmt.executeQuery();							

			// Just one please
			if( rset.next() )
				retVal = rset.getInt("maxid") + 1;
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

	return retVal;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getAddressID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setAddressType( (String) results[0] );
		setAddress( (Integer) results[1] );
		setAddressName( (String) results[2] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:05:42 AM)
 * @param newAddress java.lang.Integer
 */
public void setAddress(java.lang.Integer newAddress) {
	address = newAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:05:42 AM)
 * @param newAddressID java.lang.Integer
 */
public void setAddressID(java.lang.Integer newAddressID) {
	addressID = newAddressID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:05:42 AM)
 * @param newAddressName java.lang.String
 */
public void setAddressName(java.lang.String newAddressName) {
	addressName = newAddressName;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:05:43 AM)
 * @param newAddressType java.lang.String
 */
public void setAddressType(java.lang.String newAddressType) {
	addressType = newAddressType;
}
/**
 * toString() method comment.
 */
public String toString()
{
	return getAddressName();
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getAddressType(), 
		getAddress(), getAddressName() };

	if( getAddressID() == null )
		add(); //we need to create a new address
	else
	{
		Object constraintValues[] = { getAddressID() };

		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

}
}
