package com.cannontech.thirdparty.digi.dao.provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.cannontech.common.pao.service.PaoTypeProvider;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.thirdparty.digi.dao.provider.fields.DigiGatewayFields;

public class DigiGatewayProvider implements PaoTypeProvider<DigiGatewayFields> {

    private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public PaoProviderTable getSupportedTable() {
    	return PaoProviderTable.DIGIGATEWAY;
    }

    @Override
    public Class<DigiGatewayFields> getRequiredFields() {
        return DigiGatewayFields.class;
    }

    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, DigiGatewayFields fields) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.insertInto(getSupportedTable().name());
        params.addValue("DeviceId", paoIdentifier.getPaoId());
        params.addValue("DigiId", fields.getDigiId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void handleUpdate(PaoIdentifier paoIdentifier, DigiGatewayFields fields) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink param = sql.update(getSupportedTable().name());
        param.addValue("DigiId", fields.getDigiId());

        sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void handleDeletion(PaoIdentifier paoIdentifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("DELETE FROM").append(getSupportedTable());
        sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
        
        yukonJdbcTemplate.update(sql);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
