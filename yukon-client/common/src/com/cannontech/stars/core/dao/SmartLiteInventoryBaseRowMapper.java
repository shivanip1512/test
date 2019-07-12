package com.cannontech.stars.core.dao;

import java.sql.SQLException;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteMeterHardwareBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;

public class SmartLiteInventoryBaseRowMapper implements YukonRowMapper<LiteInventoryBase> {
    
    private static final YukonRowMapper<LiteLmHardwareBase> hardwareRowMapper = new LiteStarsLMHardwareRowMapper();
    private static final YukonRowMapper<LiteMeterHardwareBase> meterRowMapper = new LiteMeterHardwareBaseRowMapper();
    private static final YukonRowMapper<LiteInventoryBase> inventoryRowMapper = new LiteInventoryBaseRowMapper();
    private static final YukonRowMapper<LiteInventoryBase> yukonMeterRowMapper = new LiteYukonMeterInventoryBaseRowMapper();
    
    @Override
    public LiteInventoryBase mapRow(YukonResultSet rs) throws SQLException {
        YukonRowMapper<LiteInventoryBase> mapper = StrategyTypeMapper.valueOf(rs.getInt("CategoryDefId"));
        return mapper.mapRow(rs);
    }
    
    private enum StrategyTypeMapper implements YukonRowMapper<LiteInventoryBase> {
        HARWARE() {
            @Override
            public LiteInventoryBase mapRow(YukonResultSet rs) throws SQLException {
                return hardwareRowMapper.mapRow(rs);
            }
        },
        
        METER() {
            @Override
            public LiteInventoryBase mapRow(YukonResultSet rs) throws SQLException {
                return meterRowMapper.mapRow(rs);
            }
        },
        
        YUKON_METER() {
            @Override
            public LiteInventoryBase mapRow(YukonResultSet rs) throws SQLException {
                return yukonMeterRowMapper.mapRow(rs);
            }
        },
        
        GENERIC_INVENTORY() {
            @Override
            public LiteInventoryBase mapRow(YukonResultSet rs) throws SQLException {
                return inventoryRowMapper.mapRow(rs);
            }
        };
        
        public static StrategyTypeMapper valueOf(int categoryDefId) {
            switch (categoryDefId) {
                case YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC :
                case YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC : return HARWARE;
                
                case YukonListEntryTypes.YUK_DEF_ID_INV_CAT_NON_YUKON_METER : return METER;
                case YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT             : return YUKON_METER;
                
                default : return GENERIC_INVENTORY;
            }
        }
    }

}