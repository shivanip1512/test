package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
import com.cannontech.database.data.lite.stars.LiteLMHardwareBase;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeason;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeasonEntry;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsThermostatSettings;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.hardware.LMThermostatSeason;
import com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDefaultThermostatSettings;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMHardwareEvent;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsThermostatSchedule;
import com.cannontech.stars.xml.serialize.StarsThermostatSeason;
import com.cannontech.stars.xml.serialize.StarsThermostatSettings;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatSchedule;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatScheduleResponse;
import com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings;
import com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UpdateThermostatScheduleAction implements ActionBase {
	
	private static final java.text.SimpleDateFormat[] timeFormat =
	{
		new java.text.SimpleDateFormat("hh:mm a"),
		new java.text.SimpleDateFormat("HH:mm"),
	};

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) return null;
			
			StarsGetEnergyCompanySettingsResponse ecSettings = (StarsGetEnergyCompanySettingsResponse)
					user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
	        StarsDefaultThermostatSettings dftThermSettings = ecSettings.getStarsDefaultThermostatSettings();
            
            StarsUpdateThermostatSchedule updateSched = new StarsUpdateThermostatSchedule();
            updateSched.setInventoryID( Integer.parseInt(req.getParameter("invID")) );
	        
	        StarsThermoModeSettings mode = StarsThermoModeSettings.valueOf( req.getParameter("mode") );
	        StarsThermoDaySettings day = StarsThermoDaySettings.valueOf( req.getParameter("day") );
/*            
            // Send only those schedules that have been changed
            ArrayList changedSchedules = (ArrayList) user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CHANGED_THERMOSTAT_SETTINGS );
            for (int i = 0; i < thermSettings.getStarsThermostatSeasonCount(); i++) {
            	StarsThermostatSeason season = thermSettings.getStarsThermostatSeason(i);
            	StarsThermostatSeason season2 = new StarsThermostatSeason();
            	season2.setMode( season.getMode() );
            	season2.setStartDate( season.getStartDate() );
            	for (int j = 0; j < season.getStarsThermostatScheduleCount(); j++) {
            		if (changedSchedules.contains( season.getStarsThermostatSchedule(j) ))
            			season2.addStarsThermostatSchedule( season.getStarsThermostatSchedule(j) );
            	}
            	
            	updateSched.addStarsThermostatSeason( season2 );
            }
*/
            StarsThermostatSeason season = new StarsThermostatSeason();
            season.setMode( mode );
            updateSched.addStarsThermostatSeason( season );
            StarsThermostatSchedule schedule = new StarsThermostatSchedule();
            schedule.setDay( day );
            season.addStarsThermostatSchedule( schedule );
	        
	        Calendar cal = Calendar.getInstance();
	        
	        cal.setTime( parseTime(req.getParameter("time1")) );
	        long time = (cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(cal.MINUTE) * 60) * 1000;
	        schedule.setTime1( new org.exolab.castor.types.Time(time) );
	        
	        cal.setTime( parseTime(req.getParameter("time2")) );
	        time = (cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(cal.MINUTE) * 60) * 1000;
	        schedule.setTime2( new org.exolab.castor.types.Time(time) );
	        
	        cal.setTime( parseTime(req.getParameter("time3")) );
	        time = (cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(cal.MINUTE) * 60) * 1000;
	        schedule.setTime3( new org.exolab.castor.types.Time(time) );
	        
	        cal.setTime( parseTime(req.getParameter("time4")) );
	        time = (cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(cal.MINUTE) * 60) * 1000;
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

			String applyToWeekendStr = req.getParameter( "ApplyToWeekend" );
			if (day.getType() == StarsThermoDaySettings.WEEKDAY_TYPE) {
				if (applyToWeekendStr != null) {	// Checkbox "ApplyToWeekend" is checked
					schedule.setDay( StarsThermoDaySettings.ALL );
					user.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_APPLY_TO_WEEKEND, "checked" );
				}
				else	// Checkbox "ApplyToWeekend" is unchecked
					user.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_APPLY_TO_WEEKEND, "" );
			}
            
            StarsOperation operation = new StarsOperation();
            operation.setStarsUpdateThermostatSchedule( updateSched );
            
            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            e.printStackTrace();
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
        }

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        StarsOperation respOper = new StarsOperation();
        
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
                respOper.setStarsFailure( StarsFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
                
			int energyCompanyID = user.getEnergyCompanyID();
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
			
			StarsUpdateThermostatSchedule updateSched = reqOper.getStarsUpdateThermostatSchedule();
    		StarsUpdateThermostatScheduleResponse resp = new StarsUpdateThermostatScheduleResponse();
    		resp.setInventoryID( updateSched.getInventoryID() );
    		
			LiteLMHardwareBase liteHw = energyCompany.getLMHardware( updateSched.getInventoryID(), true );
    		if (liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) {
    			if (ServerUtils.isOperator( user ))
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "The thermostat is currently out of service, schedule is not sent") );
    			else
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Your thermostat is currently out of service, schedule is not sent.<br>Please go to the \"Contact Us\" page if you want to contact our CSRs for further information.") );
            	return SOAPUtil.buildSOAPMessage( respOper );
    		}
    		if (liteHw.getManufactureSerialNumber().trim().length() == 0) {
    			if (ServerUtils.isOperator( user ))
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "The manufacturer serial # of the hardware cannot be empty") );
	            else
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Not able to send out the schedule to your thermostat.<br>Please go to the \"Contact Us\" page if you want to contact our CSRs for further information.") );
            	return SOAPUtil.buildSOAPMessage( respOper );
    		}
    		
    		boolean isTwoWay = ServerUtils.isTwoWayThermostat( liteHw, energyCompany );
			String routeStr = (energyCompany == null) ? "" : " select route id " + String.valueOf(energyCompany.getDefaultRouteID());
			
			LiteStarsThermostatSettings liteDftSettings = energyCompany.getDefaultThermostatSettings();
			LiteStarsThermostatSettings liteSettings = energyCompany.getLMHardware( updateSched.getInventoryID(), true ).getThermostatSettings();
			
			for (int i = 0; i < updateSched.getStarsThermostatSeasonCount(); i++) {
				StarsThermostatSeason starsSeason = updateSched.getStarsThermostatSeason(i);
				int webConfigID = (starsSeason.getMode().getType() == StarsThermoModeSettings.COOL_TYPE) ?
						SOAPServer.YUK_WEB_CONFIG_ID_COOL : SOAPServer.YUK_WEB_CONFIG_ID_HEAT;
				
				LiteLMThermostatSeason liteSeason = null;
				LiteLMThermostatSeason liteSeason2 = null;
				for (int j = 0; j < liteSettings.getThermostatSeasons().size(); j++) {
					LiteLMThermostatSeason lSeason = (LiteLMThermostatSeason) liteSettings.getThermostatSeasons().get(j);
					if (lSeason.getWebConfigurationID() == webConfigID)
						liteSeason = lSeason;
					else
						liteSeason2 = lSeason;
				}
				
				LiteLMThermostatSeason liteDftSeason = null;
				LiteLMThermostatSeason liteDftSeason2 = null;
				for (int j = 0; j < liteDftSettings.getThermostatSeasons().size(); j++) {
					LiteLMThermostatSeason lSeason = (LiteLMThermostatSeason) liteDftSettings.getThermostatSeasons().get(j);
					if (lSeason.getWebConfigurationID() == webConfigID)
						liteDftSeason = lSeason;
					else
						liteDftSeason2 = lSeason;
				}
				
				// Create new thermostat seasons
				if (liteSeason == null) {
					LMThermostatSeason season = new LMThermostatSeason();
					season.setInventoryID( new Integer(liteSettings.getInventoryID()) );
					season.setWebConfigurationID( new Integer(webConfigID) );
					season.setStartDate( new Date(liteDftSeason.getStartDate()) );
					season = (LMThermostatSeason) Transaction.createTransaction(Transaction.INSERT, season).execute();
					
					liteSeason = (LiteLMThermostatSeason) StarsLiteFactory.createLite( season );
					liteSettings.getThermostatSeasons().add( liteSeason );
				}
				
				if (liteSeason2 == null) {
					LMThermostatSeason season2 = new LMThermostatSeason();
					season2.setInventoryID( new Integer(liteSettings.getInventoryID()) );
					season2.setWebConfigurationID( new Integer(liteDftSeason2.getWebConfigurationID()) );
					season2.setStartDate( new Date(liteDftSeason2.getStartDate()) );
					season2 = (LMThermostatSeason) Transaction.createTransaction(Transaction.INSERT, season2).execute();
					
					liteSeason2 = (LiteLMThermostatSeason) StarsLiteFactory.createLite( season2 );
					liteSettings.getThermostatSeasons().add( liteSeason2 );
				}
				
				// Build response message
				StarsThermostatSeason starsRespSeason = null;
				StarsThermostatSeason starsRespSeason2 = null;
				for (int j = 0; j < resp.getStarsThermostatSeasonCount(); j++) {
					if (resp.getStarsThermostatSeason(j).getMode().getType() == starsSeason.getMode().getType())
						starsRespSeason = resp.getStarsThermostatSeason(j);
					else
						starsRespSeason2 = resp.getStarsThermostatSeason(j);
				}
				
				if (starsRespSeason == null) {
					starsRespSeason = new StarsThermostatSeason();
					starsRespSeason.setMode( starsSeason.getMode() );
					resp.addStarsThermostatSeason( starsRespSeason );
				}
				if (starsRespSeason2 == null) {
					starsRespSeason2 = new StarsThermostatSeason();
					starsRespSeason2.setMode( energyCompany.getThermModeSetting(liteSeason2.getWebConfigurationID()) );
					resp.addStarsThermostatSeason( starsRespSeason2 );
				}
				
				if (starsSeason.getStarsThermostatScheduleCount() == 0)
					continue;
				StarsThermostatSchedule starsSched = starsSeason.getStarsThermostatSchedule(0);
				boolean applyToWeekend = false;
				Integer[] towIDs = null;
				
				if (starsSched.getDay().getType() == StarsThermoDaySettings.ALL_TYPE) {
					// If day="All", then add season entries for weekday, saturday, and sunday
					applyToWeekend = true;
					towIDs = new Integer[] {
						ServerUtils.getThermSeasonEntryTOWID( StarsThermoDaySettings.WEEKDAY, energyCompanyID ),
						ServerUtils.getThermSeasonEntryTOWID( StarsThermoDaySettings.SATURDAY, energyCompanyID ),
						ServerUtils.getThermSeasonEntryTOWID( StarsThermoDaySettings.SUNDAY, energyCompanyID )
					};
				}
				else {
					towIDs = new Integer[ starsSeason.getStarsThermostatScheduleCount() ];
					for (int j = 0; j < starsSeason.getStarsThermostatScheduleCount(); j++)
						towIDs[j] = ServerUtils.getThermSeasonEntryTOWID(
								starsSeason.getStarsThermostatSchedule(j).getDay(), energyCompanyID );
				}
					
				for (int j = 0; j < towIDs.length; j++) {
					if (!applyToWeekend) starsSched = starsSeason.getStarsThermostatSchedule(j);
					
					ArrayList liteEntries = new ArrayList();
					if (liteSeason.getSeasonEntries() != null) {
						for (int k = 0; k < liteSeason.getSeasonEntries().size(); k++) {
							LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteSeason.getSeasonEntries().get(k);
							if (liteEntry.getTimeOfWeekID() == towIDs[j].intValue())
								liteEntries.add( liteEntry );
						}
					}
					
					ArrayList liteEntries2 = new ArrayList();
					if (liteSeason2.getSeasonEntries() != null) {
						for (int k = 0; k < liteSeason2.getSeasonEntries().size(); k++) {
							LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteSeason2.getSeasonEntries().get(k);
							if (liteEntry.getTimeOfWeekID() == towIDs[j].intValue())
								liteEntries2.add( liteEntry );
						}
					}
					
					int[] times = {
						starsSched.getTime1().getHour() * 3600 + starsSched.getTime1().getMinute() * 60 + starsSched.getTime1().getSeconds(),
						starsSched.getTime2().getHour() * 3600 + starsSched.getTime2().getMinute() * 60 + starsSched.getTime2().getSeconds(),
						starsSched.getTime3().getHour() * 3600 + starsSched.getTime3().getMinute() * 60 + starsSched.getTime3().getSeconds(),
						starsSched.getTime4().getHour() * 3600 + starsSched.getTime4().getMinute() * 60 + starsSched.getTime4().getSeconds()
					};
					int[] temps = {
						starsSched.getTemperature1(), starsSched.getTemperature2(), starsSched.getTemperature3(), starsSched.getTemperature4()
					};
					
					if (liteEntries.size() > 0 && liteEntries.size() != 4) {
						// Currently this should not happen, so remove these entries
						throw new Exception( "Invalid number of thermostat season entries: " + liteEntries.size() + ", for season id = " + liteSeason.getSeasonID() );
					}
					else if (liteEntries.size() == 4) {
						// Update the season entries
						for (int k = 0; k < 4; k++) {
							LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteEntries.get(k);
							liteEntry.setStartTime( times[k] );
							liteEntry.setTemperature( temps[k] );
							
							LMThermostatSeasonEntry entry = (LMThermostatSeasonEntry) StarsLiteFactory.createDBPersistent( liteEntry );
							Transaction.createTransaction(Transaction.UPDATE, entry).execute();
						}
					}
					else {
						// There is no season entries for the current day setting
						for (int k = 0; k < 4; k++) {
							LMThermostatSeasonEntry entry = new LMThermostatSeasonEntry();
							entry.setSeasonID( new Integer(liteSeason.getSeasonID()) );
							entry.setTimeOfWeekID( towIDs[j] );
							entry.setStartTime( new Integer(times[k]) );
							entry.setTemperature( new Integer(temps[k]) );
							entry = (LMThermostatSeasonEntry) Transaction.createTransaction(Transaction.INSERT, entry).execute();
							
							LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) StarsLiteFactory.createLite( entry );
							liteSeason.getSeasonEntries().add( liteEntry );
							liteEntries.add( liteEntry );
						}
					}
					
					if (liteEntries2.size() > 0 && liteEntries2.size() != 4) {
						throw new Exception( "Invalid number of thermostat season entries: " + liteEntries2.size() + ", for season id = " + liteSeason2.getSeasonID() );
					}
					else if (liteEntries2.size() == 4) {
						// Update the season entries for the other season
						for (int k = 0; k < 4; k++) {
							LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteEntries2.get(k);
							liteEntry.setStartTime( times[k] );
							
							LMThermostatSeasonEntry entry = (LMThermostatSeasonEntry) StarsLiteFactory.createDBPersistent( liteEntry );
							Transaction.createTransaction(Transaction.UPDATE, entry).execute();
						}
					}
					else {
						// Add season entries to the other season, using the new time schedule and the default temperatures
						for (int k = 0; k < liteDftSeason2.getSeasonEntries().size(); k++) {
							LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteDftSeason2.getSeasonEntries().get(k);
							if (liteEntry.getTimeOfWeekID() == towIDs[j].intValue())
								liteEntries2.add( liteEntry );
						}
						if (liteEntries2.size() != 4)
							throw new Exception( "Invalid number of thermostat season entries: " + liteEntries2.size() + ", for season id = " + liteDftSeason2.getSeasonID() );
						
						for (int k = 0; k < 4; k++) {
							LMThermostatSeasonEntry entry = new LMThermostatSeasonEntry();
							entry.setSeasonID( new Integer(liteSeason2.getSeasonID()) );
							entry.setTimeOfWeekID( towIDs[j] );
							entry.setStartTime( new Integer(times[k]) );
							entry.setTemperature( new Integer(((LiteLMThermostatSeasonEntry) liteEntries2.get(k)).getTemperature()) );
							entry = (LMThermostatSeasonEntry) Transaction.createTransaction(Transaction.INSERT, entry).execute();
							
							LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) StarsLiteFactory.createLite( entry );
							liteSeason2.getSeasonEntries().add( liteEntry );
						}
					}
					
					// Build response message
					if (!applyToWeekend || j == 0) {	// If applyToWeekend=true, the thermostat schedule is for all days and should only be included once
						starsRespSeason.addStarsThermostatSchedule( starsSched );
						
						StarsThermostatSchedule starsRespSched = StarsLiteFactory.createStarsThermostatSchedule(
								towIDs[j].intValue(), liteEntries2 );
						if (applyToWeekend) starsRespSched.setDay( starsSched.getDay() );
						starsRespSeason2.addStarsThermostatSchedule( starsRespSched );
					}
				}
				
				// Send out command to program the thermostat
				for (int j = 0; j < starsSeason.getStarsThermostatScheduleCount(); j++) {
					starsSched = starsSeason.getStarsThermostatSchedule(j);
					
					String dayStr = null;
					if (starsSched.getDay().getType() == StarsThermoDaySettings.ALL_TYPE)
						dayStr = "all";
					else {
						if (starsSched.getDay().getType() == StarsThermoDaySettings.WEEKDAY_TYPE)
							dayStr = "weekday";
						else if (starsSched.getDay().getType() == StarsThermoDaySettings.SATURDAY_TYPE)
							dayStr = "sat";
						else if (starsSched.getDay().getType() == StarsThermoDaySettings.SUNDAY_TYPE)
							dayStr = "sun";
						else
							throw new Exception( "Invalid thermostat schedule attribute: day = " + starsSched.getDay().toString() );
					}
					
					String time1 = starsSched.getTime1().toString().substring(0, 5);
					String time2 = starsSched.getTime2().toString().substring(0, 5);
					String time3 = starsSched.getTime3().toString().substring(0, 5);
					String time4 = starsSched.getTime4().toString().substring(0, 5);
					
					String temp1H, temp2H, temp3H, temp4H;
					String temp1C, temp2C, temp3C, temp4C;
					if (starsSeason.getMode().getType() == StarsThermoModeSettings.HEAT_TYPE) {
						temp1H = String.valueOf( starsSched.getTemperature1() );
						temp2H = String.valueOf( starsSched.getTemperature2() );
						temp3H = String.valueOf( starsSched.getTemperature3() );
						temp4H = String.valueOf( starsSched.getTemperature4() );
						temp1C = temp2C = temp3C = temp4C = "ff";
					}
					else {
						temp1C = String.valueOf( starsSched.getTemperature1() );
						temp2C = String.valueOf( starsSched.getTemperature2() );
						temp3C = String.valueOf( starsSched.getTemperature3() );
						temp4C = String.valueOf( starsSched.getTemperature4() );
						temp1H = temp2H = temp3H = temp4H = "ff";
					}
					
					StringBuffer cmd = new StringBuffer();
					if (isTwoWay)
						cmd.append("putconfig epro schedule ");
					else
						cmd.append("putconfig xcom schedule ");
					cmd.append(dayStr).append(" ")
						.append(time1).append(",").append(temp1H).append(",").append(temp1C).append(", ")
						.append(time2).append(",").append(temp2H).append(",").append(temp2C).append(", ")
						.append(time3).append(",").append(temp3H).append(",").append(temp3C).append(", ")
						.append(time4).append(",").append(temp4H).append(",").append(temp4C)
						.append(" serial ").append(liteHw.getManufactureSerialNumber())
						.append(routeStr);
					ServerUtils.sendCommand( cmd.toString() );
				}
			}
			
			// Add "programming" to the hardware events
    		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
    		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
    		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
    		
    		eventDB.setInventoryID( new Integer(updateSched.getInventoryID()) );
    		eventBase.setEventTypeID( new Integer(
    				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID()) );
    		eventBase.setActionID( new Integer(
    				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_PROGRAMMING).getEntryID()) );
    		eventBase.setEventDateTime( new Date() );
    		
    		event.setEnergyCompanyID( new Integer(energyCompanyID) );
    		event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
    				Transaction.createTransaction( Transaction.INSERT, event ).execute();
    				
    		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
    		liteHw.getLmHardwareHistory().add( liteEvent );
    		
    		StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
    		StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
    		resp.setStarsLMHardwareEvent( starsEvent );
			
			respOper.setStarsUpdateThermostatScheduleResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update thermostat schedules") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
            }
        }

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}

			StarsUpdateThermostatScheduleResponse resp = operation.getStarsUpdateThermostatScheduleResponse();
            if (resp == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            	
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
            		user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            
            String confirmMsg = "Command has been sent.";
            
            for (int i = 0; i < accountInfo.getStarsInventories().getStarsLMHardwareCount(); i++) {
            	StarsLMHardware hardware = accountInfo.getStarsInventories().getStarsLMHardware(i);
            	if (hardware.getInventoryID() == resp.getInventoryID()) {
            		hardware.getStarsLMHardwareHistory().addStarsLMHardwareEvent( resp.getStarsLMHardwareEvent() );
	            	StarsThermostatSettings settings = hardware.getStarsThermostatSettings();
	            	
		            if (hardware.getStarsThermostatSettings().getStarsThermostatDynamicData() == null) {
			            // Update thermostat schedules
			            for (int j = 0; j < resp.getStarsThermostatSeasonCount(); j++) {
			            	StarsThermostatSeason season = resp.getStarsThermostatSeason(j);
			            	StarsThermostatSchedule schedule = season.getStarsThermostatSchedule(0);
			            	if (schedule.getDay().getType() == StarsThermoDaySettings.ALL_TYPE) {
			            		schedule.setDay( StarsThermoDaySettings.WEEKDAY );
			            		
			            		StarsThermostatSchedule sched = StarsFactory.newStarsThermostatSchedule( schedule );
			            		sched.setDay( StarsThermoDaySettings.SATURDAY );
			            		season.addStarsThermostatSchedule( sched );
			            		
			            		sched = StarsFactory.newStarsThermostatSchedule( schedule );
			            		sched.setDay( StarsThermoDaySettings.SUNDAY );
			            		season.addStarsThermostatSchedule( sched );
			            	}
			            	
			            	StarsThermostatSeason oldSeason = null;
			            	for (int k = 0; k < settings.getStarsThermostatSeasonCount(); k++) {
			            		StarsThermostatSeason ssn = settings.getStarsThermostatSeason(k);
			            		if (ssn.getMode().getType() == season.getMode().getType()) {
			            			oldSeason = ssn;
			            			break;
			            		}
			            	}
			            	
			            	if (oldSeason == null)
			            		settings.addStarsThermostatSeason( season );
			            	else {
			            		for (int k = 0; k < season.getStarsThermostatScheduleCount(); k++) {
			            			schedule = season.getStarsThermostatSchedule(k);
			            			boolean foundSchedule = false;
			            			
			            			for (int l = 0; l < oldSeason.getStarsThermostatScheduleCount(); l++) {
			            				if (oldSeason.getStarsThermostatSchedule(l).getDay().getType() == schedule.getDay().getType()) {
			            					oldSeason.setStarsThermostatSchedule( l, schedule );
			            					foundSchedule = true;
			            					break;
			            				}
			            			}
			            			if (!foundSchedule) oldSeason.addStarsThermostatSchedule( schedule );
			            		}
			            	}
			            }
		            }
		            
		            if (settings.getStarsThermostatDynamicData() != null)
		            	confirmMsg += " Changes may take a few minutes to get to the thermostat.";
		            break;
            	}
            }
            
            Thread.sleep(5 * 1000);		// Wait 5 seconds.
            
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, confirmMsg);
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	private Date parseTime(String timeStr) {
		java.util.Date retVal = null;
		for( int i = 0; i < timeFormat.length; i++ ) {
			try {
				retVal = timeFormat[i].parse( timeStr );
				break;
			}
			catch( java.text.ParseException pe ) {}
		}
			
		return retVal;
	}

}
