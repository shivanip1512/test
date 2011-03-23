package com.cannontech.stars.dr.thirdparty.digi.service.impl;

import java.util.concurrent.TimeUnit;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;

import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.xml.SimpleXPathTemplate;

public class DigiPollingService {
    private static final Log logger = LogFactory.getLog(DigiPollingService.class);
    private RestOperations restTemplate;
    private ScheduledExecutor globalScheduledExecutor;
    
    private Runnable digiPoller = new Runnable() {
        public void run() {
            SimpleXPathTemplate template = new SimpleXPathTemplate();

            String source = restTemplate.getForObject("http://developer.idigi.com/ws/data/", String.class);
            //template.setContext(source);
            
            logger.info(source);
            
            processMessage(template);
            
        }
    };
    
    public void initialize() {
        //globalScheduledExecutor.schedule(digiPoller, 2000, TimeUnit.MILLISECONDS);
    }
    
    private void processMessage(SimpleXPathTemplate template) {
        
    }
    
    @Autowired
    public void setRestTemplate(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Autowired
    public void setGlobalScheduledExecutor(ScheduledExecutor globalScheduledExecutor) {
        this.globalScheduledExecutor = globalScheduledExecutor;
    }
}
