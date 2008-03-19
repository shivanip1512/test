package com.cannontech.yukon.cbc;

import java.util.Vector;

public class CCSubStations extends com.cannontech.yukon.cbc.CapControlMessage
{
	public static final int STATION_ALL    = 0x00000001;
	public static final int STATION_DEL    = 0x00000002;
	public static final int STATION_ADD    = 0x00000004;
	public static final int STATION_UPDATE = 0x00000008;

	private Vector stations;
	private Integer msgInfoBitMask = new Integer(0);
	
	public CCSubStations() {
		super();
	}
	
	public boolean isAllSubStations()
	{
	    return (getMsgInfoBitMask().intValue() & STATION_ALL    ) > 0;
	}
	public boolean isAddSubStations(){
		return (getMsgInfoBitMask().intValue() & STATION_ADD    ) > 0;
	}
	public boolean isUpdateSubStations(){
		return (getMsgInfoBitMask().intValue() & STATION_UPDATE ) > 0;		
	}
	/**
	 * This method was created in VisualAge.
	 * @return int
	 */
	public int getNumberOfStations() 
	{
		return stations.size();
	}
	/**
	 * This method was created in VisualAge.
	 */
	public SubStation getSubAt(int index) 
	{
		if( stations == null || index < 0 || index >= stations.size() )
			return null;
		else
			return (SubStation)stations.get(index);
	}
	
	/**
	 * Only used to filter the unwanted SubBuses out
	 */
	public void removeSubAt(int index) 
	{
	    if( stations != null && index >= 0 && index < stations.size() )
	        stations.removeElementAt( index );
	}
	
	/**
	 * This method was created in VisualAge.
	 */
	void setSubStations(java.util.Vector subbuses)
	{
		stations = subbuses;
	}
	/**
	 * @return
	 */
	public java.lang.Integer getMsgInfoBitMask() {
		return msgInfoBitMask ;
	}

	/**
	 * @param integer
	 */
	public void setMsgInfoBitMask(java.lang.Integer integer) {
		msgInfoBitMask = integer;
	}
	
}
