package com.cannontech.stars.web.servlet;

import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDefaultThermostatSettings;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
import com.cannontech.stars.xml.serialize.StarsThermostatSchedule;
import com.cannontech.stars.xml.serialize.StarsThermostatSeason;
import com.cannontech.stars.xml.serialize.StarsThermostatSettings;
import com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings;
import com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings;

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
        if (session == null) resp.sendRedirect(req.getContextPath() + loginURL);

		StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		if (user == null) {
			resp.sendRedirect(req.getContextPath() + loginURL); return;
		}
		
		String nextURL = req.getParameter( ServletUtils.ATT_REFERRER );
		
		StarsGetEnergyCompanySettingsResponse ecSettings = (StarsGetEnergyCompanySettingsResponse)
				user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
        StarsDefaultThermostatSettings dftThermSettings = ecSettings.getStarsDefaultThermostatSettings();
		
        StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute(
        		ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        StarsThermostatSettings thermSettings = accountInfo.getStarsThermostatSettings();
        
        StarsThermoModeSettings mode = StarsThermoModeSettings.valueOf( req.getParameter("mode") );
        StarsThermoDaySettings day = StarsThermoDaySettings.valueOf( req.getParameter("day") );
        
		String action = req.getParameter("action");
        try {
	        StarsThermostatSeason season = null;
	        for (int i = 0; i < thermSettings.getStarsThermostatSeasonCount(); i++)
	        	if (thermSettings.getStarsThermostatSeason(i).getMode().getType() == mode.getType()) {
	        		season = thermSettings.getStarsThermostatSeason(i);
	        		break;
	        	}
	        if (season == null) {
	        	season = new StarsThermostatSeason();
	        	season.setMode( mode );
	        	for (int j = 0; j < dftThermSettings.getStarsThermostatSeasonCount(); j++)
	        		if (dftThermSettings.getStarsThermostatSeason(j).getMode().getType() == mode.getType()) {
	        			season.setStartDate( dftThermSettings.getStarsThermostatSeason(j).getStartDate() );
	        			break;
	        		}
/*	        	if (mode.getType() == StarsThermoModeSettings.COOL_TYPE)
	        		season.setStartDate( new org.exolab.castor.types.Date(com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("SummerStartDate"))) );
	        	else
			    	season.setStartDate( new org.exolab.castor.types.Date(com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("WinterStartDate"))) );*/
	        	thermSettings.addStarsThermostatSeason( season );
	        }
	        
	        StarsThermostatSchedule schedule = null;
	        for (int i = 0; i < season.getStarsThermostatScheduleCount(); i++)
	        	if (season.getStarsThermostatSchedule(i).getDay().getType() == day.getType()) {
	        		schedule = season.getStarsThermostatSchedule(i);
	        		break;
	        	}
	        if (schedule == null) {
	        	schedule = new StarsThermostatSchedule();
		        schedule.setDay( day );
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
	        
	        ArrayList changedSchedules = (ArrayList) user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CHANGED_THERMOSTAT_SETTINGS );
	        if (changedSchedules == null) {
	        	changedSchedules = new ArrayList();
	        	user.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CHANGED_THERMOSTAT_SETTINGS, changedSchedules );
	        }

			boolean applyToWeekend = false;
			String applyToWeekendStr = req.getParameter( "ApplyToWeekend" );
			if (day.getType() == StarsThermoDaySettings.WEEKDAY_TYPE) {
				if (applyToWeekendStr != null) {	// Checkbox "ApplyToWeekend" is checked
					applyToWeekend = true;
					user.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_APPLY_TO_WEEKEND, "checked" );
				}
				else	// Checkbox "ApplyToWeekend" is unchecked
					user.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_APPLY_TO_WEEKEND, "" );
			}
			
			if (applyToWeekend) {
				for (int i = season.getStarsThermostatScheduleCount() - 1; i >= 0; i--)
					changedSchedules.remove( season.removeStarsThermostatSchedule(i) );
				
				season.addStarsThermostatSchedule( schedule );
				changedSchedules.add( schedule );
				
				StarsThermostatSchedule sched = StarsFactory.newStarsThermostatSchedule( schedule );
				sched.setDay( StarsThermoDaySettings.SATURDAY );
				season.addStarsThermostatSchedule( sched );
				changedSchedules.add( sched );
				
				sched = StarsFactory.newStarsThermostatSchedule( schedule );
				sched.setDay( StarsThermoDaySettings.SUNDAY );
				season.addStarsThermostatSchedule( sched );
				changedSchedules.add( sched );
			}
			else if (action.equalsIgnoreCase( "SaveScheduleChanges" )) {
		        if (!changedSchedules.contains( schedule ))
		        	changedSchedules.add( schedule );
			}
		    
		    if (req.getParameter(ServletUtils.ATT_REDIRECT).equalsIgnoreCase( req.getParameter(ServletUtils.ATT_REFERRER) )) {	// Submit button clicked
		    	session.setAttribute( ServletUtils.ATT_REFERRER, req.getParameter(ServletUtils.ATT_REFERRER) );
		    	nextURL = "/servlet/SOAPClient?action=UpdateThermostatSchedule";
		    }
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
