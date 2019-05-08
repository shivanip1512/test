package com.cannontech.amr.rfn.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnDeviceSearchCriteria;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RfnDeviceDaoImpl implements RfnDeviceDao {
    
    private final static Logger log = YukonLogManager.getLogger(RfnDeviceDaoImpl.class);

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private AsyncDynamicDataSource dynamicDataSource;
    @Autowired private IDatabaseCache cache;
    @Autowired private DbChangeManager dbChangeManager;
    private RfnIdentifierCache rfnIdentifierCache;

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
    
    @PostConstruct
    private void init() {
        rfnIdentifierCache = new RfnIdentifierCache(jdbcTemplate, dynamicDataSource);
    }
    
    @Override
    public boolean deviceExists(RfnIdentifier rfnIdentifier) {

        // This method is currently only used by GatewayCreationService, and gateways are not currently
        // cached.
        // Uncomment the section below if/when they are added to RfnManufacturerModel.

//        RfnManufacturerModel mm = RfnManufacturerModel.of(rfnIdentifier);
//        if (mm != null) {
//            Integer paoId = rfnIdentifierCache.getPaoIdFor(mm, rfnIdentifier.getSensorSerialNumber());
//            if (paoId != null) {
//                return true;
//            }
//        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.PaoName");
        sql.append("from YukonPaObject ypo");
        sql.append("join RfnAddress rfn on ypo.PAObjectID = rfn.DeviceId");
        sql.append("where rfn.SerialNumber").eq(rfnIdentifier.getSensorSerialNumber());
        sql.append("and rfn.Manufacturer").eq(rfnIdentifier.getSensorManufacturer());
        sql.append("and rfn.Model").eq(rfnIdentifier.getSensorModel());
        
        try {
            jdbcTemplate.queryForString(sql);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
        
    @Override
    public RfnDevice getDeviceForExactIdentifier(RfnIdentifier rfnIdentifier) throws NotFoundException {

        if (rfnIdentifier.isBlank()) {
            throw new NotFoundException("Cannot look up blank RfnIdentifier");
        }

        RfnManufacturerModel mm = RfnManufacturerModel.of(rfnIdentifier);
        if (mm != null) {
            return Optional.ofNullable(rfnIdentifierCache.getPaoIdFor(mm, rfnIdentifier.getSensorSerialNumber()))
                    .map(paoId -> cache.getAllPaosMap().get(paoId))
                    .map(litePao -> new RfnDevice(litePao.getPaoName(), litePao, rfnIdentifier))
                    .orElseThrow(() -> new NotFoundException("No cache results for " + rfnIdentifier));
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.PaoName, ypo.PAObjectID, ypo.Type, rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sql.append("from YukonPaObject ypo");
        sql.append("join RfnAddress rfn on ypo.PAObjectID = rfn.DeviceId");
        sql.append("where rfn.SerialNumber").eq(rfnIdentifier.getSensorSerialNumber());
        sql.append("and rfn.Manufacturer").eq(rfnIdentifier.getSensorManufacturer());
        sql.append("and rfn.Model").eq(rfnIdentifier.getSensorModel());

        try {
            return jdbcTemplate.queryForObject(sql, rfnDeviceRowMapper);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Empty result set for query " + sql + sql.getArgumentList());
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
    @Transactional
    public RfnDevice updateGatewayType(RfnDevice device) {
        if (device.getPaoIdentifier().getPaoType() != PaoType.RFN_GATEWAY) {
            throw new IllegalArgumentException("updateGatewayType only accepts RFN_GATEWAY");
        }
        
        PaoIdentifier paoIdentifier = new PaoIdentifier(device.getPaoIdentifier().getPaoId(), PaoType.GWY800);
        RfnIdentifier rfnIdentifier = new RfnIdentifier(device.getRfnIdentifier().getSensorSerialNumber(),
                                                        device.getRfnIdentifier().getSensorManufacturer(),
                                                        RfnDeviceCreationService.GATEWAY_2_MODEL_STRING);
        RfnDevice updatedDevice = new RfnDevice(device.getName(),
                                                paoIdentifier,
                                                rfnIdentifier);
        
        // Update the pao type
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.update("YukonPaObject");
        sink.addValue("Type", PaoType.GWY800);
        sql.append("WHERE PaObjectId").eq(device.getPaoIdentifier().getPaoId());
        
        jdbcTemplate.update(sql);
        
        // Update the rfn address
        updateDevice(updatedDevice);
        return updatedDevice;
    }
    
    @Override
    public void updateDevice(RfnDevice device) {
        if (device.getRfnIdentifier().isBlank()) {
            /* When someone has blanked out the three fields of the rfn device address, delete that row from RfnAddress. */
            deleteRfnAddress(device);
            dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
            return;
        }
        if (!device.getRfnIdentifier().isNotBlank()) {
            // Throws an exception if any of the three fields(Serial Number, Manufacturer, and
            // Model) is missing.
            throw new DataIntegrityViolationException("Serial Number, Manufacturer, and Model all these fields are "
                    + "required to update any of these field.");
        }
        /* If there is a row in RfnAddress for this device, update it, otherwise insert it. */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("insert into RfnAddress");
        sql.values(device.getPaoIdentifier().getPaoId(), device.getRfnIdentifier().getSensorSerialNumber(), device.getRfnIdentifier().getSensorManufacturer(), device.getRfnIdentifier().getSensorModel());

        try {
            jdbcTemplate.update(sql);
            dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
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
            dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
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
        
        return jdbcTemplate.query(sql, rfnDeviceRowMapper);
    }
    
    @Override
    public List<RfnDevice> getDevicesByPaoTypes(Iterable<PaoType> paoTypes) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.PaoName, ypo.PAObjectID, ypo.Type, rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sql.append("from YukonPaObject ypo");
        sql.append(  "join RfnAddress rfn on ypo.PAObjectID = rfn.DeviceId");
        sql.append("where ypo.Type").in(paoTypes);

        return jdbcTemplate.query(sql, rfnDeviceRowMapper);
    }
    
    @Override
    public List<RfnDevice> searchDevicesByPaoTypes(Iterable<PaoType> paoTypes, RfnDeviceSearchCriteria criteria) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.PaoName, ypo.PAObjectID, ypo.Type, rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sql.append("from YukonPaObject ypo");
        sql.append(  "join RfnAddress rfn on ypo.PAObjectID = rfn.DeviceId");
        sql.append("where ypo.Type").in(paoTypes);
        if (criteria.getName() != null) {
            sql.append("AND UPPER(ypo.PAOName) LIKE UPPER (");
            sql.appendArgument(criteria.getName() + "%");
            sql.append(")");
        }
        if (criteria.getSerialNumber() != null) {
            sql.append("AND UPPER(rfn.SerialNumber) LIKE UPPER (");
            sql.appendArgument(criteria.getSerialNumber() + "%");
            sql.append(")");
        }
        return jdbcTemplate.query(sql, rfnDeviceRowMapper);
    }
    
    @Override
    public Integer findDeviceBySensorSerialNumber(String sensorSerialNumber) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select DeviceId");
        sql.append("from RfnAddress where SerialNumber").eq(sensorSerialNumber);
        
        try {
            return jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public List<RfnDevice> getDevicesByPaoIds(Iterable<Integer> paoIds) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);

        return template.query(new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append("select ypo.PaoName, ypo.PAObjectID, ypo.Type, rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
                sqlBuilder.append("from YukonPaObject ypo");
                sqlBuilder.append("join RfnAddress rfn on ypo.PAObjectID = rfn.DeviceId");
                sqlBuilder.append("where PaObjectId").in(subList);
                return sqlBuilder;
            }
        }, paoIds, rfnDeviceRowMapper);
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
    
    @Transactional
    @Override
    public void saveDynamicRfnDeviceData(int gatewayId, List<Integer> deviceIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        List<List<Object>> values = deviceIds.stream().map(deviceId -> {
            List<Object> row = Lists.newArrayList(deviceId, gatewayId, new Instant());
            return row;
        }).collect(Collectors.toList());

        sql.batchInsertInto("DynamicRfnDeviceData").columns("DeviceId", "GatewayId", "LastTransferTime").values(values);
        jdbcTemplate.yukonBatchUpdate(sql);
    }
    
    @Override
    public List<RfnDevice> getDevicesForGateway(int gatewayId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.PaoName, ypo.PAObjectID, ypo.Type, rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sql.append("FROM DynamicRfnDeviceData dd");
        sql.append("JOIN YukonPaObject ypo on dd.DeviceId = ypo.PAObjectID");
        sql.append("JOIN RfnAddress rfn on dd.DeviceId = rfn.DeviceId");
        sql.append("WHERE dd.GatewayId").eq(gatewayId);
        return jdbcTemplate.query(sql, rfnDeviceRowMapper);
    }
    
    @Override
    public void clearDynamicRfnDeviceData() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE from DynamicRfnDeviceData");
        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<Integer> getDeviceIdsForRfnIdentifiers(Iterable<RfnIdentifier> rfnIdentifiers) {
        SqlFragmentGenerator<RfnIdentifier> sql = new SqlFragmentGenerator<RfnIdentifier>() {
            @Override
            public SqlFragmentSource generate(List<RfnIdentifier> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT DeviceId from RfnAddress");
                sql.append("WHERE ");
                for (int i = 0; i < subList.size(); i++) {
                    RfnIdentifier ident = subList.get(i);
                    sql.append("(");
                    sql.append("SerialNumber").eq(ident.getSensorSerialNumber());
                    sql.append(" AND Manufacturer").eq(ident.getSensorManufacturer());
                    sql.append(" AND Model").eq(ident.getSensorModel());
                    sql.append(")");
                    if (i < subList.size() - 1) {
                        sql.append("OR");
                    }
                }
                return sql;
            }
        };
        List<Integer> deviceIds = new ArrayList<>();
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        template.setChunkSize(500);
        template.query(sql, rfnIdentifiers, (YukonResultSet rs) -> deviceIds.add(rs.getInt("deviceId")));
        return  deviceIds;
    }
}