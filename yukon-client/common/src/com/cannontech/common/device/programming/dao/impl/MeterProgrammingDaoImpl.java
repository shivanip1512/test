package com.cannontech.common.device.programming.dao.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.programming.dao.MeterProgrammingDao;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.device.programming.model.MeterProgramSource;
import com.cannontech.common.device.programming.model.MeterProgramStatus;
import com.cannontech.common.device.programming.model.ProgrammingStatus;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.SqlStatementBuilder.SqlBatchUpdater;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;
import com.google.common.collect.Lists;

public class MeterProgrammingDaoImpl implements MeterProgrammingDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private static final Set<ProgrammingStatus> nonRetriableStatusus = Set.of(ProgrammingStatus.IDLE,
                                                                              ProgrammingStatus.INITIATING,
                                                                              ProgrammingStatus.MISMATCHED,
                                                                              ProgrammingStatus.UPLOADING);
    
    private static final YukonRowMapper<MeterProgramStatus> programStatusRowMapper = rs -> {
        MeterProgramStatus row = new MeterProgramStatus();
        row.setDeviceId(rs.getInt("DeviceId"));
        row.setReportedGuid(UUID.fromString(rs.getStringSafe("ReportedGuid")));
        row.setLastUpdate(rs.getInstant("LastUpdate"));
        row.setSource(MeterProgramSource.getByPrefix(rs.getStringSafe("Source")));
        String[] parts = rs.getStringSafe("Status").split("/");
        var status = ProgrammingStatus.valueOf(parts[0]);
        row.setStatus(status);
        if (status == ProgrammingStatus.FAILED && parts.length > 1) {
            row.setError(DeviceError.valueOf(parts[1]));
        }
        return row;
    };

    private static final YukonRowMapper<MeterProgram> programRowMapper = rs -> {
        MeterProgram row = new MeterProgram();
        row.setGuid(UUID.fromString(rs.getStringSafe("Guid")));
        row.setName(rs.getStringSafe("Name"));
        row.setPaoType(rs.getEnum("PaoType", PaoType.class));
        // we are not getting the program as we will not display it to the user
        return row;
    };
    

    @Override
    public UUID saveMeterProgram(MeterProgram program) {
        try {
            AESPasswordBasedCrypto encrypter = new AESPasswordBasedCrypto(CryptoUtils.getSharedPasskey());
            String password =  encrypter.encryptToHexStr(program.getPassword());
            UUID uuid = UUID.randomUUID();
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.insertInto("MeterProgram");
            params.addValue("Guid", uuid.toString());
            params.addValue("Name", program.getName());
            params.addValue("PaoType", program.getPaoType());
            params.addValue("Program", program.getProgram());
            params.addValue("Password", password);
            jdbcTemplate.update(sql);
            return uuid;
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Unable to save program as the name must be unique.", e);
        } catch (Exception e) {
            throw new BadConfigurationException("Unable to encrypt password: " + program.getPassword(), e);
        }
    }
    
    @Override
    public void updateMeterProgramStatusToInitiating(int deviceId, long lastUpdate) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.update("MeterProgramStatus");
        params.addValue("LastUpdate", lastUpdate);
        params.addValue("Status", ProgrammingStatus.INITIATING);
        sql.append("WHERE deviceId").eq(deviceId);
        jdbcTemplate.update(sql);
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
        params.addValue("Source", status.getSource());
        if (status.getStatus() == ProgrammingStatus.FAILED && status.getError() != null) {
            params.addValue("Status", status.getStatus() + "/" + status.getError().name());
        } else {
            params.addValue("Status", status.getStatus());
        }
        params.addValue("LastUpdate", status.getLastUpdate());
    }

    @Override
    public List<SimpleDevice> getMetersWithOldFirmware(List<SimpleDevice> devices) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        
        return template.query(new SqlFragmentGenerator<SimpleDevice>() {
            @Override
            public SqlFragmentSource generate(List<SimpleDevice> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT PAObjectID, type");
                sql.append("FROM  MeterProgramStatus JOIN YukonPAObject ypo ON DeviceId = ypo.PAObjectID");
                sql.append("WHERE Source").eq_k(MeterProgramSource.OLD_FIRMWARE);
                sql.append("AND DeviceId").in(subList.stream()
                                              .map(SimpleDevice::getDeviceId)
                                              .collect(Collectors.toList()));
                return sql;
            }
        }, devices, new YukonDeviceRowMapper());
    }
    
    @Override
    public List<SimpleDevice> getMetersWithoutProgramStatus(List<SimpleDevice> devices) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        
        List<SimpleDevice> devicesWithStatus = template.query(new SqlFragmentGenerator<SimpleDevice>() {
            @Override
            public SqlFragmentSource generate(List<SimpleDevice> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT PAObjectID, type");
                sql.append("FROM  MeterProgramStatus JOIN YukonPAObject ypo ON DeviceId = ypo.PAObjectID");
                sql.append("AND DeviceId").in(subList.stream()
                                              .map(SimpleDevice::getDeviceId)
                                              .collect(Collectors.toList()));
                return sql;
            }
        }, devices, new YukonDeviceRowMapper());
        devices.removeAll(devicesWithStatus);
        return devices;
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
    public List<SimpleDevice> getAlreadyProgrammedMeters(List<SimpleDevice> devices, UUID guid) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        
        return template.query(new SqlFragmentGenerator<SimpleDevice>() {
            @Override
            public SqlFragmentSource generate(List<SimpleDevice> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT PAObjectID, type");
                sql.append("FROM MeterProgramStatus status JOIN YukonPAObject ypo ON status.DeviceId = ypo.PAObjectID");
                sql.append("JOIN MeterProgramAssignment assignment ON assignment.DeviceId = status.DeviceId");
                sql.append("AND status.DeviceId").in(subList.stream()
                                              .map(SimpleDevice::getDeviceId)
                                              .collect(Collectors.toList()));
                sql.append("AND assignment.Guid").eq(guid.toString());
                sql.append("AND status.Status").in(nonRetriableStatusus);
                Log.debug(sql);
                return sql;
            }
        }, devices, new YukonDeviceRowMapper());
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
    public boolean hasMeterProgram(UUID guid) {
        try {
            getMeterProgram(guid);
            return true;
        } catch (NotFoundException e) {
            return false;
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
        sql.append("FROM  MeterProgram program JOIN MeterProgramAssignment assignment ON program.Guid = assignment.Guid");
        sql.append("WHERE DeviceID").eq(deviceId);
        try {
            return jdbcTemplate.queryForObject(sql, programRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Meter Program not found :" + deviceId, e);
        }
    }

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
        sql.append("WHERE Guid").eq(guid.toString());
        jdbcTemplate.update(sql);
    }
}
