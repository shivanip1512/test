package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LmProgramEcobee extends LMProgramDirect {

	@Override
	protected PaoType getProgramPaoType() {
		return PaoType.LM_ECOBEE_PROGRAM;
	}	
}
