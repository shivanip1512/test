package com.cannontech.common.device.programming.dao.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.programming.dao.MeterProgrammingDao;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.SqlStatementBuilder.SqlBatchUpdater;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.google.common.collect.Lists;

public class MeterProgrammingDaoImpl implements MeterProgrammingDao {
	@Autowired private YukonJdbcTemplate jdbcTemplate;
	
	private YukonRowMapper<MeterProgram> programRowMapper = rs -> {
		MeterProgram row = new MeterProgram();
		row.setGuid(rs.getString("Guid"));
		row.setName(rs.getString("Name"));
		row.setPaoType(rs.getEnum("PaoType", PaoType.class));
		//we are not getting the program as we will not display it to the user
		return row;
	};
	
	@Override
	public void saveMeterProgram(MeterProgram program) {
		try {
			SqlStatementBuilder sql = new SqlStatementBuilder();
			SqlParameterSink params = sql.insertInto("MeterProgram");
			params.addValue("Guid", UUID.randomUUID().toString());
			params.addValue("Name", program.getName());
			params.addValue("PaoType", program.getPaoType());
			params.addValue("Program", Base64.decodeBase64(program.getProgram()));
			jdbcTemplate.update(sql);
		} catch (DataIntegrityViolationException e) {
			throw new DuplicateException("Unable to save program as the name must be unique.", e);
		}
	}
	
	@Override
	public MeterProgram getMeterProgram(String guid) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT Guid, Name, PaoType");
		sql.append("FROM  MeterProgram");
		sql.append("WHERE Guid").eq(guid);
		return jdbcTemplate.queryForObject(sql, programRowMapper);
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
		sql.append("FROM  MeterProgram program JOIN MeterProgramAssignment assignment ON program.Guid = assignment.Guid");
		sql.append("WHERE DeviceID").eq(deviceId);
        return jdbcTemplate.queryForObject(sql, programRowMapper);
    }
    
    @Override
    public void assignDevicesToProgram(String Guid, List<SimpleDevice> devices) {
        if(!devices.isEmpty()) {
        	SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlBatchUpdater updater = sql.batchInsertInto("MeterProgramAssignment");
            updater.columns("Guid", "DeviceID");
            updater.deleteBeforeInsertByColumn("Guid");
            List<List<Object>> values = devices.stream().map(device -> {
                List<Object> row = Lists.newArrayList(Guid, device.getDeviceId());
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
    public void deleteMeterProgram(String guid) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM MeterProgram");
        sql.append("WHERE Guid").eq(guid);
        jdbcTemplate.update(sql);
    }
}
