package com.cannontech.common.bulk.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Test class for ObjectMapperFactoryImpl
 */
public class ObjectMapperFactoryImplTest extends TestCase {

    private ObjectMapperFactoryImpl mapper = null;
    private SimpleDevice testDevice = new SimpleDevice(1, PaoType.MCT310);
    private PaoDaoAdapter paoDaoAdapter;
    private DeviceDaoAdapter deviceDaoAdapter;

    @Override
    protected void setUp() throws Exception {

        mapper = new ObjectMapperFactoryImpl();

        paoDaoAdapter = new PaoDaoAdapter() {

            private LiteYukonPAObject lite1 = new LiteYukonPAObject(1,
                                                                    null,
                                                                    PaoType.MCT310,
                                                                    null,
                                                                    null);
            private LiteYukonPAObject lite2 = new LiteYukonPAObject(2,
                                                                    null,
                                                                    PaoType.MCT310,
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

        public YukonPao getYukonPao(int paoId) {
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
        
        @Override
        public Map<Integer, String> getYukonPAONames(Iterable<Integer> ids) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public List<LiteYukonPAObject> searchByName(String name, String paoClass) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public List<LiteYukonPAObject> getLiteYukonPaobjectsByMeterNumber(
                String meterNumber) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public List<PaoIdentifier> getPaosByAddressRange(
                int startAddress, int endAddress) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public long getObjectCountByAddressRange(int startAddress, int endAddress) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public Integer getRouteIdForRouteName(String routeName) {
            throw new UnsupportedOperationException("Method not implemented");
        }

		@Override
		public LiteYukonPAObject findUnique(String paoName, String category,
				String paoClass) {
            throw new UnsupportedOperationException("Method not implemented");
        }
		
		@Override
		public PaoLoader<DisplayablePao> getDisplayablePaoLoader() {
		    throw new UnsupportedOperationException("Method not implemented");
		}
		
		@Override
		public List<PaoIdentifier> getPaoIdentifiersForPaoIds(List<Integer> paoIds) {
			throw new UnsupportedOperationException("Method not implemented");
		}

        @Override
        public PaoIdentifier getPaoIdentifierForPaoId(int paoId) {
            throw new UnsupportedOperationException("Method not implemented");
        }

    }

    /**
     * Adapter class which implements DeviceDao - allows for easily overriding
     * only the mehtods you need
     */
    private class DeviceDaoAdapter implements DeviceDao {

        @Override
		public SimpleDevice findYukonDeviceObjectByName(String name) {
        	throw new UnsupportedOperationException("Method not implemented");
		}

		@SuppressWarnings("unchecked")
		@Override
        public List getDevicesByDeviceAddress(Integer masterAddress,
                Integer slaveAddress) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @SuppressWarnings("unchecked")
        @Override
        public List getDevicesByPort(int portId) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteYukonPAObject getLiteDevice(int deviceID) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteDeviceMeterNumber getLiteDeviceMeterNumber(int deviceID) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteYukonPAObject getLiteYukonPAObject(String deviceName,
                int category, int paoClass, int type) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteYukonPAObject getLiteYukonPAObject(String deviceName,
                String category, String paoClass, String type) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteYukonPAObject getLiteYukonPaobjectByDeviceName(
                String deviceName) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public LiteYukonPAObject getLiteYukonPaobjectByMeterNumber(
                String meterNumber) {
            throw new UnsupportedOperationException("Method not implemented");
        }

        @Override
        public List<LiteYukonPAObject> getLiteYukonPaobjectListByMeterNumber(
                String meterNumber) {
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
        public SimpleDevice getYukonDeviceObjectById(int deviceId) {
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

    }

}
