package com.cannontech.dr.dao.impl;


import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.impl.DisplayablePaoRowMapper;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.dao.DemandResponseFavoritesDao;
import com.cannontech.dr.model.DisplayablePaoComparator;
import com.google.common.collect.Lists;

public class DemandResponseFavoritesDaoImpl implements DemandResponseFavoritesDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private FilterService filterService;
    private Map<Integer, DatedObject<Set<Integer>>> favoritesByUser =
        Collections.synchronizedMap(new HashMap<Integer, DatedObject<Set<Integer>>>());

    private static class RecentlyViewedRowMapper extends DisplayablePaoRowMapper
            implements RowMapperWithBaseQuery<DisplayablePao> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT paoName, yukonPaobject.paobjectId, type," +
            		" whenViewed FROM lmRecentViews, yukonPaobject" +
            		" WHERE yukonPaobject.paobjectId = lmRecentViews.paobjectId");
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

    public static class FavoriteRowMapper extends DisplayablePaoRowMapper
        implements RowMapperWithBaseQuery<DisplayablePao> {

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

    private static class NotInFavoritesFilter implements UiFilter<DisplayablePao> {
        private int userId;

        private NotInFavoritesFilter(int userId) {
            this.userId = userId;
        }

        @Override
        public Iterable<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
            return null;
        }

        @Override
        public Iterable<SqlFilter> getSqlFilters() {
            List<SqlFilter> sqlFilter = Lists.newArrayList();
            sqlFilter.add(new SqlFilter(){
                @Override
                public SqlFragmentSource getWhereClauseFragment() {
                    SqlStatementBuilder whereClause = new SqlStatementBuilder();
                    whereClause.append("yukonPaobject.paobjectId NOT IN" +
                            " (SELECT paobjectId FROM lmFavorites WHERE userId =");
                    whereClause.appendArgument(userId);
                    whereClause.append(")");
                    return whereClause;
                }});
            return sqlFilter;
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
            sqlFilter.add(new SqlFilter(){
                @Override
                public SqlFragmentSource getWhereClauseFragment() {
                    SqlStatementBuilder whereClause = new SqlStatementBuilder();
                    whereClause.append("paobjectId IN" +
                            " (SELECT paobjectId FROM lmFavorites WHERE userId =");
                    whereClause.appendArgument(userId);
                    whereClause.append(")");
                    return whereClause;
                }});
            return sqlFilter;
        }
    }

    @Override
    public void detailPageViewed(int paoId) {
        Date now = new Date();
        int rowsAffected = simpleJdbcTemplate.update("UPDATE lmRecentViews" +
        		" SET whenViewed = ? WHERE paobjectId = ?", now, paoId);
        if (rowsAffected < 1) {
            simpleJdbcTemplate.update("INSERT INTO" +
            		" lmRecentViews (paobjectId, whenViewed) VALUES (?, ?)",
            		paoId, now);
        }
    }

    @Override
    public List<DisplayablePao> getRecentlyViewed(LiteYukonUser user,
            int count, UiFilter<DisplayablePao> filterIn) {
        List<UiFilter<DisplayablePao>> filters = Lists.newArrayList();
        filters.add(filterIn);
        filters.add(new NotInFavoritesFilter(user.getUserID()));
        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);
        RecentlyViewedRowMapper rowMapper = new RecentlyViewedRowMapper();
        SearchResult<DisplayablePao> searchResult =
            filterService.filter(filter, null, 0, count, rowMapper);
        return searchResult.getResultList();
    }

    @Override
    public void addFavorite(int paoId, LiteYukonUser user) {
        if (paoId == 0) {
            throw new IllegalArgumentException();
        }
        simpleJdbcTemplate.update("INSERT INTO" +
                                  " lmFavorites (userId, paobjectId) VALUES (?, ?)",
                                  user.getUserID(), paoId);
        favoritesByUser.remove(user.getUserID());
    }

    @Override
    public void removeFavorite(int paoId, LiteYukonUser user) {
        simpleJdbcTemplate.update("DELETE FROM lmFavorites" +
                                  " WHERE userId = ? AND paobjectId = ?",
                                  user.getUserID(), paoId);
        favoritesByUser.remove(user.getUserID());
    }

    @Override
    public Boolean isFavorite(int paoId, LiteYukonUser user, Date afterDate) {
        DatedObject<Set<Integer>> datedUserFavorites =
            favoritesByUser.get(user.getUserID());

        if (datedUserFavorites == null) {
            List<Integer> favoritesFromDB =
                simpleJdbcTemplate.query("SELECT paobjectId FROM lmFavorites" +
                                         " WHERE userId = ?",
                                         new IntegerRowMapper(),
                                         user.getUserID());

            Set<Integer> userFavorites = new HashSet<Integer>();
            datedUserFavorites = new DatedObject<Set<Integer>>(userFavorites);
            userFavorites.addAll(favoritesFromDB);
            favoritesByUser.put(user.getUserID(), datedUserFavorites);
            return userFavorites.contains(paoId);
        }

        if (!datedUserFavorites.getDate().before(afterDate)) {
            return datedUserFavorites.getObject().contains(paoId);
        }

        return null;
    }

    @Override
    public List<DisplayablePao> getFavorites(LiteYukonUser user,
            UiFilter<DisplayablePao> filterIn) {
        List<UiFilter<DisplayablePao>> filters = Lists.newArrayList();
        filters.add(filterIn);
        filters.add(new IsFavoriteFilter(user.getUserID()));
        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);
        FavoriteRowMapper rowMapper = new FavoriteRowMapper();
        Comparator<DisplayablePao> sorter = new DisplayablePaoComparator(false);
        SearchResult<DisplayablePao> searchResult =
            filterService.filter(filter, sorter, 0, Integer.MAX_VALUE, rowMapper);
        return searchResult.getResultList();
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }
}
