package com.cannontech.database.data.point;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.database.db.point.PointAccumulator;

public class AccumulatorPoint extends ScalarPoint {

    private PointAccumulator pointAccumulator = null;

    public void add() throws SQLException {
        super.add();
        getPointAccumulator().add();
    }

    public void addPartial() throws SQLException {
        super.addPartial();
        getPointAccumulator().add();
    }

    public void delete() throws SQLException {
        getPointAccumulator().delete();
        super.delete();
    }

    public void deletePartial() throws SQLException {
        getPointAccumulator().delete();
        super.deletePartial();
    }

    public PointAccumulator getPointAccumulator() {
        if (pointAccumulator == null)
            pointAccumulator = new PointAccumulator();

        return pointAccumulator;
    }

    public void retrieve() throws SQLException {
        super.retrieve();
        getPointAccumulator().retrieve();
    }

    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getPointAccumulator().setDbConnection(conn);
    }

    public void setPointAccumulator(PointAccumulator newValue) {
        this.pointAccumulator = newValue;
    }

    public void setPointID(Integer pointID) {
        super.setPointID(pointID);
        getPointAccumulator().setPointID(pointID);
    }

    public void update() throws SQLException {
        super.update();
        getPointAccumulator().update();
    }
}
