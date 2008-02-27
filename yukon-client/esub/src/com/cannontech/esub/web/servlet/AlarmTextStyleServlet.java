package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.message.dispatch.message.Signal;

/**
 * AlarmTextStyleServlet chooses between two svg styles given a list of point ids 
 * 
 * will take a list of point ids and two svg styles
 * If none of the points are in alarm then style 1 will be returned
 * Otherwise style2 will be returned.
 * 
 * Required Parameters:
 * deviceid - 		The list of device ids, comma separated ie "32,43,54"
 * pointid -		The list of point ids
 * alarmcategoryid - The list of alarm category ids
 * style1 -		The style to return if none of the points are in alarm
 * style2 - 	The style to return if at least one of the points are in alarm
 *
 * Example:
 * 
 * @author alauinger
 */
public class AlarmTextStyleServlet extends HttpServlet {

	private static final String DEVICE_ID_KEY = "deviceid";
	private static final String POINT_ID_KEY = "pointid";
	private static final String ALARMCATEGORY_ID_KEY = "alarmcategoryid";
	private static final String FILL1_KEY = "fill1";
	private static final String FILL2_KEY = "fill2";
		
	/**
	 * TODO: combine this updating code and the same in DrawingUpdater
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		
		String deviceIdStr = req.getParameter(DEVICE_ID_KEY);
		String pointIdStr = req.getParameter(POINT_ID_KEY);
		String alarmCategoryStr = req.getParameter(ALARMCATEGORY_ID_KEY);
		String fill1 = req.getParameter(FILL1_KEY);
		String fill2 = req.getParameter(FILL2_KEY);
	
		/* check if any of the points are in alarm*/		
		int[] deviceIds = StringUtils.parseIntString(deviceIdStr);
		int[] pointIds = StringUtils.parseIntString(pointIdStr);	
		int[] alarmCategoryIds = StringUtils.parseIntString(alarmCategoryStr);
		
		boolean inAlarm = false;
		
		breakDevice:
		for(int j = 0; j < deviceIds.length; j++) {
			List<Signal> deviceSignals = DaoFactory.getAlarmDao().getSignalsForPao(deviceIds[j]);
			for (Iterator<Signal> iter = deviceSignals.iterator(); iter.hasNext();) {
				Signal signal  = iter.next();
				// find out why there is a null in the list!
				if(signal != null) {
    				if(TagUtils.isAlarmUnacked(signal.getTags())) {
    					inAlarm = true;
    					break breakDevice;
    				}
				}
			}
		}
		breakPoint:
		for(int j = 0; !inAlarm && j < pointIds.length; j++) {
			List<Signal> pointSignals = DaoFactory.getAlarmDao().getSignalsForPoint(pointIds[j]);
			for (Iterator<Signal> iter = pointSignals.iterator(); iter.hasNext();) {
				Signal signal = iter.next();
				// find out why there is a null in the list!
				if(signal != null) {
    				if(TagUtils.isAlarmUnacked(signal.getTags())) {
    					inAlarm = true;
    					break breakPoint;
    				}
				}
			}
		}
		breakAlarmCategory:
		for(int j = 0; !inAlarm && j < alarmCategoryIds.length; j++) {
			List<Signal> alarmCategorySignals = DaoFactory.getAlarmDao().getSignalsForAlarmCategory(alarmCategoryIds[j]);
			for (Iterator<Signal> iter = alarmCategorySignals.iterator(); iter.hasNext();) {
				Signal signal = iter.next();
				// find out why there is a null in the list!
				if(signal != null) {
    				if(TagUtils.isAlarmUnacked(signal.getTags())) {
    					inAlarm = true;
    					break breakAlarmCategory;
    				}
				}
			}
		}
		
		/* write the correct svg style */
		Writer writer = resp.getWriter();
		
		if(!inAlarm) { 
			writer.write(fill1);
		}
		else {
			writer.write(fill2);
		}
		
		writer.flush();
	}

	/**
	 * @see javax.servlet.Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig cfg) throws ServletException {
		super.init(cfg);							
	}
}
