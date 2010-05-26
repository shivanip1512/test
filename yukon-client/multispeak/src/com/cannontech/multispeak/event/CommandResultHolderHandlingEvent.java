package com.cannontech.multispeak.event;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.commands.CommandResultHolder;

public interface CommandResultHolderHandlingEvent {

	public void handleResult(Meter meter, CommandResultHolder result);
}
