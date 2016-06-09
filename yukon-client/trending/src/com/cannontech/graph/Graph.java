package com.cannontech.graph;

/**
 * The main graphing class.
 * main in this class starts up the standalone application.
 *
 * Creation date: (12/15/99 10:13:31 AM)
 * 
 * @author: Aaron Lauinger
 */
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;
import org.joda.time.Instant;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.graph.buffer.html.PeakHtml;
import com.cannontech.graph.buffer.html.TabularHtml;
import com.cannontech.graph.buffer.html.UsageHtml;
import com.cannontech.graph.exportdata.ExportDataFile;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.graph.model.TrendProperties;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.util.Command;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;
import com.klg.jclass.util.swing.encode.EncoderException;
import com.klg.jclass.util.swing.encode.page.PDFEncoder;

public class Graph implements GraphDefines {
    private long lastUpdateTime = 0;

    private Date startDate = ServletUtil.getToday();
    private int numberOfEvents = 20;

    private static final int DEFAULT_GIF_WIDTH = 556;
    private static final int DEFAULT_GIF_HEIGHT = 433;

    private int height = DEFAULT_GIF_HEIGHT;
    private int width = DEFAULT_GIF_WIDTH;

    private JFreeChart freeChart = null;
    private TrendModel trendModel = null;

    private int[] pointIDs = null;
    private GraphDefinition graphDefinition = null;
    private String period = ServletUtil.ONEDAY;
    private String title = null;

    private static final int DEFAULT_INTERVAL_RATE = 300; // rate is in seconds
    private int minIntervalRate = DEFAULT_INTERVAL_RATE;

    private boolean updateTrend = true;
    private StringBuffer htmlString = null;

    private TrendProperties trendProperties;

    // types of generations, GDEF is the original or default, POINT is for ad-hoc or no predefined gdef
    // exists.
    private static final int GDEF_GENERATION = 0;
    private static final int POINT_GENERATION = 1;

    private int generationType = GDEF_GENERATION;

    /**
     * Graph constructor comment.
     */
    public Graph() {
        super();
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/20/2001 5:14:03 PM)
     * 
     * @return com.cannontech.message.util.ClientConnection
     */
    public IServerConnection getClientConnection() {
        return ConnPool.getInstance().getDefDispatchConn();
    }

    public void encodeCSV(OutputStream out) throws java.io.IOException {
        synchronized (Graph.class) {
            ExportDataFile eDataFile =
                new ExportDataFile(getViewType(), getFreeChart(), getTrendModel().getChartName(), getTrendModel());

            eDataFile.setExtension("csv");

            String data[] = eDataFile.createCSVFormat();
            for (int i = 0; i < data.length; i++) {
                if (data[i] != null) {
                    out.write(data[i].getBytes());
                }
            }
        }
    }

    /**
     * Encodes a gif on the given stream. This method is used heavily
     * by the readmeter server and really should be as quick as possible.
     * Creation date: (10/12/00 2:54:13 PM)
     * 
     * @param out java.io.OutputStream
     */
    public void encodeJpeg(OutputStream out) throws java.io.IOException {
        synchronized (Graph.class) {
            ChartUtilities.writeChartAsJPEG(out, getFreeChart(), getWidth(), getHeight());
        }
    }

    public void encodePng(OutputStream out) throws java.io.IOException {
        synchronized (Graph.class) {
            ChartUtilities.writeChartAsPNG(out, getFreeChart(), getWidth(), getHeight());
        }
    }

    /**
     * Encodes a tabular representation on the given output stream.
     * Creation date: (11/17/00 11:14:55 AM)
     * 
     * @param out java.io.OutputStream
     */
    public void encodePeakHTML(OutputStream out) throws java.io.IOException {
        StringBuffer buf = new StringBuffer();
        PeakHtml tabHtml = new PeakHtml();

        tabHtml.setModel(getTrendModel());
        tabHtml.getHtml(buf);
        out.write(buf.toString().getBytes());
    }

    public void encodeSummmaryHTML(OutputStream out) throws java.io.IOException {
        encodePeakHTML(out);
        encodeUsageHTML(out);
    }

    public void encodePDF(OutputStream out) throws java.io.IOException {
        synchronized (Graph.class) {
            try {
                PDFEncoder encoder = new PDFEncoder();
                encoder.encode(getFreeChart().createBufferedImage(getWidth(), getHeight()), out);
                out.flush();
            } catch (IOException io) {
                io.printStackTrace();
            } catch (EncoderException ee) {
                ee.printStackTrace();
            }
        }
    }

    /**
     * Encodes a tabular representation on the given output stream.
     * Creation date: (11/17/00 11:14:55 AM)
     * 
     * @param out java.io.OutputStream
     */
    public void encodeTabularHTML(OutputStream out) throws java.io.IOException {
        StringBuffer buf = new StringBuffer();
        TabularHtml tabHtml = new TabularHtml();

        tabHtml.setModel(getTrendModel());
        tabHtml.getHtml(buf);
        out.write(buf.toString().getBytes());
    }

    /**
     * Encodes a tabular representation on the given output stream.
     * Creation date: (11/17/00 11:14:55 AM)
     * 
     * @param out java.io.OutputStream
     */
    public void encodeUsageHTML(OutputStream out) throws java.io.IOException {
        StringBuffer buf = new StringBuffer();
        UsageHtml usageHtml = new UsageHtml();

        usageHtml.setModel(getTrendModel());
        usageHtml.getHtml(buf);
        out.write(buf.toString().getBytes());
    }

    /**
     * Returns graphDefinition, but first retrieves and sets the
     * GraphDefinition to the current cache retrieval.
     * 
     * @return graphDefinition com.cannontech.database.data.graph.GraphDefinition
     */
    public GraphDefinition getGraphDefinition() {
        return graphDefinition;
    }

    /**
     * Set the graphDefinition.
     * Flags updateTrend = true (forces trend to update on every set of graphDefinition field).
     * 
     * @param graphDefinition com.cannontech.database.graph.GraphDefinition
     */
    public void setGraphDefinition(GraphDefinition newGraphDefinition) {
        if (getGraphDefinition() == null
            || getGraphDefinition().getGraphDefinition().getGraphDefinitionID().intValue() != newGraphDefinition.getGraphDefinition().getGraphDefinitionID().intValue()) {
            setUpdateTrend(true);
        } else {
            System.out.println("SAME GRAPH DEFs");
        }
        // ASSUMPTION!!! If you are setting the gDef, you must want to use it. You can only use pointIDs -OR-
        // graphDefinitionID...not both!
        setGenerationType(GDEF_GENERATION);
        graphDefinition = newGraphDefinition;
        getTrendProperties().setGdefName(graphDefinition.getGraphDefinition().getName());
    }

    /**
     * Set the graphDefinition using only the GraphDefinitionID.
     * Creates a GraphDefinition and calls setGraphDefinition(GraphDefinition)
     * 
     * @param graphDefinitionID int values of the GraphDefinition
     */
    public void setGraphDefinition(LiteGraphDefinition liteGraphDefinition) {
        if (liteGraphDefinition != null) {
            GraphDefinition gDef = (GraphDefinition) LiteFactory.createDBPersistent(liteGraphDefinition);
            try {
                Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, gDef);
                gDef = (GraphDefinition) t.execute();
            } catch (Exception e) {
                CTILogger.error(e.getMessage(), e);
            }
            setGraphDefinition(gDef);
        }
    }

    /**
     * Set the graphDefinition using only the GraphDefinitionID.
     * Creates a GraphDefinition and calls setGraphDefinition(GraphDefinition)
     * 
     * @param graphDefinitionID int values of the GraphDefinition
     */
    public void setGraphDefinition(int liteGraphDefinitionID) {
        if (liteGraphDefinitionID > 0) {
            LiteGraphDefinition liteGraphDef =
                YukonSpringHook.getBean(GraphDao.class).getLiteGraphDefinition(liteGraphDefinitionID);
            setGraphDefinition(liteGraphDef);
        } else {
            graphDefinition = null;
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (5/17/2001 4:49:15 PM)
     * 
     * @return TrendModel [][]
     */
    public TrendModel getTrendModel() {
        return trendModel;
    }

    /**
     * Insert the method's description here.
     * Creation date: (5/16/2001 11:23:16 AM)
     * 
     * @return String
     */
    public String getPeriod() {
        return period;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/20/2002 9:32:40 AM)
     * 
     * @return com.jrefinery.chart.JFreeChart
     */
    public synchronized JFreeChart getFreeChart() {
        if (freeChart == null) {
            freeChart =
                ChartFactory.createTimeSeriesChart("Yukon Trending Application", "Date/Time", "Value",
                    new TimeSeriesCollection(), true, true, true);
            freeChart.setBackgroundPaint(java.awt.Color.white);
        }
        return freeChart;
    }

    public StringBuffer getHtmlString() {
        return htmlString;
    }

    /**
     * Retrieves the data for the given point list for the date
     * range indicated in the startDate and endDate.
     * Creation date: (10/3/00 5:53:52 PM)
     */
    public int getMinIntervalRate() {
        return minIntervalRate;
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/5/2001 3:54:09 PM)
     * 
     * @return int
     */
    public int getViewType() {
        return getTrendProperties().getViewType();
    }

    public int getOption() {
        return getTrendProperties().getOptionsMaskSettings();
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/24/00 10:08:37 AM)
     * 
     * @return java.lang.String
     */
    public String getTitle() {
        return title;
    }

    public boolean isUpdateTrend() {
        long now = (new Date()).getTime();
        CTILogger.debug("LAST = " + new Date(lastUpdateTime));
        CTILogger.debug("NOW  = " + new Date(now));
        CTILogger.debug("waiting until  = " + new Date(lastUpdateTime + (getMinIntervalRate() * 1000)));
        if ((lastUpdateTime + (getMinIntervalRate() * 1000)) <= now) {
            updateTrend = true;
            lastUpdateTime = now;
        }
        return updateTrend;
    }

    /**
     * Retrieves the data for the given point list for the date
     * range indicated in the startDate and endDate.
     * Creation date: (10/3/00 5:53:52 PM)
     */
    private int retrieveIntervalRate() {
        /*
         * java.util.Vector pointsArrayVector = new java.util.Vector();
         * for (int i = 0; i < getCurrentGraphDefinition().getGraphDataSeries().size(); i++)
         * {
         * com.cannontech.database.db.graph.GraphDataSeries gds =
         * (com.cannontech.database.db.graph.GraphDataSeries)
         * getCurrentGraphDefinition().getGraphDataSeries().get(i);
         * pointsArrayVector.add(new Integer(gds.getPointID().intValue()));
         * //for LoadDuration we want to only use the peak point if it exists, default to all pointsArray.
         * if ( gds.getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES))
         * haveAPeakPoint = true;
         * }
         */
        Vector pointsVector = new Vector();

        for (int i = 0; i < getTrendModel().getTrendSeries().length; i++) {
            pointsVector.add(getTrendModel().getTrendSeries()[i].getPointId());
        }
        // for (int i = 0; i < currentModels.length; i++)
        // for (int j = 0; currentModels[i] != null && j < currentModels[i].length; j++)
        // for (int k = 0; k < currentModels[i][j].points.length; k++)
        // pointsVector.add(currentModels[i][j].points[k]);
        Integer[] pointsArray = new Integer[pointsVector.size()];
        pointsVector.toArray(pointsArray);

        long timer = System.currentTimeMillis();
        int minRate = DEFAULT_INTERVAL_RATE; // defaults to 900 seconds (15 mins) SN 5/23/01

        if (pointsArray.length <= 0) {
            return minRate; // could return whatever is in Graph.intervalRate otherwise
        }

        StringBuffer sql = new StringBuffer("SELECT MIN(DSR.INTERVALRATE) FROM POINT P, DEVICESCANRATE DSR WHERE ");

        if (pointsArray.length > 0) {
            sql.append(" P.POINTID IN ( ");
            sql.append(pointsArray[0].toString());
        }

        for (int i = 1; i < pointsArray.length; i++) {
            sql.append(" , ");
            sql.append(pointsArray[i].toString());
        }

        sql.append(" ) AND P.PAOBJECTID = DSR.DEVICEID");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if (conn == null) {
                CTILogger.info(getClass() + ":  Error getting database connection.");
                return minIntervalRate;
            } else {
                stmt = conn.prepareStatement(sql.toString());
                rset = stmt.executeQuery();
                while (rset.next()) {
                    if (rset.getInt(1) > 0) {
                        minRate = rset.getInt(1);
                    }
                }
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SqlUtils.close(rset, stmt, conn);
        }
        if (minRate < 60) {
            return 60;
        } else {
            return minRate;
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (10/12/00 3:02:39 PM)
     * 
     * @param gDef com.cannontech.database.data.graph.GraphDefinition
     */
    // public void setGraphDefinitionID(int newGraphDefinitionID)
    // {
    // // if( graphDefinitionID != newGraphDefinitionID)
    // {
    // graphDefinitionID = newGraphDefinitionID;
    // retrieveGraphDefinition();
    // }
    // }
    /**
     ** Both Server and Client use **
     * Insert the method's description here.
     * Creation date: (3/27/00 10:25:04 AM)
     * 
     * @param width int
     * @param height int
     */
    public void setPeriod(String newPeriod) {
        if (!period.equalsIgnoreCase(newPeriod)) {
            period = newPeriod;
            if (ServletUtil.EVENT.equals(period)) {
                this.getTrendProperties().updateOptionsMaskSettings(GraphRenderers.EVENT_MASK, true);
            } else {
                this.getTrendProperties().updateOptionsMaskSettings(GraphRenderers.EVENT_MASK, false);
            }
            setUpdateTrend(true);
        }
    }

    public void setHtmlString(StringBuffer newHtmlString) {
        htmlString = newHtmlString;
    }

    /**
     * Insert the method's description here.
     * Creation date: (5/23/2001 12:03:38 PM)
     * 
     * @param newRate int
     */
    public void setMinIntervalRate(int newRate) {
        this.minIntervalRate = newRate;
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/5/2001 3:53:34 PM)
     * 
     * @param newModelType int
     */
    public void setViewType(int newViewType) {
        // setUpdateTrend(true);
        getTrendProperties().setViewType(newViewType);
    }

    public void setStartDate(Date newStartDate) {
        if (startDate.compareTo(newStartDate) != 0) {
            CTILogger.info("Changing Date!");
            startDate = newStartDate;
            setUpdateTrend(true);
        }
    }

    public Date getStartDate() {
        return ServletUtil.getStartingDateOfInterval(startDate, getPeriod());
    }

    public Date getStopDate() {
        return ServletUtil.getEndingDateOfInterval(startDate, getPeriod());
    }

    /**
     ** Both Server and Client use **
     * Insert the method's description here.
     * Creation date: (3/27/00 10:25:04 AM)
     * 
     * @param width int
     * @param height int
     */

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/24/00 10:08:37 AM)
     * 
     * @param newTitle java.lang.String
     */
    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public void setTrendModel(TrendModel newTrendModel) {
        trendModel = newTrendModel;
    }

    public void setUpdateTrend(boolean update) {
        updateTrend = update;
    }

    public void update(Instant start, Instant stop) {
        if (start == null) {
            start = new Instant(getStartDate());
        }
        if (stop == null) {
            stop = new Instant(getStopDate());
        }

        if (isUpdateTrend()) {
            TrendModel newModel = null;
            if (getGenerationType() == GDEF_GENERATION) {
                newModel =
                    new TrendModel(getGraphDefinition(), start.toDate(), stop.toDate(), getTrendProperties(),
                        getNumberOfEvents());
            } else if (getGenerationType() == POINT_GENERATION) {
                String chartTitle = "";
                LitePoint[] lps = new LitePoint[pointIDs.length];
                for (int i = 0; i < pointIDs.length; i++) {
                    LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(pointIDs[i]);
                    lps[i] = lp;
                    if (i > 0) {
                        chartTitle += ", ";
                    }
                    chartTitle += lp.getPointName();
                }
                chartTitle += " Trend";
                newModel = new TrendModel(start.toDate(), stop.toDate(), chartTitle, lps);

                for (int i = 0; i < pointIDs.length; i++) {
                    // make every other point the opposite Y-axis
                    newModel.getTrendSeries()[i].setAxis((i % 2 == 0 ? new Character('L') : new Character('R')));
                }

            }
            setTrendModel(newModel);
            updateTrend = false;
        }
        getTrendModel().setTrendProps(getTrendProperties());
        freeChart = getTrendModel().refresh();
    }

    /**
     * Send a command message to dispatch to scan paobjects (or all meters if paobjects is null)
     * at the alternate scan rate for duration of 0 secs (once).
     **/
    public void getDataNow(List<LiteYukonPAObject> paobjects) {
        Multi multi = new Multi();

        if (paobjects == null) {
            IDatabaseCache cache = DefaultDatabaseCache.getInstance();
            paobjects = cache.getAllDevices(); // populate our list of paobjects with all devices then!
        }

        for (int i = 0; i < paobjects.size(); i++) {
            LiteYukonPAObject paobject = paobjects.get(i);
            if (DeviceTypesFuncs.hasDeviceScanRate(paobject.getPaoType())) {
                CTILogger.info("Alternate Scan Rate Command Message for DEVICE ID: " + paobject.getLiteID() + " Name: "
                    + paobject.getPaoName());
                Command messageCommand = new Command();
                messageCommand.setOperation(Command.ALTERNATE_SCAN_RATE);
                messageCommand.setPriority(14);

                List<Integer> opArgList = new ArrayList<Integer>(4);
                opArgList.add(-1); // token
                opArgList.add(paobject.getLiteID()); // deviceID
                opArgList.add(-1); // open?
                opArgList.add(0); // duration in secs

                messageCommand.setOpArgList(opArgList);
                multi.getVector().add(messageCommand);
            }
        }

        if (multi.getVector().size() > 0) {
            getClientConnection().write(multi);
        }
    }

    /**
     * @return
     */
    public TrendProperties getTrendProperties() {
        if (trendProperties == null) {
            trendProperties = new TrendProperties();
        }
        return trendProperties;
    }

    /**
     * @param properties
     */
    public void setTrendProperties(TrendProperties properties) {
        trendProperties = properties;

        if ((trendProperties.getOptionsMaskSettings() & GraphRenderers.EVENT_MASK) == GraphRenderers.EVENT_MASK) {
            this.period = ServletUtil.EVENT;
        }
    }

    /**
     * @param item
     */
    public void create(DBPersistent item) {
        if (item != null) {
            try {
                Transaction t = Transaction.createTransaction(Transaction.INSERT, item);
                item = t.execute();

                // write the DBChangeMessage out to Dispatch since it was a Successfull ADD
                DBChangeMsg[] dbChange =
                    DefaultDatabaseCache.getInstance().createDBChangeMessages((CTIDbChange) item, DbChangeType.ADD);

                for (int i = 0; i < dbChange.length; i++) {
                    DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
                    getClientConnection().write(dbChange[i]);
                }
            } catch (TransactionException e) {
                CTILogger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * @param item
     */
    public void delete(DBPersistent item) {
        try {
            Transaction t = Transaction.createTransaction(Transaction.DELETE, item);
            item = t.execute();

            // write the DBChangeMessage out to Dispatch since it was a Successfull DELETE
            DBChangeMsg[] dbChange =
                DefaultDatabaseCache.getInstance().createDBChangeMessages((CTIDbChange) item, DbChangeType.DELETE);

            for (int i = 0; i < dbChange.length; i++) {
                DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
                getClientConnection().write(dbChange[i]);
            }
            /** TODO think about this a bit more, is this really what we want to do? */
            // if( getFreeChart().getPlot() instanceof org.jfree.chart.plot.CategoryPlot)
            // {
            // ((CategoryPlot)getFreeChart().getPlot()).setDataset(null);
            // ((CategoryPlot)getFreeChart().getPlot()).setSecondaryDataset(0, null);
            // // ((CategoryPlot)getFreeChart().getPlot()).setDataset(1, null);
            // }
            // else if( getFreeChart().getPlot() instanceof org.jfree.chart.plot.XYPlot)
            // {
            // ((XYPlot)getFreeChart().getPlot()).setDataset(null);
            // ((CategoryPlot)getFreeChart().getPlot()).setSecondaryDataset(0, null);
            // // ((XYPlot)getFreeChart().getPlot()).setDataset(1, null);
            // }
        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);
        }
    }

    /**
     * @param item
     */
    public void update(DBPersistent item) {
        try {
            Transaction t = Transaction.createTransaction(Transaction.UPDATE, item);
            item = t.execute();

            // write the DBChangeMessage out to Dispatch since it was a Successfull UPDATE
            DBChangeMsg[] dbChange =
                DefaultDatabaseCache.getInstance().createDBChangeMessages((CTIDbChange) item, DbChangeType.UPDATE);

            for (int i = 0; i < dbChange.length; i++) {
                DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
                getClientConnection().write(dbChange[i]);
            }
        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);
        }
        // force an update on the GDEF update
        setUpdateTrend(true);
    }

    /**
     * @return
     */
    public int getGenerationType() {
        return generationType;
    }

    /**
     * @param i
     */
    public void setGenerationType(int i) {
        generationType = i;
    }

    public int[] getPointIDs() {
        return pointIDs;
    }

    public void setPointIDs(int[] pointIDs) {
        if (getPointIDs() == null || !pointIDs.equals(getPointIDs())) // will these ever be equal?
        {
            setUpdateTrend(true);
        } else {
            System.out.println("SAME POINTS");
        }
        // ASSUMPTION!!! If you are setting the pointIDs, you must want to use them. You can only use pointIDs
        // -OR- graphDefinitionID...not both!
        setGenerationType(POINT_GENERATION);
        this.pointIDs = pointIDs;
        getTrendProperties().setGdefName("AD HOC - Points");
    }

    /**
     * Creates an array on size 1.
     * 
     * @param points
     */
    public void setPointIDs(int pointID) {
        int[] pointIDs = new int[] { pointID };
        setPointIDs(pointIDs);
    }

    public int getNumberOfEvents() {
        return numberOfEvents;
    }

    public void setNumberOfEvents(int numberOfEvents) {
        if (this.numberOfEvents != numberOfEvents) {
            this.numberOfEvents = numberOfEvents;
            setUpdateTrend(true);
        }
    }

}
