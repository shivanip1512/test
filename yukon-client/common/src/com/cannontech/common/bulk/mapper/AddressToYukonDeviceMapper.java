package com.cannontech.common.bulk.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.DeviceDao;

public final class AddressToYukonDeviceMapper implements ObjectMapper<String, SimpleDevice> {

    private DeviceDao deviceDao = null;

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public SimpleDevice map(String from) throws ObjectMappingException {

        try {
            return deviceDao.getYukonDeviceObjectByAddress(Long.valueOf(from));
        } catch (IncorrectResultSizeDataAccessException  e) {
            throw new ObjectMappingException("Device with address: '" + from + "' not found.", "invalidAddress", from, e);
        } catch (NumberFormatException e) {
            throw new ObjectMappingException("Address '" + from + "' is not a valid address. Must be numeric.", "nonNumericAddress", from, e);
        }
    }
}