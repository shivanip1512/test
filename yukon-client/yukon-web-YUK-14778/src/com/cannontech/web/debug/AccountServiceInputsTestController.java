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
import com.cannontech.common.model.Address;
import com.cannontech.common.model.SiteInformation;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.account.service.AccountServiceHelper;
import com.cannontech.util.ServletUtil;

public class AccountServiceInputsTestController extends MultiActionController{
    
    private AccountService accountService;
    private AccountServiceHelper accountServiceHelper;
    private YukonUserDao yukonUserDao;
    
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        return returnMav(request, new ArrayList<String>(0), new ArrayList<String>(0));
    }
    
    public ModelAndView addAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        List<String> results = new ArrayList<String>();
        List<String> errorReasons = new ArrayList<String>();
        
        String user = ServletRequestUtils.getRequiredStringParameter(request, "add_user");
        String accountNumber = ServletRequestUtils.getRequiredStringParameter(request, "add_accountNumber");
        String lastName = ServletRequestUtils.getRequiredStringParameter(request, "add_lastName");
        String firstName = ServletRequestUtils.getRequiredStringParameter(request, "add_firstName");
        String homePhone = ServletRequestUtils.getRequiredStringParameter(request, "add_homePhone");
        String workPhone = ServletRequestUtils.getRequiredStringParameter(request, "add_workPhone");
        String emailAddress = ServletRequestUtils.getRequiredStringParameter(request, "add_emailAddress");
        String altTrackingNum = ServletRequestUtils.getRequiredStringParameter(request, "add_altTrackingNum");
        String mapNum = ServletRequestUtils.getRequiredStringParameter(request, "add_mapNum");
        String isCommercial = ServletRequestUtils.getRequiredStringParameter(request, "add_isCommercial");
        String companyName = ServletRequestUtils.getRequiredStringParameter(request, "add_companyName");
        String streetAddress1 = ServletRequestUtils.getRequiredStringParameter(request, "add_streetAddress1");
        String streetAddress2 = ServletRequestUtils.getRequiredStringParameter(request, "add_streetAddress2");
        String city = ServletRequestUtils.getRequiredStringParameter(request, "add_city");
        String state = ServletRequestUtils.getRequiredStringParameter(request, "add_state");
        String zip = ServletRequestUtils.getRequiredStringParameter(request, "add_zip");
        String county = ServletRequestUtils.getRequiredStringParameter(request, "add_county");
        
        String billingAddress1 = ServletRequestUtils.getRequiredStringParameter(request, "add_billingAddress1");
        String billingAddress2 = ServletRequestUtils.getRequiredStringParameter(request, "add_billingAddress2");
        String billingCity = ServletRequestUtils.getRequiredStringParameter(request, "add_billingCity");
        String billingState = ServletRequestUtils.getRequiredStringParameter(request, "add_billingState");
        String billingZip = ServletRequestUtils.getRequiredStringParameter(request, "add_billingZip");
        String billingCounty = ServletRequestUtils.getRequiredStringParameter(request, "add_billingCounty");
        
        String substation = ServletRequestUtils.getRequiredStringParameter(request, "add_substation");
        String feeder = ServletRequestUtils.getRequiredStringParameter(request, "add_feeder");
        String pole = ServletRequestUtils.getRequiredStringParameter(request, "add_pole");
        String transformerSize = ServletRequestUtils.getRequiredStringParameter(request, "add_transformerSize");
        String serviceVoltage = ServletRequestUtils.getRequiredStringParameter(request, "add_serviceVoltage");
        
        String username = ServletRequestUtils.getRequiredStringParameter(request, "add_username");
        String password = ServletRequestUtils.getRequiredStringParameter(request, "add_password");
        String userGroup = ServletRequestUtils.getRequiredStringParameter(request, "add_loginGroup");
        
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
        updatableAccount.getAccountDto().setUserGroup(userGroup);
        
        try {
            LiteYukonUser yukonuser = yukonUserDao.findUserByUsername(user);
            accountService.addAccount(updatableAccount, yukonuser);
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
        
        String accountNumber = ServletRequestUtils.getRequiredStringParameter(request, "delete_accountNumber");
        String user = ServletRequestUtils.getRequiredStringParameter(request, "delete_user");
        try {
            LiteYukonUser yukonuser = yukonUserDao.findUserByUsername(user);
            accountService.deleteAccount(accountNumber, yukonuser);
            results.add(accountNumber + " deleted successfully.");
        } catch (RuntimeException e) {
            errorReasons.add(e.getMessage());
            CTILogger.error(e.getMessage(), e);
        }
        
        return returnMav(request, results, errorReasons);
    }
    
    public ModelAndView updateAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        List<String> results = new ArrayList<String>();
        List<String> errorReasons = new ArrayList<String>();
        
        String user = ServletRequestUtils.getRequiredStringParameter(request, "update_user");
        String accountNumber = ServletRequestUtils.getRequiredStringParameter(request, "update_accountNumber");
        String lastName = ServletRequestUtils.getRequiredStringParameter(request, "update_lastName");
        String firstName = ServletRequestUtils.getRequiredStringParameter(request, "update_firstName");
        String homePhone = ServletRequestUtils.getRequiredStringParameter(request, "update_homePhone");
        String workPhone = ServletRequestUtils.getRequiredStringParameter(request, "update_workPhone");
        String emailAddress = ServletRequestUtils.getRequiredStringParameter(request, "update_emailAddress");
        String altTrackingNum = ServletRequestUtils.getRequiredStringParameter(request, "update_altTrackingNum");
        String mapNum = ServletRequestUtils.getRequiredStringParameter(request, "update_mapNum");
        String isCommercial = ServletRequestUtils.getRequiredStringParameter(request, "update_isCommercial");
        String companyName = ServletRequestUtils.getRequiredStringParameter(request, "update_companyName");
        String streetAddress1 = ServletRequestUtils.getRequiredStringParameter(request, "update_streetAddress1");
        String streetAddress2 = ServletRequestUtils.getRequiredStringParameter(request, "update_streetAddress2");
        String city = ServletRequestUtils.getRequiredStringParameter(request, "update_city");
        String state = ServletRequestUtils.getRequiredStringParameter(request, "update_state");
        String zip = ServletRequestUtils.getRequiredStringParameter(request, "update_zip");
        String county = ServletRequestUtils.getRequiredStringParameter(request, "update_county");
        
        String billingAddress1 = ServletRequestUtils.getRequiredStringParameter(request, "update_billingAddress1");
        String billingAddress2 = ServletRequestUtils.getRequiredStringParameter(request, "update_billingAddress2");
        String billingCity = ServletRequestUtils.getRequiredStringParameter(request, "update_billingCity");
        String billingState = ServletRequestUtils.getRequiredStringParameter(request, "update_billingState");
        String billingZip = ServletRequestUtils.getRequiredStringParameter(request, "update_billingZip");
        String billingCounty = ServletRequestUtils.getRequiredStringParameter(request, "update_billingCounty");
        
        String substation = ServletRequestUtils.getRequiredStringParameter(request, "update_substation");
        String feeder = ServletRequestUtils.getRequiredStringParameter(request, "update_feeder");
        String pole = ServletRequestUtils.getRequiredStringParameter(request, "update_pole");
        String transformerSize = ServletRequestUtils.getRequiredStringParameter(request, "update_transformerSize");
        String serviceVoltage = ServletRequestUtils.getRequiredStringParameter(request, "update_serviceVoltage");
        
        String username = ServletRequestUtils.getRequiredStringParameter(request, "update_username");
        String password = ServletRequestUtils.getRequiredStringParameter(request, "update_password");
        String userGroup = ServletRequestUtils.getRequiredStringParameter(request, "update_loginGroup");
        
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
        updatableAccount.getAccountDto().setUserGroup(userGroup);
        
        try {
            LiteYukonUser yukonuser = yukonUserDao.findUserByUsername(user);
            UpdatableAccount account = accountServiceHelper.buildFullDto(updatableAccount, yukonuser);
            accountService.updateAccount(account, yukonuser);
            results.add(accountNumber + " updated successfully.");
        } catch (RuntimeException e) {
            errorReasons.add(e.getMessage());
            CTILogger.error(e.getMessage(), e);
        }
        
        return returnMav(request, results, errorReasons);
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
    public void setAccountServiceHelper(AccountServiceHelper accountServiceHelper) {
        this.accountServiceHelper = accountServiceHelper;
    }
    
    @Autowired
    public void setYukonUser(YukonUserDao authDao) {
        this.yukonUserDao = authDao;
    }

}
