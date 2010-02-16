package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class OperatorAccountInformationWidget extends WidgetControllerBase {

	private AccountService accountService;
	private CustomerAccountDao customerAccountDao;
	
	@Override
	public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView mav = new ModelAndView("operatorAccountInformationWidget/render.jsp");
		
		int accountId = WidgetParameterHelper.getRequiredIntParameter(request, "accountId");
		int energyCompanyId = WidgetParameterHelper.getRequiredIntParameter(request, "energyCompanyId");
		mav.addObject("accountId", accountId);
		mav.addObject("energyCompanyId", energyCompanyId);
		
		AccountDto accountDto = accountService.getAccountDto(accountId, energyCompanyId);
		CustomerAccount customerAccount = customerAccountDao.getById(accountId);
		mav.addObject("dto", accountDto);
		mav.addObject("accountNumber", customerAccount.getAccountNumber());
		
		return mav;
	}
	
	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	@Autowired
	public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
}
