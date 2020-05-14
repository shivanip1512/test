package com.cannontech.services.systemDataPublisher.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.stereotype.Repository;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.RangeType;
import com.cannontech.common.device.data.collection.model.DataCollectionDetail;
import com.cannontech.common.device.data.collection.model.DataCollectionSummary;
import com.cannontech.common.device.data.collection.service.DataCollectionHelper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.Range;
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
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private RecentPointValueDao rpvDao;
    @Autowired private DeviceGroupService deviceGroupService;

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
    public DataCollectionSummary getReadRate(String deviceGroupName) {
        boolean includeDisabled = true;
        DeviceGroup deviceGroup = deviceGroupService.findGroupName(deviceGroupName);
        Duration days = Duration.standardDays(globalSettingDao.getInteger(GlobalSettingType.DATA_AVAILABILITY_WINDOW_IN_DAYS));
        Map<RangeType, Range<Instant>> ranges = DataCollectionHelper.getRanges(days);

        DataCollectionSummary summary = new DataCollectionSummary(Instant.now());
        summary.setAvailable(
                rpvDao.getDeviceCount(deviceGroup, includeDisabled, null, RangeType.AVAILABLE, ranges.get(RangeType.AVAILABLE)));
        summary.setExpected(
                rpvDao.getDeviceCount(deviceGroup, includeDisabled, null, RangeType.EXPECTED, ranges.get(RangeType.EXPECTED)));
        summary.setOutdated(
                rpvDao.getDeviceCount(deviceGroup, includeDisabled, null, RangeType.OUTDATED, ranges.get(RangeType.OUTDATED)));
        summary.setUnavailable(rpvDao.getDeviceCount(deviceGroup, includeDisabled, null, RangeType.UNAVAILABLE,
                ranges.get(RangeType.UNAVAILABLE)));
        summary.calculatePrecentages();

        return summary;
    }

    @Override
    public DataCollectionDetail getDataCompleteness(String deviceGroupName) {
        boolean includeDisabled = true;
        DeviceGroup deviceGroup = deviceGroupService.findGroupName(deviceGroupName);
        Duration days = Duration.standardDays(globalSettingDao.getInteger(GlobalSettingType.DATA_AVAILABILITY_WINDOW_IN_DAYS));
        Map<RangeType, Range<Instant>> ranges = DataCollectionHelper.getRanges(days);
        DataCollectionDetail detail = new DataCollectionDetail(
                rpvDao.getDeviceCount(deviceGroup, includeDisabled, null, RangeType.EXPECTED,
                        ranges.get(RangeType.EXPECTED)));
        return detail;
    }
}
