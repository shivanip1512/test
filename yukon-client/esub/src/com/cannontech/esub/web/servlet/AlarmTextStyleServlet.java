package com.cannontech.esub.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONObject;

import org.springframework.util.FileCopyUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.AlarmDao;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.messaging.message.dispatch.SignalMessage;
import com.cannontech.spring.YukonSpringHook;
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
	/**
	 * TODO: combine this updating code and the same in DrawingUpdater
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String referrer = req.getParameter("referrer");
	    BufferedReader jsonDataReader = req.getReader();
        String jsonData = FileCopyUtils.copyToString(jsonDataReader);
        JSONObject object = new JSONObject(jsonData);
        String deviceIdStr = object.getString("deviceIds");
        String pointIdStr = object.getString("pointIds");
        String alarmCategoryIdStr = object.getString("alarmCategoryIds");
        String fill1 = object.getString("fill1");
        String fill2 = object.getString("fill2");
        boolean inAlarm = false;
	
        /* check if any of the points on these devices are in alarm*/
        List<Integer> deviceIds = StringUtils.parseIntStringForList(deviceIdStr);
        try {
            List<SignalMessage> deviceSignals = YukonSpringHook.getBean(AlarmDao.class).getSignalsForPaos(deviceIds);
            for (Iterator<SignalMessage> iter = deviceSignals.iterator(); iter.hasNext();) {
                SignalMessage signal  = iter.next();
                // find out why there is a null in the list!
                if(signal != null) {
                    if(TagUtils.isAlarmUnacked(signal.getTags())) {
                        inAlarm = true;
                        writeTextStyle(resp, fill1, fill2, inAlarm);
                        return;
                    }
                }
            }
        } catch (DynamicDataAccessException e){
            Throwable cause = e.getCause();
            if(cause.getMessage().contains("not found")){ /* Referencing bad device ids. */
                CTILogger.error("AlarmText Error: devices ( " + deviceIds + " ) not found on page: " + referrer);
            } else { /*  Maybe we lost our dispatch connection */
                CTILogger.error("AlarmText Error: could not get dynamic data.", e);
            }
        }

        /* check if any of the points are in alarm*/    
        List<Integer> pointIds = StringUtils.parseIntStringForList(pointIdStr);
        try {
            List<SignalMessage> pointSignals = YukonSpringHook.getBean(AlarmDao.class).getSignalsForPoints(pointIds);
            for (Iterator<SignalMessage> iter = pointSignals.iterator(); iter.hasNext();) {
                SignalMessage signal = iter.next();
                // find out why there is a null in the list!
                if(signal != null) {
                    if(TagUtils.isAlarmUnacked(signal.getTags())) {
                        inAlarm = true;
                        writeTextStyle(resp, fill1, fill2, inAlarm);
                        return;
                    }
                }
            }
        } catch (DynamicDataAccessException e){
            Throwable cause = e.getCause();
            if(cause.getMessage().contains("not found")){ /* Referencing bad point ids. */
                CTILogger.error("AlarmText Error: points ( " + pointIds + " ) not found on page: " + referrer);
            } else { /*  Maybe we lost our dispatch connection */
                CTILogger.error("AlarmText Error: could not get dynamic data.", e);
            }
        }
        
        /* check if any of the alarm categories are in alarm*/
        List<Integer> alarmCategoryIds = StringUtils.parseIntStringForList(alarmCategoryIdStr);
        List<SignalMessage> alarmCategorySignals = YukonSpringHook.getBean(AlarmDao.class).getSignalsForAlarmCategories(alarmCategoryIds);
        for (Iterator<SignalMessage> iter = alarmCategorySignals.iterator(); iter.hasNext();) {
            SignalMessage signal = iter.next();
            // find out why there is a null in the list!
            if(signal != null) {
                if(TagUtils.isAlarmUnacked(signal.getTags())) {
                    inAlarm = true;
                    writeTextStyle(resp, fill1, fill2, inAlarm);
                    return;
                }
            }
        }
        
        writeTextStyle(resp, fill1, fill2, inAlarm);
    }
	
	private void writeTextStyle(HttpServletResponse resp, String fill1, String fill2, boolean inAlarm) throws IOException {
        Writer writer = resp.getWriter();
        if(!inAlarm) { 
            writer.write(fill1);
        } else {
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
