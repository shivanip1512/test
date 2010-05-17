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
import com.cannontech.common.pao.ControllablePaoComparator;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.impl.PaoNameControllablePaoRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.scenario.service.ScenarioService;
import com.google.common.collect.Lists;

public class FavoritesServiceImpl implements FavoritesService {
    private FilterService filterService;
    private ScenarioService scenarioService;
    
    @Override
    public List<ControllablePao> getRecentlyViewed(LiteYukonUser user,
                                                    int count, 
                                                    UiFilter<ControllablePao> filter) {
        RecentlyViewedRowMapper rowMapper = new RecentlyViewedRowMapper();
        SearchResult<ControllablePao> searchResult =
            filterService.filter(filter, null, 0, count, rowMapper);
        List<ControllablePao> retVal = searchResult.getResultList();

        scenarioService.addScenarioActionState(retVal);
        Collections.sort(retVal, new ControllablePaoComparator());
        
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
        Comparator<ControllablePao> sorter = new ControllablePaoComparator();
        SearchResult<ControllablePao> searchResult =
            filterService.filter(filter, sorter, 0, Integer.MAX_VALUE, rowMapper);

        List<ControllablePao> retVal = searchResult.getResultList();
        scenarioService.addScenarioActionState(retVal);
        
        return retVal;
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }

    @Autowired
    public void setScenarioService(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }
    
    private static class RecentlyViewedRowMapper extends
            PaoNameControllablePaoRowMapper implements
            RowMapperWithBaseQuery<ControllablePao> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT paoName, yukonPaobject.paobjectId, type, whenViewed");
            retVal.append("FROM paoRecentViews, yukonPaobject");
            retVal.append("WHERE yukonPaobject.paobjectId = paoRecentViews.paobjectId");
            return retVal;
        }

        @Override
        public SqlFragmentSource getGroupBy() {
            return null;
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
            retVal.append("SELECT paoName, paobjectId, type FROM yukonPAObject");
            return retVal;
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
