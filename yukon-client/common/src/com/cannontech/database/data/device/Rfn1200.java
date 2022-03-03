package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.port.CommPort;
import com.cannontech.database.db.port.PortTiming;

public class Rfn1200 extends RfnBase {

    private CommPort commPort;
    private PortTiming timing;
    
    public Rfn1200() {
        super(PaoType.RFN_1200);
    }
    
    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getCommPort().retrieve();
        getTiming().retrieve();
    }
    
    @Override
    public void add() throws SQLException {
        super.add();
        getCommPort().add();
        getTiming().add();
    }
    
    @Override
    public void update() throws SQLException {
        super.update();
        getTiming().update();
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getDevice().setDbConnection(conn);
        getTiming().setDbConnection(conn);
        getCommPort().setDbConnection(conn);
    }
    
    @Override
    public void delete() throws SQLException {
        getTiming().delete();
        getCommPort().delete();
        super.delete();
    }
    
    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getTiming().setPortID(deviceID);
        getCommPort().setPortID(deviceID);
    }
    
    public CommPort getCommPort() {
        if( commPort == null ) {
            commPort = new CommPort();
            commPort.setAlarmInhibit('N');
            commPort.setCommonProtocol("None");
            
            Connection conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            commPort.setDbConnection(conn);
            
            commPort.setPerformanceAlarm('N');
            commPort.setPerformThreshold(90);
            
            commPort.setPortID(getPAObjectID());
            commPort.setSharedPortType(CtiUtilities.STRING_NONE );
            commPort.setSharedSocketNumber(CommPort.DEFAULT_SHARED_SOCKET_NUMBER);
        }
        return commPort;
    }

    public PortTiming getTiming() {
        if (timing == null) {
            timing = new PortTiming();
            timing.setPostCommWait(0);
            timing.setExtraTimeOut(0);
            timing.setPostTxWait(0);
            timing.setExtraTimeOut(0);
            timing.setPreTxWait(0);
            timing.setReceiveDataWait(0);
            timing.setRtsToTxWait(0);
        }
        return timing;
    }

    public void setTiming(PortTiming timing) {
        this.timing = timing;
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE);
        tsb.append("paoName", getPAOName());
        tsb.append("rfnAddress", getRfnAddress().toString());
        tsb.append("postCommWait", getTiming().getPostCommWait());
        tsb.append("disabled", getPAODisableFlag());
        return tsb.toString();
    }
}