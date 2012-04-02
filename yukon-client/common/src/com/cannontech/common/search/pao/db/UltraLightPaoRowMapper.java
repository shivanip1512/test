package com.cannontech.common.search.pao.db;

import java.sql.SQLException;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.database.YukonResultSet;

public class UltraLightPaoRowMapper extends AbstractRowMapperWithBaseQuery<UltraLightPao> {
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
    public UltraLightPao mapRow(YukonResultSet rs)
            throws SQLException {
        final int paoId = rs.getInt("paObjectId");
        final String paoName = rs.getString("paoName");
        final String type = rs.getString("type");
        return new UltraLightPao() {

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
