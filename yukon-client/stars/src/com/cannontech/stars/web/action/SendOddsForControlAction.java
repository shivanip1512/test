package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteLMProgram;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.timertask.SendControlOddsTimerTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.ChanceOfControl;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
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
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
        	StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        	if (user == null) return null;
			
			StarsGetEnergyCompanySettingsResponse ecSettings = (StarsGetEnergyCompanySettingsResponse)
					user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
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
        						ChanceOfControl ctrlOdds = new ChanceOfControl();
        						ctrlOdds.setEntryID( Integer.parseInt(controlOdds[i]) );
			        			program.setChanceOfControl( ctrlOdds );
			        			
			        			sendCtrlOdds.addStarsEnrLMProgram( program );
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
            
            int energyCompanyID = user.getEnergyCompanyID();
            LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
            ArrayList appCatList = energyCompany.getAllApplianceCategories();
            ArrayList progList = new ArrayList();	// List of LiteLMProgram whose chance of control is going to be sent
            
        	synchronized (appCatList) {
	            StarsSendOddsForControl sendCtrlOdds = reqOper.getStarsSendOddsForControl();
	            for (int i = 0; i < sendCtrlOdds.getStarsEnrLMProgramCount(); i++) {
	            	StarsEnrLMProgram starsProg = sendCtrlOdds.getStarsEnrLMProgram(i);
            		
            		for (int j = 0; j < appCatList.size(); j++) {
            			LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCatList.get(j);
            			for (int k = 0; k < liteAppCat.getPublishedPrograms().length; k++) {
            				LiteLMProgram liteProg = liteAppCat.getPublishedPrograms()[k];
            				if (liteProg.getProgramID() == starsProg.getProgramID()) {
            					liteProg.setChanceOfControlID( starsProg.getChanceOfControl().getEntryID() );
            					progList.add( liteProg );
            					
			        			com.cannontech.database.db.stars.LMProgramWebPublishing pubProg =
			        					new com.cannontech.database.db.stars.LMProgramWebPublishing();
			        			pubProg.setApplianceCategoryID( new Integer(liteAppCat.getApplianceCategoryID()) );
			        			pubProg.setLMProgramID( new Integer(liteProg.getProgramID()) );
			        			pubProg.setWebSettingsID( new Integer(liteProg.getWebSettingsID()) );
			        			pubProg.setChanceOfControlID( new Integer(liteProg.getChanceOfControlID()) );
			        			Transaction.createTransaction(Transaction.UPDATE, pubProg).execute();
			        			
            					break;
            				}
            			}
            			
            			if (progList.size() == i+1) break;
            		}
            	}
            	
            	// Create a new thread to get through all the accounts and send out emails
            	SOAPServer.runTimerTask( new SendControlOddsTimerTask(energyCompanyID) );
            }
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "Odds for control updated successfully" );
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update control notification") );
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
			
			if (operation.getStarsSuccess() == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
