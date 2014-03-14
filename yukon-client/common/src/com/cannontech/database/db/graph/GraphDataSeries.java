package com.cannontech.database.db.graph;

/**
 * Represents a series of data on a graph
 * Creation date: (12/13/99 1:39:30 PM)
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;

public class GraphDataSeries extends DBPersistent {
    
    private Integer graphDataSeriesID = null;
    private Integer type = new Integer(GDSTypes.BASIC_GRAPH_TYPE);
    private Integer graphDefinitionID = null;
    private Integer pointID = null;    
    private String label = " ";
    private Character axis = new Character('L');
    private Integer color = null;
    private Double multiplier = new Double(1.0);
    private Integer renderer = new Integer(GraphRenderers.LINE); 
    private String moreData = CtiUtilities.STRING_NONE;
    public static final String tableName = "GraphDataSeries";
    
    //These come from the device and point unit tables
    //respectively..... it saves a database hit in
    //certain circumstances
    private String deviceName;

    public GraphDataSeries() {super();}

    public void add() throws SQLException {
        synchronized (com.cannontech.database.db.graph.GraphDataSeries.class) {
            if (getGraphDataSeriesID() == null) {
                setGraphDataSeriesID(new Integer(getNextID(getDbConnection())));
            }

            Object[] addValues = { getGraphDataSeriesID(),
                    getGraphDefinitionID(), getPointID(), getLabel(),
                    getAxis(), getColor(), getType(), getMultiplier(),
                    getRenderer(), getMoreData() };

            add(tableName, addValues);
        }
    }
    
    public void delete() throws SQLException {
        delete( tableName, "GraphDataSeriesID", getGraphDataSeriesID() );
    }
    
    public static void deleteAllGraphDataSeries(Integer graphDefinitionID) {
        deleteAllGraphDataSeries( graphDefinitionID, "yukon" );
    }
    
    public static void deleteAllGraphDataSeries(Integer graphDefinitionID, String databaseAlias) {

        String sqlString = "DELETE FROM GraphDataSeries WHERE GraphDefinitionID= " + graphDefinitionID.toString();

        SqlStatement sql = new SqlStatement(sqlString, databaseAlias);

        try {
            sql.execute();
        } catch (CommandExecutionException e) {
            CTILogger.error(e.getMessage(), e);
        }

        return;
    }
    
    public static GraphDataSeries[] getAllGraphDataSeries(Integer graphDefinitionID) {
        return getAllGraphDataSeries(graphDefinitionID, CtiUtilities.getDatabaseAlias());
    }
    
    public static GraphDataSeries[] getAllGraphDataSeries(Integer graphDefinitionID, String databaseAlias) {
        GraphDataSeries[] returnVal = null;
        String sqlString = "SELECT gds.GRAPHDATASERIESID, gds.TYPE, gds.POINTID, gds.LABEL, gds.AXIS, "
                + "gds.COLOR, pao.PAONAME, gds.MULTIPLIER, gds.RENDERER, gds.MOREDATA "
                + "FROM GRAPHDATASERIES gds, YUKONPAOBJECT pao, POINT p WHERE gds.GRAPHDEFINITIONID = "
                + graphDefinitionID.toString() + " AND p.POINTID = GDS.POINTID AND pao.PAOBJECTID = p.PAOBJECTID ORDER BY p.POINTOFFSET";

        try {
            // contains values of row/columns
            Object[][] results = SqlUtils.getResultObjects(sqlString, databaseAlias);
            if (results != null) {
                List<GraphDataSeries> temp = new ArrayList<>();
                for (int i = 0; i < results.length; i++) {
                    GraphDataSeries dataSeries = new GraphDataSeries();

                    Object[] row = results[i];

                    dataSeries.setGraphDataSeriesID((Integer) row[0]);
                    dataSeries.setGraphDefinitionID(graphDefinitionID);
                    dataSeries.setType((Integer) row[1]);
                    dataSeries.setPointID((Integer) row[2]);
                    dataSeries.setLabel((String) row[3]);
                    dataSeries.setAxis(new Character(((String) row[4]).charAt(0)));
                    dataSeries.setColor((Integer) row[5]);
                    dataSeries.setDeviceName((String) row[6]);
                    dataSeries.setMultiplier((Double) row[7]);
                    dataSeries.setRenderer((Integer) row[8]);
                    dataSeries.setMoreData((String) row[9]);

                    temp.add(dataSeries);
                }

                returnVal = temp.toArray(new GraphDataSeries[temp.size()]);
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        }

        return returnVal;
    }
    
    public Character getAxis() {
        return axis;
    }
    
    public Integer getColor() {
        return color;
    }

    public String getDeviceName() {
        return deviceName;
    }
    
    public Integer getGraphDataSeriesID() {
        return graphDataSeriesID;
    }
    
    public Integer getGraphDefinitionID() {
        return graphDefinitionID;
    }
    
    public String getLabel() {
        return label;
    }
    
    public Double getMultiplier() {
        return multiplier;
    }
    
    public static synchronized int getNextID() {
        return getNextID(PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias() ));
    }
    
    public static synchronized int getNextID(Connection conn) {
        int retVal = 0;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            if (conn == null) {
                throw new IllegalStateException("Database connection can not be (null).");
            } else {
                pstmt = conn.prepareStatement("select max(graphdataseriesID) AS maxid from graphdataseries");
                rset = pstmt.executeQuery();

                // Just one please
                if (rset.next())
                    retVal = rset.getInt("maxid") + 1;
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e2) {
                CTILogger.error(e2.getMessage(), e2);// something is up
            }
        }

        return retVal;
    }
    
    public Integer getPointID() {
        return pointID;
    }
    
    public Integer getType() {
        return type;
    }
    
    public void retrieve() throws SQLException {
        String[] selectColumns = { "GraphDefinitionID", "PointID", "Label", "Axis", "Color", "Type", "Multiplier", "Renderer", "MoreData" };

        String[] constraintColumns = { "GraphDataSeriesID" };
        Object[] constraintValues = { getGraphDataSeriesID() };

        Object results[] = retrieve(selectColumns, tableName, constraintColumns, constraintValues);

        if (results.length == selectColumns.length) {
            setGraphDefinitionID((Integer) results[0]);
            setPointID((Integer) results[1]);
            setLabel((String) results[2]);
            setAxis(new Character(((String) results[3]).charAt(0)));
            setColor((Integer) results[4]);
            setType((Integer) results[5]);
            setMultiplier((Double) results[6]);
            setRenderer((Integer) results[7]);
            setMoreData((String) results[8]);
        }
    }

    public void setAxis(Character newAxis) {
        axis = newAxis;
    }
    
    public boolean isRight() {
        return axis.equals('R');
    }
    
    public void setColor(Integer newColor) {
        color = newColor;
    }
    
    public void setDeviceName(String newDeviceName) {
        deviceName = newDeviceName;
    }
    
    public void setGraphDataSeriesID(Integer newGraphDataSeriesID) {
        graphDataSeriesID = newGraphDataSeriesID;
    }
    
    public void setGraphDefinitionID(Integer newGraphDefinitionID) {
        graphDefinitionID = newGraphDefinitionID;
    }
    
    public void setLabel(String newLabel) {
        label = newLabel;
    }
    
    public void setMultiplier(Double newMultiplier) {
        multiplier = newMultiplier;
    }
    
    public void setPointID(Integer newPointID) {
        pointID = newPointID;
    }
    
    public void setType(Integer newType) {
        type = newType;
    }
    
    public String toString() {
        return "GraphDataSeries - type:  " + getType() + "  gdsID:  " + getGraphDataSeriesID() + "  pointID:  " + getPointID() + "\n";
    }
    
    public void update() throws SQLException {

        String[] setColumns = { "GraphDefinitionID", "PointID", "Label", "Axis", "Color", "Type", "Multiplier", "Renderer", "MoreData" };

        Object[] setValues = { getGraphDefinitionID(), getPointID(), getLabel(), getAxis(), getColor(), getType(), getMultiplier(), getRenderer(), getMoreData() };

        String[] constraintColumns = { "GraphDataSeriesID" };
        Object[] constraintValues = { getGraphDataSeriesID() };

        update(tableName, setColumns, setValues, constraintColumns, constraintValues);
    }
    
    public String getMoreData() {
        return moreData;
    }
    
    public void setMoreData(String object) {
        moreData = object;
    }

    public Date getSpecificDate() {
        if (CtiUtilities.STRING_NONE.equalsIgnoreCase(getMoreData())) {
            return null;
        }

        Date date = null;
        if (GDSTypesFuncs.isDateType(getType().intValue())) {
            long ts = Long.valueOf(getMoreData()).longValue();
            date = new Date(ts);
        }
        return date;
    }
    
    public Integer getRenderer() {
        return renderer;
    }

    public void setRenderer(Integer integer) {
        renderer = integer;
    }

}