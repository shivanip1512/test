package com.cannontech.web.admin.energyCompany.service;

import org.springframework.ui.ModelMap;

import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;


public class EnergyCompanyInfoFragmentHelper {

    public static void setupModelMapBasics(EnergyCompanyInfoFragment energyCompanyInfoFragment, ModelMap modelMap) {
        modelMap.addAttribute("energyCompanyInfoFragment", energyCompanyInfoFragment);
        modelMap.addAttribute("ecId", energyCompanyInfoFragment.getEnergyCompanyId());
        modelMap.addAttribute("energyCompanyName", energyCompanyInfoFragment.getCompanyName());
    }
}