package com.cannontech.esub.web.servlet;

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

import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.cache.functions.AlarmFuncs;
import com.cannontech.message.dispatch.message.Signal;

/**
 * Returns true if alarm audio should be active.
 * Also can mute alarm audio for the current user.
 * @author alauinger
 */
public class AlarmAudioServlet extends HttpServlet {

    public static final String PARAM_MUTE_ARG = "mute";
    
    public static final String PARAM_DISPLAY_NAME_ARG = "display";
    
    private static final String PARAM_DEVICE_ID = "deviceid";
    private static final String PARAM_POINT_ID = "pointid";
    private static final String PARAM_ALARMCATEGORY_ID = "alarmcategoryid";
    
    private static String MUTE_TIMESTAMP_SESSION_KEY = "MUTED_DISPLAYS";

    /**
     * @see javax.servlet.http.HttpServlet#service(HttpServletRequest,
     *      HttpServletResponse)
     */
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

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
        
        String deviceIdStr = req.getParameter(PARAM_DEVICE_ID);
        String pointIdStr = req.getParameter(PARAM_POINT_ID);
        String alarmCategoryIdStr = req.getParameter(PARAM_ALARMCATEGORY_ID);

        int[] deviceIds = StringUtils.parseIntString(deviceIdStr);
        int[] pointIds = StringUtils.parseIntString(pointIdStr);
        int[] alarmCategoryIds = StringUtils.parseIntString(alarmCategoryIdStr);

        List deviceSigs = AlarmFuncs.getSignalsForPao(deviceIds);
        List pointSigs = AlarmFuncs.getSignalsForPoints(pointIds);
        List alarmCategorySigs = AlarmFuncs
                .getSignalsForAlarmCategories(alarmCategoryIds);

        List allSigs = new LinkedList();
        allSigs.addAll(deviceSigs);
        allSigs.addAll(pointSigs);
        allSigs.addAll(alarmCategorySigs);
        Date lastAlarmTimestamp = findLatestTimestamp(allSigs);

        // Possibly flag the client to start alarm audio playing
        if (lastAlarmTimestamp != null && audioEnabled) {
            Date lastMuteTimestamp = (Date) session.getAttribute(MUTE_TIMESTAMP_SESSION_KEY);
            if(lastMuteTimestamp == null ||
                    lastMuteTimestamp.before(lastAlarmTimestamp)) {
                audioSounding = true;
            }
        }

        Writer writer = resp.getWriter();
        writer.write(Boolean.toString(audioSounding));
    }

    private Date findLatestTimestamp(List signals) {
        Date d = null;
        for (Iterator iter = signals.iterator(); iter.hasNext();) {
            Signal signal = (Signal) iter.next();
            if(TagUtils.isAlarmUnacked(signal.getTags())) {
                if (d == null || d.before(signal.getTimeStamp())) {
                    d = signal.getTimeStamp();
                }
            }
        }
        return d;
    }
}
