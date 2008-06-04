package com.cannontech.web.group;

import java.util.Date;

import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.BaseAlert;
import com.cannontech.common.util.ResolvableTemplate;

public class CommandCompletionAlert extends BaseAlert {

    public CommandCompletionAlert(Date date, ResolvableTemplate message) {
        super(date, message);
    }

    @Override
    public AlertType getType() {
        return AlertType.COMMAND_COMPLETION;
    }
}
