package com.cannontech.web.tools.device.programming.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

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
        log.debug("Created {} ", row);
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
        log.debug("Created {} ", row);
        return row;
    };
    
    private static final YukonRowMapper<MeterProgramSummaryDetail> programSummaryDetail = rs -> {
        MeterProgramSummaryDetail row = new MeterProgramSummaryDetail();
        MeterProgramSource source = MeterProgramSource.getByPrefix(rs.getString("Source"));
        MeterProgramInfo programInfo = new MeterProgramInfo();
        programInfo.setSource(source);
        programInfo.setName(rs.getStringSafe("ProgramName"));
        programInfo.setGuid(rs.getStringSafe("Guid"));
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
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Name, ReportedGuid as Guid, Source,");
        sql.append("    COUNT(*) Total,");
        sql.append("    SUM(CASE WHEN Status").in_k(DisplayableStatus.IN_PROGRESS.getProgramStatuses()).append("THEN 1 ELSE 0 END) InProgress");
        sql.append("        FROM MeterProgramStatus status JOIN MeterProgram program ON program.Guid = status.ReportedGuid");
        sql.append("        GROUP BY ReportedGuid, Name, Source");
        sql.append("UNION");
        sql.append("SELECT NULL, NULL, Source,");
        sql.append("    COUNT(*) Total,");
        sql.append("    SUM(CASE WHEN Status").in_k(DisplayableStatus.IN_PROGRESS.getProgramStatuses()).append("THEN 1 ELSE 0 END) InProgress");
        sql.append("    FROM MeterProgramStatus");
        sql.append("        WHERE ReportedGuid NOT IN (SELECT Guid FROM MeterProgram)");
        sql.append("    GROUP BY Source");
        sql.append("UNION");
        sql.append("SELECT Name, Guid, NULL,");
        sql.append("    0 as Total,");
        sql.append("    0 as InProgress");
        sql.append("    FROM MeterProgram");
        sql.append("        WHERE Guid NOT IN (SELECT Guid FROM MeterProgramAssignment)");
        sql.append("    GROUP BY Name, Guid");
        List<MeterProgramStatistics> statistics = jdbcTemplate.query(sql, statisticsMapper);
        statistics.forEach(statistic -> populateProgramNameForUnknownPrograms(context, statistic.getProgramInfo()));
        Collections.sort(statistics, (s1, s2) -> s1.getProgramInfo().getName().compareTo(s2.getProgramInfo().getName()));
        return statistics;
    }
	
    @Override
    public List<MeterProgramInfo> getMeterProgramInfos(YukonUserContext context) {
        return getProgramStatistics(context).stream()
                .filter(program -> program.getDeviceTotal() > 0)
                .map(MeterProgramStatistics::getProgramInfo)
                .collect(Collectors.toList());
    }
	
    @Override
    public SearchResults<MeterProgramSummaryDetail> getSummary(MeterProgrammingSummaryFilter filter, PagingParameters paging, SortBy sortBy,
            Direction direction, YukonUserContext context) {
        log.debug("Getting summary for filter {}", filter);
        SqlStatementBuilder allRowsSql = buildDetailSelect(filter, sortBy, direction, false, context);
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
        List<DisplayableStatus> statuses = coalesce(filter.getStatuses(), () -> Arrays.asList(DisplayableStatus.values()));
        List<String> guids = programs.stream()
                .filter(program -> StringUtils.isNotBlank(program.getGuid()))
                .map(MeterProgramInfo::getGuid)
                .collect(Collectors.toList());
        Set<MeterProgramSource> sources = programs.stream()
                .filter(program -> StringUtils.isBlank(program.getGuid()))
                .map(program -> program.getSource())
                .collect(Collectors.toSet());
        Iterable<String> prefixes = Iterables.transform(sources, s -> s.getPrefix());
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        Map<MeterProgramSource, String> translatedSources = Maps.asMap(sources, source -> accessor.getMessage(source.getFormatKey()));

        List<ProgrammingStatus> programmingStatuses = new ArrayList<>();
        statuses.forEach(status -> programmingStatuses.addAll(status.getProgramStatuses()));
       
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (selectCount) {
            sql.append("SELECT count(status.DeviceId)");
        }else {
            if(translatedSources.isEmpty()) {
                sql.append("SELECT Name as ProgramName");
            } else {
                sql.append("SELECT");
                sql.append("    CASE");
                sql.append("        WHEN Name IS NULL THEN");
                sql.append("            CASE");
                translatedSources.forEach((source, translated) -> sql.append("WHEN Source").eq(source.getPrefix())
                                          .append("THEN").appendArgument(translated));
                sql.append("            END");
                sql.append("        ELSE Name");
                sql.append("    END as ProgramName");
            }
            sql.append(", status.DeviceId, LastUpdate, Source, Status, PaoName as DeviceName, Type, MeterNumber, program.Guid");
        }
        sql.append("FROM MeterProgramStatus status FULL JOIN MeterProgram program ON program.Guid = status.reportedGuid");
        sql.append("JOIN YukonPAObject ypo ON status.DeviceId = ypo.PAObjectID");
        sql.append("LEFT JOIN DeviceMeterGroup dmg ON status.DeviceId = dmg.DeviceId");
        if(guids != null && prefixes != null) {
            sql.append("WHERE (ReportedGuid").in(guids).append("OR").append("Source").in(prefixes).append(")");
        } else if (guids != null){
            sql.append("WHERE ReportedGuid").in(guids); 
        } else if (prefixes != null){
            sql.append("WHERE Source").in(prefixes); 
        }
        if(programmingStatuses.remove(ProgrammingStatus.FAILED) == false) {
            sql.append("AND status.Status").in_k(programmingStatuses);
        } else if(programmingStatuses.isEmpty()) {
            sql.append("AND status.Status LIKE 'FAILED%'");
        } else {
            sql.append("AND (status.Status LIKE 'FAILED%' OR status.Status").in_k(programmingStatuses).append(")");
        }
        if (!CollectionUtils.isEmpty(filter.getGroups())) {
            sql.append("AND").appendFragment(deviceGroupService.getDeviceGroupSqlWhereClause(filter.getGroups(), "status.DeviceId"));
        }
        if (sortBy != null) {
            sql.append("ORDER BY");
            sql.append(sortBy.getDbString());
            sql.append(direction);
        }
        
        log.debug("Debug Sql {}", sql.getDebugSql());
        
        return sql;
    }

    private <T> List<T> coalesce(List<T> items, Supplier<List<T>> defaults) {
        return Optional.of(items)
                    .filter(CollectionUtils::isNotEmpty)
                    .orElseGet(defaults);
    }
}

