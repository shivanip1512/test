package com.cannontech.database.data.port;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.port.CommPort;
import com.cannontech.database.db.port.PortSettings;

public abstract class DirectPort extends YukonPAObject implements EditorPanel {
    private CommPort commPort = null;
    private PortSettings portSettings = null;

    public DirectPort(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getCommPort().add();
        getPortSettings().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getPortSettings().delete();
        getCommPort().delete();
        super.delete();
    }

    public boolean equals(DirectPort obj) {
        return getCommPort().getPortID().equals(obj.getCommPort().getPortID());
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof DirectPort) {
            return getCommPort().getPortID().equals(((DirectPort) obj).getCommPort().getPortID());
        } else {
            return super.equals(obj);
        }
    }

    public CommPort getCommPort() {

        if (commPort == null) {
            commPort = new CommPort();
        }

        return commPort;
    }

    public String getPortName() {
        return getYukonPAObject().getPaoName();
    }

    public PortSettings getPortSettings() {
        if (portSettings == null) {
            portSettings = new PortSettings();
        }

        return portSettings;
    }

    public final static boolean hasDevice(Integer portID) {
        return hasDevice(portID, CtiUtilities.getDatabaseAlias());
    }

    public final static boolean hasDevice(Integer portID, String databaseAlias) {
        SqlStatement stmt = new SqlStatement("SELECT portID FROM " + DeviceDirectCommSettings.TABLE_NAME + " WHERE portID=" + portID, databaseAlias);

        try {
            stmt.execute();
            return (stmt.getRowCount() > 0);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getCommPort().retrieve();
        getPortSettings().retrieve();
    }

    public void setCommPort(CommPort newValue) {
        this.commPort = newValue;
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getCommPort().setDbConnection(conn);
        getPortSettings().setDbConnection(conn);
    }

    public void setDisableFlag(Character ch) {
        getYukonPAObject().setDisableFlag(ch);
    }

    public void setPortID(Integer portID) {
        super.setPAObjectID(portID);

        getCommPort().setPortID(portID);
        getPortSettings().setPortID(portID);
    }

    public void setPortName(String name) {
        getYukonPAObject().setPaoName(name);
    }

    public void setPortSettings(PortSettings newValue) {
        this.portSettings = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getCommPort().update();
        getPortSettings().update();
    }
}