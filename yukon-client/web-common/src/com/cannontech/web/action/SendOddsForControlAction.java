package com.cannontech.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteApplianceCategory;
import com.cannontech.stars.database.data.lite.LiteLMProgramWebPublishing;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.SendControlOddsTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.ChanceOfControl;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSendOddsForControl;
import com.cannontech.stars.xml.serialize.StarsSuccess;
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
public class SendOddsForControlAction implements ActionBase {

	/**
	 * @see com.cannontech.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsEnergyCompanySettings ecSettings = (StarsEnergyCompanySettings)
					session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnrollmentPrograms categories = ecSettings.getStarsEnrollmentPrograms();
			
			String[] progIDs = req.getParameterValues( "ProgID" );
			String[] controlOdds = req.getParameterValues( "ControlOdds" );
			StarsSendOddsForControl sendCtrlOdds = new StarsSendOddsForControl();
        	
			if (progIDs != null) {
				for (int i = 0; i < progIDs.length; i++) {
					int progID = Integer.parseInt( progIDs[i] );
        			
					for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
						StarsApplianceCategory category = categories.getStarsApplianceCategory(j);
						for (int k = 0; k < category.getStarsEnrLMProgramCount(); k++) {
							StarsEnrLMProgram program = category.getStarsEnrLMProgram(k);
							if (program.getProgramID() == progID) {
								StarsEnrLMProgram enrProg = new StarsEnrLMProgram();
								enrProg.setProgramID( program.getProgramID() );
								ChanceOfControl ctrlOdds = (ChanceOfControl) StarsFactory.newStarsCustListEntry(
										DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(controlOdds[i]) ),
										ChanceOfControl.class );
								enrProg.setChanceOfControl( ctrlOdds );
			        			
								sendCtrlOdds.addStarsEnrLMProgram( enrProg );
								break;
							}
						}
        				
						if (sendCtrlOdds.getStarsEnrLMProgramCount() == i+1) break;
					}
				}
			}
        	
			StarsOperation operation = new StarsOperation();
			operation.setStarsSendOddsForControl( sendCtrlOdds );
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
		}

		return null;
	}

	/**
	 * @see com.cannontech.web.action.ActionBase#process(SOAPMessage, HttpSession)
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
            
			int energyCompanyID = user.getEnergyCompanyID();
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( energyCompanyID );
            
            Iterable<LiteApplianceCategory> appCatList = energyCompany.getAllApplianceCategories();
			StarsSendOddsForControl sendCtrlOdds = reqOper.getStarsSendOddsForControl();
			
			for (int i = 0; i < sendCtrlOdds.getStarsEnrLMProgramCount(); i++) {
				StarsEnrLMProgram enrProg = sendCtrlOdds.getStarsEnrLMProgram(i);
				boolean enrProgFound = false;
        		
				for (LiteApplianceCategory liteAppCat : appCatList) {
					for (LiteLMProgramWebPublishing liteProg : liteAppCat.getPublishedPrograms()) {
						if (liteProg.getProgramID() == enrProg.getProgramID()) {
							com.cannontech.stars.database.db.LMProgramWebPublishing pubProg =
									new com.cannontech.stars.database.db.LMProgramWebPublishing();
							pubProg.setApplianceCategoryID( new Integer(liteAppCat.getApplianceCategoryID()) );
							pubProg.setProgramID( new Integer(liteProg.getProgramID()) );
                            pubProg.setDeviceID(liteProg.getDeviceID());
							pubProg.setWebSettingsID( new Integer(liteProg.getWebSettingsID()) );
							pubProg.setChanceOfControlID( new Integer(liteProg.getChanceOfControlID()) );
							Transaction.createTransaction(Transaction.UPDATE, pubProg).execute();
		        			
							LiteLMProgramWebPublishing cachedProgram = 
								energyCompany.getProgram(liteProg.getProgramID());
							cachedProgram.setChanceOfControlID( enrProg.getChanceOfControl().getEntryID() );
							enrProgFound = true;
        					
							break;
						}
					}
        			
					if (enrProgFound) break;
				}
            	
			}
			
			// Create a new thread to get through all the accounts and send out emails
			new Thread( new SendControlOddsTask(energyCompanyID), "SendControlOddsTask--action" ).start();
            
			StarsSuccess success = new StarsSuccess();
			success.setDescription( "Odds for control sent out successfully" );
            
			respOper.setStarsSuccess( success );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to send out odds for control") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
			}
		}

		return null;
	}

	/**
	 * @see com.cannontech.web.action.ActionBase#parse(SOAPMessage, SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
		try {
			StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			if (operation.getStarsSuccess() == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsEnergyCompanySettings ecSettings = (StarsEnergyCompanySettings)
					session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnrollmentPrograms categories = ecSettings.getStarsEnrollmentPrograms();
			
			StarsSendOddsForControl sendCtrlOdds = SOAPUtil.parseSOAPMsgForOperation( reqMsg ).getStarsSendOddsForControl();
			for (int i = 0; i < sendCtrlOdds.getStarsEnrLMProgramCount(); i++) {
				StarsEnrLMProgram enrProg = sendCtrlOdds.getStarsEnrLMProgram(i);
				boolean enrProgFound = false;
				
				for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
					StarsApplianceCategory category = categories.getStarsApplianceCategory(j);
					for (int k = 0; k < category.getStarsEnrLMProgramCount(); k++) {
						StarsEnrLMProgram program = category.getStarsEnrLMProgram(k);
						if (program.getProgramID() == enrProg.getProgramID()) {
							program.setChanceOfControl( enrProg.getChanceOfControl() );
							break;
						}
					}
					
					if (enrProgFound) break;
				}
			}
			
			session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, operation.getStarsSuccess().getDescription() );
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
