package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;
import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.database.db.stars.hardware.*;
import com.cannontech.message.porter.ClientConnection;
import com.cannontech.servlet.PILConnectionServlet;
import com.cannontech.stars.util.*;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.*;
import com.cannontech.stars.xml.util.*;

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
            com.cannontech.stars.web.StarsUser user = (com.cannontech.stars.web.StarsUser) session.getAttribute("USER");
            if (user == null) return null;
            
            StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            if (accountInfo == null) return null;
            
            ArrayList changedSchedules = (ArrayList) user.getAttribute( ServletUtils.ATT_CHANGED_THERMOSTAT_SETTINGS );
            if (changedSchedules == null) return null;
            
            StarsThermostatSettings thermSettings = accountInfo.getStarsThermostatSettings();
            StarsUpdateThermostatSettings updateSettings = new StarsUpdateThermostatSettings();
            updateSettings.setInventoryID( thermSettings.getInventoryID() );
            
            for (int i = 0; i < thermSettings.getStarsThermostatSeasonCount(); i++) {
            	StarsThermostatSeason season = thermSettings.getStarsThermostatSeason(i);
            	StarsThermostatSeason season2 = new StarsThermostatSeason();
            	season2.setMode( season.getMode() );
            	season2.setStartDate( season.getStartDate() );
            	for (int j = 0; j < season.getStarsThermostatScheduleCount(); j++) {
            		if (changedSchedules.contains( season.getStarsThermostatSchedule(j) ))
            			season2.addStarsThermostatSchedule( season.getStarsThermostatSchedule(j) );
            	}
            	
            	updateSettings.addStarsThermostatSeason( season2 );
            }
            
            StarsOperation operation = new StarsOperation();
            operation.setStarsUpdateThermostatSettings( updateSettings );
            
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
            
            com.cannontech.stars.web.StarsUser user = (com.cannontech.stars.web.StarsUser) session.getAttribute("USER");
            if (user == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}

            ClientConnection conn = ServerUtils.getClientConnection();
            if (conn == null) {
                CTILogger.debug( "ConfigThermostatAction: Failed to retrieve a client connection" );
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to send Expresscom command") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
                
			int energyCompanyID = user.getEnergyCompanyID();
			LiteStarsEnergyCompany company = SOAPServer.getEnergyCompany( energyCompanyID );
			Hashtable selectionLists = (Hashtable) company.getAllSelectionLists();
			
			StarsUpdateThermostatSettings starsSettings = reqOper.getStarsUpdateThermostatSettings();
			LiteStarsThermostatSettings liteSettings = liteAcctInfo.getThermostatSettings();
			
			// Key: StarsThermoDaySettings, value: StarsThermostatSchedule[2] ([0] is heat schedule, [1] is cool schedule)
			Hashtable daySettings = new Hashtable();
			boolean newSettings = false;
			
			for (int i = 0; i < starsSettings.getStarsThermostatSeasonCount(); i++) {
				StarsThermostatSeason starsSeason = starsSettings.getStarsThermostatSeason(i);
				LiteLMThermostatSeason liteSeason = null;
				LMThermostatSeason season = null;
				Integer webConfigID = company.getThermSeasonWebConfigID( starsSeason.getMode() );
				
				for (int j = 0; j < liteSettings.getThermostatSeasons().size(); j++) {
					LiteLMThermostatSeason lSeason = (LiteLMThermostatSeason) liteSettings.getThermostatSeasons().get(j);
					if (lSeason.getWebConfigurationID() == webConfigID.intValue()) {
						if (starsSeason.getStartDate().toDate().getTime() != lSeason.getStartDate()) {
							// Update season start date
							lSeason.setStartDate( starsSeason.getStartDate().toDate().getTime() );
							season = (LMThermostatSeason) StarsLiteFactory.createDBPersistent( lSeason );
							season = (LMThermostatSeason) Transaction.createTransaction(Transaction.UPDATE, season).execute();
						}
						
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
					if (liteSettings.getThermostatSeasons() == null)
						liteSettings.setThermostatSeasons( new ArrayList() );
					liteSettings.getThermostatSeasons().add( liteSeason );
				}
				
				for (int j = 0; j < starsSeason.getStarsThermostatScheduleCount(); j++) {
					StarsThermostatSchedule starsSched = starsSeason.getStarsThermostatSchedule(j);
					ArrayList liteEntries = new ArrayList();
					Integer towID = ServerUtils.getThermSeasonEntryTOWID( starsSched.getDay(), selectionLists );
					
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
						liteEntries.clear();
					}
					
					if (liteEntries.size() == 4) {
						// Update the season entries
						LMThermostatSeasonEntry.deleteAllLMThermostatSeasonEntries( new Integer(liteSeason.getSeasonID()), towID );
						for (int k = 0; k < 4; k++) {
							LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteEntries.get(k);
							liteEntry.setStartTime( times[k] );
							liteEntry.setTemperature( temps[k] );
							
							LMThermostatSeasonEntry entry = (LMThermostatSeasonEntry) StarsLiteFactory.createDBPersistent( liteEntry );
							Transaction.createTransaction(Transaction.INSERT, entry).execute();
						}
					}
					else {
						// There is no season entries for the current day setting
						for (int k = 0; k < 4; k++) {
							LiteLMThermostatSeasonEntry liteEntry = new LiteLMThermostatSeasonEntry();
							liteEntry.setSeasonID( liteSeason.getSeasonID() );
							liteEntry.setTimeOfWeekID( towID.intValue() );
							liteEntry.setStartTime( times[k] );
							liteEntry.setTemperature( temps[k] );
							
							LMThermostatSeasonEntry entry = (LMThermostatSeasonEntry) StarsLiteFactory.createDBPersistent( liteEntry );
							Transaction.createTransaction(Transaction.INSERT, entry).execute();
							
							if (liteSeason.getSeasonEntries() == null)
								liteSeason.setSeasonEntries( new ArrayList() );
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
			
			LiteLMHardwareBase liteHw = company.getLMHardware( starsSettings.getInventoryID(), true );
			String routeStr = (company == null) ? "" : " select route id " + String.valueOf(company.getRouteID());
			
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
					if (setting[0] != null && setting[0].equals(sched) || sameTimeSettings) {
						temp1H = String.valueOf( setting[0].getTemperature1() );
						temp2H = String.valueOf( setting[0].getTemperature2() );
						temp3H = String.valueOf( setting[0].getTemperature3() );
						temp4H = String.valueOf( setting[0].getTemperature4() );
					}
					else {
						temp1H = temp2H = temp3H = temp4H = "ff";
					}
					
					String temp1C, temp2C, temp3C, temp4C;
					if (setting[1] != null && setting[1].equals(sched) || sameTimeSettings) {
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
					ServerUtils.sendCommand( cmd.toString(), conn );
					
					// If time settings of heat & cool schedules are exactly the same,
					// we can send out just one message to save some pennies
					if (sameTimeSettings) break;
				}
			}
			
			// Add "config" to the hardware events
            Integer hwEventEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMHARDWAREEVENT)
            		.getEntryID() );
            Integer configEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_CONFIG)
            		.getEntryID() );
            
    		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
    		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
    		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
    		
    		eventDB.setInventoryID( new Integer(starsSettings.getInventoryID()) );
    		eventBase.setEventTypeID( hwEventEntryID );
    		eventBase.setActionID( configEntryID );
    		eventBase.setEventDateTime( new Date() );
    		
    		event.setEnergyCompanyID( new Integer(energyCompanyID) );
    		event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
    				Transaction.createTransaction( Transaction.INSERT, event ).execute();
    				
    		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
    		if (liteHw.getLmHardwareHistory() == null)
    			liteHw.setLmHardwareHistory( new ArrayList() );
    		liteHw.getLmHardwareHistory().add( liteEvent );
			
			StarsSuccess success = new StarsSuccess();
			success.setDescription( "Thermostat settings updated successfully" );
			respOper.setStarsSuccess( success );
			
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update thermostat settings") );
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

			StarsSuccess success = operation.getStarsSuccess();
            if (success == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
            com.cannontech.stars.web.StarsUser user = (com.cannontech.stars.web.StarsUser) session.getAttribute("USER");
            user.removeAttribute( ServletUtils.ATT_CHANGED_THERMOSTAT_SETTINGS );
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
