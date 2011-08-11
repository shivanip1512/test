package com.cannontech.thirdparty.digi.service.impl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.activation.UnsupportedDataTypeException;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.WaitableExecutor;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.service.ZigbeeStateUpdaterService;

public class DigiPollingServiceImpl {
    private static final Logger log = YukonLogManager.getLogger(DigiPollingServiceImpl.class);

    private ZigbeeStateUpdaterService zigbeeStateUpdaterService;
    private RestOperations restTemplate;
    private DigiResponseHandler digiResponseHandler;
    private ScheduledExecutorService globalScheduledExecutor;
    private ConfigurationSource configurationSource;

    private ExecutorService fileReadThreadPool;
    
    private class DigiFileReader implements Runnable {

        public String filePath;
        
        public DigiFileReader(String filePath) {
            this.filePath = filePath;
        }
     
        @Override
        public void run() {
            log.debug("Starting processing of file: " + filePath);
            String url = DigiWebServiceImpl.getDigiBaseUrl() + "ws/data/~/" + filePath;

            try {                
                //Download File
                String deviceNotification = restTemplate.getForObject(url, String.class);

                //Parse file for actions to take
                digiResponseHandler.handleDeviceNotification(deviceNotification);
            } catch (UnsupportedDataTypeException e) {
                log.error(e.getMessage());
            } catch (RestClientException e) {
                throw new DigiWebServiceException("Exception while reading file at: " + filePath, e);
            }
            
            restTemplate.delete(url);
            
            log.debug("Completed processing file: " + url);
        }
    }
    
    /**
     * Thread to initiate read on all Gateways that are currently in the commissioned state..
     * 
     */
    private Runnable digiDeviceNotificationPoll = new Runnable() {
        public void run() {
            log.debug("Digi Device Notification Started");

            try {
                String recursiveFilelist = restTemplate.getForObject(DigiWebServiceImpl.getDigiBaseUrl() + "ws/data/~?_recursive=yes", String.class);
                
                List<String> filePaths = digiResponseHandler.handleFolderListingResponse(recursiveFilelist);
                
                WaitableExecutor waitableExecutor = new WaitableExecutor(fileReadThreadPool);
                
                for (String filePath : filePaths) {
                    waitableExecutor.execute(new DigiFileReader(filePath));
                }
                
                try {
                    waitableExecutor.await();
                } catch (InterruptedException e) {
                    log.warn("caught exception in digiDeviceNotificationPoll", e);
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    log.warn("caught exception in digiDeviceNotificationPoll", e);
                }
                
            } catch (DispatchNotConnectedException dE) {
                log.error("Dispatch is not connected. Will not Poll gateways for device notifications.", dE);
            } catch (Exception e) {
                log.error("Exception while Polling Device Notifications", e);
            }
            
            log.debug("Digi Device Notification Finished");
        }
    };
    
    @PostConstruct
    public void initialize() {
        boolean runDigi = configurationSource.getBoolean("DIGI_ENABLED", false);
        
        if (runDigi) {
            globalScheduledExecutor = Executors.newScheduledThreadPool(1);
            int threadPoolSize = configurationSource.getInteger("DIGI_NOTIFICATION_THREADPOOL_SIZE", 10);
            fileReadThreadPool =  Executors.newFixedThreadPool(threadPoolSize);

            Duration duration = configurationSource.getDuration("DIGI_TIME_BETWEEN_READS", Duration.standardSeconds(600));
            globalScheduledExecutor.scheduleWithFixedDelay(digiDeviceNotificationPoll, 5, duration.getStandardSeconds(), TimeUnit.SECONDS);
            log.info("Digi Device Notification polling has been started.");
            
            duration = configurationSource.getDuration("DIGI_TIME_REFRESH_STATUS", Duration.standardHours(24));
            globalScheduledExecutor.scheduleWithFixedDelay(digiGatewayStatusPoll, 5, duration.getStandardSeconds(), TimeUnit.SECONDS);
            log.info("Digi Gateway Status polling has been started.");
        } else {
            log.info("Digi Device Notification and Gateway Status Poll was not kicked off. DIGI_ENABLED was false");
        }
    }
    
    /**
     * Queries and updates the connection and commission status of each Gateway on iDigi account.
     * 
     */
    private Runnable digiGatewayStatusPoll = new Runnable() {

        @Override
        public void run() {
            log.debug("Digi Gateway Status Poll Started");

            try {
                zigbeeStateUpdaterService.updateAllGatewayStatuses();
            } catch (Exception e) {
                log.error("Exception in Digi Gateway Status Poll", e);
            }
            log.debug("Digi Gateway Status Poll Finished");
        }
    };
    
    @Autowired
    public void setZigbeeStateUpdater(ZigbeeStateUpdaterService zigbeeStateUpdater) {
        this.zigbeeStateUpdaterService = zigbeeStateUpdater;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
    @Autowired
    public void setRestTemplate(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Autowired
    public void setDigiResponseHandler(DigiResponseHandler digiResponseHandler) {
        this.digiResponseHandler = digiResponseHandler;
    }
}
