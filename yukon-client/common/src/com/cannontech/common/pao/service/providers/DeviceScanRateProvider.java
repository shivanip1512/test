package com.cannontech.common.pao.service.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.cannontech.common.pao.service.PaoTypeProvider;
import com.cannontech.common.pao.service.providers.fields.DeviceScanRateFields;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class DeviceScanRateProvider implements PaoTypeProvider<DeviceScanRateFields>{

	private YukonJdbcTemplate yukonJdbcTemplate;
	
	@Override
	public PaoProviderTable getSupportedTable() {
		return PaoProviderTable.DEVICESCANRATE;
	}

	@Override
	public Class<DeviceScanRateFields> getRequiredFields() {
		return DeviceScanRateFields.class;
	}
	
	private void setupParameters(SqlParameterSink params, DeviceScanRateFields fields) {
	    params.addValue("ScanType", fields.getScanType());
        params.addValue("IntervalRate", fields.getIntervalRate());
        params.addValue("ScanGroup", fields.getScanGroup());
        params.addValue("AlternateRate", fields.getAlternateRate());
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, DeviceScanRateFields fields) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto(getSupportedTable().name());
		params.addValue("DeviceId", paoIdentifier.getPaoId());
		setupParameters(params, fields);
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, DeviceScanRateFields fields) {
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
