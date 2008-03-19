package com.cannontech.yukon.cbc;

import java.util.Vector;

/**
 */


public class CCSubstationBuses extends com.cannontech.yukon.cbc.CapControlMessage {
	
	private java.lang.Integer msgInfoBitMask = new Integer(0);
	private java.util.Vector<SubBus> buses;
	
	
	public static final int SUB_ALL    = 0x00000001;
	public static final int SUB_DEL    = 0x00000002;
	public static final int SUB_ADD    = 0x00000004;
	public static final int SUB_UPDATE = 0x00000008;
	
	/**
	 * CBCSubstationBuses constructor comment.
	 */
	public CCSubstationBuses() {
		super();
	}
	
	public boolean isAllSubs()
	{
	    return (getMsgInfoBitMask().intValue() & SUB_ALL    ) > 0;
	}
	public boolean isAddSub(){
		return (getMsgInfoBitMask().intValue() & SUB_ADD    ) > 0;
	}
	public boolean isUpdateSub(){
		return (getMsgInfoBitMask().intValue() & SUB_UPDATE ) > 0;		
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

        return buses.get(index);
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
	void setSubBuses(Vector<SubBus> subbuses)
	{
		buses = subbuses;
	}
    
    public Vector<SubBus> getSubBuses() {
        return buses;
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
