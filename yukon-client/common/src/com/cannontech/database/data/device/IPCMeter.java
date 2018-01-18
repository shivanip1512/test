package com.cannontech.database.data.device;

import java.sql.SQLException;
import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.port.TerminalServerSharedPortBase;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.db.port.PortSettings;
import com.cannontech.database.db.port.PortTerminalServer;
import com.cannontech.spring.YukonSpringHook;

/**
 * DBPersistent for IPC meters.
 * IPC Meters are essentially identical to Electronic Meters, but IPC Meters automatically generate
 * a TCP Terminal Server comm channel when they are created. This is set as the IPC Meter's comm 
 * channel.
 */
public abstract class IPCMeter extends IEDMeter {
    private TerminalServerSharedPortBase comms;
    
    public IPCMeter(PaoType paoType) {
        super(paoType);
    }
    
    @Override
    public void add() throws SQLException {
        //add comm channel
        comms.setPortName(getPAOName());
        comms.setDbConnection(getDbConnection());
        comms.getCommPort().setCommonProtocol("None");
        comms.add();
        
        // Since ports have a PAO definition xml file now, we need to add the default points 
        // to the DB when a port is created.
        YukonPAObject ypo = new YukonPAObject();
        ypo.setPaObjectID(comms.getPAObjectID());
        ypo.setPaoName(comms.getPAOName());
        ypo.setPaoType(comms.getPaoType());
        PaoDefinitionService paoDefinitionService = YukonSpringHook.getBean(PaoDefinitionService.class);
        List<PointBase> defaultPoints = paoDefinitionService.createDefaultPointsForPao(ypo);
        for (PointBase point : defaultPoints) {
            point.setDbConnection(getDbConnection());
            point.add();
        }
        
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
    
    public void setCommChannel(TerminalServerSharedPortBase comms) {
        this.comms = comms;
    }
}