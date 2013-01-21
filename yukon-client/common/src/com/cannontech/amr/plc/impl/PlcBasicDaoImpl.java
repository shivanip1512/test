package com.cannontech.amr.plc.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.plc.PlcBasicDao;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class PlcBasicDaoImpl implements PlcBasicDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader() {
        return new PaoLoader<DeviceCollectionReportDevice>() {
            @Override
            public Map<PaoIdentifier, DeviceCollectionReportDevice> getForPaos(Iterable<PaoIdentifier> identifiers) {

                return getPlcBasicEntriesFromIdentifiers(identifiers);
            }
        };
    }

    public Map<PaoIdentifier, DeviceCollectionReportDevice> getPlcBasicEntriesFromIdentifiers(Iterable<PaoIdentifier> identifiers) {
        
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ypo.paObjectId, ypo.paoName, ypo.type, ypo.disableFlag,");
                sql.append("       DeviceCarrierSettings.address,");
                sql.append("       rypo.paoName as route"); 
                sql.append("from YukonPaObject ypo"); 
                sql.append("join Device on ypo.paObjectId = Device.deviceId"); 
                sql.append("join DeviceCarrierSettings on Device.deviceId = DeviceCarrierSettings.deviceId"); 
                sql.append("left join DeviceRoutes on Device.deviceId = DeviceRoutes.deviceId"); 
                sql.append("left join YukonPaObject rypo on DeviceRoutes.routeId = rypo.paObjectId");
                sql.append("where ypo.paObjectId").in(subList);
                
                return sql;
            }
        };
        
        Function<PaoIdentifier, Integer> inputTypeToSqlGeneratorTypeMapper = new Function<PaoIdentifier, Integer>() {
            @Override
            public Integer apply(PaoIdentifier from) {
                return from.getPaoId();
            }
        };

        YukonRowMapper<Entry<Integer, DeviceCollectionReportDevice>> rowMapper = new YukonRowMapper<Entry<Integer, DeviceCollectionReportDevice>>() {
            @Override
            public Entry<Integer, DeviceCollectionReportDevice> mapRow(YukonResultSet rs) throws SQLException {
                PaoIdentifier paoIdentifier = new PaoIdentifier(rs.getInt("paObjectId"), rs.getEnum("type", PaoType.class));
                DeviceCollectionReportDevice dcrd = new DeviceCollectionReportDevice(paoIdentifier);
                dcrd.setName(rs.getString("paoName"));
                dcrd.setAddress(rs.getString("address"));
                //dcrd.setMeterNumber(meter.getMeterNumber()); Meter number is not set for these devices
                dcrd.setRoute(rs.getString("route"));
                
                Entry<Integer, DeviceCollectionReportDevice> immutableEntry = Maps.immutableEntry(paoIdentifier.getPaoId(), dcrd);
                
                return immutableEntry;
                
            }
        };
        

        return template.mappedQuery(sqlGenerator, identifiers, rowMapper, inputTypeToSqlGeneratorTypeMapper);
    }
}
