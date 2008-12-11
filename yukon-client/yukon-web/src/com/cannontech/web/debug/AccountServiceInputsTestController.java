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

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.bulk.field.impl.AccountDto;
import com.cannontech.common.bulk.field.impl.UpdatableAccount;
import com.cannontech.common.model.Address;
import com.cannontech.common.model.SiteInformation;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.util.ServletUtil;

public class AccountServiceInputsTestController extends MultiActionController{
    
    private AccountService accountService;
    private YukonUserDao yukonUserDao;
    
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        return returnMav(request, new ArrayList<String>(0), new ArrayList<String>(0));
    }
    
    public ModelAndView addAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        List<String> results = new ArrayList<String>();
        List<String> errorReasons = new ArrayList<String>();
        
        String accountNumber = ServletRequestUtils.getRequiredStringParameter(request, "accountNumber");
        String lastName = ServletRequestUtils.getRequiredStringParameter(request, "lastName");
        String firstName = ServletRequestUtils.getRequiredStringParameter(request, "firstName");
        String homePhone = ServletRequestUtils.getRequiredStringParameter(request, "homePhone");
        String workPhone = ServletRequestUtils.getRequiredStringParameter(request, "workPhone");
        String emailAddress = ServletRequestUtils.getRequiredStringParameter(request, "emailAddress");
        String altTrackingNum = ServletRequestUtils.getRequiredStringParameter(request, "altTrackingNum");
        String mapNum = ServletRequestUtils.getRequiredStringParameter(request, "mapNum");
        String isCommercial = ServletRequestUtils.getRequiredStringParameter(request, "isCommercial");
        String companyName = ServletRequestUtils.getRequiredStringParameter(request, "companyName");
        String streetAddress1 = ServletRequestUtils.getRequiredStringParameter(request, "streetAddress1");
        String streetAddress2 = ServletRequestUtils.getRequiredStringParameter(request, "streetAddress2");
        String city = ServletRequestUtils.getRequiredStringParameter(request, "city");
        String state = ServletRequestUtils.getRequiredStringParameter(request, "state");
        String zip = ServletRequestUtils.getRequiredStringParameter(request, "zip");
        String county = ServletRequestUtils.getRequiredStringParameter(request, "county");
        
        String billingAddress1 = ServletRequestUtils.getRequiredStringParameter(request, "billingAddress1");
        String billingAddress2 = ServletRequestUtils.getRequiredStringParameter(request, "billingAddress2");
        String billingCity = ServletRequestUtils.getRequiredStringParameter(request, "billingCity");
        String billingState = ServletRequestUtils.getRequiredStringParameter(request, "billingState");
        String billingZip = ServletRequestUtils.getRequiredStringParameter(request, "billingZip");
        String billingCounty = ServletRequestUtils.getRequiredStringParameter(request, "billingCounty");
        
        String substation = ServletRequestUtils.getRequiredStringParameter(request, "substation");
        String feeder = ServletRequestUtils.getRequiredStringParameter(request, "feeder");
        String pole = ServletRequestUtils.getRequiredStringParameter(request, "pole");
        String transformerSize = ServletRequestUtils.getRequiredStringParameter(request, "transformerSize");
        String serviceVoltage = ServletRequestUtils.getRequiredStringParameter(request, "serviceVoltage");
        
        String username = ServletRequestUtils.getRequiredStringParameter(request, "username");
        String password = ServletRequestUtils.getRequiredStringParameter(request, "password");
        String loginGroup = ServletRequestUtils.getRequiredStringParameter(request, "loginGroup");
        
        UpdatableAccount updatableAccount = new UpdatableAccount();
        updatableAccount.setAccountDto(new AccountDto());
        updatableAccount.setAccountNumber(accountNumber);
        updatableAccount.getAccountDto().setFirstName(firstName);
        updatableAccount.getAccountDto().setLastName(lastName);
        updatableAccount.getAccountDto().setEmailAddress(emailAddress);
        updatableAccount.getAccountDto().setHomePhone(homePhone);
        updatableAccount.getAccountDto().setWorkPhone(workPhone);
        updatableAccount.getAccountDto().setAltTrackingNumber(altTrackingNum);
        updatableAccount.getAccountDto().setMapNumber(mapNum);
        updatableAccount.getAccountDto().setIsCommercial(new Boolean(isCommercial));
        updatableAccount.getAccountDto().setCompanyName(companyName);
        Address streetAddress = new Address();
        streetAddress.setLocationAddress1(streetAddress1);
        streetAddress.setLocationAddress2(streetAddress2);
        streetAddress.setCityName(city);
        streetAddress.setStateCode(state);
        streetAddress.setZipCode(zip);
        streetAddress.setCounty(county);
        updatableAccount.getAccountDto().setStreetAddress(streetAddress);
        Address billingAddress = new Address();
        billingAddress.setLocationAddress1(billingAddress1);
        billingAddress.setLocationAddress2(billingAddress2);
        billingAddress.setCityName(billingCity);
        billingAddress.setStateCode(billingState);
        billingAddress.setZipCode(billingZip);
        billingAddress.setCounty(billingCounty);
        updatableAccount.getAccountDto().setBillingAddress(billingAddress);
        SiteInformation siteInfo = new SiteInformation();
        siteInfo.setSubstationName(substation);
        siteInfo.setFeeder(feeder);
        siteInfo.setPole(pole);
        siteInfo.setTransformerSize(transformerSize);
        siteInfo.setServiceVoltage(serviceVoltage);
        updatableAccount.getAccountDto().setSiteInfo(siteInfo);
        updatableAccount.getAccountDto().setUserName(username);
        updatableAccount.getAccountDto().setPassword(password);
        updatableAccount.getAccountDto().setLoginGroup(loginGroup);
        
        LiteYukonUser yukon = yukonUserDao.getLiteYukonUser("yukon");
        try {
            accountService.addAccount(updatableAccount, yukon);
            results.add(accountNumber + " added successfully.");
        } catch (RuntimeException e) {
            errorReasons.add(e.getMessage());
            CTILogger.error(e.getMessage(), e);
        }
        
        return returnMav(request, results, errorReasons);
    }
    
    public ModelAndView deleteAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        List<String> results = new ArrayList<String>();
        List<String> errorReasons = new ArrayList<String>();
        
        String accountNumber = ServletRequestUtils.getRequiredStringParameter(request, "accountNumber");
        String user = ServletRequestUtils.getRequiredStringParameter(request, "user");
        try {
            LiteYukonUser yukonuser = yukonUserDao.getLiteYukonUser(user);
            accountService.deleteAccount(accountNumber, yukonuser);
            results.add(accountNumber + " deleted successfully.");
        } catch (RuntimeException e) {
            errorReasons.add(e.getMessage());
        }
        
        return returnMav(request, results, errorReasons);
    }
    
    public ModelAndView updateAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        return null;
    }
    
    // HELPERS
    private ModelAndView returnMav(HttpServletRequest request, List<String> results, List<String> errorReasons) {
        
        ModelAndView mav = new ModelAndView("accountService/inputs/home.jsp");
     
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
    
    @Autowired
    public void setYukonUser(YukonUserDao authDao) {
        this.yukonUserDao = authDao;
    }

}
