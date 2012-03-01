package com.cannontech.billing;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.transaction.NotSupportedException;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.billing.model.CMEPCommodityEnum;
import com.cannontech.billing.model.CMEPUnitEnum;
import com.cannontech.billing.model.CMEP_MEPMD01Record;
import com.cannontech.billing.model.CMEP_MEPMD01Record.DataEntry;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.RPHServiceTagDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.RphServiceTag;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.base.Function;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CMEP_MEPMD01Format extends FileFormatBase  {

    private ConfigurationSource configurationSource = YukonSpringHook.getBean("configurationSource", ConfigurationSource.class);
    private DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);
    private MeterDao meterDao = YukonSpringHook.getBean("meterDao", MeterDao.class);
    private PaoDefinitionDao paoDefinitionDao = YukonSpringHook.getBean("paoDefinitionDao", PaoDefinitionDao.class);
    private RawPointHistoryDao rawPointHistoryDao = YukonSpringHook.getBean("rphDao", RawPointHistoryDao.class);
    private RPHServiceTagDao rphServiceTagDao = YukonSpringHook.getBean("rphServiceTagDao", RPHServiceTagDao.class);

    private String RPH_SERVICE = "CMEP";
    
    @Override
	public boolean retrieveBillingData() {	
        long timer = System.currentTimeMillis();
        Instant processingDate = new Instant();

        // Get the information from the CPARMS and the billing module
        RphServiceTag cmepServiceTag = null;
        Integer startChangeId = null;
        Integer stopChangeId = null;
        if (getBillingFileDefaults().getToken() != null) {
            cmepServiceTag = rphServiceTagDao.getRphServiceTag(RPH_SERVICE, null);
            startChangeId = cmepServiceTag.getChangeId();
            stopChangeId = rawPointHistoryDao.getMaxChangeId();
        }
        
        Date billingStartDate = getBillingFileDefaults().getEnergyStartDate();
        Date billingEndDate = getBillingFileDefaults().getEndDate();
        List<CMEPUnitEnum> cmepUnits = getCMEPUnits();
        List<DeviceGroup> billingDeviceGroups = getBillingDeviceGroups();
        
        // Build up the data entries and write them out the report file.
        Map<Integer, Meter> deviceIdToMeterMap = getDeviceIdToMeterMap(billingDeviceGroups);
        for (CMEPUnitEnum cmepUnit : cmepUnits) {
            try {
                ListMultimap<PaoIdentifier, PointValueQualityHolder> billingAttributeData;  
                if (cmepServiceTag != null) {
                    billingAttributeData = rawPointHistoryDao.getAttributeData(deviceIdToMeterMap.values(), cmepUnit.getAttribute(), startChangeId, stopChangeId, false, Clusivity.INCLUSIVE_EXCLUSIVE, Order.FORWARD);
                } else {
                    billingAttributeData = rawPointHistoryDao.getAttributeData(deviceIdToMeterMap.values(), cmepUnit.getAttribute(), billingStartDate, billingEndDate, false, Clusivity.INCLUSIVE_EXCLUSIVE, Order.FORWARD);
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
                    billingRow.setCalculationConstant(1);
    
                    DataEntry dataEntry = billingRow.new DataEntry();
                    dataEntry.setReadingTimstamp(new Instant(pointValueHolder.getPointDataTimeStamp()));
                    dataEntry.setReadingValue(pointValueHolder.getValue());
                    billingRow.addReadingData(dataEntry);
                    
                    getRecordVector().add(billingRow);
                }
                
            // The user is trying to generate a billing file for a CMEP unit that we do not currently use in Yukon.  Log the error and move on.
            } catch (NotSupportedException e) {
                CTILogger.error(e);
            }
        }
        
        // If we are using the changeId version make sure to update the changeId to the next entry needed.
        if (cmepServiceTag != null) {
            cmepServiceTag.setChangeId(stopChangeId+1);
            rphServiceTagDao.save(cmepServiceTag);
        }

	    CTILogger.info("@" +this.toString() +" Data Collection : Took " + (System.currentTimeMillis() - timer));
	    return true;
	}

    /**
     * This method gets all of the meters for the supplied groups and builds up a map to better access these meters by their id.
     */
    private Map<Integer, Meter> getDeviceIdToMeterMap(List<DeviceGroup> billingDeviceGroups) {
        Set<SimpleDevice> devices = deviceGroupService.getDevices(billingDeviceGroups);
        List<Meter> meters = meterDao.getMetersForYukonPaos(devices);
        Map<Integer, Meter> deviceIdToMeterMap =
            Maps.uniqueIndex(meters, new Function<Meter, Integer>() {
                @Override
                public Integer apply(Meter meter) {
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
        String cmepUnitsStr = configurationSource.getString(MasterConfigBooleanKeysEnum.CMEP_UNITS, "KWHREG,KW");
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
}