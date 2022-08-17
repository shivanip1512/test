package com.cannontech.web.dev.service.impl;

import static com.cannontech.web.dev.model.RelayStateInjectionStatus.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.core.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.SimplePeriodFormat;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.web.dev.error.RelayStateInjectionException;
import com.cannontech.web.dev.model.RelayStateInjectionParams;
import com.cannontech.web.dev.model.RelayStateInjectionStatus;
import com.cannontech.web.dev.service.RelayStateInjectionService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

@Service
public class RelayStateInjectionServiceImpl implements RelayStateInjectionService {
    private static final Logger log = YukonLogManager.getLogger(RelayStateInjectionServiceImpl.class);
    private static final Multimap<PaoType,Attribute> supportedTypesAndRelays = MultimapBuilder.hashKeys()
                                                                                              .arrayListValues()
                                                                                              .build();  
    private PeriodFormatter periodFormatter = SimplePeriodFormat.getConfigPeriodFormatter();
    private RelayStateInjectionParams injectionParams = new RelayStateInjectionParams();
    private RelayStateInjectionStatus injectionStatus = NOT_RUN;
    
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired @Qualifier("main") private Executor executor;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    
    @PostConstruct
    public void init() {
        supportedTypesAndRelays.putAll(
                PaoType.LCR6600S, Lists.newArrayList(
                    BuiltInAttribute.RELAY_1_RELAY_STATE,
                    BuiltInAttribute.RELAY_2_RELAY_STATE,
                    BuiltInAttribute.RELAY_3_RELAY_STATE,
                    BuiltInAttribute.RELAY_4_RELAY_STATE
                )
        );
        
        supportedTypesAndRelays.putAll(
                PaoType.LCR6601S, Lists.newArrayList(
                    BuiltInAttribute.RELAY_1_RELAY_STATE,
                    BuiltInAttribute.RELAY_2_RELAY_STATE
                )
        );
    }
    
    @Override
    public synchronized int startInjection(RelayStateInjectionParams params) throws RelayStateInjectionException {
        if (injectionStatus == RUNNING || injectionStatus == CANCELING) {
            throw new RelayStateInjectionException("Cannot start injection. Task is still running.");
        }
        
        try {
            injectionStatus = RUNNING;
            injectionParams = params.copy();
            injectionParams.setInjectionStart(Instant.now());
            DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(injectionParams.getGroupName());
            Set<Integer> deviceIds = deviceGroupService.getDeviceIds(List.of(deviceGroup));
            int deviceCount = deviceIds.size();
            injectionParams.setDeviceCount(deviceCount);
            
            log.info("Found " + deviceCount + " devices in group " + deviceGroup);
            
            Runnable injectionThread = buildInjectionThread(deviceIds);
            executor.execute(injectionThread);
            
            return deviceCount;
        } catch (Exception e) {
            injectionStatus = COMPLETE;
            throw e;
        }
    }

    @Override
    public synchronized void stopInjection() throws RelayStateInjectionException {
        if (injectionStatus == NOT_RUN || injectionStatus == COMPLETE || injectionStatus == CANCELED) {
            throw new RelayStateInjectionException("Cannot stop injection. Task is not running.");
        }
        injectionStatus = RelayStateInjectionStatus.CANCELED;
    }

    @Override
    public RelayStateInjectionStatus getStatus() {
        return injectionStatus;
    }

    @Override
    public RelayStateInjectionParams getParams() {
        return injectionParams.copy();
    }
    
    private Runnable buildInjectionThread(Collection<Integer> deviceIds) {
        return new Runnable() {
            @Override
            public void run() {
                log.info("Starting relay state injection.");
                injectionParams.setInjectionStart(Instant.now());
                // Where the real work happens
                injectRelayStateData(deviceIds);
                log.info("Relay state injection complete.");
                injectionParams.setInjectionEnd(Instant.now());
                if (injectionStatus != CANCELED) {
                    injectionStatus = COMPLETE;
                }
            }
        };
    }
    
    private void injectRelayStateData(Collection<Integer> deviceIds) {
        log.info("Relay state injection parameters: {}", injectionParams);
        List<LiteYukonPAObject> devices = 
                serverDatabaseCache.getAllDevices()
                                   .stream()
                                   .filter(pao -> deviceIds.contains(pao.getPaoIdentifier().getPaoId()))
                                   .collect(Collectors.toList());
        
        for (LiteYukonPAObject device : devices) {
            try {
                //Check for cancelation
                if (injectionStatus == CANCELED) {
                    log.info("Canceling relay state data injection.");
                    break;
                }
                
                PaoType paoType = device.getPaoIdentifier().getPaoType();
                
                //Is device type supported? If not, skip injection and mark done
                if (isTypeSupported(paoType)) {
                    log.info("Inserting data for " + device.getPaoIdentifier());
                    //What relays does it support?
                    Collection<Attribute> relayAttributes = getSupportedRelayAttributes(paoType);
                    log.info("Identified " + relayAttributes.size() + " relay attributes.");
                    for (Attribute attribute : relayAttributes) {
                        // Get the point for this relay
                        LitePoint point = attributeService.getPointForAttribute(device, attribute);
                        
                        // Get the start time, which is either the specified start time or the time of the most recent
                        // data on that point
                        Instant startDate = getStartDate(point, 
                                                         injectionParams.isStartAfterLastReading(),
                                                         injectionParams.getStart());
                        
                        // Generate point data from start time to stop time, at the specified interval
                        List<PointData> pointData = generatePointDataForRange(point, 
                                                                              startDate, 
                                                                              injectionParams.getStop(), 
                                                                              injectionParams.getPeriod());
                        
                        // Ship all the point data for that attribute to dispatch
                        log.info("Inserting " + pointData.size() + " values for " + attribute);
                        sendPointData(pointData);
                    }
                    log.info("Insertion complete for device.");
                }
                //Increment completion count
                injectionParams.incrementDevicesComplete();
                log.info("Devices complete: " + injectionParams.getDevicesComplete());
            } catch (Exception e) {
                //Increment completion count
                injectionParams.incrementDevicesComplete();
                log.error("An error occurred injecting relay state data for device with ID: " + 
                          device.getPaoIdentifier().getPaoId(), e);
            }
        }
    }
    
    private boolean isTypeSupported(PaoType paoType) {
        return supportedTypesAndRelays.containsKey(paoType);
    }
    
    private Collection<Attribute> getSupportedRelayAttributes(PaoType paoType) {
        return supportedTypesAndRelays.get(paoType);
    }
    
    private Instant getStartDate(LitePoint point, boolean isStartAfterLastReading, Instant start) {
        Instant updatedStart = start;
        if (isStartAfterLastReading) {
            PointValueQualityHolder pointValue = asyncDynamicDataSource.getPointValue(point.getLiteID());
            Instant lastReadingTime = new Instant(pointValue.getPointDataTimeStamp());
            if (lastReadingTime.isAfter(updatedStart)) {
                updatedStart = lastReadingTime;
                log.info("Updated start time to last timestamp for point: " + updatedStart);
            } else {
                log.info("Start time is later than last timestamp for point (" + lastReadingTime + ").");
            }
            
        }
        log.info("Start time: " + updatedStart);
        return updatedStart;
    }
    
    private List<PointData> generatePointDataForRange(LitePoint point, Instant start, Instant stop, String periodString) {
        log.debug("Start: {}, Stop: {}", start, stop);
        Period period = periodFormatter.parsePeriod(periodString);
        int pointId = point.getLiteID();
        
        // Start one interval after the start time, with relay ON.
        Instant injectionTime = start.plus(period.toStandardDuration());
        RelayState state = RelayState.ON;
        
        List<PointData> dataList = new ArrayList<>();
        while (injectionTime.isBefore(stop)) {
            log.trace("Generating point data for " + injectionTime);
            
            // Generate a point data for the given point, state and time
            PointData data = generatePointData(pointId, state.getDbValue(), injectionTime);
            dataList.add(data);
            
            //set up the next point data by incrementing the time and switching the relay state
            injectionTime = injectionTime.plus(period.toStandardDuration());
            state = state.toggle();
        }
        
        // If the last data generated set the relay to ON, create one last point data right after the stop time to turn
        // it OFF again.
        if (state == RelayState.ON && !dataList.isEmpty()) {
            PointData data = generatePointData(pointId, 
                                               RelayState.OFF.getDbValue(), 
                                               stop.plus(Duration.standardSeconds(1)));
            dataList.add(data);
        }
        
        return dataList;
    }
    
    private PointData generatePointData(int pointId, double value, Instant time) {
        PointData pointData = new PointData();
        pointData.setId(pointId);
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setValue(value);
        pointData.setTime(time.toDate());
        pointData.setType(PointType.Status.getPointTypeId());
        pointData.setTagsPointMustArchive(true);
        return pointData;
    }
    
    private void sendPointData(Collection<PointData> pointData) {
        asyncDynamicDataSource.putValues(pointData);
    }
    
    private enum RelayState {
        HEAT(0),
        COOL(1),
        OFF(2),
        ON(3),
        ;
        
        private double dbValue;
        
        private RelayState(int dbValue) {
            this.dbValue = dbValue;
        }
        
        private double getDbValue() {
            return dbValue;
        }
        
        /**
         * Switch between ON/OFF values. Doesn't currently work for HEAT/COOL.
         */
        private RelayState toggle() {
            if (this == OFF) {
                return ON;
            } else if (this == ON) {
                return OFF;
            } else {
                throw new IllegalStateException("HEAT/COOL not supported");
            }
        }
    }
}
