package com.cannontech.amr.disconnect.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.core.Logger;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.smartNotification.model.MeterDrEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.dr.meterDisconnect.DrMeterControlStatus;
import com.cannontech.dr.meterDisconnect.DrMeterEventStatus;
import com.cannontech.dr.meterDisconnect.service.DrMeterDisconnectStatusService;

/**
 * Callback to handle logging the disconnect status of disconnect meters in a DR event.
 */
public class DrDisconnectStatusCallback {
    private static final Logger log = YukonLogManager.getLogger(DrDisconnectStatusCallback.class);
    private final boolean isConnect;
    private final int eventId;
    private final DrMeterDisconnectStatusService drStatusService;
    private final SmartNotificationEventCreationService smartNotificationEventCreationService;
    private final String programName;
    
    public DrDisconnectStatusCallback(ControlOperation operation, int eventId, DrMeterDisconnectStatusService drStatusService,
            SmartNotificationEventCreationService notifService, String programName) {
        isConnect = operation.isConnect;
        this.eventId = eventId;
        this.drStatusService = drStatusService;
        smartNotificationEventCreationService = notifService;
        this.programName = programName;
    }
    
    /**
     * Log devices that Yukon attempted to control, but had no valid disconnect strategy. 
     */
    public void handleUnsupported(List<SimpleDevice> unsupportedDevices) {
        if (isConnect) {
            drStatusService.updateControlStatus(eventId, DrMeterControlStatus.RESTORE_FAILED_UNSUPPORTED, Instant.now(), 
                                                getDeviceIds(unsupportedDevices));
        } else {
            drStatusService.updateControlStatus(eventId, DrMeterControlStatus.CONTROL_FAILED_UNSUPPORTED, Instant.now(), 
                                                getDeviceIds(unsupportedDevices));
        }
    }
    
    /**
     * Log devices that Yukon attempted to control, but were not properly configured for disconnect.
     */
    public void handleNotConfigured(List<SimpleDevice> notConfiguredDevices) {
        if (isConnect) {
            drStatusService.updateControlStatus(eventId, DrMeterControlStatus.RESTORE_FAILED_NOT_CONFIGURED, Instant.now(), 
                                                getDeviceIds(notConfiguredDevices));
        } else {
            drStatusService.updateControlStatus(eventId, DrMeterControlStatus.CONTROL_FAILED_NOT_CONFIGURED, Instant.now(), 
                                                getDeviceIds(notConfiguredDevices));
        }
    }
    
    /**
     * Log the result of a connect or disconnect attempt for a device.
     */
    public void handleResult(CollectionActionDetail resultDetail, SimpleDevice device) {
        List<Integer> deviceIds = List.of(device.getDeviceId());
        if (isConnect) {
            handleConnectResult(resultDetail, deviceIds, Instant.now());
        } else {
            handleDisconnectResult(resultDetail, deviceIds, Instant.now());
        }
    }
    
    /**
     * Handle the completion of the connect or disconnect operation by logging a timeout for any devices that are still 
     * waiting for a response.
     */
    public void handleComplete() {
        if (isConnect) {
            drStatusService.updateAllControlTimeout(eventId);
        } else {
            drStatusService.updateAllRestoreTimeout(eventId);
        }
        
        // Collect the total counts of each status, for notification
        Map<String, Long> statusCounts = drStatusService.getAllCurrentStatusForEvent(eventId)
                                                        .stream()
                                                        .map(DrMeterEventStatus::getControlStatus)
                                                        .collect(Collectors.groupingBy(DrMeterControlStatus::name, 
                                                                                       Collectors.counting()));
        
        smartNotificationEventCreationService.send(SmartNotificationEventType.METER_DR,
            MeterDrEventAssembler.assemble(statusCounts, programName));
    }
    
    /**
     * Log the result of a connect attempt for a device.
     */
    private void handleConnectResult(CollectionActionDetail resultDetail, Collection<Integer> deviceIds, Instant now) {
        switch (resultDetail) {
            case ARMED:
                drStatusService.updateControlStatus(eventId, DrMeterControlStatus.FAILED_ARMED, now, deviceIds);
                break;
            case CONNECTED:
                // THIS IS THE SUCCESS CASE
                drStatusService.updateControlStatus(eventId, DrMeterControlStatus.RESTORE_CONFIRMED, now, deviceIds);
                break;
            case DISCONNECTED:
                drStatusService.updateControlStatus(eventId, DrMeterControlStatus.RESTORE_FAILED, now,  deviceIds);
                break;
            case FAILURE:
                drStatusService.updateControlStatus(eventId, DrMeterControlStatus.RESTORE_FAILED, now, deviceIds);
                break;
            //handle CANCELED, CONFIRMED, NOT_CONFIGURED, SUCCESS, UNCONFIRMED, UNSUPPORTED
            default:
                log.warn("Unexpected connect response: " + resultDetail);
                drStatusService.updateControlStatus(eventId, DrMeterControlStatus.RESTORE_UNKNOWN, now, deviceIds);
                break;
        }
    }
    
    /**
     * Log the result of a disconnect attempt for a device.
     */
    private void handleDisconnectResult(CollectionActionDetail resultDetail, Collection<Integer> deviceIds, Instant now) {
        switch (resultDetail) {
            case CONNECTED:
                drStatusService.updateControlStatus(eventId, DrMeterControlStatus.CONTROL_FAILED, now, deviceIds);
                break;
            case DISCONNECTED:
                // THIS IS THE SUCCESS CASE
                drStatusService.updateControlStatus(eventId, DrMeterControlStatus.CONTROL_CONFIRMED, now, deviceIds);
                break;
            case FAILURE:
                drStatusService.updateControlStatus(eventId, DrMeterControlStatus.CONTROL_FAILED, now, deviceIds);
                break;
            //handle CANCELED, CONFIRMED, NOT_CONFIGURED, SUCCESS, UNCONFIRMED, UNSUPPORTED
            default:
                log.warn("Unexpected disconnect response: " + resultDetail);
                drStatusService.updateControlStatus(eventId, DrMeterControlStatus.CONTROL_UNKNOWN, now, deviceIds);
                break;
        }
    }
    
    /**
     * Transform a collection of simple devices into a list of deviceIds.
     */
    private static List<Integer> getDeviceIds(Collection<SimpleDevice> devices) {
        return devices.stream()
                      .map(SimpleDevice::getDeviceId)
                      .collect(Collectors.toList());
    }
    
    public enum ControlOperation {
        CONTROL(false),
        RESTORE(true)
        ;
        
        public final boolean isConnect;
        
        private ControlOperation(boolean isConnect) {
            this.isConnect = isConnect;
        }
        
        public static ControlOperation of(boolean isConnect) {
            if (isConnect) {
                return RESTORE;
            }
            return CONTROL;
        }
    }
}
