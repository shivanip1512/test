package com.cannontech.database.db.capcontrol;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

public class CapControlArea extends DBPersistent {
    private Integer areaID;
    private Integer controlPointId = new Integer(0);
    public static final String CONSTRAINT_COLUMNS[] = { "AreaId" };
    public static final String SETTER_COLUMNS[] = {"controlPointId"};
    public static final String TABLE_NAME = "CapControlArea";

    public void add() throws SQLException {
        Object[] values = { getAreaID(), getControlPointId()};
        add(TABLE_NAME, values);
    }

    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS, new Object[] { getAreaID() });
    }

    public void retrieve() throws SQLException {
        
        Object constraintValues[] = { getAreaID() };
        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setControlPointId((Integer) results[0]);
            
        } else {
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
    }

    public void update() throws SQLException {
        Object setValues[]= {getControlPointId()}; 
        Object constraintValues[] = { getAreaID() };
        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    public Integer getAreaID() {
        return areaID;
    }

    public void setAreaID(Integer areaID) {
        this.areaID = areaID;
    }
    
    public Integer getControlPointId() {
        return controlPointId;
    }

    public void setControlPointId(Integer controlPointId) {
        this.controlPointId = controlPointId;
    }

}
