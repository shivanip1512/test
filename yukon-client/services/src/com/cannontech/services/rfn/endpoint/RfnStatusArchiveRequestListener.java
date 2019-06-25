package com.cannontech.services.rfn.endpoint;

import java.util.Date;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.status.RfnStatusArchiveRequest;
import com.cannontech.amr.rfn.message.status.RfnStatusArchiveResponse;
import com.cannontech.amr.rfn.message.status.type.DemandResetStatus;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.services.rfn.RfnArchiveCache;
import com.cannontech.services.rfn.RfnArchiveProcessor;
import com.cannontech.yukon.IDatabaseCache;

@ManagedResource
public class RfnStatusArchiveRequestListener implements RfnArchiveProcessor {
    private static final Logger log = YukonLogManager.getLogger(RfnStatusArchiveRequestListener.class);
    @Autowired private RfnArchiveCache rfnArchiveCache;
    @Autowired private AttributeService attributeService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private IDatabaseCache dbCache;
    private JmsTemplate jmsTemplate;
    private Logger rfnCommsLog = YukonLogManager.getRfnLogger();

    @Override
    public void process(Object obj, String processor) {
        processRequest((RfnStatusArchiveRequest) obj, processor);
    }
    
    /**
     * Handles message from NM, logs the message and put in on a queue.
     */
    public void handleArchiveRequest(RfnStatusArchiveRequest request) {
        if(rfnCommsLog.isEnabled(Level.INFO)) {
            rfnCommsLog.log(Level.INFO, "<<< " + request.toString());
        }
        rfnArchiveCache.add(this, request);
    }
    
    /**
     * Attempts publish the point data received from NM.
     */
    private void processRequest(RfnStatusArchiveRequest request, String processor) {
        if (!request.getRfnIdentifier().is_Empty_()) {
            if(request.getStatus() instanceof DemandResetStatus) {
                DemandResetStatus status = (DemandResetStatus) request.getStatus();
                int value = status.getData().getDemandResetStatusCodeID();
          //      publishPointData(value, BuiltInAttribute.RF_DEMAND_RESET_STATUS, request.getRfnIdentifier(), processor);
                sendAcknowledgement(request.getStatusPointId(), processor); 
            }
        } else {
            sendAcknowledgement(request.getStatusPointId(), processor);
        }
    }
    
    /**
     * Attempts to publish point data for the device. If unable to lookup device in cache the exception will be thrown and acknowledgement 
     * will not be sent to NM.
     */
    private void publishPointData(int value, BuiltInAttribute attribute, RfnIdentifier rfnIdentifier, String processor) {
        PointData pointData = null;
        try {
            Integer id = rfnDeviceDao.getDeviceIdForRfnIdentifier(rfnIdentifier);
            LiteYukonPAObject device = dbCache.getAllPaosMap().get(id);
            LitePoint point = attributeService.getPointForAttribute(device, attribute);
            pointData = new PointData();
            pointData.setId(point.getLiteID());
            pointData.setPointQuality(PointQuality.Normal);
            pointData.setValue(value);
            pointData.setTime(new Date());
            pointData.setType(point.getPointType());
            pointData.setTagsPointMustArchive(true); 

            asyncDynamicDataSource.putValue(pointData);

            log.debug("{} generated {} {} {}", processor, pointData, attribute, rfnIdentifier);
        } catch (IllegalUseOfAttribute e) {
            log.error("{} generation of point data for {} {} value {} failed", processor, rfnIdentifier, attribute, value, e);
        }
    }

    /**
     * Sends acknowledgement to NM
     */
    private void sendAcknowledgement(Long statusPointID, String processor) {
        RfnStatusArchiveResponse response = new RfnStatusArchiveResponse();
        response.setStatusPointId(response.getStatusPointId());
        log.debug("{} acknowledged statusPointId={}", processor,statusPointID);
        jmsTemplate.convertAndSend(JmsApiDirectory.RFN_STATUS_ARCHIVE.getResponseQueue().get().getName(), response);
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}