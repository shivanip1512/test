package com.cannontech.database.db.capcontrol;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

@SuppressWarnings("serial")
public class CapControlArea extends DBPersistent {
    private Integer areaID;
    public static final String CONSTRAINT_COLUMNS[] = { "AreaId" };
    public static final String TABLE_NAME = "CapControlArea";

    public void add() throws SQLException {
        Object[] values = { getAreaID()};
        add(TABLE_NAME, values);
    }

    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS, new Object[] { getAreaID() });
    }

    public void retrieve() throws SQLException {
        
        Object constraintValues[] = { getAreaID() };
        String[] cols = new String[] {"AreaId"};
        Object results[] = retrieve(cols, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == cols.length) {
            setAreaID((Integer) results[0]);
        } else {
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
    }

    public void update() throws SQLException {
        Object constraintValues[] = { getAreaID() };
        update(TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
    }

    public Integer getAreaID() {
        return areaID;
    }

    public void setAreaID(Integer areaID) {
        this.areaID = areaID;
    }

}
