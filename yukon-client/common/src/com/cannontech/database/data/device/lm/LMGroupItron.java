package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.lm.LMGroupItronMapping;

public class LMGroupItron extends LMGroup {
    private LMGroupItronMapping mapping = new LMGroupItronMapping();
    private final static long serialVersionUID = 1L;

    public LMGroupItron() {
        super(PaoType.LM_GROUP_ITRON);
    }
    
    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        mapping.add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        mapping.delete();
        super.delete();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        mapping.add();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        mapping.setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        mapping.setYukonGroupId(deviceID);
    }
    
    public void setRelay(Integer relay) {
        mapping.setVirtualRelayId(relay);
    }
    
    public int getRelay() {
        return mapping.getVirtualRelayId();
    }
    
    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        mapping.retrieve();
    }
    
    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        mapping.update();
    }
}
