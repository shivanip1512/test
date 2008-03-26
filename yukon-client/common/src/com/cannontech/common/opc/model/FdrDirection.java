package com.cannontech.common.opc.model;

/* for control options are not here because of the space. 
 * If we need to support those styles, we need to remove the spaces in the database*/
public enum FdrDirection {
	Receive("Receive"),
	Send("Send"),
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
