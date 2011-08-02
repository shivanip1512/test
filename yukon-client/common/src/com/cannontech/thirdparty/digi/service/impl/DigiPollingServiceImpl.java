package com.cannontech.thirdparty.digi.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.service.ZigbeeStateUpdaterService;
import com.google.common.collect.Lists;

public class DigiPollingServiceImpl {
    private static final Logger log = YukonLogManager.getLogger(DigiPollingServiceImpl.class);

    private ZigbeeStateUpdaterService zigbeeStateUpdaterService;
    private RestOperations restTemplate;
    private DigiResponseHandler digiResponseHandler;
    private GatewayDeviceDao gatewayDeviceDao;
    private ScheduledExecutorService globalScheduledExecutor;
    private ConfigurationSource configurationSource;
    private AttributeDynamicDataSource aDynamicDataSource;

    private ExecutorService fileReadThreadPool;
    
    private class DigiGatewayFileReader implements Runnable {

        public ZigbeeDevice gateway;
        
        public DigiGatewayFileReader(ZigbeeDevice gateway) {
            this.gateway = gateway;
        }
     
        @Override
        public void run() {
            String zbDeviceId = DigiXMLBuilder.convertMacAddresstoDigi(gateway.getZigbeeMacAddress());
            String folderUrl = DigiWebServiceImpl.digiBaseUrl + "ws/data/~/" + zbDeviceId;
            
            
            String folderListResponse;
            try {
                //Get listing of files in folder
                folderListResponse = restTemplate.getForObject(folderUrl + "?recursive=no", String.class);
                
                //Parse out file names
                List<String> files = digiResponseHandler.handleFolderListingResponse(folderListResponse);
                
                log.debug("Processing " + files.size() + " files for gateway: " + gateway.getName());
                
                for (String fileName: files) {
                    try {
                        String deviceNotification;
                        try {
                            //Download File
                            deviceNotification = restTemplate.getForObject(folderUrl + "/" + fileName, String.class);
                        } catch (RestClientException e) {
                            throw new DigiWebServiceException(e);
                        }
                        
                        //Parse file for actions to take
                        digiResponseHandler.handleDeviceNotification(deviceNotification);
                    } catch (UnsupportedDataTypeException e) {
                        log.error(e.getMessage());
                    } finally {
                        //Delete the file, already processed.
                        restTemplate.delete(folderUrl + "/" + fileName);
                    }
                }
            } catch (RestClientException e) {
                throw new DigiWebServiceException(e);
            } catch (DigiWebServiceException e) {
                log.error("Exception while processing files on gateway with name, " + gateway.getName() + ":", e);
            }
            
            log.debug("Completed processing files for gateway: " + gateway.getName());
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
                List<Future<?>> futures = Lists.newArrayList();
   
                //Get commissioned gateways to Poll.
                List<ZigbeeDevice> gateways = gatewayDeviceDao.getAllGateways();
                
                //Grab current commissioned states
                Map<PaoIdentifier, PointValueHolder> currentStates = aDynamicDataSource.getPointValues(gateways, BuiltInAttribute.ZIGBEE_LINK_STATUS);
                
                for (ZigbeeDevice gateway : gateways) {
                    //Only query the gateway if it is commissioned.
                    PointValueHolder value = currentStates.get(gateway.getPaoIdentifier());
                    if (value.getValue() != Commissioned.DECOMMISSIONED.getRawState()) {
                        Future<?> future = fileReadThreadPool.submit(new DigiGatewayFileReader(gateway));
                        futures.add(future);
                    }
                }
            
                log.debug("Waiting for " + futures.size() + " gateways to process files.");
                
                try {
                    for (Future<?> future:futures) {
                        future.get();
                    }
                } catch (InterruptedException e) {
                    log.warn("caught exception in processAllNotificationsForGateway", e);
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    log.warn("caught exception in processAllNotificationsForGateway", e);
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
        } else {
            log.info("Digi Services not started. No URL configured in master.cfg");
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
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
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
    
    @Autowired
    public void setAttributeDynamicDataSource(AttributeDynamicDataSource aDynamicDataSource) {
        this.aDynamicDataSource = aDynamicDataSource;
    }
}
