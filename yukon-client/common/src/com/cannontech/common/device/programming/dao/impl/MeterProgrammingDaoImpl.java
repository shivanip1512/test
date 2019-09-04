package com.cannontech.common.device.programming.dao.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.programming.dao.MeterProgrammingDao;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.device.programming.model.MeterProgramSource;
import com.cannontech.common.device.programming.model.MeterProgramStatus;
import com.cannontech.common.device.programming.model.ProgramStatus;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.SqlStatementBuilder.SqlBatchUpdater;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.google.common.collect.Lists;

public class MeterProgrammingDaoImpl implements MeterProgrammingDao {
	private final static Logger log = YukonLogManager.getLogger(MeterProgrammingDaoImpl.class);
	@Autowired
	private YukonJdbcTemplate jdbcTemplate;

	private final YukonRowMapper<MeterProgramStatus> programStatusRowMapper = rs -> {
		MeterProgramStatus row = new MeterProgramStatus();
		row.setDeviceId(rs.getInt("DeviceId"));
		row.setReportedGuid(UUID.fromString(rs.getStringSafe("ReportedGuid")));
		row.setLastUpdate(rs.getInstant("Name"));
		row.setSource(MeterProgramSource.getByPrefix(rs.getStringSafe("Source")));
		try {
			row.setStatus(rs.getEnum("MeterProgramStatus", ProgramStatus.class));
		} catch (Exception e) {
			// The value contains STATUS/ERROR
			String[] parts = rs.getStringSafe("MeterProgramStatus").split("/");
			row.setStatus(ProgramStatus.valueOf(parts[0]));
			row.setError(DeviceError.valueOf(parts[1]));
		}
		return row;
	};

	private final YukonRowMapper<MeterProgram> programRowMapper = rs -> {
		MeterProgram row = new MeterProgram();
		row.setGuid(UUID.fromString(rs.getStringSafe("Guid")));
		row.setName(rs.getStringSafe("Name"));
		row.setPaoType(rs.getEnum("PaoType", PaoType.class));
		// we are not getting the program as we will not display it to the user
		return row;
	};

	/*private final YukonRowMapper<MeterProgramConfiguration> configurationRowMapper = rs -> {
		MeterProgramConfiguration row = new MeterProgramConfiguration();
		MeterProgramSource source = MeterProgramSource.getByPrefix(rs.getString("Source"));

		row.setSource(source);

		String programGuid = rs.getStringSafe("ProgramGuid");
		String reportedGuid = rs.getStringSafe("ReportedGuid");
		String name = rs.getStringSafe("Name");

		if (source.isYukon() && programGuid.equals(reportedGuid) && !name.isEmpty()) {
			row.setName(name);
		}

		if (source.isNotYukon()) {
			try {
				UUID guid = UUID.fromString(reportedGuid);
				if (guid.version() == 1) {
					row.setTimestamp(new Instant(guid.timestamp()));
				}
			} catch (Exception e) {
				Log.error("Unable to parse time for none-Yukon guid:" + reportedGuid);
			}
		}

		row.setGuid(reportedGuid);
		row.setDeviceId(rs.getInt("DeviceId"));
		log.debug("Created configuration {} ", row);
		return row;
	};*/

	@Override
	public UUID saveMeterProgram(MeterProgram program) {
		try {
			UUID uuid = UUID.randomUUID();
			SqlStatementBuilder sql = new SqlStatementBuilder();
			SqlParameterSink params = sql.insertInto("MeterProgram");
			params.addValue("Guid", uuid.toString());
			params.addValue("Name", program.getName());
			params.addValue("PaoType", program.getPaoType());
			params.addValue("Program", program.getProgram());
			jdbcTemplate.update(sql);
			return uuid;
		} catch (DataIntegrityViolationException e) {
			throw new DuplicateException("Unable to save program as the name must be unique.", e);
		}
	}

	@Override
	public void updateMeterProgramStatus(MeterProgramStatus status) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		SqlParameterSink params = sql.update("MeterProgramStatus");
		addMeterProgramStatusFields(status, params);
		sql.append("WHERE deviceId").eq(status.getDeviceId());
		jdbcTemplate.update(sql);
	}

	@Override
	public void createMeterProgramStatus(MeterProgramStatus status) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		SqlParameterSink params = sql.insertInto("MeterProgramStatus");
		params.addValue("DeviceId", status.getDeviceId());
		addMeterProgramStatusFields(status, params);
		jdbcTemplate.update(sql);
	}

	private void addMeterProgramStatusFields(MeterProgramStatus status, SqlParameterSink params) {
		params.addValue("ReportedGuid", status.getReportedGuid().toString());
		params.addValue("Source", status.getSource().getPrefix());
		if (status.getStatus() == ProgramStatus.FAILED && status.getError() != null) {
			params.addValue("Status", status.getStatus() + "/" + status.getError().name());
		} else {
			params.addValue("Status", status.getStatus());
		}
		params.addValue("LastUpdate", status.getLastUpdate());
	}

	@Override
	public MeterProgramStatus getMeterProgramStatus(int deviceId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT DeviceId, ReportedGuid, LastUpdate, Source, Status");
		sql.append("FROM  MeterProgramStatus");
		sql.append("WHERE DeviceId").eq(deviceId);
		try {
			return jdbcTemplate.queryForObject(sql, programStatusRowMapper);
		} catch (EmptyResultDataAccessException e) {
			throw new NotFoundException("Meter Program Status not found deviceId:" + deviceId, e);
		}
	}

	@Override
	public MeterProgram getMeterProgram(UUID guid) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT Guid, Name, PaoType");
		sql.append("FROM  MeterProgram");
		sql.append("WHERE Guid").eq(guid.toString());
		try {
			return jdbcTemplate.queryForObject(sql, programRowMapper);
		} catch (EmptyResultDataAccessException e) {
			throw new NotFoundException("Meter Program not found guid:" + guid, e);
		}
	}

	@Override
	public List<MeterProgram> getAllMeterPrograms() {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT Guid, Name, PaoType");
		sql.append("FROM  MeterProgram");
		sql.append("ORDER BY Name Desc");
		return jdbcTemplate.query(sql, programRowMapper);
	}

	@Override
	public MeterProgram getProgramByDeviceId(int deviceId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT program.Guid, program.Name, program.PaoType");
		sql.append(
				"FROM  MeterProgram program JOIN MeterProgramAssignment assignment ON program.Guid = assignment.Guid");
		sql.append("WHERE DeviceID").eq(deviceId);
		try {
			return jdbcTemplate.queryForObject(sql, programRowMapper);
		} catch (EmptyResultDataAccessException e) {
			throw new NotFoundException("Meter Program not found :" + deviceId, e);
		}
	}

/*	@Override
	public MeterProgramConfiguration getProgramConfigurationByDeviceId(int deviceId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append(
				"SELECT program.Guid as ProgramGuid, status.ReportedGuid as ReportedGuid, program.Name, status.Source, status.deviceId");
		sql.append(
				"FROM  MeterProgram program FULL JOIN MeterProgramStatus status ON program.Guid = status.reportedGuid");
		sql.append("WHERE status.DeviceID").eq(deviceId);
		try {
			return jdbcTemplate.queryForObject(sql, configurationRowMapper);
		} catch (EmptyResultDataAccessException e) {
			throw new NotFoundException("Program configuration for device id:" + deviceId, e);
		}
	}*/

	@Override
	public void assignDevicesToProgram(UUID guid, List<SimpleDevice> devices) {
		if (!devices.isEmpty()) {
			SqlStatementBuilder sql = new SqlStatementBuilder();
			SqlBatchUpdater updater = sql.batchInsertInto("MeterProgramAssignment");
			updater.columns("Guid", "DeviceID");
			updater.deleteBeforeInsertByColumn("DeviceID");
			List<List<Object>> values = devices.stream().map(device -> {
				List<Object> row = Lists.newArrayList(guid.toString(), device.getDeviceId());
				return row;
			}).collect(Collectors.toList());
			updater.values(values);
			jdbcTemplate.yukonBatchUpdate(sql);
		}
	}

	@Override
	public void unassignDeviceFromProgram(int deviceId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("DELETE FROM MeterProgramAssignment");
		sql.append("WHERE DeviceID").eq(deviceId);
		jdbcTemplate.update(sql);
	}

	@Override
	public void deleteMeterProgram(UUID guid) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("DELETE FROM MeterProgram");
		sql.append("WHERE Guid").eq(guid);
		jdbcTemplate.update(sql);
	}
}
