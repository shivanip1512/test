package com.cannontech.common.search.pao.db;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class PaoTagFilter implements SqlFilter {

	private PaoTag paoTag;
    private PaoDefinitionDao paoDefinitionDao;
    
	@Override
    public SqlFragmentSource getWhereClauseFragment() {
		Set<PaoType> types = paoDefinitionDao.getPaoTypesThatSupportTag(paoTag);
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("type").in(types);
        return retVal;
    }
	
	public void setPaoTag(PaoTag paoTag) {
		this.paoTag = paoTag;
	}
	
	@Autowired
	public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
		this.paoDefinitionDao = paoDefinitionDao;
	}
}
