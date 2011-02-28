package com.cannontech.amr.porterResponseMonitor.model;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.LazyLinkedHashMap;
import com.cannontech.database.data.lite.LiteStateGroup;

public class PorterResponseMonitorDto {
    private Integer monitorId;
    private String name;
    private String groupName;
    private LiteStateGroup stateGroup;
    private Attribute attribute;
    private MonitorEvaluatorStatus evaluatorStatus;
    private final Map<Integer, PorterResponseMonitorRuleDto> rules = 
        new LazyLinkedHashMap<Integer, PorterResponseMonitorRuleDto>(Integer.class, PorterResponseMonitorRuleDto.class);
    private static final AtomicInteger nextKey = new AtomicInteger();

    public PorterResponseMonitorDto() {
        groupName = SystemGroupEnum.ROOT.getFullPath();
        attribute = BuiltInAttribute.OUTAGE_STATUS;
        evaluatorStatus = MonitorEvaluatorStatus.ENABLED;
    }

    public PorterResponseMonitorDto(PorterResponseMonitor monitor) {
        monitorId = monitor.getMonitorId();
        name = monitor.getName();
        groupName = monitor.getGroupName();
        stateGroup = monitor.getStateGroup();
        attribute = monitor.getAttribute();
        evaluatorStatus = monitor.getEvaluatorStatus();
        for (PorterResponseMonitorRule rule : monitor.getRules()) {
            Integer nextMapKey = nextKey.getAndIncrement();
            PorterResponseMonitorRuleDto ruleDto = new PorterResponseMonitorRuleDto(rule);
            rules.put(nextMapKey, ruleDto);
        }
    }

    public Integer getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Integer monitorId) {
        this.monitorId = monitorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public LiteStateGroup getStateGroup() {
        return stateGroup;
    }

    public void setStateGroup(LiteStateGroup stateGroup) {
        this.stateGroup = stateGroup;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public MonitorEvaluatorStatus getEvaluatorStatus() {
        return evaluatorStatus;
    }

    public void setEvaluatorStatus(MonitorEvaluatorStatus evaluatorStatus) {
        this.evaluatorStatus = evaluatorStatus;
    }

    public Map<Integer, PorterResponseMonitorRuleDto> getRules() {
        return rules;
    }

    public static Integer getNextKey() {
        return nextKey.getAndIncrement();
    }
}
