package com.cannontech.stars.web.action;

import java.util.ArrayList;
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
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
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

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) return null;
            
            StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            if (accountInfo == null) return null;
            
            ArrayList changedSchedules = (ArrayList) user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CHANGED_THERMOSTAT_SETTINGS );
            if (changedSchedules == null) return null;
            
            StarsThermostatSettings thermSettings = accountInfo.getStarsThermostatSettings();
            StarsUpdateThermostatSchedule updateSched = new StarsUpdateThermostatSchedule();
            updateSched.setInventoryID( thermSettings.getInventoryID() );
            
            // Send only those schedules that have been changed
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
			LiteStarsThermostatSettings liteSettings = liteAcctInfo.getThermostatSettings();
			
			// Key: StarsThermoDaySettings, value: StarsThermostatSchedule[2] ([0] is heat schedule, [1] is cool schedule)
			Hashtable daySettings = new Hashtable();
			boolean newSettings = false;
			
			for (int i = 0; i < updateSched.getStarsThermostatSeasonCount(); i++) {
				StarsThermostatSeason starsSeason = updateSched.getStarsThermostatSeason(i);
				LiteLMThermostatSeason liteSeason = null;
				LMThermostatSeason season = null;
				Integer webConfigID = energyCompany.getThermSeasonWebConfigID( starsSeason.getMode() );
				
				for (int j = 0; j < liteSettings.getThermostatSeasons().size(); j++) {
					LiteLMThermostatSeason lSeason = (LiteLMThermostatSeason) liteSettings.getThermostatSeasons().get(j);
					if (lSeason.getWebConfigurationID() == webConfigID.intValue()) {
/*						if (starsSeason.getStartDate().toDate().getTime() != lSeason.getStartDate()) {
							// Update season start date
							lSeason.setStartDate( starsSeason.getStartDate().toDate().getTime() );
							season = (LMThermostatSeason) StarsLiteFactory.createDBPersistent( lSeason );
							season = (LMThermostatSeason) Transaction.createTransaction(Transaction.UPDATE, season).execute();
						}
						*/
						liteSeason = lSeason;
						break;
					}
				}
				
				if (liteSeason == null) {
					// Create new thermostat season
					season = new LMThermostatSeason();
					season.setInventoryID( new Integer(liteSettings.getInventoryID()) );
					season.setWebConfigurationID( webConfigID );
					season.setStartDate( starsSeason.getStartDate().toDate() );
					season = (LMThermostatSeason) Transaction.createTransaction(Transaction.INSERT, season).execute();
					
					liteSeason = (LiteLMThermostatSeason) StarsLiteFactory.createLite( season );
					liteSettings.getThermostatSeasons().add( liteSeason );
				}
				
				for (int j = 0; j < starsSeason.getStarsThermostatScheduleCount(); j++) {
					StarsThermostatSchedule starsSched = starsSeason.getStarsThermostatSchedule(j);
					ArrayList liteEntries = new ArrayList();
					Integer towID = ServerUtils.getThermSeasonEntryTOWID( starsSched.getDay(), energyCompanyID );
					
					if (liteSeason.getSeasonEntries() != null) {
						for (int k = 0; k < liteSeason.getSeasonEntries().size(); k++) {
							LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteSeason.getSeasonEntries().get(k);
							if (liteEntry.getTimeOfWeekID() == towID.intValue())
								liteEntries.add( liteEntry );
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
						for (int k = liteEntries.size() - 1; k >= 0; k--) {
							LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteEntries.remove(k);
							LMThermostatSeasonEntry entry = (LMThermostatSeasonEntry) StarsLiteFactory.createDBPersistent( liteEntry );
							Transaction.createTransaction(Transaction.DELETE, entry).execute();
							liteSeason.getSeasonEntries().remove( liteEntry );
						}
					}
					
					if (liteEntries.size() == 4) {
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
							entry.setTimeOfWeekID( towID );
							entry.setStartTime( new Integer(times[k]) );
							entry.setTemperature( new Integer(temps[k]) );
							entry = (LMThermostatSeasonEntry) Transaction.createTransaction(Transaction.INSERT, entry).execute();
							
							LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) StarsLiteFactory.createLite( entry );
							liteSeason.getSeasonEntries().add( liteEntry );
						}
					}
					
					StarsThermostatSchedule[] daySetting = (StarsThermostatSchedule[]) daySettings.get( starsSched.getDay() );
					if (daySetting == null) {
						daySetting = new StarsThermostatSchedule[2];
						daySetting[0] = daySetting[1] = null;
						daySettings.put( starsSched.getDay(), daySetting );
					}
					if (starsSeason.getMode().getType() == StarsThermoModeSettings.HEAT_TYPE)
						daySetting[0] = starsSched;
					else
						daySetting[1] = starsSched;
				}
			}
			
			LiteLMHardwareBase liteHw = energyCompany.getLMHardware( updateSched.getInventoryID(), true );
			String routeStr = (energyCompany == null) ? "" : " select route id " + String.valueOf(energyCompany.getRouteID());
			
			Iterator it = daySettings.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				StarsThermoDaySettings day = (StarsThermoDaySettings) entry.getKey();
				StarsThermostatSchedule[] setting = (StarsThermostatSchedule[]) entry.getValue();
				
				String dayStr = null;
				if (day.getType() == StarsThermoDaySettings.WEEKDAY_TYPE)
					dayStr = "weekday";
				else if (day.getType() == StarsThermoDaySettings.WEEKEND_TYPE)
					dayStr = "weekend";
				else if (day.getType() == StarsThermoDaySettings.SATURDAY_TYPE)
					dayStr = "sat";
				else if (day.getType() == StarsThermoDaySettings.SUNDAY_TYPE)
					dayStr = "sun";
				else
					dayStr = "all";
				
				for (int i = 0; i < 2; i++) {
					StarsThermostatSchedule sched = setting[i];
					if (sched == null) continue;
					
					String time1 = sched.getTime1().toString().substring(0, 5);
					String time2 = sched.getTime2().toString().substring(0, 5);
					String time3 = sched.getTime3().toString().substring(0, 5);
					String time4 = sched.getTime4().toString().substring(0, 5);
					
					boolean sameTimeSettings = false;
					if (i == 0 && setting[1] != null
						&& setting[0].getTime1().equals( setting[1].getTime1() )
						&& setting[0].getTime2().equals( setting[1].getTime2() )
						&& setting[0].getTime3().equals( setting[1].getTime3() )
						&& setting[0].getTime4().equals( setting[1].getTime4() ))
						sameTimeSettings = true;
					
					String temp1H, temp2H, temp3H, temp4H;
					if (i == 0 || sameTimeSettings) {
						temp1H = String.valueOf( setting[0].getTemperature1() );
						temp2H = String.valueOf( setting[0].getTemperature2() );
						temp3H = String.valueOf( setting[0].getTemperature3() );
						temp4H = String.valueOf( setting[0].getTemperature4() );
					}
					else {
						temp1H = temp2H = temp3H = temp4H = "ff";
					}
					
					String temp1C, temp2C, temp3C, temp4C;
					if (i == 1 || sameTimeSettings) {
						temp1C = String.valueOf( setting[1].getTemperature1() );
						temp2C = String.valueOf( setting[1].getTemperature2() );
						temp3C = String.valueOf( setting[1].getTemperature3() );
						temp4C = String.valueOf( setting[1].getTemperature4() );
					}
					else {
						temp1C = temp2C = temp3C = temp4C = "ff";
					}
					
					StringBuffer cmd = new StringBuffer("putconfig xcom schedule ")
							.append(dayStr).append(" ")
							.append(time1).append(",").append(temp1H).append(",").append(temp1C).append(", ")
							.append(time2).append(",").append(temp2H).append(",").append(temp2C).append(", ")
							.append(time3).append(",").append(temp3H).append(",").append(temp3C).append(", ")
							.append(time4).append(",").append(temp4H).append(",").append(temp4C)
							.append(" serial ").append(liteHw.getManufactureSerialNumber())
							.append(routeStr);
					ServerUtils.sendCommand( cmd.toString() );
					
					// If time settings of heat & cool schedules are exactly the same,
					// we can send out just one message to save some pennies
					if (sameTimeSettings) break;
				}
			}
			
			// Add "change schedule" to the hardware events
    		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
    		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
    		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
    		
    		eventDB.setInventoryID( new Integer(updateSched.getInventoryID()) );
    		eventBase.setEventTypeID( new Integer(
    				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID()) );
    		eventBase.setActionID( new Integer(
    				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CHANGE_SCHEDULE).getEntryID()) );
    		eventBase.setEventDateTime( new Date() );
    		
    		event.setEnergyCompanyID( new Integer(energyCompanyID) );
    		event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
    				Transaction.createTransaction( Transaction.INSERT, event ).execute();
    				
    		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
    		liteHw.getLmHardwareHistory().add( liteEvent );
    		
    		StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
    		StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
    		StarsUpdateThermostatScheduleResponse resp = new StarsUpdateThermostatScheduleResponse();
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
            user.removeAttribute( ServletUtils.ATT_CHANGED_THERMOSTAT_SETTINGS );
            
            // Append the new hardware event to hardware history
            StarsUpdateThermostatSchedule updateSched = SOAPUtil.parseSOAPMsgForOperation( reqMsg ).getStarsUpdateThermostatSchedule();
            int invID = updateSched.getInventoryID();
            
            StarsLMHardwareEvent event = resp.getStarsLMHardwareEvent();
            StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
            		user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            
            StarsInventories inventories = accountInfo.getStarsInventories();
            for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
            	if (inventories.getStarsLMHardware(i).getInventoryID() == invID) {
            		inventories.getStarsLMHardware(i).getStarsLMHardwareHistory().addStarsLMHardwareEvent( event );
            		break;
            	}
            }
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
