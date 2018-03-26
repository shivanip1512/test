package com.cannontech.web.dev;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;

@Controller
public class CollectionActionsTestingController {
  
    @RequestMapping("collectionActionsTesting")
    public String collectionActionsTesting(ModelMap model) {
        model.addAttribute("description", "Page for Testing Collection Actions.  Click on the Clear Cache button to clear all Collection Actions from cache.");
        return "collectionActionsTesting.jsp";
    }
    
    @RequestMapping("clearCache")
    public String clearCache(FlashScope flash) {
        //TODO: Clear Cache
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Collection Actions cache has been cleared."));
        return "redirect:collectionActionsTesting";
    }
}
