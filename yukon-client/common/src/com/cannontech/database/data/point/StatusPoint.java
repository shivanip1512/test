package com.cannontech.database.data.point;

import com.cannontech.database.db.point.PointStatus;
import com.cannontech.database.db.point.PointStatusControl;

public class StatusPoint extends PointBase {
    private PointStatus pointStatus = null;
    private PointStatusControl pointStatusControl = null;

    /**
     * StatusPoint constructor comment.
     */
    public StatusPoint() {
        super();
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getPointStatus().add();
        getPointStatusControl().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        getPointStatus().add();
        getPointStatusControl().add();
        super.addPartial();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getPointStatusControl().delete();
        getPointStatus().delete();
        super.delete();
    }

    @Override
    public void deletePartial() throws java.sql.SQLException {
        super.deletePartial();
    }

    public PointStatus getPointStatus() {
        if (pointStatus == null) {
            pointStatus = new PointStatus();
        }
        return pointStatus;
    }

    public PointStatusControl getPointStatusControl() {
        if (pointStatusControl == null) {
            pointStatusControl = new PointStatusControl();
        }
        return pointStatusControl;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getPointStatus().retrieve();
        getPointStatusControl().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getPointStatus().setDbConnection(conn);
        getPointStatusControl().setDbConnection(conn);
    }

    @Override
    public void setPointID(Integer pointID) {
        super.setPointID(pointID);
        getPointStatus().setPointID(pointID);
        getPointStatusControl().setPointID(pointID);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getPointStatus().update();
        getPointStatusControl().update();
    }
}