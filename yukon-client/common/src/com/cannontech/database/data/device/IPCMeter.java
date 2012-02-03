package com.cannontech.database.data.device;

import java.sql.SQLException;

import com.cannontech.database.data.port.TerminalServerSharedPort;
import com.cannontech.database.db.port.PortSettings;
import com.cannontech.database.db.port.PortTerminalServer;

/**
 * DBPersistent for IPC meters.
 * IPC Meters are essentially identical to Electronic Meters, but IPC Meters automatically generate
 * a TCP Terminal Server comm channel when they are created. This is set as the IPC Meter's comm 
 * channel.
 */
public class IPCMeter extends IEDMeter {
    private TerminalServerSharedPort comms;
    
    public void add() throws SQLException {
        //add comm channel
        comms.setPortName(getPAOName());
        comms.setDbConnection(getDbConnection());
        comms.add();
        
        //add meter
        getDeviceDirectCommSettings().setPortID(comms.getPortSettings().getPortID());
        getDeviceDialupSettings().setBaudRate(comms.getPortSettings().getBaudRate());
        super.add();
    }
    
    public PortTerminalServer getPortTerminalServer() {
        return comms.getPortTerminalServer();
    }
    
    public PortSettings getPortSettings() {
        return comms.getPortSettings();
    }
    
    public void setCommChannel(TerminalServerSharedPort comms) {
        this.comms = comms;
    }
}
