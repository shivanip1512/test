package com.cannontech.database.db.device.lm;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.SepDeviceClassDao;
import com.cannontech.database.data.device.lm.SepDeviceClass;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public class LMGroupSep extends DBPersistent {
    private static final long serialVersionUID = -5869275545572761995L;
    private Integer deviceId = null;
    private Integer utilityEnrollmentGroup = null;
    private Set<SepDeviceClass> deviceClassSet = null;

    public static final String tableName = "LMGroupSep";

    @Override
    public void add() throws SQLException {
        Object addValues[] = { getDeviceId(), getUtilityEnrollmentGroup() };

        add(tableName, addValues);
        getDao().save(getDeviceClassSet(), deviceId);
    }

    @Override
    public void delete() throws SQLException {
        delete(tableName, "DeviceId", getDeviceId());
        getDao().deleteByDeviceId(deviceId);
    }

    @Override
    public void retrieve() throws SQLException {
        String selectColumns[] = { "UtilityEnrollmentGroup" };
        String constraintColumns[] = { "DeviceId" };
        Object constraintValues[] = { getDeviceId() };

        Object results[] = retrieve(selectColumns, tableName, constraintColumns, constraintValues);

        if (results.length == selectColumns.length) {
            setUtilityEnrollmentGroup((Integer) results[0]);
        } else
            throw new SQLException(getClass() + " - Incorrect Number of results retrieved");

        // Load device class set from dao
        setDeviceClassSet(getDao().getSepDeviceClassesByDeviceId(getDeviceId()));
    }

    @Override
    public void update() throws SQLException {
        String setColumns[] = { "UtilityEnrollmentGroup" };
        Object setValues[] = { getUtilityEnrollmentGroup() };

        String constraintColumns[] = { "DeviceId" };
        Object constraintValues[] = { getDeviceId() };

        update(tableName, setColumns, setValues, constraintColumns, constraintValues);
        getDao().save(getDeviceClassSet(), deviceId);
    }

    private SepDeviceClassDao getDao() {
        return YukonSpringHook.getBean("sepLMGroupDeviceClassDao", SepDeviceClassDao.class);
    }

    public Integer getUtilityEnrollmentGroup() {
        return utilityEnrollmentGroup;
    }

    public void setUtilityEnrollmentGroup(Integer utilityEnrollmentGroup) {
        this.utilityEnrollmentGroup = utilityEnrollmentGroup;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public Set<SepDeviceClass> getDeviceClassSet() {
        if (deviceClassSet == null) {
            deviceClassSet = EnumSet.noneOf(SepDeviceClass.class);
        }
        return deviceClassSet;
    }

    public void setDeviceClassSet(Set<SepDeviceClass> deviceClassSet) {
        this.deviceClassSet = deviceClassSet;
    }

    public boolean hasDeviceClass(SepDeviceClass deviceClass) {
        if (getDeviceClassSet().contains(deviceClass))
            return true;
        return false;
    }

    public void removeDeviceClass(SepDeviceClass deviceClass) {
        getDeviceClassSet().remove(deviceClass);
    }

    public void addDeviceClass(SepDeviceClass deviceClass) {
        getDeviceClassSet().add(deviceClass);
    }
}