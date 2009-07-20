package com.cannontech.web.group;

import java.util.Date;

import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.BaseAlert;
import com.cannontech.common.util.ResolvableTemplate;


public class GroupMeterReadCompletionAlert extends BaseAlert {

	public GroupMeterReadCompletionAlert(Date date, ResolvableTemplate message) {
        super(date, message);
    }
	
	@Override
	public AlertType getType() {
		return AlertType.GROUP_METER_READ_COMPLETION;
	}

}