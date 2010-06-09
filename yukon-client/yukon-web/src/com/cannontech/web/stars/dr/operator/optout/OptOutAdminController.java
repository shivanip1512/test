package com.cannontech.web.stars.dr.operator.optout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutCountsDto;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Maps;

/**
 * Controller for Manual thermostat operations
 */
@CheckRole(YukonRole.CONSUMER_INFO)
@Controller
public class OptOutAdminController {

	private OptOutEventDao optOutEventDao;
	private CustomerAccountDao customerAccountDao;
	private OptOutStatusService optOutStatusService;
	private StarsDatabaseCache starsDatabaseCache;
	private OptOutService optOutService;
	private StarsInventoryBaseDao starsInventoryBaseDao;
	private ProgramDao programDao;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	private RolePropertyDao rolePropertyDao;
	
    @RequestMapping(value = "/operator/optOut/admin", method = RequestMethod.GET)
    public String view(YukonUserContext userContext, ModelMap map, Boolean emptyProgramName, Boolean programNotFound) throws Exception {
        
    	MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
    	
    	rolePropertyDao.verifyAnyProperties(userContext.getYukonUser(), 
        		YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_STATUS,
        		YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE,
        		YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS,
        		YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT);
    	
    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
    	map.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyID());

    	int totalNumberOfAccounts = customerAccountDao.getTotalNumberOfAccounts(energyCompany);
    	map.addAttribute("totalNumberOfAccounts", totalNumberOfAccounts);

    	Integer currentOptOuts = optOutEventDao.getTotalNumberOfActiveOptOuts(energyCompany);
    	map.addAttribute("currentOptOuts", currentOptOuts);
    	
    	Integer scheduledOptOuts = optOutEventDao.getTotalNumberOfScheduledOptOuts(energyCompany);
    	map.addAttribute("scheduledOptOuts", scheduledOptOuts);
    	
    	// programNameCountsMap
    	OptOutCountsDto defaultOptOutCountsSetting = optOutStatusService.getDefaultOptOutCounts(userContext.getYukonUser());
		List<OptOutCountsDto> programSpecificOptOutCounts = optOutStatusService.getProgramSpecificOptOutCounts(userContext.getYukonUser());
		
		Map<String, OptOutCounts> programNameCountsMap = Maps.newLinkedHashMap();
		for (OptOutCountsDto setting : programSpecificOptOutCounts) {
			
			int programId = setting.getProgramId();
			Program program = programDao.getByProgramId(programId);
			programNameCountsMap.put(program.getProgramName(), setting.getOptOutCounts());
		}
		if (programSpecificOptOutCounts.size() == 0) {
			programNameCountsMap.put(messageSourceAccessor.getMessage("yukon.web.modules.dr.optOut.allPrograms"), defaultOptOutCountsSetting.getOptOutCounts());
		} else {
			programNameCountsMap.put(messageSourceAccessor.getMessage("yukon.web.modules.dr.optOut.otherPrograms"), defaultOptOutCountsSetting.getOptOutCounts());
		}
    	map.addAttribute("programNameCountsMap", programNameCountsMap);
    	
    	// optOutsEnabled
    	boolean optOutsEnabled = optOutStatusService.getOptOutEnabled(userContext.getYukonUser());
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
		
		map.addAttribute("emptyProgramName", emptyProgramName);
		map.addAttribute("programNotFound", programNotFound);
		
		return "operator/optout/optOutAdmin.jsp";
    }
    
    @RequestMapping(value = "/operator/optOut/admin/enable", method = RequestMethod.POST)
    public String enableOptOutsToday(LiteYukonUser user, ModelMap map, FlashScope flashScope) throws Exception {

    	rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE, user);
    	
    	optOutService.changeOptOutEnabledStateForToday(user, true);
    	flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.confirm.enableConsumerOptOuts"));
    	
    	return "redirect:/spring/stars/operator/optOut/admin";
    }
    
    @RequestMapping(value = "/operator/optOut/admin/disable", method = RequestMethod.POST)
    public String disableOptOutsToday(LiteYukonUser user, ModelMap map, FlashScope flashScope) throws Exception {
    	
    	rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE, user);
    	
    	optOutService.changeOptOutEnabledStateForToday(user, false);
    	flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.confirm.disableConsumerOptOuts"));
    	
    	return "redirect:/spring/stars/operator/optOut/admin";
    }
    
    @RequestMapping(value = "/operator/optOut/admin/cancelAllOptOuts", method = RequestMethod.POST)
    public String cancelActiveOptOuts(LiteYukonUser user, ModelMap map, String programName, FlashScope flashScope) throws Exception {
    	
    	rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT, user);
    	
		if (StringUtils.isNotBlank(programName)) {
				
			try {
				
				optOutService.cancelAllOptOutsByProgramName(programName, user);
				flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.byProgramName.confirm.successfullyCanceledCurrentOptOuts", programName));
				
			} catch (ProgramNotFoundException e) {
				flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.byProgramName.error.programNotFound", programName));
			}

		} else {
			optOutService.cancelAllOptOuts(user);
			flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.byProgramName.confirm.successfullyCanceledCurrentOptOuts.allPrograms"));
		}

    	return "redirect:/spring/stars/operator/optOut/admin";
    }
    
    @RequestMapping(value = "/operator/optOut/admin/setCounts", method = RequestMethod.POST)
    public String setCounts(LiteYukonUser user, ModelMap map, String count, String dontCount, String programName, FlashScope flashScope) throws Exception {
    	
    	rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS, user);
    	
    	boolean countBool = true;
    	if (dontCount != null) {
    		countBool = false;
    	}
    	
    	if (StringUtils.isNotBlank(programName)) {

			try {
				
            	optOutService.changeOptOutCountStateForTodayByProgramName(user, countBool, programName);
            	flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.byProgramName.confirm.countingTodaysOptOuts", programName));
            	if (!countBool) {
            		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.byProgramName.confirm.notCountingTodaysOptOuts", programName));
            	}
            	
			} catch (ProgramNotFoundException e) {
				flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.byProgramName.error.programNotFound", programName));
			}

		} else {
			optOutService.changeOptOutCountStateForToday(user, countBool);
			flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.byProgramName.confirm.countingTodaysOptOuts.allPrograms"));
        	if (!countBool) {
        		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.byProgramName.confirm.notCountingTodaysOptOuts.allPrograms"));
        	}
		}
    	
    	return "redirect:/spring/stars/operator/optOut/admin";
    }
    
    @RequestMapping(value = "/operator/optOut/admin/viewScheduled", method = RequestMethod.GET)
    public String viewScheduledOptOuts(LiteYukonUser user, ModelMap map) throws Exception {
    	
    	rolePropertyDao.verifyProperty(YukonRoleProperty.ADMIN_VIEW_OPT_OUT_EVENTS, user);
    	
    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
    	List<OptOutEvent> scheduledEvents = 
    		optOutEventDao.getAllScheduledOptOutEvents(energyCompany);
    	
    	List<ScheduledOptOutEventDto> events = new ArrayList<ScheduledOptOutEventDto>();
    	for(OptOutEvent event : scheduledEvents) {
    		
    		ScheduledOptOutEventDto eventDto = new ScheduledOptOutEventDto();
    		eventDto.setStartDate(event.getStartDate());
    		eventDto.setStopDate(event.getStopDate());
    		
    		Integer accountId = event.getCustomerAccountId();
    		CustomerAccount customerAccount = customerAccountDao.getById(accountId);
    		eventDto.setAccountNumber(customerAccount.getAccountNumber());
    		
    		Integer inventoryId = event.getInventoryId();
    		LiteStarsLMHardware inventory = 
    			(LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(inventoryId);
    		eventDto.setSerialNumber(inventory.getManufacturerSerialNumber());
    		
    		events.add(eventDto);
    	}
    	
    	map.addAttribute("scheduledEvents", events);
    	
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
    	
    	return "operator/optout/scheduledEvents.jsp";
    }
    
    /**
     * Helper class to hold Scheduled opt out information for jsp
     */
    public static class ScheduledOptOutEventDto {
    	
    	private ReadableInstant startDate;
    	private ReadableInstant stopDate;
    	private String accountNumber;
    	private String serialNumber;
		
    	public ReadableInstant getStartDate() {
			return startDate;
		}
		public void setStartDate(ReadableInstant startDate) {
			this.startDate = startDate;
		}
		public ReadableInstant getStopDate() {
			return stopDate;
		}
		public void setStopDate(ReadableInstant stopDate) {
			this.stopDate = stopDate;
		}
		public String getAccountNumber() {
			return accountNumber;
		}
		public void setAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
		}
		public String getSerialNumber() {
			return serialNumber;
		}
		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}
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
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
    	this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
		this.messageSourceResolver = messageSourceResolver;
	}
    
    @Autowired
    public void setProgramDao(ProgramDao programDao) {
		this.programDao = programDao;
	}
}
