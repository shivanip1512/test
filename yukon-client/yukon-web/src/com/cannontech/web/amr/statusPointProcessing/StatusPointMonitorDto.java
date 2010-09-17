package com.cannontech.web.amr.statusPointProcessing;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitorMessageProcessor;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitor;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.LazyList;
import com.cannontech.common.util.SimpleSupplier;
import com.cannontech.database.data.lite.LiteStateGroup;

public class StatusPointMonitorDto {
    private Integer statusPointMonitorId;
    private String statusPointMonitorName;
    private String deviceGroupName;
    private Attribute attribute;
    private LiteStateGroup stateGroup;
    private String evaluatorStatus;
    private List<StatusPointMonitorMessageProcessor> messageProcessors = new LazyList<StatusPointMonitorMessageProcessor>(new ArrayList<StatusPointMonitorMessageProcessor>(), 
                                                                new SimpleSupplier<StatusPointMonitorMessageProcessor>(StatusPointMonitorMessageProcessor.class));
    
    public void setStatusPointMonitorDto(StatusPointMonitor monitor) {
        statusPointMonitorId = monitor.getStatusPointMonitorId();
        statusPointMonitorName = monitor.getStatusPointMonitorName();
        deviceGroupName = monitor.getGroupName();
        attribute = monitor.getAttribute();
        stateGroup = monitor.getStateGroup();
        evaluatorStatus = monitor.getEvaluatorStatus().getDescription().toUpperCase();
        messageProcessors = monitor.getStatusPointMonitorMessageProcessors();
    }
    
    public StatusPointMonitor getStatusPointMonitor() {
        StatusPointMonitor statusPointMonitor = new StatusPointMonitor();
        statusPointMonitor.setStatusPointMonitorId(statusPointMonitorId);
        statusPointMonitor.setStatusPointMonitorName(statusPointMonitorName);
        
        statusPointMonitor.setStateGroup(stateGroup);
        
        //if these below fields are null, then the monitor was just created... so give them a default value
        
        if( deviceGroupName == null ) {
            deviceGroupName = SystemGroupEnum.ROOT.getFullPath();
        }
        statusPointMonitor.setGroupName(deviceGroupName);
        
        if ( attribute == null ) {
            attribute = (Attribute)BuiltInAttribute.FAULT_STATUS;
        }
        statusPointMonitor.setAttribute(attribute);
        
        if( evaluatorStatus == null ) {
            evaluatorStatus = MonitorEvaluatorStatus.ENABLED.getDescription().toUpperCase();
        }
        statusPointMonitor.setEvaluatorStatus(MonitorEvaluatorStatus.valueOf(evaluatorStatus));
        
        if( messageProcessors == null ) {
            messageProcessors = new ArrayList<StatusPointMonitorMessageProcessor>();
        }
        statusPointMonitor.setStatusPointMonitorMessageProcessors(messageProcessors);
        
        return statusPointMonitor;
    }
    
    public Integer getStatusPointMonitorId() {
        return statusPointMonitorId;
    }
    
    public void setStatusPointMonitorId(Integer statusPointMonitorId) {
        this.statusPointMonitorId = statusPointMonitorId;
    }
    
    public String getStatusPointMonitorName() {
        return statusPointMonitorName;
    }
    
    public void setStatusPointMonitorName(String name) {
        this.statusPointMonitorName = name;
    }
    
    public String getDeviceGroupName() {
        return deviceGroupName;
    }
    
    public void setDeviceGroupName(String deviceGroupName) {
        this.deviceGroupName = deviceGroupName;
    }
    
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
    
    public Attribute getAttribute() {
        return attribute;
    }
    
    public void setStateGroup(LiteStateGroup stateGroup) {
        this.stateGroup = stateGroup;
    }
    
    public LiteStateGroup getStateGroup() {
        return stateGroup;
    }
    
    public String getEvaluatorStatus() {
        return evaluatorStatus;
    }
    
    public void setEvaluatorStatus(String evaluatorStatus) {
        this.evaluatorStatus = evaluatorStatus;
    }

    public void setMessageProcessors(List<StatusPointMonitorMessageProcessor> messageProcessors) {
        this.messageProcessors = messageProcessors;
    }

    public List<StatusPointMonitorMessageProcessor> getMessageProcessors() {
        return messageProcessors;
    }
}