package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.providers.fields.CapbankAdditionalFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.cannontech.common.pao.service.PaoTypeProvider;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class CapbankAdditionalProvider implements PaoTypeProvider<CapbankAdditionalFields> {

	private YukonJdbcTemplate yukonJdbcTemplate;
	
	@Override
	public PaoProviderTable getSupportedTable() {
		return PaoProviderTable.CAPBANKADDITIONAL;
	}

	@Override
	public Class<CapbankAdditionalFields> getRequiredFields() {
		return CapbankAdditionalFields.class;
	}
	
	private void setupParameters(SqlParameterSink params, CapbankAdditionalFields fields) {
	    params.addValue("MaintenanceAreaID", fields.getMaintenanceAreaId());
        params.addValue("PoleNumber", fields.getPoleNumber());
        params.addValue("DriveDirections", fields.getDriveDirections());
        params.addValue("Latitude", fields.getLatitude());
        params.addValue("Longitude", fields.getLongitude());
        params.addValue("CapBankConfig", fields.getCapbankConfig());
        params.addValue("CommMedium", fields.getCommMedium());
        params.addValue("CommStrength", fields.getCommStrength());
        params.addValue("ExtAntenna", fields.getExtAntenna());
        params.addValue("AntennaType", fields.getAntennaType());
        params.addValue("LastMaintVisit", fields.getLastMaintenanceVisit());
        params.addValue("LastInspVisit", fields.getLastInspection());
        params.addValue("OpCountResetDate", fields.getOpCountResetDate());
        params.addValue("PotentialTransformer", fields.getPotentialTransformer());
        params.addValue("MaintenanceReqPend", fields.getMaintenanceRequired());
        params.addValue("OtherComments", fields.getOtherComments());
        params.addValue("OpTeamComments", fields.getOpTeamComments());
        params.addValue("CBCBattInstallDate", fields.getCbcInstallDate());
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, CapbankAdditionalFields fields) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.insertInto(getSupportedTable().name());
        params.addValue("DeviceID", paoIdentifier.getPaoId());
        setupParameters(params, fields);
        
        yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, CapbankAdditionalFields fields) {
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
