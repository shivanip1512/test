package com.cannontech.common.pao.service.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTable;
import com.cannontech.common.pao.service.PaoTypeProvider;
import com.cannontech.common.pao.service.providers.fields.YukonPaObjectFields;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;

public class YukonPaObjectProvider implements PaoTypeProvider<YukonPaObjectFields> {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    
	@Override
	public PaoProviderTable getSupportedTable() {
		return PaoProviderTable.YUKONPAOBJECT;
	}

    @Override
    public Class<YukonPaObjectFields> getRequiredFields() {
        return YukonPaObjectFields.class;
    }

    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, YukonPaObjectFields paoFields) {
        //This would normally call a Dao, but it is a simple one line insert in a very base class..
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.insertInto(getSupportedTable().name());
        params.addValue("PAObjectId",paoIdentifier.getPaoId());
        setupParameters(params, paoIdentifier, paoFields);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void handleUpdate(PaoIdentifier paoIdentifier, YukonPaObjectFields paoFields) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	SqlParameterSink params = sql.update(getSupportedTable().name());
    	setupParameters(params, paoIdentifier, paoFields);
        
        sql.append("WHERE PAObjectID").eq(paoIdentifier.getPaoId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    private void setupParameters(SqlParameterSink params, PaoIdentifier paoIdentifier, YukonPaObjectFields fields) {
    	params.addValue("Category", paoIdentifier.getPaoType().getPaoCategory());
        params.addValue("PaoClass", paoIdentifier.getPaoType().getPaoClass());
        params.addValue("Type", paoIdentifier.getPaoType());
        params.addValue("PaoName", fields.getName());
        params.addValue("Description", fields.getDescription());
        params.addValue("DisableFlag", YNBoolean.valueOf(fields.isDisabled()));
        params.addValue("PaoStatistics", fields.getStatistics());
    }
    
    @Override
    public void handleDeletion(PaoIdentifier paoIdentifier) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("DELETE FROM " + getSupportedTable().name());
    	sql.append("WHERE PAObjectId").eq(paoIdentifier.getPaoId());
    	
    	yukonJdbcTemplate.update(sql);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
