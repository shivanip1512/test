package com.cannontech.loadcontrol.data;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:06:09 PM)
 * @author: 
 */
public abstract class LMGroupBase implements ILMData
{
	//Posible states a group may be in
	public static final int STATE_INACTIVE = 0;
	public static final int STATE_ACTIVE = 1;
	public static final int STATE_INACTIVE_PENDING = 2;
	public static final int STATE_ACTIVE_PENDING = 3;


	//Possible state strings
	public static final String[] CURRENT_STATES =
	{
		"Inactive",
		"Active",
		"Inactive Pending",
		"Active Pending"
	};
	
	private Integer yukonID = null;
	private String yukonCategory = null;
	private String yukonClass = null;
	private String yukonName = null;
	private Integer yukonType = null;
	private String yukonDescription = null;
	private Boolean disableFlag = null;
	private Double kwCapacity = null;
	private Integer groupOrder = null;


	/**
	 * Insert the method's description here.
	 * Creation date: (7/31/2001 2:34:50 PM)
	 * @return boolean
	 * @param o java.lang.Object
	 */
	public boolean equals(Object o) 
	{
		return ( (o != null) &&
				   (o instanceof LMGroupBase) &&
				   ( ((LMGroupBase)o).getYukonID().intValue() == getYukonID().intValue()) );
	}
	
	public int hashCode() {
		return getYukonID().intValue();
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (4/16/2001 11:03:32 AM)
	 * @return java.lang.String
	 * @param state int
	 */
	protected static String getCurrentStateString(int state) 
	{
		switch( state )
		{
			case STATE_INACTIVE:
			return CURRENT_STATES[STATE_INACTIVE];
			
			case STATE_ACTIVE:
			return CURRENT_STATES[STATE_ACTIVE];
	
			case STATE_ACTIVE_PENDING:
			return CURRENT_STATES[STATE_ACTIVE_PENDING];
	
			case STATE_INACTIVE_PENDING:
			return CURRENT_STATES[STATE_INACTIVE_PENDING];
	
			default:
			throw new RuntimeException("*** Unknown state(" + state + ") in getCurrentStateString(int) in : " + LMGroupBase.class.getName() );
		}
		
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/3/2001 10:57:58 AM)
	 * @return java.lang.Boolean
	 */
	public java.lang.Boolean getDisableFlag() {
		return disableFlag;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 8:56:31 AM)
	 * @return java.lang.String
	 */
	/* Override this method if you want to use it */
	public abstract String getGroupControlStateString();
	/**
	 * Insert the method's description here.
	 * Creation date: (4/3/2001 10:57:58 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getGroupOrder() {
		return groupOrder;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 8:56:31 AM)
	 * @return java.lang.String
	 */
	/* Override this method if you want to use it */
	public abstract java.util.Date getGroupTime();
	/**
	 * Insert the method's description here.
	 * Creation date: (4/3/2001 10:57:58 AM)
	 * @return java.lang.Double
	 */
	public java.lang.Double getKwCapacity() {
		return kwCapacity;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 8:56:31 AM)
	 * @return java.lang.String
	 */
	public abstract String getStatistics();
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getYukonCategory() {
		return yukonCategory;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getYukonClass() {
		return yukonClass;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getYukonDescription() {
		return yukonDescription;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getYukonID() {
		return yukonID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getYukonName() {
		return yukonName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getYukonType() {
		return yukonType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/3/2001 10:57:58 AM)
	 * @param newDisableFlag java.lang.Boolean
	 */
	public void setDisableFlag(java.lang.Boolean newDisableFlag) {
		disableFlag = newDisableFlag;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/3/2001 10:57:58 AM)
	 * @param newGroupOrder java.lang.Integer
	 */
	public void setGroupOrder(java.lang.Integer newGroupOrder) {
		groupOrder = newGroupOrder;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/3/2001 10:57:58 AM)
	 * @param newKwCapacity java.lang.Double
	 */
	public void setKwCapacity(java.lang.Double newKwCapacity) {
		kwCapacity = newKwCapacity;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @param newYukonCategory java.lang.String
	 */
	public void setYukonCategory(java.lang.String newYukonCategory) {
		yukonCategory = newYukonCategory;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @param newYukonClass java.lang.String
	 */
	public void setYukonClass(java.lang.String newYukonClass) {
		yukonClass = newYukonClass;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @param newYukonDescription java.lang.String
	 */
	public void setYukonDescription(java.lang.String newYukonDescription) {
		yukonDescription = newYukonDescription;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @param newYukonID java.lang.Integer
	 */
	public void setYukonID(java.lang.Integer newYukonID) {
		yukonID = newYukonID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @param newYukonName java.lang.String
	 */
	public void setYukonName(java.lang.String newYukonName) {
		yukonName = newYukonName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @param newYukonType java.lang.Integer
	 */
	public void setYukonType(java.lang.Integer newYukonType) {
		yukonType = newYukonType;
	}

}
