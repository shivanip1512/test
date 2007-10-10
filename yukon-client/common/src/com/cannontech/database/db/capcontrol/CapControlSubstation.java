package com.cannontech.database.db.capcontrol;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

@SuppressWarnings("serial")
public class CapControlSubstation extends DBPersistent {
    private Integer substationID;
    public static final String CONSTRAINT_COLUMNS[] = { "SubstationId" };
    public static final String TABLE_NAME = "CapControlSubstation";

    public void add() throws SQLException {
        Object[] values = { getSubstationID()};
        add(TABLE_NAME, values);
    }

    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS, new Object[] { getSubstationID() });
    }

    public void retrieve() throws SQLException {
        
        Object constraintValues[] = { getSubstationID() };
        String[] cols = new String[] {"SubstationId"};
        Object results[] = retrieve(cols, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == cols.length) {
            setSubstationID((Integer) results[0]);
        } else {
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
    }

    public void update() throws SQLException {
        Object constraintValues[] = { getSubstationID() };
        update(TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
    }

    public Integer getSubstationID() {
        return substationID;
    }

    public void setSubstationID(Integer substationID) {
        this.substationID = substationID;
    }

}
