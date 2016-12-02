/**
 * 
 */
package com.cannontech.common.systemMetrics;


import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.device.Device;

/**
 * Spring Bean that inserts point data for CPU and Memory point of the invoking service.
 */
@ManagedResource
public class SystemMetricsImpl extends SystemMetricsBase {

    @Autowired private AttributeService attributeService;
    @Autowired private SimplePointAccessDao pointAccessDao;

    private Logger log = YukonLogManager.getLogger(this.getClass());

    /** Point for process CPU usage */
    private LitePoint loadAveragePoint;
    /** Point for process memory usage */
    private LitePoint memoryPoint;
    
    /** Attribute to access process memory usage */
   // private BuiltInAttribute memoryAttribute;
    /** Attribute to access process CPU usage */
   // private Attribute loadAverageAttribute;
    
    private HashMap<Attribute, LitePoint> pointAttributeMapping = new HashMap<>();

    /**
     * After everything is set, we need to do some one time lookups.
     */
    public void init() {
        log.debug("SystemMetricsImpl.init()");

        PaoIdentifier paoIdentifier = new PaoIdentifier(Device.SYSTEM_DEVICE_ID, PaoType.SYSTEM);
        try {
            loadAveragePoint = attributeService.getPointForAttribute(paoIdentifier,
                                                                     loadAverageAttribute);
        } catch (IllegalUseOfAttribute e) {
            log.error("Attribute: [" + loadAverageAttribute + "] not found for pao type: [SYSTEM]");
        }
        try {
            memoryPoint = attributeService.getPointForAttribute(paoIdentifier, memoryAttribute);
        } catch (IllegalUseOfAttribute e) {
            log.error("Attribute: [" + memoryAttribute + "] not found for pao type: [SYSTEM]");
        }
        pointAttributeMapping.put(loadAverageAttribute, loadAveragePoint);
        pointAttributeMapping.put(memoryAttribute, memoryPoint);
        
    }

    @Override
    public void insertPointData(Attribute attribute, double value) {
        LitePoint point = pointAttributeMapping.get(attribute);
        pointAccessDao.setPointValue(point, value);
    }
}
