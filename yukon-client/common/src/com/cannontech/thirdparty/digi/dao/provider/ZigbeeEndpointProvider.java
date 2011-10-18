package com.cannontech.thirdparty.digi.dao.provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.cannontech.common.pao.service.PaoTypeProvider;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.thirdparty.digi.dao.provider.fields.ZigbeeEndpointFields;

public class ZigbeeEndpointProvider implements PaoTypeProvider<ZigbeeEndpointFields> {

    private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public Class<ZigbeeEndpointFields> getRequiredFields() {
        return ZigbeeEndpointFields.class;
    }
    
	@Override
	public PaoProviderTable getSupportedTable() {
		return PaoProviderTable.ZBENDPOINT;
	}
	
	private void setupParameters(SqlParameterSink params, ZigbeeEndpointFields fields) {
	    params.addValue("InstallCode", fields.getInstallCode().toUpperCase());
        params.addValue("MacAddress", fields.getMacAddress().toUpperCase());
        params.addValue("NodeId", fields.getNodeId());
        params.addValue("DestinationEndPointId", fields.getEndPointId());
	}
    
    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, ZigbeeEndpointFields fields) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.insertInto(getSupportedTable().name());
        params.addValue("DeviceId", paoIdentifier.getPaoId());
        setupParameters(params, fields);
        
        yukonJdbcTemplate.update(sql);
    }

	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, ZigbeeEndpointFields fields) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.update(getSupportedTable().name());
        setupParameters(params, fields);

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