package com.cannontech.common.bulk.mapper;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

/**
 * Default implementation of ObjectMapperFactory
 */
public class ObjectMapperFactoryImpl implements ObjectMapperFactory {

    private SimpleJdbcOperations jdbcTemplate = null;
    private PaoGroupsWrapper paoGroupsWrapper = null;

    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }

    public ObjectMapper<String, YukonDevice> createPaoNameToYukonDeviceMapper() {

        return new ObjectMapper<String, YukonDevice>() {

            public YukonDevice map(String from) throws ObjectMappingException {

                // Call YukonDeviceDao instead??

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ypo.paobjectid, ypo.type");
                sql.append("FROM Device d");
                sql.append("JOIN YukonPaObject ypo ON d.deviceid = ypo.paobjectid");
                sql.append("WHERE upper(ypo.PAOName) LIKE ?");
                YukonDeviceRowMapper mapper = new YukonDeviceRowMapper(paoGroupsWrapper);

                List<YukonDevice> deviceList = jdbcTemplate.query(sql.toString(),
                                                                  mapper,
                                                                  StringUtils.upperCase(from));

                if (deviceList.size() == 0) {
                    throw new ObjectMappingException("Pao name '" + from + "' not found.");
                }
                if (deviceList.size() > 1) {
                    throw new ObjectMappingException("Expected one result for PaoName: " + from + " but got: " + deviceList.size());
                }

                return deviceList.get(0);
            }
        };
    }

    public ObjectMapper<String, YukonDevice> createMeterNumberToYukonDeviceMapper() {

        return new ObjectMapper<String, YukonDevice>() {

            public YukonDevice map(String from) throws ObjectMappingException {

                // Call YukonDeviceDao instead??

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ypo.paobjectid, ypo.type");
                sql.append("FROM Device d");
                sql.append("JOIN YukonPaObject ypo ON d.deviceid = ypo.paobjectid");
                sql.append("JOIN DeviceMeterGroup dmg ON d.deviceid = dmg.deviceid");
                sql.append("WHERE upper(dmg.meternumber) LIKE ?");
                YukonDeviceRowMapper mapper = new YukonDeviceRowMapper(paoGroupsWrapper);

                List<YukonDevice> deviceList = jdbcTemplate.query(sql.toString(),
                                                                  mapper,
                                                                  StringUtils.upperCase(from));

                if (deviceList.size() == 0) {
                    throw new ObjectMappingException("Meternumber '" + from + "' not found.");
                }
                if (deviceList.size() > 1) {
                    throw new ObjectMappingException("Expected one result for Meternumber '" + from + "' but got: " + deviceList.size());
                }

                return deviceList.get(0);

            }

        };
    }
}
