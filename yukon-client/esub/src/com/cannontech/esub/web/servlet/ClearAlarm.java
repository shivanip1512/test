package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.common.constants.LoginController;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.util.Util;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.util.Command;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.conns.ConnPool;

/**
 * Clears an alarm off a list of device ids, point ids, and alarm category ids
 * 
 * One of the following is required:
 * pointid		- Comma separated list of point ids
 * deviceid		- Comma seperated list of device ids
 * alarmcategoryid - Comma seperated list of alarm category ids
 * @author alauinger
 */
public class ClearAlarm extends HttpServlet {

	// Parameter Names
	private static final String DEVICE_ID = "deviceid";
	private static final String POINT_ID = "pointid";
	private static final String ALARMCATEGORY_ID = "alarmcategoryid";
	
	/**
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		
		LiteYukonUser user = (LiteYukonUser) req.getSession(false).getAttribute(LoginController.YUKON_USER);	
		
		int[] deviceIds = StringUtils.parseIntString(req.getParameter(DEVICE_ID));
		int[] pointIds = StringUtils.parseIntString(req.getParameter(POINT_ID)); 
		int[] alarmCategoryIds = StringUtils.parseIntString(req.getParameter(ALARMCATEGORY_ID));
		
		// A single Command message can ackknowledge any number of Signals(alarms)
		// Create one, fill it up, then send it to Dispatch
		Command cmd = createAckCommand(user);
		for(int i = 0; i < deviceIds.length; i++) {
			ackDevice(deviceIds[i], cmd);
		}
		for(int i = 0; i < pointIds.length; i++) {
			ackPoint(pointIds[i], cmd);
		}
		for(int i = 0; i < alarmCategoryIds.length; i++) {
			ackAlarmCategory(alarmCategoryIds[i], cmd);
		}
        ConnPool.getInstance().getDefDispatchConn().write(cmd);
}
	

	/** 
	 * Add signals to an acknowledge command message for a given device.
	 * and send it
	 * @param deviceID
	 * @param user
	 */
	private void ackDevice(int deviceId, Command cmd) {
        List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(deviceId);
        for (LitePoint point : points) {
			ackPoint(point.getPointID(), cmd);	
		}
	}
	
	/**
	 * Add signals to an acknowledge command message for a given point
	 * @param pointId
	 * @param cmd
	 */
	private void ackPoint(int pointId, Command cmd) {
        DynamicDataSource dds = (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");
		List sigList = new ArrayList<Signal>(dds.getSignals(pointId));
		ackSignals(sigList, cmd);
	}
	
	/**
	 * Add signals to an acknowledge command message for a given
	 * alarm category
	 * @param alarmCategoryId
	 * @param cmd
	 */
	private void ackAlarmCategory(int alarmCategoryId, Command cmd) {
        DynamicDataSource dds = (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");
		List<Signal> sigList = new ArrayList<Signal>(dds.getSignalsByCategory(alarmCategoryId));		
		ackSignals(sigList, cmd);
	}
	
	/**
	 * Takes a list of Signals and adds them to an acknowledge
	 * Command message.
	 * @param signalList
	 * @param cmd
	 */
	private void ackSignals(List signalList, Command cmd) {
		List<Integer> argList = cmd.getOpArgList();
		if(signalList != null) {
			Iterator iter = signalList.iterator();
			while(iter.hasNext()) {
				Signal sig = (Signal) iter.next();
				argList.add(sig.getPointID());
				argList.add(sig.getCondition());
			}
		}				
	}
	
	/**
	 * Creates a Command message to acknowledge signals
	 * @param user	Yukon user to attach to the Command
	 * @return
	 */
	private Command createAckCommand(LiteYukonUser user) {
		Command cmd = new Command();
		cmd.setOperation(Command.ACKNOWLEGDE_ALARM);
        List<Integer> argList = new ArrayList<Integer>(1);
        argList.add(-1);
        
		cmd.setOpArgList(argList);
		cmd.setUserName(user.getUsername());
		
		return cmd;
	}
}
