package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.util.UpdateUtil;
import com.cannontech.message.util.Command;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

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
		Writer out = resp.getWriter();
		RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
		if(!rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_CONTROL, user)) {
			CTILogger.info("Control request received by user without "+ YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_CONTROL.toString() + ", ip: " + req.getRemoteAddr());
			out.write("error");
			return;
		}
		
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
		
		if(!UpdateUtil.isControllable(id)) {
			out.write("error");
			return;
		}
		
		PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);
		LitePoint lp = pointDao.getLitePoint(id);
		
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
        List<Integer> opArgList = new ArrayList<Integer>(4);
		opArgList.add(-1);  // this is the ClientRegistrationToken
		opArgList.add(deviceID);
		opArgList.add(pointID);
		opArgList.add(rawstate);
			
		Command cmd = new Command();
		cmd.setUserName(user.getUsername());
		cmd.setOperation( Command.CONTROL_REQUEST );
		cmd.setOpArgList( opArgList );
		cmd.setTimeStamp( new java.util.Date() );
		
        IServerConnection conn = ConnPool.getInstance().getDefDispatchConn();
		if(!conn.isValid()) {
			CTILogger.info("Control request received but discarded, connection with dispatch is invalid");
			out.write("error");
			return;
		}
		
		conn.write(cmd);
		
		CTILogger.info("Control request sent, deviceid: " + deviceID + " pointid: " + pointID + " rawstate: " + rawstate);
	}
}
