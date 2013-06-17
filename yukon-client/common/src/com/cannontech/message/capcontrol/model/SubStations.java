package com.cannontech.message.capcontrol.model;

import java.util.Vector;

import com.cannontech.message.capcontrol.streamable.SubStation;

public class SubStations extends CapControlMessage {
    
	public static final int STATION_ALL    = 0x00000001;
	public static final int STATION_DEL    = 0x00000002;
	public static final int STATION_ADD    = 0x00000004;
	public static final int STATION_UPDATE = 0x00000008;

	private Vector<SubStation> stations;
	private int msgInfoBitMask;
	
	public SubStations() {
		super();
	}
	
	public boolean isAllSubStations() {
	    return (getMsgInfoBitMask() & STATION_ALL) > 0;
	}
	
	public boolean isAddSubStations() {
		return (getMsgInfoBitMask() & STATION_ADD) > 0;
	}
	
	public boolean isUpdateSubStations(){
		return (getMsgInfoBitMask() & STATION_UPDATE) > 0;		
	}
	
	public int getNumberOfStations() {
		return stations.size();
	}
	
	public SubStation getSubAt(int index) {
		if(stations == null || index < 0 || index >= stations.size()) {
			return null;
		} else {
			return stations.get(index);
		}
	}
	
	/**
	 * Only used to filter the unwanted SubStations out
	 */
	public void removeSubAt(int index) {
	    if (stations != null && index >= 0 && index < stations.size()) {
	        stations.removeElementAt(index);
	    }
	}
	
	public void setSubStations(Vector<SubStation> stations) {
		this.stations = stations;
	}
	
	public int getMsgInfoBitMask() {
		return msgInfoBitMask ;
	}

	public void setMsgInfoBitMask(int msgInfoBitMask) {
		this.msgInfoBitMask = msgInfoBitMask;
	}
	
}