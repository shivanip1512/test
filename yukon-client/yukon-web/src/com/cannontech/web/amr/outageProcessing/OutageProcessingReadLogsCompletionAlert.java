package com.cannontech.web.amr.outageProcessing;

import java.util.Date;

import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.BaseAlert;
import com.cannontech.common.util.ResolvableTemplate;

public class OutageProcessingReadLogsCompletionAlert extends BaseAlert {

	public OutageProcessingReadLogsCompletionAlert(Date date, ResolvableTemplate message) {
        super(date, message);
    }
	
	@Override
	public AlertType getType() {
		return AlertType.OUTAGE_PROCESSING_READ_LOGS_COMPLETION;
	}

}
