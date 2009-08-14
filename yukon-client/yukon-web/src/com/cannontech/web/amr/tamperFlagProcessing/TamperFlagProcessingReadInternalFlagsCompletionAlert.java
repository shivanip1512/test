package com.cannontech.web.amr.tamperFlagProcessing;

import java.util.Date;

import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.BaseAlert;
import com.cannontech.common.util.ResolvableTemplate;

public class TamperFlagProcessingReadInternalFlagsCompletionAlert extends BaseAlert {

	public TamperFlagProcessingReadInternalFlagsCompletionAlert(Date date, ResolvableTemplate message) {
        super(date, message);
    }
	
	@Override
	public AlertType getType() {
		return AlertType.TAMPER_FLAG_PROCESSING_READ_INTERNAL_FLAGS_COMPLETION;
	}

}
