package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.providers.fields.SubstationBusFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class SubstationBusProvider implements PaoTypeProvider<SubstationBusFields> {

	private YukonJdbcTemplate yukonJdbcTemplate;
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.CAPCONTROLSUBSTATIONBUS;
	};

	@Override
	public Class<SubstationBusFields> getRequiredFields() {
		return SubstationBusFields.class;
	}

	private void setupParameters(SqlParameterSink params, SubstationBusFields fields) {
	    params.addValue("CurrentVarLoadPointID", fields.getCurrentVarLoadPointId());
        params.addValue("CurrentWattLoadPointID", fields.getCurrentWattLoadPointId());
        params.addValue("MapLocationID", fields.getMapLocationId());
        params.addValue("CurrentVoltLoadPointID", fields.getCurrentVoltLoadPointId());
        params.addValue("AltSubID", fields.getAltSubId());
        params.addValue("SwitchPointID", fields.getSwitchPointId());
        params.addValue("DualBusEnabled", fields.getDualBusEnabled());
        params.addValue("MultiMonitorControl", fields.getMultiMonitorControl());
        params.addValue("UsePhaseData", fields.getUsePhaseData());
        params.addValue("PhaseB", fields.getPhaseB());
        params.addValue("PhaseC", fields.getPhaseC());
        params.addValue("ControlFlag", fields.getControlFlag());
        params.addValue("VoltReductionPointId", fields.getVoltReductionPointId());
        params.addValue("DisableBusPointId", fields.getDisableBusPointId());
	}
	
	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, SubstationBusFields fields) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.insertInto(getSupportedTable().name());
        params.addValue("SubstationBusID", paoIdentifier.getPaoId());
        setupParameters(params, fields);

        yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, SubstationBusFields fields) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.update(getSupportedTable().name());
        setupParameters(params, fields);
        
        sql.append("WHERE SubstationBusID").eq(paoIdentifier.getPaoId());

        yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("DELETE FROM " + getSupportedTable().name());
        sql.append("WHERE SubstationBusId").eq(paoIdentifier.getPaoId());
        
        yukonJdbcTemplate.update(sql);
	}

	@Autowired
	public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
