package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.port.CommPort;

public class Rfn1200 extends RfnBase {

    private CommPort commPort = null;
    
    public Rfn1200() {
        super(PaoType.RFN_1200);
    }
    
    @Override
    public void add() throws SQLException {
        super.add();
        getCommPort().add();
    }
    
    @Override
    public void delete() throws SQLException {
        getCommPort().delete();
        super.delete();
    }
    
    public CommPort getCommPort() {
        if( commPort == null ) {
            commPort = new CommPort();
            commPort.setAlarmInhibit('N');
            commPort.setCommonProtocol("None");
            
            Connection conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());;
            commPort.setDbConnection(conn);
            
            commPort.setPerformanceAlarm('N');
            commPort.setPerformThreshold(90);
            
            commPort.setPortID(getPAObjectID());
            commPort.setSharedPortType(CtiUtilities.STRING_NONE );
            commPort.setSharedSocketNumber(CommPort.DEFAULT_SHARED_SOCKET_NUMBER);
        }
        return commPort;
    }
    
    public void setCommPort(CommPort newValue) {
        this.commPort = newValue;
    }
}