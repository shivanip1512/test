package com.cannontech.web.picker.v2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.google.common.collect.Lists;

public class FilterPaoPicker extends BasePicker<UltraLightPao> {
    private FilterService filterService;
    private List<SqlFilter> filters = Lists.newArrayList();
    {
        filters.add(new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append("type").eq(PaoType.LM_DIRECT_PROGRAM.getDbString());
                return retVal;
            }
        });
        filters.add(new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append("paobjectId NOT IN (SELECT deviceId FROM lmProgramWebPublishing)");
                retVal.append("AND paobjectId IN (SELECT lmProgramDeviceId FROM lmControlAreaProgram)");
                return retVal;
            }
        });
    }

    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.pao.";

        columns.add(new OutputColumn("paoName", titleKeyPrefix + "name"));
        columns.add(new OutputColumn("type", titleKeyPrefix + "type"));

        outputColumns = Collections.unmodifiableList(columns);
    }
    private final static RowMapperWithBaseQuery<UltraLightPao> rowMapper =
        new AbstractRowMapperWithBaseQuery<UltraLightPao>() {

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
        public UltraLightPao mapRow(ResultSet rs, int rowNum)
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
    };

    @Override
    public String getIdFieldName() {
        return "paoId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Override
    public SearchResult<UltraLightPao> search(final String ss, int start, int count) {
        final List<SqlFilter> myFilters = Lists.newArrayList();
        myFilters.addAll(filters);
        myFilters.add(new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append("LOWER(paoName) LIKE LOWER(").appendArgument('%' + ss + '%').append(")");
                return retVal;
            }
        });

        UiFilter<UltraLightPao> filter = new UiFilter<UltraLightPao>() {
            @Override
            public Iterable<PostProcessingFilter<UltraLightPao>> getPostProcessingFilters() {
                // Since this is used in a picker we most certainly do NOT
                // want any post processing filters. We don't want to force
                // the filter to retrieve all of the results.
                return null;
            }

            @Override
            public Iterable<SqlFilter> getSqlFilters() {
                return myFilters;
            }
        };

        // We can't use a sorter either or the filter service would need to
        // get everything from the database.
        return filterService.filter(filter, null, start, count, rowMapper);
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }
}
