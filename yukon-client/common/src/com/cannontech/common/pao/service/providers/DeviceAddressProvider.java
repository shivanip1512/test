package com.cannontech.common.pao.service.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.cannontech.common.pao.service.PaoTypeProvider;
import com.cannontech.common.pao.service.providers.fields.DeviceAddressFields;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class DeviceAddressProvider implements PaoTypeProvider<DeviceAddressFields> {

	private YukonJdbcTemplate yukonJdbcTemplate;
	
	@Override
	public PaoProviderTable getSupportedTable() {
		return PaoProviderTable.DEVICEADDRESS;
	}

	@Override
	public Class<DeviceAddressFields> getRequiredFields() {
		return DeviceAddressFields.class;
	}
	
	private void setupParameters(SqlParameterSink params, DeviceAddressFields fields) {
	    params.addValue("MasterAddress", fields.getMasterAddress());
        params.addValue("SlaveAddress", fields.getSlaveAddress());
        params.addValue("PostCommWait", fields.getPostCommWait());
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, DeviceAddressFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto(getSupportedTable().name());
		params.addValue("DeviceId", paoIdentifier.getPaoId());
		setupParameters(params, fields);
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, DeviceAddressFields fields) {
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
