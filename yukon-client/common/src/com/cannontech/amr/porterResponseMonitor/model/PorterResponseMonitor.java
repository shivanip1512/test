package com.cannontech.amr.porterResponseMonitor.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class PorterResponseMonitor {
	private Integer monitorId;
	private String name;
	private String groupName;
	private LiteStateGroup stateGroup;
	private Attribute attribute;
	private MonitorEvaluatorStatus evaluatorStatus;
	private List<PorterResponseMonitorRule> rules = Lists.newArrayList();

	public PorterResponseMonitor() {
        groupName = SystemGroupEnum.ROOT.getFullPath();
	    attribute = BuiltInAttribute.FAULT_STATUS; //this should be changed to the new Attribute. yet to be implemented
	    evaluatorStatus = MonitorEvaluatorStatus.ENABLED;
	}

	public PorterResponseMonitor(PorterResponseMonitorDto monitorDto) {
	    monitorId = monitorDto.getMonitorId();
	    name = monitorDto.getName();
	    groupName = monitorDto.getGroupName();
	    stateGroup = monitorDto.getStateGroup();
	    attribute = monitorDto.getAttribute();
	    evaluatorStatus = monitorDto.getEvaluatorStatus();
	    Collection<PorterResponseMonitorRuleDto> values = monitorDto.getRules().values();
	    // sort those values
	    List<PorterResponseMonitorRuleDto> sortedCopy = Ordering.natural().sortedCopy(values);
	    int order = 0;
        for (PorterResponseMonitorRuleDto ruleDto : sortedCopy) {
	        PorterResponseMonitorRule rule = new PorterResponseMonitorRule(ruleDto);
            rule.setRuleOrder(order++);
	        rules.add(rule);
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

    public List<PorterResponseMonitorRule> getRules() {
        Collections.sort(rules);
        return rules;
    }

    public void setRules(List<PorterResponseMonitorRule> rules) {
        this.rules = rules;
    }

    @Override
    public String toString() {
        return String.format("PorterResponseMonitor [monitorId=%s, name=%s, groupName=%s, stateGroup=%s, attribute=%s, evaluatorStatus=%s]",
                    monitorId,
                    name,
                    groupName,
                    stateGroup,
                    attribute,
                    evaluatorStatus);
    }
}
