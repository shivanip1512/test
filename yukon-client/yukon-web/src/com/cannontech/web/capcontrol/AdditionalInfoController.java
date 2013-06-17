package com.cannontech.web.capcontrol;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.model.CapbankAdditional;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.web.capcontrol.models.ViewableSubBus;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/addInfo/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class AdditionalInfoController {

	@Autowired private CapbankDao capbankDao;
	@Autowired private CapControlCache cache;
	@Autowired private CapControlWebUtilsService capControlWebUtilsService; 
	
    @RequestMapping
    public String bank(ModelMap model, int bankId) {
        CapbankAdditional capBankAdd = capbankDao.getCapbankAdditional(bankId);
        model.addAttribute("capBankAdd", capBankAdd);
        return "capBankAddInfoPopup.jsp";
    }

    @RequestMapping
    public String bus(ModelMap model, int busId) {
        SubBus bus = cache.getSubBus(busId);
        ViewableSubBus viewable = capControlWebUtilsService.createViewableSubBus(Collections.singletonList(bus)).get(0);
        model.addAttribute("bus", viewable);
        return "busInfo.jsp";
    }
    
}