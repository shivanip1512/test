package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.common.constants.LoginController;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.util.Util;
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.message.dispatch.message.Signal;

/**
 * Clears an alarm off a given point.
 * Requires an instance of com.cannontech.esub.web.SessionInfo to be in the session
 * 
 * One of the following is required:
 * pointid		- The point id (NOT IMPLEMENTED YET)
 * deviceid		- The device id
 * @author alauinger
 */
public class ClearAlarm extends HttpServlet {

	// Parameter Names
	private static final String POINT_ID = "pointid";
	private static final String DEVICE_ID = "deviceid";
		
	/**
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		
		LiteYukonUser user = (LiteYukonUser) req.getSession(false).getAttribute(LoginController.YUKON_USER);	
		Writer out = resp.getWriter();
		
		String pointID = req.getParameter(POINT_ID); 
				
/*		if(pointID != null) {
			clearPoint(Integer.parseInt(pointID), user);
		}
*/		
		String deviceID = req.getParameter(DEVICE_ID);
		if(deviceID != null) {
			ackDevice(Integer.parseInt(deviceID), user);
		}
		
		if(pointID == null && deviceID == null) {
			out.write("error");
			return;
		}
}

	private void clearPoint(int pointID, LiteYukonUser user) {
/*		Command cmd = makeCommandMsg(pointID,user);
		if(cmd != null) {
			Util.getConnToDispatch().write(cmd);
		}
*/
	}
	
	/** 
	 * build up a command message to acknowledge all the points on a device
	 * and send it
	 * @param deviceID
	 * @param user
	 */
	private void ackDevice(int deviceID, LiteYukonUser user) {
		Command cmd = new Command();
		cmd.setOperation(Command.ACKNOWLEGDE_ALARM);
		Vector argList = new Vector();
		cmd.setOpArgList(argList);
		cmd.setUserName(user.getUsername());
		
		argList.add(new Integer(-1));
		
		LitePoint[] points = PAOFuncs.getLitePointsForPAObject(deviceID);
		PointChangeCache pcc = PointChangeCache.getPointChangeCache();
		for(int i = points.length-1; i >= 0; i--) {
			List sigList = (List) pcc.getSignals(points[i].getPointID());
			if(sigList != null) {
				Iterator iter = sigList.iterator();
				while(iter.hasNext()) {
					Signal sig = (Signal) iter.next();
					argList.add(new Integer(sig.getPointID()));
					argList.add(new Integer(sig.getCondition()));
				}
			}
		}
		
		Util.getConnToDispatch().write(cmd);			
	}
}
