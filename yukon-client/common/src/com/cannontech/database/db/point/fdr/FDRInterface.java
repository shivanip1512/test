package com.cannontech.database.db.point.fdr;

import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */
@Deprecated
public class FDRInterface extends com.cannontech.database.db.DBPersistent 
{
	private Integer interfaceID = null;
	private String  interfaceName = null;
	private String possibleDirections = null;
	private boolean hasDestination = false;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"InterfaceNames", "PossibleDirection", "HasDestination"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "InterfaceID" };

	
	private static final String TABLE_NAME = "FDRInterface";
/**
 * Point constructor comment.
 */
public FDRInterface() 
{
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[]= { getInterfaceID(), getInterfaceName(), 
				getPossibleDirections(), new Boolean(hasDestination()) };

	add( this.TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "INTERFACEID", getInterfaceID() );
}
/**
 * This method was created in VisualAge.
 */
public static com.cannontech.database.data.fdr.FDRInterface[] getALLFDRInterfaces() 
{	
	return getAllFDRInterfaces(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public static com.cannontech.database.data.fdr.FDRInterface[] getAllFDRInterfaces( String databaseAlias )
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	
	//get all the gears that have the passed in DeviceID
	String sql = "select InterfaceID, InterfaceName, PossibleDirections, hasDestination " + 
					 " from " + TABLE_NAME +
					 " order by InterfaceName";
	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				FDRInterface interf = new FDRInterface();
				interf.setInterfaceID( new Integer(rset.getInt("InterfaceID")) );
				interf.setInterfaceName( rset.getString("InterfaceName") );
				interf.setPossibleDirections( rset.getString("PossibleDirections") );
				interf.setHasDestination( rset.getString("HasDestination").equalsIgnoreCase("t") );
				
				com.cannontech.database.data.fdr.FDRInterface value = new com.cannontech.database.data.fdr.FDRInterface();
				value.setFdrInterface( interf );
				tmpList.add( value );
			}

			//Match up all the InterfaceOptions that have the same interfaceID's
			sql = "select InterfaceID, OptionLabel, Ordering, OptionType, " + 
 					  " OptionValues from " + FDRInterfaceOption.TABLE_NAME +
					  " order by InterfaceID, Ordering";

			pstmt = conn.prepareStatement(sql.toString());
			rset = pstmt.executeQuery();

			while( rset.next() )
			{
				FDRInterfaceOption option = new FDRInterfaceOption();
				option.setInterfaceID( new Integer(rset.getInt("InterfaceID")) );
				option.setOptionLabel( rset.getString("OptionLabel") );
				option.setOrdering( new Integer(rset.getInt("Ordering")) );
				option.setOptionType( rset.getString("OptionType") );
				option.setOptionValues( rset.getString("OptionValues") );

				for( int i = 0; i < tmpList.size(); i++ )
				{
					if( ((com.cannontech.database.data.fdr.FDRInterface)tmpList.get(i)).getFdrInterface().getInterfaceID().intValue() ==
						 option.getInterfaceID().intValue() )
					{
						((com.cannontech.database.data.fdr.FDRInterface)tmpList.get(i)).getInterfaceOptionVector().add( option );
						break;
					}
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
		SqlUtils.close(rset, pstmt, conn );
	}


	com.cannontech.database.data.fdr.FDRInterface retVal[] = new com.cannontech.database.data.fdr.FDRInterface[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:08:18 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getInterfaceID() {
	return interfaceID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:08:18 PM)
 * @return java.lang.String
 */
public java.lang.String getInterfaceName() {
	return interfaceName;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:08:18 PM)
 * @return java.lang.String
 */
public java.lang.String getPossibleDirections() {
	return possibleDirections;
}

/**
 * Returns all the Directions by parsing the PossibleDirectsion String.
 * Assumes the delimiter is a comma.
 */
public String[] getAllDirections() {

	if( getPossibleDirections() == null )
		return new String[0];

	return getPossibleDirections().split( "," );
}

/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 11:02:01 AM)
 * @return boolean
 */
public boolean hasDestination() {
	return hasDestination;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getInterfaceID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setInterfaceName( (String) results[0] );
		setPossibleDirections( (String) results[1]);
		setHasDestination( ((String)results[2]).equalsIgnoreCase("t") );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
		
}
/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 11:02:01 AM)
 * @param newHasDestination boolean
 */
public void setHasDestination(boolean newHasDestination) {
	hasDestination = newHasDestination;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:08:18 PM)
 * @param newInterfaceID java.lang.Integer
 */
public void setInterfaceID(java.lang.Integer newInterfaceID) {
	interfaceID = newInterfaceID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:08:18 PM)
 * @param newInterfaceName java.lang.String
 */
public void setInterfaceName(java.lang.String newInterfaceName) {
	interfaceName = newInterfaceName;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:08:18 PM)
 * @param newPossibleDirections java.lang.String
 */
public void setPossibleDirections(java.lang.String newPossibleDirections) {
	possibleDirections = newPossibleDirections;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[]= { getInterfaceName(), 
				getPossibleDirections(), new Boolean(hasDestination()) };

	Object constraintValues[] = { getInterfaceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
