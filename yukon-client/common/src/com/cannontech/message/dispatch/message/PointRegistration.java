package com.cannontech.message.dispatch.message;

/**
 * This type was created in VisualAge.
 */

public class PointRegistration extends com.cannontech.message.util.Message {

	public static final int REG_NOTHING = 0x00000000;
	public static final int REG_ALL_STATUS = 0x00000001;
	public static final int REG_ALL_ANALOG = 0x00000002;
	public static final int REG_ALL_ACCUMULATOR = 0x00000004;
	public static final int REG_ALL__CALCULATED = 0x00000008;
	public static final int REG_EVENTS = 0x00000010;
	public static final int REG_ALARMS = 0x00000020;	
	public static final int REG_ALL_PTS_MASK = 0x0000000f;
	public static final int REG_LOAD_PROFILE = 0x00000040;
	public static final int REG_NO_UPLOAD = 0x00010000;

	private int regFlags;
	private com.roguewave.tools.v2_0.Slist pointList;
/**
 * PointRegistration constructor comment.
 */
public PointRegistration() {
	super();
}
/**
 * This method was created in VisualAge.
 * @return com.roguewave.tools.v2_0.Slist
 */
public com.roguewave.tools.v2_0.Slist getPointList() {
	return pointList;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getRegFlags() {
	return regFlags;
}
/**
 * This method was created in VisualAge.
 * @param newValue com.roguewave.tools.v2_0.Slist
 */
public void setPointList(com.roguewave.tools.v2_0.Slist newValue) {
	this.pointList = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setRegFlags(int newValue) {
	this.regFlags = newValue;
}
}
