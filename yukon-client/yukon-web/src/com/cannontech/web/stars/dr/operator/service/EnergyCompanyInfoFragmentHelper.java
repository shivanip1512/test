package com.cannontech.web.stars.dr.operator.service;

import org.springframework.ui.ModelMap;

import com.cannontech.web.stars.dr.operator.energyCompany.EnergyCompanyInfoFragment;


public class EnergyCompanyInfoFragmentHelper {

    public static void setupModelMapBasics(EnergyCompanyInfoFragment energyCompanyInfoFragment, ModelMap modelMap) {
        modelMap.addAttribute("energyCompanyInfoFragment", energyCompanyInfoFragment);
        modelMap.addAttribute("ecId", energyCompanyInfoFragment.getEnergyCompanyId());
        modelMap.addAttribute("companyName", energyCompanyInfoFragment.getCompanyName());
    }
}