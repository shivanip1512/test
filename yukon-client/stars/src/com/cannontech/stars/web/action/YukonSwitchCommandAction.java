package com.cannontech.stars.web.action;

import java.util.*;
import javax.servlet.http.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.util.XMLUtil;
import com.cannontech.servlet.PILConnectionServlet;
import com.cannontech.message.porter.ClientConnection;
import org.apache.commons.logging.Log;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class YukonSwitchCommandAction extends ActionBase {

    private Log logger = XMLUtil.getLogger( YukonSwitchCommandAction.class );

	// increment this for every message
	private static long userMessageIDCounter = 1;

    public YukonSwitchCommandAction() {
        super();
    }

    public StarsOperation build(HttpServletRequest req, HttpSession session) {
        StarsCustomerAccountInformation accountInfo =
                (StarsCustomerAccountInformation) session.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
        if (accountInfo == null) return null;

        String action = req.getParameter("action");
        StarsOperation operation = null;

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

                operation = new StarsOperation();
                operation.setStarsDisableService( service );
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

            operation = new StarsOperation();
            operation.setStarsEnableService( service );
        }

        return operation;
    }

    public boolean parse(StarsOperation operation, HttpSession session) {
        return (operation.getStarsSuccess() != null);
    }

    public StarsOperation process(StarsOperation reqOper, HttpSession session) {
        StarsOperation respOper = new StarsOperation();

        PILConnectionServlet connContainer = (PILConnectionServlet)
                session.getAttribute( PILConnectionServlet.SERVLET_CONTEXT_ID );
        if (connContainer == null) {
            logger.error("YukonSwitchCommandAction: Failed to retrieve PILConnectionServlet from servlet context");

            StarsFailure failure = new StarsFailure();
            failure.setContent("Failed to send Yukon switch command");
            respOper.setStarsFailure( failure );
            return respOper;
        }

        ClientConnection conn = connContainer.getConnection();
        if (conn == null) {
            logger.error( "YukonSwitchCommandAction: Failed to retrieve a connection" );

            StarsFailure failure = new StarsFailure();
            failure.setContent("Failed to send Yukon switch command");
            respOper.setStarsFailure( failure );
            return respOper;
        }

        if (reqOper.getStarsDisableService() != null) {
            StarsDisableService service = reqOper.getStarsDisableService();

            for (int i = 0; i < service.getSerialNumberCount(); i++) {
                String command = "putconfig service out serial " + service.getSerialNumber(i);
                sendCommand(command, conn);
            }

            session.setAttribute("PROGRAM_STATUS", "Out of Service");
        }
        else if (reqOper.getStarsEnableService() != null) {
            StarsEnableService service = reqOper.getStarsEnableService();

            for (int i = 0; i < service.getSerialNumberCount(); i++) {
                String command = "putconfig service in serial " + service.getSerialNumber(i);
                sendCommand(command, conn);
            }

            session.setAttribute("PROGRAM_STATUS", "In Service");
        }

        StarsSuccess success = new StarsSuccess();
        success.setContent( "Yukon switch command has been sent successfully" );
        respOper.setStarsSuccess( success );

        return respOper;
    }

    private void sendCommand(String command, ClientConnection conn)
    {
        com.cannontech.message.porter.message.Request req = // no need for deviceid so send 0
            new com.cannontech.message.porter.message.Request( 0, command, userMessageIDCounter++ );

        conn.write(req);

        logger.info( "YukonSwitchCommandAction: Sent command to PIL: " + command );
    }
}