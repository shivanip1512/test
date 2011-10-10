package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.providers.fields.VoltageRegulatorFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class VoltageRegulatorProvider implements PaoTypeProvider<VoltageRegulatorFields> {
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public PaoProviderTableEnum getSupportedTable() {
    	return PaoProviderTableEnum.REGULATOR;
    };

    @Override
    public Class<VoltageRegulatorFields> getRequiredFields() {
        return VoltageRegulatorFields.class;
    }

    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, VoltageRegulatorFields fields) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink p = sql.insertInto(getSupportedTable().name());
        p.addValue("RegulatorId", paoIdentifier.getPaoId());
        p.addValue("KeepAliveTimer", fields.getKeepAliveTimer());
        p.addValue("KeepAliveConfig", fields.getKeepAliveConfig());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void handleUpdate(PaoIdentifier paoIdentifier, VoltageRegulatorFields fields) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink p = sql.update(getSupportedTable().name());
        p.addValue("KeepAliveTimer", fields.getKeepAliveTimer());
        p.addValue("KeepAliveConfig", fields.getKeepAliveConfig());
        sql.append("WHERE RegulatorId").eq(paoIdentifier.getPaoId());
        
        yukonJdbcTemplate.update(sql);    
    }
    
    @Override
    public void handleDeletion(PaoIdentifier paoIdentifier) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("DELETE FROM").append(getSupportedTable().name());
    	sql.append("WHERE RegulatorId").eq(paoIdentifier.getPaoId());
    	
    	yukonJdbcTemplate.update(sql);
    };

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
