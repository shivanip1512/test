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

import com.cannontech.common.util.LogWriter;
import com.cannontech.database.data.web.User;

public class CurtailmentServlet extends javax.servlet.http.HttpServlet {

	private static final String LOGFILE = "login.log";
	
	private static final String ACK_SQL = "UPDATE LCCurtailCustomerActivity SET AcknowledgementStatus=?,AckDateTime=?,IPAddressOfAckUser=?,UserIDName=? WHERE CustomerID=? AND CurtailReferenceID=?";
	private static final String ACK_STATUS = "Acknowledged";
	private LogWriter logger = null;
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
	 	System.out.println("name: " + msg.getNameOfAckPerson() );
	 	System.out.println("ref id: " + msg.getCurtailReferenceID() );
	 	System.out.println("ip addy: " + msg.getIpAddressOfAckUser() );
	 	System.out.println("dev id: " + msg.getYukonID() );
	 	System.out.println("username: " + msg.getUserName() );
	 	System.out.println("notes: " + msg.getUserName() );
	 	
 		conn.write(msg);
 		logger.log("Ack sent, username: " + username + " curtailid: " + curtailEventID + " ip: " + clientInfo, LogWriter.INFO );
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
		User user = null;
	
		if (session == null || (user = (User) session.getValue("USER") ) == null )
		{
			resp.sendRedirect("/login.jsp");
			return;
		}
			
		String customerIDStr = req.getParameter("CUSTOMERID");
		String curtailIDStr  = req.getParameter("CURTAILID");
		String ackTimeStr    = req.getParameter("ACKTIME");
		String initials      = req.getParameter("initials");
		String redirectURI   = req.getParameter("redirect");
		 
		// Confirm that the customer id passed here and the id stored
		// in the session are the same
		if( user.getCustomerId() != Integer.parseInt(customerIDStr) )
		{
			resp.sendRedirect("/login.jsp");
			return;
		}

		if( 
		!doAck( 	user.getUsername(), 
			user.getCustomerId(), 	
			Integer.parseInt(curtailIDStr), 	
			new java.util.Date( Long.parseLong(ackTimeStr)), 
			req.getRemoteAddr(),
			initials,
			user.getDatabaseAlias() )
		)
		{
			logger.log("An error occured acknowledging", LogWriter.ERROR);
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
		logger.log(t,"Error processing curtailment acknowledgerequest", LogWriter.ERROR);
	}
}
/**
 * Creation date: (4/2/2001 3:46:53 PM)
 * @param config javax.servlet.ServletConfig
 */
public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException
{
	super.init(config);	
	synchronized (this)
	{		
		try
		{
			java.io.PrintWriter writer =
				new java.io.PrintWriter( new java.io.FileOutputStream("curtail.log", true ) );

			logger = new LogWriter("LoginController", com.cannontech.common.util.LogWriter.DEBUG, writer);
			logger.log("Starting up....", LogWriter.INFO );
		}
		catch( java.io.FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( java.io.IOException ioe )
		{
			ioe.printStackTrace();
		}	
	}
}
}
