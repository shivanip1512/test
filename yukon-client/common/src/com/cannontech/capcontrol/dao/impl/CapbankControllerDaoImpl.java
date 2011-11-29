package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.device.DeviceScanType;
import com.cannontech.common.device.DeviceWindowType;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.pao.service.providers.fields.DeviceAddressFields;
import com.cannontech.common.pao.service.providers.fields.DeviceFields;
import com.cannontech.common.pao.service.providers.fields.DeviceScanRateFields;
import com.cannontech.common.pao.service.providers.fields.DeviceWindowFields;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.DbChangeType;

public class CapbankControllerDaoImpl implements CapbankControllerDao {

    private @Autowired YukonJdbcTemplate yukonJdbcTemplate;
    private @Autowired PaoDao paoDao;
    private @Autowired AttributeService attributeService;
    private @Autowired PaoCreationHelper paoCreationHelper;

    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;

    static {
        liteCapControlObjectRowMapper =
            CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
    }

    public static final ParameterizedRowMapper<LiteCapControlObject> createLiteCapControlObjectRowMapper() {
        ParameterizedRowMapper<LiteCapControlObject> rowMapper =
            new ParameterizedRowMapper<LiteCapControlObject>() {
                public LiteCapControlObject mapRow(ResultSet rs, int rowNum) throws SQLException {

                    LiteCapControlObject lco = new LiteCapControlObject();
                    lco.setId(rs.getInt("PAObjectID"));
                    lco.setType(rs.getString("TYPE"));
                    lco.setDescription(rs.getString("Description"));
                    lco.setName(rs.getString("PAOName"));
                    // This is used for orphans. We will need to adjust the SQL if we intend to use
                    // this
                    // for anything other than orphaned cbcs.
                    lco.setParentId(0);
                    return lco;
                }
            };
        return rowMapper;
    }

    @Override
    public boolean assignController(int controllerId, String capbankName) {
        YukonPao pao = paoDao.findYukonPao(capbankName, PaoType.CAPBANK);
        return (pao == null) ? false : assignController(pao.getPaoIdentifier().getPaoId(), controllerId);
    }

    @Override
    public boolean assignController(int capbankId, int controllerId) {
        YukonPao controller = paoDao.getYukonPao(controllerId);

        LitePoint pointForAttribute = attributeService.getPointForAttribute(controller, BuiltInAttribute.CONTROL_POINT);
        int pointId = pointForAttribute.getPointID();

        unassignController(controllerId);

        SqlStatementBuilder sql = new SqlStatementBuilder();

        SqlParameterSink params = sql.update("CapBank");
        params.addValue("ControlDeviceID", controllerId);
        params.addValue("ControlPointID", pointId);

        sql.append("WHERE DeviceID").eq(capbankId);

        int rowsAffected = yukonJdbcTemplate.update(sql);

        boolean result = (rowsAffected == 1);

        if (result) {
            YukonPao capBank = paoDao.getYukonPao(capbankId);
            paoCreationHelper.processDbChange(controller, DbChangeType.UPDATE);
            paoCreationHelper.processDbChange(capBank, DbChangeType.UPDATE);
        }

        return result;
    }

    @Override
    public boolean unassignController(int controllerId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        SqlParameterSink params = sql.update("CapBank");
        params.addValue("ControlDeviceID", 0);
        params.addValue("ControlPointID", 0);

        sql.append("WHERE ControlDeviceID").eq(controllerId);

        int rowsAffected = yukonJdbcTemplate.update(sql);

        boolean result = (rowsAffected == 1);

        return result;
    }

    @Override
    public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count) {
        /* Get the unordered total count */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Category").eq(PaoCategory.DEVICE);
        sql.append("    AND PAOClass").eq(PaoClass.CAPCONTROL);
        sql.append("    AND type like 'CBC%'");
        sql.append("    AND PAObjectID not in (SELECT ControlDeviceID FROM CAPBANK)");

        int orphanCount = yukonJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());

        /* Get the paged subset of cc objects */
        sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Category").eq(PaoCategory.DEVICE);
        sql.append("    AND PAOClass").eq(PaoClass.CAPCONTROL);
        sql.append("    AND type like 'CBC%'");
        sql.append("    AND PAObjectID not in (SELECT ControlDeviceID FROM CAPBANK)");
        sql.append("ORDER BY PAOName");

        PagingResultSetExtractor<LiteCapControlObject> cbcOrphanExtractor =
            new PagingResultSetExtractor<LiteCapControlObject>(start,
                                                               count,
                                                               liteCapControlObjectRowMapper);
        
        yukonJdbcTemplate.getJdbcOperations().query(sql.getSql(),
                                                    sql.getArguments(),
                                                    cbcOrphanExtractor);

        List<LiteCapControlObject> unassignedCbcs = (List<LiteCapControlObject>) cbcOrphanExtractor.getResultList();

        SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedCbcs);
        searchResult.setBounds(start, count, orphanCount);

        return searchResult;
    }

    @Override
    public DeviceFields getDeviceData(PaoIdentifier paoIdentifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT AlarmInhibit, ControlInhibit");
        sql.append("FROM Device");
        sql.append("WHERE DeviceID").eq(paoIdentifier.getPaoId());

        YukonRowMapper<DeviceFields> deviceRowMapper = new YukonRowMapper<DeviceFields>() {
            public DeviceFields mapRow(YukonResultSet rs) throws SQLException {
                DeviceFields deviceFields = new DeviceFields();

                deviceFields.setAlarmInhibit(rs.getString("AlarmInhibit"));
                deviceFields.setControlInhibit(rs.getString("ControlInhibit"));

                return deviceFields;
            }
        };

        DeviceFields deviceFields = yukonJdbcTemplate.queryForObject(sql, deviceRowMapper);

        return deviceFields;
    }

    @Override
    public DeviceScanRateFields getDeviceScanRateData(PaoIdentifier paoIdentifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT ScanType, IntervalRate, ScanGroup, AlternateRate");
        sql.append("FROM DeviceScanRate");
        sql.append("WHERE DeviceID").eq(paoIdentifier.getPaoId());

        YukonRowMapper<DeviceScanRateFields> scanRateRowMapper =
            new YukonRowMapper<DeviceScanRateFields>() {
                public DeviceScanRateFields mapRow(YukonResultSet rs) throws SQLException {
                    DeviceScanRateFields scanRateFields = new DeviceScanRateFields();

                    scanRateFields.setAlternateRate(rs.getInt("AlternateRate"));
                    scanRateFields.setIntervalRate(rs.getInt("IntervalRate"));
                    scanRateFields.setScanGroup(rs.getInt("ScanGroup"));
                    scanRateFields.setScanType(DeviceScanType.getForDbString(rs
                        .getString("ScanType")));

                    return scanRateFields;
                }
            };

        DeviceScanRateFields scanRateFields =
            yukonJdbcTemplate.queryForObject(sql, scanRateRowMapper);

        return scanRateFields;
    }

    @Override
    public DeviceWindowFields getDeviceWindowData(PaoIdentifier paoIdentifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT Type, WinOpen, WinClose, AlternateOpen, AlternateClose");
        sql.append("FROM DeviceWindow");
        sql.append("WHERE DeviceID").eq(paoIdentifier.getPaoId());

        YukonRowMapper<DeviceWindowFields> windowFieldsRowMapper =
            new YukonRowMapper<DeviceWindowFields>() {
                @Override
                public DeviceWindowFields mapRow(YukonResultSet rs) throws SQLException {
                    DeviceWindowFields windowFields = new DeviceWindowFields();

                    windowFields.setAlternateClose(rs.getInt("AlternateClose"));
                    windowFields.setAlternateOpen(rs.getInt("AlternateOpen"));
                    windowFields.setType(DeviceWindowType.getForDbString(rs.getString("Type")));
                    windowFields.setWindowClose(rs.getInt("WinClose"));
                    windowFields.setWindowOpen(rs.getInt("WinOpen"));

                    return windowFields;
                }
            };

        DeviceWindowFields windowFields =
            yukonJdbcTemplate.queryForObject(sql, windowFieldsRowMapper);

        return windowFields;
    }

    @Override
    public DeviceAddressFields getDeviceAddressData(PaoIdentifier paoIdentifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT MasterAddress, SlaveAddress, PostCommWait");
        sql.append("FROM DeviceAddress");
        sql.append("WHERE DeviceID").eq(paoIdentifier.getPaoId());

        YukonRowMapper<DeviceAddressFields> addressRowMapper =
            new YukonRowMapper<DeviceAddressFields>() {
                @Override
                public DeviceAddressFields mapRow(YukonResultSet rs) throws SQLException {
                    DeviceAddressFields addressFields = new DeviceAddressFields();

                    addressFields.setMasterAddress(rs.getInt("MasterAddress"));
                    addressFields.setPostCommWait(rs.getInt("PostCommWait"));
                    addressFields.setSlaveAddress(rs.getInt("SlaveAddress"));

                    return addressFields;
                }
            };

        DeviceAddressFields addressFields = yukonJdbcTemplate.queryForObject(sql, addressRowMapper);

        return addressFields;
    }
}