package com.cannontech.web.dev;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.core.dao.EnergyCompanyNotFoundException;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@CheckCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class MiscellaneousMethodController {
	private final Logger log = YukonLogManager.getLogger(MiscellaneousMethodController.class);
	
    @Autowired EnergyCompanyDao ecDao;
    
    @RequestMapping("/miscellaneousMethod/main")
    public void main(YukonUserContext userContext, ModelMap model) {
		try {
		    YukonEnergyCompany energyCompanyByOperator = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
		    model.addAttribute("energyCompanyId",energyCompanyByOperator.getEnergyCompanyId());
		} catch (EnergyCompanyNotFoundException e) {
			log.error("User is not associated with an Energy Company. No methods available.");
		}
    }
    
}
