package com.cannontech.web.api.dr.setup.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.model.Direction;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.web.api.dr.setup.dao.LMSetupDao;

public abstract class AbstractLMSetupDaoImpl<T> implements LMSetupDao<T> {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    public SearchResults<LMPaoDto> getDetails(FilterCriteria<LMSetupFilter> criteria, List<PaoType> paoTypes) {
        SqlStatementBuilder sqlCommon = new SqlStatementBuilder();
        SqlStatementBuilder sqlTotalCountQuery = new SqlStatementBuilder();
        SqlStatementBuilder sqlPaginationQuery = new SqlStatementBuilder();
        Direction sortingDirection = ((criteria.getSortingParameters().getDirection() == null) ? Direction.asc
            : criteria.getSortingParameters().getDirection());

        String sortValue = ((criteria.getSortingParameters().getSort() == null) ? SortBy.NAME.getDbString()
            : criteria.getSortingParameters().getSort());

        LMSetupFilter filter = criteria.getFilteringParameters();

        sqlTotalCountQuery.append("SELECT COUNT(*) FROM (");
        sqlCommon.append("SELECT");
        sqlCommon.append("RowNumber, PAObjectID, PAOName, Type");
        sqlCommon.append("FROM (");
        sqlCommon.append("SELECT ");
        sqlCommon.append("ROW_NUMBER() OVER (ORDER BY ");
        sqlCommon.append(sortValue);
        sqlCommon.append(" ");
        sqlCommon.append(sortingDirection);
        sqlCommon.append(") AS RowNumber,");
        sqlCommon.append("PAObjectID,");
        sqlCommon.append("PAOName,");
        sqlCommon.append("Type");
        sqlCommon.append("FROM YukonPAObject");

        sqlCommon.append("WHERE Type").in_k(paoTypes);

        if (filter.getName() != null && !filter.getName().isBlank()) {
            sqlCommon.append("AND PAOName").contains(filter.getName());
        }
        sqlCommon.append(") innertable");
        sqlTotalCountQuery.append(sqlCommon);
        sqlTotalCountQuery.append(") outertable");
        sqlPaginationQuery.append(sqlCommon);
        sqlPaginationQuery.append(getPaginationQuery(criteria));

        int totalHitCount = jdbcTemplate.queryForInt(sqlTotalCountQuery);

        final List<LMPaoDto> resultList = jdbcTemplate.query(sqlPaginationQuery, (YukonResultSet rs) -> {
            LMPaoDto lmPaoDto = new LMPaoDto();
            lmPaoDto.setId(rs.getInt("PAObjectID"));
            lmPaoDto.setName(rs.getString("PAOName"));
            lmPaoDto.setType(rs.getEnum("Type", PaoType.class));
            return lmPaoDto;
        });
        SearchResults<LMPaoDto> searchResults =
            SearchResults.pageBasedForSublist(resultList, criteria.getPagingParameters(), totalHitCount);
        return searchResults;

    }

    public SqlStatementBuilder getCommonQuery(Direction sortingDirection, String sortValue, LMSetupFilter filter, String columnNames) {
        SqlStatementBuilder sqlCommon = new SqlStatementBuilder();

        sqlCommon.append("SELECT");
        sqlCommon.append("RowNumber, * ");
        sqlCommon.append("FROM (");
        sqlCommon.append("SELECT ");
        sqlCommon.append("ROW_NUMBER() OVER (ORDER BY ");
        sqlCommon.append(sortValue);
        sqlCommon.append(" ");
        sqlCommon.append(sortingDirection);
        sqlCommon.append(") AS RowNumber, " + columnNames);
        sqlCommon.append(getTableAndWhereClause(filter));
        sqlCommon.append(") innertable");

        return sqlCommon;
    }

    public SqlStatementBuilder getPaginationQuery(FilterCriteria<LMSetupFilter> criteria) {

        SqlStatementBuilder sqlPaginationQuery = new SqlStatementBuilder();
        if (criteria.getPagingParameters() != null) {
            sqlPaginationQuery.append(" WHERE RowNumber BETWEEN");
            sqlPaginationQuery.append(criteria.getPagingParameters().getOneBasedStartIndex());
            sqlPaginationQuery.append(" AND ");
            sqlPaginationQuery.append(criteria.getPagingParameters().getOneBasedEndIndex());
        }
        return sqlPaginationQuery;
    }

    /**
     * Return SqlStatementBuilder corresponding to Table and where clause.
     */
    public abstract SqlStatementBuilder getTableAndWhereClause(LMSetupFilter filter);

    /**
     * Return selected columns names for filtering.
     */
    public abstract String getColumnNames();
}
