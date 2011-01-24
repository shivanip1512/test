package com.cannontech.common.search.pao.db;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class LmProgramForEnergyCompanyIdFilter implements SqlFilter {
    
	private Iterable<Integer> energyCompanyIds;
	
	public LmProgramForEnergyCompanyIdFilter(Iterable<Integer> energyCompanyIds) {
		this.energyCompanyIds = energyCompanyIds;
	}
	
	@Override
    public SqlFragmentSource getWhereClauseFragment() {
		
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("paobjectId IN (SELECT deviceId");
        retVal.append("				  FROM LMProgramWebPublishing LMPWP");
        retVal.append("               INNER JOIN ECToGenericMapping ECTGM ON (ECTGM.itemId = LMPWP.applianceCategoryId AND ECTGM.mappingCategory = 'ApplianceCategory')");
        retVal.append("               WHERE ECTGM.energyCompanyId").in(energyCompanyIds);
        retVal.append("               )");
        retVal.append("AND paobjectId IN (SELECT lmProgramDeviceId FROM lmControlAreaProgram)");
        return retVal;
    }
}
