package com.cannontech.services.systemDataPublisher.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cannontech.common.device.data.collection.dao.RecentPointValueDao;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.RangeType;
import com.cannontech.common.device.data.collection.model.DataCollectionSummary;
import com.cannontech.common.device.data.collection.service.DataCollectionHelper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.service.model.DataCompletenessSummary;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.ImmutableSet;

@Repository
public class SystemDataPublisherDaoImpl implements SystemDataPublisherDao {

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private RecentPointValueDao rpvDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private RawPointHistoryDao rphDao; 

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
    public double getDataCompleteness(String deviceGroupName, ImmutableSet<PaoType> paoType) {
        DeviceGroup deviceGroup = deviceGroupService.findGroupName(deviceGroupName);
        Date startDate = new Instant()
                .minus(Duration.standardDays(globalSettingDao.getInteger(GlobalSettingType.DATA_AVAILABILITY_WINDOW_IN_DAYS) + 7))
                .toDate();
        Date stopDate = new Instant()
                .minus(Duration.standardDays(globalSettingDao.getInteger(GlobalSettingType.DATA_AVAILABILITY_WINDOW_IN_DAYS)))
                .toDate();
        Range<Date> dateRange = new Range<Date>(startDate, false, stopDate, true);
        DataCompletenessSummary records = rphDao.getDataCompletenessRecords(deviceGroup, dateRange, paoType);
        double dataCompleteness;
        if (records.getPaoCount() == 0) {
            dataCompleteness = 0;
        } else {
            double actual = records.getRecordCount();
            double expected = records.getPaoCount() * 168.00;
            dataCompleteness = (actual / expected) * 100;
            dataCompleteness = new BigDecimal(dataCompleteness).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }
        return dataCompleteness;
    }
}
