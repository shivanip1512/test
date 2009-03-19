package com.cannontech.yukon.cbc;

import java.util.List;

public class CCSubAreas extends com.cannontech.yukon.cbc.CapControlMessage {	
	
	public static final int AREA_ALL    = 0x00000001;
	public static final int AREA_DEL    = 0x00000002;
	public static final int AREA_ADD    = 0x00000004;
	public static final int AREA_UPDATE = 0x00000008;
	
	private List<CCArea> areas;
	private Integer msgInfoBitMask = new Integer(0);

public Integer getMsgInfoBitMask() {
		return msgInfoBitMask;
	}

	public void setMsgInfoBitMask(Integer msgInfoBitMask) {
		this.msgInfoBitMask = msgInfoBitMask;
	}

/**
 * CBCSubAreaNames constructor comment.
 */
public CCSubAreas() {
	super();
}

/**
 * This method was created in VisualAge.
 */
public CCArea getArea(int index) 
{
	return getAreas().get(index);
}

/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 1:17:13 PM)
 * @return java.util.Vector
 */
public List<CCArea> getAreas() {
	return areas;
}

public boolean isAllAreas()
{
    return (getMsgInfoBitMask().intValue() & AREA_ALL    ) > 0;
}
public boolean isAddAreas(){
	return (getMsgInfoBitMask().intValue() & AREA_ADD    ) > 0;
}
public boolean isUpdateAreas(){
	return (getMsgInfoBitMask().intValue() & AREA_UPDATE ) > 0;		
}

/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumberOfAreas() {
	return areas.size();
}

/**
 * This method was created in VisualAge.
 */
public void setAreas(List<CCArea> a) {
	areas = a;
}

}