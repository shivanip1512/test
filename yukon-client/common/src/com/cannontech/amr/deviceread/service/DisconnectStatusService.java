package com.cannontech.amr.deviceread.service;

import com.cannontech.amr.deviceread.model.DisconnectState;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.commands.CommandResultHolder;

public interface DisconnectStatusService {

	public DisconnectState getDisconnectState(Meter meter, CommandResultHolder result);
}
