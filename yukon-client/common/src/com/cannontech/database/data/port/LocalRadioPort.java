package com.cannontech.database.data.port;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.port.PortRadioSettings;

public class LocalRadioPort extends LocalSharedPort {
    private PortRadioSettings portRadioSettings = null;

    public LocalRadioPort() {
        super(PaoType.LOCAL_RADIO);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getPortRadioSettings().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getPortRadioSettings().delete();
        super.delete();
    }

    public PortRadioSettings getPortRadioSettings() {
        if (portRadioSettings == null)
            portRadioSettings = new PortRadioSettings();
        return portRadioSettings;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getPortRadioSettings().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getPortRadioSettings().setDbConnection(conn);
    }

    @Override
    public void setPortID(Integer portID) {
        super.setPortID(portID);
        getPortRadioSettings().setPortID(portID);
    }

    public void setPortRadioSettings(PortRadioSettings newValue) {
        this.portRadioSettings = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getPortRadioSettings().update();
    }
}
