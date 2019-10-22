package com.cannontech.web.tools.device.programming.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
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
import com.cannontech.database.YukonRowMapper;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.device.programming.dao.MeterProgrammingSummaryDao;
import com.cannontech.web.tools.device.programming.model.MeterProgramInfo;
import com.cannontech.web.tools.device.programming.model.MeterProgramStatistics;
import com.cannontech.web.tools.device.programming.model.MeterProgramSummaryDetail;
import com.cannontech.web.tools.device.programming.model.MeterProgramWidgetDisplay;
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

    private static final YukonRowMapper<MeterProgramWidgetDisplay> configurationRowMapper = rs -> {
        MeterProgramWidgetDisplay row = new MeterProgramWidgetDisplay();
        MeterProgramSource source = MeterProgramSource.getByPrefix(rs.getString("Source"));
        MeterProgramInfo programInfo = new MeterProgramInfo();

        programInfo.setSource(source);

        String programGuid = rs.getStringSafe("ProgramGuid");
        String reportedGuid = rs.getStringSafe("ReportedGuid");
        String name = rs.getStringSafe("Name");

        if (source.isYukon() && programGuid != null && reportedGuid != null && programGuid.equals(reportedGuid) && name != null) {
            programInfo.setName(name);
            programInfo.setGuid(reportedGuid);
        }

        if (source.isNotYukon()) {
            try {
                UUID guid = UUID.fromString(reportedGuid);
                if (guid.version() == 1) {
                    //The timestamp to be displayed in the widget for the Optical, etc GUIDs is from 
                    //the GUID, and not related to LastUpdated (which could be the timestamp of a subsequent failure attempt).
                    row.setTimestamp(new Instant(guid.timestamp()));
                }
            } catch (Exception e) {
                log.error("Unable to parse time for none-Yukon guid:" + reportedGuid);
            }
        }

        row.setDeviceId(rs.getInt("DeviceId"));
        row.setProgramInfo(programInfo);
        //log.debug("Created {} ", row);
        return row;
    };
    
    private static final YukonRowMapper<MeterProgramStatistics> statisticsMapper = rs -> {
        MeterProgramStatistics row = new MeterProgramStatistics();
        MeterProgramSource source = MeterProgramSource.getByPrefix(rs.getString("Source"));
        MeterProgramInfo programInfo = new MeterProgramInfo();
        programInfo.setSource(source);
        String name = rs.getStringSafe("Name");
        if (name != null) {
            programInfo.setName(name);
            programInfo.setGuid(rs.getString("Guid"));
        }
        row.setProgramInfo(programInfo);
        row.setDeviceTotal(rs.getInt("Total"));
        row.setInProgressTotal(rs.getInt("InProgress"));
       // log.debug("Created {} ", row);
        return row;
    };
    
    private static final YukonRowMapper<MeterProgramSummaryDetail> programSummaryDetail = rs -> {
        MeterProgramSummaryDetail row = new MeterProgramSummaryDetail();
        MeterProgramSource source = MeterProgramSource.getByPrefix(rs.getString("Source"));
        MeterProgramInfo programInfo = new MeterProgramInfo();
        programInfo.setSource(source);
        programInfo.setGuid(rs.getStringSafe("ReportedGuid"));    
        programInfo.setName(rs.getStringSafe("ReportedProgramName"));
        row.setProgramInfo(programInfo);
        row.setAssignedProgramName(rs.getStringSafe("AssignedProgramName"));
        row.setAssignedGuid(rs.getStringSafe("AssignedGuid"));

        String[] parts = rs.getStringSafe("Status").split("/");
        ProgrammingStatus status = ProgrammingStatus.valueOf(parts[0]);
        row.setStatus(DisplayableStatus.getDisplayableStatus(status));
        if (status == ProgrammingStatus.FAILED && parts.length > 1) {
            row.setError(DeviceError.valueOf(parts[1]));
        }
        row.setDevice(new DisplayableDevice(rs.getPaoIdentifier("DeviceId", "Type"), rs.getString("DeviceName")));
        row.setMeterNumber(rs.getStringSafe("MeterNumber"));
        row.setLastUpdate(rs.getInstant("LastUpdate"));
       // log.debug("row {}", row);
        return row;
    };

    @Override
    public MeterProgramWidgetDisplay getProgramConfigurationByDeviceId(int deviceId, YukonUserContext context) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT program.Guid as ProgramGuid, status.ReportedGuid as ReportedGuid, program.Name, status.Source, status.deviceId");
        sql.append("FROM  MeterProgram program FULL JOIN MeterProgramStatus status ON program.Guid = status.reportedGuid");
        sql.append("WHERE status.DeviceID").eq(deviceId);
        try {
            MeterProgramWidgetDisplay program = jdbcTemplate.queryForObject(sql, configurationRowMapper);
            populateProgramNameForUnknownPrograms(context, program.getProgramInfo());
            return program;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Program configuration for device id:" + deviceId, e);
        }
    }

    private void populateProgramNameForUnknownPrograms(YukonUserContext context, MeterProgramInfo programInfo) {
        if (programInfo.getName() == null) {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
            programInfo.setName(accessor.getMessage(programInfo.getSource().getFormatKey()));
        }
    }

    @Override
    public List<MeterProgramStatistics> getProgramStatistics(YukonUserContext context) {
        List<ProgrammingStatus> inProgressAndConfirming = new ArrayList<>();
        inProgressAndConfirming.addAll(DisplayableStatus.IN_PROGRESS.getProgramStatuses());
        inProgressAndConfirming.addAll(DisplayableStatus.CONFIRMING.getProgramStatuses());
        SqlStatementBuilder sql = new SqlStatementBuilder();        
        sql.append("SELECT mp.Guid, Source, Name, SUM(reported.Total) Total, SUM(assigned.InProgress) InProgress");
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
        sql.append("group by mp.Guid, Source, Name");
        List<MeterProgramStatistics> statistics = jdbcTemplate.query(sql, statisticsMapper);
        statistics.forEach(statistic -> populateProgramNameForUnknownPrograms(context, statistic.getProgramInfo()));
        Collections.sort(statistics, (s1, s2) -> s1.getProgramInfo().getName().compareTo(s2.getProgramInfo().getName()));
        log.debug(statistics);
        return statistics;
    }
	
    @Override
    public List<MeterProgramInfo> getMeterProgramInfos(YukonUserContext context) {
        return getProgramStatistics(context).stream()
                .filter(program -> !program.isUnused())
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

        var rse = new PagingResultSetExtractor<>(start, count, programSummaryDetail);
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
                    fragment.append("(ReportedGuid").in(guids).append("OR Source").in_k(sources).append(" AND AssignedGuid IS NULL)");
                }
                else if (!sources.isEmpty()) {
                    fragment.append("(Source").in_k(sources).append(" AND AssignedGuid IS NULL)");
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
                    fragment.append("(AssignedGuid").in(guids).append("OR Source").in_k(sources).append(")");
                }
                else if (!sources.isEmpty()) {
                    fragment.append("(Source").in_k(sources).append(" AND AssignedGuid IS NULL)");
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
                    SqlStatementBuilder combinedSql = getSqlWithProgrammingCte(sources, context);
                    if(selectCount) {
                        combinedSql.append("SELECT SUM(total)").append("FROM").append("(");
                    }
                    combinedSql.append(fragment);
                    if (!CollectionUtils.isEmpty(filter.getGroups())) {
                        combinedSql.append("AND").appendFragment(deviceGroupService.getDeviceGroupSqlWhereClause(filter.getGroups(), "DeviceId"));
                    }

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
            sqlFrom.append("SELECT ReportedProgramName , AssignedProgramName, DeviceId, LastUpdate, Source, Status, DeviceName, Type, MeterNumber, AssignedGuid, ReportedGuid");
        }
        sqlFrom.append("FROM ProgrammingCTE");
        return sqlFrom;
    }

    private SqlStatementBuilder getSqlWithProgrammingCte(Set<MeterProgramSource> sources, YukonUserContext context) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        Map<MeterProgramSource, String> translatedSources = Maps.asMap(sources, source -> accessor.getMessage(source.getFormatKey()));
        sql.append("WITH ProgrammingCTE (ReportedProgramName , AssignedProgramName, DeviceId, LastUpdate, Source, Status, DeviceName, Type, MeterNumber, AssignedGuid, ReportedGuid");
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
            sql.append(", mpAssigned.name as AssignedProgramName, mps.DeviceId, LastUpdate, Source, Status, PaoName as DeviceName, Type, MeterNumber, mpa.Guid as AssignedGuid, ReportedGuid");
        
        sql.append("FROM MeterProgramStatus mps FULL join MeterProgramAssignment mpa ON mps.DeviceId = mpa.DeviceId");
        sql.append("FULL JOIN MeterProgram mpReported ON mpReported.Guid = mps.ReportedGuid");
        sql.append("FULL JOIN MeterProgram mpAssigned ON mpAssigned.Guid = mpa.Guid");
        sql.append("JOIN YukonPAObject ypo ON mps.DeviceId = ypo.PAObjectID");
        sql.append("LEFT JOIN DeviceMeterGroup dmg ON mps.DeviceId = dmg.DeviceId");
        sql.append(")");
        return sql;
    }

    private <T> List<T> coalesce(List<T> items, Supplier<List<T>> defaults) {
        return Optional.of(items)
                    .filter(CollectionUtils::isNotEmpty)
                    .orElseGet(defaults);
    }
}

