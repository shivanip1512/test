package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeason;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeasonEntry;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsThermostatSettings;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.hardware.LMThermostatSeason;
import com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMHardwareEvent;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsThermoSettings;
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
            
            StarsOperation operation = getRequestOperation( req );
            return SOAPUtil.buildSOAPMessage( operation );
        }
		catch (WebClientException we) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, we.getMessage() );
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
			StarsUpdateThermostatScheduleResponse resp = null;
    		
    		int[] invIDs = null;
    		if (updateSched.getInventoryIDs() == null) {
    			// Setup for a single thermostat
    			invIDs = new int[] {updateSched.getInventoryID()};
    		}
    		else {
    			// Setup for multiple thermostats
    			String[] invIDStrs = updateSched.getInventoryIDs().split(",");
    			invIDs = new int[ invIDStrs.length ];
    			for (int i = 0; i < invIDs.length; i++)
    				invIDs[i] = Integer.parseInt( invIDStrs[i] );
    		}
    		
    		boolean hasTwoWay = false;
    		
    		for (int i = 0; i < invIDs.length; i++) {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invIDs[i], true );
				
				if (liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) {
					String errorMsg = (invIDs.length == 1)?
							"The thermostat is currently out of service, schedule is not sent." :
							"The thermostat \"" + liteHw.getDeviceLabel() + "\" is currently out of service, schedule is not sent.";
					
					if (ServerUtils.isOperator( user )) {
						respOper.setStarsFailure( StarsFactory.newStarsFailure(
								StarsConstants.FAILURE_CODE_OPERATION_FAILED, errorMsg) );
					}
					else {
						respOper.setStarsFailure( StarsFactory.newStarsFailure(
								StarsConstants.FAILURE_CODE_OPERATION_FAILED, errorMsg + " Please refer to the \"Contact us\" page if you want to get more information.") );
					}
					
					return SOAPUtil.buildSOAPMessage( respOper );
				}
    			
				if (liteHw.getManufacturerSerialNumber().trim().length() == 0) {
					if (ServerUtils.isOperator( user )) {
						String errorMsg = (invIDs.length == 1)?
								"The serial # of the thermostat cannot be empty, schedule is not sent." :
								"The serial # of thermostat \"" + liteHw.getDeviceLabel() + "\" is empty, schedule is not sent.";
						
						respOper.setStarsFailure( StarsFactory.newStarsFailure(
								StarsConstants.FAILURE_CODE_OPERATION_FAILED, errorMsg) );
					}
					else {
						respOper.setStarsFailure( StarsFactory.newStarsFailure(
								StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot send schedule to the thermostat. Please refer to the \"Contact us\" page if you want to get more information.") );
					}
					
					return SOAPUtil.buildSOAPMessage( respOper );
				}
				
				int hwTypeDefID = YukonListFuncs.getYukonListEntry(liteHw.getLmHardwareTypeID()).getYukonDefID();
				LiteStarsThermostatSettings dftSettings = energyCompany.getDefaultLMHardware(hwTypeDefID).getThermostatSettings();
				LiteStarsThermostatSettings liteSettings = liteHw.getThermostatSettings();
				
				int mondayID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_MONDAY ).getEntryID();
				int weekdayID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKDAY ).getEntryID();
				
				// Send out commands to program the thermostat
				ArrayList cmdList = new ArrayList();
				
				for (int j = 0; j < updateSched.getStarsThermostatSeasonCount(); j++) {
					StarsThermostatSeason starsSeason = updateSched.getStarsThermostatSeason(j);
					
					for (int k = 0; k < starsSeason.getStarsThermostatScheduleCount(); k++) {
						StarsThermostatSchedule starsSched = starsSeason.getStarsThermostatSchedule(k);
						int towID = ECUtils.getThermSeasonEntryTOWID( starsSched.getDay(), energyCompany );
						
						ArrayList oldSched = new ArrayList();
						for (int l = 0; l < liteSettings.getThermostatSeasons().size(); l++) {
							LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) liteSettings.getThermostatSeasons().get(l);
							if (liteSeason.getWebConfigurationID() == ECUtils.YUK_WEB_CONFIG_ID_COOL  && starsSeason.getMode().getType() == StarsThermoModeSettings.COOL_TYPE ||
								liteSeason.getWebConfigurationID() == ECUtils.YUK_WEB_CONFIG_ID_HEAT  && starsSeason.getMode().getType() == StarsThermoModeSettings.HEAT_TYPE)
							{
								for (int m = 0; m < liteSeason.getSeasonEntries().size(); m++) {
									LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteSeason.getSeasonEntries().get(m);
									if (liteEntry.getTimeOfWeekID() == towID)
										oldSched.add( liteEntry );
								}
								break;
							}
						}
						
						ArrayList dftOtherSched = new ArrayList();
						for (int l = 0; l < dftSettings.getThermostatSeasons().size(); l++) {
							LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) dftSettings.getThermostatSeasons().get(l);
							if (liteSeason.getWebConfigurationID() == ECUtils.YUK_WEB_CONFIG_ID_COOL  && starsSeason.getMode().getType() == StarsThermoModeSettings.HEAT_TYPE ||
								liteSeason.getWebConfigurationID() == ECUtils.YUK_WEB_CONFIG_ID_HEAT  && starsSeason.getMode().getType() == StarsThermoModeSettings.COOL_TYPE)
							{
								for (int m = 0; m < liteSeason.getSeasonEntries().size(); m++) {
									LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteSeason.getSeasonEntries().get(m);
									if (liteEntry.getTimeOfWeekID() == towID)
										dftOtherSched.add( liteEntry );
								}
								break;
							}
						}
						
						String dayStr = null;
						if (starsSched.getDay().getType() == StarsThermoDaySettings.ALL_TYPE)
							dayStr = "all";
						else if (starsSched.getDay().getType() == StarsThermoDaySettings.WEEKDAY_TYPE)
							dayStr = "weekday";
						else if (starsSched.getDay().getType() == StarsThermoDaySettings.WEEKEND_TYPE)
							dayStr = "weekend";
						else if (starsSched.getDay().getType() == StarsThermoDaySettings.SATURDAY_TYPE)
							dayStr = "sat";
						else if (starsSched.getDay().getType() == StarsThermoDaySettings.SUNDAY_TYPE)
							dayStr = "sun";
						else if (starsSched.getDay().getType() == StarsThermoDaySettings.MONDAY_TYPE)
							dayStr = "mon";
						else if (starsSched.getDay().getType() == StarsThermoDaySettings.TUESDAY_TYPE)
							dayStr = "tue";
						else if (starsSched.getDay().getType() == StarsThermoDaySettings.WEDNESDAY_TYPE)
							dayStr = "wed";
						else if (starsSched.getDay().getType() == StarsThermoDaySettings.THURSDAY_TYPE)
							dayStr = "thu";
						else if (starsSched.getDay().getType() == StarsThermoDaySettings.FRIDAY_TYPE)
							dayStr = "fri";
						else
							throw new Exception( "Invalid thermostat schedule attribute: day = " + starsSched.getDay().toString() );
						
						if (dayStr == null) continue;
						
						boolean isCool = (starsSeason.getMode().getType() == StarsThermoModeSettings.COOL_TYPE);
						boolean skip1 = (starsSched.getTemperature1() == -1);
						boolean skip2 = (starsSched.getTemperature2() == -1);
						boolean skip3 = (starsSched.getTemperature3() == -1);
						boolean skip4 = (starsSched.getTemperature4() == -1);
						
						boolean oldSkip1 = false, oldSkip2 = false, oldSkip3 = false, oldSkip4 = false;
						if (oldSched.size() == 4) {
							oldSkip1 = ((LiteLMThermostatSeasonEntry) oldSched.get(0)).getTemperature() == -1;
							oldSkip2 = ((LiteLMThermostatSeasonEntry) oldSched.get(1)).getTemperature() == -1;
							oldSkip3 = ((LiteLMThermostatSeasonEntry) oldSched.get(2)).getTemperature() == -1;
							oldSkip4 = ((LiteLMThermostatSeasonEntry) oldSched.get(3)).getTemperature() == -1;
						}
						
						String time1 = (skip1)? "HH:MM" : starsSched.getTime1().toString().substring(0, 5);
						String time2 = (skip2)? "HH:MM" : starsSched.getTime2().toString().substring(0, 5);
						String time3 = (skip3)? "HH:MM" : starsSched.getTime3().toString().substring(0, 5);
						String time4 = (skip4)? "HH:MM" : starsSched.getTime4().toString().substring(0, 5);
						
						String temp1C = "ff", temp2C = "ff", temp3C = "ff", temp4C = "ff";
						String temp1H = "ff", temp2H = "ff", temp3H = "ff", temp4H = "ff";
						if (isCool) {
							if (!skip1) {
								temp1C = String.valueOf( starsSched.getTemperature1() );
								if (oldSkip1) temp1H = String.valueOf( ((LiteLMThermostatSeasonEntry) dftOtherSched.get(0)).getTemperature() );
							}
							if (!skip2) {
								temp2C = String.valueOf( starsSched.getTemperature2() );
								if (oldSkip2) temp2H = String.valueOf( ((LiteLMThermostatSeasonEntry) dftOtherSched.get(1)).getTemperature() );
							}
							if (!skip3) {
								temp3C = String.valueOf( starsSched.getTemperature3() );
								if (oldSkip3) temp3H = String.valueOf( ((LiteLMThermostatSeasonEntry) dftOtherSched.get(2)).getTemperature() );
							}
							if (!skip4) {
								temp4C = String.valueOf( starsSched.getTemperature4() );
								if (oldSkip4) temp4H = String.valueOf( ((LiteLMThermostatSeasonEntry) dftOtherSched.get(3)).getTemperature() );
							}
						}
						else {
							if (!skip1) {
								temp1H = String.valueOf( starsSched.getTemperature1() );
								if (oldSkip1) temp1C = String.valueOf( ((LiteLMThermostatSeasonEntry) dftOtherSched.get(0)).getTemperature() );
							}
							if (!skip2) {
								temp2H = String.valueOf( starsSched.getTemperature2() );
								if (oldSkip2) temp2C = String.valueOf( ((LiteLMThermostatSeasonEntry) dftOtherSched.get(1)).getTemperature() );
							}
							if (!skip3) {
								temp3H = String.valueOf( starsSched.getTemperature3() );
								if (oldSkip3) temp3C = String.valueOf( ((LiteLMThermostatSeasonEntry) dftOtherSched.get(2)).getTemperature() );
							}
							if (!skip4) {
								temp4H = String.valueOf( starsSched.getTemperature4() );
								if (oldSkip4) temp4C = String.valueOf( ((LiteLMThermostatSeasonEntry) dftOtherSched.get(3)).getTemperature() );
							}
						}
						
						StringBuffer cmd = new StringBuffer();
						if (liteHw.isTwoWayThermostat())
							cmd.append("putconfig epro schedule ");
						else
							cmd.append("putconfig xcom schedule ");
						cmd.append(dayStr).append(" ")
							.append(time1).append(",").append(temp1H).append(",").append(temp1C).append(", ")
							.append(time2).append(",").append(temp2H).append(",").append(temp2C).append(", ")
							.append(time3).append(",").append(temp3H).append(",").append(temp3C).append(", ")
							.append(time4).append(",").append(temp4H).append(",").append(temp4C)
							.append(" serial ").append(liteHw.getManufacturerSerialNumber());
						
						cmdList.add( cmd.toString() );
						
						// Log activity
						String tempUnit = "F";
						if (liteSettings.getDynamicData() != null && liteSettings.getDynamicData().getDisplayedTempUnit() != null)
							tempUnit = liteSettings.getDynamicData().getDisplayedTempUnit();
						
						String logMsg = "Serial #:" + liteHw.getManufacturerSerialNumber() + ", " +
								"Mode:" + starsSeason.getMode().toString() + ", " +
								"Day:" + starsSched.getDay().toString() + ", ";
						
						if (hwTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT) {
							logMsg += "Occupied:" + starsSched.getTime4().toString().substring(0, 5) + "," + starsSched.getTemperature4() + tempUnit + ", " +
									"Unoccupied:" + starsSched.getTime1().toString().substring(0, 5) + "," + starsSched.getTemperature1() + tempUnit;
						}
						else {
							logMsg += "Wake:" + ((starsSched.getTemperature1() < 0)? "(NONE)" : starsSched.getTime1().toString().substring(0, 5) + "," + starsSched.getTemperature1() + tempUnit) + ", " +
									"Leave:" + ((starsSched.getTemperature2() < 0)? "(NONE)" : starsSched.getTime2().toString().substring(0, 5) + "," + starsSched.getTemperature2() + tempUnit) + ", " +
									"Return:" + ((starsSched.getTemperature3() < 0)? "(NONE)" : starsSched.getTime3().toString().substring(0, 5) + "," + starsSched.getTemperature3() + tempUnit) + ", " +
									"Sleep:" + ((starsSched.getTemperature4() < 0)? "(NONE)" : starsSched.getTime4().toString().substring(0, 5) + "," + starsSched.getTemperature4() + tempUnit);
						}
						
						ActivityLogger.logEvent(user.getUserID(), liteAcctInfo.getAccountID(), energyCompany.getLiteID(), liteAcctInfo.getCustomer().getCustomerID(),
								"Thermostat Schedule", logMsg );
					}
				}
				
				com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
				synchronized (yc) {
					yc.setRouteID( energyCompany.getDefaultRouteID() );
					for (int j = 0; j < cmdList.size(); j++) {
						yc.setCommand( (String)cmdList.get(j) );
						yc.handleSerialNumber();
					}
				}
				
				// Add "programming" to the hardware events
				com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
				com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
				com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
    			
				eventDB.setInventoryID( new Integer(invIDs[i]) );
				eventBase.setEventTypeID( new Integer(
						energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID()) );
				eventBase.setActionID( new Integer(
						energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_PROGRAMMING).getEntryID()) );
				eventBase.setEventDateTime( new Date() );
    			
				event.setEnergyCompanyID( new Integer(energyCompanyID) );
				event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
						Transaction.createTransaction( Transaction.INSERT, event ).execute();
    			
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
				liteHw.getInventoryHistory().add( liteEvent );
    			
    			// The response message only need to be set once
				if (resp == null) {
					resp = updateThermostatSchedule( updateSched, liteHw, energyCompany );
					StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
					StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
					resp.setStarsLMHardwareEvent( starsEvent );
    			}
	    		
				if (liteHw.isTwoWayThermostat()) hasTwoWay = true;
    		}
			
			if (hasTwoWay) {
				try {
					Thread.sleep(3 * 1000);	// Wait a while for the new settings to be reflected in the table
				}
				catch (InterruptedException e) {}
				
				energyCompany.updateThermostatSettings( liteAcctInfo );
			}
			
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
            
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsUpdateThermostatSchedule updateSched = reqOper.getStarsUpdateThermostatSchedule();
			
            int[] invIDs = null;
			if (updateSched.getInventoryIDs() == null) {
				invIDs = new int[] {updateSched.getInventoryID()};
			}
			else {
				String[] invIDStrs = updateSched.getInventoryIDs().split(",");
				invIDs = new int[ invIDStrs.length ];
				for (int i = 0; i < invIDs.length; i++)
					invIDs[i] = Integer.parseInt( invIDStrs[i] );
			}
			
			for (int i = 0; i < invIDs.length; i++) {
				for (int j = 0; j < accountInfo.getStarsInventories().getStarsInventoryCount(); j++) {
					StarsInventory inv = accountInfo.getStarsInventories().getStarsInventory(j);
					if (inv.getInventoryID() == invIDs[i]) {
						if (resp.getStarsLMHardwareEvent() != null)
							inv.getStarsLMHardwareHistory().addStarsLMHardwareEvent( resp.getStarsLMHardwareEvent() );
	            		
						StarsThermostatSettings settings = inv.getLMHardware().getStarsThermostatSettings();
						if (settings.getStarsThermostatDynamicData() == null)
							parseResponse( resp, settings );
						
						break;
					}
				}
			}
            
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE,
					"The new schedule has been sent. It may take a few minutes before the thermostat gets it.");
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static StarsOperation getRequestOperation(HttpServletRequest req) throws WebClientException {
		StarsUpdateThermostatSchedule updateSched = new StarsUpdateThermostatSchedule();
        
		String[] invIDStrs = req.getParameterValues("InvIDs");
		if (invIDStrs == null || invIDStrs.length == 0) {
			updateSched.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
		}
		else {
			StringBuffer invIDStr = new StringBuffer( invIDStrs[0] );
			for (int i = 1; i < invIDStrs.length; i++)
				invIDStr.append(",").append(invIDStrs[i]);
            	
			updateSched.setInventoryIDs( invIDStr.toString() );
		}
	    
		StarsThermoModeSettings mode = StarsThermoModeSettings.valueOf( req.getParameter("mode") );
		StarsThermoDaySettings day = StarsThermoDaySettings.valueOf( req.getParameter("day") );
		
		StarsThermostatSeason season = new StarsThermostatSeason();
		season.setMode( mode );
		updateSched.addStarsThermostatSeason( season );
		StarsThermostatSchedule schedule = new StarsThermostatSchedule();
		schedule.setDay( day );
		season.addStarsThermostatSchedule( schedule );
	    
		Calendar cal = Calendar.getInstance();
		boolean noScript = (req.getParameter("temp1") != null);
	    
		if (req.getParameter("time1") != null) {
			Date time1 = ServletUtils.parseTime(req.getParameter("time1"), TimeZone.getDefault());
			if (time1 == null)
				throw new WebClientException("Invalid time format '" + req.getParameter("time1") + "'");
	        
			cal.setTime( time1 );
			schedule.setTime1( new org.exolab.castor.types.Time(
					(cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60) * 1000) );
			schedule.setTemperature1( (noScript)?
					Integer.parseInt(req.getParameter("temp1")) :
					Integer.parseInt(req.getParameter("tempval1")) );
		}
		else {
			schedule.setTime1( new org.exolab.castor.types.Time(0) );
			schedule.setTemperature1( -1 );
		}
	    
		if (req.getParameter("time2") != null) {
			Date time2 = ServletUtils.parseTime(req.getParameter("time2"), TimeZone.getDefault());
			if (time2 == null)
				throw new WebClientException("Invalid time format '" + req.getParameter("time2") + "'");
	        
			cal.setTime( time2 );
			schedule.setTime2( new org.exolab.castor.types.Time(
					(cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60) * 1000) );
			schedule.setTemperature2( (noScript)?
					Integer.parseInt(req.getParameter("temp2")) :
					Integer.parseInt(req.getParameter("tempval2")) );
		}
		else {
			schedule.setTime2( new org.exolab.castor.types.Time(0) );
			schedule.setTemperature2( -1 );
		}
	    
		if (req.getParameter("time3") != null) {
			Date time3 = ServletUtils.parseTime(req.getParameter("time3"), TimeZone.getDefault());
			if (time3 == null)
				throw new WebClientException("Invalid time format '" + req.getParameter("time3") + "'");
	        
			cal.setTime( time3 );
			schedule.setTime3( new org.exolab.castor.types.Time(
					(cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60) * 1000) );
			schedule.setTemperature3( (noScript)?
					Integer.parseInt(req.getParameter("temp3")) :
					Integer.parseInt(req.getParameter("tempval3")) );
		}
		else {
			schedule.setTime3( new org.exolab.castor.types.Time(0) );
			schedule.setTemperature3( -1 );
		}
	    
		if (req.getParameter("time4") != null) {
			Date time4 = ServletUtils.parseTime(req.getParameter("time4"), TimeZone.getDefault());
			if (time4 == null)
				throw new WebClientException("Invalid time format '" + req.getParameter("time4") + "'");
	        
			cal.setTime( time4 );
			schedule.setTime4( new org.exolab.castor.types.Time(
					(cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60) * 1000) );
			schedule.setTemperature4( (noScript)?
					Integer.parseInt(req.getParameter("temp4")) :
					Integer.parseInt(req.getParameter("tempval4")) );
		}
		else {
			schedule.setTime4( new org.exolab.castor.types.Time(0) );
			schedule.setTemperature4( -1 );
		}
		
		if (req.getParameter("isTwoWay") == null) {
			// This is a one-way thermostat or the default thermostat
			if (ServletUtils.isWeekday( day )) {
				if (req.getParameter("ApplyToWeekend") != null)
					schedule.setDay( StarsThermoDaySettings.ALL );
			}
		}
		else {
			// This is a two-way thermostat
			String applyToWeekdaysStr = req.getParameter( "ApplyToWeekdays" );
			String applyToWeekendStr = req.getParameter( "ApplyToWeekend" );
			
			if (applyToWeekdaysStr != null && applyToWeekendStr != null) {
				schedule.setDay( StarsThermoDaySettings.ALL );
			}
			else if (applyToWeekdaysStr != null) {
				if (ServletUtils.isWeekday( day )) {
					schedule.setDay( StarsThermoDaySettings.WEEKDAY );
				}
				else {
					StarsThermostatSchedule schedule2 = StarsFactory.newStarsThermostatSchedule( schedule );
					schedule2.setDay( StarsThermoDaySettings.WEEKDAY );
					season.addStarsThermostatSchedule( schedule2 );
				}
			}
			else if (applyToWeekendStr != null) {
				if (!ServletUtils.isWeekday( day )) {
					schedule.setDay( StarsThermoDaySettings.WEEKEND );
				}
				else {
					StarsThermostatSchedule schedule2 = StarsFactory.newStarsThermostatSchedule( schedule );
					schedule2.setDay( StarsThermoDaySettings.WEEKEND );
					season.addStarsThermostatSchedule( schedule2 );
				}
			}
		}
        
		StarsOperation operation = new StarsOperation();
		operation.setStarsUpdateThermostatSchedule( updateSched );
		
		return operation;
	}
	
	public static StarsUpdateThermostatScheduleResponse updateThermostatSchedule(
			StarsUpdateThermostatSchedule updateSched, LiteStarsLMHardware liteHw, LiteStarsEnergyCompany energyCompany)
			throws Exception
	{
		StarsUpdateThermostatScheduleResponse resp = new StarsUpdateThermostatScheduleResponse();
		
		int hwTypeDefID = YukonListFuncs.getYukonListEntry(liteHw.getLmHardwareTypeID()).getYukonDefID();
		LiteStarsThermostatSettings liteDftSettings = energyCompany.getDefaultLMHardware(hwTypeDefID).getThermostatSettings();
		LiteStarsThermostatSettings liteSettings = liteHw.getThermostatSettings();
		
		java.sql.Connection conn = null;
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(
					com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
			if (conn == null)
				throw new java.sql.SQLException("Cannot get database connection");
			
			for (int i = 0; i < updateSched.getStarsThermostatSeasonCount(); i++) {
				StarsThermostatSeason starsSeason = updateSched.getStarsThermostatSeason(i);
				int webConfigID = (starsSeason.getMode().getType() == StarsThermoModeSettings.COOL_TYPE) ?
						ECUtils.YUK_WEB_CONFIG_ID_COOL : ECUtils.YUK_WEB_CONFIG_ID_HEAT;
				
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
					season.setInventoryID( new Integer(liteHw.getInventoryID()) );
					season.setWebConfigurationID( new Integer(webConfigID) );
					season.setStartDate( new Date(liteDftSeason.getStartDate()) );
					season = (LMThermostatSeason) Transaction.createTransaction(Transaction.INSERT, season).execute();
					
					liteSeason = (LiteLMThermostatSeason) StarsLiteFactory.createLite( season );
					liteSettings.getThermostatSeasons().add( liteSeason );
				}
				
				if (liteSeason2 == null) {
					LMThermostatSeason season2 = new LMThermostatSeason();
					season2.setInventoryID( new Integer(liteHw.getInventoryID()) );
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
					starsRespSeason2.setMode( ECUtils.getThermSeasonMode(liteSeason2.getWebConfigurationID()) );
					resp.addStarsThermostatSeason( starsRespSeason2 );
				}
					
				for (int j = 0; j < starsSeason.getStarsThermostatScheduleCount(); j++) {
					StarsThermostatSchedule starsSched = starsSeason.getStarsThermostatSchedule(j);
					int[] towIDs = null;
					
					if (starsSched.getDay().getType() == StarsThermoDaySettings.ALL_TYPE) {
						if (liteHw.isTwoWayThermostat()) {
							// Add season entries monday through sunday
							towIDs = new int[] {
								energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_MONDAY ).getEntryID(),
								energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_TUESDAY ).getEntryID(),
								energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_WEDNESDAY ).getEntryID(),
								energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_THURSDAY ).getEntryID(),
								energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_FRIDAY ).getEntryID(),
								energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_SATURDAY ).getEntryID(),
								energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_SUNDAY ).getEntryID()
							};
						}
						else {
							// Add season entries for weekday, saturday, and sunday
							towIDs = new int[] {
								energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKDAY ).getEntryID(),
								energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_SATURDAY ).getEntryID(),
								energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_SUNDAY ).getEntryID()
							};
						}
					}
					else if (starsSched.getDay().getType() == StarsThermoDaySettings.WEEKDAY_TYPE && liteHw.isTwoWayThermostat()) {
						// Add season entries monday through friday
						towIDs = new int[] {
							energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_MONDAY ).getEntryID(),
							energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_TUESDAY ).getEntryID(),
							energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_WEDNESDAY ).getEntryID(),
							energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_THURSDAY ).getEntryID(),
							energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_FRIDAY ).getEntryID(),
						};
					}
					else if (starsSched.getDay().getType() == StarsThermoDaySettings.WEEKEND_TYPE) {
						// Add season entries for saturday and sunday
						towIDs = new int[] {
							energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_SATURDAY ).getEntryID(),
							energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_TOW_SUNDAY ).getEntryID(),
						};
					}
					else {
						towIDs = new int[] {
							ECUtils.getThermSeasonEntryTOWID( starsSched.getDay(), energyCompany )
						};
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
					
					for (int k = 0; k < towIDs.length; k++) {
						ArrayList liteEntries = new ArrayList();
						if (liteSeason.getSeasonEntries() != null) {
							for (int l = 0; l < liteSeason.getSeasonEntries().size(); l++) {
								LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteSeason.getSeasonEntries().get(l);
								if (liteEntry.getTimeOfWeekID() == towIDs[k])
									liteEntries.add( liteEntry );
							}
						}
						
						ArrayList liteEntries2 = new ArrayList();
						if (liteSeason2.getSeasonEntries() != null) {
							for (int l = 0; l < liteSeason2.getSeasonEntries().size(); l++) {
								LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteSeason2.getSeasonEntries().get(l);
								if (liteEntry.getTimeOfWeekID() == towIDs[k])
									liteEntries2.add( liteEntry );
							}
						}
						
						if (liteEntries.size() > 0 && liteEntries.size() != 4) {
							// Currently this should not happen, so remove these entries
							throw new Exception( "Invalid number of thermostat season entries: " + liteEntries.size() + ", for season id = " + liteSeason.getSeasonID() );
						}
						else if (liteEntries.size() == 4) {
							// Update the season entries
							for (int l = 0; l < 4; l++) {
								LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteEntries.get(l);
								liteEntry.setStartTime( times[l] );
								liteEntry.setTemperature( temps[l] );
								
								LMThermostatSeasonEntry entry = (LMThermostatSeasonEntry) StarsLiteFactory.createDBPersistent( liteEntry );
								entry.setDbConnection( conn );
								entry.update();
							}
						}
						else {
							// There is no season entries for the current day setting
							for (int l = 0; l < 4; l++) {
								LMThermostatSeasonEntry entry = new LMThermostatSeasonEntry();
								entry.setSeasonID( new Integer(liteSeason.getSeasonID()) );
								entry.setTimeOfWeekID( new Integer(towIDs[k]) );
								entry.setStartTime( new Integer(times[l]) );
								entry.setTemperature( new Integer(temps[l]) );
								entry.setDbConnection( conn );
								entry.add();
								
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
							for (int l = 0; l < 4; l++) {
								LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteEntries2.get(l);
								liteEntry.setStartTime( times[l] );
								
								LMThermostatSeasonEntry entry = (LMThermostatSeasonEntry) StarsLiteFactory.createDBPersistent( liteEntry );
								entry.setDbConnection( conn );
								entry.update();
							}
						}
						else {
							// Add season entries to the other season, using the new time schedule and the default temperatures
							for (int l = 0; l < liteDftSeason2.getSeasonEntries().size(); l++) {
								LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteDftSeason2.getSeasonEntries().get(l);
								if (liteEntry.getTimeOfWeekID() == towIDs[k])
									liteEntries2.add( liteEntry );
							}
							if (liteEntries2.size() != 4)
								throw new Exception( "Invalid number of thermostat season entries: " + liteEntries2.size() + ", for season id = " + liteDftSeason2.getSeasonID() );
							
							for (int l = 0; l < 4; l++) {
								LMThermostatSeasonEntry entry = new LMThermostatSeasonEntry();
								entry.setSeasonID( new Integer(liteSeason2.getSeasonID()) );
								entry.setTimeOfWeekID( new Integer(towIDs[k]) );
								entry.setStartTime( new Integer(times[l]) );
								entry.setTemperature( new Integer(((LiteLMThermostatSeasonEntry) liteEntries2.get(l)).getTemperature()) );
								entry.setDbConnection( conn );
								entry.add();
								
								LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) StarsLiteFactory.createLite( entry );
								liteSeason2.getSeasonEntries().add( liteEntry );
							}
						}
						
						// Build response message
						StarsThermostatSchedule starsRespSched = StarsLiteFactory.createStarsThermostatSchedule( towIDs[k], liteEntries );
						starsRespSeason.addStarsThermostatSchedule( starsRespSched );
						StarsThermostatSchedule starsRespSched2 = StarsLiteFactory.createStarsThermostatSchedule( towIDs[k], liteEntries2 );
						starsRespSeason2.addStarsThermostatSchedule( starsRespSched2 );
					}
				}
			}
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
		
		return resp;
	}
	
	public static void parseResponse(StarsUpdateThermostatScheduleResponse resp, StarsThermoSettings settings) {
        for (int i = 0; i < resp.getStarsThermostatSeasonCount(); i++) {
        	StarsThermostatSeason newSeason = resp.getStarsThermostatSeason(i);
        	
        	StarsThermostatSeason oldSeason = null;
        	for (int j = 0; j < settings.getStarsThermostatSeasonCount(); j++) {
        		StarsThermostatSeason season = settings.getStarsThermostatSeason(j);
        		if (season.getMode().getType() == newSeason.getMode().getType()) {
        			oldSeason = season;
        			break;
        		}
        	}
        	
        	if (oldSeason == null) {
        		oldSeason = new StarsThermostatSeason();
        		oldSeason.setMode( newSeason.getMode() );
        		oldSeason.setStartDate( newSeason.getStartDate() );
        		settings.addStarsThermostatSeason( oldSeason );
        	}
        	
    		for (int j = 0; j < newSeason.getStarsThermostatScheduleCount(); j++) {
    			StarsThermostatSchedule newSched = newSeason.getStarsThermostatSchedule(j);
    			StarsThermoDaySettings daySetting = newSched.getDay();
    			
    			StarsThermostatSchedule oldSched = null;
    			for (int k = 0; k < oldSeason.getStarsThermostatScheduleCount(); k++) {
    				StarsThermostatSchedule sched = oldSeason.getStarsThermostatSchedule(k);
    				if (sched.getDay().getType() == daySetting.getType()) {
    					oldSched = sched;
    					break;
    				}
    			}
    			
				if (oldSched == null) {
					oldSched = new StarsThermostatSchedule();
					oldSched.setDay( daySetting );
					oldSeason.addStarsThermostatSchedule( oldSched );
				}
				
				oldSched.setTime1( newSched.getTime1() );
				oldSched.setTemperature1( newSched.getTemperature1() );
				oldSched.setTime2( newSched.getTime2() );
				oldSched.setTemperature2( newSched.getTemperature2() );
				oldSched.setTime3( newSched.getTime3() );
				oldSched.setTemperature3( newSched.getTemperature3() );
				oldSched.setTime4( newSched.getTime4() );
				oldSched.setTemperature4( newSched.getTemperature4() );
    		}
        }
	}

}
