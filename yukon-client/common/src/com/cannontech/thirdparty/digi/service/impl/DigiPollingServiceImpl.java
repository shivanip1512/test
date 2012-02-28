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
import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.WaitableExecutor;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.thirdparty.digi.exception.DigiNotConfiguredException;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.digi.model.FileData;
import com.cannontech.thirdparty.service.ZigbeeStateUpdaterService;

public class DigiPollingServiceImpl {
    private static final Logger log = YukonLogManager.getLogger(DigiPollingServiceImpl.class);

    private ZigbeeStateUpdaterService zigbeeStateUpdaterService;
    private RestOperations restTemplate;
    private DigiResponseHandler digiResponseHandler;
    private ScheduledExecutorService globalScheduledExecutor;
    private ConfigurationSource configurationSource;
    private ExecutorService fileReadThreadPool;
    
    private class DigiFileProcessor implements Runnable {

        public FileData xmlWithFileData;
        
        public DigiFileProcessor(FileData filePath) {
            this.xmlWithFileData = filePath;
        }
     
        @Override
        public void run() {
            try {
                digiResponseHandler.handleDeviceNotification(xmlWithFileData);
            } catch (UnsupportedDataTypeException e) {
                log.info(e.getMessage());
            }
        }
    }
    
    /**
     * Thread to initiate read on all Gateways that are currently in the commissioned state.
     * 
     */
    private Runnable digiDeviceNotificationPoll = new Runnable() {
        public void run() {
            log.debug("Digi Device Notification Started");

            //This contains the 'timestamp' to return all files uploaded before now.
            String url = DigiWebServiceImpl.getDigiBaseUrl() + "ws/FileData?condition=fdCreatedDate<'" 
                                                                             +  new Instant().toString(ISODateTimeFormat.dateTimeNoMillis())
                                                                             + "' and fdType='file'";

            try {
                //embed=true: embeds the file into the response coded in Base64. 
                //_recursive=yes: will traverse all lower folders instead of just the requested
                String recursiveFilelist = restTemplate.getForObject(url + "&embed=true&_recursive=yes", String.class);
                
                List<FileData> xmlDatas = digiResponseHandler.handleFolderListingResponse(recursiveFilelist);
                
                WaitableExecutor waitableExecutor = new WaitableExecutor(fileReadThreadPool);
                
                //DigiFileProcessor at one time was making its own HttpRequests and this was threaded to increase performance.
                //Leaving the threading in place to scale with many devices. This will keep backlogs from causing performanc
                for (FileData xmlWithFile : xmlDatas) {
                    waitableExecutor.execute(new DigiFileProcessor(xmlWithFile));
                }
                
                //This will use the same 'timestamp' to delete. So we don't delete anything new that got uploaded during this process.
                restTemplate.delete(url);
                
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
            globalScheduledExecutor.scheduleWithFixedDelay(digiDeviceStatusPoll, 5, duration.getStandardSeconds(), TimeUnit.SECONDS);
            log.info("Digi Device Status polling has been started.");
        } else {
            log.info("Digi Device Notification and Status Poll was not kicked off. DIGI_ENABLED was false");
        }
    }
    
    /**
     * Queries and updates the connection and commission status of each Device on iDigi account.
     * 
     */
    private Runnable digiDeviceStatusPoll = new Runnable() {

        @Override
        public void run() {
            log.debug("Digi Device Status Poll Started");

            try {
                zigbeeStateUpdaterService.updateAllGatewayStatuses();
                zigbeeStateUpdaterService.updateAllEndPointStatuses();
            } catch (DigiNotConfiguredException e) {
                log.warn("Digi not configured", e);
            } catch (DigiWebServiceException e) {
                log.error("Digi status update failed",e);
            }
            log.debug("Digi Device Status Poll Finished");
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
