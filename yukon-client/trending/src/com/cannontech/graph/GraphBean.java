package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (5/16/2001 5:21:13 PM)
 * 
 * @author:
 */

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.activity.ActivityLogSummary;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.graph.buffer.html.HTMLBuffer;
import com.cannontech.graph.buffer.html.PeakHtml;
import com.cannontech.graph.buffer.html.TabularHtml;
import com.cannontech.graph.buffer.html.UsageHtml;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.util.ServletUtil;
import com.cannontech.util.SessionAttribute;
import com.google.common.collect.Lists;

public class GraphBean extends Graph {
    private String start = "";
    private String format = "png";
    private int page = 1;

    /**
     * GraphClient constructor comment.
     */
    public GraphBean() {
        super();
        initialize();
    }

    /**
     * Returns an html String based on the current TrendModel selected.
     * The HTMLBuffer instanceof determines which type of buffer is generated.
     * 
     * @param htmlBuffer com.cannontech.graph.buffer.html.HTMLBuffer
     * @return String
     */
    private String buildHTMLBuffer(HTMLBuffer htmlBuffer) {
        StringBuffer returnBuffer = null;
        try {
            returnBuffer = new StringBuffer("<HTML><CENTER>");

            TrendModel tModel = getTrendModel();
            {
                htmlBuffer.setModel(tModel);

                if (htmlBuffer instanceof TabularHtml) {
                    GregorianCalendar tempCal = new GregorianCalendar();
                    tempCal.setTime((Date) tModel.getStartDate().clone());
                    tempCal.add(Calendar.DATE, (page - 1));
                    ((TabularHtml) htmlBuffer).setTabularStartDate(tempCal.getTime());

                    if (!((tModel.getTrendProps().getOptionsMaskSettings() & GraphRenderers.EVENT_MASK) == GraphRenderers.EVENT_MASK)) {
                        tempCal.add(Calendar.DATE, 1); // incr date by one
                    } else {
                        tempCal.setTime((Date) tModel.getStopDate().clone());
                    }
                    ((TabularHtml) htmlBuffer).setTabularEndDate(tempCal.getTime());

                    ((TabularHtml) htmlBuffer).setResolution(getTrendProperties().getResolutionInMillis());
                }

                htmlBuffer.getHtml(returnBuffer);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return returnBuffer.toString();
    }

    /**
     * Method getFormat.
     * 
     * @return String
     */
    public String getFormat() {
        return format;
    }

    /**
     * Method setOption.
     * 
     * @param newOption int
     */
    public void setOption(int newOption) {
        getTrendProperties().setOptionsMaskSettings(newOption);
    }

    /**
     * Method setPeriod.
     * 
     * @param newPeriod java.lang.String
     */
    @Override
    public void setPeriod(String newPeriod) {
        // Actually compares the periods in super and this so we
        // know if the page must be set in this.
        if (!newPeriod.equalsIgnoreCase(getPeriod())) {
            super.setPeriod(newPeriod);
            setPage(1);
        }
    }

    /**
     * Method setGdefid.
     * 
     * @param newGdefid int
     */
    public void setGdefid(int newGdefid) {
        if (newGdefid != getGdefid()) {
            setGraphDefinition(newGdefid);
        }
    }

    /**
     * Method getGdefid.
     * 
     * @return int
     */
    public int getGdefid() {
        if (getGraphDefinition() != null) {
            return getGraphDefinition().getGraphDefinition().getGraphDefinitionID().intValue();
        } else {
            return -1;
        }
    }

    /**
     * Method setPointID.
     * 
     * @param newPointid int
     */
    public void setPointid(int newPointID) {
        if (newPointID != getPointid()) {
            setPointIDs(newPointID);
        }
    }

    /**
     * Method getPointID.
     * 
     * @return int
     */
    public int getPointid() {
        if (getPointIDs() != null) {
            return getPointIDs()[0];
        } else {
            return -1;
        }
    }

    /**
     * Method setStart.
     * 
     * @param newStart String
     */
    public void setStart(String newStart) {
        start = newStart;
        setStartDate(ServletUtil.parseDateStringLiberally(start));
    }

    /**
     * Method initialize.
     */
    private void initialize() {
        // Don't attempt to load any properties from a file.
        setTrendProperties(new com.cannontech.graph.model.TrendProperties(false));
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/9/00 1:19:04 PM)
     * 
     * @param args java.lang.String[]
     */
    public static void main(String[] args) {
        System.setProperty("cti.app.name", "Trending");
        GraphBean gb = new GraphBean();
    }

    /**
     * Method setFormat.
     * 
     * @param newFormat java.lang.String
     */
    public void setFormat(String newFormat) {
        if (!newFormat.equalsIgnoreCase(format)) {
            format = newFormat;
        }
    }

    /**
     * Method getPage.
     * 
     * @return int
     */
    public int getPage() {
        return page;
    }

    /**
     * Method setPage.
     * 
     * @param newPage int
     */
    public void setPage(int newPage) {
        if (page != newPage) {
            setUpdateTrend(true);
            page = newPage;
        }
    }

    /**
     * Method getNumDays.
     * 
     * @return int
     */
    public int getNumDays() {
        int numDays = com.cannontech.common.util.TimeUtil.absDifferenceInDays(getStartDate(), getStopDate());
        return numDays;
    }

    /**
     * Method updateCurrentPane.
     */
    public void updateCurrentPane() {
        if (getViewType() == GraphRenderers.TABULAR) {
            update(null, null);

            StringBuffer buf = new StringBuffer();
            buf.append(buildHTMLBuffer(new TabularHtml()));

            buf.append("</CENTER></HTML>");
            setHtmlString(buf);
        } else if (getViewType() == GraphRenderers.SUMMARY) {
            StringBuffer buf = new StringBuffer();

            update(null, null);
            buf.append(buildHTMLBuffer(new PeakHtml()));
            buf.append(buildHTMLBuffer(new UsageHtml()));
            buf.append("</CENTER></HTML>");

            setHtmlString(buf);
        } else {
            update(null, null);
        }
    }

    /**
     * Method encode.
     * This method will encode the graph in the outputStream.
     * 
     * @param out java.io.OutputStream
     * @throws IOException
     */
    public void encode(java.io.OutputStream out) throws IOException {
        encode(out, getFormat());
    }

    public void encode(java.io.OutputStream out, String format) throws IOException {
        if (format.equalsIgnoreCase("png")) {
            encodePng(out);
        } else if (format.equalsIgnoreCase("jpg")) {
            encodeJpeg(out);
        } else {
            encodePng(out);
        }
    }

    /**
     * Performs the error checking for getDataNow functionality based on properties.
     * 
     * @param liteYukonUser LiteYukonUser
     * @param custDevices String[] of ids
     * @return SessionAttribute (success/error message)
     */
    public SessionAttribute getDataNow(LiteYukonUser liteYukonUser, String[] custDevices) {
        EnergyCompany energyCompany = YukonSpringHook.getBean(EnergyCompanyDao.class).getEnergyCompany(liteYukonUser);
        int ecId = -1;
        if (energyCompany != null) {
            ecId = energyCompany.getId();
        }

        ActivityLogSummary actLogSummary = new ActivityLogSummary(liteYukonUser.getUserID(), ecId);
        actLogSummary.retrieve();

        for (int i = 0; i < actLogSummary.getLogSummaryVector().size(); i++) {
            ActivityLogSummary.LogSummary logSummary =
                (ActivityLogSummary.LogSummary) actLogSummary.getLogSummaryVector().get(i);
            if (logSummary.action.equals(ActivityLogActions.SCAN_DATA_NOW_ACTION)) {
                RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
                if (logSummary.count >= rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.MAXIMUM_DAILY_SCANS,
                    liteYukonUser)) {
                    return new SessionAttribute(ServletUtil.ATT_ERROR_MESSAGE,
                        "Maximum Scans allowed for today exceeded");
                }

                Date now = new Date();
                long sinceLast = now.getTime() - logSummary.maxTimeStamp.getTime();
                long duration =
                    rolePropertyDao.getPropertyLongValue(YukonRoleProperty.MAXIMUM_DAILY_SCANS, liteYukonUser) * 36000;
                if (sinceLast <= duration) {
                    long waitTime = duration - sinceLast;
                    return new SessionAttribute(ServletUtil.ATT_ERROR_MESSAGE, "Last Scan Time: "
                        + logSummary.maxTimeStamp + ". Please wait " + waitTime / 36000 + " minutes.");
                }
            }
        }

        List<LiteYukonPAObject> paObjects = Lists.newArrayList();
        String logDesc = "Forced alternate scan of deviceID(s): ";
        for (int i = 0; i < custDevices.length; i++) {
            LiteYukonPAObject litePAO =
                YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(Integer.valueOf(custDevices[i]).intValue());
            paObjects.add(litePAO);
            logDesc += litePAO.getYukonID() + ", ";
        }
        ActivityLogger.logEvent(liteYukonUser.getUserID(), ActivityLogActions.SCAN_DATA_NOW_ACTION, logDesc);
        getDataNow(paObjects);
        return new SessionAttribute(ServletUtil.ATT_CONFIRM_MESSAGE, "Alternate Scans of Selected Meters Started.");
    }
}
