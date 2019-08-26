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

    private void logConfigDataStreaming(CollectionActionResult result) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            dataStreamingEventLogService.configDataStreamingInitiated(accessor.getMessage(result.getAction()), 
                                                                      result.getInputString(), 
                                                                      result.getCounts().getTotalCount(), 
                                                                      result.getContext().getYukonUser(), 
                                                                      String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            dataStreamingEventLogService.configDataStreamingCompleted(accessor.getMessage(result.getAction()),
                                                                      result.getInputString(),
                                                                      result.getResultStatsString(accessor), 
                                                                      accessor.getMessage(result.getStatus()), 
                                                                      String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            dataStreamingEventLogService.configDataStreamingCancelled(accessor.getMessage(result.getAction()), 
                                                                      result.getInputString(),
                                                                      result.getResultStatsString(accessor), 
                                                                      result.getContext().getYukonUser(),
                                                                      String.valueOf(result.getCacheKey()));
            break;
        }
    }

    private void logRemoveDataStreaming(CollectionActionResult result) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            dataStreamingEventLogService.removeDataStreamingInitiated(accessor.getMessage(result.getAction()), 
                                                                      result.getInputString(), 
                                                                      result.getCounts().getTotalCount(), 
                                                                      result.getContext().getYukonUser(), 
                                                                      String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            dataStreamingEventLogService.removeDataStreamingCompleted(accessor.getMessage(result.getAction()),
                                                                      result.getInputString(),
                                                                      result.getResultStatsString(accessor), 
                                                                      accessor.getMessage(result.getStatus()), 
                                                                      String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            dataStreamingEventLogService.removeDataStreamingCancelled(accessor.getMessage(result.getAction()), 
                                                                      result.getInputString(),
                                                                      result.getResultStatsString(accessor), 
                                                                      result.getContext().getYukonUser(),
                                                                      String.valueOf(result.getCacheKey()));
            break;
        }
    }

    private void logLocateRoute(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            commanderEventLogService.locateRouteInitiated(accessor.getMessage(result.getAction()), 
                                                          result.getInputString(), 
                                                          result.getCounts().getTotalCount(), 
                                                          result.getContext().getYukonUser(), 
                                                          String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            commanderEventLogService.locateRouteCompleted(accessor.getMessage(result.getAction()),
                                                          result.getInputString(),
                                                          result.getResultStatsString(accessor), 
                                                          accessor.getMessage(result.getStatus()), 
                                                          String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            commanderEventLogService.locateRouteCancelled(accessor.getMessage(result.getAction()), 
                                                          result.getInputString(),
                                                          result.getResultStatsString(accessor), 
                                                          result.getContext().getYukonUser(),
                                                          String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logDisconnect(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            disconnectEventLogService.disconnectInitiated(accessor.getMessage(result.getAction()), 
                                                          result.getInputString(), 
                                                          result.getCounts().getTotalCount(), 
                                                          result.getContext().getYukonUser(), 
                                                          String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            disconnectEventLogService.disconnectCompleted(accessor.getMessage(result.getAction()),
                                                          result.getInputString(),
                                                          result.getResultStatsString(accessor), 
                                                          accessor.getMessage(result.getStatus()), 
                                                          String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            disconnectEventLogService.disconnectCancelled(accessor.getMessage(result.getAction()), 
                                                          result.getInputString(),
                                                          result.getResultStatsString(accessor), 
                                                          result.getContext().getYukonUser(),
                                                          String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logDemandReset(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            demandResetEventLogService.demandResetInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getInputString(), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            demandResetEventLogService.demandResetCompleted(accessor.getMessage(result.getAction()), 
                                                            result.getInputString(), 
                                                            result.getResultStatsString(accessor),
                                                            accessor.getMessage(result.getStatus()), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            demandResetEventLogService.demandResetCancelled(accessor.getMessage(result.getAction()), 
                                                            result.getInputString(), 
                                                            result.getResultStatsString(accessor),
                                                            result.getContext().getYukonUser(),
                                                            String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logSendConfig(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            deviceConfigEventLogService.sendConfigInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getInputString(), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            deviceConfigEventLogService.sendConfigCompleted(accessor.getMessage(result.getAction()), 
                                                            result.getInputString(), 
                                                            result.getResultStatsString(accessor),
                                                            accessor.getMessage(result.getStatus()), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            deviceConfigEventLogService.sendConfigCancelled(accessor.getMessage(result.getAction()), 
                                                            result.getInputString(), 
                                                            result.getResultStatsString(accessor),
                                                            result.getContext().getYukonUser(),
                                                            String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logReadConfig(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            deviceConfigEventLogService.readConfigInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getInputString(), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            deviceConfigEventLogService.readConfigCompleted(accessor.getMessage(result.getAction()), 
                                                            result.getInputString(), 
                                                            result.getResultStatsString(accessor),
                                                            accessor.getMessage(result.getStatus()), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            deviceConfigEventLogService.readConfigCancelled(accessor.getMessage(result.getAction()), 
                                                            result.getInputString(), 
                                                            result.getResultStatsString(accessor),
                                                            result.getContext().getYukonUser(),
                                                            String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logVerifyConfig(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            deviceConfigEventLogService.verifyConfigInitiated(accessor.getMessage(result.getAction()), 
                                                              result.getInputString(), 
                                                              result.getCounts().getTotalCount(), 
                                                              result.getContext().getYukonUser(), 
                                                              String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            deviceConfigEventLogService.verifyConfigCompleted(accessor.getMessage(result.getAction()),
                                                              result.getInputString(),
                                                              result.getResultStatsString(accessor), 
                                                              accessor.getMessage(result.getStatus()), 
                                                              String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            deviceConfigEventLogService.verifyConfigCancelled(accessor.getMessage(result.getAction()), 
                                                              result.getInputString(),
                                                              result.getResultStatsString(accessor), 
                                                              result.getContext().getYukonUser(),
                                                              String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logMassChange(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            endpointEventLogService.changeInitiated(accessor.getMessage(result.getAction()), 
                                                    result.getInputString(), 
                                                    result.getCounts().getTotalCount(), 
                                                    result.getContext().getYukonUser(), 
                                                    String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            endpointEventLogService.changeCompleted(accessor.getMessage(result.getAction()),
                                                    result.getInputString(),
                                                    result.getResultStatsString(accessor), 
                                                    accessor.getMessage(result.getStatus()), 
                                                    String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            endpointEventLogService.changeCancelled(accessor.getMessage(result.getAction()), 
                                                    result.getInputString(),
                                                    result.getResultStatsString(accessor), 
                                                    result.getContext().getYukonUser(),
                                                    String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logChangeType(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            endpointEventLogService.changeTypeInitiated(accessor.getMessage(result.getAction()), 
                                                        result.getInputString(), 
                                                        result.getCounts().getTotalCount(), 
                                                        result.getContext().getYukonUser(), 
                                                        String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            endpointEventLogService.changeTypeCompleted(accessor.getMessage(result.getAction()),
                                                        result.getInputString(),
                                                        result.getResultStatsString(accessor), 
                                                        accessor.getMessage(result.getStatus()), 
                                                        String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            endpointEventLogService.changeTypeCancelled(accessor.getMessage(result.getAction()), 
                                                        result.getInputString(),
                                                        result.getResultStatsString(accessor), 
                                                        result.getContext().getYukonUser(),
                                                        String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logMassDelete(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            endpointEventLogService.deleteInitiated(accessor.getMessage(result.getAction()), 
                                                    result.getInputString(), 
                                                    result.getCounts().getTotalCount(), 
                                                    result.getContext().getYukonUser(), 
                                                    String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            endpointEventLogService.deleteCompleted(accessor.getMessage(result.getAction()),
                                                    result.getInputString(),
                                                    result.getResultStatsString(accessor), 
                                                    accessor.getMessage(result.getStatus()), 
                                                    String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            endpointEventLogService.deleteCancelled(accessor.getMessage(result.getAction()), 
                                                    result.getInputString(),
                                                    result.getResultStatsString(accessor), 
                                                    result.getContext().getYukonUser(),
                                                    String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logAddPoints(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            pointEventLogService.pointsCreateInitiated(accessor.getMessage(result.getAction()), 
                                                       result.getInputString(), 
                                                       result.getCounts().getTotalCount(), 
                                                       result.getContext().getYukonUser(), 
                                                       String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            pointEventLogService.pointsCreateCompleted(accessor.getMessage(result.getAction()),
                                                       result.getInputString(),
                                                       result.getResultStatsString(accessor), 
                                                       accessor.getMessage(result.getStatus()), 
                                                       String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            pointEventLogService.pointsCreateCancelled(accessor.getMessage(result.getAction()), 
                                                       result.getInputString(),
                                                       result.getResultStatsString(accessor), 
                                                       result.getContext().getYukonUser(),
                                                       String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logUpdatePoints(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            pointEventLogService.pointsUpdateInitiated(accessor.getMessage(result.getAction()), 
                                                       result.getInputString(), 
                                                       result.getCounts().getTotalCount(), 
                                                       result.getContext().getYukonUser(), 
                                                       String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            pointEventLogService.pointsUpdateCompleted(accessor.getMessage(result.getAction()),
                                                       result.getInputString(),
                                                       result.getResultStatsString(accessor), 
                                                       accessor.getMessage(result.getStatus()), 
                                                       String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            pointEventLogService.pointsUpdateCancelled(accessor.getMessage(result.getAction()), 
                                                       result.getInputString(),
                                                       result.getResultStatsString(accessor), 
                                                       result.getContext().getYukonUser(),
                                                       String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logRemovePoints(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            pointEventLogService.pointsDeleteInitiated(accessor.getMessage(result.getAction()), 
                                                       result.getInputString(), 
                                                       result.getCounts().getTotalCount(), 
                                                       result.getContext().getYukonUser(), 
                                                       String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            pointEventLogService.pointsDeleteCompleted(accessor.getMessage(result.getAction()),
                                                       result.getInputString(),
                                                       result.getResultStatsString(accessor), 
                                                       accessor.getMessage(result.getStatus()), 
                                                       String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            pointEventLogService.pointsDeleteCancelled(accessor.getMessage(result.getAction()), 
                                                       result.getInputString(),
                                                       result.getResultStatsString(accessor), 
                                                       result.getContext().getYukonUser(),
                                                       String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logAssignConfig(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            deviceConfigEventLogService.assignConfigInitiated(accessor.getMessage(result.getAction()), 
                                                              result.getInputString(), 
                                                              result.getCounts().getTotalCount(), 
                                                              result.getContext().getYukonUser(), 
                                                              String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            deviceConfigEventLogService.assignConfigCompleted(accessor.getMessage(result.getAction()),
                                                              result.getInputString(),
                                                              result.getResultStatsString(accessor), 
                                                              accessor.getMessage(result.getStatus()), 
                                                              String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logUnassignConfig(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            deviceConfigEventLogService.unassignConfigInitiated(accessor.getMessage(result.getAction()), 
                                                                result.getInputString(), 
                                                                result.getCounts().getTotalCount(), 
                                                                result.getContext().getYukonUser(), 
                                                                String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            deviceConfigEventLogService.unassignConfigCompleted(accessor.getMessage(result.getAction()),
                                                                result.getInputString(),
                                                                result.getResultStatsString(accessor), 
                                                                accessor.getMessage(result.getStatus()), 
                                                                String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logReadAttribute(CollectionActionResult result) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            deviceConfigEventLogService.readConfigInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getInputString(), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            deviceConfigEventLogService.readConfigCompleted(accessor.getMessage(result.getAction()), 
                                                            result.getInputString(), 
                                                            result.getResultStatsString(accessor),
                                                            accessor.getMessage(result.getStatus()), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            deviceConfigEventLogService.readConfigCancelled(accessor.getMessage(result.getAction()), 
                                                            result.getInputString(), 
                                                            result.getResultStatsString(accessor),
                                                            result.getContext().getYukonUser(),
                                                            String.valueOf(result.getCacheKey()));
            break;
        }

    }

    private void logSendCommand(CollectionActionResult result){

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
            commanderEventLogService.groupCommandInitiated(accessor.getMessage(result.getAction()), 
                                                           result.getInputString(), 
                                                           result.getCounts().getTotalCount(), 
                                                           result.getContext().getYukonUser(), 
                                                           String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
            commanderEventLogService.groupCommandCompleted(accessor.getMessage(result.getAction()),
                                                           result.getInputString(),
                                                           result.getResultStatsString(accessor), 
                                                           accessor.getMessage(result.getStatus()), 
                                                           String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
            commanderEventLogService.groupCommandCancelled(accessor.getMessage(result.getAction()), 
                                                           result.getInputString(),
                                                           result.getResultStatsString(accessor), 
                                                           result.getContext().getYukonUser(),
                                                           String.valueOf(result.getCacheKey()));
            break;
        }
    }
    
    private void logMeterProgramUpload(CollectionActionResult result) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
        	meterProgrammingEventLogService.meterProgramUploadInitiated(accessor.getMessage(result.getAction()), 
                                                                      result.getInputString(), 
                                                                      result.getCounts().getTotalCount(), 
                                                                      result.getContext().getYukonUser(), 
                                                                      String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
        	meterProgrammingEventLogService.meterProgramUploadCompleted(accessor.getMessage(result.getAction()),
                                                                      result.getInputString(),
                                                                      result.getResultStatsString(accessor), 
                                                                      accessor.getMessage(result.getStatus()), 
                                                                      String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
        	meterProgrammingEventLogService.meterProgramUploadCancelled(accessor.getMessage(result.getAction()), 
                                                                      result.getInputString(),
                                                                      result.getResultStatsString(accessor), 
                                                                      result.getContext().getYukonUser(),
                                                                      String.valueOf(result.getCacheKey()));
            break;
        }
    }
    
    private void logMeterProgramStatusRead(CollectionActionResult result) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

        switch (result.getStatus()) {
        case STARTED:
        	meterProgrammingEventLogService.meterProgramStatusReadInitiated(accessor.getMessage(result.getAction()), 
                                                                      result.getInputString(), 
                                                                      result.getCounts().getTotalCount(), 
                                                                      result.getContext().getYukonUser(), 
                                                                      String.valueOf(result.getCacheKey()));
            break;
        case COMPLETE:
        	meterProgrammingEventLogService.meterProgramStatusReadCompleted(accessor.getMessage(result.getAction()),
                                                                      result.getInputString(),
                                                                      result.getResultStatsString(accessor), 
                                                                      accessor.getMessage(result.getStatus()), 
                                                                      String.valueOf(result.getCacheKey()));
            break;
        case CANCELLED:
        	meterProgrammingEventLogService.meterProgramStatusReadCancelled(accessor.getMessage(result.getAction()), 
                                                                      result.getInputString(),
                                                                      result.getResultStatsString(accessor), 
                                                                      result.getContext().getYukonUser(),
                                                                      String.valueOf(result.getCacheKey()));
            break;
        }
    }
}
