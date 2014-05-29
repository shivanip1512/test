package com.cannontech.database.data.port;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.port.PortTiming;

public abstract class LocalSharedPortBase extends LocalDirectPortBase {
    private PortTiming portTiming = null;

    public LocalSharedPortBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getPortTiming().add();

    }

    @Override
    public void delete() throws java.sql.SQLException {
        getPortTiming().delete();
        super.delete();
    }

    public PortTiming getPortTiming() {
        if (portTiming == null) {
            portTiming = new PortTiming();
        }
        return portTiming;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getPortTiming().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getPortTiming().setDbConnection(conn);
    }

    @Override
    public void setPortID(Integer portID) {
        super.setPortID(portID);
        getPortTiming().setPortID(portID);
    }

    public void setPortTiming(PortTiming newValue) {
        this.portTiming = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getPortTiming().update();
    }
}