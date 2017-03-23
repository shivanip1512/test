package com.cannontech.web.dev;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;


@Controller
public class PaoDefinitionTestingController {
    
    @Autowired private PaoDefinitionDao paoDefinitionDao;


    @RequestMapping("paoDefinition")
    public String paoDefinitionTesting() {
        return "paoDefinitionTesting.jsp";
    }
    
    @RequestMapping("reloadPaoDefinition")
    public String reloadPaoDefinition(FlashScope flash) {
        paoDefinitionDao.reload();
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.paoDefinitionTesting.reloadSuccessful"));
        return "redirect:paoDefinition";
    }
    
    @RequestMapping("reloadOverrideFile")
    public String reloadOverrideFile(FlashScope flash) {
        //paoDefinitionDao.reload();
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.paoDefinitionTesting.reloadOverrideSuccessful"));
        return "redirect:paoDefinition";
    }
}
