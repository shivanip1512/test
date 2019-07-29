package com.cannontech.web.api.dr.setup.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
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
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.web.api.dr.setup.dao.LMSetupDao;

public class LMSetupDaoImpl implements LMSetupDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public SearchResults<LMPaoDto> getPaoDetails(FilterCriteria<LMSetupFilter> criteria, List<PaoType> paoTypes) {
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
        if (!paoTypes.isEmpty()) {
            sqlCommon.append("WHERE Type").in_k(paoTypes);
        }
        if (filter.getName() != null && !filter.getName().isBlank()) {
            sqlCommon.append("AND PAOName").contains(filter.getName());
        }
        sqlCommon.append(") innertable");
        sqlTotalCountQuery.append(sqlCommon);
        sqlTotalCountQuery.append(") outertable");
        sqlPaginationQuery.append(sqlCommon);
        if (criteria.getPagingParameters() != null) {
            sqlPaginationQuery.append(" WHERE RowNumber BETWEEN");
            sqlPaginationQuery.append(criteria.getPagingParameters().getOneBasedStartIndex());
            sqlPaginationQuery.append(" AND ");
            sqlPaginationQuery.append(criteria.getPagingParameters().getOneBasedEndIndex());
        }

        int totalHitCount = jdbcTemplate.queryForInt(sqlTotalCountQuery);

        final List<LMPaoDto> resultList = new ArrayList<>();

        jdbcTemplate.query(sqlPaginationQuery, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                LMPaoDto lmPaoDto = new LMPaoDto();
                lmPaoDto.setId(rs.getInt("PAObjectID"));
                lmPaoDto.setName(rs.getString("PAOName"));
                lmPaoDto.setType(rs.getEnum("Type", PaoType.class));
                resultList.add(lmPaoDto);
            }
        });
        SearchResults<LMPaoDto> searchResults =
            SearchResults.pageBasedForSublist(resultList, criteria.getPagingParameters(), totalHitCount);
        return searchResults;

    }

    @Override
    public SearchResults<LMPaoDto> getProgramConstraint(FilterCriteria<LMSetupFilter> criteria) {
        SqlStatementBuilder sqlCommon = new SqlStatementBuilder();
        SqlStatementBuilder sqlTotalCountQuery = new SqlStatementBuilder();
        SqlStatementBuilder sqlPaginationQuery = new SqlStatementBuilder();
        Direction sortingDirection = ((criteria.getSortingParameters().getDirection() == null) ? Direction.asc
            : criteria.getSortingParameters().getDirection());

        LMSetupFilter filter = criteria.getFilteringParameters();

        sqlTotalCountQuery.append("SELECT COUNT(*) FROM (");
        sqlCommon.append("SELECT");
        sqlCommon.append("RowNumber, ConstraintID, ConstraintName");
        sqlCommon.append("FROM (");
        sqlCommon.append("SELECT ");
        sqlCommon.append("ROW_NUMBER() OVER (ORDER BY ");
        sqlCommon.append("ConstraintName");
        sqlCommon.append(" ");
        sqlCommon.append(sortingDirection);
        sqlCommon.append(") AS RowNumber,");
        sqlCommon.append("ConstraintID,");
        sqlCommon.append("ConstraintName");
        sqlCommon.append("FROM LMProgramConstraints");
        if (filter.getName() != null && !filter.getName().isBlank()) {
            sqlCommon.append("WHERE ConstraintName").contains(filter.getName());
        }
        sqlCommon.append(") innertable");
        sqlTotalCountQuery.append(sqlCommon);
        sqlTotalCountQuery.append(") outertable");
        sqlPaginationQuery.append(sqlCommon);
        if (criteria.getPagingParameters() != null) {
            sqlPaginationQuery.append(" WHERE RowNumber BETWEEN");
            sqlPaginationQuery.append(criteria.getPagingParameters().getOneBasedStartIndex());
            sqlPaginationQuery.append(" AND ");
            sqlPaginationQuery.append(criteria.getPagingParameters().getOneBasedEndIndex());
        }

        int totalHitCount = jdbcTemplate.queryForInt(sqlTotalCountQuery);

        final List<LMPaoDto> resultList = new ArrayList<>();

        jdbcTemplate.query(sqlPaginationQuery, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                LMPaoDto lmPaoDto = new LMPaoDto();
                lmPaoDto.setId(rs.getInt("ConstraintID"));
                lmPaoDto.setName(rs.getString("ConstraintName"));
                resultList.add(lmPaoDto);
            }
        });
        SearchResults<LMPaoDto> searchResults =
            SearchResults.pageBasedForSublist(resultList, criteria.getPagingParameters(), totalHitCount);
        return searchResults;
    }
}
