package com.cannontech.common.pao.service.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.cannontech.common.pao.service.PaoTypeProvider;
import com.cannontech.common.pao.service.providers.fields.DeviceFields;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class YukonDeviceProvider implements PaoTypeProvider<DeviceFields> {

    private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public PaoProviderTable getSupportedTable() {
    	return PaoProviderTable.DEVICE;
    }
    
    @Override
    public Class<DeviceFields> getRequiredFields() {
        return DeviceFields.class;
    }

    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, DeviceFields fields) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto(getSupportedTable().name());
		params.addValue("DeviceId", paoIdentifier.getPaoId());
		params.addValue("AlarmInhibit", fields.getAlarmInhibit());
		params.addValue("ControlInhibit", fields.getControlInhibit());
		
		yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void handleUpdate(PaoIdentifier paoIdentifier, DeviceFields fields) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.update(getSupportedTable().name());
		params.addValue("AlarmInhibit", fields.getAlarmInhibit());
		params.addValue("ControlInhibit", fields.getControlInhibit());
		
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
