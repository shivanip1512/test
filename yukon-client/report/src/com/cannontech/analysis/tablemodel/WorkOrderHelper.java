package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;

public final class WorkOrderHelper {
    private final StarsCustAccountInformationDao starsCustAccountInformationDao = 
        YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
    private final SimpleJdbcTemplate simpleJdbcTemplate = 
        YukonSpringHook.getBean("simpleJdbcTemplate", SimpleJdbcTemplate.class);
    
    private final Map<Integer, Map<Integer, LiteAccountInfo>> ecToAccountMap = 
        new HashMap<Integer, Map<Integer, LiteAccountInfo>>();
    
    public synchronized Map<Integer, LiteAccountInfo> getAccountMap(final Integer energyCompanyId) {
        Map<Integer, LiteAccountInfo> accountMap = ecToAccountMap.get(energyCompanyId);
        
        if (accountMap == null) {
            accountMap = createAccountMap(energyCompanyId);
            ecToAccountMap.put(energyCompanyId, accountMap);
        }
        
        return accountMap;
    }
    
    private Map<Integer, LiteAccountInfo> createAccountMap(Integer energyCompanyId) {
        final List<LiteAccountInfo> accountList = starsCustAccountInformationDao.getAll(energyCompanyId);
        
        final Map<Integer, LiteAccountInfo> resultMap =
            new HashMap<Integer, LiteAccountInfo>(accountList.size());
        
        for (final LiteAccountInfo account : accountList) {
            Integer accountId = account.getAccountID();
            resultMap.put(accountId, account);
        }
        
        return resultMap;
    }
    
    public Map<LiteInventoryBase, String> getInventoryMeterNumbers(List<LiteInventoryBase> liteInvBaseList) {
        final Map<Integer, LiteInventoryBase> inventoryMap = new HashMap<Integer, LiteInventoryBase>(liteInvBaseList.size());
        final List<Integer> nonZeroDeviceIdList = new ArrayList<Integer>();
        final List<Integer> zeroDeviceIdList = new ArrayList<Integer>();
        
        /* Populate the inventoryMap with InventoryID to LiteInventoryBase Object, and sort
         * the LiteInventoryBase Objects by the DeviceID value into separate lists. 
         */
        for (final LiteInventoryBase inventoryBase : liteInvBaseList) {
            if (!(inventoryBase instanceof LiteLmHardwareBase)) continue;
            
            int inventoryId = inventoryBase.getInventoryID(); 
            int deviceId = inventoryBase.getDeviceID();
            
            if (deviceId > 0) {
                nonZeroDeviceIdList.add(inventoryId);
            } else {
                zeroDeviceIdList.add(inventoryId);
            }
            
            inventoryMap.put(inventoryId, inventoryBase);
        }
        
        // Temporary holder for generating the result Map<LiteInventoryBase, String>
        class Holder {
            LiteInventoryBase inventory;
            String meterNumber;
        }
        
        final ParameterizedRowMapper<Holder> holderRowMapper = new ParameterizedRowMapper<Holder>() {
            @Override
            public Holder mapRow(ResultSet rs, int rowNum) throws SQLException {
                int inventoryId = rs.getInt(1);
                LiteInventoryBase inventory = inventoryMap.get(inventoryId);
                String meterNumber = rs.getString(2);
                
                Holder holder = new Holder();
                holder.inventory = inventory;
                holder.meterNumber = meterNumber;
                return holder;
            }
        };
        
        // Run SQL for Inventory that contain non-zero DeviceID's.
        final ChunkingSqlTemplate template = new ChunkingSqlTemplate(simpleJdbcTemplate);
        List<Holder> list1 = template.query(new SqlGenerator<Integer>() {
            @Override
            public String generate(List<Integer> subList) {
                final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append(" SELECT inv.InventoryID,dmg.MeterNumber ");
                sqlBuilder.append(" FROM InventoryBase inv, DeviceMeterGroup dmg ");
                sqlBuilder.append(" WHERE INV.INVENTORYID IN (");
                sqlBuilder.append(subList);
                sqlBuilder.append(") AND INV.DEVICEID = DMG.DEVICEID ");
                sqlBuilder.append(" ORDER BY inv.InventoryID");
                String sql = sqlBuilder.toString();
                return sql;
            }
            
        }, nonZeroDeviceIdList, holderRowMapper);
        
        // Run SQL for Inventory that have DeviceID of zero.
        List<Holder> list2 = template.query(new SqlGenerator<Integer>() {
            @Override
            public String generate(List<Integer> subList) {
                final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append("SELECT MAP.LMHARDWAREINVENTORYID,MHB.MeterNumber ");
                sqlBuilder.append(" FROM MeterHardwareBase mhb, lmhardwaretometermapping map "); 
                sqlBuilder.append(" WHERE MAP.LMHARDWAREINVENTORYID IN ("); 
                sqlBuilder.append(subList);    
                sqlBuilder.append(") and mhb.inventoryid = map.meterinventoryid ");
                String sql = sqlBuilder.toString();
                return sql;
            }
            
        }, zeroDeviceIdList, holderRowMapper);
        
        final List<Holder> holderList = new ArrayList<Holder>(list1.size() + list2.size());
        holderList.addAll(list1);
        holderList.addAll(list2);
        
        final Map<LiteInventoryBase, String> meterNumberMap = new HashMap<LiteInventoryBase, String>(holderList.size());
        
        for (final Holder holder : holderList) {
            LiteInventoryBase key = holder.inventory;
            String value = holder.meterNumber;
            meterNumberMap.put(key, value);
        }
        
        return meterNumberMap;
    }
    
}
