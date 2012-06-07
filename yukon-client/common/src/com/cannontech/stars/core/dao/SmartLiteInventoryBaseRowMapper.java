package com.cannontech.stars.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteMeterHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsLMHardware;

public class SmartLiteInventoryBaseRowMapper implements ParameterizedRowMapper<LiteInventoryBase> {
    private static final ParameterizedRowMapper<LiteStarsLMHardware> hardwareRowMapper =
        new LiteStarsLMHardwareRowMapper();

    private static final ParameterizedRowMapper<LiteMeterHardwareBase> meterRowMapper =
        new LiteMeterHardwareBaseRowMapper();
    
    private static final ParameterizedRowMapper<LiteInventoryBase> inventoryRowMapper = 
        new LiteInventoryBaseRowMapper();
    
    @Override
    public LiteInventoryBase mapRow(ResultSet rs, int rowNum) throws SQLException {
        ParameterizedRowMapper<LiteInventoryBase> mapper = StrategyTypeMapper.valueOf(rs.getInt("CategoryDefId"));
        return mapper.mapRow(rs, rowNum);
    }
    
    private enum StrategyTypeMapper implements ParameterizedRowMapper<LiteInventoryBase> {
        HARWARE() {
            @Override
            public LiteInventoryBase mapRow(ResultSet rs, int rowNum) throws SQLException {
                return hardwareRowMapper.mapRow(rs, rowNum);
            }
        },
        
        METER() {
            @Override
            public LiteInventoryBase mapRow(ResultSet rs, int rowNum) throws SQLException {
                return meterRowMapper.mapRow(rs, rowNum);
            }
        },
        
        GENERIC_INVENTORY() {
            @Override
            public LiteInventoryBase mapRow(ResultSet rs, int rowNum) throws SQLException {
                return inventoryRowMapper.mapRow(rs, rowNum);
            }
        };
        
        public static StrategyTypeMapper valueOf(int categoryDefId) {
            switch (categoryDefId) {
                case YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC :
                case YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC : return HARWARE;
                
                case YukonListEntryTypes.YUK_DEF_ID_INV_CAT_NON_YUKON_METER : return METER;
                
                default : return GENERIC_INVENTORY;
            }
        }
    }
}
