package com.cannontech.dr.honeywellWifi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.stars.dr.hardware.dao.HoneywellWifiThermostatDao;

public abstract class AbstractHoneywellWifiDataProcessingStrategy implements HoneywellWifiDataProcessingStrategy {
    private static final Logger log = YukonLogManager.getLogger(AbstractHoneywellWifiDataProcessingStrategy.class);
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private AttributeService attributeService;
    @Autowired private HoneywellWifiThermostatDao honeywellWifiDao;
    
    /**
     * @param macId The formatted or unformatted (colon-less) MAC ID.
     * @return The PaoIdentifer for the Honeywell Wifi thermostat with the specified MAC ID. 
     */
    protected PaoIdentifier getThermostatByMacId(String macId) {
        if (!macId.contains(":")) {
            macId = addColons(macId);
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
        
        asyncDynamicDataSource.putValue(pointData);
    }
    
    /**
     * Formats a plain, alphanumeric mac id with colons between every two characters.
     */
    private String addColons(String macId) {
        if (macId.length() != 12) {
            throw new IllegalArgumentException("Invalid mac address, length is " + macId.length());
        }
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < macId.length(); i += 2) {
            chunks.add(macId.substring(i, i + 2));
        }
        String result = StringUtils.join(chunks, ":");
        log.debug("Formatted Mac ID: " + result);
        return result;
    }
}
