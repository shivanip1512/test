package com.cannontech.dr.honeywellWifi.azure.event.processing;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.stars.dr.hardware.dao.HoneywellWifiThermostatDao;

public abstract class AbstractHoneywellWifiDataProcessor implements HoneywellWifiDataProcessor {
    private static final Logger log = YukonLogManager.getLogger(AbstractHoneywellWifiDataProcessor.class);
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private AttributeService attributeService;
    @Autowired private HoneywellWifiThermostatDao honeywellWifiDao;
    @Autowired private DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;
    /**
     * @param macId The formatted or unformatted (colon-less) MAC ID.
     * @return The PaoIdentifer for the Honeywell Wifi thermostat with the specified MAC ID. 
     */
    protected PaoIdentifier getThermostatByMacId(String macId) {
        if (!macId.contains(":")) {
            macId = StringUtils.colonizeMacAddress(macId);
            log.debug("Formatted Mac ID - " + macId);
        }
        PaoIdentifier paoIdentifier = honeywellWifiDao.getPaoIdentifierByMacId(macId);
        return paoIdentifier;
    }
    
    /**
     * Generates point data and sends it to dispatch to be processed and inserted in the DB.
     * @param paoIdentifier The device that produced the data.
     * @param attribute The attribute associated with the data.
     * @param time The time the point data was generated.
     * @param pointValue The numeric point data value to insert.
     */
    protected void inputPointValue(PaoIdentifier paoIdentifier, BuiltInAttribute attribute, Instant time, 
                                  Double pointValue) {
        LitePoint point = attributeService.getPointForAttribute(paoIdentifier, attribute);
        
        PointData pointData = new PointData();
        pointData.setId(point.getLiteID());
        pointData.setValue(pointValue);
        pointData.setType(point.getPointType());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setTime(time.toDate());
        pointData.setTagsDataTimestampValid(true);
        
        log.debug("Submitting point data: " + pointData);
        
        boolean dispatchConnected = false;
        while (!dispatchConnected) {
            try {
                asyncDynamicDataSource.putValue(pointData);
                dispatchConnected = true;
            } catch (DispatchNotConnectedException e) {
                log.info("Not connected to Dispatch. Point data cannot be processed. Retrying in 5 seconds.");
                try { Thread.sleep(5000); } catch (InterruptedException e1) { /*ignore interruption*/ }
            }
        }
    }
    
    public void updateAssetAvailability(HoneywellWifiData data) {
        log.debug("Updating asset availability for Honeywell Device: " + data);
        
        Instant time = data.getMessageWrapper().getDate();
        
        try {
            PaoIdentifier thermostat = getThermostatByMacId(data.getMacId());

            AssetAvailabilityPointDataTimes assetAvailabilityPointDataTimes = new AssetAvailabilityPointDataTimes(thermostat.getPaoId());
            assetAvailabilityPointDataTimes.setLastCommunicationTime(time);
            dynamicLcrCommunicationsDao.insertData(assetAvailabilityPointDataTimes);

        } catch (NotFoundException e) {
            log.info("Unable to update asset availability for unknown device with MAC ID " + data.getMacId());
        }
        
    }
}
