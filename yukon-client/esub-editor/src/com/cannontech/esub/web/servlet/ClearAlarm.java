package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.util.Util;
import com.cannontech.esub.web.SessionInfo;
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.Signal;

/**
 * Clears an alarm off a given point.
 * Requires an instance of com.cannontech.esub.web.SessionInfo to be in the session
 * 
 * One of the following is required:
 * pointid		- The point id
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
		
		SessionInfo info = (SessionInfo) req.getSession(false).getAttribute(SessionInfo.SESSION_KEY);
		
		Writer out = resp.getWriter();
		
		String pointID = req.getParameter(POINT_ID); 
				
		if(pointID != null) {
			clearPoint(Integer.parseInt(pointID), info.getUser());
		}
		
		String deviceID = req.getParameter(DEVICE_ID);
		if(deviceID != null) {
			clearDevice(Integer.parseInt(deviceID), info.getUser());
		}
		
		if(pointID == null && deviceID == null) {
			out.write("error");
			return;
		}
}

	private void clearPoint(int pointID, LiteYukonUser user) {
		Command cmd = makeCommandMsg(pointID,user);
		if(cmd != null) {
			Util.getConnToDispatch().write(cmd);
		}
	}
	
	private void clearDevice(int deviceID, LiteYukonUser user) {
		Multi multi = new Multi();
		LitePoint[] points = PAOFuncs.getLitePointsForPAObject(deviceID);
		for(int i = 0; i < points.length; i++) {
			Command cmd = makeCommandMsg(points[i].getPointID(),user);
			if(cmd != null) {
				multi.getVector().add(cmd);
			}
		}
		
		if(multi.getVector().size() > 0) {
				Util.getConnToDispatch().write(multi);			
		}
	}
	
	private Command makeCommandMsg(int pointID, LiteYukonUser user) {
		Signal sig = PointChangeCache.getPointChangeCache().getSignal(pointID);
		if(sig != null) {
			Command ackMsg = new Command();
			ackMsg.setOperation(Command.CLEAR_ALARM);
			Vector argList = new Vector();
			argList.add(new Integer(-1));
			argList.add(new Integer(pointID));
			ackMsg.setOpArgList(argList);	
			ackMsg.setUserName(user.getUsername());		
			return ackMsg;
		}
		return null;
	}
			
	
}
