package com.cannontech.servlet;

/**
 * Acknowledges a mandatory curtailment.
 * Sends a message to the loadcontrol server to ack a mandatory curtailment
 * Verifies that the customer that is doing
 * the acknowledgement is legit in doing so.
 *
 * Parameters:
 * CUSTOMERID - The specific contacts customer ID
 * CURTAILID  - The id of the Curtailment action, primary key of LMCurtailCustomerActivity
 * ACKTIME    - The time of the acknowledgement, in seconds since 1970
 *
 * Creation date: (4/2/2001 3:20:34 PM)
 * @author: Aaron Lauinger
 */

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

public class CurtailmentServlet extends javax.servlet.http.HttpServlet {

	private static final String LOGFILE = "login.log";
	
	private static final String ACK_SQL = "UPDATE LCCurtailCustomerActivity SET AcknowledgementStatus=?,AckDateTime=?,IPAddressOfAckUser=?,UserIDName=? WHERE CustomerID=? AND CurtailReferenceID=?";
	private static final String ACK_STATUS = "Acknowledged";
	
/**
 * CurtailmentServlet constructor comment.
 */
public CurtailmentServlet() {
	super();
}
/**
 * Creation date: (4/2/2001 3:53:57 PM)
 * @return boolean
 * @param curtailEventID int
 * @param ackTime java.util.Date
 * @param clientInfo java.lang.String
 */
private boolean doAck(String username, int customerID, int curtailEventID, java.util.Date ackTime, String clientInfo, String initials,String dbAlias) {

	LCConnectionServlet connServlet = (LCConnectionServlet) getServletContext().getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);

	com.cannontech.loadcontrol.messages.LMCurtailmentAcknowledgeMsg msg =
	 	new com.cannontech.loadcontrol.messages.LMCurtailmentAcknowledgeMsg();

	msg.setAcknowledgeStatus(com.cannontech.loadcontrol.messages.LMCurtailmentAcknowledgeMsg.ACKNOWLEDGED);
	
	if( initials != null )
		msg.setNameOfAckPerson(initials);
	else		
		msg.setNameOfAckPerson("-");
		
	msg.setCurtailReferenceID(curtailEventID);
	msg.setIpAddressOfAckUser(clientInfo);
	msg.setYukonID(customerID);
	msg.setUserIdName(username);
	msg.setCurtailmentNotes("-");
		
 	com.cannontech.loadcontrol.LoadControlClientConnection conn = connServlet.getConnection();

 	if( conn != null )
 	{
 		CTILogger.debug("Sending ack");
		CTILogger.debug("name: " + msg.getNameOfAckPerson() );
		CTILogger.debug("ref id: " + msg.getCurtailReferenceID() );
		CTILogger.debug("ip addy: " + msg.getIpAddressOfAckUser() );
		CTILogger.debug("dev id: " + msg.getYukonID() );
		CTILogger.debug("username: " + msg.getUserName() );
		CTILogger.debug("notes: " + msg.getUserName() );
	 	
 		conn.write(msg);
 		return true;
 	}
 	else
 	{
	 	return false;
 	}
}
/**
 * Creation date: (4/2/2001 3:44:57 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException 
{
	resp.sendError( javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED);
}
/**
 * Creation date: (4/2/2001 3:44:26 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException 
{
	try
	{
		javax.servlet.http.HttpSession session = ((javax.servlet.http.HttpServletRequest) req).getSession(false);
		LiteYukonUser user = (LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);
					
		String customerIDStr = req.getParameter("CUSTOMERID");
		String curtailIDStr  = req.getParameter("CURTAILID");
		String ackTimeStr    = req.getParameter("ACKTIME");
		String initials      = req.getParameter("initials");
		String redirectURI   = req.getParameter("redirect");
		 
		LiteContact contact = com.cannontech.database.cache.functions.YukonUserFuncs.getLiteContact(user.getUserID());
				
		// Confirm that the customer id passed here and the id stored
		// in the session are the same
		if( contact == null || 
			ContactFuncs.getCICustomer(contact.getContactID()).getCustomerID() != Integer.parseInt(customerIDStr) ) {
				CTILogger.error("Customer id of the current user doesn't match that of the request");
				return;				
			}

		if( 
		!doAck( 	user.getUsername(), 
			ContactFuncs.getCICustomer(contact.getContactID()).getCustomerID(),
			Integer.parseInt(curtailIDStr), 	
			new java.util.Date( Long.parseLong(ackTimeStr)), 
			req.getRemoteAddr(),
			initials,
			"yukon")
		)
		{
			CTILogger.error("An error occured acknowledging");
		}		
		else
		{
			// fudge
			// give the server time to update the database
			Thread.sleep(4000);
		}

		resp.sendRedirect(redirectURI);
		
	}
	catch(Throwable t )
	{
		CTILogger.error("Error processing curtailment acknowledgerequest", t);
	}
}

}
