package com.cannontech.database.data.point;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CapBank;
import com.cannontech.database.db.capcontrol.CapControlSubstationBus;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;
import com.cannontech.database.db.device.lm.LMGroupPoint;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.fdr.FDRTranslation;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;

public class PointBase extends DBPersistent implements CTIDbChange, EditorPanel {
    private Point point = null;
    private Vector<FDRTranslation> pointFDR = null;
    private PointAlarming pointAlarming = null;

    public PointBase() {
        super();
    }

    @Override
    public void add() throws SQLException {
        if (getPoint().getPointID() == null) {
            int nextId = YukonSpringHook.getBean(PointDao.class).getNextPointId();
            setPointID(nextId);
        }

        getPoint().add();

        for (int i = 0; i < getPointFDRVector().size(); i++) {
            getPointFDRVector().elementAt(i).add();
        }
        // add a PointAlarming row for each point created
        getPointAlarming().setPointID(getPoint().getPointID());
        getPointAlarming().add();
    }

    @Override
    public void addPartial() throws SQLException {
        setPointID(getPoint().getPointID());
    }

    @Override
    public void deletePartial() throws SQLException {
        Integer pointID = getPoint().getPointID();
        PointPropertyValueDao dao = YukonSpringHook.getBean("pointPropertyValueDao", PointPropertyValueDao.class);
        dao.removeByPointId(pointID);

        // ADD TABLES THAT HAVE A REFERENCE TO THE POINT TABLE AND THAT
        // NEED TO BE DELETED WHEN A POINT ROW IS DELETED (CASCADE DELETE)
        delete(FDRTranslation.TABLE_NAME, "PointID", pointID);
        delete(GraphDataSeries.tableName, "PointID", pointID);
        delete(CalcComponent.TABLENAME, "ComponentPointID", pointID);
        delete("TagLog", "PointID", pointID);
        delete("Display2WayData", "PointID", pointID);
        delete("CCEventLog", "PointID", pointID);
        delete("PointUnit", "PointID", pointID);
    }

    @Override
    public void delete() throws SQLException {
        deletePartial();

        getPointAlarming().delete();
        getPoint().delete();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PointBase) {
            return getPoint().getPointID().equals(((PointBase) obj).getPoint().getPointID());
        } else {
            return super.equals(obj);
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((point == null) ? 0 : point.getPointID().hashCode());
        return result;
    }

    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
        DBChangeMsg[] msgs = { new DBChangeMsg(getPoint().getPointID().intValue(),
                                               DBChangeMsg.CHANGE_POINT_DB,
                                               DBChangeMsg.CAT_POINT,
                                               getPoint().getPointType(),
                                               dbChangeType) };

        return msgs;
    }

    public Point getPoint() {
        if (this.point == null) {
            this.point = new Point();
        }
        return this.point;
    }

    public PointAlarming getPointAlarming() {
        if (pointAlarming == null) {
            pointAlarming = new PointAlarming();
        }
        return pointAlarming;
    }

    public Vector<FDRTranslation> getPointFDRVector() {
        if (pointFDR == null) {
            pointFDR = new Vector<FDRTranslation>();
        }
        return pointFDR;
    }

    public final static boolean hasCapBank(Integer pointID) {
        return hasCapBank(pointID, CtiUtilities.getDatabaseAlias());
    }

    public final static boolean hasCapBank(Integer pointID, String databaseAlias) {
        SqlStatement stmt = new SqlStatement("SELECT count(ControlPointID) FROM " + CapBank.TABLE_NAME + 
                                             " WHERE ControlPointID=" + pointID,
                                             databaseAlias);

        try {
            stmt.execute();
            return new Integer(stmt.getRow(0)[0].toString()) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public final static boolean hasCapControlSubstationBus(Integer pointID) {
        return hasCapControlSubstationBus(pointID, CtiUtilities.getDatabaseAlias());
    }

    public final static boolean hasCapControlSubstationBus(Integer pointID, String databaseAlias) {
        SqlStatement stmt = new SqlStatement("SELECT count(CurrentWattLoadPointID) FROM " + CapControlSubstationBus.TABLE_NAME + 
                                             " WHERE CurrentWattLoadPointID=" + pointID + " or CurrentVarLoadPointID=" + pointID,
                                             databaseAlias);

        try {
            stmt.execute();
            return new Integer(stmt.getRow(0)[0].toString()) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public final static boolean hasLMGroup(Integer pointID) {
        return hasLMGroup(pointID, CtiUtilities.getDatabaseAlias());
    }

    public final static boolean hasLMGroup(Integer pointID, String databaseAlias) {
        SqlStatement stmt = new SqlStatement("SELECT count(PointIDUsage) FROM " + LMGroupPoint.TABLE_NAME + 
                                             " WHERE PointIDUsage=" + pointID,
                                             databaseAlias);

        try {
            stmt.execute();
            return new Integer(stmt.getRow(0)[0].toString()) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public final static boolean hasLMTrigger(Integer pointID) {
        return hasLMTrigger(pointID, CtiUtilities.getDatabaseAlias());
    }

    public final static boolean hasLMTrigger(Integer pointID, String databaseAlias) {
        SqlStatement stmt = new SqlStatement("SELECT count(PointID) FROM " + LMControlAreaTrigger.TABLE_NAME + 
                                             " WHERE PointID=" + pointID + " or PeakPointID=" + pointID,
                                             databaseAlias);

        try {
            stmt.execute();
            return new Integer(stmt.getRow(0)[0].toString()) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public final static boolean hasRawPointHistorys(Integer pointID) {
        return hasRawPointHistorys(pointID, CtiUtilities.getDatabaseAlias());
    }

    public final static boolean hasRawPointHistorys(Integer pointID, String databaseAlias) {
        SqlStatement stmt = new SqlStatement("SELECT count(pointID) FROM " + RawPointHistory.TABLE_NAME + 
                                             " WHERE pointID=" + pointID,
                                             databaseAlias);

        try {
            stmt.execute();
            return new Integer(stmt.getRow(0)[0].toString()) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static final boolean hasSystemLogEntry(Integer ptID) {
        return hasSystemLogEntry(ptID, CtiUtilities.getDatabaseAlias());
    }

    public final static boolean hasSystemLogEntry(Integer ptID, String databaseAlias) {
        SqlStatement stmt = new SqlStatement("SELECT count(pointID) FROM " + SystemLog.TABLE_NAME + 
                                             " WHERE pointID=" + ptID, databaseAlias);

        try {
            stmt.execute();
            return new Integer(stmt.getRow(0)[0].toString()) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void retrieve() throws SQLException {
        getPoint().retrieve();
        getPointAlarming().retrieve();

        setPointFDRVector(FDRTranslation.getFDRTranslations(getPoint().getPointID()));
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getPoint().setDbConnection(conn);
        getPointAlarming().setDbConnection(conn);

        for (int i = 0; i < getPointFDRVector().size(); i++) {
            getPointFDRVector().elementAt(i).setDbConnection(conn);
        }
    }

    public void setPoint(Point newValue) {
        this.point = newValue;
    }

    public void setPointAlarming(PointAlarming newPointAlarming) {
        pointAlarming = newPointAlarming;
    }

    public void setPointFDRVector(Vector<FDRTranslation> newValue) {
        this.pointFDR = newValue;
    }

    public void setPointID(Integer newID) {
        getPoint().setPointID(newID);
        getPointAlarming().setPointID(newID);

        for (int i = 0; i < getPointFDRVector().size(); i++) {
            getPointFDRVector().elementAt(i).setPointID(newID);
        }
    }

    @Override
    public String toString() {
        return getPoint().getPointName();
    }

    @Override
    public void update() throws SQLException {
        getPoint().update();
        getPointAlarming().update();
        // delete all the current FDRTranslations, then add the new ones
        delete(FDRTranslation.TABLE_NAME, "PointID", getPoint().getPointID());

        for (int i = 0; i < getPointFDRVector().size(); i++) {
            getPointFDRVector().elementAt(i).add();
        }
    }

    public List<FDRTranslation> getPointFDRList() {
        return getPointFDRVector();
    }
}