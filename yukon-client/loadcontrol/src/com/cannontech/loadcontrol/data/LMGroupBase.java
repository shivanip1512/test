package com.cannontech.loadcontrol.data;

public abstract class LMGroupBase implements ILMData, Cloneable {
	
	// Posible states a group may be in
	public static final int STATE_INACTIVE = 0;
	public static final int STATE_ACTIVE = 1;
	public static final int STATE_INACTIVE_PENDING = 2;
	public static final int STATE_ACTIVE_PENDING = 3;

	// Possible state strings
	public static final String[] CURRENT_STATES = { "Inactive", "Active",
			"Inactive Pending", "Active Pending" };

	private Integer yukonID = null;
	private String yukonCategory = null;
	private String yukonClass = null;
	private String yukonName = null;
	private String yukonType = null;
	private String yukonDescription = null;
	private Boolean disableFlag = null;
	private Double kwCapacity = null;
	private Integer groupOrder = null;
	private Integer dailyOps = null;

	public LMGroupBase clone() {
		LMGroupBase clone;
		try {
			clone = (LMGroupBase) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		return clone;
	}

	public Integer getDailyOps() {
		return dailyOps;
	}

	public void setDailyOps(Integer dailyOps) {
		this.dailyOps = dailyOps;
	}

	public boolean equals(Object o) {
		return ((o != null) && (o instanceof LMGroupBase) && (((LMGroupBase) o)
				.getYukonID().intValue() == getYukonID().intValue()));
	}

	public int hashCode() {
		return getYukonID().intValue();
	}

	protected static String getCurrentStateString(int state) {
		switch (state) {
		case STATE_INACTIVE:
			return CURRENT_STATES[STATE_INACTIVE];

		case STATE_ACTIVE:
			return CURRENT_STATES[STATE_ACTIVE];

		case STATE_ACTIVE_PENDING:
			return CURRENT_STATES[STATE_ACTIVE_PENDING];

		case STATE_INACTIVE_PENDING:
			return CURRENT_STATES[STATE_INACTIVE_PENDING];

		default:
			throw new RuntimeException("*** Unknown state(" + state
					+ ") in getCurrentStateString(int) in : "
					+ LMGroupBase.class.getName());
		}

	}

	public java.lang.Boolean getDisableFlag() {
		return disableFlag;
	}

	/* Override this method if you want to use it */
	public abstract String getGroupControlStateString();

	public java.lang.Integer getGroupOrder() {
		return groupOrder;
	}

	/* Override this method if you want to use it */
	public abstract java.util.Date getGroupTime();

	public java.lang.Double getKwCapacity() {
		return kwCapacity;
	}

	public abstract String getStatistics();

	public java.lang.String getYukonCategory() {
		return yukonCategory;
	}

	public java.lang.String getYukonClass() {
		return yukonClass;
	}

	public java.lang.String getYukonDescription() {
		return yukonDescription;
	}

	public java.lang.Integer getYukonID() {
		return yukonID;
	}

	public java.lang.String getYukonName() {
		return yukonName;
	}

	public String getYukonType() {
		return yukonType;
	}

	public void setDisableFlag(java.lang.Boolean newDisableFlag) {
		disableFlag = newDisableFlag;
	}

	public void setGroupOrder(java.lang.Integer newGroupOrder) {
		groupOrder = newGroupOrder;
	}

	public void setKwCapacity(java.lang.Double newKwCapacity) {
		kwCapacity = newKwCapacity;
	}

	public void setYukonCategory(java.lang.String newYukonCategory) {
		yukonCategory = newYukonCategory;
	}

	public void setYukonClass(java.lang.String newYukonClass) {
		yukonClass = newYukonClass;
	}

	public void setYukonDescription(java.lang.String newYukonDescription) {
		yukonDescription = newYukonDescription;
	}

	public void setYukonID(java.lang.Integer newYukonID) {
		yukonID = newYukonID;
	}

	public void setYukonName(java.lang.String newYukonName) {
		yukonName = newYukonName;
	}

	public void setYukonType(String newYukonType) {
		yukonType = newYukonType;
	}

}
