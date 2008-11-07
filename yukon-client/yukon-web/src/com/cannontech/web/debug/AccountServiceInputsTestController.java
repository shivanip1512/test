package com.cannontech.web.debug;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.account.service.result.AccountActionResult;
import com.cannontech.util.ServletUtil;

public class AccountServiceInputsTestController extends MultiActionController{
    
    private AccountService accountService;
    
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        return returnMav(request, new ArrayList<String>(0), new ArrayList<String>(0));
    }
    
    public ModelAndView addAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        return null;
    }
    
    public ModelAndView deleteAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        List<String> results = new ArrayList<String>();
        List<String> errorReasons = new ArrayList<String>();
        
        String accountNumber = ServletRequestUtils.getRequiredStringParameter(request, "accountNumber");
        LiteEnergyCompany liteEnergyCompany = new LiteEnergyCompany();
        try {
            AccountActionResult result = accountService.deleteAccount(accountNumber, liteEnergyCompany);
            results.add(result.toString());
        } catch (IllegalArgumentException e) {
            errorReasons.add(e.getMessage());
        }
        
        return returnMav(request, results, errorReasons);
    }
    
    public ModelAndView updateAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        return null;
    }
    
    // HELPERS
    private ModelAndView returnMav(HttpServletRequest request, List<String> results, List<String> errorReasons) {
        
        ModelAndView mav = new ModelAndView("home.jsp");
     
        // re-populate fields
        mav.addAllObjects(ServletUtil.getParameterMap(request));
        mav.addObject("results", results);
        mav.addObject("errorReasons", errorReasons);
        
        return mav;
    }
    
    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

}
