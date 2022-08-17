package com.cannontech.common.rfn.service.impl;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.message.metadata.RfnMetadata;
import com.cannontech.common.rfn.message.metadata.RfnMetadataRequest;
import com.cannontech.common.rfn.message.metadata.RfnMetadataResponse;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.DataCallback;
import com.cannontech.common.rfn.service.RfnDeviceMetadataService;
import com.cannontech.common.util.jms.JmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class RfnDeviceMetadataServiceImpl implements RfnDeviceMetadataService {

    public static final String commsError =
        "Unable to send request due to a communication error between Yukon and Network Manager.";
    public static final String nmError = "Received error from Network Manager.";
        
    private static final Logger log = YukonLogManager.getLogger(RfnDeviceMetadataServiceImpl.class);

    private RequestReplyTemplateImpl<RfnMetadataResponse> qrTemplate;
    
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ConfigurationSource configurationSource;
    
    /**
     * Attempts to send a meta-data request for a RFN device.  Will use a separate thread to make the request.
     * Will expect one response. 
     * 
     * The response message should come back within seconds.  Default timeout is one minute.
     * 
     * @param device The device to get meta-data for.
     * @param callback The callback to use for updating the result.
     */
    @Override
    public void send(final RfnDevice device, final DataCallback<Map<RfnMetadata, Object>> callback, final String keyPrefix) {
        
        JmsReplyHandler<RfnMetadataResponse> handler = new JmsReplyHandler<RfnMetadataResponse>() {

            @Override
            public void complete() {
                callback.complete();
            }

            @Override
            public void handleException(Exception e) {
                MessageSourceResolvable message = 
                    YukonMessageSourceResolvable.createSingleCodeWithArguments(keyPrefix + "error", e.toString());
                callback.processingExceptionOccurred(message);
            }

            @Override
            public void handleReply(RfnMetadataResponse reply) {
                if (!reply.isSuccess()) {
                    log.debug("Received unsuccessful meta-data response " + reply);
                    /* Request failed */
                    MessageSourceResolvable message = 
                        YukonMessageSourceResolvable.createSingleCodeWithArguments(keyPrefix + "error", reply.getReplyType());
                    callback.receivedDataError(message);
                } else {
                    log.debug("Received successful meta-data response " + reply);
                    callback.receivedData(reply.getMetadata());
                }
            }

            @Override
            public void handleTimeout() {
                log.debug("Timeout requesting meta-data from NM for " + device);
                MessageSourceResolvable createSingleCodeWithArguments = 
                    YukonMessageSourceResolvable.createSingleCodeWithArguments(keyPrefix + "timeout");
                callback.receivedDataError(createSingleCodeWithArguments);
            }

            @Override
            public Class<RfnMetadataResponse> getExpectedType() {
                return RfnMetadataResponse.class;
            }

        };
        
        qrTemplate.send(new RfnMetadataRequest(device.getRfnIdentifier()), handler);
    }
    
    @PostConstruct
    public void initialize() {
        qrTemplate = new RequestReplyTemplateImpl<>("RFN_METADATA", configurationSource, connectionFactory,
            "yukon.qr.obj.common.rfn.MetadataRequest", false);
    }
    
}