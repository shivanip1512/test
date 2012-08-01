package com.cannontech.database.db.version;

/**
 * This type was created in VisualAge.
 */
public class CTIDatabase extends com.cannontech.database.db.DBPersistent {
    private String version = null;
    private String ctiEmployeeName = null;
    private java.util.Date dateApplied = null;
    private String notes = null;
    private Integer build = null;

    public static final String SETTER_COLUMNS[] = { "CTIEmployeeName", "DateApplied", "Notes", "Build" };

    public static final String CONSTRAINT_COLUMNS[] = { "version" };

    public static final String TABLE_NAME = "CTIDatabase";

    public CTIDatabase() {
        super();
    }

    @Override
    public void add() throws java.sql.SQLException {
        Object setValues[] = { getVersion(), getCtiEmployeeName(), getDateApplied(), getNotes(), getBuild() };

        add(TABLE_NAME, setValues);
    }

    @Override
    public void delete() throws java.sql.SQLException {
        Object constraintValues[] = { getVersion() };

        delete(TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
    }

    public java.lang.String getCtiEmployeeName() {
        return ctiEmployeeName;
    }

    public java.util.Date getDateApplied() {
        return dateApplied;
    }

    public java.lang.String getNotes() {
        return notes;
    }

    public java.lang.String getVersion() {
        return version;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        Object constraintValues[] = { getVersion() };

        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setCtiEmployeeName((String) results[0]);
            setDateApplied((java.util.Date) results[1]);
            setNotes((String) results[2]);
        } else {
            throw new Error(getClass() + "::retrieve - Incorrect number of results");
        }
    }

    public void setCtiEmployeeName(java.lang.String newCtiEmployeeName) {
        ctiEmployeeName = newCtiEmployeeName;
    }

    public void setDateApplied(java.util.Date newDateApplied) {
        dateApplied = newDateApplied;
    }

    public void setNotes(java.lang.String newNotes) {
        notes = newNotes;
    }

    public void setVersion(java.lang.String newVersion) {
        version = newVersion;
    }

    @Override
    public void update() throws java.sql.SQLException {
        Object setValues[] = { getCtiEmployeeName(), getDateApplied(), getNotes(), getBuild() };

        Object constraintValues[] = { getVersion() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    public Integer getBuild() {
        return build;
    }

    public void setBuild(Integer integer) {
        build = integer;
    }
}
