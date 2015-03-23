package com.cannontech.amr.rfn.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class RfnDeviceDaoImpl implements RfnDeviceDao {
    
    private final static Logger log = Logger.getLogger(RfnDeviceDaoImpl.class);

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PaoDao paoDao;
    @Autowired private IDatabaseCache cache;

    private final static YukonRowMapper<RfnDevice> rfnDeviceRowMapper = new YukonRowMapper<RfnDevice>() {
        @Override
        public RfnDevice mapRow(YukonResultSet rs) throws SQLException {
            
            String name = rs.getString("PaoName");
            PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PaobjectId", "Type");
            RfnIdentifier rfnIdentifier = new RfnIdentifier(rs.getStringSafe("SerialNumber"), 
                                                     rs.getStringSafe("Manufacturer"), 
                                                     rs.getStringSafe("Model"));
            RfnDevice rfnDevice = new RfnDevice(name, paoIdentifier, rfnIdentifier);
            
            return rfnDevice;
        }
    };
    
    @Override
    public RfnDevice getDeviceForExactIdentifier(RfnIdentifier rfnIdentifier) throws NotFoundException {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.PaoName, ypo.PAObjectID, ypo.Type, rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sql.append("from YukonPaObject ypo");
        sql.append("join RfnAddress rfn on ypo.PAObjectID = rfn.DeviceId");
        sql.append("where rfn.SerialNumber").eq(rfnIdentifier.getSensorSerialNumber());
        sql.append("and rfn.Manufacturer").eq(rfnIdentifier.getSensorManufacturer());
        sql.append("and rfn.Model").eq(rfnIdentifier.getSensorModel());
        
        try {
            RfnDevice rfnDevice = jdbcTemplate.queryForObject(sql, rfnDeviceRowMapper);
            return rfnDevice;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Unknown rfn identifier " + rfnIdentifier);
        }
    }

    @Override
    public RfnDevice getDeviceForId(int paoId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select pao.paoName, pao.Type, pao.PaobjectId, rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sql.append("from YukonPaobject pao");
        sql.append("join RfnAddress rfn on rfn.DeviceId = pao.PaobjectId");
        sql.append("where rfn.DeviceId").eq(paoId);
        
        try {
            RfnDevice rfnDevice= jdbcTemplate.queryForObject(sql, rfnDeviceRowMapper);
            return rfnDevice;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Unknown rfn device Id " + paoId + ". RfnAddress may be empty.");
        }
    }
    
    @Override
    public RfnDevice getDevice(YukonPao pao) {
        try {
            return getDeviceForId(pao.getPaoIdentifier().getPaoId());
        } catch (NotFoundException e) {
            log.warn("No RfnAddress found for " + pao.getPaoIdentifier() + ". Returning object with blank RfnIdentifier");
            String name = cache.getAllPaosMap().get(pao.getPaoIdentifier().getPaoId()).getPaoName();
            RfnDevice rfnDevice = new RfnDevice(name, pao, RfnIdentifier.BLANK);
            return rfnDevice;
        }
    }
    
    @Override
    public <T extends YukonPao> Map<T, RfnIdentifier> getRfnIdentifiersByPao(Iterable<T> paos) {
        ChunkingMappedSqlTemplate template =
                new ChunkingMappedSqlTemplate(jdbcTemplate);
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("select SerialNumber, Manufacturer, Model, DeviceId");
                sql.append("from RfnAddress");
                sql.append("where DeviceId").in(subList);
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
        if (device.getRfnIdentifier().isBlank()) {
            /* When someone has blanked out the three fields of the rfn device address, delete that row from RfnAddress. */
            deleteRfnAddress(device);
            return;
        }
        /* If there is a row in RfnAddress for this device, update it, otherwise insert it. */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("insert into RfnAddress");
        sql.values(device.getPaoIdentifier().getPaoId(), device.getRfnIdentifier().getSensorSerialNumber(), device.getRfnIdentifier().getSensorManufacturer(), device.getRfnIdentifier().getSensorModel());

        try {
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
        sql.append("delete from RfnAddress");
        sql.append("where DeviceId").eq(device.getPaoIdentifier().getPaoId());
        
        jdbcTemplate.update(sql);
    }
    
    public String getFormattedDeviceName(RfnDevice device) throws IllegalArgumentException{
        return device.getName();
    }
    
    @Override
    public List<RfnDevice> getDevicesByPaoType(PaoType paoType) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.PaoName, ypo.PAObjectID, ypo.Type, rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sql.append("from YukonPaObject ypo");
        sql.append(  "join RfnAddress rfn on ypo.PAObjectID = rfn.DeviceId");
        sql.append("where ypo.Type").eq(paoType);
        try {
            return jdbcTemplate.query(sql, rfnDeviceRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<RfnDevice>();
        }
    }
    
    @Override
    public List<RfnDevice> getDevicesByPaoTypes(Iterable<PaoType> paoTypes) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.PaoName, ypo.PAObjectID, ypo.Type, rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sql.append("from YukonPaObject ypo");
        sql.append(  "join RfnAddress rfn on ypo.PAObjectID = rfn.DeviceId");
        sql.append("where ypo.Type").in(paoTypes);
        try {
            return jdbcTemplate.query(sql, rfnDeviceRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<RfnDevice>();
        }
    }
    
    @Override 
    public List<RfnDevice> getDevicesByPaoIds(Iterable<Integer> paoIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.PaoName, ypo.PAObjectID, ypo.Type, rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sql.append("from YukonPaObject ypo");
        sql.append(  "join RfnAddress rfn on ypo.PAObjectID = rfn.DeviceId");
        sql.append("where PaObjectId").in(paoIds);
        try {
            return jdbcTemplate.query(sql, rfnDeviceRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<RfnDevice>();
        }
    }
    
    @Override
    public Map<Integer, RfnDevice> getPaoIdMappedDevicesByPaoType(PaoType paoType) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.PaoName, ypo.PAObjectID, ypo.Type, rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sql.append("from YukonPaObject ypo");
        sql.append(  "join RfnAddress rfn on ypo.PAObjectID = rfn.DeviceId");
        sql.append("where ypo.Type").eq(paoType);
        
        final Map<Integer, RfnDevice> rfnDevices = new HashMap<>();
        
        try {
            jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
                @Override
                public void processRow(YukonResultSet rs) throws SQLException {
                    String name = rs.getString("PaoName");
                    PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PaobjectId", "Type");
                    RfnIdentifier rfnIdentifier = new RfnIdentifier(rs.getStringSafe("SerialNumber"), 
                                                             rs.getStringSafe("Manufacturer"), 
                                                             rs.getStringSafe("Model"));
                    RfnDevice rfnDevice = new RfnDevice(name, paoIdentifier, rfnIdentifier);
                    rfnDevices.put(paoIdentifier.getPaoId(), rfnDevice);
                }
            });
        } catch (EmptyResultDataAccessException e) {
            //just return the empty map
        }
        
        return rfnDevices;
    }
}