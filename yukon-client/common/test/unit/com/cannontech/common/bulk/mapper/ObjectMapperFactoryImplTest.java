package com.cannontech.common.bulk.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

/**
 * Test class for ObjectMapperFactoryImpl
 */
public class ObjectMapperFactoryImplTest extends TestCase {

    private ObjectMapperFactoryImpl mapper = null;
    private YukonDevice testDevice = new YukonDevice(1, 1);

    @Override
    protected void setUp() throws Exception {

        mapper = new ObjectMapperFactoryImpl();

        mapper.setJdbcTemplate(new SimpleJdbcOperationsAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public List query(String sql, ParameterizedRowMapper rm, Object... args)
                    throws DataAccessException {

                String string = (String) args[0];

                if ("none".equalsIgnoreCase(string)) {
                    return new ArrayList<YukonDevice>();
                }
                if ("one".equalsIgnoreCase(string)) {
                    return Collections.singletonList(testDevice);
                }

                if ("two".equalsIgnoreCase(string)) {
                    List<YukonDevice> deviceList = new ArrayList<YukonDevice>();
                    deviceList.add(testDevice);
                    deviceList.add(new YukonDevice(2, 1));

                    return deviceList;
                }

                throw new IllegalArgumentException(string + " is not supported");
            }

        });

        mapper.setPaoGroupsWrapper(new PaoGroupsWrapper() {
            public int getDeviceType(String typeString) {
                return 0;
            }

            public String getPAOTypeString(int type) {
                return null;
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

    /**
     * Adapter class which implements SimpleJdbcOperations - allows for easily
     * overriding only the mehtods you need
     */
    private class SimpleJdbcOperationsAdapter implements SimpleJdbcOperations {

        public JdbcOperations getJdbcOperations() {
            return null;
        }

        public <T> List<T> query(String sql, ParameterizedRowMapper<T> rm, Object... args)
                throws DataAccessException {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public int queryForInt(String sql, Object... args) throws DataAccessException {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public List<Map<String, Object>> queryForList(String sql, Object... args)
                throws DataAccessException {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public long queryForLong(String sql, Object... args) throws DataAccessException {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public Map<String, Object> queryForMap(String sql, Object... args)
                throws DataAccessException {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public <T> T queryForObject(String sql, Class<T> requiredType, Object... args)
                throws DataAccessException {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public <T> T queryForObject(String sql, ParameterizedRowMapper<T> rm, Object... args)
                throws DataAccessException {
            throw new UnsupportedOperationException("Method not implemented");
        }

        public int update(String sql, Object... args) throws DataAccessException {
            throw new UnsupportedOperationException("Method not implemented");
        }
    }
}
