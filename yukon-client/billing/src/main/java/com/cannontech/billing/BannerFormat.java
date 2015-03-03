package com.cannontech.billing;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.billing.record.BannerData;
import com.cannontech.billing.record.BannerRecord;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class BannerFormat extends FileFormatBase {

    private final RawPointHistoryDao rawPointHistoryDao = YukonSpringHook.getBean(RawPointHistoryDao.class);
    private final YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
    private final DeviceGroupService deviceGroupService = YukonSpringHook.getBean(DeviceGroupService.class);
    private final MeterDao meterDao = YukonSpringHook.getBean(MeterDao.class);

    private final Logger log = YukonLogManager.getLogger(BannerFormat.class);
    
    @Override
    public boolean retrieveBillingData() {
        Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(getBillingFileDefaults().getDeviceGroups());
        Set<SimpleDevice> allDevices = deviceGroupService.getDevices(deviceGroups);
        final Map<MultiKey<String>, BannerData> recordsByMeterNumberScat = getBannerRecords();
        
        ListMultimap<PaoIdentifier, PointValueQualityHolder> limitedUsageAttributeDatas =
                rawPointHistoryDao.getLimitedAttributeData(allDevices,
                                                           Sets.newHashSet(BuiltInAttribute.USAGE, BuiltInAttribute.USAGE_WATER),
                                                           Range.exclusiveInclusive(new Instant(getBillingFileDefaults().getEnergyStartDate()),
                                                                                    new Instant(getBillingFileDefaults().getEndDate())),
                                                           1, false, Order.REVERSE, null); 

        
        Range<Instant> instantRange =
            new Range<Date>(getBillingFileDefaults().getDemandStartDate(), false,
                getBillingFileDefaults().getEndDate(), true).translate(CtiUtilities.INSTANT_FROM_DATE);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> limitedPeakDemandAttributeDatas =
            rawPointHistoryDao.getLimitedAttributeData(allDevices, BuiltInAttribute.PEAK_DEMAND, 1, false,
                instantRange, Order.REVERSE, null);
        
        List<YukonMeter> meters = meterDao.getMetersForYukonPaos(allDevices);
        for (YukonMeter meter : meters) {
            boolean dataAdded = false;
            
            final MultiKey<String> usageKey = new MultiKey<String>(meter.getMeterNumber(), "1");
            BannerData usageData = recordsByMeterNumberScat.remove(usageKey);
            if (usageData != null) {
                List<PointValueQualityHolder> pointValues = limitedUsageAttributeDatas.get(meter.getPaoIdentifier());
                addPointValueToRecords(pointValues, usageData, BuiltInAttribute.USAGE);    //attribute needs to align with the type of data in pointValues
                dataAdded = true;
            }

            final MultiKey<String> peakDemandKey = new MultiKey<String>(meter.getMeterNumber(), "2");
            BannerData peakDemandData = recordsByMeterNumberScat.remove(peakDemandKey);
            if (peakDemandData != null) {
                List<PointValueQualityHolder> pointValues = limitedPeakDemandAttributeDatas.get(meter.getPaoIdentifier());
                addPointValueToRecords(pointValues, peakDemandData, BuiltInAttribute.PEAK_DEMAND);    //attribute needs to align with the type of data in pointValues
                dataAdded = true;
            }
            
            if (!dataAdded) {
                log.warn("No BannerData found for meternumber: " + meter.getMeterNumber() + ". Check BannerData_View for existance.");
                // error...meter doesn't exist in banner view
            }
        }
        return true;
    }

    /**
     * Loads BannerData values from database.
     * Requires special view BannerData_View to exist: YUK-12890
     * Returns a map of <MeterNumber, BannerData>
     */
    private Map<MultiKey<String>, BannerData> getBannerRecords() {
        final Map<MultiKey<String>, BannerData> recordsMap = Maps.newHashMap();
        

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT MeterNumber, PremiseNumber, ServiceNumber, RouteNumber, ScatNumber");
        sql.append("FROM BannerData_View");

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                String meterNumber = rs.getString("MeterNumber");
                String premiseNumber = rs.getString("PremiseNumber");
                String serviceNumber = rs.getString("ServiceNumber");
                String routeNumber = rs.getString("RouteNumber");
                int scatNumber = rs.getInt("ScatNumber");
                BannerData data = new BannerData(premiseNumber, serviceNumber, meterNumber, routeNumber, scatNumber);
                final MultiKey<String> key = new MultiKey<String>(meterNumber, String.valueOf(scatNumber));
                recordsMap.put(key,  data);
            }
        });
        log.info("Loaded " + recordsMap.size() + " records from BannerData_View");
        return recordsMap;
    }
    
    /**
     * Helper method to load the BannerRecord object and add it to the recordVector
     */
    private void addPointValueToRecords(List<PointValueQualityHolder> pointValues, BannerData bannerData, BuiltInAttribute attribute) {
        //populate the rest of the object!
        if (!pointValues.isEmpty()) {
            // Only asked for one in the query, so get(0) is safe to assume.
            PointValueQualityHolder thisValue = pointValues.get(0);
            
            BannerRecord record = new BannerRecord(bannerData, attribute, thisValue.getValue(), thisValue.getPointDataTimeStamp());
            getRecordVector().add(record);
        } else {
            log.trace("No " + attribute + " pointdata found for meternumber: " + bannerData.getMeterNumber());
        }
    }
}
