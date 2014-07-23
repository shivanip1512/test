package com.cannontech.billing.mvrsformatsegments;

/**
 * Insert the type's description here.
 * Creation date: (5/19/00 9:33:05 AM)
 * @author: 
 */
public class FileHeaderRecordAndTrailer {
	public String recordID;
	public String newServiceIndicator;
	public String changeMeterIndicator;
	public String numberRoutes;
	public String numberCustomers;
	public String numberMeters;
	public String numberReads;
	public String extendedRouteHeaderFlag;
	
	public String crlf;
/**
 * FileHeaderRecordAndTrailer constructor comment.
 */
public FileHeaderRecordAndTrailer() {
	super();
}
}
