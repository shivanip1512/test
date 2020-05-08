package com.cannontech.services.systemDataPublisher.dao.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.stereotype.Repository;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.NetworkManagerJdbcTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

@Repository
public class SystemDataPublisherDaoImpl implements SystemDataPublisherDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NetworkManagerJdbcTemplate networkManagerJdbcTemplate;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private GlobalSettingDao globalSettingDao;

    private static final ColumnMapRowMapper columnMapRowMapper = new ColumnMapRowMapper();
    private static final Logger log = YukonLogManager.getLogger(SystemDataPublisherDaoImpl.class);

    @Override
    public List<Map<String, Object>> getSystemData(CloudDataConfiguration cloudDataConfiguration) {
        List<Map<String, Object>> systemData = null;
        try {
            systemData = jdbcTemplate.query(cloudDataConfiguration.getSource(), columnMapRowMapper);
        } catch (EmptyResultDataAccessException e) {
            log.debug("No result found for field = " + cloudDataConfiguration.getField());
        }
        return systemData;
    }

    @Override
    public List<Map<String, Object>> getNMSystemData(CloudDataConfiguration cloudDataConfiguration) {
        List<Map<String, Object>> nmSystemData = null;
        try {
            nmSystemData = networkManagerJdbcTemplate.query(cloudDataConfiguration.getSource(), columnMapRowMapper);
        } catch (EmptyResultDataAccessException e) {
            log.debug("No result found for NM field = " + cloudDataConfiguration.getField());
        }
        return nmSystemData;
    }

    @Override
    public int getReadRate(String deviceGroupName) {
        int days = globalSettingDao.getInteger(GlobalSettingType.DATA_AVAILABILITY_WINDOW_IN_DAYS);
        DeviceGroup deviceGroup = deviceGroupService.findGroupName(deviceGroupName);
        SqlFragmentSource groupSqlWhereClause = getDeviceGroupSql(deviceGroup);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DECLARE @totalRFNMeters INTEGER");
        sql.append("DECLARE @reportingRFNMeters INTEGER");
        sql.append("DECLARE @value FLOAT");
        sql.append("    SET @totalRFNMeters =");
        sql.append("        (SELECT COUNT(*) FROM YukonPaObject pao");
        sql.append("         WHERE").appendFragment(groupSqlWhereClause);
        sql.append("    )IF @totalRFNMeters < 1 SET @value = 0");
        sql.append("    ELSE SET @reportingRFNMeters = ");
        sql.append("        (SELECT SUM(TotalVal) FROM (");
        sql.append("            SELECT COUNT(pao.PAObjectID) AS TotalVal FROM YukonPaObject pao");
        sql.append("            JOIN Point p ON pao.PaObjectId = p.PaObjectId");
        sql.append("            JOIN RawPointHistory rph ON rph.PointId = p.PointId");
        sql.append("            JOIN DeviceMeterGroup dmg ON pao.PaObjectId = dmg.DeviceId");
        sql.append("            JOIN RecentPointValue rpv ON pao.PaObjectId = rpv.PaObjectId");
        sql.append("            WHERE").appendFragment(groupSqlWhereClause);
        sql.append("            AND rph.TimeStamp > DATEADD(day, -").append(days);
        sql.append("            , CONVERT(date,SYSDATETIME()))");
        sql.append("            AND PointType = 'Analog'");
        sql.append("            AND PointOffset = 1");
        sql.append("            GROUP BY pao.PaObjectId) a)");
        sql.append("    IF @reportingRFNMeters > 0 SET @value = ROUND((@reportingRFNMeters/@totalRFNMeters)*100, 2)");
        sql.append("    ELSE SET @value = 0");
        sql.append("    SELECT @value AS ElectricReadRate");

        log.debug("SQL for Read Rate ", sql);

        int readRate = jdbcTemplate.queryForInt(sql);
        return readRate;
    }

    /**
     * Returns SqlFragementSource for the passed deviceGroup.
     */
    private SqlFragmentSource getDeviceGroupSql(DeviceGroup deviceGroup) {
        SqlFragmentSource groupSqlWhereClause = deviceGroupService
                .getDeviceGroupSqlWhereClause(Collections.singleton(deviceGroup), "pao.PaObjectId");

        return groupSqlWhereClause;
    }
}
