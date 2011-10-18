package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.providers.fields.FeederFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.cannontech.common.pao.service.PaoTypeProvider;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class FeederProvider implements PaoTypeProvider<FeederFields> {
	
	private YukonJdbcTemplate yukonJdbcTemplate;
	
	@Override
	public PaoProviderTable getSupportedTable() {
		return PaoProviderTable.CAPCONTROLFEEDER;
	};

	@Override
	public Class<FeederFields> getRequiredFields() {
		return FeederFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, FeederFields fields) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.insertInto(getSupportedTable().name());
        params.addValue("FeederID", paoIdentifier.getPaoId());
        params.addValue("CurrentVarLoadPointID", fields.getCurrentVarLoadPointId());
        params.addValue("CurrentWattLoadPointID", fields.getCurrentWattLoadPointId());
        params.addValue("MapLocationID", fields.getMapLocationId());
        params.addValue("CurrentVoltLoadPointID", fields.getcurrentVoltLoadPointId());
        params.addValue("MultiMonitorControl", fields.getMultiMonitorControl());
        params.addValue("UsePhaseData", fields.getUsePhaseData());
        params.addValue("PhaseB", fields.getPhaseB());
        params.addValue("PhaseC", fields.getPhaseC());
        params.addValue("ControlFlag", fields.getControlFlag());
        
        yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, FeederFields fields) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.update(getSupportedTable().name());
        params.addValue("CurrentVarLoadPointID", fields.getCurrentVarLoadPointId());
        params.addValue("CurrentWattLoadPointID", fields.getCurrentWattLoadPointId());
        params.addValue("MapLocationID", fields.getMapLocationId());
        params.addValue("CurrentVoltLoadPointID", fields.getcurrentVoltLoadPointId());
        params.addValue("MultiMonitorControl", fields.getMultiMonitorControl());
        params.addValue("UsePhaseData", fields.getUsePhaseData());
        params.addValue("PhaseB", fields.getPhaseB());
        params.addValue("PhaseC", fields.getPhaseC());
        params.addValue("ControlFlag", fields.getControlFlag());
        
        sql.append("WHERE FeederId").eq(paoIdentifier.getPaoId());
        
        yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {		
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("DELETE FROM").append(getSupportedTable());
        sql.append("WHERE FeederId").eq(paoIdentifier.getPaoId());
        
        yukonJdbcTemplate.update(sql);
	}
	
	@Autowired
	public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
