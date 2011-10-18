package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.providers.fields.CapBankFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.cannontech.common.pao.service.PaoTypeProvider;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class CapbankProvider implements PaoTypeProvider<CapBankFields> {

	private YukonJdbcTemplate yukonJdbcTemplate;
	
	@Override
	public PaoProviderTable getSupportedTable() {
		return PaoProviderTable.CAPBANK;
	};

	@Override
	public Class<CapBankFields> getRequiredFields() {
		return CapBankFields.class;
	}
	
	private void setupParameters(SqlParameterSink params, CapBankFields fields) {
	    params.addValue("OperationalState", fields.getOperationalState().name());
        params.addValue("ControllerType", fields.getControllerType());
        params.addValue("ControlDeviceId", fields.getControlDeviceId());
        params.addValue("ControlPointId", fields.getControlPointId());
        params.addValue("BankSize", fields.getBankSize());
        params.addValue("TypeOfSwitch", fields.getTypeOfSwitch());
        params.addValue("SwitchManufacture", fields.getSwitchManufacturer());
        params.addValue("MapLocationId", fields.getMapLocationId());
        params.addValue("RecloseDelay", fields.getRecloseDelay());
        params.addValue("MaxDailyOps", fields.getMaxDailyOps());
        params.addValue("MaxOpDisable", fields.getMaxOpDisable());
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, CapBankFields fields) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.insertInto(getSupportedTable().name());
        params.addValue("DeviceId", paoIdentifier.getPaoId());
        setupParameters(params, fields);
        
        yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, CapBankFields fields) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.update(getSupportedTable().name());
        setupParameters(params, fields);
        
        sql.append("WHERE DeviceID").eq(paoIdentifier.getPaoId());
        
        yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("DELETE FROM").append(getSupportedTable());
        sql.append("WHERE DeviceID").eq(paoIdentifier.getPaoId());
        
        yukonJdbcTemplate.update(sql);
	}
	
	@Autowired
	public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
