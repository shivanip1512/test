package com.cannontech.dr.controlarea.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.controlarea.filter.ForProgramFilter;
import com.cannontech.dr.controlarea.model.ControlArea;
import com.cannontech.dr.controlarea.model.ControlAreaDisplayField;
import com.cannontech.dr.controlarea.model.ControlAreaTrigger;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

public class ControlAreaServiceImpl implements ControlAreaService {
    private ControlAreaDao controlAreaDao;
    private LoadControlClientConnection loadControlClientConnection = null;
    private FilterService filterService;

    private static class TriggerRowMapper implements RowMapperWithBaseQuery<ControlAreaTrigger> {
        Map<Integer, List<ControlAreaTrigger>> triggersByControlAreaId = Maps.newHashMap();

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT deviceId, triggerNumber, triggerType FROM lmControlAreaTrigger");
            return retVal;
        }

        @Override
        public SqlFragmentSource getOrderBy() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("ORDER BY deviceId, triggerNumber");
            return retVal;
        }

        @Override
        public boolean needsWhere() {
            return true;
        }

        @Override
        public ControlAreaTrigger mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            int controlAreaId = rs.getInt("deviceId");
            ControlAreaTrigger retVal = new ControlAreaTrigger(controlAreaId,
                                                               rs.getInt("triggerNumber"),
                                                               rs.getString("triggerType"));
            List<ControlAreaTrigger> triggers = triggersByControlAreaId.get(controlAreaId);
            if (triggers == null) {
                triggers = Lists.newArrayList();
                triggersByControlAreaId.put(controlAreaId, triggers);
            }
            triggers.add(retVal);

            return retVal;
        }
    }

    private static class ControlAreaRowMapper extends AbstractRowMapperWithBaseQuery<ControlArea> {
        Map<Integer, List<ControlAreaTrigger>> triggersByControlAreaId;

        ControlAreaRowMapper(Map<Integer, List<ControlAreaTrigger>> triggersByControlAreaId) {
            this.triggersByControlAreaId = triggersByControlAreaId;
        }

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT paObjectId, paoName FROM yukonPAObject"
                + " WHERE type = ");
            retVal.appendArgument(PaoType.LM_CONTROL_AREA.getDbString());
            return retVal;
        }

        @Override
        public ControlArea mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            int controlAreaId = rs.getInt("paObjectId");
            PaoIdentifier paoId = new PaoIdentifier(controlAreaId,
                                                    PaoType.LM_CONTROL_AREA);
            ControlArea retVal = new ControlArea(paoId, rs.getString("paoName"));
            retVal.setTriggers(triggersByControlAreaId.get(controlAreaId));
            return retVal;
        }
    }

    // We can use this class to wrap filters made for a control area.
    // Then, we can query specifically for triggers that are specific to the
    // control areas we are filtering on.
    private class TriggerFilter implements UiFilter<ControlAreaTrigger> {
        UiFilter<DisplayablePao> wrappedControlAreaFilter;

        class WrappedSqlFilter implements SqlFilter {
            SqlFilter wrapped;

            WrappedSqlFilter(SqlFilter wrapped) {
                this.wrapped = wrapped;
            }

            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append("deviceId IN (SELECT paObjectId FROM yukonPAObject WHERE");
                retVal.append(wrapped.getWhereClauseFragment());
                retVal.append(")");
                return retVal;
            }
        }

        TriggerFilter(UiFilter<DisplayablePao> wrappedControlAreaFiter) {
            this.wrappedControlAreaFilter = wrappedControlAreaFiter;
        }

        @Override
        public Iterable<PostProcessingFilter<ControlAreaTrigger>> getPostProcessingFilters() {
            // this is only meant as a SQL filter
            return null;
        }

        @Override
        public Iterable<SqlFilter> getSqlFilters() {
            Iterable<SqlFilter> toBeWrapped = wrappedControlAreaFilter.getSqlFilters();
            if (toBeWrapped == null || !toBeWrapped.iterator().hasNext()) {
                return null;
            }

            List<SqlFilter> retVal = new ArrayList<SqlFilter>();
            for (SqlFilter wrapped : toBeWrapped) {
                retVal.add(new WrappedSqlFilter(wrapped));
            }

            return retVal;
        }
    }

    @Override
    public LMControlArea map(DisplayablePao from) throws ObjectMappingException {
        DatedObject<LMControlArea> datedControlArea =
            loadControlClientConnection.getDatedControlArea(from.getPaoIdentifier().getPaoId());
        return datedControlArea == null ? null : datedControlArea.getObject();
    }

    @Override
    public DatedObject<LMControlArea> getDatedControlArea(int controlAreaId) {
        return loadControlClientConnection.getDatedControlArea(controlAreaId);
    }

    @Override
    public ControlArea findControlAreaForProgram(YukonUserContext userContext, int programId) {
        UiFilter<DisplayablePao> filter = new ForProgramFilter(programId);

        SearchResult<ControlArea> searchResult =
            filterControlAreas(userContext, filter, null, 0, Integer.MAX_VALUE);

        if (searchResult.getHitCount() > 0) {
            return searchResult.getResultList().get(0);
        }
        return null;
    }

    @Override
    public SearchResult<ControlArea> filterControlAreas(
            YukonUserContext userContext, UiFilter<DisplayablePao> filter,
            Comparator<DisplayablePao> sorter, int startIndex, int count) {

        UiFilter<ControlAreaTrigger> triggerFilter = null;
        if (filter != null) {
            triggerFilter = new TriggerFilter(filter);
        }

        TriggerRowMapper triggerRowMapper = new TriggerRowMapper();
        filterService.filter(triggerFilter, null, startIndex, count,
                             triggerRowMapper);

        Comparator<DisplayablePao> defaultSorter =
            ControlAreaDisplayField.NAME.getSorter(this, userContext, false);
        if (sorter == null) {
            sorter = defaultSorter;
        } else {
            sorter = Ordering.from(sorter).compound(defaultSorter);
        }
        SearchResult<ControlArea> searchResult =
            filterService.filter(filter, sorter, startIndex, count,
                                 new ControlAreaRowMapper(triggerRowMapper.triggersByControlAreaId));

        return searchResult;
    }

    @Override
    public ControlArea getControlArea(int controlAreaId) {
        return controlAreaDao.getControlArea(controlAreaId);
    }

    @Autowired
    public void setControlAreaDao(ControlAreaDao controlAreaDao) {
        this.controlAreaDao = controlAreaDao;
    }

    @Autowired
    public void setLoadControlClientConnection(
            LoadControlClientConnection loadControlClientConnection) {
        this.loadControlClientConnection = loadControlClientConnection;
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }
}
