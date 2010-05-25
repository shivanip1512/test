package com.cannontech.dr.quicksearch;

import java.util.Arrays;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.dr.dao.impl.PaoNameControllablePaoRowMapper;
import com.cannontech.dr.model.ControllablePao;

/**
 * ControllablePao row mapper for DR quick search
 */
public class QuickSearchRowMapper extends PaoNameControllablePaoRowMapper 
    implements RowMapperWithBaseQuery<ControllablePao> {

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder fragment = new SqlStatementBuilder();
        fragment.append("SELECT PAO.PAOName, PAO.PAObjectId, PAO.Type, ");
        fragment.append("       COUNT(LMCSP.ProgramId) ScenarioProgramCount");
        fragment.append("FROM YukonPAObject PAO");
        fragment.append("LEFT JOIN LMControlScenarioProgram LMCSP ");
        fragment.append("    ON LMCSP.ScenarioId = PAO.PAObjectId ");
        fragment.append("WHERE ((PAO.Category").eq(PaoCategory.LOADMANAGEMENT.toString());
        fragment.append("       AND PAO.PAOClass").eq(PaoClass.LOADMANAGEMENT.toString());
        fragment.append("       AND PAO.Type").in(
            Arrays.asList(PaoType.LM_CONTROL_AREA.getDbString(), 
                          PaoType.LM_SCENARIO.getDbString(), 
                          PaoType.LM_DIRECT_PROGRAM.getDbString()));
        fragment.append("       )");
        fragment.append("       OR (PAO.Category").eq(PaoCategory.DEVICE.toString());
        fragment.append("    AND PAO.PAOClass").eq(PaoClass.GROUP.toString());
        fragment.append("    ))");
        
        return fragment;
    }

    @Override
    public SqlFragmentSource getGroupBy() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("GROUP BY  PAO.PAOName, PAO.PAObjectId, PAO.Type ");
        return retVal;
    }
    
    @Override
    public SqlFragmentSource getOrderBy() {
        return null;
    }

    @Override
    public boolean needsWhere() {
        return false;
    }

}
