package com.cannontech.common.bulk.collection.device.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.service.CollectionActionLoggingHelperService;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.events.loggers.CommanderEventLogService;
import com.cannontech.common.events.loggers.DemandResetEventLogService;
import com.cannontech.common.events.loggers.DeviceConfigEventLogService;
import com.cannontech.common.events.loggers.DisconnectEventLogService;
import com.cannontech.common.events.loggers.EndpointEventLogService;
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

    @Override
    public void logActionInitiated(CollectionActionResult result) throws ClassNotFoundException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());
        switch (result.getAction()) {
        case SEND_COMMAND:
            commanderEventLogService.groupCommandInitiated(accessor.getMessage(result.getAction()), 
                                                           result.getDetailsString(accessor), 
                                                           result.getCounts().getTotalCount(), 
                                                           result.getContext().getYukonUser(), 
                                                           String.valueOf(result.getCacheKey()));
            break;
        case READ_ATTRIBUTE:
            commanderEventLogService.attributeReadInitiated(accessor.getMessage(result.getAction()), 
                                                           result.getDetailsString(accessor), 
                                                           result.getCounts().getTotalCount(), 
                                                           result.getContext().getYukonUser(), 
                                                           String.valueOf(result.getCacheKey()));
            break;
        case LOCATE_ROUTE:
            commanderEventLogService.locateRouteInitiated(accessor.getMessage(result.getAction()), 
                                                          result.getDetailsString(accessor), 
                                                          result.getCounts().getTotalCount(), 
                                                          result.getContext().getYukonUser(), 
                                                          String.valueOf(result.getCacheKey()));
        case CONNECT:
        case DISCONNECT:
        case ARM:
            disconnectEventLogService.disconnectInitiated(accessor.getMessage(result.getAction()), 
                                                          result.getDetailsString(accessor), 
                                                          result.getCounts().getTotalCount(), 
                                                          result.getContext().getYukonUser(), 
                                                          String.valueOf(result.getCacheKey()));
            break;
        case DEMAND_RESET:
            demandResetEventLogService.demandResetInitiated(accessor.getMessage(result.getAction()), 
                                                           result.getDetailsString(accessor), 
                                                           result.getCounts().getTotalCount(), 
                                                           result.getContext().getYukonUser(), 
                                                           String.valueOf(result.getCacheKey()));
            break;
        case SEND_CONFIG:
            deviceConfigEventLogService.sendConfigInitiated(accessor.getMessage(result.getAction()), 
                                                           result.getDetailsString(accessor), 
                                                           result.getCounts().getTotalCount(), 
                                                           result.getContext().getYukonUser(), 
                                                           String.valueOf(result.getCacheKey()));
            break;
        case READ_CONFIG:
            deviceConfigEventLogService.readConfigInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getDetailsString(accessor), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case VERIFY_CONFIG:
            deviceConfigEventLogService.verifyConfigInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getDetailsString(accessor), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case MASS_CHANGE:
            endpointEventLogService.changeInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getDetailsString(accessor), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case CHANGE_TYPE:
            endpointEventLogService.changeTypeInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getDetailsString(accessor), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case MASS_DELETE:
            endpointEventLogService.deleteInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getDetailsString(accessor), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case ADD_POINTS:
            pointEventLogService.pointsCreateInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getDetailsString(accessor), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case UPDATE_POINTS:
            pointEventLogService.pointsUpdateInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getDetailsString(accessor), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case REMOVE_POINTS:
            pointEventLogService.pointsDeleteInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getDetailsString(accessor), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case ASSIGN_CONFIG:
            deviceConfigEventLogService.assignConfigInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getDetailsString(accessor), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        case UNASSIGN_CONFIG:
            deviceConfigEventLogService.unassignConfigInitiated(accessor.getMessage(result.getAction()), 
                                                            result.getDetailsString(accessor), 
                                                            result.getCounts().getTotalCount(), 
                                                            result.getContext().getYukonUser(), 
                                                            String.valueOf(result.getCacheKey()));
            break;
        default:
            throw new ClassNotFoundException();
        }
    }
    
    @Override
    public void logActionCompletedCanceled(CollectionActionResult result) throws ClassNotFoundException {
//        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());
//        if (result.getStatus() == CommandRequestExecutionStatus.CANCELLED) {
//            switch (result.getAction()) {
//            case SEND_COMMAND:
//                commanderEventLogService.groupCommandCancelled(accessor.getMessage(result.getAction()), 
//                                                               result.getDetailsString(accessor), 
//                                                               String.valueOf(result.getCacheKey()), 
//                                                               result.getContext().getYukonUser());
//                break;
//            default:
//                throw new ClassNotFoundException();
//            }
//        }
//        else if (result.getStatus() == CommandRequestExecutionStatus.COMPLETE) {
//            switch (result.getAction()) {
//            case SEND_COMMAND:
//                commanderEventLogService.groupCommandCompleted(accessor.getMessage(result.getAction()), 
//                                                               result.getDetailsString(accessor), 
//                                                               accessor.getMessage(result.getStatus()), 
//                                                               String.valueOf(result.getCacheKey()));
//                break;
//            default:
//                throw new ClassNotFoundException();
//            }
//        }

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());
        if (result.getStatus() == CommandRequestExecutionStatus.CANCELLED) {
            switch (result.getAction()) {
            case SEND_COMMAND:
                commanderEventLogService.groupCommandCancelled(accessor.getMessage(result.getAction()), 
                                                             result.getDetailsString(accessor), 
                                                             result.getContext().getYukonUser(),
                                                             String.valueOf(result.getCacheKey()));
                break;
            case READ_ATTRIBUTE:
                commanderEventLogService.attributeReadCancelled(accessor.getMessage(result.getAction()), 
                                                                result.getDetailsString(accessor), 
                                                                result.getContext().getYukonUser(),
                                                                String.valueOf(result.getCacheKey()));
                break;
            case LOCATE_ROUTE:
                commanderEventLogService.locateRouteCancelled(accessor.getMessage(result.getAction()), 
                                                              result.getDetailsString(accessor), 
                                                              result.getContext().getYukonUser(),
                                                              String.valueOf(result.getCacheKey()));
            case CONNECT:
            case DISCONNECT:
            case ARM:
                disconnectEventLogService.disconnectCancelled(accessor.getMessage(result.getAction()), 
                                                              result.getDetailsString(accessor), 
                                                              result.getContext().getYukonUser(),
                                                              String.valueOf(result.getCacheKey()));
                break;
            case DEMAND_RESET:
                demandResetEventLogService.demandResetCancelled(accessor.getMessage(result.getAction()), 
                                                                result.getDetailsString(accessor), 
                                                                result.getContext().getYukonUser(),
                                                                String.valueOf(result.getCacheKey()));
                break;
            case SEND_CONFIG:
                deviceConfigEventLogService.sendConfigCancelled(accessor.getMessage(result.getAction()), 
                                                                result.getDetailsString(accessor), 
                                                                result.getContext().getYukonUser(),
                                                                String.valueOf(result.getCacheKey()));
                break;
            case READ_CONFIG:
                deviceConfigEventLogService.readConfigCancelled(accessor.getMessage(result.getAction()), 
                                                                result.getDetailsString(accessor), 
                                                                result.getContext().getYukonUser(),
                                                                String.valueOf(result.getCacheKey()));
                break;
            case VERIFY_CONFIG:
                deviceConfigEventLogService.verifyConfigCancelled(accessor.getMessage(result.getAction()), 
                                                                  result.getDetailsString(accessor), 
                                                                  result.getContext().getYukonUser(),
                                                                  String.valueOf(result.getCacheKey()));
                break;
            case MASS_CHANGE:
                endpointEventLogService.changeCancelled(accessor.getMessage(result.getAction()), 
                                                        result.getDetailsString(accessor), 
                                                        result.getContext().getYukonUser(),
                                                        String.valueOf(result.getCacheKey()));
                break;
            case CHANGE_TYPE:
                endpointEventLogService.changeTypeCancelled(accessor.getMessage(result.getAction()), 
                                                            result.getDetailsString(accessor), 
                                                            result.getContext().getYukonUser(),
                                                            String.valueOf(result.getCacheKey()));
                break;
            case MASS_DELETE:
                endpointEventLogService.deleteCancelled(accessor.getMessage(result.getAction()), 
                                                        result.getDetailsString(accessor), 
                                                        result.getContext().getYukonUser(),
                                                        String.valueOf(result.getCacheKey()));
                break;
            case ADD_POINTS:
                pointEventLogService.pointsCreateCancelled(accessor.getMessage(result.getAction()), 
                                                           result.getDetailsString(accessor), 
                                                           result.getContext().getYukonUser(),
                                                           String.valueOf(result.getCacheKey()));
                break;
            case UPDATE_POINTS:
                pointEventLogService.pointsUpdateCancelled(accessor.getMessage(result.getAction()), 
                                                           result.getDetailsString(accessor), 
                                                           result.getContext().getYukonUser(),
                                                           String.valueOf(result.getCacheKey()));
                break;
            case REMOVE_POINTS:
                pointEventLogService.pointsDeleteCancelled(accessor.getMessage(result.getAction()), 
                                                           result.getDetailsString(accessor), 
                                                           result.getContext().getYukonUser(),
                                                           String.valueOf(result.getCacheKey()));
                break;
            case ASSIGN_CONFIG:
            case UNASSIGN_CONFIG:
                break;
            default:
                throw new ClassNotFoundException();
            }
        }
        else if (result.getStatus() == CommandRequestExecutionStatus.COMPLETE) {
            switch (result.getAction()) {
            case SEND_COMMAND:
                commanderEventLogService.groupCommandCompleted(accessor.getMessage(result.getAction()), 
                                                             result.getDetailsString(accessor), 
                                                             accessor.getMessage(result.getStatus()), 
                                                             String.valueOf(result.getCacheKey()));
                break;
            case READ_ATTRIBUTE:
                commanderEventLogService.attributeReadCompleted(accessor.getMessage(result.getAction()), 
                                                                result.getDetailsString(accessor), 
                                                                accessor.getMessage(result.getStatus()), 
                                                                String.valueOf(result.getCacheKey()));
                break;
            case LOCATE_ROUTE:
                commanderEventLogService.locateRouteCompleted(accessor.getMessage(result.getAction()), 
                                                              result.getDetailsString(accessor), 
                                                              accessor.getMessage(result.getStatus()), 
                                                              String.valueOf(result.getCacheKey()));
            case CONNECT:
            case DISCONNECT:
            case ARM:
                disconnectEventLogService.disconnectCompleted(accessor.getMessage(result.getAction()), 
                                                              result.getDetailsString(accessor), 
                                                              accessor.getMessage(result.getStatus()), 
                                                              String.valueOf(result.getCacheKey()));
                break;
            case DEMAND_RESET:
                demandResetEventLogService.demandResetCompleted(accessor.getMessage(result.getAction()), 
                                                                result.getDetailsString(accessor), 
                                                                accessor.getMessage(result.getStatus()), 
                                                                String.valueOf(result.getCacheKey()));
                break;
            case SEND_CONFIG:
                deviceConfigEventLogService.sendConfigCompleted(accessor.getMessage(result.getAction()), 
                                                                result.getDetailsString(accessor), 
                                                                accessor.getMessage(result.getStatus()), 
                                                                String.valueOf(result.getCacheKey()));
            case READ_CONFIG:
                deviceConfigEventLogService.readConfigCompleted(accessor.getMessage(result.getAction()), 
                                                                result.getDetailsString(accessor), 
                                                                accessor.getMessage(result.getStatus()), 
                                                                String.valueOf(result.getCacheKey()));
                break;
            case VERIFY_CONFIG:
                deviceConfigEventLogService.verifyConfigCompleted(accessor.getMessage(result.getAction()), 
                                                                  result.getDetailsString(accessor), 
                                                                  accessor.getMessage(result.getStatus()), 
                                                                  String.valueOf(result.getCacheKey()));
                break;
            case MASS_CHANGE:
                endpointEventLogService.changeCompleted(accessor.getMessage(result.getAction()), 
                                                        result.getDetailsString(accessor), 
                                                        accessor.getMessage(result.getStatus()), 
                                                        String.valueOf(result.getCacheKey()));
                break;
            case CHANGE_TYPE:
                endpointEventLogService.changeTypeCompleted(accessor.getMessage(result.getAction()), 
                                                            result.getDetailsString(accessor), 
                                                            accessor.getMessage(result.getStatus()), 
                                                            String.valueOf(result.getCacheKey()));
                break;
            case MASS_DELETE:
                endpointEventLogService.deleteCompleted(accessor.getMessage(result.getAction()), 
                                                        result.getDetailsString(accessor), 
                                                        accessor.getMessage(result.getStatus()), 
                                                        String.valueOf(result.getCacheKey()));
                break;
            case ADD_POINTS:
                pointEventLogService.pointsCreateCompleted(accessor.getMessage(result.getAction()), 
                                                           result.getDetailsString(accessor), 
                                                           accessor.getMessage(result.getStatus()), 
                                                           String.valueOf(result.getCacheKey()));
                break;
            case UPDATE_POINTS:
                pointEventLogService.pointsUpdateCompleted(accessor.getMessage(result.getAction()), 
                                                           result.getDetailsString(accessor), 
                                                           accessor.getMessage(result.getStatus()), 
                                                           String.valueOf(result.getCacheKey()));
                break;
            case REMOVE_POINTS:
                pointEventLogService.pointsDeleteCompleted(accessor.getMessage(result.getAction()), 
                                                           result.getDetailsString(accessor), 
                                                           accessor.getMessage(result.getStatus()), 
                                                           String.valueOf(result.getCacheKey()));
                break;
            case ASSIGN_CONFIG:
                deviceConfigEventLogService.assignConfigCompleted(accessor.getMessage(result.getAction()), 
                                                                  result.getDetailsString(accessor), 
                                                                  accessor.getMessage(result.getStatus()), 
                                                                  String.valueOf(result.getCacheKey()));
                break;
            case UNASSIGN_CONFIG:
                deviceConfigEventLogService.unassignConfigCompleted(accessor.getMessage(result.getAction()), 
                                                                    result.getDetailsString(accessor), 
                                                                    accessor.getMessage(result.getStatus()), 
                                                                    String.valueOf(result.getCacheKey()));
                break;
            default:
                throw new ClassNotFoundException();
            }
        }
    
    }
}
