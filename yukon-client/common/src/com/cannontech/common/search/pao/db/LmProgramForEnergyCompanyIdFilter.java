package com.cannontech.common.search.pao.db;

import java.util.Set;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.energyCompany.EcMappingCategory;

public class LmProgramForEnergyCompanyIdFilter implements SqlFilter {
    
	private Set<Integer> energyCompanyIds;
	
	public LmProgramForEnergyCompanyIdFilter(Set<Integer> energyCompanyIds) {
		this.energyCompanyIds = energyCompanyIds;
	}
	
	@Override
    public SqlFragmentSource getWhereClauseFragment() {
		
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("paobjectId IN (SELECT deviceId");
        retVal.append("				  FROM LMProgramWebPublishing LMPWP");
        retVal.append("               JOIN ECToGenericMapping ECTGM ON ");
        retVal.append("                 ECTGM.itemId = LMPWP.applianceCategoryId AND ECTGM.mappingCategory").eq_k(EcMappingCategory.APPLIANCE_CATEGORY);
        retVal.append("               WHERE ECTGM.energyCompanyId").in(energyCompanyIds);
        retVal.append("               )");
        retVal.append("AND paobjectId IN (SELECT lmProgramDeviceId FROM lmControlAreaProgram)");
        return retVal;
    }
}
