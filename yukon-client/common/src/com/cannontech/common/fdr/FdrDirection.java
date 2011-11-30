package com.cannontech.common.fdr;


public enum FdrDirection {
	Receive("Receive"),
	ReceiveForControl("Receive for control"),
	ReceiveForAnalogOutput("Receive for Analog Output"),
	Send("Send"),
	SendForControl("Send for control"),
	LinkStatus("Link Status");
	
	private final String dbName;
	
	FdrDirection ( String dbName ) {
	    this.dbName = dbName;
	}
	
	public String getValue() {
	    return dbName;
	}
	
	public static FdrDirection getEnum(String name) {
	    FdrDirection ret = null;
	    for( FdrDirection dir : FdrDirection.values() ) {
	        if( dir.getValue().equalsIgnoreCase(name)) {
	            ret = dir;
	            break;
	        }
	    }
	    return ret;
	}
}
