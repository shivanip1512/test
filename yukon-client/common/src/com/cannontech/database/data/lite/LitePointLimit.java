package com.cannontech.database.data.lite;

import java.math.BigDecimal;

/**
 * Lite representation of PointLimit
 * @author alauinger
 */
public class LitePointLimit extends LiteBase {
	private static final String retrieveSql = "SELECT LIMITNUMBER,HIGHLIMIT,LOWLIMIT,LIMITDURATION FROM POINTLIMIT WHERE POINTID=";
	
	private int pointID;
	private int limitNumber;
	private int highLimit;
	private int lowLimit;
	private int limitDuration;
	
	public LitePointLimit(int pointID) {
		setPointID(pointID);
		setLiteType(LiteTypes.POINT_LIMIT);
	}
	public LitePointLimit(int pointID, int limitNumber, int highLimit, int lowLimit, int limitDuration) {
		setPointID(pointID);
		setLimitNumber(limitNumber);
		setHighLimit(highLimit);
		setLowLimit(lowLimit);
		setLimitDuration(limitDuration);
		setLiteType(LiteTypes.POINT_LIMIT);
	}
	
	public void retrieve(String dbAlias) {
		com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement(retrieveSql + Integer.toString(getPointID()), dbAlias);

 		try
 		{
 			stmt.execute();
 			setLimitNumber( ((BigDecimal) stmt.getRow(0)[0]).intValue() );
 			setHighLimit( ((BigDecimal) stmt.getRow(0)[1]).intValue() );
 			setLowLimit( ((BigDecimal) stmt.getRow(0)[2]).intValue() );
 			setLimitDuration( ((BigDecimal) stmt.getRow(0)[3]).intValue() );
	 	}
 		catch( Exception e )
	 	{
 			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
 		}
	}
	
	/**
	 * Returns the highLimit.
	 * @return int
	 */
	public int getHighLimit() {
		return highLimit;
	}

	/**
	 * Returns the limitDuration.
	 * @return int
	 */
	public int getLimitDuration() {
		return limitDuration;
	}

	/**
	 * Returns the limitNumber.
	 * @return int
	 */
	public int getLimitNumber() {
		return limitNumber;
	}

	/**
	 * Returns the lowLimit.
	 * @return int
	 */
	public int getLowLimit() {
		return lowLimit;
	}

	/**
	 * Returns the pointID.
	 * @return int
	 */
	public int getPointID() {
		return pointID;
	}

	/**
	 * Sets the highLimit.
	 * @param highLimit The highLimit to set
	 */
	public void setHighLimit(int highLimit) {
		this.highLimit = highLimit;
	}

	/**
	 * Sets the limitDuration.
	 * @param limitDuration The limitDuration to set
	 */
	public void setLimitDuration(int limitDuration) {
		this.limitDuration = limitDuration;
	}

	/**
	 * Sets the limitNumber.
	 * @param limitNumber The limitNumber to set
	 */
	public void setLimitNumber(int limitNumber) {
		this.limitNumber = limitNumber;
	}

	/**
	 * Sets the lowLimit.
	 * @param lowLimit The lowLimit to set
	 */
	public void setLowLimit(int lowLimit) {
		this.lowLimit = lowLimit;
	}

	/**
	 * Sets the pointID.
	 * @param pointID The pointID to set
	 */
	public void setPointID(int pointID) {
		this.pointID = pointID;
	}

}
