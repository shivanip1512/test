package com.cannontech.cbc.messages;

/**
 */

import com.cannontech.cbc.data.SubBus;

public class CBCSubstationBuses extends com.cannontech.cbc.messages.CBCMessage {
	
	private java.lang.Integer msgInfoBitMask = new Integer(0);
	private java.util.Vector buses;
	
	
	public static final int SUB_ALL = 0x00000001;
	public static final int SUB_DELETE = 0x00000002;
	
	
/**
 * CBCSubstationBuses constructor comment.
 */
public CBCSubstationBuses() {
	super();
}

public boolean isSubDeleted()
{
	return (getMsgInfoBitMask().intValue() & SUB_DELETE) > 0;
}

/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumberOfBuses() 
{
	return buses.size();
}
/**
 * This method was created in VisualAge.
 */
public SubBus getSubBusAt(int index) 
{
	if( buses == null || index < 0 || index >= buses.size() )
		return null;
	else
		return (SubBus)buses.get(index);
}

/**
 * Only used to filter the unwanted SubBuses out
 */
public void removeSubBusAt(int index) 
{
    if( buses != null && index >= 0 && index < buses.size() )
        buses.removeElementAt( index );
}

/**
 * This method was created in VisualAge.
 */
void setSubBuses(java.util.Vector subbuses)
{
	buses = subbuses;
}
	/**
	 * @return
	 */
	public java.lang.Integer getMsgInfoBitMask() {
		return msgInfoBitMask;
	}

	/**
	 * @param integer
	 */
	public void setMsgInfoBitMask(java.lang.Integer integer) {
		msgInfoBitMask = integer;
	}

}
