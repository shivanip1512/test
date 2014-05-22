package com.cannontech.database.data.port;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.port.PortDialupModem;

public class TerminalServerDialupPort extends TerminalServerSharedPort {

    private PortDialupModem portDialupModem = null;

    public TerminalServerDialupPort() {
        super(PaoType.TSERVER_DIALUP);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getPortDialupModem().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getPortDialupModem().delete();
        super.delete();
    }

    public PortDialupModem getPortDialupModem() {
        if (portDialupModem == null)
            portDialupModem = new PortDialupModem();

        return portDialupModem;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getPortDialupModem().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getPortDialupModem().setDbConnection(conn);
    }

    public void setPortDialupModem(PortDialupModem newValue) {
        this.portDialupModem = newValue;
    }

    @Override
    public void setPortID(Integer portID) {
        super.setPortID(portID);
        getPortDialupModem().setPortID(portID);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getPortDialupModem().update();
    }
}
