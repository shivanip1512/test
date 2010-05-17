package com.cannontech.dr.program.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.dr.model.ControllablePao;

public class ForScenarioFilter implements UiFilter<ControllablePao> {
    private int scenarioId;

    public ForScenarioFilter(int scenarioId) {
        this.scenarioId = scenarioId;
    }

    @Override
    public List<PostProcessingFilter<ControllablePao>> getPostProcessingFilters() {
        return null;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        List<SqlFilter> retVal = new ArrayList<SqlFilter>(1);
        retVal.add(new SqlFilter(){

            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder retVal = new SqlStatementBuilder(
                    "paObjectId IN (SELECT programId"
                    + " FROM lmControlScenarioProgram WHERE scenarioId =");
                retVal.appendArgument(scenarioId);
                retVal.append(")");
                return retVal;
            }});

        return retVal;
    }
}
