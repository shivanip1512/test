package com.cannontech.common.pao.service.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;
import com.cannontech.common.pao.service.providers.fields.YukonPaObjectFields;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;

public class YukonPaObjectProvider implements PaoTypeProvider<YukonPaObjectFields> {
    
    YukonJdbcTemplate yukonJdbcTemplate;
    
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.YUKONPAOBJECT;
	}

    @Override
    public Class<YukonPaObjectFields> getRequiredFields() {
        return YukonPaObjectFields.class;
    }

    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, YukonPaObjectFields paoFields) {
        //This would normally call a Dao, but it is a simple one line insert in a very base class..
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink p = sql.insertInto("YukonPaObject");
        p.addValue("PAObjectId",paoIdentifier.getPaoId());
        p.addValue("Category", paoIdentifier.getPaoType().getPaoCategory());
        p.addValue("PaoClass", paoIdentifier.getPaoType().getPaoClass());
        p.addValue("Type", paoIdentifier.getPaoType());
        p.addValue("PaoName", paoFields.getName());
        p.addValue("Description", paoFields.getDescription());
        p.addValue("DisableFlag", YNBoolean.valueOf(paoFields.isDisabled()));
        p.addValue("PaoStatistics", paoFields.getStatistics());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void handleUpdate(PaoIdentifier paoIdentifier, YukonPaObjectFields paoFields) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	SqlParameterSink params = sql.update("YukonPAObject");
    	params.addValue("Category", paoIdentifier.getPaoType().getPaoCategory());
        params.addValue("PaoClass", paoIdentifier.getPaoType().getPaoClass());
        params.addValue("Type", paoIdentifier.getPaoType());
        params.addValue("PaoName", paoFields.getName());
        params.addValue("Description", paoFields.getDescription());
        params.addValue("DisableFlag", YNBoolean.valueOf(paoFields.isDisabled()));
        params.addValue("PaoStatistics", paoFields.getStatistics());
        
        sql.append("WHERE PAObjectID").eq(paoIdentifier.getPaoId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void handleDeletion(PaoIdentifier paoIdentifier) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("DELETE FROM YukonPaObject");
    	sql.append("WHERE PAObjectId").eq(paoIdentifier.getPaoId());
    	
    	yukonJdbcTemplate.update(sql);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
