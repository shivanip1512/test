package com.cannontech.common.device.commands;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.spring.YukonSpringHook;

public class CommandDateFormatFactory {

    public static SimpleDateFormat createPeakReportCommandDateFormatter() {
        SystemDateFormattingService systemDateFormattingService = (SystemDateFormattingService) YukonSpringHook.getBean("systemDateFormattingService");
        SimpleDateFormat dateFormat = systemDateFormattingService.getSystemDateFormat("MM/dd/yyyy");
        return dateFormat;
    }
    
    public static SimpleDateFormat createLoadProfileCommandDateFormatter() {
        SystemDateFormattingService systemDateFormattingService = (SystemDateFormattingService) YukonSpringHook.getBean("systemDateFormattingService");
        SimpleDateFormat dateFormat = systemDateFormattingService.getSystemDateFormat("MM/dd/yyyy HH:mm");
        return dateFormat;
    }
    
    public static Calendar createServerCalendar() {
        return Calendar.getInstance();
	}
}