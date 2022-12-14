package com.cannontech.database.data.port;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.port.PortLocalSerial;

public abstract class LocalDirectPortBase extends DirectPort {
    private PortLocalSerial portLocalSerial = null;

    public LocalDirectPortBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getPortLocalSerial().add();

    }

    @Override
    public void delete() throws java.sql.SQLException {
        getPortLocalSerial().delete();
        super.delete();
    }

    public PortLocalSerial getPortLocalSerial() {
        if (portLocalSerial == null) {
            portLocalSerial = new PortLocalSerial();
        }

        return portLocalSerial;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getPortLocalSerial().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getPortLocalSerial().setDbConnection(conn);
    }

    @Override
    public void setPortID(Integer portID) {
        super.setPortID(portID);
        getPortLocalSerial().setPortID(portID);
    }

    public void setPortLocalSerial(PortLocalSerial newValue) {
        this.portLocalSerial = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getPortLocalSerial().update();
    }
}