package com.cannontech.web.support.development;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.AuthorizeByCparm;

@Controller
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class MiscellaneousIsocMethodController {
    private static final Logger log =
        YukonLogManager.getLogger(MiscellaneousIsocMethodController.class);
    private YukonJdbcTemplate yukonJdbcTemplate;
    private YukonEnergyCompanyService yukonEnergyCompanyService;
    
    
    @RequestMapping("/development/miscellaneousMethod/insertProgramTypes")
    public String insertProgramTypes(LiteYukonUser user, FlashScope flashScope) {
        try {
            SqlStatementBuilder getMax = new SqlStatementBuilder();
            getMax.append("SELECT max(CCurtProgramTypeId)");
            getMax.append("FROM CCurtProgramType");
            Integer currentMax = yukonJdbcTemplate.queryForObject(getMax, new IntegerRowMapper(true));
            
            int primaryKey = currentMax == null ? 1 : currentMax + 1;
            
            YukonEnergyCompany energyCompany;
            try {
                energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
            } catch (EmptyResultDataAccessException e) {
                flashScope.setWarning(YukonMessageSourceResolvable.createDefaultWithoutCode("Current User must be an Energy Company Operator to insert Program Types"));
                return "redirect:main";
            }
            
            SqlStatementBuilder sql1 = new SqlStatementBuilder();
            SqlParameterSink insert1 = sql1.insertInto("CCurtProgramType");
            insert1.addValue("CCurtProgramTypeId", primaryKey++);
            insert1.addValue("EnergyCompanyId", energyCompany.getEnergyCompanyId());
            insert1.addValue("CcurtProgramTypeStrategy", "isocNotification");
            insert1.addValue("CcurtProgramTypeName", "Capacity/Contingency");
            yukonJdbcTemplate.update(sql1);
            
            SqlStatementBuilder sql2 = new SqlStatementBuilder();
            SqlParameterSink insert2 = sql2.insertInto("CCurtProgramType");
            insert2.addValue("CCurtProgramTypeId", primaryKey++);
            insert2.addValue("EnergyCompanyId", energyCompany.getEnergyCompanyId());
            insert2.addValue("CcurtProgramTypeStrategy", "isocDirect");
            insert2.addValue("CcurtProgramTypeName", "Direct Control");
            yukonJdbcTemplate.update(sql2);
            
            SqlStatementBuilder sql3 = new SqlStatementBuilder();
            SqlParameterSink insert3 = sql3.insertInto("CCurtProgramType");
            insert3.addValue("CCurtProgramTypeId", primaryKey++);
            insert3.addValue("EnergyCompanyId", energyCompany.getEnergyCompanyId());
            insert3.addValue("CcurtProgramTypeStrategy", "genericAccounting");
            insert3.addValue("CcurtProgramTypeName", "Accounting");
            yukonJdbcTemplate.update(sql3);
            
            SqlStatementBuilder sql4 = new SqlStatementBuilder();
            SqlParameterSink insert4 = sql4.insertInto("CCurtProgramType");
            insert4.addValue("CCurtProgramTypeId", primaryKey++);
            insert4.addValue("EnergyCompanyId", energyCompany.getEnergyCompanyId());
            insert4.addValue("CcurtProgramTypeStrategy", "isocSameDay");
            insert4.addValue("CcurtProgramTypeName", "Economic");
            yukonJdbcTemplate.update(sql4);
            
            flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Four Program Types have been added to the database"));
        } catch (Exception e) {
            log.warn("caught exception in insertProgramTypes", e);
            flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode("Unable to add Program Types: " + e.getMessage()));
        }
        
        return "redirect:main";
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }
}
