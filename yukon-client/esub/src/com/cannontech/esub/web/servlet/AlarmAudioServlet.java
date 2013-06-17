package com.cannontech.esub.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jsonOLD.JSONObject;

import org.springframework.util.FileCopyUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.AlarmDao;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.spring.YukonSpringHook;

/**
 * Returns true if alarm audio should be active.
 * Also can mute alarm audio for the current user.
 * @author alauinger
 */
public class AlarmAudioServlet extends HttpServlet {

    public static final String PARAM_MUTE_ARG = "mute";
    public static final String PARAM_DISPLAY_NAME_ARG = "display";
    private static String MUTE_TIMESTAMP_SESSION_KEY = "MUTED_DISPLAYS";

    /**
     * @see javax.servlet.http.HttpServlet#service(HttpServletRequest,
     *      HttpServletResponse)
     */
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String referrer = req.getParameter("referrer");
        HttpSession session = req.getSession(false);

        // is audio even allowed?
        boolean audioEnabled = true; // TODO: check roleproperty
        // should audio be playing now?
        boolean audioSounding = false;

        String mute = req.getParameter(PARAM_MUTE_ARG);
        
        if(mute != null && mute.equals("true")) {
            if(session != null){
                session.setAttribute(MUTE_TIMESTAMP_SESSION_KEY, new Date());
            }
            Writer w = resp.getWriter();
            w.write("ok");        
            return;
        }
        
        BufferedReader jsonDataReader = req.getReader();
        String jsonData = FileCopyUtils.copyToString(jsonDataReader);
        JSONObject object = new JSONObject(jsonData);
        String deviceIdStr = object.getString("deviceIds");
        String pointIdStr = object.getString("pointIds");
        String alarmCategoryIdStr = object.getString("alarmCategoryIds");

        List<Integer> deviceIds = StringUtils.parseIntStringForList(deviceIdStr);
        List<Integer> pointIds = StringUtils.parseIntStringForList(pointIdStr);
        List<Integer> alarmCategoryIds = StringUtils.parseIntStringForList(alarmCategoryIdStr);
        List<Signal> allSigs = new LinkedList<Signal>();

        try {
            List<Signal> deviceSigs = YukonSpringHook.getBean(AlarmDao.class).getSignalsForPaos(deviceIds);
            allSigs.addAll(deviceSigs);
        } catch (DynamicDataAccessException e){
            Throwable cause = e.getCause();
            if(cause.getMessage().contains("not found")){ /* Referencing bad device ids. */
                CTILogger.error("Alarm Audio Check Error: devices ( " + deviceIds + " ) not found on page: " + referrer);
            } else { /*  Maybe we lost our dispatch connection */
                CTILogger.error("Alarm Audio Check Error: could not get dynamic data.", e);
            }
        }

        try {
            List<Signal> pointSigs = YukonSpringHook.getBean(AlarmDao.class).getSignalsForPoints(pointIds);
            allSigs.addAll(pointSigs);
        } catch (DynamicDataAccessException e){
            Throwable cause = e.getCause();
            if(cause.getMessage().contains("not found")){ /* Referencing bad point ids. */
                CTILogger.error("Alarm Audio Check Error: points ( " + pointIds + " ) not found on page: " + referrer);
            } else { /*  Maybe we lost our dispatch connection */
                CTILogger.error("Alarm Audio Check Error: could not get dynamic data.", e);
            }
        }
        
        List<Signal> alarmCategorySigs = YukonSpringHook.getBean(AlarmDao.class).getSignalsForAlarmCategories(alarmCategoryIds);
        allSigs.addAll(alarmCategorySigs);
        Date lastAlarmTimestamp = findLatestTimestamp(allSigs);

        // Possibly flag the client to start alarm audio playing
        if (lastAlarmTimestamp != null && audioEnabled) {
            Date lastMuteTimestamp = (Date) session.getAttribute(MUTE_TIMESTAMP_SESSION_KEY);
            if(lastMuteTimestamp == null || lastMuteTimestamp.before(lastAlarmTimestamp)) {
                audioSounding = true;
            }
        }
        
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache, must-revalidate, no-store");

        Writer writer = resp.getWriter();
        writer.write(Boolean.toString(audioSounding));
    }

    private Date findLatestTimestamp(List<Signal> signals) {
        Date d = null;
        for (Iterator<Signal> iter = signals.iterator(); iter.hasNext();) {
            Signal signal = iter.next();
            //find out why there is a null in the list!
            if(signal != null) {
                if(TagUtils.isAlarmUnacked(signal.getTags())) {
                    if (d == null || d.before(signal.getTimeStamp())) {
                        d = signal.getTimeStamp();
                    }
                }
            }
        }
        return d;
    }
}
