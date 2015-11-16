package com.cannontech.web.stars;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.action.ActionBase;
import com.cannontech.web.action.CreateServiceRequestAction;
import com.cannontech.web.action.DeleteServiceRequestAction;
import com.cannontech.web.action.SendOddsForControlAction;
import com.cannontech.web.action.UpdateServiceRequestAction;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.stars.service.SwitchContextService;

public class SOAPClientController implements Controller {
    public static final String LOGIN_URL = "/login.jsp";
    public static final String HOME_URL = "/dashboard";
    private SwitchContextService switchContextService;
    
    public void setSwitchContextService(final SwitchContextService switchContextService) {
        this.switchContextService = switchContextService;
    }
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String referer = request.getHeader( "referer" );
        String action = request.getParameter( "action" );
        if (action == null) {
            action = "";
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            String location = ServletUtil.createSafeUrl(request, LOGIN_URL);
            response.sendRedirect(location);
            return null;
        }
        
        if(referer == null) {
            referer = request.getParameter( ServletUtils.ATT_REFERRER );
        }
        if(referer == null) {
            referer = ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
        }
        
        
        StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        if (user == null) {
            String location = ServletUtil.createSafeUrl(request, LOGIN_URL);
            response.sendRedirect(location);
            return null;
        }
        
        if (request.getParameter("SwitchContext") != null && request.getParameter("SwitchContext").length() > 0) {
            try{
                int memberID = Integer.parseInt( request.getParameter("SwitchContext") );
                switchContextService.switchContext(user, request, session, memberID);
                session = request.getSession( false );
            }
            catch (WebClientException e) {
                session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                String location = ServletUtil.createSafeRedirectUrl(request, referer);
                response.sendRedirect(location);
                return null;
            }
        }
        
        session.removeAttribute( ServletUtils.ATT_REDIRECT );
        session.removeAttribute( ServletUtils.ATT_ERROR_MESSAGE );
        session.removeAttribute( ServletUtils.ATT_CONFIRM_MESSAGE );
        
        SOAPMessage reqMsg = null;
        SOAPMessage respMsg = null;
        ActionBase clientAction = null;

        String nextURL = request.getContextPath() + LOGIN_URL;  // The next URL we're going to, operation succeed -> destURL, operation failed -> errorURL
        String destURL = request.getParameter( ServletUtils.ATT_REDIRECT ); // URL we should go to if action succeed
        String errorURL = request.getParameter( ServletUtils.ATT_REFERRER );    // URL we should go to if action failed
        
        // If parameter "ConfirmOnMessagePage" specified, the confirm/error message will be displayed on Message.jsp
        if (request.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null) {
            session.setAttribute( ServletUtils.ATT_MSG_PAGE_REDIRECT, destURL );
            session.setAttribute( ServletUtils.ATT_MSG_PAGE_REFERRER, errorURL );
            destURL = errorURL = request.getContextPath() + "/operator/Admin/Message.jsp";
            
            Integer delay = null;
            try {
                delay = Integer.valueOf( request.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) );
                destURL += "?delay=" + delay;
            }
            catch (NumberFormatException e) {}
        }
        
        if (action.equalsIgnoreCase("SendControlOdds")) {
            clientAction = new SendOddsForControlAction();
            if (destURL == null) {
                destURL = request.getContextPath() + "/operator/Consumer/Odds.jsp";
            }
            if (errorURL == null) {
                errorURL = request.getContextPath() + "/operator/Consumer/Odds.jsp";
            }
        } else if (action.equalsIgnoreCase("CreateWorkOrder")) {
            clientAction = new CreateServiceRequestAction();
            session.setAttribute(ServletUtils.ATT_REDIRECT, destURL);
        } else if (action.equalsIgnoreCase("UpdateWorkOrder")) {
            clientAction = new UpdateServiceRequestAction();
        } else if (action.equalsIgnoreCase("DeleteWorkOrder")) {
            clientAction = new DeleteServiceRequestAction();
        } else {
            CTILogger.info( "SOAPClient: Invalid action type '" + action + "'");
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid action type '" + action + "'");
            String location = ServletUtil.createSafeRedirectUrl(request, referer);
            response.sendRedirect(location);
            return null;
        }
        
        if (destURL == null) {
            destURL = referer;
        }
        if (errorURL == null) {
            errorURL = referer;
        }

        if (clientAction != null) {
            nextURL = errorURL;
            reqMsg = clientAction.build(request, session);

            if (reqMsg != null) {
                respMsg = clientAction.process(reqMsg, session);

                if (respMsg != null) {
                    int status = clientAction.parse(reqMsg, respMsg, session);
                    if (session.getAttribute( ServletUtils.ATT_REDIRECT ) != null) {
                        destURL = request.getContextPath() + (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
                    }
                    
                    if (status == 0) {
                        nextURL = destURL;
                    } else if (status == StarsConstants.FAILURE_CODE_SESSION_INVALID) {
                        nextURL = LOGIN_URL;
                    } else {
                        setErrorMsg( session, status );
                        nextURL = errorURL;
                    }
                } else {
                    setErrorMsg( session, StarsConstants.FAILURE_CODE_RESPONSE_NULL );
                }
            }
            else {
                if (session.getAttribute(ServletUtils.ATT_ERROR_MESSAGE) == null) {
                    setErrorMsg( session, StarsConstants.FAILURE_CODE_REQUEST_NULL );
                }
            }
        }
        
        nextURL = ServletUtil.createSafeRedirectUrl(request, nextURL);

        response.sendRedirect(nextURL);
        return null;
    }
    
    private static void setErrorMsg(HttpSession session, int status) {
        if (status == StarsConstants.FAILURE_CODE_RUNTIME_ERROR) {
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to process response message" );
        } else if (status == StarsConstants.FAILURE_CODE_REQUEST_NULL) {
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to build request message" );
        } else if (status == StarsConstants.FAILURE_CODE_RESPONSE_NULL) {
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to receive response message" );
        } else if (status == StarsConstants.FAILURE_CODE_NODE_NOT_FOUND) {
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Operation failed: invalid response message" );
        }
    }
}
