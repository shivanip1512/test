/*
 * Created on Nov 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.xml.serialize.CurrentState;
import com.cannontech.stars.xml.serialize.ServiceCompany;
import com.cannontech.stars.xml.serialize.ServiceType;
import com.cannontech.stars.xml.serialize.StarsSrvReq;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkOrderManagerUtil {

	public static final String STARS_WORK_ORDER_OPER_REQ = "STARS_WORK_ORDER_OPERATION_REQUEST";
	public static final String WORK_ORDER_SET = "WORK_ORDER_SET";
	public static final String WORK_ORDER_SET_DESC = "WORK_ORDER_SET_DESC";
	
	public static void setStarsServiceRequest(StarsSrvReq starsOrder, HttpServletRequest req, java.util.TimeZone tz)
		throws WebClientException
	{
		if (req.getParameter("OrderID") != null)
			starsOrder.setOrderID( Integer.parseInt(req.getParameter("OrderID")) );
		
		int accountId = ServletRequestUtils.getIntParameter(req,
                                                                    "AccountId",
                                                                    0);
		if (accountId > 0)
			starsOrder.setAccountID(accountId);
		if (req.getParameter("OrderNo") != null)
			starsOrder.setOrderNumber( req.getParameter("OrderNo") );
		if (req.getParameter("AddtlOrderNumber") != null)
			starsOrder.setAddtlOrderNumber( req.getParameter("AddtlOrderNumber") );		
		if (req.getParameter("ActionTaken") != null)
			starsOrder.setActionTaken( req.getParameter("ActionTaken").replaceAll(System.getProperty("line.separator"), "<br>") );
		starsOrder.setOrderedBy( req.getParameter("OrderedBy") );
		starsOrder.setDescription( req.getParameter("Description").replaceAll(System.getProperty("line.separator"), "<br>") );
			
		ServiceType servType = new ServiceType();
		servType.setEntryID( Integer.parseInt(req.getParameter("ServiceType")) );
		starsOrder.setServiceType( servType );
			
		ServiceCompany company = new ServiceCompany();
		if (req.getParameter("ServiceCompany") != null ) 
			company.setEntryID( Integer.parseInt(req.getParameter("ServiceCompany") ) );
		else
			company.setEntryID( com.cannontech.stars.database.db.report.ServiceCompany.NONE_INT );
		starsOrder.setServiceCompany( company );
		
		if (req.getParameter("CurrentState") != null) {
			CurrentState status = new CurrentState();
			status.setEntryID( Integer.parseInt(req.getParameter("CurrentState")) );
			starsOrder.setCurrentState( status );
		}
		
		//TODO Currently using the DateReported field to hold the current state's date.  This should be changed since this field is deprecated. 
		if (req.getParameter("DateEventTimestamp")!= null && req.getParameter("DateEventTimestamp").length() > 0) {
			Date dateReported = parseDateTime(
					req.getParameter("DateEventTimestamp"), req.getParameter("TimeEventTimestamp"), tz );
			if (dateReported == null)
				throw new WebClientException("Invalid report date format '" + req.getParameter("DateEventTimestamp") + " " + req.getParameter("TimeEventTimestamp") + "', the date/time should be in the form of 'mm/dd/yy hh:mm'");
			starsOrder.setDateReported( dateReported );
		}
		
		/*if (req.getParameter("DateReported").length() > 0) {
			Date dateReported = ServletUtils.parseDateTime(
					req.getParameter("DateReported"), req.getParameter("TimeReported"), tz );
			if (dateReported == null)
				throw new WebClientException("Invalid report date format '" + req.getParameter("DateReported") + " " + req.getParameter("TimeReported") + "', the date/time should be in the form of 'mm/dd/yy hh:mm'");
			starsOrder.setDateReported( dateReported );
		}
		
		if (req.getParameter("DateScheduled") != null && req.getParameter("DateScheduled").length() > 0) {
			Date dateScheduled = ServletUtils.parseDateTime(
					req.getParameter("DateScheduled"), req.getParameter("TimeScheduled"), tz );
			if (dateScheduled == null)
				throw new WebClientException("Invalid scheduled date format '" + req.getParameter("DateScheduled") + " " + req.getParameter("TimeScheduled") + "', the date/time should be in the form of 'mm/dd/yy hh:mm'");
			starsOrder.setDateScheduled( dateScheduled );
		}
		
		if (req.getParameter("DateCompleted") != null && req.getParameter("DateCompleted").length() > 0) {
			Date dateCompleted = ServletUtils.parseDateTime(
					req.getParameter("DateCompleted"), req.getParameter("TimeCompleted"), tz );
			if (dateCompleted == null)
				throw new WebClientException("Invalid close date format '" + req.getParameter("DateCompleted") + " " + req.getParameter("TimeCompleted") + "', the date/time should be in the form of 'mm/dd/yy hh:mm'");
			starsOrder.setDateCompleted( dateCompleted );
		}*/
	}
	
	   /**
     * Parse date/time in the format of MM/dd/yyyy HH:mm,
     * MM/dd/yyyy hh:mm a, etc., in the specified time zone
     */
    private static Date parseDateTime(String dateStr, String timeStr, TimeZone tz) {
        Date datePart = com.cannontech.util.ServletUtil.parseDateStringLiberally( dateStr, tz );
        if (datePart == null) return null;
        
        Date timePart = parseTime( timeStr, tz );
        if (timePart == null) return null;
        
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime( datePart );
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime( timePart );
        
        dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
        
        return dateCal.getTime();
    }
    
    /**
     * Parse time in the specified time zone 
     */
    private static Date parseTime(String timeStr, TimeZone tz) {
        Date retVal = null;
        final java.text.SimpleDateFormat[] timeFormat = {
            new java.text.SimpleDateFormat("hh:mm a"),
            new java.text.SimpleDateFormat("hh:mma"),
            new java.text.SimpleDateFormat("HH:mm"),
        };

        for( int i = 0; i < timeFormat.length; i++ ) {
            try {
                timeFormat[i].setTimeZone(tz);
                retVal = timeFormat[i].parse(timeStr);
                break;
            }
            catch( java.text.ParseException pe ) {}
        }
        
        return retVal;  
    }
}
