package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.providers.fields.DeviceWindowFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class DeviceWindowProvider implements PaoTypeProvider<DeviceWindowFields> {

	private YukonJdbcTemplate yukonJdbcTemplate;
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.DEVICEWINDOW;
	}

	@Override
	public Class<DeviceWindowFields> getRequiredFields() {
		return DeviceWindowFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, DeviceWindowFields fields) {		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto("DeviceWindow");
		params.addValue("DeviceId", paoIdentifier.getPaoId());
		params.addValue("Type", fields.getType());
		params.addValue("WinOpen", fields.getWindowOpen());
		params.addValue("WinClose", fields.getWindowClose());
		params.addValue("AlternateOpen", fields.getAlternateOpen());
		params.addValue("AlternateClose", fields.getAlternateClose());
		
		yukonJdbcTemplate.update(sql);
	}

	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, DeviceWindowFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.update("DeviceWindow");
		params.addValue("Type", fields.getType());
		params.addValue("WinOpen", fields.getWindowOpen());
		params.addValue("WinClose", fields.getWindowClose());
		params.addValue("AlternateOpen", fields.getAlternateOpen());
		params.addValue("AlternateClose", fields.getAlternateClose());
		
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
