package com.cannontech.database.db.point.fdr;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

@Deprecated
public class FDRInterface extends DBPersistent {
    
    private Integer interfaceID = null;
    private String interfaceName = null;
    private String possibleDirections = null;
    private boolean hasDestination = false;

    public static final String SETTER_COLUMNS[] = { "InterfaceNames", "PossibleDirection", "HasDestination" };

    public static final String CONSTRAINT_COLUMNS[] = { "InterfaceID" };

    private static final String TABLE_NAME = "FDRInterface";


    public void add() throws SQLException {
        
        Object addValues[] = { getInterfaceID(), getInterfaceName(), getPossibleDirections(), hasDestination() };

        add(TABLE_NAME, addValues);
    }

    public void delete() throws SQLException {
        delete(TABLE_NAME, "INTERFACEID", getInterfaceID());
    }

    public Integer getInterfaceID() {
        return interfaceID;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getPossibleDirections() {
        return possibleDirections;
    }

    public String[] getAllDirections() {

        if (getPossibleDirections() == null)
            return new String[0];

        return getPossibleDirections().split(",");
    }

    public boolean hasDestination() {
        return hasDestination;
    }

    public void retrieve() throws SQLException {
        
        Object constraintValues[] = { getInterfaceID() };

        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setInterfaceName((String) results[0]);
            setPossibleDirections((String) results[1]);
            setHasDestination(((String) results[2]).equalsIgnoreCase("t"));
        } else
            throw new Error(getClass() + " - Incorrect Number of results retrieved");

    }

    public void setHasDestination(boolean newHasDestination) {
        hasDestination = newHasDestination;
    }

    public void setInterfaceID(Integer newInterfaceID) {
        interfaceID = newInterfaceID;
    }

    public void setInterfaceName(String newInterfaceName) {
        interfaceName = newInterfaceName;
    }

    public void setPossibleDirections(String newPossibleDirections) {
        possibleDirections = newPossibleDirections;
    }

    public void update() throws SQLException {
        
        Object setValues[] = { getInterfaceName(), getPossibleDirections(), hasDestination() };

        Object constraintValues[] = { getInterfaceID() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }
}
