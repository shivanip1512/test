package com.cannontech.database.db.device.lm;

import java.sql.SQLException;

public class LMGroupItronMapping extends com.cannontech.database.db.DBPersistent {

    private static final long serialVersionUID = 1L;
    private Integer virtualRelayId;
    private Integer yukonGroupId;
    public static final String TABLE_NAME = "LMGroupItronMapping";
    public static final String CONSTRAINT_COLUMNS[] = { "YukonGroupId" };
    public static final String SETTER_COLUMNS[] = { "YukonGroupId", "ItronGroupId", "VirtualRelayId"};

    public LMGroupItronMapping() {
        super();
    }

    @Override
    public void add() throws java.sql.SQLException {
        Object addValues[] = { yukonGroupId, null, virtualRelayId};
        add(TABLE_NAME, addValues);

    }

    @Override
    public void delete() throws java.sql.SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], yukonGroupId);
    }
    
    @Override
    public void retrieve() throws java.sql.SQLException {
        Object constraintValues[] = { yukonGroupId };
        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            yukonGroupId = ((Integer) results[0]);
            virtualRelayId = ((Integer) results[2]);
        } else {
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
    }

    @Override
    public void update() throws SQLException {
        Object setValues[] =  {virtualRelayId};
        Object constraintValues[] = {yukonGroupId};
        String columns[] = {"VirtualRelayId"};
        update(TABLE_NAME, columns, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }
    
    public Integer getVirtualRelayId() {
        return virtualRelayId;
    }

    public void setVirtualRelayId(Integer virtualRelayId) {
        this.virtualRelayId = virtualRelayId;
    }

    public Integer getYukonGroupId() {
        return yukonGroupId;
    }

    public void setYukonGroupId(Integer yukonGroupId) {
        this.yukonGroupId = yukonGroupId;
    }
}
