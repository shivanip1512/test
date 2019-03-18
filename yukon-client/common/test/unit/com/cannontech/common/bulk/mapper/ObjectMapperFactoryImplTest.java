package com.cannontech.common.bulk.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;

import junit.framework.TestCase;

/**
 * Test class for ObjectMapperFactoryImpl
 */
public class ObjectMapperFactoryImplTest extends TestCase {
    private SimpleDevice testDevice = new SimpleDevice(1, PaoType.MCT310);
    private PaoDaoAdapter paoDaoAdapter;
    private DeviceDaoAdapter deviceDaoAdapter;

    @Override
    protected void setUp() throws Exception {
        paoDaoAdapter = new PaoDaoAdapter() {

            private LiteYukonPAObject lite1 = new LiteYukonPAObject(1, null, PaoType.MCT310, null, null);
            private LiteYukonPAObject lite2 = new LiteYukonPAObject(2, null, PaoType.MCT310, null, null);

            @Override
            public List<LiteYukonPAObject> getLiteYukonPaoByName(String name, boolean partialMatch) {

                if ("none".equalsIgnoreCase(name)) {
                    return new ArrayList<>();
                }
                if ("one".equalsIgnoreCase(name)) {
                    return Collections.singletonList(lite1);
                }

                if ("two".equalsIgnoreCase(name)) {
                    List<LiteYukonPAObject> deviceList = new ArrayList<>();
                    deviceList.add(lite1);
                    deviceList.add(lite2);

                    return deviceList;
                }

                throw new IllegalArgumentException(name + " is not supported");

            }

            @Override
            public List<LiteYukonPAObject> getLiteYukonPaobjectsByAddress(int address) {

                if (address == 0) {
                    return new ArrayList<>();
                }
                if (address == 1) {
                    return Collections.singletonList(lite1);
                }

                if (address == 2) {
                    List<LiteYukonPAObject> deviceList = new ArrayList<>();
                    deviceList.add(lite1);
                    deviceList.add(lite2);

                    return deviceList;
                }

                throw new IllegalArgumentException(address + " is not supported");
            }

        };

        deviceDaoAdapter = new DeviceDaoAdapter() {
            @Override
            public SimpleDevice getYukonDevice(LiteYukonPAObject yukonPAObject) {
                return new SimpleDevice(yukonPAObject.getLiteID(), yukonPAObject.getPaoType());
            }

            @Override
            public SimpleDevice getYukonDevice(int paoId) {

                switch (paoId) {
                case 1:
                    return new SimpleDevice(1, PaoType.MCT310);
                default:
                    throw new NotFoundException(paoId + " is not supported");
                }
            }

        };

    }

    public void testCreateBulkImporterToYukonDeviceMapper() throws Exception {

        ObjectMapper<String, SimpleDevice> testMapper;
        {
            BulkImporterToYukonDeviceMapper origMapper = new BulkImporterToYukonDeviceMapper();
            origMapper.setDeviceDao(deviceDaoAdapter);
            origMapper.setPaoDao(paoDaoAdapter);
            testMapper = origMapper;
        }

        try {
            testMapper.map("0");
            fail("Should throw exception when there are no results");
        } catch (ObjectMappingException e) {
            // expected exception
        }

        SimpleDevice device = testMapper.map("1");
        assertEquals("Mapped device is not as expected", testDevice, device);

        device = testMapper.map("1,2,0");
        assertEquals("Mapped device is not as expected", testDevice, device);

        try {
            testMapper.map("2");
            fail("Should throw exception when there are multiple results");
        } catch (ObjectMappingException e) {
            // expected exception
        }

        try {
            testMapper.map("2,1,2,3");
            fail("Should throw exception when there are multiple results");
        } catch (ObjectMappingException e) {
            // expected exception
        }

        try {
            testMapper.map("not an address");
            fail("Should throw exception when a non-integer value is mapped");
        } catch (ObjectMappingException e) {
            // expected exception
        }

        try {
            testMapper.map("not an address,1,2,3");
            fail("Should throw exception when a non-integer value is mapped");
        } catch (ObjectMappingException e) {
            // expected exception
        }

        try {
            testMapper.map("");
            fail("Should throw exception when an empty value is mapped");
        } catch (ObjectMappingException e) {
            // expected exception
        }

        try {
            testMapper.map(",1,2,3");
            fail("Should throw exception when an empty value is mapped");
        } catch (ObjectMappingException e) {
            // expected exception
        }

        try {
            testMapper.map(",,,");
            fail("Should throw exception when an empty value is mapped");
        } catch (ObjectMappingException e) {
            // expected exception
        }
    }

    /**
     * Adapter class which implements PaoDao - allows for easily overriding only
     * the mehtods you need
     */
    private class PaoDaoAdapter implements PaoDao {

        @Override
        public List<LiteYukonPAObject> getAllCapControlSubBuses() {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteYukonPAObject[] getAllLiteRoutes() {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public YukonPao getYukonPao(int paoId) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteYukonPAObject getLiteYukonPAO(int paoID) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<LiteYukonPAObject> getLiteYukonPAObjectByType(PaoType paoType) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<LiteYukonPAObject> getLiteYukonPaoByName(String name, boolean partialMatch) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<LiteYukonPAObject> getLiteYukonPaobjectsByAddress(int address) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public int getNextPaoId() {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteYukonPAObject[] getRoutesByType(PaoType[] routeTypes) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public String getYukonPAOName(int paoID) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public Map<Integer, String> getYukonPAONames(Iterable<Integer> ids) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<PaoIdentifier> getPaosByAddressRange(int startAddress, int endAddress) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public Integer getRouteIdForRouteName(String routeName) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public PaoLoader<DisplayablePao> getDisplayablePaoLoader() {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteYukonPAObject findUnique(String paoName, PaoType paoType) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public boolean isNameAvailable(String paoName, PaoType paoType) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<PaoIdentifier> getPaoIdentifiersForPaoIds(Iterable<Integer> paoIds) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public Map<PaoIdentifier, LiteYukonPAObject> getLiteYukonPaosById(Iterable<PaoIdentifier> paos) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public Map<Integer, PaoIdentifier> findPaoIdentifiersByCarrierAddress(Iterable<Integer> carrierAddresses) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public Map<String, PaoIdentifier> findPaoIdentifiersByMeterNumber(Iterable<String> meterNumbers) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public Map<String, PaoIdentifier> findPaoIdentifiersByName(Iterable<String> names) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public YukonPao findYukonPao(String paoName, PaoType paoType) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public YukonPao findYukonPao(String paoName, PaoCategory paoCategory, PaoClass paoClass) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public YukonPao getYukonPao(String paoName, PaoType paoType) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public YukonPao getYukonPao(String paoName, PaoCategory paoCategory, PaoClass paoClass) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public int getDisabledDeviceCount(DeviceGroup deviceGroup) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<PaoIdentifier> getAllPaoIdentifiersForTags(PaoTag paoTag, PaoTag... paoTags) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<LiteYukonPAObject> getAllPaos() {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<PaoType> getExistingPaoTypes() {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<LiteYukonPAObject> getLiteYukonPaos(Iterable<Integer> paoIds) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public int getPaoCount(Set<PaoType> paoTypes) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public YukonPao findPort(String paoName) {
            throw new UnsupportedOperationException("Method not implemented");
        }
    }

    /**
     * Adapter class which implements DeviceDao - allows for easily overriding
     * only the methods you need
     */
    private class DeviceDaoAdapter implements DeviceDao {

        @Override
        public SimpleDevice findYukonDeviceObjectByName(String name) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<Integer> getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<Integer> getDevicesByPort(int portId) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteDeviceMeterNumber getLiteDeviceMeterNumber(int deviceID) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteYukonPAObject getLiteYukonPAObject(String deviceName, PaoType paoType) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteYukonPAObject getLiteYukonPaobjectByDeviceName(String deviceName) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteYukonPAObject getLiteYukonPaobjectByMeterNumber(String meterNumber) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<LiteYukonPAObject> getLiteYukonPaobjectListByMeterNumber(String meterNumber) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public SimpleDevice getYukonDevice(int paoId) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public SimpleDevice getYukonDevice(LiteYukonPAObject yukonPAObject) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public void disableDevice(YukonDevice device) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public void enableDevice(YukonDevice device) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public void removeDevice(YukonDevice device) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public void removeDevice(int id) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public void changeAddress(YukonDevice device, int newAddress) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public void changeMeterNumber(YukonDevice device, String newMeterNumber) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public void changeName(YukonDevice device, String newName) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public void changeRoute(YukonDevice device, int newRouteId) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public SimpleDevice getYukonDeviceObjectByAddress(Long address) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public SimpleDevice getYukonDeviceObjectByMeterNumber(String meterNumber) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public SimpleDevice getYukonDeviceObjectByName(String name) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public String getFormattedName(int deviceId) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public String getFormattedName(YukonDevice device) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public SimpleDevice getYukonDeviceForDevice(DeviceBase oldDevice) {
        	throw new UnsupportedOperationException("Method not implemented");
        }
        @Override
        public PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader() {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<SimpleDevice> getDevicesForRouteId(int routeId) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public int getRouteDeviceCount(int routeId) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<SimpleDevice> getYukonDeviceObjectByIds(Iterable<Integer> ids) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<SimpleDevice> getDevicesForPaoTypes(Iterable<PaoType> types) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<DisplayableDevice> getChildDevices(int parentId) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public void updateDeviceMacAddress(PaoType type, int deviceId, String macAddress) {
            throw new MethodNotImplementedException();
        }

        @Override
        public String getDeviceMacAddress(int deviceId) {
            throw new MethodNotImplementedException();
        }

        @Override
        public boolean isMacAddressExists(String macAddress) {
            throw new MethodNotImplementedException();
        }

        @Override
        public Map<Integer, String> getDeviceMacAddresses(Collection<Integer> deviceIds) {
            throw new MethodNotImplementedException();
        }

        @Override
        public int getDeviceIdFromMacAddress(String macAddress) {
            throw new MethodNotImplementedException();
        }
    }
}