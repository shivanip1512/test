package com.cannontech.database.db.device;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

public class DeviceParent extends DBPersistent {
    private Integer deviceId = null;
    private Integer parentId = null;

    public static final String SETTER_COLUMNS[] = { "ParentID" };

    public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
    public static final String TABLE_NAME = "DeviceParent";

    public DeviceParent() {
        super();
        initialize(null, null);
    }

    public DeviceParent(Integer deviceID, Integer routeID) {
        super();
        initialize(deviceID, routeID);
    }

    public void add() throws SQLException {

        Object addValues[] = { getDeviceId(), getParentId() };
        add(DeviceParent.TABLE_NAME, addValues);
    }

    public void delete() throws SQLException {
        Object values[] = { getDeviceId() };
        delete(DeviceParent.TABLE_NAME, CONSTRAINT_COLUMNS, values);
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void initialize(Integer deviceId, Integer parentId) {
        this.deviceId = deviceId;
        this.parentId = parentId;
    }

    public void retrieve() throws SQLException {

        Object constraintValues[] = { getDeviceId() };

        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setParentId((Integer) results[0]);
        }
    }

    public void setDeviceId(Integer newValue) {
        this.deviceId = newValue;
    }

    public void setParentId(Integer newValue) {
        this.parentId = newValue;
    }

    public void update() throws SQLException {

        Object setValues[] = { getParentId() };
        Object constraintValues[] = { getDeviceId() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }
}