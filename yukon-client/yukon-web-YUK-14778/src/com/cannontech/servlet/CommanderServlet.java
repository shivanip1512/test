package com.cannontech.servlet;

/**
 Creates a local bean, com.cannontech.gui.yc.YC, (if one not found) to contain
 the request parameter values. The calling jsp may create the bean instance to
 populate the required fields instead, though.  (Need some more work on opt 2.)
 
 A bean is used, created by the jsp or here, so that only one YC instance exists for 
 each session which can be reference throughout the session.
 Does not currently parse any of the properties from the bean, relies on 
 getting the values from the request message.
 
 If deviceID is specified, it will override any serialNumber specified!
 If routeID specified, it is only applicable for looplocate route command
  or when sending to a serialNumber as perhibited by YC.class and not by this servlet.
 (!Changes may be made to YC.class to handle route selection differently!)
 
PARAMETERS
-------------------------------------------
 deviceID           - id of the device to send the command to
 serialNumber         - serial number of the meter
 [* deviceID or serialNumber must exist.]
 routeID            - route to send command on
 command            - specific command string to send to device
 function            - what should be done (?)
 clearResult        - flag (not null)indicating to clear the resultText data field
 timeOut            - Time in millis to wait for a response to come back from YC before ignoring it.
 REDIRECT            - where the servlet should go after the post
*/

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.util.ServletUtil;
import com.cannontech.yc.bean.YCBean;


public class CommanderServlet extends javax.servlet.http.HttpServlet 
{
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException
    {    
        javax.servlet.http.HttpSession session = req.getSession(false);
        if (session == null)
        {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        /**Create a YC bean for the session if one does not already exist.*/
        YCBean localBean = (YCBean)session.getAttribute(ServletUtil.ATT_YC_BEAN);
        if(localBean == null)
        {
            session.setAttribute(ServletUtil.ATT_YC_BEAN, new YCBean());
            localBean = (YCBean)session.getAttribute(ServletUtil.ATT_YC_BEAN);
        }

        String redirectURL = ServletRequestUtils.getStringParameter(req, ServletUtil.ATT_REDIRECT);

        /**deviceID(opt1) or SerialNumber(opt2) must exist!
         * deviceID/serialNumber command is sent. */
        int deviceId = ServletRequestUtils.getIntParameter(req, "deviceID", PAOGroups.INVALID);
        localBean.setLiteYukonPao(deviceId);

        String serialNumber = ServletRequestUtils.getStringParameter(req, "serialNumber", PAOGroups.STRING_INVALID);    //system command string
        localBean.setSerialNumber(serialNumber);
        
        //Flag to clear the resultText, no commands sent
        String clear = ServletRequestUtils.getStringParameter(req, "clearText");

        if (clear != null) {
            localBean.clearResultText();
            
        } else {
            String command = ServletRequestUtils.getStringParameter(req, "command");    //system command string
            //Set our yc bean command string by using a function lookup if null
            if( command == null ) {
                /** If we have no command string, see if there is a function and find it's
                 * corresponding command string.  (Will substitute based on key=value
                 * pairs found in the commands file directory for a particular device. */
                String function = ServletRequestUtils.getStringParameter(req, "function");  //user friendly command string
                if( function != null) {
                    command = localBean.getCommandFromLabel(function);
                }
            }

            if( StringUtils.isNotBlank(command) ) {
                
                try {
                    /** Specific route to send on, only used in the case of loops or serial number is used
                     * When sending to a device, the route is ignored and the porter connection takes care
                     * of sending the command on the device's assigned route. */
                    int routeID = ServletRequestUtils.getIntParameter(req, "routeID", -1);    //-1 is the default used in YCBean also
                    localBean.setRouteID(routeID);
                    
                    /** Time to wait for return to calling jsp
                     * Timeout is used to <hope to> assure there is some resultText to display when we do go back. */
                    int timeOut = ServletRequestUtils.getIntParameter(req, "timeOut", 8000);   //8 secs default 
                    int commandCount = 1 + StringUtils.countMatches(command, "&");
                    timeOut = (timeOut * commandCount);
                    localBean.setTimeOut(timeOut);
                    
                    localBean.setCommandString(command);
                    localBean.clearErrorMsg();
                    localBean.executeCommand();
        
                    /** Don't return to the jsp until we have the message or we've timed out.*/
                    while( (localBean.getRequestMessageIDs_Executing().size() > 0 && localBean.isWatchRunning())) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            CTILogger.error(e);
                        }
                    }
                    CTILogger.debug("ExecutingMessageIDs:" + localBean.getRequestMessageIDs_Executing().size() + " | Watching:" + localBean.isWatchRunning());

                } catch (PaoAuthorizationException e) {
                    localBean.setErrorMsg("You do not have permission to execute command: " + e.getPermission());
                }
            }
        }
        
        redirectURL = ServletUtil.createSafeRedirectUrl(req, redirectURL);
        
        resp.sendRedirect(redirectURL);
    }
}