package com.cannontech.amr.rfn.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.impl.YukonPaoRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class RfnDeviceDaoImpl implements RfnDeviceDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PaoDao paoDao;

    @Override
    public RfnDevice getDeviceForExactIdentifier(RfnIdentifier rfnIdentifier) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.PAObjectID, ypo.Type");
        sql.append("from YukonPaObject ypo");
        sql.append(  "join Device d on ypo.PAObjectID = d.DeviceId");
        sql.append(  "join RfnAddress rfn on d.DeviceId = rfn.DeviceId");
        sql.append("where rfn.SerialNumber").eq(rfnIdentifier.getSensorSerialNumber());
        sql.append(  "and rfn.Manufacturer").eq(rfnIdentifier.getSensorManufacturer());
        sql.append(  "and rfn.Model").eq(rfnIdentifier.getSensorModel());
        
        try {
            PaoIdentifier pao = jdbcTemplate.queryForObject(sql, new YukonPaoRowMapper());
            RfnDevice rfnDevice = new RfnDevice(pao, rfnIdentifier);
            return rfnDevice;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("no device matches " + rfnIdentifier);
        }
    }
    
    @Override
    public RfnDevice getDeviceForId(int paoId) throws NotFoundException {
        return getDevice(paoDao.getYukonPao(paoId));
    }
    
    @Override
    public RfnDevice getDevice(YukonPao pao) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sql.append("from RfnAddress rfn");
        sql.append("where rfn.DeviceId").eq(pao.getPaoIdentifier().getPaoId());
        
        try {
            RfnIdentifier rfnIdentifier = jdbcTemplate.queryForObject(sql, new YukonRowMapper<RfnIdentifier>() {
                public RfnIdentifier mapRow(YukonResultSet rs) throws SQLException {
                    RfnIdentifier result = new RfnIdentifier(rs.getStringSafe("SerialNumber"), 
                                                             rs.getStringSafe("Manufacturer"), 
                                                             rs.getStringSafe("Model"));
                    return result;
                }
            });
            RfnDevice rfnDevice = new RfnDevice(pao, rfnIdentifier);
            return rfnDevice;
        } catch (EmptyResultDataAccessException e) {
            RfnDevice rfnDevice = new RfnDevice(pao, RfnIdentifier.createBlank());
            return rfnDevice;
        }
    }
    
    @Override
    public RfnMeter getMeterForId(int paoId) throws NotFoundException {
        return getMeter(paoDao.getYukonPao(paoId));
    }

    @Override
    public RfnMeter getMeter(final YukonPao pao) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select rfn.SerialNumber, rfn.Manufacturer, rfn.Model, dmg.MeterNumber");
        sql.append("from RfnAddress rfn");
        sql.append(  "join DeviceMeterGroup dmg on dmg.deviceId = rfn.DeviceId");
        sql.append("where rfn.DeviceId").eq(pao.getPaoIdentifier().getPaoId());
        
        try {
            RfnMeter rfnMeter = jdbcTemplate.queryForObject(sql, new YukonRowMapper<RfnMeter>() {
                public RfnMeter mapRow(YukonResultSet rs) throws SQLException {
                    RfnIdentifier rfnIdentifier = new RfnIdentifier(rs.getStringSafe("SerialNumber"), 
                                                                       rs.getStringSafe("Manufacturer"), 
                                                                       rs.getStringSafe("Model"));
                    RfnMeter rfnMeter = new RfnMeter(pao, rfnIdentifier);
                    rfnMeter.setMeterNumber(rs.getStringSafe("MeterNumber"));
                    return rfnMeter;
                }
            });
            return rfnMeter;
        } catch (EmptyResultDataAccessException e) {
            RfnMeter rfnMeter = new RfnMeter(pao, RfnIdentifier.createBlank());
            return rfnMeter;
        }
    }

    @Override
    public <T extends YukonPao> Map<T, RfnIdentifier> getRfnIdentifiersByPao(Iterable<T> paos) {
        ChunkingMappedSqlTemplate template =
                new ChunkingMappedSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT SerialNumber, Manufacturer, Model, DeviceId");
                sql.append("FROM RfnAddress WHERE DeviceId").in(subList);
                return sql;
            }
        };

        Function<T, Integer> typeMapper = new Function<T, Integer>() {
            @Override
            public Integer apply(T pao) {
                return pao.getPaoIdentifier().getPaoId();
            }
        };

        YukonRowMapper<Map.Entry<Integer, RfnIdentifier>> rowMapper =
                new YukonRowMapper<Entry<Integer, RfnIdentifier>>() {
            @Override
            public Entry<Integer, RfnIdentifier> mapRow(YukonResultSet rs) throws SQLException {
                RfnIdentifier rfnIdentifier =
                        new RfnIdentifier(rs.getStringSafe("SerialNumber"), 
                                               rs.getStringSafe("Manufacturer"), 
                                               rs.getStringSafe("Model"));
                int deviceId = rs.getInt("DeviceId");
                return Maps.immutableEntry(deviceId, rfnIdentifier);
            }
        };

        Map<T, RfnIdentifier> retVal = template.mappedQuery(sqlGenerator, paos, rowMapper, typeMapper);
        return retVal;
    }

    @Override
    public void updateDevice(RfnDevice device) {
        if(device.getRfnIdentifier().isBlank()) {
            /* When someone has blanked out the three fields of the rfn device address, delete that row from RfnAddress. */
            deleteRfnAddress(device);
            return;
        }
        /* If there is a row in RfnAddress for this device, update it, otherwise insert it. */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO RfnAddress");
        sql.values(device.getPaoIdentifier().getPaoId(), device.getRfnIdentifier().getSensorSerialNumber(), device.getRfnIdentifier().getSensorManufacturer(), device.getRfnIdentifier().getSensorModel());

        try{
            jdbcTemplate.update(sql);
            return;
        } catch (DataIntegrityViolationException e) {
            /* Row is there, try to update it. */
            SqlStatementBuilder updateSql = new SqlStatementBuilder();
            updateSql.append("update RfnAddress");
            updateSql.append("set SerialNumber").eq(device.getRfnIdentifier().getSensorSerialNumber());
            updateSql.append(  ", Manufacturer").eq(device.getRfnIdentifier().getSensorManufacturer());
            updateSql.append(  ", Model").eq(device.getRfnIdentifier().getSensorModel());
            updateSql.append("where DeviceId").eq(device.getPaoIdentifier().getPaoId());
            int rowsAffected = jdbcTemplate.update(updateSql);
            
            if(rowsAffected == 0) {
                /* The initial insert failed because a different device is using this SN, Manufacturer, Model combination. */
                throw new DataIntegrityViolationException("Serial Number, Manufacturer, and Model must be unique.");
            }
        }

    }
    
    private void deleteRfnAddress(RfnDevice device) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM RfnAddress");
        sql.append("where DeviceId").eq(device.getPaoIdentifier().getPaoId());
        
        jdbcTemplate.update(sql);
    }
    
    public String getFormattedDeviceName(RfnDevice device) throws IllegalArgumentException{
        return device.getName();
    }
    
}