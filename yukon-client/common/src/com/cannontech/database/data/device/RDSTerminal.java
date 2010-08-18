package com.cannontech.database.data.device;

import java.sql.SQLException;

import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.db.device.RDSTransmitter;
import com.cannontech.database.db.pao.StaticPaoInfo;

public class RDSTerminal extends IEDBase {
    private RDSTransmitter rdsTransmitter = null;
    private StaticPaoInfo paoInfoIpAddress = null;
    private StaticPaoInfo paoInfoIpPort = null;
    
    public RDSTerminal(){
        super();
        setDeviceClass(DeviceClasses.STRING_CLASS_TRANSMITTER);
    }
    
    @Override
    public void add() throws SQLException {
        super.add();
        getRdsTransmitter().add();
        getPaoInfoIpAddress().add();
        getPaoInfoIpPort().add();
    }
    
    @Override
    public void addPartial() throws SQLException {
        super.addPartial();
        getRdsTransmitter().add();
        getPaoInfoIpAddress().add();
        getPaoInfoIpPort().add();
    }
    
    @Override
    public void delete() throws SQLException {
        getRdsTransmitter().delete();
        getPaoInfoIpAddress().delete();
        getPaoInfoIpPort().delete();
        super.delete();
    }
    
    @Override
    public void deletePartial() throws SQLException {
        super.deletePartial();
    }
    
    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getRdsTransmitter().retrieve();
        getPaoInfoIpAddress().retrieve();
        getPaoInfoIpPort().retrieve();
    }

    @Override
    public void update() throws SQLException {
        super.update();
        getRdsTransmitter().update();
        getPaoInfoIpAddress().update();
        getPaoInfoIpPort().update();
    }
    
    @Override
    public void setDeviceID(Integer deviceId){
        super.setDeviceID(deviceId);
        getRdsTransmitter().setPaobjectId(deviceId);
        getPaoInfoIpAddress().setPaobjectId(deviceId);
        getPaoInfoIpPort().setPaobjectId(deviceId);
    }
    
    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getRdsTransmitter().setDbConnection(conn);
        getPaoInfoIpAddress().setDbConnection(conn);
        getPaoInfoIpPort().setDbConnection(conn);
    }
    
    public RDSTransmitter getRdsTransmitter(){
        if(rdsTransmitter == null) {
            rdsTransmitter = new RDSTransmitter();
        }
        return rdsTransmitter;
    }
    
    public void setRdsTransmitter(RDSTransmitter rdsTransmitter){
        this.rdsTransmitter = rdsTransmitter;
    }

    public StaticPaoInfo getPaoInfoIpAddress() {
        if(paoInfoIpAddress == null) {
            paoInfoIpAddress = new StaticPaoInfo(StaticPaoInfo.INFO_KEY_IP_ADDRESS);
        }
        return paoInfoIpAddress;
    }
    
    public void setPaoInfoIpAddress(StaticPaoInfo paoInfoIpAddress) {
        this.paoInfoIpAddress = paoInfoIpAddress;
    }
    
    public StaticPaoInfo getPaoInfoIpPort() {
        if(paoInfoIpPort == null) {
            paoInfoIpPort = new StaticPaoInfo(StaticPaoInfo.INFO_KEY_IP_PORT);
        }
        return paoInfoIpPort;
    }
    
    public void setPaoInfoIpPort(StaticPaoInfo paoInfoIpPort) {
        this.paoInfoIpPort = paoInfoIpPort;
    }
}
