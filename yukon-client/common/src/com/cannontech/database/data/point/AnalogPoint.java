package com.cannontech.database.data.point;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.database.db.point.PointAnalog;
import com.cannontech.database.db.point.PointAnalogControl;

public class AnalogPoint extends ScalarPoint {

    private PointAnalog pointAnalog = null;
    private PointAnalogControl pointAnalogControl = null;

    public AnalogPoint() {
        super();
    }

    @Override
    public void add() throws SQLException {
        super.add();
        getPointAnalog().add();
        getPointAnalogControl().add();
    }

    @Override
    public void addPartial() throws SQLException {
        super.addPartial();
        getPointAnalog().add();
        getPointAnalogControl().add();
    }

    @Override
    public void delete() throws SQLException {
        getPointAnalogControl().delete();
        getPointAnalog().delete();
        super.delete();
    }

    @Override
    public void deletePartial() throws SQLException {
        getPointAnalogControl().delete();
        getPointAnalog().delete();
        super.deletePartial();
    }

    public PointAnalog getPointAnalog() {
        if (pointAnalog == null) {
            pointAnalog = new PointAnalog();
        }
        return pointAnalog;
    }

    public PointAnalogControl getPointAnalogControl() {
        if (pointAnalogControl == null) {
            pointAnalogControl = new PointAnalogControl();
        }
        return pointAnalogControl;
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getPointAnalog().retrieve();
        getPointAnalogControl().retrieve();
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getPointAnalog().setDbConnection(conn);
        getPointAnalogControl().setDbConnection(conn);
    }

    @Override
    public void setPointID(Integer pointID) {
        super.setPointID(pointID);
        getPointAnalog().setPointID(pointID);
        getPointAnalogControl().setPointID(pointID);
    }

    @Override
    public void update() throws SQLException {
        super.update();
        getPointAnalog().update();
        getPointAnalogControl().update();
    }
}