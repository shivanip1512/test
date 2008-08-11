package com.cannontech.message.capcontrol;

public enum CapControlResponseType {
	
	CommandRefused(CapControlServerResponse.COMMANDREFUSED_ID);
	
	private int typeId;
	
	private CapControlResponseType(int id) {
		typeId = id;
	}
	
	public int getTypeId() {
		return typeId;
	}
	
	public static CapControlResponseType getResponseTypeById(int id) throws IllegalArgumentException{
	    
	    for(CapControlResponseType type : CapControlResponseType.values()) {
	        if(type.getTypeId() == id) {
	            return type;
	        }
	    }
	    throw new IllegalArgumentException(" Unsupported ResponseType: " + id);
	}
}
