package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig;
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
public class UpdateLMHardwareConfigAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;

			StarsUpdateLMHardwareConfig updateHwConfig = new StarsUpdateLMHardwareConfig();
			updateHwConfig.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
			
			String[] appIDs = req.getParameterValues( "AppID" );
			String[] grpIDs = req.getParameterValues( "GroupID" );
			if (appIDs != null) {
				for (int i = 0; i < appIDs.length; i++) {
					StarsLMHardwareConfig config = new StarsLMHardwareConfig();
					config.setApplianceID( Integer.parseInt(appIDs[i]) );
					config.setGroupID( Integer.parseInt(grpIDs[i]) );
					updateHwConfig.addStarsLMHardwareConfig( config );
				}
			}
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateLMHardwareConfig( updateHwConfig );
			
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
        	
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
            
            StarsUpdateLMHardwareConfig updateHwConfig = reqOper.getStarsUpdateLMHardwareConfig();
            LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( updateHwConfig.getInventoryID(), true );
            
            ArrayList appList = new ArrayList();		// Appliances connected to the hardware before
            for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
            	LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
            	if (liteApp.getInventoryID() == liteHw.getInventoryID())
            		appList.add( liteApp );
            }
			
            for (int i = 0; i < updateHwConfig.getStarsLMHardwareConfigCount(); i++) {
            	StarsLMHardwareConfig starsConfig = updateHwConfig.getStarsLMHardwareConfig(i);
            	if (starsConfig.getGroupID() == 0) continue;
            	
            	for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
            		LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
            		if (liteApp.getApplianceID() == starsConfig.getApplianceID()) {
            			liteApp.setInventoryID( liteHw.getInventoryID() );
            			
            			for (int k = 0; k < liteAcctInfo.getLmPrograms().size(); k++) {
            				LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(k);
            				if (liteProg.getLmProgram().getProgramID() == liteApp.getLmProgramID()) {
            				/* If the appliance was connected with the hardware before, update its assigned group
            				 * Otherwise add new configuration for the hardware
            				 */
            				 	if (appList.contains( liteApp )) {
	            					if (liteProg.getGroupID() != starsConfig.getGroupID()) {
						            	com.cannontech.database.db.stars.hardware.LMHardwareConfiguration config =
						            			new com.cannontech.database.db.stars.hardware.LMHardwareConfiguration();
						            	config.setInventoryID( new Integer(liteHw.getInventoryID()) );
						            	config.setApplianceID( new Integer(starsConfig.getApplianceID()) );
						            	config.setAddressingGroupID( new Integer(starsConfig.getGroupID()) );
						            	config = (com.cannontech.database.db.stars.hardware.LMHardwareConfiguration)
						            			Transaction.createTransaction( Transaction.UPDATE, config ).execute();
	            					}
	            				 	appList.remove( liteApp );
            				 	}
            				 	else {
					            	com.cannontech.database.db.stars.hardware.LMHardwareConfiguration config =
					            			new com.cannontech.database.db.stars.hardware.LMHardwareConfiguration();
					            	config.setInventoryID( new Integer(liteHw.getInventoryID()) );
					            	config.setApplianceID( new Integer(starsConfig.getApplianceID()) );
					            	config.setAddressingGroupID( new Integer(starsConfig.getGroupID()) );
					            	config = (com.cannontech.database.db.stars.hardware.LMHardwareConfiguration)
					            			Transaction.createTransaction( Transaction.INSERT, config ).execute();
            				 	}
            				 	
            					liteProg.setGroupID( starsConfig.getGroupID() );
            					break;
            				}
            			}
            			break;
            		}
            	}
            }
            
            // Remove configuration of all the remaining appliances
            for (int i = 0; i < appList.size(); i++) {
            	LiteStarsAppliance liteApp = (LiteStarsAppliance) appList.get(i);
            	com.cannontech.database.db.stars.hardware.LMHardwareConfiguration config =
            			new com.cannontech.database.db.stars.hardware.LMHardwareConfiguration();
            	config.setInventoryID( new Integer(liteHw.getInventoryID()) );
            	config.setApplianceID( new Integer(liteApp.getApplianceID()) );
            	config = (com.cannontech.database.db.stars.hardware.LMHardwareConfiguration)
            			Transaction.createTransaction( Transaction.DELETE, config ).execute();
            			
            	liteApp.setInventoryID( 0 );
    			for (int j = 0; j < liteAcctInfo.getLmPrograms().size(); j++) {
    				LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(j);
    				if (liteProg.getLmProgram().getProgramID() == liteApp.getLmProgramID()) {
    					liteProg.setGroupID( 0 );
    					break;
    				}
    			}
            }
            
			// Log activity
			ActivityLogger.log(user.getUserID(), liteAcctInfo.getAccountID(), energyCompany.getLiteID(), liteAcctInfo.getCustomer().getCustomerID(),
					"Hardware Configuration", "Serial #:" + liteHw.getManufacturerSerialNumber() );
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "LM Hardware configuration updated successfully" );
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the hardware information") );
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
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            if (accountInfo == null)
            	return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
			
			StarsUpdateLMHardwareConfig updateHwConfig = SOAPUtil.parseSOAPMsgForOperation( reqMsg ).getStarsUpdateLMHardwareConfig();
			
			for (int i = 0; i < accountInfo.getStarsAppliances().getStarsApplianceCount(); i++) {
				StarsAppliance appliance = accountInfo.getStarsAppliances().getStarsAppliance(i);
				if (appliance.getInventoryID() == updateHwConfig.getInventoryID()) {
					appliance.setInventoryID( 0 );
					for (int j = 0; j < accountInfo.getStarsLMPrograms().getStarsLMProgramCount(); j++) {
						StarsLMProgram program = accountInfo.getStarsLMPrograms().getStarsLMProgram(j);
						if (program.getProgramID() == appliance.getLmProgramID()) {
							program.setGroupID( 0 );
							break;
						}
					}
				}
			}
			
			for (int i = 0; i < updateHwConfig.getStarsLMHardwareConfigCount(); i++) {
				StarsLMHardwareConfig config = updateHwConfig.getStarsLMHardwareConfig(i);
				
				for (int j = 0; j < accountInfo.getStarsAppliances().getStarsApplianceCount(); j++) {
					StarsAppliance app = accountInfo.getStarsAppliances().getStarsAppliance(j);
					if (app.getApplianceID() == config.getApplianceID()) {
						app.setInventoryID( updateHwConfig.getInventoryID() );
						for (int k = 0; k < accountInfo.getStarsLMPrograms().getStarsLMProgramCount(); k++) {
							StarsLMProgram prog = accountInfo.getStarsLMPrograms().getStarsLMProgram(k);
							if (prog.getProgramID() == app.getLmProgramID()) {
								prog.setGroupID( config.getGroupID() );
								break;
							}
						}
						break;
					}
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
