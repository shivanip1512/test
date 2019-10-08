package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.inventory.InventoryIdentifierMapper;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.stars.model.AssetReportDevice;
import com.cannontech.web.stars.dr.operator.inventory.service.AssetReportService;

public class AssetReportServiceImpl implements AssetReportService {
    
    private static final Logger log = YukonLogManager.getLogger(AssetReportServiceImpl.class);
    
    @Autowired private InventoryIdentifierMapper identifierMapper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private ChunkingSqlTemplate chunkyTemplate;
    
    @PostConstruct
    public void init() {
        chunkyTemplate = new ChunkingSqlTemplate(jdbcTemplate);
    }
    
    @Override
    public List<AssetReportDevice> getAssetReportDevices(int ecId, List<Integer> assetIds) {

        List<AssetReportDevice> devices = new ArrayList<>();

        chunkyTemplate.queryInto(getSqlFragmentGenerator(ecId), assetIds,
            getYukonRowMapper(null, new AtomicBoolean(), 0), devices);

        return devices;
    }

    @Override
    public void queueAssetReportDevices(int ecId, List<Integer> assetIds, BlockingQueue<AssetReportDevice> queue,
            AtomicBoolean isCompleted) {
        chunkyTemplate.query(getSqlFragmentGenerator(ecId), assetIds,
            getYukonRowMapper(queue, isCompleted, assetIds.size()));
    }

    private YukonRowMapper<AssetReportDevice> getYukonRowMapper(BlockingQueue<AssetReportDevice> queue,
            AtomicBoolean isCompleted, int size) {
        return new YukonRowMapper<AssetReportDevice>() {
            private int count;

            @Override
            public AssetReportDevice mapRow(YukonResultSet rs) throws SQLException {
                AssetReportDevice device = new AssetReportDevice();
                device.setInventoryIdentifier(identifierMapper.mapRow(rs));
                int deviceId = rs.getInt("DeviceId");
                device.setDeviceId(deviceId);
                if (deviceId > 0) {
                    // This is actually a pao
                    device.setPaoIdentifier(rs.getPaoIdentifier());
                }
                device.setName(rs.getString("PAOName"));
                device.setMeterNumber(rs.getString("MeterNumber"));
                device.setSerialNumber(rs.getString("ManufacturerSerialNumber"));
                device.setLabel(rs.getString("DeviceLabel"));
                device.setAccountId(rs.getInt("AccountId"));
                device.setAccountNo(rs.getString("AccountNumber"));
                device.setEnergyCompanyId(rs.getInt("EnergyCompanyId"));

                try {
                    if (queue != null) {
                        queue.put(device);
                        synchronized (AssetReportServiceImpl.class) {
                            count++;
                        }
                        if (count == size) {
                            isCompleted.set(true);
                            count = 0;
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("Error while queuing data " + e);
                }

                return device;
            }
        };
    }

    private SqlFragmentGenerator<Integer> getSqlFragmentGenerator(int ecId) {
        return new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ib.InventoryId, ib.AccountId, ib.DeviceId, ib.DeviceLabel,");
                sql.append("ca.AccountNumber,");
                sql.append("ypo.PAOName, ypo.Type, ypo.PAObjectId,");
                sql.append("COALESCE(dmg.MeterNumber, mhb.MeterNumber) MeterNumber,");
                sql.append("lhb.ManufacturerSerialNumber, lhb.LmHardwareTypeId,");
                sql.append("ecti.EnergyCompanyId");

                sql.append("FROM InventoryBase ib");
                sql.append("JOIN CustomerAccount ca ON ca.AccountId = ib.AccountId");
                sql.append("LEFT JOIN YukonPAObject ypo ON ypo.PAObjectId = ib.DeviceId");
                sql.append("LEFT JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId");
                sql.append("LEFT JOIN DeviceMeterGroup dmg ON dmg.DeviceId = ib.DeviceId");
                sql.append("LEFT JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
                sql.append("LEFT JOIN EcToInventoryMapping ecti ON ecti.InventoryId = ib.InventoryId");

                sql.append("WHERE ecti.EnergyCompanyId").eq(ecId);
                sql.append("AND ib.InventoryId").in(subList);

                return sql;
            }
        };
    }

}
