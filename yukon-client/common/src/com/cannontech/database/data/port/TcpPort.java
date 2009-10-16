package com.cannontech.database.data.port;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.database.db.port.PortTiming;

public class TcpPort extends DirectPort implements EditorPanel{

    private PortTiming portTiming = null;
    
    public TcpPort() {
        super();
    }

    public void add() throws SQLException {
        super.add();
        getPortTiming().add();
    }

    public void delete() throws SQLException {
        getPortTiming().delete();
        super.delete();
    }

    public void retrieve() throws SQLException {
        super.retrieve();
        getPortTiming().retrieve();
    }

    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getPortTiming().setDbConnection(conn);
    }

    public void setDisableFlag(Character ch) {
        getYukonPAObject().setDisableFlag(ch);
    }

    public void setPortID(Integer portID) {
        super.setPortID(portID);
        getPortTiming().setPortID(portID);
    }

    public void setPortName(String name) {
        getYukonPAObject().setPaoName( name );
    }

    public void setPortType(String value) {
        getYukonPAObject().setType(value);
    }

    public void update() throws SQLException {
        super.update();
        getPortTiming().update();
    }
    
    public PortTiming getPortTiming() {
        if( portTiming == null )
            portTiming = new PortTiming();
        return portTiming;
    }
    
    public void setPortTiming(PortTiming newValue) {
        this.portTiming = newValue;
    }
}
