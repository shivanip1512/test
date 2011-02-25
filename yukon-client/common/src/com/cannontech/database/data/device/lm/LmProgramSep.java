package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LmProgramSep extends LMProgramDirect {

	public LmProgramSep() {
		super();
	}

	@Override
	protected PaoType getProgramPaoType() {
		return PaoType.LM_SEP_PROGRAM;
	}	
}
