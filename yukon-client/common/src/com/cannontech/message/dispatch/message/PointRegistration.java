package com.cannontech.message.dispatch.message;

import java.util.Set;

/**
 * This type was created in VisualAge.
 */

public class PointRegistration extends com.cannontech.message.util.Message {

	public static final int REG_NOTHING = 0x00000000;
	public static final int REG_ALL_POINTS = 0x00000001;
	public static final int REG_EVENTS = 0x00000010;
	public static final int REG_ALARMS = 0x00000020;	
    public static final int REG_ADD_POINTS = 0x00000100;
	public static final int REG_NO_UPLOAD = 0x00010000;

	private int regFlags;
	private Set<Integer> pointIds;
    
/**
 * PointRegistration constructor comment.
 */
public PointRegistration() {
	super();
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getRegFlags() {
	return regFlags;
}

public void setPointIds(Set<Integer> pointIds) {
    this.pointIds = pointIds;
}

public Set<Integer> getPointIds() {
    return pointIds;
}

/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setRegFlags(int newValue) {
	this.regFlags = newValue;
}

@Override
public String toString() {
    return "PointRegistration [regFlags=" + regFlags + ", pointIds=" + pointIds + "]";
}
}
