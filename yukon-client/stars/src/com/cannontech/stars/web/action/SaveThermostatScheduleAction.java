/*
 * Created on May 21, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSchedule;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeason;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeasonEntry;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.hardware.LMThermostatSchedule;
import com.cannontech.database.data.stars.hardware.LMThermostatSeason;
import com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSaveThermostatSchedule;
import com.cannontech.stars.xml.serialize.StarsSaveThermostatScheduleResponse;
import com.cannontech.stars.xml.serialize.StarsSavedThermostatSchedules;
import com.cannontech.stars.xml.serialize.StarsThermostatProgram;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SaveThermostatScheduleAction implements ActionBase {

	/* (non-Javadoc)
	 * @see com.cannontech.stars.web.action.ActionBase#build(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			int invID = Integer.parseInt( req.getParameter("InvID") );
			String scheduleName = req.getParameter( "ScheduleName" );
			if (scheduleName.equals(""))
				throw new WebClientException( "Schedule name cannot be empty" );
			
			StarsSaveThermostatSchedule saveSchedule = new StarsSaveThermostatSchedule();
			saveSchedule.setInventoryID( invID );
			saveSchedule.setScheduleName( scheduleName );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsSaveThermostatSchedule( saveSchedule );
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (WebClientException e) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.web.action.ActionBase#process(javax.xml.soap.SOAPMessage, javax.servlet.http.HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
		StarsOperation respOper = new StarsOperation();
		
		try {
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsSaveThermostatSchedule saveSchedule = reqOper.getStarsSaveThermostatSchedule();
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			if (liteAcctInfo == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
			
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( saveSchedule.getInventoryID(), true );
			LiteLMThermostatSchedule liteNewSched = liteHw.getThermostatSettings().getThermostatSchedule();
			
			LiteLMThermostatSchedule liteOldSched = null;
			for (int i = 0; i < liteAcctInfo.getThermostatSchedules().size(); i++) {
				LiteLMThermostatSchedule liteSched = (LiteLMThermostatSchedule) liteAcctInfo.getThermostatSchedules().get(i);
				if (liteSched.getScheduleName().equalsIgnoreCase( saveSchedule.getScheduleName() )) {
					liteOldSched = liteSched;
					break;
				}
			}
			
			LMThermostatSchedule schedule = null;
			try {
				schedule = createLMThermostatSchedule( liteOldSched, liteNewSched );
			}
			catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			
			if (liteOldSched != null) {
				// Save to an existing schedule
				schedule = (LMThermostatSchedule) Transaction.createTransaction( Transaction.UPDATE, schedule ).execute();
				liteAcctInfo.getThermostatSchedules().remove( liteOldSched );
			}
			else {
				// Save to a new schedule
				schedule.getLmThermostatSchedule().setScheduleName( saveSchedule.getScheduleName() );
				// Use the entry ID in the default list as the thermostat type ID, so that
				// we can delete a thermostat type without deleting all the saved schedules
				int hwTypeDefID = YukonListFuncs.getYukonListEntry( liteNewSched.getThermostatTypeID() ).getYukonDefID();
				schedule.getLmThermostatSchedule().setThermostatTypeID(
						new Integer(StarsDatabaseCache.getInstance().getDefaultEnergyCompany().getYukonListEntry(hwTypeDefID).getEntryID()) );
				
				schedule = (LMThermostatSchedule) Transaction.createTransaction( Transaction.INSERT, schedule ).execute();
			}
			
			LiteLMThermostatSchedule liteSched = StarsLiteFactory.createLiteLMThermostatSchedule( schedule );
			liteAcctInfo.getThermostatSchedules().add( 0, liteSched );
			
			StarsThermostatProgram starsSchedule = StarsLiteFactory.createStarsThermostatProgram( liteSched, energyCompany );
			StarsSaveThermostatScheduleResponse resp = new StarsSaveThermostatScheduleResponse();
			resp.setStarsThermostatProgram( starsSchedule );
			
			respOper.setStarsSaveThermostatScheduleResponse( resp );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot save thermostat schedule") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.web.action.ActionBase#parse(javax.xml.soap.SOAPMessage, javax.xml.soap.SOAPMessage, javax.servlet.http.HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
		try {
			StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );
            
			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsSaveThermostatScheduleResponse resp = operation.getStarsSaveThermostatScheduleResponse();
			if (resp == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
					session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			
			StarsThermostatProgram starsSchedule = resp.getStarsThermostatProgram();
			StarsSavedThermostatSchedules schedules = starsAcctInfo.getStarsSavedThermostatSchedules();
			boolean foundSchedule = false;
			
			for (int i = 0; i < schedules.getStarsThermostatProgramCount(); i++) {
				if (schedules.getStarsThermostatProgram(i).getScheduleName().equals( starsSchedule.getScheduleName() )) {
					schedules.setStarsThermostatProgram(i, starsSchedule);
					foundSchedule = true;
					break;
				}
			}
			
			if (!foundSchedule)
				schedules.addStarsThermostatProgram( starsSchedule );
			
			session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Thermostat schedule saved successfully" );
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static LMThermostatSchedule createLMThermostatSchedule(LiteLMThermostatSchedule liteOldSched, LiteLMThermostatSchedule liteNewSched)
		throws WebClientException
	{
		if (!ECUtils.isValidThermostatSchedule( liteNewSched ))
			throw new WebClientException( "Cannot copy thermostat schedule because the source is invalid" );
		
		LMThermostatSchedule schedule = StarsLiteFactory.createLMThermostatSchedule( liteNewSched );
		
		if (liteOldSched != null) {
			StarsLiteFactory.setLMThermostatSchedule( schedule.getLmThermostatSchedule(), liteOldSched );
		}
		else {
			schedule.getLmThermostatSchedule().setScheduleID( null );
			schedule.getLmThermostatSchedule().setInventoryID( new Integer(CtiUtilities.NONE_ID) );
		}
		
		for (int i = 0; i < schedule.getThermostatSeasons().size(); i++) {
			LMThermostatSeason season = (LMThermostatSeason) schedule.getThermostatSeasons().get(i);
			LiteLMThermostatSeason liteOldSeason = null;
			
			if (liteOldSched != null) {
				for (int j = 0; j < liteOldSched.getThermostatSeasons().size(); j++) {
					LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) liteOldSched.getThermostatSeasons().get(j);
					if (liteSeason.getWebConfigurationID() == season.getLMThermostatSeason().getWebConfigurationID().intValue()) {
						liteOldSeason = liteSeason;
						break;
					}
				}
			}
			
			if (liteOldSeason != null) {
				StarsLiteFactory.setLMThermostatSeason( season.getLMThermostatSeason(), liteOldSeason );
			}
			else {
				season.getLMThermostatSeason().setSeasonID( null );
				season.getLMThermostatSeason().setScheduleID( schedule.getLmThermostatSchedule().getScheduleID() );
			}
			
			if (liteOldSeason != null && liteOldSeason.getSeasonEntries().size() == season.getLMThermostatSeasonEntries().size()) {
				for (int j = 0; j < season.getLMThermostatSeasonEntries().size(); j++) {
					LMThermostatSeasonEntry entry = (LMThermostatSeasonEntry) season.getLMThermostatSeasonEntries().get(j);
					LiteLMThermostatSeasonEntry liteOldEntry = (LiteLMThermostatSeasonEntry) liteOldSeason.getSeasonEntries().get(j);
					entry.setEntryID( new Integer(liteOldEntry.getEntryID()) );
					entry.setSeasonID( new Integer(liteOldEntry.getSeasonID()) );
				}
			}
			else {
				for (int j = 0; j < season.getLMThermostatSeasonEntries().size(); j++) {
					LMThermostatSeasonEntry entry = (LMThermostatSeasonEntry) season.getLMThermostatSeasonEntries().get(j);
					entry.setEntryID( null );
					entry.setSeasonID( season.getLMThermostatSeason().getSeasonID() );
				}
			}
		}
		
		return schedule;
	}

}
