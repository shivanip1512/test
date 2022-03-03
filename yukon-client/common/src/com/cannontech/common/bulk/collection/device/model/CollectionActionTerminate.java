package com.cannontech.common.bulk.collection.device.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.user.YukonUserContext;

/**
 * Contains information needed to terminate collection on startup
 */
public class CollectionActionTerminate {
    private int cacheKey;
    private CollectionAction action;
    private CommandRequestExecution execution;
    private List<YukonPao> allDevices = new ArrayList<>();
    private Map<CollectionActionDetail, List<YukonPao>> details = new HashMap<>();
    private LinkedHashMap<String, String> inputs;
    private CommandRequestExecution verificationExecution;

    public CollectionActionTerminate(int cacheKey, CollectionAction action, List<? extends YukonPao> allDevices,
            LinkedHashMap<String, String> inputs, CommandRequestExecution execution) {        
        this.cacheKey = cacheKey;
        this.allDevices.addAll(allDevices);
        this.execution = execution;
        this.action = action;
        this.inputs = inputs;
        action.getDetails().forEach(detail -> {
            details.put(detail, new ArrayList<>());
        });
    }

    public CommandRequestExecution getExecution() {
        return execution;
    }

    public void addDevices(CollectionActionDetail detail, List<? extends YukonPao> devices) {
        details.get(detail).addAll(devices);
    }
    
    /**
     * Returns the list of devices that can be marked as canceled
     */
    public List<SimpleDevice> getCancelableDevices() {
        List<YukonPao> devices = new ArrayList<YukonPao>();
        devices.addAll(allDevices);
        details.values().forEach(values -> {
            devices.removeAll(values);
        });
        return devices.stream()
                .map(d -> new SimpleDevice(d))
                .collect(Collectors.toList());
    }

    public int getCacheKey() {
        return cacheKey;
    }

    public CollectionAction getAction() {
        return action;
    }
   
    public CollectionActionResult toCollectionActionResult() {
        return new CollectionActionResult() {
            @Override
            public YukonUserContext getContext() {
                return YukonUserContext.system;
            }

            @Override
            public CollectionAction getAction() {
                return action;
            }
            
            @Override
            public CommandRequestExecutionStatus getStatus() {
                return CommandRequestExecutionStatus.CANCELLED;
            }

            @Override
            public int getCacheKey() {
                return cacheKey;
            }

            @Override
            public String getInputString() {
                if (inputs == null) {
                    return "";
                }
                String retVal = "";
                for (String key : inputs.keySet()) {
                    retVal += key + ": " + inputs.get(key) + ",";
                }
                return StringUtils.isNotEmpty(retVal) ? StringUtils.abbreviate(retVal.substring(0, retVal.length() - 1),
                        2000) : "";
            }

            @Override
            public String getResultStatsString(MessageSourceAccessor accessor) {
                String retValue = "";
                for (CollectionActionDetail key : details.keySet()) {
                    retValue += accessor.getMessage(key) + ":" + details.get(key).size() + ",";
                }
                return StringUtils.isNotEmpty(retValue) ? retValue.substring(0, retValue.length() - 1) : "";
            }
        };
    }
    
    public CommandRequestExecution getVerificationExecution() {
        return verificationExecution;
    }

    public void setVerificationExecution(CommandRequestExecution verificationExecution) {
        this.verificationExecution = verificationExecution;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCacheKey());
        sb.append(" [");
        sb.append("Terminatable Action ");
        sb.append(getAction());
        sb.append("(");
        sb.append(getAction().getProcess());
        sb.append(") ");
        sb.append("Execution status:");
        sb.append(execution.getCommandRequestExecutionStatus());
        if (verificationExecution != null) {
            sb.append(" Verification Execution status:");
            sb.append(verificationExecution.getCommandRequestExecutionStatus());
        }
        sb.append(" Devices:");
        sb.append(allDevices.size());
        sb.append(" Cancelable Devices:");
        sb.append(getCancelableDevices().size());
        sb.append("]");
        return sb.toString();
    }
}
