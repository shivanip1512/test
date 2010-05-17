package com.cannontech.dr.quicksearch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.dr.model.ControllablePao;

/**
 * ControllablePao row mapper for DR quick search
 */
public class QuickSearchRowMapper implements RowMapperWithBaseQuery<ControllablePao> {

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder fragment = new SqlStatementBuilder();
        fragment.append("SELECT paoName, paObjectId, type");
        fragment.append("FROM yukonPAObject");
        fragment.append("WHERE ((category = ");
        fragment.appendArgument(PaoCategory.LOADMANAGEMENT.toString());
        fragment.append("       AND paoClass = ");
        fragment.appendArgument(PaoClass.LOADMANAGEMENT.toString());

        fragment.append("       AND type in (");
        fragment.appendArgumentList(Arrays.asList(PaoType.LM_CONTROL_AREA.getDbString(), 
                                                  PaoType.LM_SCENARIO.getDbString(), 
                                                  PaoType.LM_DIRECT_PROGRAM.getDbString()));
        fragment.append("       ))");
        fragment.append("       OR (category = ");
        fragment.appendArgument(PaoCategory.DEVICE.toString());
        fragment.append("    AND paoClass = ");
        fragment.appendArgument(PaoClass.GROUP.toString());
        fragment.append("    ))");
        
        return fragment;
    }

    @Override
    public SqlFragmentSource getGroupBy() {
        return null;
    }
    
    @Override
    public SqlFragmentSource getOrderBy() {
        return null;
    }

    @Override
    public boolean needsWhere() {
        return false;
    }

    @Override
    public ControllablePao mapRow(ResultSet rs, int rowNum) throws SQLException {
        int paoId = rs.getInt("paObjectId");
        String paoType = rs.getString("type");
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, PaoType.getForDbString(paoType));
        ControllablePao pao = new ControllablePao(paoIdentifier, rs.getString("paoName"));
        return pao;
    }
}
