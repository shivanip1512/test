package com.cannontech.common.favorites.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterDao;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.favorites.service.FavoritesService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.impl.PaoNameDisplayablePaoRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;

public class FavoritesServiceImpl implements FavoritesService {
    @Autowired private FilterDao filterDao;

    @Override
    public List<DisplayablePao> getRecentlyViewed(LiteYukonUser user,
            int count, UiFilter<DisplayablePao> filter) {
        RecentlyViewedRowMapper rowMapper = new RecentlyViewedRowMapper();
        SearchResults<DisplayablePao> searchResult =
            filterDao.filter(filter, null, 0, count, rowMapper);
        List<DisplayablePao> retVal = searchResult.getResultList();
        Collections.sort(retVal, new DisplayablePaoComparator());
        return retVal;
    }

    @Override
    public List<DisplayablePao> getFavorites(LiteYukonUser user,
            UiFilter<DisplayablePao> filterIn) {
        List<UiFilter<DisplayablePao>> filters = Lists.newArrayList();
        filters.add(filterIn);
        filters.add(new IsFavoriteFilter(user.getUserID()));
        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);
        FavoriteRowMapper rowMapper = new FavoriteRowMapper();
        Comparator<DisplayablePao> sorter = new DisplayablePaoComparator();
        SearchResults<DisplayablePao> searchResult =
            filterDao.filter(filter, sorter, 0, Integer.MAX_VALUE, rowMapper);
        return searchResult.getResultList();
    }

    private static class RecentlyViewedRowMapper extends PaoNameDisplayablePaoRowMapper implements
            RowMapperWithBaseQuery<DisplayablePao> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT paoName, yukonPaobject.paobjectId, type, whenViewed");
            retVal.append("FROM paoRecentViews, yukonPaobject");
            retVal.append("WHERE yukonPaobject.paobjectId = paoRecentViews.paobjectId");
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

    private static class FavoriteRowMapper extends PaoNameDisplayablePaoRowMapper implements RowMapperWithBaseQuery<DisplayablePao> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT paoName, paobjectId, type FROM yukonPAObject");
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

    private static class IsFavoriteFilter implements UiFilter<DisplayablePao> {
        private int userId;

        private IsFavoriteFilter(int userId) {
            this.userId = userId;
        }

        @Override
        public Iterable<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
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
