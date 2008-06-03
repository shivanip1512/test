package com.cannontech.common.bulk.mapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public final class BulkImporterToYukonDeviceMapper implements ObjectMapper<String, YukonDevice> {
    private PaoDao paoDao = null;
    private DeviceDao deviceDao = null;

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public YukonDevice map(String from) throws ObjectMappingException {

        String[] strings = from.split(",");
        if (strings.length == 0 || strings[0] == null || strings[0] == "") {
            throw new ObjectMappingException("Cannot find address on the following line: " + from + "  Address is the first value on each line in the Bulk Importer file");
        }

        String address = strings[0];

        List<LiteYukonPAObject> deviceList = null;
        try {
            deviceList = paoDao.getLiteYukonPaobjectsByAddress(Integer.valueOf(address));
        } catch (NumberFormatException e) {
            throw new ObjectMappingException("Address '" + address + "' is not a valid address. Address is the first value on each line in the Bulk Importer file",
                                             e);
        }

        if (deviceList.size() == 0) {
            throw new ObjectMappingException("Address '" + address + "' not found.");
        }
        if (deviceList.size() > 1) {
            throw new ObjectMappingException("Expected one result for Address '" + address + "' but got: " + deviceList.size());
        }

        LiteYukonPAObject liteYukonPAObject = deviceList.get(0);
        YukonDevice device = deviceDao.getYukonDevice(liteYukonPAObject);

        return device;

    }
}