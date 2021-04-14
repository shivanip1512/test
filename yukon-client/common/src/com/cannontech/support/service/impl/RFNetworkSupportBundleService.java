package com.cannontech.support.service.impl;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.jms.JmsReplyReplyHandler;
import com.cannontech.common.util.jms.RequestReplyReplyTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.support.rfn.message.RfnSupportBundleRequest;
import com.cannontech.support.rfn.message.RfnSupportBundleResponse;
import com.cannontech.support.rfn.message.RfnSupportBundleResponseType;

public class RFNetworkSupportBundleService {

    private final static Logger log = YukonLogManager.getLogger(RFNetworkSupportBundleService.class);

    @Autowired private ConfigurationSource configurationSource;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    
    private RequestReplyReplyTemplate<RfnSupportBundleResponse, RfnSupportBundleResponse> template;
    private RfnSupportBundleResponseType responseStatus;
    
    @PostConstruct
    public void initialize() {
        YukonJmsTemplate jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RF_SUPPORT_BUNDLE);
        template = new RequestReplyReplyTemplate<>("RF_SUPPORT_BUNDLE", configurationSource, jmsTemplate); 
    }
    public void send(RfnSupportBundleRequest request) {
        JmsReplyReplyHandler<RfnSupportBundleResponse, RfnSupportBundleResponse> handler = new JmsReplyReplyHandler<>() {

            @Override
            public void handleException(Exception e) {
                log.info("Inside handleException." + e);
                responseStatus = RfnSupportBundleResponseType.FAILED;
            }

            @Override
            public void complete() {
                log.info("Inside complete.");
                responseStatus = RfnSupportBundleResponseType.COMPLETED;
            }

            @Override
            public void handleTimeout1() {
                log.info("Inside handleTimeout1.");
                responseStatus = RfnSupportBundleResponseType.TIMEOUT;
            }

            @Override
            public boolean handleReply1(RfnSupportBundleResponse t) {
                log.info("Inside handleReply1 " + t);
                responseStatus = t.getResponseType();
                return true;
            }

            @Override
            public Class<RfnSupportBundleResponse> getExpectedType1() {
                log.info("Inside getExpectedType1.");
                return RfnSupportBundleResponse.class;
            }

            @Override
            public void handleTimeout2() {
                log.info("Inside handleTimeout2.");
                responseStatus = RfnSupportBundleResponseType.TIMEOUT;
            }

            @Override
            public void handleReply2(RfnSupportBundleResponse t) {
                log.info("Inside handleReply2." + t.getResponseType());
                responseStatus = t.getResponseType();
            }

            @Override
            public Class<RfnSupportBundleResponse> getExpectedType2() {
                return RfnSupportBundleResponse.class;
            }};
        template.send(request, handler);
    }
    
    public RfnSupportBundleResponseType getStatus() {
        return responseStatus;
    }

}
