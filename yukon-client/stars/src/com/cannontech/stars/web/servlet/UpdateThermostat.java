package com.cannontech.stars.web.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UpdateThermostat extends HttpServlet {

    private static final String loginURL = "/login.jsp";

    public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
        HttpSession session = req.getSession(false);
        if (session == null) resp.sendRedirect(loginURL);

		StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
		if (user == null) {
			resp.sendRedirect(loginURL); return;
		}
		
		String nextURL = req.getParameter( ServletUtils.ATT_REFERRER );
		
        StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute(
        		ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        StarsThermostatSettings thermSettings = accountInfo.getStarsThermostatSettings();
        
		StringTokenizer actions = new StringTokenizer( req.getParameter("action"), ":" );
        try {
			while (actions.hasMoreTokens()) {
				String action = actions.nextToken();
				
				if (action.equalsIgnoreCase( "SaveDateChanges" )) {
			        StarsThermostatSeason summer = null;
			        for (int i = 0; i < thermSettings.getStarsThermostatSeasonCount(); i++)
			        	if (thermSettings.getStarsThermostatSeason(i).getMode().getType() == StarsThermoModeSettings.COOL_TYPE) {
			        		summer = thermSettings.getStarsThermostatSeason(i);
			        		break;
			        	}
			        if (summer == null) {
			        	summer = new StarsThermostatSeason();
			        	summer.setMode( StarsThermoModeSettings.COOL );
			        	thermSettings.addStarsThermostatSeason( summer );
			        }
			    	summer.setStartDate( new org.exolab.castor.types.Date(com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("SummerStartDate"))) );
			        
			        StarsThermostatSeason winter = null;
			        for (int i = 0; i < thermSettings.getStarsThermostatSeasonCount(); i++)
			        	if (thermSettings.getStarsThermostatSeason(i).getMode().getType() == StarsThermoModeSettings.HEAT_TYPE) {
			        		winter = thermSettings.getStarsThermostatSeason(i);
			        		break;
			        	}
			        if (winter == null) {
			        	winter = new StarsThermostatSeason();
			        	winter.setMode( StarsThermoModeSettings.HEAT );
			        	thermSettings.addStarsThermostatSeason( winter );
			        }
			    	winter.setStartDate( new org.exolab.castor.types.Date(com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("WinterStartDate"))) );
				}
		        else if (action.equalsIgnoreCase( "SaveScheduleChanges" )) {
			        StarsThermostatSeason season = null;
			        for (int i = 0; i < thermSettings.getStarsThermostatSeasonCount(); i++)
			        	if (thermSettings.getStarsThermostatSeason(i).getMode().toString().equalsIgnoreCase( req.getParameter("mode") )) {
			        		season = thermSettings.getStarsThermostatSeason(i);
			        		break;
			        	}
			        if (season == null) {
			        	season = new StarsThermostatSeason();
			        	season.setMode( StarsThermoModeSettings.valueOf(req.getParameter("mode")) );
			        	if (season.getMode().getType() == StarsThermoModeSettings.COOL_TYPE)
			        		season.setStartDate( new org.exolab.castor.types.Date(com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("SummerStartDate"))) );
			        	else
					    	season.setStartDate( new org.exolab.castor.types.Date(com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("WinterStartDate"))) );
			        	thermSettings.addStarsThermostatSeason( season );
			        }
			        
			        StarsThermostatSchedule schedule = null;
			        for (int i = 0; i < season.getStarsThermostatScheduleCount(); i++)
			        	if (season.getStarsThermostatSchedule(i).getDay().toString().equalsIgnoreCase( req.getParameter("day") )) {
			        		schedule = season.getStarsThermostatSchedule(i);
			        		break;
			        	}
			        if (schedule == null) {
			        	schedule = new StarsThermostatSchedule();
				        schedule.setDay( StarsThermoDaySettings.valueOf(req.getParameter("day")) );
			        	season.addStarsThermostatSchedule( schedule );
			        }
			        
			        java.util.StringTokenizer st = new java.util.StringTokenizer( req.getParameter("time1"), ":" );
			        long time = (Integer.parseInt(st.nextToken()) * 3600 + Integer.parseInt(st.nextToken()) * 60) * 1000;
			        schedule.setTime1( new org.exolab.castor.types.Time(time) );
			        st = new java.util.StringTokenizer( req.getParameter("time2"), ":" );
			        time = (Integer.parseInt(st.nextToken()) * 3600 + Integer.parseInt(st.nextToken()) * 60) * 1000;
			        schedule.setTime2( new org.exolab.castor.types.Time(time) );
			        st = new java.util.StringTokenizer( req.getParameter("time3"), ":" );
			        time = (Integer.parseInt(st.nextToken()) * 3600 + Integer.parseInt(st.nextToken()) * 60) * 1000;
			        schedule.setTime3( new org.exolab.castor.types.Time(time) );
			        st = new java.util.StringTokenizer( req.getParameter("time4"), ":" );
			        time = (Integer.parseInt(st.nextToken()) * 3600 + Integer.parseInt(st.nextToken()) * 60) * 1000;
			        schedule.setTime4( new org.exolab.castor.types.Time(time) );
			        
			        boolean noscript = (req.getParameter("temp1") != null);
			        if (noscript) {
				        schedule.setTemperature1( Integer.parseInt(req.getParameter("temp1")) );
				        schedule.setTemperature2( Integer.parseInt(req.getParameter("temp2")) );
				        schedule.setTemperature3( Integer.parseInt(req.getParameter("temp3")) );
				        schedule.setTemperature4( Integer.parseInt(req.getParameter("temp4")) );
			        }
			        else {
				        schedule.setTemperature1( Integer.parseInt(req.getParameter("tempval1")) );
				        schedule.setTemperature2( Integer.parseInt(req.getParameter("tempval2")) );
				        schedule.setTemperature3( Integer.parseInt(req.getParameter("tempval3")) );
				        schedule.setTemperature4( Integer.parseInt(req.getParameter("tempval4")) );
			        }
			        
			        ArrayList changedSchedules = (ArrayList) user.getAttribute( ServletUtils.ATT_CHANGED_THERMOSTAT_SETTINGS );
			        if (changedSchedules == null) {
			        	changedSchedules = new ArrayList();
			        	user.setAttribute( ServletUtils.ATT_CHANGED_THERMOSTAT_SETTINGS, changedSchedules );
			        }
			        changedSchedules.add( schedule );
		        }
			}
		    
		    if (req.getParameter(ServletUtils.ATT_REDIRECT).equalsIgnoreCase( req.getParameter(ServletUtils.ATT_REFERRER) ))	// Submit button clicked
		    	nextURL = "/servlet/SOAPClient?action=UpdateThermostatSchedule"
		    			+ "&REFERRER=" + java.net.URLEncoder.encode( req.getParameter(ServletUtils.ATT_REFERRER) )
		    			+ "&REDIRECT=" + java.net.URLEncoder.encode( req.getParameter(ServletUtils.ATT_REDIRECT) );
		    else
	        	nextURL = req.getParameter( ServletUtils.ATT_REDIRECT );
        }
        catch (Exception e) {
        	e.printStackTrace();
        	session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
        }
        
        resp.sendRedirect( nextURL );
    }
}
