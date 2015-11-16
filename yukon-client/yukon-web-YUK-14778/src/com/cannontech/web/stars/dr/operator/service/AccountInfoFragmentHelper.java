package com.cannontech.web.stars.dr.operator.service;

import org.springframework.ui.ModelMap;

import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;

public class AccountInfoFragmentHelper {

	public static void setupModelMapBasics(AccountInfoFragment accountInfoFragment, ModelMap modelMap) {
		
		modelMap.addAttribute("accountInfoFragment", accountInfoFragment);
		modelMap.addAttribute("accountId", accountInfoFragment.getAccountId());
		modelMap.addAttribute("accountNumber", accountInfoFragment.getAccountNumber());
	}
}
