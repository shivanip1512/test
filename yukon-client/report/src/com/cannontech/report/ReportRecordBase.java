package com.cannontech.report;

import java.text.SimpleDateFormat;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:31:18 PM)
 * @author: 
 */
public interface ReportRecordBase {
    //Date formatter (used in pageHeaders)
    public SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    
/**
 * dataToString method comment.
 */
public abstract String dataToString();
}
