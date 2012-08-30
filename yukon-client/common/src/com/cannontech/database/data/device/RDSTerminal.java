package com.cannontech.database.data.device;

import java.sql.SQLException;


import com.cannontech.common.pao.PaoInfo;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.db.pao.StaticPaoInfo;

public class RDSTerminal extends IEDBase {
    private StaticPaoInfo rdsIpAddress = new StaticPaoInfo(PaoInfo.RDS_TRANSMITTER_IP_ADDRESS);
    private StaticPaoInfo rdsIpPort = new StaticPaoInfo(PaoInfo.RDS_TRANSMITTER_IP_PORT);
    private StaticPaoInfo siteAddress = new StaticPaoInfo(PaoInfo.RDS_TRANSMITTER_SITE_ADDRESS);
    private StaticPaoInfo encoderAddress = new StaticPaoInfo(PaoInfo.RDS_TRANSMITTER_ENCODER_ADDRESS);
    private StaticPaoInfo transmitSpeed = new StaticPaoInfo(PaoInfo.RDS_TRANSMITTER_TRANSMIT_SPEED);
    private StaticPaoInfo groupType = new StaticPaoInfo(PaoInfo.RDS_TRANSMITTER_GROUP_TYPE);
    private StaticPaoInfo spid = new StaticPaoInfo(PaoInfo.RDS_TRANSMITTER_SPID);
    private StaticPaoInfo aidRepeatPeriod = new StaticPaoInfo(PaoInfo.RDS_TRANSMITTER_AID_REPEAT_PERIOD);
    
    public RDSTerminal(){
        super();
        setDeviceClass(DeviceClasses.STRING_CLASS_TRANSMITTER);
    }
    
    @Override
    public void add() throws SQLException {
        super.add();
        getRdsIpAddress().add();
        getRdsIpPort().add();
        getSiteAddress().add();
        getEncoderAddress().add();
        getTransmitSpeed().add();
        getGroupType().add();
        getSpid().add();
        getAidRepeatPeriod().add();
    }
    
    @Override
    public void addPartial() throws SQLException {
        super.addPartial();
        getRdsIpAddress().add();
        getRdsIpPort().add();
        getSiteAddress().add();
        getEncoderAddress().add();
        getTransmitSpeed().add();
        getGroupType().add();
        getSpid().add();
        getAidRepeatPeriod().add();
    }
    
    @Override
    public void delete() throws SQLException {
        getRdsIpAddress().delete();
        getRdsIpPort().delete();
        getSiteAddress().delete();
        getEncoderAddress().delete();
        getTransmitSpeed().delete();
        getGroupType().delete();
        getSpid().delete();
        getAidRepeatPeriod().delete();
        super.delete();
    }
    
    @Override
    public void deletePartial() throws SQLException {
        super.deletePartial();
    }
    
    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getRdsIpAddress().retrieve();
        getRdsIpPort().retrieve();
        getSiteAddress().retrieve();
        getEncoderAddress().retrieve();
        getTransmitSpeed().retrieve();
        getGroupType().retrieve();
        getSpid().retrieve();
        getAidRepeatPeriod().retrieve();
    }

    @Override
    public void update() throws SQLException {
        super.update();
        getRdsIpAddress().update();
        getRdsIpPort().update();
        getSiteAddress().update();
        getEncoderAddress().update();
        getTransmitSpeed().update();
        getGroupType().update();
        getSpid().update();
        getAidRepeatPeriod().update();
    }
    
    @Override
    public void setDeviceID(Integer deviceId){
        super.setDeviceID(deviceId);
        getRdsIpAddress().setPaobjectId(deviceId);
        getRdsIpPort().setPaobjectId(deviceId);
        getSiteAddress().setPaobjectId(deviceId);
        getEncoderAddress().setPaobjectId(deviceId);
        getTransmitSpeed().setPaobjectId(deviceId);
        getGroupType().setPaobjectId(deviceId);
        getSpid().setPaobjectId(deviceId);
        getAidRepeatPeriod().setPaobjectId(deviceId);
    }
    
    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getRdsIpAddress().setDbConnection(conn);
        getRdsIpPort().setDbConnection(conn);
        getSiteAddress().setDbConnection(conn);
        getEncoderAddress().setDbConnection(conn);
        getTransmitSpeed().setDbConnection(conn);
        getGroupType().setDbConnection(conn);
        getSpid().setDbConnection(conn);
        getAidRepeatPeriod().setDbConnection(conn);
    }
    
    public StaticPaoInfo getRdsIpAddress() {
        return rdsIpAddress;
    }

    public void setRdsIpAddress(StaticPaoInfo rdsIpAddress) {
        this.rdsIpAddress = rdsIpAddress;
    }

    public StaticPaoInfo getRdsIpPort() {
        return rdsIpPort;
    }

    public void setRdsIpPort(StaticPaoInfo rdsIpPort) {
        this.rdsIpPort = rdsIpPort;
    }

    public StaticPaoInfo getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(StaticPaoInfo siteAddress) {
        this.siteAddress = siteAddress;
    }

    public StaticPaoInfo getEncoderAddress() {
        return encoderAddress;
    }

    public void setEncoderAddress(StaticPaoInfo encoderAddress) {
        this.encoderAddress = encoderAddress;
    }

    public StaticPaoInfo getTransmitSpeed() {
        return transmitSpeed;
    }

    public void setTransmitSpeed(StaticPaoInfo transmitSpeed) {
        this.transmitSpeed = transmitSpeed;
    }

    public StaticPaoInfo getGroupType() {
        return groupType;
    }

    public void setGroupType(StaticPaoInfo groupType) {
        this.groupType = groupType;
    }
    
    public StaticPaoInfo getSpid() {
        return spid;
    }
    
    public void setSpid(StaticPaoInfo spid) {
        this.spid = spid;
    }
    
    public StaticPaoInfo getAidRepeatPeriod() {
        return aidRepeatPeriod;
    }
    
    public void setAidRepeatPeriod(StaticPaoInfo aidRepeatPeriod) {
        this.aidRepeatPeriod = aidRepeatPeriod;
    }
}
