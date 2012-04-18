package com.cannontech.capcontrol.creation.model;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum CbcImportResultType implements DatabaseRepresentationSource {
	SUCCESS("Success", 0),
	INVALID_PARENT("Invalid Parent Name", 1),
	INVALID_TYPE("Invalid Type", 2),
	INVALID_IMPORT_ACTION("Invalid Import Action", 3),
	MISSING_DATA("Missing Data", 6),
	OBJECT_EXISTS("Object Already Exists", 7),
	NO_SUCH_OBJECT("Object Doesn't Exist", 8),
	INVALID_COMM_CHANNEL("Invalid Comm Channel", 9),
	INVALID_SERIAL_NUMBER("Invalid Serial Number", 10),
	;
	
	private final String dbString;
	private final int errorCode;

	private CbcImportResultType(String dbString, int errorCode) {
		this.dbString = dbString;
		this.errorCode = errorCode;
	}

	public Boolean isSuccess() {
		return this == SUCCESS;
	}
	
	@Override
	public Object getDatabaseRepresentation() {
		return dbString;
	}
	
	public String getDbString() {
		return dbString;
	}

    public int getErrorCode() {
        return errorCode;
    }
}
