package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.config.MCTConfigMapping;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.database.db.device.DeviceMeterGroup;

public abstract class MCTBase extends CarrierBase implements IDeviceMeterGroup {
    private DeviceMeterGroup deviceMeterGroup = null;
    private DeviceLoadProfile deviceLoadProfile = null;
    private boolean hasConfig = false;
    private MCTConfigMapping configMapping = null;

    public MCTBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getDeviceMeterGroup().add();
        getDeviceLoadProfile().add();
        if (hasConfig) {
            getConfigMapping().add();
        }

    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        // getDeviceMeterGroupDefaults().add();
        getDeviceMeterGroup().add();
        getDeviceLoadProfile().add();
        if (hasConfig) {
            getConfigMapping().add();
        }

    }
    
    @Override
    public void deletePartial() throws java.sql.SQLException {
        getDeviceMeterGroup().delete();
        getDeviceLoadProfile().delete();
        if (hasConfig) {
            getConfigMapping().delete();
        }

        super.deletePartial();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getDeviceMeterGroup().delete();
        getDeviceLoadProfile().delete();
        if (hasConfig) {
            getConfigMapping().delete();
        }

        super.delete();
    }

    public DeviceLoadProfile getDeviceLoadProfile() {
        if (deviceLoadProfile == null) {
            deviceLoadProfile = new DeviceLoadProfile();
        }
        return deviceLoadProfile;
    }

    @Override
    public DeviceMeterGroup getDeviceMeterGroup() {
        if (deviceMeterGroup == null) {
            deviceMeterGroup = new DeviceMeterGroup();
        }
        return deviceMeterGroup;
    }

    public MCTConfigMapping getConfigMapping() {
        if (configMapping == null) {
            configMapping = new MCTConfigMapping();
        }
        return configMapping;
    }

    public void setConfigMapping(MCTConfigMapping newConfig) {
        configMapping = newConfig;
    }

    public void setConfigMapping(Integer conID, Integer mctID) {
        getConfigMapping().setconfigID(conID);
        getConfigMapping().setmctID(mctID);
    }

    @Override
    public void setDeviceMeterGroup(DeviceMeterGroup dvMtrGrp_) {
        deviceMeterGroup = dvMtrGrp_;
    }

    public void setDeviceLoadProfile(DeviceLoadProfile deviceLoadProfile) {
        this.deviceLoadProfile = deviceLoadProfile;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getDeviceMeterGroup().retrieve();
        getDeviceLoadProfile().retrieve();
        if (hasConfig) {
            getConfigMapping().retrieve();
        }
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDeviceLoadProfile().setDbConnection(conn);
        getDeviceMeterGroup().setDbConnection(conn);
        if (hasConfig) {
            getConfigMapping().setDbConnection(conn);
        }
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceMeterGroup().setDeviceID(deviceID);
        getDeviceLoadProfile().setDeviceID(deviceID);
    }

    public void setHasConfig(boolean yesno) {
        hasConfig = yesno;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getDeviceMeterGroup().update();
        getDeviceLoadProfile().update();
        if (hasConfig) {
            getConfigMapping().add();
        }
    }

    public boolean hasMappedConfig() {
        hasConfig = false;

        try {
            hasConfig = MCTConfigMapping.hasConfig(getDevice().getDeviceID());
        } catch (java.sql.SQLException e2) {
            CTILogger.error(e2.getMessage(), e2);
        }

        return hasConfig;
    }

    public Integer getConfigID() {
        Integer id = new Integer(-1);

        if (hasConfig) {
            Connection conn = null;
            try {

                conn = PoolManager.getInstance().getConnection("yukon");
                id = MCTConfigMapping.getTheConfigID(getDevice().getDeviceID(), conn);

            } catch (SQLException e2) {
                CTILogger.error(e2.getMessage(), e2);
            } finally {
                SqlUtils.close(conn);
            }
        } else
            id = configMapping.getConfigID();

        return id;
    }
}