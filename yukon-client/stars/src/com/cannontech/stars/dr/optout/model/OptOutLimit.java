package com.cannontech.stars.dr.optout.model;

/**
 * Model object to represent an opt out limit
 */
public class OptOutLimit {

	private int limit = 0;
	private Integer startMonth;
	private Integer stopMonth;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * Starting month which this opt out limit applies (Limit applies to the first day of the
	 * month and forward)
	 * @return Month (1-12)
	 */
	public Integer getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(Integer startMonth) {
		this.startMonth = startMonth;
	}

	/**
	 * Month through which this opt out limit applies (Limit applies through the last day of
	 * the month)
	 * @return Month (1-12)
	 */
	public Integer getStopMonth() {
		return stopMonth;
	}

	public void setStopMonth(Integer stopMonth) {
		this.stopMonth = stopMonth;
	}

}
