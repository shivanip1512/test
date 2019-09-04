package com.cannontech.web.tools.device.programming.dao.impl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.programming.model.MeterProgramSource;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.device.programming.dao.MeterProgrammingSummaryDao;
import com.cannontech.web.tools.device.programming.model.MeterProgramInfo;
import com.cannontech.web.tools.device.programming.model.MeterProgramStatistics;
import com.cannontech.web.tools.device.programming.model.MeterProgramWidgetDisplay;
import com.cannontech.web.tools.device.programming.model.MeterProgrammingSummaryFilter.DisplayableStatus;

public class MeterProgrammingSummaryDaoImpl implements MeterProgrammingSummaryDao{
	
    private static final Logger log = YukonLogManager.getLogger(MeterProgrammingSummaryDaoImpl.class);
	@Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
	
	private static final YukonRowMapper<MeterProgramWidgetDisplay> configurationRowMapper = rs -> {
		MeterProgramWidgetDisplay row = new MeterProgramWidgetDisplay();
		MeterProgramSource source = MeterProgramSource.getByPrefix(rs.getString("Source"));
		MeterProgramInfo programInfo = new MeterProgramInfo();
	
		programInfo.setSource(source);

		String programGuid = rs.getStringSafe("ProgramGuid");
		String reportedGuid = rs.getStringSafe("ReportedGuid");
		String name = rs.getStringSafe("Name");

		if (source.isYukon() && programGuid != null && reportedGuid != null && programGuid.equals(reportedGuid)
				&& name != null) {
			programInfo.setName(name);
			programInfo.setGuid(reportedGuid);
		}

		if (source.isNotYukon()) {
			try {
				UUID guid = UUID.fromString(reportedGuid);
				if (guid.version() == 1) {
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
			programInfo.setGuid(rs.getString("ReportedGuid"));
		}
		row.setProgramInfo(programInfo);
		row.setDeviceTotal(rs.getInt("Total"));
		row.setInProgressTotal(rs.getInt("InProgress"));
		log.debug("Created {} ", row);
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
			programInfo.setName(accessor.getMessage(programInfo.getSource()));
		}
	}
	
	@Override
	public List<MeterProgramStatistics> getProgramStatistics(YukonUserContext context) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT Name, ReportedGuid, Source,");
		sql.append("count(*) Total,");
		sql.append("sum(CASE WHEN Status").in_k(DisplayableStatus.IN_PROGRESS.getProgramStatuses()).append("THEN 1 ELSE 0 END) InProgress");
		sql.append("FROM MeterProgramStatus status JOIN MeterProgram program ON program.Guid = status.ReportedGuid");
		sql.append("GROUP BY ReportedGuid, Name, Source");
		sql.append("UNION");
		sql.append("SELECT NULL, NULL, Source,");
		sql.append("count(*) Total,");
		sql.append("sum(CASE WHEN Status").in_k(DisplayableStatus.IN_PROGRESS.getProgramStatuses()).append("THEN 1 ELSE 0 END) InProgress");
		sql.append("FROM MeterProgramStatus");
		sql.append("WHERE ReportedGuid NOT IN (SELECT Guid FROM MeterProgram)");
		sql.append("GROUP BY Source");
		List<MeterProgramStatistics> statistics = jdbcTemplate.query(sql, statisticsMapper);
		statistics.forEach(statictic -> populateProgramNameForUnknownPrograms(context, statictic.getProgramInfo()));
		Collections.sort(statistics, (s1, s2) -> s1.getProgramInfo().getName().compareTo(s2.getProgramInfo().getName()));
		return statistics;
	}
	
	@Override
	public List<MeterProgramInfo> getMeterProgramInfos(YukonUserContext context) {
		return getProgramStatistics(context).stream()
				.map(MeterProgramStatistics::getProgramInfo)
				.collect(Collectors.toList());
	}
}

