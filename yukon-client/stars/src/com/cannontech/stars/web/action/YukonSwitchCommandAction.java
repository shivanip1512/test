package com.cannontech.stars.web.action;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.xml.soap.SOAPMessage;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.util.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.database.Transaction;
import com.cannontech.servlet.PILConnectionServlet;
import com.cannontech.message.porter.ClientConnection;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class YukonSwitchCommandAction implements ActionBase {

    private org.apache.commons.logging.Log logger = XMLUtil.getLogger( YukonSwitchCommandAction.class );

    // increment this for every message
    private static long userMessageIDCounter = 1;

    public YukonSwitchCommandAction() {
        super();
    }

    public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsCustAccountInfo accountInfo = null;
			if (operator != null)
				accountInfo = (StarsCustAccountInfo) operator.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
			else
				accountInfo = (StarsCustAccountInfo) session.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
            if (accountInfo == null) return null;

            String action = req.getParameter("action");
            StarsSwitchCommand command = new StarsSwitchCommand();

            if (action.equalsIgnoreCase("DisableService")) {
                String periodStr = req.getParameter("OptOutPeriod");
                int period = 0;
                if (periodStr != null)
                    try {
                        period = Integer.parseInt(periodStr);
                    }
                    catch (NumberFormatException e) {}

                if (period > 0) {
                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.DATE, period);

                    StarsDisableService service = new StarsDisableService();
                    service.setReEnableDateTime( now.getTime() );

                    StarsInventories inventories = accountInfo.getStarsInventories();
                    for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
                        StarsLMHardware hardware = inventories.getStarsLMHardware(i);
                        service.addSerialNumber( hardware.getManufactureSerialNumber() );
                    }

                    command.setStarsDisableService( service );
                }
            }
            else if (action.equalsIgnoreCase("EnableService")) {
                Calendar now = Calendar.getInstance();

                StarsEnableService service = new StarsEnableService();
                service.setEnableDateTime( now.getTime() );

                StarsInventories inventories = accountInfo.getStarsInventories();
                for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
                    StarsLMHardware hardware = inventories.getStarsLMHardware(i);
                    service.addSerialNumber( hardware.getManufactureSerialNumber() );
                }

                command.setStarsEnableService( service );
            }

            StarsOperation operation = new StarsOperation();
            operation.setStarsSwitchCommand( command );
            
            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) return failure.getStatusCode();
			
            if (operation.getStarsSwitchCommandResponse() == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            	
            // Update hardware history
            StarsLMHardwareHistory hwHist = operation.getStarsSwitchCommandResponse().getStarsLMHardwareHistory();
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsCustAccountInfo accountInfo = null;
			if (operator != null)
				accountInfo = (StarsCustAccountInfo) operator.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
			else
				accountInfo = (StarsCustAccountInfo) session.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
				
			StarsInventories inventories = accountInfo.getStarsInventories();
			for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
				StarsLMHardware hw = inventories.getStarsLMHardware(i);
				hw.setStarsLMHardwareHistory( hwHist );
			}
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }

    public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();
            
            /* This part is for consumer login, must be removed later */
            Integer energyCompanyID = (Integer) session.getAttribute("ENERGY_COMPANY_ID");
            StarsOperator operator = null;
            com.cannontech.database.data.starscustomer.CustomerAccount account = null;

            if (energyCompanyID == null) {
                operator = (StarsOperator) session.getAttribute("OPERATOR");
                if (operator != null)
                	account = (com.cannontech.database.data.starscustomer.CustomerAccount)
                			operator.getAttribute("CUSTOMER_ACCOUNT");
            }
            else {
            	account = (com.cannontech.database.data.starscustomer.CustomerAccount)
                		session.getAttribute("CUSTOMER_ACCOUNT");
            }
            
            if (account == null) {
                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
                failure.setDescription("Session invalidated, please login again");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

            PILConnectionServlet connContainer = (PILConnectionServlet)
                    session.getAttribute( PILConnectionServlet.SERVLET_CONTEXT_ID );
            if (connContainer == null) {
                logger.error("YukonSwitchCommandAction: Failed to retrieve PILConnectionServlet from servlet context");

                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_OPERATION_FAILED );
                failure.setDescription("Failed to send Yukon switch command");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

            ClientConnection conn = connContainer.getConnection();
            if (conn == null) {
                logger.error( "YukonSwitchCommandAction: Failed to retrieve a connection" );

                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_OPERATION_FAILED );
                failure.setDescription("Failed to send Yukon switch command");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            StarsSwitchCommand command = reqOper.getStarsSwitchCommand();
            StarsSwitchCommandResponse cmdResp = new StarsSwitchCommandResponse();
            
            if (command.getStarsDisableService() != null) {
                StarsDisableService service = command.getStarsDisableService();

                for (int i = 0; i < service.getSerialNumberCount(); i++) {
                    String cmd = "putconfig service out serial " + service.getSerialNumber(i);
                    sendCommand(cmd, conn);
                }
                
                Vector invVct = account.getInventoryVector();
                for (int j = 0; j < invVct.size(); j++) {
                	com.cannontech.database.data.starshardware.LMHardwareBase hw =
                			(com.cannontech.database.data.starshardware.LMHardwareBase) invVct.elementAt(j);
                	com.cannontech.database.db.starshardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
                			
                	if (hwDB.getManufacturerSerialNumber().equalsIgnoreCase( service.getSerialNumber(j) )) {
                		com.cannontech.database.data.multi.MultiDBPersistent multiDB =
                				new com.cannontech.database.data.multi.MultiDBPersistent();
                				
                		com.cannontech.database.db.starsevent.LMHardwareActivity event =
                				new com.cannontech.database.db.starsevent.LMHardwareActivity();
                		event.setInventoryID( hwDB.getInventoryID() );
                		event.setActionID( new Integer(com.cannontech.database.db.starscustomer.CustomerAction.TEMPORARY_TERMINATION) );
                		event.setEventDateTime( new Date() );
                		event.setNotes("");
                		multiDB.getDBPersistentVector().addElement( event );
						
                		event = new com.cannontech.database.db.starsevent.LMHardwareActivity();
                		event.setInventoryID( hwDB.getInventoryID() );
                		event.setActionID( new Integer(com.cannontech.database.db.starscustomer.CustomerAction.FUTURE_ACTIVATION) );
                		event.setEventDateTime( service.getReEnableDateTime() );
                		event.setNotes("");
                		multiDB.getDBPersistentVector().addElement( event );
                		
		                Vector appVct = account.getApplianceVector();
		                for (int k = 0; k < appVct.size(); k++) {
		                	com.cannontech.database.data.starsappliance.ApplianceBase app =
		                			(com.cannontech.database.data.starsappliance.ApplianceBase) appVct.elementAt(k);
			                com.cannontech.database.db.starshardware.LMHardwareConfiguration config = app.getLMHardwareConfig();
			                
			                if (config.getInventoryID().intValue() == hwDB.getInventoryID().intValue()) {
			                	com.cannontech.database.data.device.lm.LMProgramBase program = app.getLMProgram();
					            if (program.getPAObjectID().intValue() == com.cannontech.database.db.starsappliance.ApplianceBase.NONE_INT)
					                continue;
					                
					            com.cannontech.database.db.starsevent.LMProgramCustomerActivity activity =
					            		new com.cannontech.database.db.starsevent.LMProgramCustomerActivity();
					            activity.setLMProgramID( program.getPAObjectID() );
					            activity.setAccountID( account.getCustomerAccount().getAccountID() );
					            activity.setActionID( new Integer(com.cannontech.database.db.starscustomer.CustomerAction.TEMPORARY_TERMINATION) );
					            activity.setEventDateTime( new Date() );
					            activity.setNotes("");
					            multiDB.getDBPersistentVector().addElement( activity );
					            
					            activity = new com.cannontech.database.db.starsevent.LMProgramCustomerActivity();
					            activity.setLMProgramID( program.getPAObjectID() );
					            activity.setAccountID( account.getCustomerAccount().getAccountID() );
					            activity.setActionID( new Integer(com.cannontech.database.db.starscustomer.CustomerAction.FUTURE_ACTIVATION) );
					            activity.setEventDateTime( service.getReEnableDateTime() );
					            activity.setNotes("");
					            multiDB.getDBPersistentVector().addElement( activity );
			                }
		                }
                		
                		Transaction transaction = Transaction.createTransaction( Transaction.INSERT, multiDB );
                		transaction.execute();
                		
						StarsLMHardwareHistory hwHist = com.cannontech.database.data.starsevent.LMHardwareActivity.getStarsLMHardwareHistory( hwDB.getInventoryID() );
						cmdResp.setStarsLMHardwareHistory( hwHist );
						
                		break;
                	}
                }
            }
            else if (command.getStarsEnableService() != null) {
                StarsEnableService service = command.getStarsEnableService();

                for (int i = 0; i < service.getSerialNumberCount(); i++) {
                    String cmd = "putconfig service in serial " + service.getSerialNumber(i);
                    sendCommand(cmd, conn);
                }

                session.setAttribute("PROGRAM_STATUS", "In Service");
            }

            respOper.setStarsSwitchCommandResponse( cmdResp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void sendCommand(String command, ClientConnection conn)
    {
        com.cannontech.message.porter.message.Request req = // no need for deviceid so send 0
            new com.cannontech.message.porter.message.Request( 0, command, userMessageIDCounter++ );

        conn.write(req);

        logger.info( "YukonSwitchCommandAction: Sent command to PIL: " + command );
    }
}