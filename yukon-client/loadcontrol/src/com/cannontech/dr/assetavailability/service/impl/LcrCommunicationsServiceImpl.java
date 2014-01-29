package com.cannontech.dr.assetavailability.service.impl;

import java.util.Map;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dao.PointDao;
import com.cannontech.dr.assetavailability.AssetAvailabilityRelays;
import com.cannontech.dr.assetavailability.dao.LcrCommunicationsDao;
import com.cannontech.dr.assetavailability.service.LcrCommunicationsService;
import com.cannontech.message.dispatch.message.PointData;

public class LcrCommunicationsServiceImpl implements LcrCommunicationsService {
    @Autowired LcrCommunicationsDao lcrCommunicationsDao;
    @Autowired AttributeService attributeService;
    @Autowired PointDao pointDao;
    
    @Override
    public void processPointData(Iterable<PointData> pointDatas) {
        for(PointData pointData : pointDatas) {
            int pointId = pointData.getId();
            PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(pointId);
            PaoIdentifier paoIdentifier = paoPointIdentifier.getPaoIdentifier();
            Instant timestamp = new Instant(pointData.getPointDataTimeStamp());
            Integer relay = getPointRuntimeRelay(paoPointIdentifier);
            if (relay != null && pointData.getValue() > 0) {
                //Non-zero runtime data
                //Update relay timestamp, runtime timestamp, comm timestamp, if newer
                lcrCommunicationsDao.updateRuntimeAndComms(paoIdentifier, relay, timestamp);
            } else {
                //Communication only, not non-zero runtime
                //Update comm timestamp, if it is newer
                lcrCommunicationsDao.updateComms(paoIdentifier, timestamp);
            }
        }
    }
    
    /**
     * Determines if this PaoPointIdentifier represents a runtime point.
     * @return The relay number this point represents, if it is a runtime point, otherwise null.
     */
    private Integer getPointRuntimeRelay(PaoPointIdentifier paoPointIdentifier) {
        for(Map.Entry<Integer, ? extends Attribute> entry : AssetAvailabilityRelays.RELAY_ATTRIBUTES.entrySet()) {
            if(attributeService.isPointAttribute(paoPointIdentifier, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
