package com.cannontech.web.dr;

import java.util.List;

import com.cannontech.messaging.message.loadcontrol.data.ControlAreaTriggerItem;
import com.google.common.collect.Lists;

public class TriggersDto {
	
	private ControlAreaTriggerItem trigger1 = new ControlAreaTriggerItem();
	private ControlAreaTriggerItem trigger2 = new ControlAreaTriggerItem();
	
	public ControlAreaTriggerItem getTrigger1() {
		return trigger1;
	}
	public void setTrigger1(ControlAreaTriggerItem trigger1) {
		this.trigger1 = trigger1;
	}
	public ControlAreaTriggerItem getTrigger2() {
		return trigger2;
	}
	public void setTrigger2(ControlAreaTriggerItem trigger2) {
		this.trigger2 = trigger2;
	}
	
	public List<ControlAreaTriggerItem> getTriggers() {
		
		List<ControlAreaTriggerItem> triggers = Lists.newArrayListWithCapacity(2);
		triggers.add(trigger1);
		triggers.add(trigger2);
		return triggers;
	}
}
