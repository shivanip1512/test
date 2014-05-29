package com.cannontech.database.data.port;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.port.PortTerminalServer;

public abstract class TerminalServerPortBase extends DirectPort {
    private PortTerminalServer portTerminalServer = null;

    public TerminalServerPortBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getPortTerminalServer().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getPortTerminalServer().delete();
        super.delete();
    }

    public PortTerminalServer getPortTerminalServer() {
        if (portTerminalServer == null) {
            portTerminalServer = new PortTerminalServer();
        }

        return portTerminalServer;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getPortTerminalServer().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getPortTerminalServer().setDbConnection(conn);
    }

    @Override
    public void setPortID(Integer portID) {
        super.setPortID(portID);
        getPortTerminalServer().setPortID(portID);
    }

    public void setPortTerminalServer(PortTerminalServer newValue) {
        this.portTerminalServer = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getPortTerminalServer().update();
    }
}