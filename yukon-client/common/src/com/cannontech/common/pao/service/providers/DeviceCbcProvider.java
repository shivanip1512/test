package com.cannontech.common.pao.service.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.cannontech.common.pao.service.PaoTypeProvider;
import com.cannontech.common.pao.service.providers.fields.DeviceCbcFields;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class DeviceCbcProvider implements PaoTypeProvider<DeviceCbcFields> {

	private YukonJdbcTemplate yukonJdbcTemplate;
	
	@Override
	public PaoProviderTable getSupportedTable() {
		return PaoProviderTable.DEVICECBC;
	}

	@Override
	public Class<DeviceCbcFields> getRequiredFields() {
		return DeviceCbcFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier,DeviceCbcFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto(getSupportedTable().name());
		params.addValue("DeviceId", paoIdentifier.getPaoId());
		params.addValue("SerialNumber", fields.getSerialNumber());
		params.addValue("RouteId", fields.getRouteId());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, DeviceCbcFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.update(getSupportedTable().name());
		params.addValue("SerialNumber", fields.getSerialNumber());
		params.addValue("RouteId", fields.getRouteId());
		
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
