package com.cannontech.message.capcontrol;

import javax.activation.UnsupportedDataTypeException;


public enum CapControlResponseType {
	
	CommandRefused(CapControlServerResponse.COMMANDREFUSED_ID);
	
	private int typeId;
	
	private CapControlResponseType(int id) {
		typeId = id;
	}
	
	public int getTypeId() {
		return typeId;
	}
	
	public static CapControlResponseType getResponseTypeById(int id) throws UnsupportedDataTypeException{
		switch(id) {
			case CapControlServerResponse.COMMANDREFUSED_ID: 
				return CommandRefused;
			default:
				break;
		}
		throw new UnsupportedDataTypeException(" Unsupported ResponseType: " + id);
	}
}
