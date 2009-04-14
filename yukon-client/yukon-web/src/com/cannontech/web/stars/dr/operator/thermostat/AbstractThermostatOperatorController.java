package com.cannontech.web.stars.dr.operator.thermostat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.util.ServletUtil;

/**
 * Base controller for Operator-side Thermostat operations
 */
public abstract class AbstractThermostatOperatorController {

	private CustomerAccountDao customerAccountDao;
	private StarsInventoryBaseDao starsInventoryBaseDao;
	
	@ModelAttribute("thermostatIds")
    public List<Integer> getThermostatIds(HttpServletRequest request)
            throws ServletRequestBindingException {

        String thermostatIds = ServletRequestUtils.getStringParameter(request,
                                                                      "thermostatIds");

        if(!StringUtils.isBlank(thermostatIds)) {
	        List<Integer> idList = new ArrayList<Integer>();
	
	        // If thermostatIds exists, remove brackets, split and create Integer list
	        thermostatIds = thermostatIds.replaceAll("[\\[|\\]]", "");
	        if (!StringUtils.isBlank(thermostatIds)) {
				List<Integer> tempIdList = ServletUtil.getIntegerListFromString(thermostatIds);
	        	idList.addAll(tempIdList);
	        }
	
	        return idList;
        }
        
        return new ArrayList<Integer>();
    }
    
    @ModelAttribute("customerAccount")
    public CustomerAccount getCustomerAccount(HttpServletRequest request) {
    	
    	// Get the account info from the session
    	HttpSession session = request.getSession();
    	StarsCustAccountInformation accountInfo = 
            (StarsCustAccountInformation) session.getAttribute(ServletUtils.TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO);
    	
    	if(accountInfo != null) {
	        int accountId = accountInfo.getStarsCustomerAccount().getAccountID();
			CustomerAccount account = customerAccountDao.getById(accountId);
	        return account;
    	}
    	
    	return null;
    }
    
    /**
     * Helper method to get the legacy 'inventory number' for use with leagcy stars operator
     * urls
     * @param request - Current request
     * @param thermostatId - Id to get number for
     * @return Inventory number
     */
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
    
    /**
     * Helper method to make sure the inventory being used belongs to the current account 
     */
    public void checkInventoryAgainstAccount(List<Integer> inventoryIdList, CustomerAccount customerAccount){
    	
    	for(Integer inventoryId : inventoryIdList) {
    	
	    	LiteInventoryBase inventory = starsInventoryBaseDao.getByInventoryId(inventoryId);
	    	int accountId = customerAccount.getAccountId();
			if(inventory.getAccountID() != accountId) {
	    		throw new NotAuthorizedException("The Inventory with id: " + inventoryId + 
	    				" does not belong to the current customer account with id: " + accountId);
	    	}
    	}
    }
    
    public void addThermostatModelAttribute(
    		HttpServletRequest request, ModelMap map, List<Integer> thermostatIds) {
    	
    	if(thermostatIds.size() > 1) {
    		map.addAttribute("AllTherm", "true");
    	} else {
    		Integer thermostatId = thermostatIds.get(0);
    		int inventoryNumber = this.getInventoryNumber(request, thermostatId);
    		map.addAttribute("InvNo", inventoryNumber);
    	}
    }

    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
    
    @Autowired
    public void setStarsInventoryBaseDao(
			StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}
}
