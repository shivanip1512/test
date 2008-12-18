package com.cannontech.web.stars.dr.operator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.web.security.annotation.CheckRole;

/**
 * Controller for Manual thermostat operations
 */
@CheckRole(ConsumerInfoRole.ROLEID)
@Controller
public class OptOutAdminController {

	private OptOutEventDao optOutEventDao;
	private CustomerAccountDao customerAccountDao;
	private OptOutStatusService optOutStatusService;
	private StarsDatabaseCache starsDatabaseCache;
	private OptOutService optOutService;
	
	private AuthDao authDao;
	
    @RequestMapping(value = "/operator/optOut/admin", method = RequestMethod.GET)
    public String view(LiteYukonUser user, ModelMap map) throws Exception {
        
    	authDao.verifyTrueProperty(user, 
    			ConsumerInfoRole.OPT_OUT_ADMIN_STATUS,
    			ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_ENABLE,
    			ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_COUNTS,
    			ConsumerInfoRole.OPT_OUT_ADMIN_CANCEL_CURRENT);
    	
    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);

    	int totalNumberOfAccounts = customerAccountDao.getTotalNumberOfAccounts(energyCompany);
    	map.addAttribute("totalNumberOfAccounts", totalNumberOfAccounts);

    	Integer currentOptOuts = optOutEventDao.getTotalNumberOfActiveOptOuts(energyCompany);
    	map.addAttribute("currentOptOuts", currentOptOuts);
    	
    	Integer scheduledOptOuts = optOutEventDao.getTotalNumberOfScheduledOptOuts(energyCompany);
    	map.addAttribute("scheduledOptOuts", scheduledOptOuts);
    	
    	boolean optOutCounts = optOutStatusService.getOptOutCounts(user);
    	map.addAttribute("optOutCounts", optOutCounts);
    	
    	boolean optOutsEnabled = optOutStatusService.getOptOutEnabled(user);
    	map.addAttribute("optOutsEnabled", optOutsEnabled);
    	
    	// Get the customer search by list for search drop down box
    	YukonSelectionList yukonSelectionList = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE);
    	List<YukonListEntry> customerSearchList = new ArrayList<YukonListEntry>();
		List<YukonListEntry> yukonListEntries = yukonSelectionList.getYukonListEntries();
		for (YukonListEntry entry : yukonListEntries) {
			if (entry.getYukonDefID() != YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_METER_NO) {
				customerSearchList.add(entry);
			}
		}
		map.addAttribute("customerSearchList", customerSearchList);
		
		return "operator/optout/optOutAdmin.jsp";
    }
    
    @RequestMapping(value = "/operator/optOut/admin/enable", method = RequestMethod.POST)
    public String enableOptOutsToday(LiteYukonUser user, ModelMap map) throws Exception {

    	authDao.verifyTrueProperty(user, ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_ENABLE);
    	
    	optOutService.changeOptOutEnabledStateForToday(user, true);
    	
    	return "redirect:/spring/stars/operator/optOut/admin";
    }
    
    @RequestMapping(value = "/operator/optOut/admin/disable", method = RequestMethod.POST)
    public String disableOptOutsToday(LiteYukonUser user, ModelMap map) throws Exception {
    	
    	authDao.verifyTrueProperty(user, ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_ENABLE);
    	
    	optOutService.changeOptOutEnabledStateForToday(user, false);
    	
    	return "redirect:/spring/stars/operator/optOut/admin";
    }
    
    @RequestMapping(value = "/operator/optOut/admin/cancelAllOptOuts", method = RequestMethod.POST)
    public String cancelActiveOptOuts(LiteYukonUser user, ModelMap map) throws Exception {
    	
    	authDao.verifyTrueProperty(user, ConsumerInfoRole.OPT_OUT_ADMIN_CANCEL_CURRENT);
    	
    	optOutService.cancelAllOptOuts(user);

    	return "redirect:/spring/stars/operator/optOut/admin";
    }
    
    @RequestMapping(value = "/operator/optOut/admin/count", method = RequestMethod.POST)
    public String countOptOuts(LiteYukonUser user, ModelMap map) throws Exception {
    	
    	authDao.verifyTrueProperty(user, ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_COUNTS);
    	
    	optOutService.changeOptOutCountStateForToday(user, true);
    	
    	return "redirect:/spring/stars/operator/optOut/admin";
    }
    
    @RequestMapping(value = "/operator/optOut/admin/dontCount", method = RequestMethod.POST)
    public String dontCountOptOuts(LiteYukonUser user, ModelMap map) throws Exception {
    	
    	authDao.verifyTrueProperty(user, ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_COUNTS);
    	
    	optOutService.changeOptOutCountStateForToday(user, false);
    	
    	return "redirect:/spring/stars/operator/optOut/admin";
    }
    
    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
		this.optOutEventDao = optOutEventDao;
	}
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
    
    @Autowired
    public void setOptOutStatusService(OptOutStatusService optOutStatusService) {
		this.optOutStatusService = optOutStatusService;
	}
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
		this.starsDatabaseCache = starsDatabaseCache;
	}
    
    @Autowired
    public void setOptOutServiceHelper(OptOutService optOutServiceHelper) {
		this.optOutService = optOutServiceHelper;
	}
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
		this.authDao = authDao;
	}
    
}
