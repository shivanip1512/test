package com.cannontech.database.data.lite;

/**
 * Lite representation of PointLimit
 * @author alauinger
 */
public class LitePointLimit extends LiteBase {
	private int pointID;
	private int limitNumber;
	private double highLimit;
	private double lowLimit;
	private int limitDuration;
	
	public LitePointLimit(int pointID) {
		setPointID(pointID);
		setLiteType(LiteTypes.POINT_LIMIT);
	}
	public LitePointLimit(int pointID, int limitNumber, double highLimit, double lowLimit, int limitDuration) {
		setPointID(pointID);
		setLimitNumber(limitNumber);
		setHighLimit(highLimit);
		setLowLimit(lowLimit);
		setLimitDuration(limitDuration);
		setLiteType(LiteTypes.POINT_LIMIT);
	}
	
	/**
	 * Returns the highLimit.
	 * @return double
	 */
	public double getHighLimit() {
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
	public double getLowLimit() {
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
	public void setHighLimit(double highLimit) {
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
	public void setLowLimit(double lowLimit) {
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
