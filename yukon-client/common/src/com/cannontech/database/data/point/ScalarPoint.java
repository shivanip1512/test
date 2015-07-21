package com.cannontech.database.data.point;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.point.PointLimit;
import com.cannontech.database.db.point.PointUnit;

public class ScalarPoint extends PointBase {

    // contains <Integer:limitNumber, PointLimit>
    private Map<Integer, PointLimit> pointLimitsMap = new HashMap<Integer, PointLimit>();

    private PointUnit pointUnit = null;

    public ScalarPoint() {
        super();
    }

    @Override
    public void add() throws SQLException {
        super.add();
        getPointUnit().add();

        Iterator<PointLimit> it = pointLimitsMap.values().iterator();
        while (it.hasNext()) {
            it.next().add();
        }
    }

    @Override
    public void addPartial() throws SQLException {
        Iterator<PointLimit> it = pointLimitsMap.values().iterator();
        while (it.hasNext()) {
            it.next().add();
        }
        
        getPointUnit().add();
        super.addPartial();
    }

    @Override
    public void delete() throws SQLException {
        getPointUnit().delete();
        // delete all the associated PointLimits
        delete(PointLimit.TABLE_NAME, "POINTID", getPointUnit().getPointID());
        super.delete();
    }

    @Override
    public void deletePartial() throws SQLException {
        getPointUnit().delete();
        // delete all the associated PointLimits
        delete(PointLimit.TABLE_NAME, "POINTID", getPoint().getPointID());
        super.deletePartial();
    }

    public Map<Integer, PointLimit> getPointLimitsMap() {
        if (pointLimitsMap == null) {
            pointLimitsMap = new HashMap<Integer, PointLimit>();
        }
        return pointLimitsMap;
    }

    public PointLimit getLimitOne() {
        PointLimit plOne = pointLimitsMap.get(1);
        if (plOne == null) {
            return new PointLimit(getPoint().getPointID(), 1, 0.0, 0.0, 0);
        }
        return plOne;
    }
    
    public boolean isLimitOneSpecified() {
        return pointLimitsMap.containsKey(1);
    }
    
    public boolean isLimitTwoSpecified() {
        return pointLimitsMap.containsKey(2);
    }
       

    public PointLimit getLimitTwo() {
        PointLimit plTwo = pointLimitsMap.get(2);
        if (plTwo == null) {
            return new PointLimit(getPoint().getPointID(), 2, 0.0, 0.0, 0);
        }
        return plTwo;
    }
    

    public PointUnit getPointUnit() {
        if (pointUnit == null) {
            pointUnit = new PointUnit();
        }
        return pointUnit;
    }

    @Override
    public void retrieve() throws SQLException {

        super.retrieve();
        getPointUnit().retrieve();

        PointLimit plArray[] = PointLimit.getPointLimits(getPoint().getPointID(), CtiUtilities.getDatabaseAlias());

        for (int i = 0; i < plArray.length; i++) {
            pointLimitsMap.put(plArray[i].getLimitNumber(), plArray[i]);
        }

        Iterator<PointLimit> it = pointLimitsMap.values().iterator();
        while (it.hasNext()) {
            PointLimit o = it.next();
            o.setDbConnection(getDbConnection());
            o.retrieve();
            o.setDbConnection(null);
        }
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);

        getPointUnit().setDbConnection(conn);
        Iterator<PointLimit> it = pointLimitsMap.values().iterator();
        while (it.hasNext()) {
            it.next().setDbConnection(conn);
        }
    }

    @Override
    public void setPointID(Integer pointID) {
        super.setPointID(pointID);

        getPointUnit().setPointID(pointID);

        Iterator<PointLimit> it = pointLimitsMap.values().iterator();
        while (it.hasNext()) {
            it.next().setPointID(pointID);
        }
    }

    public void setPointLimitsMap(Map<Integer, PointLimit> newValue) {
        this.pointLimitsMap = newValue;
    }

    public void setPointUnit(PointUnit newValue) {
        this.pointUnit = newValue;
    }

    @Override
    public void update() throws SQLException {

        super.update();
        getPointUnit().update();

        PointLimit.deletePointLimits(getPoint().getPointID(), getDbConnection());

        Iterator<PointLimit> it = pointLimitsMap.values().iterator();
        while (it.hasNext()) {
            PointLimit pointLimit = it.next();
            pointLimit.setPointID(getPoint().getPointID());
            pointLimit.add();
        }
    }
}