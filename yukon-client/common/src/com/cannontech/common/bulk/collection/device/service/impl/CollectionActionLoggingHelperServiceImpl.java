package com.cannontech.common.bulk.collection.device.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.service.CollectionActionLoggingHelperService;
import com.cannontech.common.events.loggers.CommanderEventLogService;
import com.cannontech.common.events.loggers.DataStreamingEventLogService;
import com.cannontech.common.events.loggers.DemandResetEventLogService;
import com.cannontech.common.events.loggers.DeviceConfigEventLogService;
import com.cannontech.common.events.loggers.DisconnectEventLogService;
import com.cannontech.common.events.loggers.EndpointEventLogService;
import com.cannontech.common.events.loggers.MeterProgrammingEventLogService;
import com.cannontech.common.events.loggers.PointEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;

public class CollectionActionLoggingHelperServiceImpl implements CollectionActionLoggingHelperService {

    @Autowired private CommanderEventLogService commanderEventLogService;
    @Autowired private DemandResetEventLogService demandResetEventLogService;
    @Autowired private DeviceConfigEventLogService deviceConfigEventLogService;
    @Autowired private DisconnectEventLogService disconnectEventLogService;
    @Autowired private EndpointEventLogService endpointEventLogService;
    @Autowired private PointEventLogService pointEventLogService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DataStreamingEventLogService dataStreamingEventLogService;
    @Autowired private MeterProgrammingEventLogService meterProgrammingEventLogService;

    @Override
    public void log(CollectionActionResult result){
        switch (result.getAction()) {
        case SEND_COMMAND:
            logSendCommand(result);
            break;
        case READ_ATTRIBUTE:
            logReadAttribute(result);
            break;
        case LOCATE_ROUTE:
            logLocateRoute(result);
            break;
        case CONNECT:
        case DISCONNECT:
        case ARM:
            logDisconnect(result);
            break;
        case DEMAND_RESET:
            logDemandReset(result);
            break;
        case SEND_CONFIG:
            logSendConfig(result);
            break;
        case READ_CONFIG:
            logReadConfig(result);
            break;
        case VERIFY_CONFIG:
            logVerifyConfig(result);
            break;
        case MASS_CHANGE:
            logMassChange(result);
            break;
        case CHANGE_TYPE:
            logChangeType(result);
            break;
        case MASS_DELETE:
            logMassDelete(result);
            break;
        case ADD_POINTS:
            logAddPoints(result);
            break;
        case UPDATE_POINTS:
            logUpdatePoints(result);
            break;
        case REMOVE_POINTS:
            logRemovePoints(result);
            break;
        case ASSIGN_CONFIG:
            logAssignConfig(result);
            break;
        case UNASSIGN_CONFIG:
            logUnassignConfig(result);
            break;
        case CONFIGURE_DATA_STREAMING:
        case READ_DATA_STREAMING_CONFIG:
            logConfigDataStreaming(result);
            break;
        case REMOVE_DATA_STREAMING:
            logRemoveDataStreaming(result);
            break;
        case METER_PROGRAM_UPLOAD_INITIATE:
            logMeterProgramUpload(result);
            break;
        case METER_PROGRAM_STATUS_READ:
            logMeterProgramStatusRead(result);
            break;
        default:
            throw new UnsupportedOperationException("Add event logger for collection action="+result.getAction());
        }
    }

    @FunctionalInterface
    private interface LogInitiated {
        public void log(String action, String input, Integer numDevices, LiteYukonUser username, String resultKey);
    }
    
    @FunctionalInterface
    private interface LogCompleted {
        public void log(String action, String input, String statistics, String creStatus, String resultKey);
    }
    
    @FunctionalInterface
    private interface LogCancelled {
        public void log(String action, String input, String statistics, LiteYukonUser user, String resultKey);
    }
    
    private void log(LogInitiated logInitiated, CollectionActionResult result) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());
        
        logInitiated.log(accessor.getMessage(result.getAction()), 
                         result.getInputString(), 
                         result.getCounts().getTotalCount(), 
                         result.getContext().getYukonUser(), 
                         String.valueOf(result.getCacheKey()));
    }
    
    private void log(LogCompleted logCompleted, CollectionActionResult result) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());
        
        logCompleted.log(accessor.getMessage(result.getAction()),
                         result.getInputString(),
                         result.getResultStatsString(accessor), 
                         accessor.getMessage(result.getStatus()), 
                         String.valueOf(result.getCacheKey()));
    }
    
    private void log(LogCancelled logCancelled, CollectionActionResult result) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());
        
        logCancelled.log(accessor.getMessage(result.getAction()), 
                         result.getInputString(),
                         result.getResultStatsString(accessor), 
                         result.getContext().getYukonUser(),
                         String.valueOf(result.getCacheKey()));
    }
    
    private void logConfigDataStreaming(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(dataStreamingEventLogService::configDataStreamingInitiated, result);
            break;
        case COMPLETE:
            log(dataStreamingEventLogService::configDataStreamingCompleted, result);
            break;
        case CANCELLED:
            log(dataStreamingEventLogService::configDataStreamingCancelled, result);
            break;
        }
    }

    private void logRemoveDataStreaming(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(dataStreamingEventLogService::removeDataStreamingInitiated, result);
            break;
        case COMPLETE:
            log(dataStreamingEventLogService::removeDataStreamingCompleted, result);
            break;
        case CANCELLED:
            log(dataStreamingEventLogService::removeDataStreamingCancelled, result);
            break;
        }
    }

    private void logLocateRoute(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(commanderEventLogService::locateRouteInitiated, result);
            break;
        case COMPLETE:
            log(commanderEventLogService::locateRouteCompleted, result);
            break;
        case CANCELLED:
            log(commanderEventLogService::locateRouteCancelled, result);
            break;
        }
    }

    private void logDisconnect(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log((LogInitiated)disconnectEventLogService::disconnectInitiated, result);
            break;
        case COMPLETE:
            log(disconnectEventLogService::disconnectCompleted, result);
            break;
        case CANCELLED:
            log(disconnectEventLogService::disconnectCancelled, result);
            break;
        }
    }

    private void logDemandReset(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(demandResetEventLogService::demandResetInitiated, result);
            break;
        case COMPLETE:
            log(demandResetEventLogService::demandResetCompleted, result);
            break;
        case CANCELLED:
            log(demandResetEventLogService::demandResetCancelled, result);
            break;
        }
    }

    private void logSendConfig(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(deviceConfigEventLogService::sendConfigInitiated, result);
            break;
        case COMPLETE:
            log(deviceConfigEventLogService::sendConfigCompleted, result);
            break;
        case CANCELLED:
            log(deviceConfigEventLogService::sendConfigCancelled, result);
            break;
        }
    }

    private void logReadConfig(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(deviceConfigEventLogService::readConfigInitiated, result);
            break;
        case COMPLETE:
            log(deviceConfigEventLogService::readConfigCompleted, result);
            break;
        case CANCELLED:
            log(deviceConfigEventLogService::readConfigCancelled, result);
            break;
        }
    }

    private void logVerifyConfig(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(deviceConfigEventLogService::verifyConfigInitiated, result);
            break;
        case COMPLETE:
            log(deviceConfigEventLogService::verifyConfigCompleted, result);
            break;
        case CANCELLED:
            log(deviceConfigEventLogService::verifyConfigCancelled, result);
            break;
        }
    }

    private void logMassChange(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(endpointEventLogService::changeInitiated, result);
            break;
        case COMPLETE:
            log(endpointEventLogService::changeCompleted, result);
            break;
        case CANCELLED:
            log(endpointEventLogService::changeCancelled, result);
            break;
        }
    }

    private void logChangeType(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(endpointEventLogService::changeTypeInitiated, result);
            break;
        case COMPLETE:
            log(endpointEventLogService::changeTypeCompleted, result);
            break;
        case CANCELLED:
            log(endpointEventLogService::changeTypeCancelled, result);
            break;
        }
    }

    private void logMassDelete(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(endpointEventLogService::deleteInitiated, result);
            break;
        case COMPLETE:
            log(endpointEventLogService::deleteCompleted, result);
            break;
        case CANCELLED:
            log(endpointEventLogService::deleteCancelled, result);
            break;
        }
    }

    private void logAddPoints(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(pointEventLogService::pointsCreateInitiated, result);
            break;
        case COMPLETE:
            log(pointEventLogService::pointsCreateCompleted, result);
            break;
        case CANCELLED:
            log(pointEventLogService::pointsCreateCancelled, result);
            break;
        }
    }

    private void logUpdatePoints(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(pointEventLogService::pointsUpdateInitiated, result);
            break;
        case COMPLETE:
            log(pointEventLogService::pointsUpdateCompleted, result);
            break;
        case CANCELLED:
            log(pointEventLogService::pointsUpdateCancelled, result);
            break;
        }
    }

    private void logRemovePoints(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(pointEventLogService::pointsDeleteInitiated, result);
            break;
        case COMPLETE:
            log(pointEventLogService::pointsDeleteCompleted, result);
            break;
        case CANCELLED:
            log(pointEventLogService::pointsDeleteCancelled, result);
            break;
        }
    }

    private void logAssignConfig(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(deviceConfigEventLogService::assignConfigInitiated, result);
            break;
        case COMPLETE:
            log(deviceConfigEventLogService::assignConfigCompleted, result);
            break;
        }
    }

    private void logUnassignConfig(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(deviceConfigEventLogService::unassignConfigInitiated, result);
            break;
        case COMPLETE:
            log(deviceConfigEventLogService::unassignConfigCompleted, result);
            break;
        }
    }

    private void logReadAttribute(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(deviceConfigEventLogService::readConfigInitiated, result);
            break;
        case COMPLETE:
            log(deviceConfigEventLogService::readConfigCompleted, result);
            break;
        case CANCELLED:
            log(deviceConfigEventLogService::readConfigCancelled, result);
            break;
        }
    }

    private void logSendCommand(CollectionActionResult result){
        switch (result.getStatus()) {
        case STARTED:
            log(commanderEventLogService::groupCommandInitiated, result);
            break;
        case COMPLETE:
            log(commanderEventLogService::groupCommandCompleted, result);
            break;
        case CANCELLED:
            log(commanderEventLogService::groupCommandCancelled, result);
            break;
        }
    }
    
    private void logMeterProgramUpload(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(meterProgrammingEventLogService::meterProgramUploadInitiated, result);
            break;
        case COMPLETE:
            log(meterProgrammingEventLogService::meterProgramUploadCompleted, result);
            break;
        case CANCELLED:
            log(meterProgrammingEventLogService::meterProgramUploadCancelled, result);
            break;
        }
    }
    
    private void logMeterProgramStatusRead(CollectionActionResult result) {
        switch (result.getStatus()) {
        case STARTED:
            log(meterProgrammingEventLogService::meterProgramStatusReadInitiated, result);
            break;
        case COMPLETE:
            log(meterProgrammingEventLogService::meterProgramStatusReadCompleted, result);
            break;
        case CANCELLED:
            log(meterProgrammingEventLogService::meterProgramStatusReadCancelled, result);
            break;
        }
    }
}
