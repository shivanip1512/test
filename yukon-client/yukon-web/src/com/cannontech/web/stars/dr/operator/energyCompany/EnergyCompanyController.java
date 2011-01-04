package com.cannontech.web.stars.dr.operator.energyCompany;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.service.EnergyCompanyInfoFragmentHelper;

@RequestMapping("/operator/energyCompany/*")
@Controller
public class EnergyCompanyController {

    /* Energy Company Operations Page*/
    @RequestMapping
    public String home(YukonUserContext userContext, ModelMap modelMap) {
        return "operator/energyCompany/home.jsp";
    }
    
    /* Energy Company Settings Page*/
    @RequestMapping
    public String generalInfo(YukonUserContext userContext, ModelMap modelMap, int ecId, EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        modelMap.addAttribute("energyCompanyName", "Norris Public Power District");
        modelMap.addAttribute("mode", "VIEW");
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        return "operator/energyCompany/generalInfo.jsp";
    }
    
    /* Energy Company Settings Page*/
    @RequestMapping
    public String editGeneralInfo(YukonUserContext userContext, ModelMap modelMap, int ecId, EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        modelMap.addAttribute("energyCompanyName", "Norris Public Power District");
        modelMap.addAttribute("mode", "EDIT");
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        return "operator/energyCompany/generalInfo.jsp";
    }
}