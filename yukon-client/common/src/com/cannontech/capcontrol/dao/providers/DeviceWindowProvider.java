package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.providers.fields.DeviceWindowFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.cannontech.common.pao.service.PaoTypeProvider;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class DeviceWindowProvider implements PaoTypeProvider<DeviceWindowFields> {

	private YukonJdbcTemplate yukonJdbcTemplate;
	
	@Override
	public PaoProviderTable getSupportedTable() {
		return PaoProviderTable.DEVICEWINDOW;
	}

	@Override
	public Class<DeviceWindowFields> getRequiredFields() {
		return DeviceWindowFields.class;
	}
	
	private void setupParameters(SqlParameterSink params, DeviceWindowFields fields) {
	    params.addValue("Type", fields.getType());
        params.addValue("WinOpen", fields.getWindowOpen());
        params.addValue("WinClose", fields.getWindowClose());
        params.addValue("AlternateOpen", fields.getAlternateOpen());
        params.addValue("AlternateClose", fields.getAlternateClose());
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, DeviceWindowFields fields) {		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto(getSupportedTable().name());
		params.addValue("DeviceId", paoIdentifier.getPaoId());
		setupParameters(params, fields);
		
		yukonJdbcTemplate.update(sql);
	}

	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, DeviceWindowFields fields) {
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
