package com.cannontech.web.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.common.pao.attribute.model.Attribute;

public class GroupMeterReadResultWrapper {

	private GroupMeterReadResult result;
	
	public GroupMeterReadResultWrapper(GroupMeterReadResult groupMeterReadResult) {
		this.result = groupMeterReadResult;
	}
	
	public GroupMeterReadResult getResult() {
		return this.result;
	}
	
	public String getAttributesDescription() {

		Set<? extends Attribute> attributes = this.result.getAttributes();
		List<String> descriptions = new ArrayList<String>();
		for (Attribute attribute : attributes) {
			descriptions.add(attribute.getDescription());
		}
		
		return StringUtils.join(descriptions, ", ");
	}
}
