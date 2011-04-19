package com.cannontech.common.pao.service.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.impl.PaoCreationTypeProvider;
import com.cannontech.common.pao.service.providers.fields.YukonPaObjectFields;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;

public class YukonPaObjectCreationProvider extends BaseCreationProvider<YukonPaObjectFields> {
    
    YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public int compareTo(PaoCreationTypeProvider<YukonPaObjectFields> o) {
        return Float.compare(getOrder(), o.getOrder());
    }
    
    @Override
    public float getOrder() {
        return 0;
    }
    
    @Override
    public boolean isTypeSupported(PaoType paoType) {
        return true;
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
        p.addValue("Category", paoIdentifier.getPaoType().getPaoCategory());
        p.addValue("PaoClass", paoIdentifier.getPaoType().getPaoClass());
        p.addValue("Type", paoIdentifier.getPaoType());
        p.addValue("PaoName", paoFields.getName());
        p.addValue("Description", paoFields.getDescription());
        p.addValue("DisableFlag", YNBoolean.valueOf(paoFields.isDisabled()));
        p.addValue("PaoStatistics", paoFields.getStatistics());
        
        yukonJdbcTemplate.update(sql);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
