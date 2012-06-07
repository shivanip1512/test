package com.cannontech.web.stars.dr.hardware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountSite;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.service.DeviceActivationService;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.util.ServletUtil;

public class DeviceActivationController extends MultiActionController {
    private DeviceActivationService deviceActivationService;
    private StarsDatabaseCache starsDatabaseCache;
    private CustomerAccountDao customerAccountDao;
    private ContactDao contactDao;
    private AddressDao addressDao;
    private CustomerDao customerDao;
    private AccountSiteDao accountSiteDao;
    
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        mav.setViewName("hardware/deviceactivation/Activation.jsp");
        return mav;
    }
    
    public ModelAndView confirmation(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	final LiteYukonUser user = ServletUtil.getYukonUser(request);
        final String accountNumber = ServletRequestUtils.getRequiredStringParameter(request, "accountnumber");
        final String serialNumber = ServletRequestUtils.getRequiredStringParameter(request, "serialnumber");

        if (accountNumber.equals("") || serialNumber.equals("")) {
            String message = "Both fields required.";
            ModelAndView mav = createStatusView(message);
            return mav;
        }
        
        final boolean result = this.deviceActivationService.isValidActivation(accountNumber, 
        																      serialNumber,
        																      user);
        if (!result) {
            String message = "FAILED";
            ModelAndView mav = createStatusView(message);
            return mav;
        }
        
        CustomerAccount account = customerAccountDao.getByAccountNumber(accountNumber, user);
        AccountSite accountSite = accountSiteDao.getByAccountSiteId(account.getAccountSiteId());
        LiteAddress address = addressDao.getByAddressId(accountSite.getStreetAddressId());
        LiteCustomer customer = customerDao.getLiteCustomer(account.getCustomerId());
        LiteContact contact = contactDao.getContact(customer.getPrimaryContactID());
        
        boolean emptyContactName = isEmptyValue(contact.getContFirstName()) && isEmptyValue(contact.getContLastName()); 
        boolean emptyAddress = isEmptyValue(address.getLocationAddress1());

        ModelAndView mav = new ModelAndView();
        mav.addObject("contact", (!emptyContactName) ? contact : null);
        mav.addObject("address", (!emptyAddress) ? address : null);
        mav.addObject("accountNumber", accountNumber);
        mav.addObject("serialNumber", serialNumber);
        mav.setViewName("hardware/deviceactivation/Confirmation.jsp");
        return mav;
    }
    
    public ModelAndView activate(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	final LiteYukonUser liteYukonUser = ServletUtil.getYukonUser(request);
        final String accountNumber = ServletRequestUtils.getRequiredStringParameter(request, "accountnumber");
        final String serialNumber = ServletRequestUtils.getRequiredStringParameter(request, "serialnumber");
        final StarsYukonUser user = ServletUtils.getStarsYukonUser(request);
        final LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(user.getEnergyCompanyID());
        
        final boolean result = this.deviceActivationService.activate(energyCompany, 
        															 accountNumber,
        															 serialNumber,
        															 liteYukonUser);
        if (!result) {
            String message = "FAILED";
            ModelAndView mav = createStatusView(message);
            return mav;
        }
        
        String message = "SUCCESS";
        ModelAndView mav =  createStatusView(message);
        return mav;
    }
    
    private ModelAndView createStatusView(final String message) {
        final ModelAndView mav = new ModelAndView();
        mav.addObject("message", message);
        mav.setViewName("hardware/deviceactivation/Status.jsp");
        return mav;
    }
    
    private boolean isEmptyValue(String value) {
        if (value == null || value.equalsIgnoreCase(CtiUtilities.STRING_NONE)) return true;
        return false;
    }
    
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    public void setDeviceActivationService(DeviceActivationService deviceActivationService) {
        this.deviceActivationService = deviceActivationService;
    }
    
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }

    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }
    
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
    
    public void setAccountSiteDao(AccountSiteDao accountSiteDao) {
        this.accountSiteDao = accountSiteDao;
    }

}
