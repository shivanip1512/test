package com.cannontech.database.db.capcontrol;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

@SuppressWarnings("serial")
public class CapControlSubstation extends DBPersistent {
    private Integer substationID;
    private Integer voltReductionPointId = new Integer(0);
    public static final String CONSTRAINT_COLUMNS[] = { "SubstationId" };
    public static final String SETTER_COLUMNS[] = {"voltReductionPointId"};
    public static final String TABLE_NAME = "CapControlSubstation";

    public void add() throws SQLException {
        Object[] values = { getSubstationID(), getVoltReductionPointId()};
        add(TABLE_NAME, values);
    }

    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS, new Object[] { getSubstationID() });
    }

    public void retrieve() throws SQLException {
        
        Object constraintValues[] = { getSubstationID() };
        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setVoltReductionPointId((Integer) results[0]);
        } else {
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
    }

    public void update() throws SQLException {
        Object setValues[]= {getVoltReductionPointId()}; 
        Object constraintValues[] = { getSubstationID() };
        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    public Integer getSubstationID() {
        return substationID;
    }

    public void setSubstationID(Integer substationID) {
        this.substationID = substationID;
    }
    
    public Integer getVoltReductionPointId() {
        return voltReductionPointId;
    }

    public void setVoltReductionPointId(Integer voltReductionPointId) {
        this.voltReductionPointId = voltReductionPointId;
    }

}
