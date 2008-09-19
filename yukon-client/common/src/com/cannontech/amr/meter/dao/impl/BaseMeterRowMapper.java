package com.cannontech.amr.meter.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlProvidingRowMapper;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public abstract class BaseMeterRowMapper<T> implements SqlProvidingRowMapper<T> {

    private PaoGroupsWrapper paoGroupsWrapper;
    
    private final String sql = "SELECT ypo.paObjectId, ypo.paoName, ypo.type, ypo.disableFlag, " +
                                "DeviceMeterGroup.meterNumber, " + 
                                "DeviceCarrierSettings.address, " +
                                "DeviceRoutes.routeId, " +
                                "rypo.paoName as route " + 
                                "from YukonPaObject ypo " + 
                                "join Device on ypo.paObjectId = Device.deviceId " + 
                                "join DeviceMeterGroup on Device.deviceId = DeviceMeterGroup.deviceId " + 
                                "left outer join DeviceCarrierSettings on Device.deviceId = DeviceCarrierSettings.deviceId " + 
                                "left outer join DeviceRoutes on Device.deviceId = DeviceRoutes.deviceId " + 
                                "left outer join YukonPaObject rypo on DeviceRoutes.routeId = rypo.paObjectId ";

    public BaseMeterRowMapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }

    protected void fillInMeter(ResultSet rs, Meter meter) throws SQLException {
        int paObjectId = rs.getInt("paObjectId");
        meter.setDeviceId(paObjectId);
        String paoName = rs.getString("paoName");
        meter.setName(paoName);
        String type = rs.getString("type").intern();
        meter.setTypeStr(type);
        int deviceType = paoGroupsWrapper.getDeviceType(type);
        meter.setType(deviceType);
        String meterNumber = rs.getString("meterNumber");
        meter.setMeterNumber(meterNumber);
        char disabledChar = rs.getString("disableFlag").charAt(0);
        boolean disabled = CtiUtilities.isTrue(disabledChar);
        meter.setDisabled(disabled);
        String route = rs.getString("route");
        meter.setRoute(route);
        int routeId = rs.getInt("routeId");
        meter.setRouteId(routeId);
        String address = rs.getString("address");
        meter.setAddress(address);
    }
    
    @Override
    public String getSql() {
        return this.sql;
    }

}