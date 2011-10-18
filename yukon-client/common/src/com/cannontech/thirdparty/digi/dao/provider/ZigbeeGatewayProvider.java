package com.cannontech.thirdparty.digi.dao.provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.cannontech.common.pao.service.PaoTypeProvider;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.thirdparty.digi.dao.provider.fields.ZigbeeGatewayFields;

public class ZigbeeGatewayProvider implements PaoTypeProvider<ZigbeeGatewayFields> {

	private YukonJdbcTemplate yukonJdbcTemplate;

	@Override
	public PaoProviderTable getSupportedTable() {
		return PaoProviderTable.ZBGATEWAY;
	}

	@Override
	public Class<ZigbeeGatewayFields> getRequiredFields() {
		return ZigbeeGatewayFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, ZigbeeGatewayFields fields) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.insertInto(getSupportedTable().name());
        params.addValue("DeviceId", paoIdentifier.getPaoId());
        params.addValue("FirmwareVersion", fields.getFirmwareVersion());
        params.addValue("MacAddress", fields.getMacAddress().toUpperCase());
        
        yukonJdbcTemplate.update(sql);
	}

	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, ZigbeeGatewayFields fields) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.update(getSupportedTable().name());
        params.addValue("FirmwareVersion",fields.getFirmwareVersion());
        params.addValue("MacAddress",fields.getMacAddress().toUpperCase());

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
