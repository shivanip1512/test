package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
public class PointUnit extends com.cannontech.database.db.DBPersistent 
{
	public static final int DEFAULT_DECIMAL_PLACES = 3;
    public static final int DEFAULT_DECIMAL_DIGITS = 0;

	private Integer pointID = null;
	private Integer uomID = new Integer(com.cannontech.database.data.point.PointUnits.UOMID_KWH);
	private Integer decimalPlaces = new Integer(DEFAULT_DECIMAL_PLACES);
	private Double highReasonabilityLimit = new Double(com.cannontech.common.util.CtiUtilities.INVALID_MAX_DOUBLE);
	private Double lowReasonabilityLimit = new Double(com.cannontech.common.util.CtiUtilities.INVALID_MIN_DOUBLE);
    private Integer decimalDigits = new Integer(DEFAULT_DECIMAL_DIGITS);
	
	public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };
	public static final String SETTER_COLUMNS[] = 
	{ 
		"UomID", "DecimalPlaces", "HighReasonabilityLimit", "LowReasonabilityLimit", "DecimalDigits"
	};

	public final static String TABLE_NAME = "PointUnit";


/**
 * PointUnit constructor comment.
 */
public PointUnit() 
{
	super();
}
/**
 * PointUnit constructor comment.
 */
public PointUnit(Integer pointID, Integer umID, Integer newDecimalPlaces, Double highReasonValue, Double lowReasonValue, Integer newDecimalDigits ) 
{
	super();
	
	setPointID( pointID );
	setUomID( umID ) ;
	setDecimalPlaces( newDecimalPlaces );
	setHighReasonabilityLimit( highReasonValue );
	setLowReasonabilityLimit( lowReasonValue );
    setDecimalDigits( newDecimalDigits );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getPointID(), getUomID(), getDecimalPlaces(),
			getHighReasonabilityLimit(), getLowReasonabilityLimit(), getDecimalDigits() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getPointID() );
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 3:26:27 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDecimalPlaces() {
	return decimalPlaces;
}

/**
 * Returns the decimal digits allowed to the left of the decimal point
 * @return java.lang.Integer
 */
public java.lang.Integer getDecimalDigits() {
    return decimalDigits;
}

/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 3:18:55 PM)
 * @return java.lang.Double
 */
public java.lang.Double getHighReasonabilityLimit() {
	return highReasonabilityLimit;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 3:18:55 PM)
 * @return java.lang.Double
 */
public java.lang.Double getLowReasonabilityLimit() {
	return lowReasonabilityLimit;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPointID() {
	return pointID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 3:15:41 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getUomID() {
	return uomID;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getPointID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setUomID( (Integer) results[0] );
		setDecimalPlaces( (Integer) results[1] );
		setHighReasonabilityLimit( (Double) results[2] );
		setLowReasonabilityLimit( (Double) results[3] );
        setDecimalDigits( (Integer) results[4]);
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/00 3:26:27 PM)
 * @param newDecimalPlaces java.lang.Integer
 */
public void setDecimalPlaces(java.lang.Integer newDecimalPlaces) {
	decimalPlaces = newDecimalPlaces;
}

/**
 * Sets the number of digits allowed to the left of the decimal point
 * @param newDecimalPlaces java.lang.Integer
 */
public void setDecimalDigits(java.lang.Integer newDecimalDigits) {
    decimalDigits = newDecimalDigits;
}

/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 3:18:55 PM)
 * @param newHighReasonabilityLimit java.lang.Double
 */
public void setHighReasonabilityLimit(java.lang.Double newHighReasonabilityLimit) {
	highReasonabilityLimit = newHighReasonabilityLimit;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 3:18:55 PM)
 * @param newLowReasonabilityLimit java.lang.Double
 */
public void setLowReasonabilityLimit(java.lang.Double newLowReasonabilityLimit) {
	lowReasonabilityLimit = newLowReasonabilityLimit;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPointID(Integer newValue) {
	this.pointID = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 3:15:41 PM)
 * @param newUomID java.lang.Integer
 */
public void setUomID(java.lang.Integer newUomID) {
	uomID = newUomID;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getUomID(), getDecimalPlaces(),
			getHighReasonabilityLimit(), getLowReasonabilityLimit(), getDecimalDigits() };
	
	Object constraintValues[] = { getPointID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.common.util.NativeIntVector
 * @param uomid int
 */
/*public static final com.cannontech.common.util.NativeIntVector getAllPointIDsByUOMID(int uomids[]) throws java.sql.SQLException
{
   com.cannontech.common.util.NativeIntVector idList = 
            new com.cannontech.common.util.NativeIntVector(10);

   java.sql.Statement stmt = null;
   java.sql.ResultSet rset = null;
   java.sql.Connection conn = 
      com.cannontech.database.PoolManager.getInstance().getConnection( com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
   

   StringBuffer sql = new StringBuffer(
                  "select pointid from " +
                  TABLE_NAME +
                  " where uomid=");                  
                  for( int i = 0; i < uomids.length; i++ )
                  {
                     sql.append( uomids[i] );
                     if( i < (uomids.length-1) )
                        sql.append( " or uomid=" );
                  }
                  sql.append(" order by uomid");
   try
   {
      if( conn == null )
         throw new IllegalArgumentException("Received a (null) database connection");

      stmt = conn.createStatement();
      rset = stmt.executeQuery( sql.toString() );

      while( rset.next() )
      {
         idList.add( rset.getInt(1) );
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
         if( stmt != null ) stmt.close();
         if( conn != null ) conn.close();
      } 
      catch( java.sql.SQLException e2 )
      {
         com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
      }  
   }


   return idList;
}
*/

}