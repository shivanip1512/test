package com.cannontech.common.bulk.mapper;

import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Default implementation of ObjectMapperFactory
 */
public class ObjectMapperFactoryImpl implements ObjectMapperFactory {

    private PaoDao paoDao = null;
    private DeviceDao deviceDao = null;

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public ObjectMapper<String, YukonDevice> createPaoNameToYukonDeviceMapper() {

        return new ObjectMapper<String, YukonDevice>() {

            public YukonDevice map(String from) throws ObjectMappingException {

                List<LiteYukonPAObject> deviceList = paoDao.getLiteYukonPaoByName(from, false);

                if (deviceList.size() == 0) {
                    throw new ObjectMappingException("Pao name '" + from + "' not found.");
                }
                if (deviceList.size() > 1) {
                    throw new ObjectMappingException("Expected one result for PaoName: " + from + " but got: " + deviceList.size());
                }

                LiteYukonPAObject liteYukonPAObject = deviceList.get(0);
                YukonDevice device = deviceDao.getYukonDevice(liteYukonPAObject);

                return device;
            }
        };
    }

    public ObjectMapper<String, YukonDevice> createMeterNumberToYukonDeviceMapper() {

        return new ObjectMapper<String, YukonDevice>() {

            public YukonDevice map(String from) throws ObjectMappingException {

                List<LiteYukonPAObject> deviceList = paoDao.getLiteYukonPaobjectsByMeterNumber(from);

                if (deviceList.size() == 0) {
                    throw new ObjectMappingException("Meternumber '" + from + "' not found.");
                }
                if (deviceList.size() > 1) {
                    throw new ObjectMappingException("Expected one result for Meternumber '" + from + "' but got: " + deviceList.size());
                }

                LiteYukonPAObject liteYukonPAObject = deviceList.get(0);
                YukonDevice device = deviceDao.getYukonDevice(liteYukonPAObject);

                return device;

            }

        };
    }

    public ObjectMapper<String, YukonDevice> createAddressToYukonDeviceMapper() {

        return new ObjectMapper<String, YukonDevice>() {

            public YukonDevice map(String from) throws ObjectMappingException {

                List<LiteYukonPAObject> deviceList = null;
                try {
                    deviceList = paoDao.getLiteYukonPaobjectsByAddress(Integer.valueOf(from));
                } catch (NumberFormatException e) {
                    throw new ObjectMappingException("Address '" + from + "' is not a valid address.",
                                                     e);
                }

                if (deviceList.size() == 0) {
                    throw new ObjectMappingException("Address '" + from + "' not found.");
                }
                if (deviceList.size() > 1) {
                    throw new ObjectMappingException("Expected one result for Address '" + from + "' but got: " + deviceList.size());
                }

                LiteYukonPAObject liteYukonPAObject = deviceList.get(0);
                YukonDevice device = deviceDao.getYukonDevice(liteYukonPAObject);

                return device;

            }

        };
    }

    public ObjectMapper<String, YukonDevice> createBulkImporterToYukonDeviceMapper() {

        return new ObjectMapper<String, YukonDevice>() {

            public YukonDevice map(String from) throws ObjectMappingException,
                    IgnoreMappingException {

                String[] strings = from.split(",");
                if (strings.length == 0 || strings[0] == null || strings[0] == "") {
                    throw new ObjectMappingException("Cannot find address on the following line: " + from + "  Address is the first value on each line in the Bulk Importer file");
                }

                String address = strings[0];

                if ("Address".equalsIgnoreCase(address)) {
                    throw new IgnoreMappingException("Ignore the header line in the Bulk Importer file");
                }

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

        };
    }

    public ObjectMapper<LiteYukonPAObject, YukonDevice> createLiteYukonPAObjectToYukonDeviceMapper() {

        return new ObjectMapper<LiteYukonPAObject, YukonDevice>() {

            public YukonDevice map(LiteYukonPAObject from) throws ObjectMappingException {

                YukonDevice device = deviceDao.getYukonDevice(from);
                return device;

            }
        };
    }
}
