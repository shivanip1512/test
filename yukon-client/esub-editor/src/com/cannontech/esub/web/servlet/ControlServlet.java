package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.util.Util;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.roles.operator.EsubDrawingsRole;

/**
 * Send out a control command.  Requires com.cannontech.roles.operator.EsubDrawingsRole.CONTROL
 * 
 * Required Parameters:
 * id		- The point id
 * rawstate - 0 or 1 (opened or closed)
 * 
 * @author alauinger
 */
public class ControlServlet extends HttpServlet {

	// Parameter Names
	private static final String ID = "id";
	private static final String RAWSTATE = "rawstate";
			
	/**
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		
		LiteYukonUser user = (LiteYukonUser) req.getSession(false).getAttribute(LoginController.YUKON_USER);
		
		if(!AuthFuncs.checkRoleProperty(user, EsubDrawingsRole.CONTROL)) {
			CTILogger.info("Control request received by user without CONTROL role, ip: " + req.getRemoteAddr());
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		
		Writer out = resp.getWriter();
		
		String idStr = req.getParameter(ID);
		String rawstateStr = req.getParameter(RAWSTATE);
		
		if( idStr == null || rawstateStr == null) {		
			out.write("error");
			return;
		}
		
		int id = -1;
		int rawstate = -1;
		 
		try {
			id = Integer.parseInt(idStr);
			rawstate = Integer.parseInt(rawstateStr);
		}
		catch(NumberFormatException nfe) {
			out.write("error");		
			return;
		}
		
		LitePoint lp = PointFuncs.getLitePoint(id);
		
		if( lp == null ) {
			out.write("error");		
			return;
		}
		
		int pointID = lp.getPointID();
		int deviceID = lp.getPaobjectID();
		
		if(pointID <= 0 || deviceID <= 0) {
			CTILogger.info("Control request received with invalid device or pointid");
			out.write("error");
			return;
		}
		/*** Start building the Command.opArgList() **************************/
		Vector opArgList = new Vector( 4 );
		opArgList.addElement( new Integer(-1) );  // this is the ClientRegistrationToken
		opArgList.addElement( new Integer((int)deviceID));
		opArgList.addElement( new Integer((int)pointID) );
		opArgList.addElement( new Integer(rawstate) );
			
		Command cmd = new Command();
		cmd.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
		cmd.setOperation( Command.CONTROL_REQUEST );
		cmd.setOpArgList( opArgList );
		cmd.setTimeStamp( new java.util.Date() );
		
		ClientConnection conn = Util.getConnToDispatch();
		if(!conn.isValid()) {
			CTILogger.info("Control request received but discarded, connection with dispatch is invalid");
			out.write("error");
			return;
		}
		
		conn.write(cmd);
		CTILogger.info("Control request sent, deviceid: " + deviceID + " pointid: " + pointID + " rawstate: " + rawstate);
	}
}
