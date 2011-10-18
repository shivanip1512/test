package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.providers.fields.SpecialAreaFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.cannontech.common.pao.service.PaoTypeProvider;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class SpecialAreaProvider implements PaoTypeProvider<SpecialAreaFields> {

	private YukonJdbcTemplate yukonJdbcTemplate;
	
	@Override
	public PaoProviderTable getSupportedTable() {
		return PaoProviderTable.CAPCONTROLSPECIALAREA;
	};

	@Override
	public Class<SpecialAreaFields> getRequiredFields() {
		return SpecialAreaFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, SpecialAreaFields fields) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.insertInto(getSupportedTable().name());
        params.addValue("AreaId", paoIdentifier.getPaoId());
        params.addValue("VoltReductionPointId", fields.getVoltReductionPointId());
        
        yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, SpecialAreaFields fields) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.update(getSupportedTable().name());
        params.addValue("VoltReductionPointId", fields.getVoltReductionPointId());
        
        sql.append("WHERE AreaId").eq(paoIdentifier.getPaoId());
        
        yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("DELETE FROM").append(getSupportedTable());
        sql.append("WHERE AreaId").eq(paoIdentifier.getPaoId());
        
        yukonJdbcTemplate.update(sql);
	}
	
	@Autowired
	public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
