package com.cannontech.billing;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;

import com.cannontech.amr.archivedValueExporter.model.CMEPUnitEnum;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.billing.model.CMEPCommodityEnum;
import com.cannontech.billing.model.CMEP_MEPMD01Record;
import com.cannontech.billing.model.CMEP_MEPMD01Record.DataEntry;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.base.Function;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CMEP_MEPMD01Format extends FileFormatBase  {

    private final ConfigurationSource configurationSource = YukonSpringHook.getBean("configurationSource", ConfigurationSource.class);
    private final DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);
    private final MeterDao meterDao = YukonSpringHook.getBean("meterDao", MeterDao.class);
    private final PaoDefinitionDao paoDefinitionDao = YukonSpringHook.getBean("paoDefinitionDao", PaoDefinitionDao.class);
    private final PersistedSystemValueDao persistedSystemValueDao = YukonSpringHook.getBean("persistedSystemValueDaoImpl", PersistedSystemValueDao.class);
    private final RawPointHistoryDao rawPointHistoryDao = YukonSpringHook.getBean("rphDao", RawPointHistoryDao.class);
    private YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);

    @Override
	public boolean retrieveBillingData() {	
        long timer = System.currentTimeMillis();
        Instant processingDate = new Instant();

        // Get the information from the CPARMS and the billing module
        Range<Long> changeIdRange = null;
        boolean useLastChangeId= getBillingFileDefaults().getToken() != null;
        if (useLastChangeId) {
            Long lastCempChangeId = persistedSystemValueDao.getLongValue(PersistedSystemValueKey.CMEP_BILLING_FILE_LAST_CHANGE_ID);
            Long maxChangeId = rawPointHistoryDao.getMaxChangeId();
            changeIdRange = Range.inclusive(lastCempChangeId, maxChangeId);
        }
        
        Date billingStartDate = getBillingFileDefaults().getEnergyStartDate();
        Date billingEndDate = getBillingFileDefaults().getEndDate();
        List<CMEPUnitEnum> cmepUnits = getCMEPUnits();
        List<DeviceGroup> billingDeviceGroups = getBillingDeviceGroups();
        
        // Build up the data entries and write them out the report file.
        Map<Integer, YukonMeter> deviceIdToMeterMap = getDeviceIdToMeterMap(billingDeviceGroups);
        Range<Date> dateRange = null;
        for (CMEPUnitEnum cmepUnit : cmepUnits) {
            try {
                ListMultimap<PaoIdentifier, PointValueQualityHolder> billingAttributeData;
                if (useLastChangeId) { // get data by token (lastChangeId)
                    dateRange = new Range<Date>(null, false, null, true);
                    billingAttributeData =
                        rawPointHistoryDao.getAttributeDataByChangeIdRange(deviceIdToMeterMap.values(),
                            cmepUnit.getAttribute(), changeIdRange, false,
                            dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD);
                } else { // get data by date range
                    dateRange = new Range<Date>(billingStartDate, false, billingEndDate, true);
                    billingAttributeData =
                        rawPointHistoryDao.getAttributeData(deviceIdToMeterMap.values(), cmepUnit.getAttribute(), false,
                            dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD, null);
                }

                for (Entry<PaoIdentifier, PointValueQualityHolder> entry : billingAttributeData.entries()) {
                    PointValueQualityHolder pointValueHolder = entry.getValue();
                    PaoIdentifier paoIdentifier = entry.getKey();
                    CMEPCommodityEnum commodity = getCmepCommodity(paoIdentifier);
                    
                    CMEP_MEPMD01Record billingRow = new CMEP_MEPMD01Record();
                    billingRow.setTimestamp(processingDate);
                    billingRow.setMeterId(deviceIdToMeterMap.get(paoIdentifier.getPaoId()).getMeterNumber());
                    billingRow.setCommodity(commodity);
                    billingRow.setUnits(cmepUnit);
                    billingRow.setCalculationConstant(getCalculationConstant(paoIdentifier, cmepUnit));
    
                    DataEntry dataEntry = billingRow.new DataEntry();
                    dataEntry.setReadingTimstamp(new Instant(pointValueHolder.getPointDataTimeStamp()));
                    dataEntry.setReadingValue(pointValueHolder.getValue());
                    billingRow.addReadingData(dataEntry);
                    
                    getRecordVector().add(billingRow);
                }
                
            // The user is trying to generate a billing file for a CMEP unit that we do not currently use in Yukon.  Log the error and move on.
            } catch (IllegalUseOfAttribute e) {
                CTILogger.error(e);
            }
        }
        
        // If we are using the changeId version make sure to update the changeId to the next entry needed.
        if (useLastChangeId) {
            persistedSystemValueDao.setValue(PersistedSystemValueKey.CMEP_BILLING_FILE_LAST_CHANGE_ID, changeIdRange.getMax());
        }

	    CTILogger.info("@" +this.toString() +" Data Collection : Took " + (System.currentTimeMillis() - timer));
	    return true;
	}

    /**
     * This method gets all of the meters for the supplied groups and builds up a map to better access these meters by their id.
     */
    private Map<Integer, YukonMeter> getDeviceIdToMeterMap(List<DeviceGroup> billingDeviceGroups) {
        Set<SimpleDevice> devices = deviceGroupService.getDevices(billingDeviceGroups);
        List<YukonMeter> meters = meterDao.getMetersForYukonPaos(devices);
        Map<Integer, YukonMeter> deviceIdToMeterMap =
            Maps.uniqueIndex(meters, new Function<YukonMeter, Integer>() {
                @Override
                public Integer apply(YukonMeter meter) {
                    return meter.getDeviceId();
                }
            });
        return deviceIdToMeterMap;
    }

    /**
     * This method returns the given CMEPCommodity for the supplied paoIdentifier.
     */
    private CMEPCommodityEnum getCmepCommodity(PaoIdentifier paoIdentifier) {
        CMEPCommodityEnum commodity = CMEPCommodityEnum.ELECTRIC;  // I'm not sure we want to do this, but we're defaulting it to electric for now.
        if (paoDefinitionDao.isTagSupported(paoIdentifier.getPaoType(), PaoTag.DEVICE_ICON_TYPE)) {
            String serviceTypeStr = paoDefinitionDao.getValueForTagString(paoIdentifier.getPaoType(), PaoTag.DEVICE_ICON_TYPE);
            commodity = CMEPCommodityEnum.findByDeviceIconType(serviceTypeStr);
        }
        return commodity;
    }

    /**
     * Gets the CMEP units from master.cfg.  If this CPARM doesn't exist it will use the KVARHREG CMEP unit.
     */
    private List<CMEPUnitEnum> getCMEPUnits() {
        String cmepUnitsStr = configurationSource.getString(MasterConfigString.CMEP_UNITS, "KWHREG,KW");
        List<String> cmepUnitsStrs = Lists.newArrayList(StringUtils.split(cmepUnitsStr, ','));
        List<CMEPUnitEnum> cmepUnits = 
            Lists.transform(cmepUnitsStrs, new Function<String, CMEPUnitEnum>() {
                @Override
                public CMEPUnitEnum apply(String cmepUnitStr) {
                    CMEPUnitEnum cmepUnit = CMEPUnitEnum.valueOf(cmepUnitStr);
                    return cmepUnit;
                }});
        return cmepUnits;
    }

    /**
     * This method gets the billing device groups for the CMEP report
     */
    private List<DeviceGroup> getBillingDeviceGroups() {
        List<String> billingDeviceGroupsStr = getBillingFileDefaults().getDeviceGroups();
        List<DeviceGroup> billingDeviceGroups = Lists.transform(billingDeviceGroupsStr, new Function<String, DeviceGroup>() {
            @Override
            public DeviceGroup apply(String deviceGroupName) {
                return deviceGroupService.findGroupName(deviceGroupName);
            }
        });
        return billingDeviceGroups;
    }

    /**
     * This method gets the calculation constant for the given device and cmepUnit. 
     * 
     * Example:  
     *     LoadProfileDemandRate is 900 seconds (15 mins)
     *     This method would return .25 since 15 mins is a quarter of an hour.
     */
    private float getCalculationConstant(PaoIdentifier paoIdentifier, CMEPUnitEnum cmepUnit) {

        if (cmepUnit == CMEPUnitEnum.KWH) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT LoadProfileDemandRate");
            sql.append("FROM DeviceLoadProfile");
            sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
                
            int deviceLoadProfile = jdbcTemplate.queryForInt(sql);
            if (deviceLoadProfile != 0) {
                return deviceLoadProfile/ 3600f; // An hour in seconds
            }
        }

        return 1f;
    }
}