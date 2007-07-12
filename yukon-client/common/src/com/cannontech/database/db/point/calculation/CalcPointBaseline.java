package com.cannontech.database.db.point.calculation;

import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */

public class CalcPointBaseline extends com.cannontech.database.db.DBPersistent 
{
	private Integer pointID = null;
	private Integer baselineID = null;
	
	public static final String COLUMNS[] = { 
			"POINTID", 
			"BASELINEID" };


	public static final String SELECT_COLUMNS[] = { COLUMNS[1] };

	public static final String TABLENAME = "CalcPointBaseline";

	/**
	 * CalcPointBaseline constructor comment.
	 */
	public CalcPointBaseline() {
		super();
		initialize( null, null );
	}
	/**
	 * CalcPointBaseline constructor comment.
	 */
	public CalcPointBaseline(Integer pointID) {
		super();
		initialize(pointID, null );
	}
	/**
	 * Point constructor comment.
	 */
	public CalcPointBaseline( Integer pointID, Integer baselineID) {
		super();
		initialize( pointID, baselineID);
	}
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException {
		Object addValues[]= { getPointID(), getBaselineID() };
	
		add( TABLENAME, addValues );
	}
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException {
		delete( TABLENAME, COLUMNS[0], getPointID() );
	}
	
	/**
	 * This method was created by Cannon Technologies Inc.
	 * @return boolean
	 * @param pointID java.lang.Integer
	 */
	public static boolean deleteCalcBaselinePoint(Integer pointID, java.sql.Connection conn )
	{
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement(
	            "DELETE FROM " + TABLENAME + " WHERE " + COLUMNS[0] + "=" + pointID,
					conn );
		try
		{
			stmt.execute();
		}
		catch(Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			return false;
		}
	
		return true;
	}
	
	public static boolean inUseByPoint(Integer baselineID, String databaseAlias)
	{
		com.cannontech.database.SqlStatement stmt = 
					new com.cannontech.database.SqlStatement(
						"SELECT PointID FROM " + 
						TABLENAME + 
						" WHERE BaselineID=" + baselineID,
						databaseAlias );
	
				try
				{
					stmt.execute();
					return (stmt.getRowCount() > 0 );
				}
				catch( Exception e )
				{
					return false;
				}		
	}
	/**
	 * This method was created in VisualAge.
	 * @param pointID java.lang.Integer
	 */
	public static CalcPointBaseline getCalcBaselinePoint(Integer pointID){
	
		return getCalcBaselinePoint( pointID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
	}
	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.database.db.device.DeviceScanRate[]
	 * @param deviceID java.lang.Integer
	 */
	public static CalcPointBaseline getCalcBaselinePoint(Integer pointID, String databaseAlias)
	{
		CalcPointBaseline returnPoint = null;
		Integer baselineID = null;
	
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		
		String sql = "SELECT " + COLUMNS[1] + " FROM " + TABLENAME +
			" WHERE " + COLUMNS[0] + " = ? ORDER BY " + COLUMNS[0];
	
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
				pstmt.setInt( 1, pointID.intValue() );
				
				rset = pstmt.executeQuery();
					
				while( rset.next() )
				{
					baselineID = new Integer( rset.getInt(COLUMNS[1]) );
					returnPoint = new CalcPointBaseline(pointID, baselineID);				
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
		return returnPoint;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getBaselineID() {
		return baselineID;
	}
	public Integer getPointID() {
		return pointID;
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void initialize( Integer pointID, Integer baselineID) {
	
		setPointID( pointID ) ;
		setBaselineID( baselineID);
	}
	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException 
	{
		String constraintColumns[] = { COLUMNS[0] };
		Object constraintValues[] = { getPointID() };
	
		Object results[] = retrieve( SELECT_COLUMNS, TABLENAME, constraintColumns, constraintValues );
	
		if( results.length == SELECT_COLUMNS.length )
		{
			setBaselineID( (Integer) results[0] );
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
			
	}
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setBaselineID(Integer newBaselineID) {
		baselineID = newBaselineID;
	}
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setPointID(Integer newValue) {
		this.pointID = newValue;
	}
	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException 
	{
		Object setValues[]= { getBaselineID()};
	
		String constraintColumns[] = { COLUMNS[0] };
		Object constraintValues[] = { getPointID() };
	
		update( TABLENAME, SELECT_COLUMNS, setValues, constraintColumns, constraintValues );
	}
}
