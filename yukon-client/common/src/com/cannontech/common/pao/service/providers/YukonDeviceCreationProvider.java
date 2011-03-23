package com.cannontech.common.pao.service.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.providers.fields.NullFields;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;

public class YukonDeviceCreationProvider extends BaseCreationProvider<NullFields> {

    YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public float getOrder() {
        return 1;
    }

    @Override
    public boolean isTypeSupported(PaoType paoType) {
        //TODO Make function to check if we are a device.
        return true;
    }

    @Override
    public Class<NullFields> getRequiredFields() {
        return NullFields.class;
    }

    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, NullFields fields) {
        //This would normally call a Dao, but it is a simple one line insert in a very base class..
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("INSERT INTO Device");
        sql.append("(DeviceId,AlarmInhibit,ControlInhibit)");
        sql.values(paoIdentifier.getPaoId(),
                      YNBoolean.NO,
                      YNBoolean.NO);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
