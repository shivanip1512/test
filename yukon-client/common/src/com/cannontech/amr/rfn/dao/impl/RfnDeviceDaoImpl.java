package com.cannontech.amr.rfn.dao.impl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnDeviceSearchCriteria;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlBuilder;
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
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RfnDeviceDaoImpl implements RfnDeviceDao {
    
    private final static Logger log = YukonLogManager.getLogger(RfnDeviceDaoImpl.class);

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private JdbcTemplate template;
    @Autowired private AsyncDynamicDataSource dynamicDataSource;
    @Autowired private IDatabaseCache cache;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    @Autowired private DatabaseVendorResolver dbVendorResolver;
    private RfnAddressCache rfnIdentifierCache;
    private static SimpleDateFormat oracleLastTransferTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    
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
        rfnIdentifierCache = new RfnAddressCache(jdbcTemplate, dynamicDataSource);
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

        return Optional.ofNullable(rfnIdentifierCache.getPaoIdFor(rfnIdentifier))
                .map(paoId -> cache.getAllPaosMap().get(paoId))
                .map(litePao -> new RfnDevice(litePao.getPaoName(), litePao, rfnIdentifier))
                .orElseThrow(() -> new NotFoundException("No cache results for " + rfnIdentifier));
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
        sql.append("SELECT DeviceId");
        sql.append("FROM RfnAddress WHERE SerialNumber").eq(sensorSerialNumber);
        
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
    
    
   private static class DynamicRfnDeviceData {
        private int deviceId;
        private int gatewayId;
        private Instant transferTime;
        //id to send back to NM if NM requires acknowledgment 
        private Long referenceID;
    }
    
   private boolean isValidNodeComm(NodeComm comm) {
       if(comm == null) {
           return false;
       }
       
       boolean isValid = comm.getDeviceRfnIdentifier() != null && !comm.getDeviceRfnIdentifier().is_Empty_() && comm.getGatewayRfnIdentifier() != null
               && !comm.getGatewayRfnIdentifier().is_Empty_();
       
       if(!isValid) {
           log.info("Missing RfnIdentifier or GatewayRfnIdentifier {}", comm);
           return false;
       }
       Integer deviceId = rfnIdentifierCache.getPaoIdFor(comm.getDeviceRfnIdentifier());
       if(deviceId == null) {
           log.error("Unable to find {} in rfnIdentifier cache", comm.getDeviceRfnIdentifier());
       }
       Integer gatewayId = rfnIdentifierCache.getPaoIdFor(comm.getGatewayRfnIdentifier());
       if(gatewayId == null) {
           log.error("Unable to find {} in rfnIdentifier cache", comm.getGatewayRfnIdentifier());
       }
       
       if(deviceId == null || gatewayId == null) {
           return false;
       }
       
       return true;
   }
   
   
    @Override
    public void saveDynamicRfnDeviceData(List<NodeComm> nodes) {
        if(CollectionUtils.isNotEmpty(nodes)) {
            log.info("Attempting to save device to gateway mapping for devices {}", nodes);
            List<DynamicRfnDeviceData> data = nodes.stream()
                    .filter(node -> isValidNodeComm(node))
                    .map(node -> getDynamicRfnDeviceData(node, null))
                    .collect(Collectors.toList());
            log.info("Saving device to gateway mapping for valid devices {}",  data );
            chunkAndSaveDynamicRfnDeviceData(data);
        }
    }
    
    @Override
    public Set<Long> saveDynamicRfnDeviceData(Map<Long, NodeComm> nodes) {
        if(MapUtils.isEmpty(nodes)) {
            return new HashSet<>();
        }
        log.info("Attempting to save device to gateway mapping for devices {}", nodes);
        List<DynamicRfnDeviceData> data = nodes.entrySet().stream()
                .filter(node -> isValidNodeComm(node.getValue()))
                .map(node -> getDynamicRfnDeviceData(node.getValue(), node.getKey()))
                .collect(Collectors.toList());
        log.info("Saving device to gateway mapping for valid devices {}",  data );
        Set<Long> refIds = chunkAndSaveDynamicRfnDeviceData(data);
        log.info("Rows saved {}", refIds.size());
        return refIds;
    }

    private Set<Long> chunkAndSaveDynamicRfnDeviceData(List<DynamicRfnDeviceData> data) {
        List<List<DynamicRfnDeviceData>> subSets = Lists.partition(data, ChunkingSqlTemplate.DEFAULT_SIZE/3);
        log.debug("Starting update of {} DynamicRfnDeviceData rows ({} transaction sets).", data.size(), subSets.size());
        if (dbVendorResolver.getDatabaseVendor().isSqlServer()) {  
            return saveDynamicRfnDeviceDataSqlServer(subSets);
        }
        else if (dbVendorResolver.getDatabaseVendor().isOracle()) {  
            return saveDynamicRfnDeviceDataOracle(subSets);
        }
        log.debug("Update DynamicRfnDeviceData completed.", data.size(), subSets.size());
        return new HashSet<>();
    }

    private DynamicRfnDeviceData getDynamicRfnDeviceData(NodeComm comm, Long referenceId) {
        Integer deviceId = rfnIdentifierCache.getPaoIdFor(comm.getDeviceRfnIdentifier());
        Integer gatewayId = rfnIdentifierCache.getPaoIdFor(comm.getGatewayRfnIdentifier());
        DynamicRfnDeviceData deviceData = new DynamicRfnDeviceData();
        deviceData.deviceId = deviceId;
        deviceData.gatewayId = gatewayId;
        deviceData.transferTime = new Instant(comm.getNodeCommStatusTimestamp());
        deviceData.referenceID = referenceId;
        return deviceData;
    }
    
    private Set<Long> saveDynamicRfnDeviceDataOracle(List<List<DynamicRfnDeviceData>> subSets) {
        /**
         * MERGE INTO DynamicRfnDeviceData DRDD
                 USING (  
                      SELECT 123977 AS DeviceId, 138791 AS GatewayId, SYSDATE AS LastTransferTime FROM DUAL UNION
                      SELECT 138787, 138791, SYSDATE FROM DUAL UNION
                      SELECT 132879, 138791, SYSDATE FROM DUAL ) CACHE_DATA
                 ON ( DRDD.DeviceId = CACHE_DATA.DeviceId )
            WHEN MATCHED THEN
                 UPDATE SET DRDD.GatewayId = CACHE_DATA.GatewayId, DRDD.LastTransferTime = CACHE_DATA.LastTransferTime
            WHEN NOT MATCHED THEN
                 INSERT VALUES (CACHE_DATA.DeviceId, CACHE_DATA.GatewayId, CACHE_DATA.LastTransferTime);
         */
        Set<Long> referenceIds = new HashSet<>();
        AtomicLong setNumber = new AtomicLong(0);
        subSets.forEach(part -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("MERGE INTO DynamicRfnDeviceData DRDD");
            sql.append("USING (");
            String params = IntStream.range(0, part.size()).mapToObj(i -> {
                String param =  "SELECT ? AS DeviceId, ? AS GatewayId, TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') AS LastTransferTime FROM DUAL";
                if(i != 0) {
                    param =  "SELECT ? , ? , TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') FROM DUAL";
                }
                return param;
            }).collect(Collectors.joining(" UNION "));
            sql.append(params);
            sql.append(") CACHE_DATA");
            sql.append("    ON ( DRDD.DeviceId = CACHE_DATA.DeviceId )");
            sql.append("WHEN MATCHED THEN");
            sql.append("    UPDATE SET DRDD.GatewayId = CACHE_DATA.GatewayId, DRDD.LastTransferTime = CACHE_DATA.LastTransferTime");
            sql.append("WHEN NOT MATCHED THEN");
            sql.append("    INSERT VALUES (CACHE_DATA.DeviceId, CACHE_DATA.GatewayId, CACHE_DATA.LastTransferTime)");
            Object[] values = part.stream()
                   // .peek(value -> log.debug(value.deviceId+" "+ value.gatewayId+" "+ oracleLastTransferTimeFormat.format(value.transferTime.toDate())))
                    .flatMap(value -> Stream.of(value.deviceId, value.gatewayId, oracleLastTransferTimeFormat.format(value.transferTime.toDate())))
                    .toArray();
            referenceIds.addAll(update(sql, values, setNumber.incrementAndGet(), part));
        });
        return referenceIds;
    }

    /**
     * Inserts the data in the table, retries 3 times on failure
     */
    private Set<Long> update(SqlStatementBuilder sql, Object[] values, long setNumber,
            List<DynamicRfnDeviceData> part) {           
            int retryCount = 1; 
            while (retryCount <= 3) {
                try {
                    log.debug("Starting DynamicRfnDeviceData update try {} of 3, set {}.", retryCount, setNumber);
                    template.update(sql.getSql(), values);
                    log.debug("DynamicRfnDeviceData update success on try {} of 3, set {}. Updated {} rows.", retryCount, setNumber,  part.size());
                    // ids to send to NM for acknowledgment
                    return part.stream()
                            .filter(p -> p.referenceID != null)
                            .map(p -> p.referenceID)
                            .collect(Collectors.toSet());
                } catch (Exception e) {
                    if (retryCount == 3) {
                        log.error("DynamicRfnDeviceData update failed on try {} of 3, set {}. Transaction failed and aborting. Must perform a NM sync to retry updating data.", retryCount, setNumber, e);
                    } else {
                        log.debug("DynamicRfnDeviceData update failed on try {} of 3, set {}. Will retry.", retryCount, setNumber, e);
                    }
                    retryCount++;
                }
            }
            return new HashSet<>();
    }
    
    private Set<Long> saveDynamicRfnDeviceDataSqlServer(List<List<DynamicRfnDeviceData>> subSets) {
        /**
         * WITH NMD_CTE (DeviceId, GatewayId, LastTransferTime) AS (
            SELECT * FROM (
                VALUES 
                    (123977, 138791, GETDATE()),
                    (138787, 138791, GETDATE()),
                    (132879, 138791, GETDATE())
                ) AS inner_query (DeviceId, GatewayId, LastTransferTime)
            )
            MERGE INTO DynamicRfnDeviceData DRDD
                 USING NMD_CTE CACHE
                 ON DRDD.DeviceId = CACHE.DeviceId
            WHEN MATCHED THEN
                 UPDATE SET DRDD.GatewayId = CACHE.GatewayId, DRDD.LastTransferTime = CACHE.LastTransferTime
            WHEN NOT MATCHED THEN
                 INSERT VALUES (CACHE.DeviceId, CACHE.GatewayId, CACHE.LastTransferTime);
         */
        Set<Long> referenceIds = new HashSet<>();
        AtomicLong setNumber = new AtomicLong(0);
        subSets.forEach(part -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("WITH NMD_CTE (DeviceId, GatewayId, LastTransferTime) AS (");
            sql.append("SELECT * FROM (");
            sql.append("    VALUES ");
            String params = part.stream()
                    .map(node -> " (?,?,?)")
                    .collect(Collectors.joining(","));
            sql.append(params);
            sql.append("    ) AS inner_query (DeviceId, GatewayId, LastTransferTime)");
            sql.append(")");
            sql.append("MERGE INTO DynamicRfnDeviceData DRDD");
            sql.append("    USING NMD_CTE CACHE");
            sql.append("    ON DRDD.DeviceId = CACHE.DeviceId");
            sql.append("WHEN MATCHED THEN");
            sql.append("    UPDATE SET DRDD.GatewayId = CACHE.GatewayId, DRDD.LastTransferTime = CACHE.LastTransferTime");
            sql.append("WHEN NOT MATCHED THEN");
            sql.append("    INSERT VALUES (CACHE.DeviceId, CACHE.GatewayId, CACHE.LastTransferTime);");
            Object[] values = part.stream()
                    .flatMap(value -> Stream.of(value.deviceId, value.gatewayId, value.transferTime.toString()))
                    .toArray();
            referenceIds.addAll(update(sql, values, setNumber.incrementAndGet(), part));
        });
        return referenceIds;
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

    public List<RfnDevice> getDevicesForGateways(List<Integer> gatewayIds, boolean orderDescending) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.PaoName, ypo.PAObjectID, ypo.Type, rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sql.append("FROM DynamicRfnDeviceData dd");
        sql.append("JOIN YukonPaObject ypo on dd.DeviceId = ypo.PAObjectID");
        sql.append("JOIN RfnAddress rfn on dd.DeviceId = rfn.DeviceId");
        sql.append("WHERE dd.GatewayId").in(gatewayIds);
        if (orderDescending) {
            sql.append("ORDER BY LastTransferTime DESC");
        } else {
            sql.append("ORDER BY LastTransferTime ASC");
        }
        return jdbcTemplate.query(sql, rfnDeviceRowMapper);
    }
        
    @Override
    public List<RfnIdentifier> getRfnIdentifiersForGateway(int gatewayId, int rowLimit) {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();

        SqlBuilder sqla = builder.buildFor(DatabaseVendor.getMsDatabases());
        sqla.append("SELECT TOP " + rowLimit  + " rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sqla.append("FROM DynamicRfnDeviceData dd");
        sqla.append("JOIN YukonPaObject ypo on dd.DeviceId = ypo.PAObjectID");
        sqla.append("JOIN RfnAddress rfn on dd.DeviceId = rfn.DeviceId");
        sqla.append("WHERE dd.GatewayId").eq(gatewayId);
        
        SqlBuilder sqlb = builder.buildOther();
        sqlb.append("SELECT rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sqlb.append("FROM DynamicRfnDeviceData dd");
        sqlb.append("JOIN YukonPaObject ypo on dd.DeviceId = ypo.PAObjectID");
        sqlb.append("JOIN RfnAddress rfn on dd.DeviceId = rfn.DeviceId");
        sqlb.append("WHERE dd.GatewayId").eq(gatewayId);
        sqlb.append("AND ROWNUM").lte(rowLimit);
        
        return jdbcTemplate.query(builder, new YukonRowMapper<RfnIdentifier>() {
            @Override
            public RfnIdentifier mapRow(YukonResultSet rs) throws SQLException {
                RfnIdentifier rfnIdentifier = new RfnIdentifier(
                    rs.getStringSafe("SerialNumber"),
                    rs.getStringSafe("Manufacturer"), 
                    rs.getStringSafe("Model"));
                return rfnIdentifier;
            }
        });
    }
    
    @Override
    public Set<Integer> getGatewayIdsForDevices(Set<Integer> deviceIds) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        Set<Integer> gatewayIds = new HashSet<>();
        template.query(devices -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT DISTINCT GatewayId");
            sql.append("FROM DynamicRfnDeviceData");
            sql.append("WHERE DeviceId").in(devices);
            return sql;
        }, deviceIds, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                gatewayIds.add(rs.getInt("GatewayId"));
            }
        });
        return gatewayIds;
    }
    
    @Override
    public void clearDynamicRfnDeviceData() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE from DynamicRfnDeviceData");
        jdbcTemplate.update(sql);
    }
    
    @Override
    public Set<Integer> getDeviceIdsForRfnIdentifiers(Iterable<RfnIdentifier> rfnIdentifiers) {
        return rfnIdentifierCache.getPaoIdsFor(rfnIdentifiers);
    }
    
    @Override
    public Integer getDeviceIdForRfnIdentifier(RfnIdentifier rfnIdentifier) {
        return rfnIdentifierCache.getPaoIdFor(rfnIdentifier);
    }
}