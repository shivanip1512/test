package com.cannontech.capcontrol.creation.service.impl;

import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.creation.service.PaoCreationTableParserService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.google.common.collect.Lists;

public class PaoTableParserServiceImpl implements PaoCreationTableParserService {

	private PaoDefinitionDao paoDefinitionDao;
	
	@Override
	public List<PaoProviderTable> parseTableNames(PaoType paoType) {
		List<PaoProviderTable> tables = Lists.newArrayList();
		
		String tableNames = paoDefinitionDao.getValueForTagString(paoType, PaoTag.PAO_CREATION_SERVICE);
		
		StringTokenizer stringTokenizer = new StringTokenizer(tableNames, ",");
		
		while (stringTokenizer.hasMoreTokens()) {
			tables.add(PaoProviderTable.valueOf(stringTokenizer.nextToken()));
		}
		
		return tables;
	}
	
	@Autowired
	public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
		this.paoDefinitionDao = paoDefinitionDao;
	}
}
