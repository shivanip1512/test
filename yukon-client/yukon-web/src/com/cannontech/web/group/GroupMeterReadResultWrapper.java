package com.cannontech.web.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.user.YukonUserContext;

public class GroupMeterReadResultWrapper {

	private GroupMeterReadResult result;
	
	public GroupMeterReadResultWrapper(GroupMeterReadResult groupMeterReadResult) {
		this.result = groupMeterReadResult;
	}
	
	public GroupMeterReadResult getResult() {
		return this.result;
	}
	
	public String getAttributesDescription(YukonUserContext userContext, ObjectFormattingService objectFormattingService) {

		Set<? extends Attribute> attributes = this.result.getAttributes();
		List<String> descriptions = new ArrayList<String>();
		String name;
		for (Attribute attribute : attributes) {
		    name = objectFormattingService.formatObjectAsString(attribute.getMessage(), userContext);
			descriptions.add(name);
		}
		
		return StringUtils.join(descriptions, ", ");
	}
}
