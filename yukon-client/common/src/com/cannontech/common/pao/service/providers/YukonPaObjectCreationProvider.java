package com.cannontech.common.pao.service.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.impl.PaoCreationTypeProvider;
import com.cannontech.common.pao.service.providers.fields.YukonPaObjectFields;
import com.cannontech.common.util.SqlStatementBuilder;
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
        
        sql.append("INSERT INTO YukonPaObject");
        sql.append("(PaObjectId,Category,PaoClass,");
        sql.append(" PaoName,Type,Description,DisableFlag,PaoStatistics)");
        sql.values(paoIdentifier.getPaoId(),
                   paoIdentifier.getPaoType().getPaoCategory(),
                   paoIdentifier.getPaoType().getPaoClass(),
                   paoFields.getName(),
                   paoIdentifier.getPaoType().getPaoTypeName(),
                   paoFields.getDescription(),
                   YNBoolean.valueOf(paoFields.isDisabled()),
                   paoFields.getStatistics());
        
        yukonJdbcTemplate.update(sql);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
