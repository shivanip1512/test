package com.cannontech.capcontrol.creation.service;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.PaoProviderTableEnum;

public interface PaoCreationTableParserService {

	public List<PaoProviderTableEnum> parseTableNames(PaoType paoType);
}
