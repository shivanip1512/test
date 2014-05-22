package com.cannontech.database.data.port;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.port.PortTiming;

public class TerminalServerSharedPort extends TerminalServerDirectPort {
    private PortTiming portTiming = null;

    /**
     * Valid PaoTypes are TSERVER_SHARED and UDPPort, or those that extend this object;  TSERVER_DIALUP, TSERVER_RADIO
     * @param paoType
     */
    public TerminalServerSharedPort(PaoType paoType) {
        super(paoType);
    }

    /**
     * This method was created in VisualAge.
     */
    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getPortTiming().add();
    }

    /**
     * This method was created in VisualAge.
     */
    @Override
    public void delete() throws java.sql.SQLException {
        getPortTiming().delete();
        super.delete();
    }

    /**
     * This method was created in VisualAge.
     * @return com.cannontech.database.db.port.PortTiming
     */
    public PortTiming getPortTiming() {
        if (portTiming == null)
            portTiming = new PortTiming();
        return portTiming;
    }

    /**
     * This method was created in VisualAge.
     */
    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getPortTiming().retrieve();
    }

    /**
     * Insert the method's description here. Creation date: (1/4/00 3:32:03 PM)
     * @param conn java.sql.Connection
     */
    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getPortTiming().setDbConnection(conn);
    }

    /**
     * This method was created in VisualAge.
     * @param portID java.lang.Integer
     */
    @Override
    public void setPortID(Integer portID) {
        super.setPortID(portID);
        getPortTiming().setPortID(portID);
    }

    /**
     * This method was created in VisualAge.
     * @param newValue com.cannontech.database.db.port.PortTiming
     */
    public void setPortTiming(PortTiming newValue) {
        this.portTiming = newValue;
    }

    /**
     * This method was created in VisualAge.
     */
    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getPortTiming().update();
    }
}
