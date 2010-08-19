package com.cannontech.common.search.pao.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.UltraLightPaoAdapter;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;

public class UltraLightPaoRowMapper extends AbstractRowMapperWithBaseQuery<UltraLightPaoAdapter> {
    @Override
    public SqlFragmentSource getBaseQuery() {
        return new SimpleSqlFragment("SELECT paObjectId, paoName, type" +
                " FROM yukonPAObject");
    }

    @Override
    public SqlFragmentSource getOrderBy() {
        return new SimpleSqlFragment("ORDER BY LOWER(paoName)");
    }

    @Override
    public boolean needsWhere() {
        return true;
    }

    @Override
    public UltraLightPaoAdapter mapRow(ResultSet rs, int rowNum)
            throws SQLException {
        final int paoId = rs.getInt("paObjectId");
        final String paoName = rs.getString("paoName");
        final String type = rs.getString("type");
        return new UltraLightPaoAdapter() {

            @Override
            public PaoIdentifier getPaoIdentifier() {
                int paoId = getPaoId();
                String typeStr = getType();
                PaoType paoType = PaoType.getForDbString(typeStr);
                
                return new PaoIdentifier(paoId, paoType);
            }

            @Override
            public int getPaoId() {
                return paoId;
            }

            @Override
            public String getPaoName() {
                return paoName;
            }

            @Override
            public String getType() {
                return type;
            }};
    }
}
