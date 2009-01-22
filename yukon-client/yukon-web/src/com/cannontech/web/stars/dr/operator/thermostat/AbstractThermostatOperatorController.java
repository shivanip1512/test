package com.cannontech.web.stars.dr.operator.thermostat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;

/**
 * Base controller for Operator-side Thermostat operations
 */
public abstract class AbstractThermostatOperatorController {

	private CustomerAccountDao customerAccountDao;
	
	@ModelAttribute("thermostatIds")
    public List<Integer> getThermostatIds(HttpServletRequest request)
            throws ServletRequestBindingException {

        String thermostatIds = ServletRequestUtils.getStringParameter(request,
                                                                      "thermostatIds");

        // Override the toString method to get a comma separated list with no
        // leading or trailing brackets
        List<Integer> idList = new ArrayList<Integer>() {
            @Override
            public String toString() {
                return super.toString().replaceAll("[\\[|\\]]", "");
            }

        };

        // If thermostatIds exists, split and create Integer list
        if (!StringUtils.isBlank(thermostatIds)) {
            String[] ids = thermostatIds.split(",");
            for (String id : ids) {
                try {
                    int idInt = Integer.parseInt(id.trim());
                    idList.add(idInt);
                } catch(NumberFormatException nfe) {
                    CTILogger.error(nfe);
                }
            }
        }

        return idList;
    }
    
    @ModelAttribute("customerAccount")
    public CustomerAccount getCustomerAccount(HttpServletRequest request) {
    	
    	// Get the account info from the session
    	HttpSession session = request.getSession();
    	StarsCustAccountInformation accountInfo = 
            (StarsCustAccountInformation) session.getAttribute(ServletUtils.TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO);
    	
        int accountId = accountInfo.getStarsCustomerAccount().getAccountID();
		CustomerAccount account = customerAccountDao.getById(accountId);
        return account;
    }
    
    protected int getInventoryNumber(HttpServletRequest request, int thermostatId) {
    	
    	StarsCustAccountInformation accountInfo = 
            (StarsCustAccountInformation) request.getSession().getAttribute(
            		ServletUtils.TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO);
        StarsInventories inventories = accountInfo.getStarsInventories();
        StarsInventory[] starsInventory = inventories.getStarsInventory();
        int count = 0;
        for(StarsInventory inventory : starsInventory) {
        	if(thermostatId == inventory.getInventoryID()) {
        		break;
        	}
        	count++;
        }
        
        return count;
    }


    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
}
