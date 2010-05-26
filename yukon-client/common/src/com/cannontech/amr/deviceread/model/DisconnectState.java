package com.cannontech.amr.deviceread.model;

public enum DisconnectState {
	
	CONFIRMED_DISCONNECTED(0, SimpleDisconnectState.DISCONNECTED),
	CONNECTED(1, SimpleDisconnectState.CONNECTED),
	UNCONFIRMED_DISCONNECTED(2, SimpleDisconnectState.DISCONNECTED),
	CONNECT_ARMED(3, SimpleDisconnectState.CONNECTED),
	UNKNOWN(null, SimpleDisconnectState.UNKNOWN);
	
	private Integer rawState;
	private SimpleDisconnectState simpleState;
		
	DisconnectState(Integer rawState, SimpleDisconnectState simpleState) {
		this.rawState = rawState;
		this.simpleState = simpleState;
	}
	
	public enum SimpleDisconnectState {
		CONNECTED, DISCONNECTED, UNKNOWN;
	}
	
	public Integer getRawState() {
		return rawState;
	}
	
	public SimpleDisconnectState getSimpleDisconnectState() {
		return simpleState;
	}
	
	public static DisconnectState getDisconneectStateForRawState(double rawState) {

		for (DisconnectState disconnectState : DisconnectState.values()) {
			
			Integer disconnectStateRawState = disconnectState.getRawState();
			if (disconnectStateRawState != null && ((int)rawState) == disconnectState.getRawState()) {
				return disconnectState;
			}
		}
		
		return UNKNOWN;
	}
}
