package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.providers.fields.DeviceAddressFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class DeviceAddressProvider implements PaoTypeProvider<DeviceAddressFields> {

	private YukonJdbcTemplate yukonJdbcTemplate;
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.DEVICEADDRESS;
	}

	@Override
	public Class<DeviceAddressFields> getRequiredFields() {
		return DeviceAddressFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, DeviceAddressFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto("DeviceAddress");
		params.addValue("DeviceId", paoIdentifier.getPaoId());
		params.addValue("MasterAddress", fields.getMasterAddress());
		params.addValue("SlaveAddress", fields.getSlaveAddress());
		params.addValue("PostCommWait", fields.getPostCommWait());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, DeviceAddressFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.update("DeviceAddress");
		params.addValue("MasterAddress", fields.getMasterAddress());
		params.addValue("SlaveAddress", fields.getSlaveAddress());
		params.addValue("PostCommWait", fields.getPostCommWait());
		
		sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("DELETE FROM " + getSupportedTable().name());
		sql.append("WHERE DeviceId").eq(paoIdentifier.getPaoId());
		
		yukonJdbcTemplate.update(sql);
	}

	@Autowired
	public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
}
