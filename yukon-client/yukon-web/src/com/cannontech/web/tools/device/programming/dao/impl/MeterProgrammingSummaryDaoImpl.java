package com.cannontech.web.tools.device.programming.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.device.programming.model.MeterProgramSource;
import com.cannontech.common.device.programming.model.ProgrammingStatus;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.device.programming.dao.MeterProgrammingSummaryDao;
import com.cannontech.web.tools.device.programming.model.MeterProgramInfo;
import com.cannontech.web.tools.device.programming.model.MeterProgramStatistics;
import com.cannontech.web.tools.device.programming.model.MeterProgramSummaryDetail;
import com.cannontech.web.tools.device.programming.model.MeterProgrammingSummaryFilter;
import com.cannontech.web.tools.device.programming.model.MeterProgrammingSummaryFilter.DisplayableStatus;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MeterProgrammingSummaryDaoImpl implements MeterProgrammingSummaryDao{
	
    private static final Logger log = YukonLogManager.getLogger(MeterProgrammingSummaryDaoImpl.class);
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DeviceGroupService deviceGroupService;
    
    final class MeterProgramStatisticsMapper implements YukonRowMapper<MeterProgramStatistics> {
        private MessageSourceAccessor accessor;

        MeterProgramStatisticsMapper(YukonUserContext context) {
            accessor = messageSourceResolver.getMessageSourceAccessor(context);
        }

        @Override
        public MeterProgramStatistics mapRow(YukonResultSet rs) throws SQLException {
            MeterProgramStatistics row = new MeterProgramStatistics();
            MeterProgramSource source = MeterProgramSource.getByPrefix(rs.getString("Source"));
            MeterProgramInfo programInfo = new MeterProgramInfo();
            programInfo.setSource(source);
            String name = rs.getStringSafe("Name");
            if (!StringUtils.isEmpty(name)) {
                programInfo.setName(name);
                programInfo.setGuid(rs.getString("Guid"));
            } else {
                programInfo.setName(accessor.getMessage(source.getFormatKey()));
            }
            row.setProgramInfo(programInfo);
            row.setDeviceTotal(rs.getInt("Total"));
            row.setInProgressTotal(rs.getInt("InProgress"));
            row.setFailureTotal(rs.getInt("Failed"));
            log.debug("Created {} ", row);
            return row;
        }
    }

    final class MeterProgramSummaryMapper implements YukonRowMapper<MeterProgramSummaryDetail> {
        private MessageSourceAccessor accessor;

        MeterProgramSummaryMapper(YukonUserContext context) {
            accessor = messageSourceResolver.getMessageSourceAccessor(context);
        }

        @Override
        public MeterProgramSummaryDetail mapRow(YukonResultSet rs) throws SQLException {
            MeterProgramSummaryDetail row = new MeterProgramSummaryDetail();
            MeterProgramSource source = MeterProgramSource.getByPrefix(rs.getString("Source"));
            row.setAssignedProgramName(rs.getStringSafe("AssignedProgramName"));
            row.setAssignedGuid(rs.getStringSafe("AssignedGuid"));
            MeterProgramInfo programInfo = new MeterProgramInfo();
            programInfo.setSource(source);
            programInfo.setGuid(rs.getStringSafe("ReportedGuid"));
            String name = rs.getStringSafe("ReportedProgramName");
            if (!StringUtils.isEmpty(name)) {
                programInfo.setName(name);
            } else {
                programInfo.setName(accessor.getMessage(source.getFormatKey()));
            }
            row.setProgramInfo(programInfo);
            String[] parts = rs.getStringSafe("Status").split("/");
            ProgrammingStatus status = ProgrammingStatus.valueOf(parts[0]);
            row.setStatus(DisplayableStatus.getDisplayableStatus(status));
            if (status == ProgrammingStatus.FAILED && parts.length > 1) {
                row.setError(DeviceError.valueOf(parts[1]));
            }
            row.setDevice(new DisplayableDevice(rs.getPaoIdentifier("DeviceId", "Type"), rs.getString("DeviceName")));
            row.setMeterNumber(rs.getStringSafe("MeterNumber"));
            row.setLastUpdate(rs.getInstant("LastUpdate"));
            return row;
        }
    }

    @Override
    public MeterProgramSummaryDetail getProgramConfigurationByDeviceId(int deviceId, YukonUserContext context) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT mps.ReportedGuid as ReportedGuid, mpReported.Name as ReportedProgramName, "
                   + " mpAssigned.Guid as AssignedGuid, mpAssigned.Name as AssignedProgramName, "
                   + " PaoName as DeviceName, Type, MeterNumber, LastUpdate, mps.Source, mps.Status, mps.DeviceId");
        sql.append("FROM MeterProgramStatus mps");
        sql.append("LEFT JOIN MeterProgramAssignment mpa ON mps.DeviceId = mpa.DeviceId");
        sql.append("LEFT JOIN MeterProgram mpReported ON mpReported.Guid = mps.ReportedGuid");
        sql.append("LEFT JOIN MeterProgram mpAssigned ON mpAssigned.Guid = mpa.Guid");
        sql.append("JOIN YukonPAObject ypo ON mps.DeviceId = ypo.PAObjectID");
        sql.append("LEFT JOIN DeviceMeterGroup dmg ON mps.DeviceId = dmg.DeviceId");
        sql.append("WHERE mps.DeviceID").eq(deviceId);

        try {
            return jdbcTemplate.queryForObject(sql, new MeterProgramSummaryMapper(context));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Program configuration for device id:" + deviceId, e);
        }
    }
    
    @Override
    public List<MeterProgramStatistics> getProgramStatistics(YukonUserContext context) {
        List<ProgrammingStatus> inProgressAndConfirming = new ArrayList<>();
        inProgressAndConfirming.addAll(DisplayableStatus.IN_PROGRESS.getProgramStatuses());
        inProgressAndConfirming.addAll(DisplayableStatus.CONFIRMING.getProgramStatuses());
        SqlStatementBuilder sql = new SqlStatementBuilder();        
        sql.append("SELECT mp.Guid, Source, Name, SUM(reported.Total) Total, SUM(assigned.InProgress) InProgress, SUM(failure.Failed) Failed");
        sql.append("FROM MeterProgram mp");
        sql.append("FULL JOIN (");
        sql.append("    SELECT ReportedGuid, Source, count(deviceid) Total");
        sql.append("    FROM MeterProgramStatus ");
        sql.append("    GROUP BY ReportedGuid, Source) reported");
        sql.append("    ON mp.Guid=reported.ReportedGuid");
        sql.append("FULL JOIN (");
        sql.append("    SELECT Guid, count(mps.deviceid) InProgress");
        sql.append("    FROM MeterProgramAssignment mpa JOIN MeterProgramStatus mps on mpa.DeviceId=mps.DeviceId");
        sql.append("    WHERE mps.Status").in_k(inProgressAndConfirming);
        sql.append("    GROUP BY Guid) assigned");
        sql.append("    ON mp.Guid=assigned.Guid");
        sql.append("FULL JOIN (");
        sql.append("    SELECT Guid, count(mps.deviceid) Failed");
        sql.append("    FROM MeterProgramAssignment mpa JOIN MeterProgramStatus mps on mpa.DeviceId=mps.DeviceId");
        sql.append("    WHERE (mps.Status").in_k(Sets.newHashSet(ProgrammingStatus.CANCELED, ProgrammingStatus.MISMATCHED)).append(" OR Status LIKE 'FAILED%')");
        sql.append("    GROUP BY Guid) failure");
        sql.append("    ON mp.Guid=failure.Guid");
        sql.append("group by mp.Guid, Source, Name");
        List<MeterProgramStatistics> statistics = jdbcTemplate.query(sql, new MeterProgramStatisticsMapper(context));
        Collections.sort(statistics, (s1, s2) -> s1.getProgramInfo().getName().compareTo(s2.getProgramInfo().getName()));
        log.debug(statistics);
        return statistics;
    }
	
    @Override
    public List<MeterProgramInfo> getMeterProgramInfos(YukonUserContext context) {
        return getProgramStatistics(context).stream()
                .map(MeterProgramStatistics::getProgramInfo)
                .collect(Collectors.toList());
    }
	
    @Override
    public SearchResults<MeterProgramSummaryDetail> getSummary(MeterProgrammingSummaryFilter filter, PagingParameters paging, SortBy sortBy,
            Direction direction, YukonUserContext context) {
        log.debug("Getting summary for filter {}", filter);
        SqlStatementBuilder allRowsSql = buildDetailSelect(filter, sortBy, direction, false, context);
        if(allRowsSql == null) {
            // in some cases we do not want to run sql (Insufficient meter hardware and and buttons other then failed)
            SearchResults<MeterProgramSummaryDetail> searchResult = new SearchResults<>();
            searchResult.setBounds(paging.getStartIndex(), paging.getItemsPerPage(), 0);
            searchResult.setResultList(new ArrayList<>());
            return searchResult;
        }
        
        log.debug(allRowsSql.getDebugSql());
        SqlStatementBuilder countSql = buildDetailSelect(filter, null, null, true, context);
        int totalCount = jdbcTemplate.queryForInt(countSql);

        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();

        var rse = new PagingResultSetExtractor<>(start, count, new MeterProgramSummaryMapper(context));
        jdbcTemplate.query(allRowsSql, rse);

        SearchResults<MeterProgramSummaryDetail> searchResult = new SearchResults<>();
        searchResult.setBounds(paging.getStartIndex(), paging.getItemsPerPage(), totalCount);
        searchResult.setResultList(rse.getResultList());

        return searchResult;
    }

    private SqlStatementBuilder buildDetailSelect(MeterProgrammingSummaryFilter filter, SortBy sortBy, Direction direction,
            boolean selectCount, YukonUserContext context) {
        
        log.debug("Filter {}", filter);
        
        List<MeterProgramInfo> programs = coalesce(filter.getPrograms(), () -> getMeterProgramInfos(context));
        List<String> guids = programs.stream()
                .filter(program -> StringUtils.isNotBlank(program.getGuid()))
                .map(MeterProgramInfo::getGuid)
                .collect(Collectors.toList());
        Set<MeterProgramSource> sources = programs.stream()
                .filter(program -> StringUtils.isBlank(program.getGuid()))
                .map(program -> program.getSource())
                .collect(Collectors.toSet());       
        SqlStatementBuilder selectFrom = getSelect(selectCount);
        
        Set<MeterProgramSource> failedSources = sources.stream()
                                                       .filter(source -> source.isFailure())
                                                       .collect(Collectors.toSet());
                                                      
        List<SqlStatementBuilder> fragments = new ArrayList<>();
        if (sources.removeAll(failedSources)) {
            if (filter.getStatuses().contains(DisplayableStatus.FAILURE)) {
                var fragment = new SqlStatementBuilder();
                fragment.append(selectFrom.toString());
                fragment.append("WHERE Source").in_k(failedSources);
                addSelectCountGroupBy(selectCount, fragment);
                fragments.add(fragment);
            }
        }
        
        if(!guids.isEmpty() || !sources.isEmpty()) {
            if (filter.getStatuses().contains(DisplayableStatus.PROGRAMMED)) {
                var fragment = new SqlStatementBuilder();
                fragment.append(selectFrom);
                fragment.append("WHERE");
                if (!guids.isEmpty() && !sources.isEmpty()) {
                    fragment.append("(");
                    fragment.append("ReportedGuid").in(guids);
                    fragment.append("OR");
                    appendSourceFragment(sources, fragment);
                    fragment.append(")");
      
                }
                else if (!sources.isEmpty()) {
                    fragment.append("(");
                    appendSourceFragment(sources, fragment);
                    fragment.append(")");
                }
                else if (!guids.isEmpty()) {
                    fragment.append("ReportedGuid").in(guids);
                }
                addSelectCountGroupBy(selectCount, fragment);
                fragments.add(fragment);
            }

            List<ProgrammingStatus> inProgressOrConfirming = getInProgressOrConfirmingStatuses(filter);
            if (!inProgressOrConfirming.isEmpty() && !guids.isEmpty()) {
                var fragment = new SqlStatementBuilder();
                fragment.append(selectFrom);
                fragment.append("WHERE AssignedGuid").in(guids).append("AND Status").in_k(inProgressOrConfirming);
                addSelectCountGroupBy(selectCount, fragment);
                fragments.add(fragment);
            }

            if (filter.getStatuses().contains(DisplayableStatus.FAILURE)) {
                var fragment = new SqlStatementBuilder();
                fragment.append(selectFrom);
                fragment.append("WHERE");
                if (!guids.isEmpty() && !sources.isEmpty()) {
                    fragment.append("(");
                    fragment.append("AssignedGuid").in(guids);
                    fragment.append("OR");
                    appendSourceFragment(sources, fragment);
                    fragment.append(")");
                }
                else if (!sources.isEmpty()) {
                    fragment.append("(");
                    appendSourceFragment(sources, fragment);
                    fragment.append(")");
                }
                else if (!guids.isEmpty()) {
                    fragment.append("AssignedGuid").in(guids);
                }
                fragment.append("AND (Status")
                        .in_k(Sets.newHashSet(ProgrammingStatus.CANCELED, ProgrammingStatus.MISMATCHED))
                        .append(" OR Status LIKE 'FAILED%')");
                addSelectCountGroupBy(selectCount, fragment);
                fragments.add(fragment);
            }
        }
        
        log.debug("Fragments {}", Lists.transform(fragments, SqlStatementBuilder::getDebugSql));

        return fragments.stream()
                //  Join with a UNION if there is more than one fragment
                .collect(Collectors.reducing((sb1, sb2) -> sb1.append("UNION").appendFragment(sb2)))
                .map(fragment -> {
                    SqlStatementBuilder combinedSql = getSqlWithProgrammingCte(sources, filter, context);
                    if(selectCount) {
                        combinedSql.append("SELECT SUM(total)").append("FROM").append("(");
                    }
                    combinedSql.append(fragment);
                    if (sortBy != null) {
                        combinedSql.append("ORDER BY");
                        combinedSql.append(sortBy.getDbString());
                        combinedSql.append(direction);
                    }
                    
                    if(selectCount) {
                        combinedSql.append(") x");
                    }
                    return combinedSql;
                })
                .orElse(null);
    }

    private void appendSourceFragment(Set<MeterProgramSource> sources, SqlStatementBuilder fragment) {
        fragment.append("(Source").in_k(sources);
        fragment.append("AND ProgramForReportedGuid IS NULL)");
    }

    private void addSelectCountGroupBy(boolean selectCount, SqlStatementBuilder sql) {
        if(selectCount) {
            sql.append("GROUP BY DeviceId");
        }
    }

    private List<ProgrammingStatus> getInProgressOrConfirmingStatuses(MeterProgrammingSummaryFilter filter) {
        return Stream.of(DisplayableStatus.IN_PROGRESS, DisplayableStatus.CONFIRMING)
                .filter(s -> filter.getStatuses().contains(s))
                .flatMap(s -> s.getProgramStatuses().stream())
                .collect(Collectors.toList());
    }

    private SqlStatementBuilder getSelect(boolean selectCount) {
        SqlStatementBuilder sqlFrom = new SqlStatementBuilder();
        if(selectCount) {
            sqlFrom.append("SELECT DeviceId, count(DeviceId) as total");
        } else {
            sqlFrom.append("SELECT ReportedProgramName , AssignedProgramName, DeviceId, LastUpdate, Source, Status, DeviceName, Type, MeterNumber, AssignedGuid, ReportedGuid, ProgramForReportedGuid");
        }
        sqlFrom.append("FROM ProgrammingCTE");
        return sqlFrom;
    }

    private SqlStatementBuilder getSqlWithProgrammingCte(Set<MeterProgramSource> sources, MeterProgrammingSummaryFilter filter,
            YukonUserContext context) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        Map<MeterProgramSource, String> translatedSources = Maps.asMap(sources, source -> accessor.getMessage(source.getFormatKey()));
        sql.append("WITH ProgrammingCTE (ReportedProgramName , AssignedProgramName, DeviceId, LastUpdate, Source, Status, DeviceName, Type, MeterNumber, AssignedGuid, ReportedGuid, ProgramForReportedGuid");
        sql.append(") AS (");
            if(translatedSources.isEmpty()) {
                sql.append("SELECT mpReported.Name as ReportedProgramName");
            } else {
                sql.append("SELECT");
                sql.append("    CASE");
                sql.append("        WHEN mpReported.Name IS NULL THEN");
                sql.append("            CASE");
                translatedSources.forEach((source, translated) -> sql.append("WHEN Source").eq(source)
                                          .append("THEN").appendArgument(translated));
                sql.append("            END");
                sql.append("        ELSE mpReported.Name");
                sql.append("    END as ReportedProgramName");
            }
            sql.append(", mpAssigned.name as AssignedProgramName, mps.DeviceId, LastUpdate, Source, Status, PaoName as DeviceName, Type, MeterNumber, mpa.Guid as AssignedGuid, ReportedGuid, mpReported.Guid as ProgramForReportedGuid");
        
        sql.append("FROM MeterProgramStatus mps FULL join MeterProgramAssignment mpa ON mps.DeviceId = mpa.DeviceId");
        sql.append("FULL JOIN MeterProgram mpReported ON mpReported.Guid = mps.ReportedGuid");
        sql.append("FULL JOIN MeterProgram mpAssigned ON mpAssigned.Guid = mpa.Guid");
        sql.append("JOIN YukonPAObject ypo ON mps.DeviceId = ypo.PAObjectID");
        sql.append("LEFT JOIN DeviceMeterGroup dmg ON mps.DeviceId = dmg.DeviceId");
        if (!CollectionUtils.isEmpty(filter.getGroups())) {
            sql.append("WHERE").appendFragment(deviceGroupService.getDeviceGroupSqlWhereClause(filter.getGroups(), "mps.DeviceId"));
        }
        sql.append(")");
        return sql;
    }

    private <T> List<T> coalesce(List<T> items, Supplier<List<T>> defaults) {
        return Optional.of(items)
                    .filter(CollectionUtils::isNotEmpty)
                    .orElseGet(defaults);
    }
}

