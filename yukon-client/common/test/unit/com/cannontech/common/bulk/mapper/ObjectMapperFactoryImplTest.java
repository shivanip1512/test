package com.cannontech.common.bulk.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Test class for ObjectMapperFactoryImpl
 */
public class ObjectMapperFactoryImplTest extends TestCase {

    private ObjectMapperFactoryImpl mapper = null;
    private YukonDevice testDevice = new YukonDevice(1, 1);

    @Override
    protected void setUp() throws Exception {

        mapper = new ObjectMapperFactoryImpl();

        mapper.setPaoDao(new PaoDaoAdapter() {

            private LiteYukonPAObject lite1 = new LiteYukonPAObject(1,
                                                                    null,
                                                                    0,
                                                                    1,
                                                                    0,
                                                                    null,
                                                                    null);
            private LiteYukonPAObject lite2 = new LiteYukonPAObject(2,
                                                                    null,
                                                                    0,
                                                                    2,
                                                                    0,
                                                                    null,
                                                                    null);

            @Override
            public List<LiteYukonPAObject> getLiteYukonPaoByName(String name,
                    boolean partialMatch) {

                if ("none".equalsIgnoreCase(name)) {
                    return new ArrayList<LiteYukonPAObject>();
                }
                if ("one".equalsIgnoreCase(name)) {
                    return Collections.singletonList(lite1);
                }

                if ("two".equalsIgnoreCase(name)) {
                    List<LiteYukonPAObject> deviceList = new ArrayList<LiteYukonPAObject>();
                    deviceList.add(lite1);
                    deviceList.add(lite2);

                    return deviceList;
                }

                throw new IllegalArgumentException(name + " is not supported");

            }

            @Override
            public List<LiteYukonPAObject> getLiteYukonPaobjectsByMeterNumber(
                    String meterNumber) {

                if ("none".equalsIgnoreCase(meterNumber)) {
                    return new ArrayList<LiteYukonPAObject>();
                }
                if ("one".equalsIgnoreCase(meterNumber)) {
                    return Collections.singletonList(lite1);
                }

                if ("two".equalsIgnoreCase(meterNumber)) {
                    List<LiteYukonPAObject> deviceList = new ArrayList<LiteYukonPAObject>();
                    deviceList.add(lite1);
                    deviceList.add(lite2);

                    return deviceList;
                }

                throw new IllegalArgumentException(meterNumber + " is not supported");
            }

            @Override
            public List<LiteYukonPAObject> getLiteYukonPaobjectsByAddress(
                    int address) {

                if (address == 0) {
                    return new ArrayList<LiteYukonPAObject>();
                }
                if (address == 1) {
                    return Collections.singletonList(lite1);
                }

                if (address == 2) {
                    List<LiteYukonPAObject> deviceList = new ArrayList<LiteYukonPAObject>();
                    deviceList.add(lite1);
                    deviceList.add(lite2);

                    return deviceList;
                }

                throw new IllegalArgumentException(address + " is not supported");
            }

        });

        mapper.setDeviceDao(new DeviceDaoAdapter() {
            @Override
            public YukonDevice getYukonDevice(LiteYukonPAObject yukonPAObject) {
                return new YukonDevice(yukonPAObject.getLiteID(),
                                       yukonPAObject.getType());
            }

            @Override
            public YukonDevice getYukonDevice(int paoId) {

                switch (paoId) {
                case 1:
                    return new YukonDevice(1, 1);
                default:
                    throw new NotFoundException(paoId + " is not supported");
                }
            }

        });

    }

    public void testCreatePaoNameToYukonDeviceMapper() throws Exception {

        ObjectMapper<String, YukonDevice> testMapper = mapper.createPaoNameToYukonDeviceMapper();

        try {
            testMapper.map("none");
            fail("Should throw exception when there are no results");
        } catch (ObjectMappingException e) {
            // expected exception
        }

        YukonDevice device = testMapper.map("one");
        assertEquals("Mapped device is not as expected", testDevice, device);

        try {
            testMapper.map("two");
            fail("Should throw exception when there are multiple results");
        } catch (ObjectMappingException e) {
            // expected exception
        }

    }

    public void testCreateMeterNumberToYukonDeviceMapper() throws Exception {

        ObjectMapper<String, YukonDevice> testMapper = mapper.createMeterNumberToYukonDeviceMapper();

        try {
            testMapper.map("none");
            fail("Should throw exception when there are no results");
        } catch (ObjectMappingException e) {
            // expected exception
        }

        YukonDevice device = testMapper.map("one");
        assertEquals("Mapped device is not as expected", testDevice, device);

        try {
            testMapper.map("two");
            fail("Should throw exception when there are multiple results");
        } catch (ObjectMappingException e) {
            // expected exception
        }
    }

    public void testCreateAddressToYukonDeviceMapper() throws Exception {

        ObjectMapper<String, YukonDevice> testMapper = mapper.createAddressToYukonDeviceMapper();

        try {
            testMapper.map("0");
            fail("Should throw exception when there are no results");
        } catch (ObjectMappingException e) {
            // expected exception
        }

        YukonDevice device = testMapper.map("1");
        assertEquals("Mapped device is not as expected", testDevice, device);

        try {
            testMapper.map("2");
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
    }

    public void testCreateBulkImporterToYukonDeviceMapper() throws Exception {

        ObjectMapper<String, YukonDevice> testMapper = mapper.createBulkImporterToYukonDeviceMapper();

        try {
            testMapper.map("0");
            fail("Should throw exception when there are no results");
        } catch (ObjectMappingException e) {
            // expected exception
        }

        YukonDevice device = testMapper.map("1");
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

    public void testCreateLiteYukonPaoToYukonDeviceMapper() throws Exception {

        ObjectMapper<LiteYukonPAObject, YukonDevice> testMapper = mapper.createLiteYukonPAObjectToYukonDeviceMapper();

        LiteYukonPAObject litePao = new LiteYukonPAObject(1);
        litePao.setType(1);

        YukonDevice device = testMapper.map(litePao);
        assertEquals("Mapped device is not as expected", testDevice, device);

    }

    public void testCreatePaoIdToYukonDeviceMapper() throws Exception {

        ObjectMapper<Integer, YukonDevice> testMapper = mapper.createPaoIdToYukonDeviceMapper();

        try {
            testMapper.map(0);
            fail("Should throw exception when there are no results");
        } catch (ObjectMappingException e) {
            // expected exception
        }

        YukonDevice device = testMapper.map(1);
        assertEquals("Mapped device is not as expected", testDevice, device);

    }
    
    public void testCreatePassThroughMapper() throws Exception {
        
        // Test integer pass through
        ObjectMapper<Integer, Integer> testIntMapper = mapper.createPassThroughMapper();
        
        Integer testInt = 1;
        
        Integer resultInt = testIntMapper.map(testInt);
        assertEquals("Mapped integer is not as expected", testInt, resultInt);

        // Test yukon device pass through
        ObjectMapper<YukonDevice, YukonDevice> testDeviceMapper = mapper.createPassThroughMapper();
        
        YukonDevice device = testDeviceMapper.map(testDevice);
        assertEquals("Mapped yukon device is not as expected", testDevice, device);
        
    }

    /**
     * Adapter class which implements PaoDao - allows for easily overriding only
     * the mehtods you need
     */
    private class PaoDaoAdapter implements PaoDao {

        public int countLiteYukonPaoByName(String name, boolean partialMatch) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @SuppressWarnings("unchecked")
        public List getAllCapControlSubBuses() {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public LiteYukonPAObject[] getAllLiteRoutes() {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public LiteYukonPAObject[] getAllUnusedCCPAOs(Integer ignoreID) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public LiteYukonPAObject getLiteYukonPAO(int paoID) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public LiteYukonPAObject getLiteYukonPAObject(String deviceName,
                int category, int paoClass, int type) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public List<LiteYukonPAObject> getLiteYukonPAObjectBy(
                Integer[] paoType, Integer[] paoCategory, Integer[] paoClass,
                Integer[] pointTypes, Integer[] uOfMId) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public List<LiteYukonPAObject> getLiteYukonPAObjectByType(int paoType) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public List<LiteYukonPAObject> getLiteYukonPaoByName(String name,
                boolean partialMatch) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public List<LiteYukonPAObject> getLiteYukonPaobjectsByAddress(
                int address) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public int getMaxPAOid() {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public int getNextPaoId() {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public int[] getNextPaoIds(int count) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public LiteYukonPAObject[] getRoutesByType(int[] routeTypes) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public String getYukonPAOName(int paoID) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public List<LiteYukonPAObject> searchByName(String name, String paoClass) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public List<LiteYukonPAObject> getLiteYukonPaobjectsByMeterNumber(
                String meterNumber) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public List<LiteYukonPAObject> getLiteYukonPaobjectsByAddressRange(
                int startAddress, int endAddress) {
            throw new UnsupportedOperationException("Method not implemented");
        }
    }

    /**
     * Adapter class which implements DeviceDao - allows for easily overriding
     * only the mehtods you need
     */
    private class DeviceDaoAdapter implements DeviceDao {

        @SuppressWarnings("unchecked")
        public List getDevicesByDeviceAddress(Integer masterAddress,
                Integer slaveAddress) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @SuppressWarnings("unchecked")
        public List getDevicesByPort(int portId) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public LiteYukonPAObject getLiteDevice(int deviceID) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public LiteDeviceMeterNumber getLiteDeviceMeterNumber(int deviceID) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public LiteYukonPAObject getLiteYukonPAObject(String deviceName,
                int category, int paoClass, int type) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public LiteYukonPAObject getLiteYukonPAObject(String deviceName,
                String category, String paoClass, String type) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public LiteYukonPAObject getLiteYukonPaobjectByDeviceName(
                String deviceName) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public LiteYukonPAObject getLiteYukonPaobjectByMeterNumber(
                String meterNumber) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public List<LiteYukonPAObject> getLiteYukonPaobjectListByMeterNumber(
                String meterNumber) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public YukonDevice getYukonDevice(int paoId) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public YukonDevice getYukonDevice(LiteYukonPAObject yukonPAObject) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public void disableDevice(YukonDevice device) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public void enableDevice(YukonDevice device) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public void removeDevice(YukonDevice device) {
            throw new UnsupportedOperationException("Method not implemented");
        }

    }

}
