package com.cannontech.database.db.device.lm;

import java.sql.SQLException;

public class LMGroupEatonCloud extends com.cannontech.database.db.DBPersistent {
    private static final long serialVersionUID = 1L;
    private String relayUsage;
    private Integer yukonGroupId;
    public static final String TABLE_NAME = "LMGroupEatonCloud";
    public static final String CONSTRAINT_COLUMNS[] = { "YukonGroupId" };
    public static final String SETTER_COLUMNS[] = { "YukonGroupId", "RelayUsage"};

    public LMGroupEatonCloud() {
        super();
    }

    @Override
    public void add() throws SQLException {
        Object addValues[] = { yukonGroupId, relayUsage };
        add(TABLE_NAME, addValues);
    }

    @Override
    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], yukonGroupId);
        
    }

    @Override
    public void retrieve() throws SQLException {
        Object constraintValues[] = { yukonGroupId };
        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            yukonGroupId = ((Integer) results[0]);
            relayUsage = ((String) results[1]);
        } else {
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
    }

    @Override
    public void update() throws SQLException {
        Object setValues[] = { relayUsage };
        Object constraintValues[] = { yukonGroupId };
        String columns[] = { "RelayUsage" };
        update(TABLE_NAME, columns, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    public Integer getYukonGroupId() {
        return yukonGroupId;
    }

    public void setYukonGroupId(Integer yukonGroupId) {
        this.yukonGroupId = yukonGroupId;
    }

    public String getRelayUsage() {
        return relayUsage;
    }

    public void setRelayUsage(String relayUsage) {
        this.relayUsage = relayUsage;
    }

}
