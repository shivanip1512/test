package com.cannontech.database.db.point;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

public abstract class PointControl extends DBPersistent {
    private Integer pointID = null;
    private Integer controlOffset = 0;
    private boolean controlInhibited = false;

    public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };
    public static final String VALUE_COLUMNS[] = { "CONTROLOFFSET", "CONTROLINHIBIT" };
    public static final String TABLE_NAME = "PointControl";

    public PointControl() {
        super();
    }

    @Override
    public void add() throws java.sql.SQLException {
        if (hasControl()) {
            Character controlInhibit = CtiUtilities.getBooleanCharacter(isControlInhibited());
    
            Object addValues[] = { getPointID(), getControlOffset(), controlInhibit };
            add(PointControl.TABLE_NAME, addValues);
        }
    }
    
    @Override
    public void update() throws java.sql.SQLException {
        delete();
        add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        if (getPointID() != null) {
            delete(PointControl.TABLE_NAME, CONSTRAINT_COLUMNS[0], getPointID());
        }
    }

    public Integer getPointID() {
        return pointID;
    }

    public void setPointID(Integer newValue) {
        this.pointID = newValue;
    }

    public Integer getControlOffset() {
        return controlOffset;
    }

    public void setControlOffset(Integer controlOffset) {
        this.controlOffset = controlOffset;
    }

    public boolean isControlInhibited() {
        return controlInhibited;
    }
    
    public void setControlInhibited(boolean controlInhibited) {
        this.controlInhibited = controlInhibited;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        Object constraintValues[] = { getPointID() };

        Object[] results =
            retrieve(VALUE_COLUMNS, PointControl.TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == VALUE_COLUMNS.length) {
            setControlType(getNormalType());

            setControlOffset((Integer) results[0]);
            setControlInhibited(CtiUtilities.isTrue(((String) results[1]).charAt(0)));
        } else if (results.length != 0) {
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
    }
    /**
     * Indicates that there is (or should be) a PointControl record for this DBPersistent.
     * @return
     */
    public abstract boolean hasControl();
    public abstract String getControlType();
    public abstract void setControlType(String newValue);

    protected abstract boolean isValidControlType(String newValue);
    protected abstract String getNormalType();
}