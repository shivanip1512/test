package com.cannontech.common.favorites.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.favorites.service.FavoritesService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.impl.PaoNameControllablePaoRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.model.ControllablePao;
import com.google.common.collect.Lists;

public class FavoritesServiceImpl implements FavoritesService {
    private FilterService filterService;
    
    @Override
    public List<ControllablePao> getRecentlyViewed(LiteYukonUser user,
                                                    int count, 
                                                    UiFilter<ControllablePao> filter) {
        RecentlyViewedRowMapper rowMapper = new RecentlyViewedRowMapper();
        SearchResult<ControllablePao> searchResult =
            filterService.filter(filter, null, 0, count, rowMapper);
        List<ControllablePao> retVal = searchResult.getResultList();

        Collections.sort(retVal, new DisplayablePaoComparator());
        
        return retVal;
    }

    @Override
    public List<ControllablePao> getFavorites(LiteYukonUser user,
                                               UiFilter<ControllablePao> filterIn) {
        List<UiFilter<ControllablePao>> filters = Lists.newArrayList();
        filters.add(filterIn);
        filters.add(new IsFavoriteFilter(user.getUserID()));
        UiFilter<ControllablePao> filter = UiFilterList.wrap(filters);
        FavoriteRowMapper rowMapper = new FavoriteRowMapper();
        Comparator<DisplayablePao> sorter = new DisplayablePaoComparator();
        SearchResult<ControllablePao> searchResult =
            filterService.filter(filter, sorter, 0, Integer.MAX_VALUE, rowMapper);

        List<ControllablePao> retVal = searchResult.getResultList();
        
        return retVal;
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }

    private static class RecentlyViewedRowMapper extends
            PaoNameControllablePaoRowMapper implements
            RowMapperWithBaseQuery<ControllablePao> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT PAO.PAOName, PAO.PAObjectId, PAO.Type, PRV.WhenViewed, ");
            retVal.append("       COUNT(LMCSP.ProgramId) ScenarioProgramCount");
            retVal.append("FROM YukonPAObject PAO");
            retVal.append("JOIN PAORecentViews PRV ON PRV.PAObjectId = PAO.PAObjectId ");
            retVal.append("LEFT JOIN LMControlScenarioProgram LMCSP ");
            retVal.append("	   ON LMCSP.ScenarioId = PAO.PAObjectId ");
            return retVal;
        }

        @Override
        public SqlFragmentSource getGroupBy() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("GROUP BY  PAO.PAOName, PAO.PAObjectId, PAO.Type, PRV.WhenViewed ");
            return retVal;
        }

        @Override
        public SqlFragmentSource getOrderBy() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("ORDER BY whenViewed DESC");
            return retVal;
        }

        @Override
        public boolean needsWhere() {
            return false;
        }
    }

    private static class FavoriteRowMapper extends PaoNameControllablePaoRowMapper
            implements RowMapperWithBaseQuery<ControllablePao> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT PAO.PAOName, PAO.PAObjectId, PAO.Type, ");
            retVal.append("       COUNT(LMCSP.ProgramId) ScenarioProgramCount");
            retVal.append("FROM YukonPAObject PAO ");
            retVal.append("LEFT JOIN LMControlScenarioProgram LMCSP ");
            retVal.append("    ON LMCSP.ScenarioId = PAO.PAObjectId ");
            return retVal;
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
            return true;
        }
    }

    private static class IsFavoriteFilter implements UiFilter<ControllablePao> {
        private int userId;

        private IsFavoriteFilter(int userId) {
            this.userId = userId;
        }

        @Override
        public Iterable<PostProcessingFilter<ControllablePao>> getPostProcessingFilters() {
            return null;
        }

        @Override
        public Iterable<SqlFilter> getSqlFilters() {
            List<SqlFilter> sqlFilter = Lists.newArrayList();
            sqlFilter.add(new SqlFilter() {
                @Override
                public SqlFragmentSource getWhereClauseFragment() {
                    SqlStatementBuilder whereClause = new SqlStatementBuilder();
                    whereClause.append("paobjectId IN");
                    whereClause.append(" (SELECT paobjectId FROM paoFavorites WHERE userId =");
                    whereClause.appendArgument(userId);
                    whereClause.append(")");
                    return whereClause;
                }
            });
            return sqlFilter;
        }
    }
}
