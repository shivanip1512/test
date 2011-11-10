package com.cannontech.message.capcontrol.model;

import java.util.Vector;

import com.cannontech.message.capcontrol.streamable.SubBus;

public class SubstationBuses extends CapControlMessage {
	
	private int msgInfoBitMask;
	private Vector<SubBus> buses;
	public static final int SUB_ALL    = 0x00000001;
	public static final int SUB_DEL    = 0x00000002;
	public static final int SUB_ADD    = 0x00000004;
	public static final int SUB_UPDATE = 0x00000008;
	
	public SubstationBuses() {
		super();
	}
	
	public boolean isAllSubs() {
	    return (getMsgInfoBitMask() & SUB_ALL) > 0;
	}
	
	public boolean isAddSub() {
		return (getMsgInfoBitMask() & SUB_ADD) > 0;
	}
	
	public boolean isUpdateSub() {
		return (getMsgInfoBitMask() & SUB_UPDATE) > 0;		
	}
	
	public int getNumberOfBuses() {
		return buses.size();
	}
	
	public SubBus getSubBusAt(int index) {
		if (buses == null || index < 0 || index >= buses.size()) {
			return null;
		}
        return buses.get(index);
	}
	
	/**
	 * Only used to filter the unwanted SubBuses out
	 */
	public void removeSubBusAt(int index) {
	    if (buses != null && index >= 0 && index < buses.size()) {
	        buses.removeElementAt( index );
	    }
	}
	
	public void setSubBuses(Vector<SubBus> subbuses) {
		buses = subbuses;
	}
    
    public Vector<SubBus> getSubBuses() {
        return buses;
    }
    
	public int getMsgInfoBitMask() {
		return msgInfoBitMask;
	}

	public void setMsgInfoBitMask(int msgInfoBitMask) {
		this.msgInfoBitMask = msgInfoBitMask;
	}

}