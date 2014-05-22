package com.cannontech.web.dev;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.AuthorizeByCparm;

@Controller
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class MiscellaneousMethodController {
    
    @Autowired EnergyCompanyDao ecDao;
    
    @RequestMapping("/miscellaneousMethod/main")
    public void main(YukonUserContext userContext, ModelMap model) {
        
        YukonEnergyCompany energyCompanyByOperator = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
       
        model.addAttribute("energyCompanyId",energyCompanyByOperator.getEnergyCompanyId());
    }
    
}
