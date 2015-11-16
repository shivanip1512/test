package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.inventory.InventoryIdentifierMapper;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.MeteringType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.model.AssetReportDevice;
import com.cannontech.web.stars.dr.operator.inventory.service.AssetReportService;

public class AssetReportServiceImpl implements AssetReportService {
    
    private static final EnergyCompanySettingType meteringType = EnergyCompanySettingType.METER_MCT_BASE_DESIGNATION;
    
    @Autowired private EnergyCompanySettingDao ecSettingsDao;
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
        
        boolean starsMetering = ecSettingsDao.getEnum(meteringType, MeteringType.class, ecId) == MeteringType.stars;
        
        chunkyTemplate.queryInto(new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ib.InventoryId, ib.AccountId, ib.DeviceId, ib.DeviceLabel,");
                sql.append("ca.AccountNumber,");
                sql.append("ypo.PAOName, ypo.Type, ypo.PAObjectId,");
                if (starsMetering) {
                    sql.append("mhb.MeterNumber, mhb.MeterTypeId,");
                } else {
                    sql.append("dmg.MeterNumber,");
                }
                sql.append("lhb.ManufacturerSerialNumber, lhb.LmHardwareTypeId,"); 
                sql.append("ecti.EnergyCompanyId");
                
                sql.append("FROM InventoryBase ib");
                sql.append("JOIN CustomerAccount ca ON ca.AccountId = ib.AccountId");
                sql.append("LEFT JOIN YukonPAObject ypo ON ypo.PAObjectId = ib.DeviceId");
                if (starsMetering) {
                    sql.append("LEFT JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId");
                } else {
                    sql.append("LEFT JOIN DeviceMeterGroup dmg ON dmg.DeviceId = ib.DeviceId");
                }
                sql.append("LEFT JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
                sql.append("LEFT JOIN EcToInventoryMapping ecti ON ecti.InventoryId = ib.InventoryId");
                
                sql.append("WHERE ecti.EnergyCompanyId").eq(ecId);
                sql.append("AND ib.InventoryId").in(subList);
                
                return sql;
            }
        }, assetIds, new YukonRowMapper<AssetReportDevice>() {
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
                
                return device;
            }
            
        }, devices);
        
        return devices;
    }

}
