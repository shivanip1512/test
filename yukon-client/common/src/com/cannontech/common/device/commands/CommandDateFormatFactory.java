package com.cannontech.common.device.commands;

import java.text.SimpleDateFormat;


public class CommandDateFormatFactory {

    public static SimpleDateFormat createPeakReportCommandDateFormatter() {
        return new SimpleDateFormat("MM/dd/yyyy");
    }
    
    public static SimpleDateFormat createLoadProfileCommandDateFormatter() {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm");
    }
}
