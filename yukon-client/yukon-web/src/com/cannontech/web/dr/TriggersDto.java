package com.cannontech.web.dr;

import java.util.List;

import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.google.common.collect.Lists;

public class TriggersDto {
	
	private LMControlAreaTrigger trigger1 = new LMControlAreaTrigger();
	private LMControlAreaTrigger trigger2 = new LMControlAreaTrigger();
	
	public LMControlAreaTrigger getTrigger1() {
		return trigger1;
	}
	public void setTrigger1(LMControlAreaTrigger trigger1) {
		this.trigger1 = trigger1;
	}
	public LMControlAreaTrigger getTrigger2() {
		return trigger2;
	}
	public void setTrigger2(LMControlAreaTrigger trigger2) {
		this.trigger2 = trigger2;
	}
	
	public List<LMControlAreaTrigger> getTriggers() {
		
		List<LMControlAreaTrigger> triggers = Lists.newArrayListWithCapacity(2);
		triggers.add(trigger1);
		triggers.add(trigger2);
		return triggers;
	}
}
