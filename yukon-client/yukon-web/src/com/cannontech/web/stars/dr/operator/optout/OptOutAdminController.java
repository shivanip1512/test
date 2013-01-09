package com.cannontech.web.stars.dr.operator.optout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.ProgramToAlternateProgramDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutCountsTemporaryOverride;
import com.cannontech.stars.dr.optout.model.OptOutEnabled;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.util.JsonView;
import com.google.common.collect.Maps;

/**
 * Controller for Manual thermostat operations
 */
@CheckRole(YukonRole.CONSUMER_INFO)
@Controller
public class OptOutAdminController {

    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private OptOutService optOutService;
    @Autowired private OptOutStatusService optOutStatusService;
    @Autowired private ProgramDao programDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ProgramToAlternateProgramDao programToAlternameProgramDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private StarsEventLogService starsEventLogService;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
	
    @RequestMapping(value = "/operator/optOut/admin", method = RequestMethod.GET)
    public String view(YukonUserContext userContext, ModelMap model, Boolean emptyProgramName, Boolean programNotFound) throws Exception {
        
    	rolePropertyDao.verifyAnyProperties(userContext.getYukonUser(), 
        		YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_STATUS,
        		YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE,
        		YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS,
        		YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT);
    	
    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
    	model.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyId());

    	systemOptOuts(null, userContext, model);

    	// programNameEnabledMap
    	OptOutEnabled defaultOptOutEnabledSetting = optOutStatusService.getDefaultOptOutEnabled(userContext.getYukonUser());
    	Map<Integer, OptOutEnabled> programSpecificEnabledOptOuts = 
    	    optOutStatusService.getProgramSpecificEnabledOptOuts(energyCompany.getEnergyCompanyId()); 

        Map<String, OptOutEnabled> programNameEnabledMap = Maps.newLinkedHashMap();
        for (Entry<Integer, OptOutEnabled> programOptOutEnabledEntry : programSpecificEnabledOptOuts.entrySet()) {
            
            int programId = programOptOutEnabledEntry.getKey();
            Program program = programDao.getByProgramId(programId);
            programNameEnabledMap.put(program.getProgramName(), programOptOutEnabledEntry.getValue());
        }
        model.addAttribute("programNameEnabledMap", programNameEnabledMap);
        model.addAttribute("energyCompanyOptOutEnabledSetting", defaultOptOutEnabledSetting);

        // programNameCountsMap
        OptOutCountsTemporaryOverride defaultOptOutCountsSetting = optOutStatusService.getDefaultOptOutCounts(userContext.getYukonUser());
		List<OptOutCountsTemporaryOverride> programSpecificOptOutCounts = optOutStatusService.getProgramSpecificOptOutCounts(userContext.getYukonUser());
		
		Map<String, OptOutCounts> programNameCountsMap = Maps.newLinkedHashMap();
		for (OptOutCountsTemporaryOverride setting : programSpecificOptOutCounts) {
			
			int programId = setting.getAssignedProgramId();
			Program program = programDao.getByProgramId(programId);
			programNameCountsMap.put(program.getProgramName(), setting.getOptOutCounts());
		}
    	model.addAttribute("programNameCountsMap", programNameCountsMap);
        model.addAttribute("energyCompanyOptOutCountsSetting", defaultOptOutCountsSetting.getOptOutCounts());
    	
    	// Get the customer search by list for search drop down box
    	YukonSelectionList yukonSelectionList = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE);
    	List<YukonListEntry> customerSearchList = new ArrayList<YukonListEntry>();
		List<YukonListEntry> yukonListEntries = yukonSelectionList.getYukonListEntries();
		for (YukonListEntry entry : yukonListEntries) {
			if (entry.getYukonDefID() != YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_METER_NO) {
				customerSearchList.add(entry);
			}
		}
		model.addAttribute("customerSearchList", customerSearchList);
		
		model.addAttribute("emptyProgramName", emptyProgramName);
		model.addAttribute("programNotFound", programNotFound);
		
		return "operator/optout/optOutAdmin.jsp";
    }

    @RequestMapping(value = "/operator/optOut/systemOptOuts", method = RequestMethod.POST)
    public View systemOptOuts(Integer assignedProgramId, YukonUserContext userContext, ModelMap model) throws Exception {
        List<Integer> assignedProgramIds = assignedProgramId != null ? Collections.singletonList(assignedProgramId): null;

        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        model.addAttribute("totalNumberOfAccounts", customerAccountDao.getTotalNumberOfAccounts(yukonEnergyCompany, assignedProgramIds));
        model.addAttribute("currentOptOuts", optOutEventDao.getTotalNumberOfActiveOptOuts(yukonEnergyCompany, assignedProgramIds));
        model.addAttribute("scheduledOptOuts", optOutEventDao.getTotalNumberOfScheduledOptOuts(yukonEnergyCompany, assignedProgramIds));
        model.addAttribute("alternateEnrollments", programToAlternameProgramDao.getTotalNumberOfAccountsInSeasonalOptOuts(yukonEnergyCompany, assignedProgramIds));

        return new JsonView();
    }
    
    @RequestMapping(value = "/operator/optOut/admin/setDisabled", params="enableOptOuts", method = RequestMethod.POST)
    public String enableOptOutsAndCommsToday(LiteYukonUser user, ModelMap map, String programName, FlashScope flashScope) throws Exception {
        return toggleOptOutsToday(user, map, programName, OptOutEnabled.ENABLED, flashScope);
    }
    
    @RequestMapping(value = "/operator/optOut/admin/setDisabled", params="disableOptOuts", method = RequestMethod.POST)
    public String disableOptOutsToday(LiteYukonUser user, ModelMap map, String programName, FlashScope flashScope) throws Exception {
    	return toggleOptOutsToday(user, map, programName, OptOutEnabled.DISABLED_WITH_COMM, flashScope);
    }
    
    @RequestMapping(value = "/operator/optOut/admin/setDisabled", params="disableOptOutsAndComms", method = RequestMethod.POST)
    public String disableOptOutAndCommsToday(LiteYukonUser user, ModelMap map, String programName, FlashScope flashScope) throws Exception {
        return toggleOptOutsToday(user, map, programName, OptOutEnabled.DISABLED_WITHOUT_COMM, flashScope);
    }
    
    private String toggleOptOutsToday(LiteYukonUser user, ModelMap map, String programName, OptOutEnabled optOutEnabled, FlashScope flashScope) throws Exception {
        // Log disable opt outs for today attempt
        if (StringUtils.isNotBlank(programName)) {
            starsEventLogService.disablingOptOutUsageForTodayByProgramAttempted(user, programName, EventSource.OPERATOR);
        } else {
            starsEventLogService.disablingOptOutUsageForTodayAttempted(user, EventSource.OPERATOR);
        }
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE, user);
        
        if (StringUtils.isNotBlank(programName)) {
            
            try {
                
                optOutService.changeOptOutEnabledStateForTodayByProgramName(user, optOutEnabled, programName);
                flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.byProgramName.confirm." + optOutEnabled.toString(), programName));
                
            } catch (ProgramNotFoundException e) {
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.byProgramName.error.programNotFound", programName));
            }

        } else {
            optOutService.changeOptOutEnabledStateForToday(user, optOutEnabled);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.byProgramName.confirm."+ optOutEnabled.toString() +".allPrograms"));
        }
        
        return "redirect:/stars/operator/optOut/admin";
    }

    @RequestMapping(value = "/operator/optOut/admin/cancelAllOptOuts", method = RequestMethod.POST)
    public String cancelActiveOptOuts(LiteYukonUser user, ModelMap map, String programName, FlashScope flashScope) throws Exception {
    	
        if (StringUtils.isNotBlank(programName)) {
            starsEventLogService.cancelCurrentOptOutsByProgramAttempted(user, programName, EventSource.OPERATOR);
        } else {
            starsEventLogService.cancelCurrentOptOutsAttempted(user, EventSource.OPERATOR);
        }
        
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

    	return "redirect:/stars/operator/optOut/admin";
    }
    
    @RequestMapping(value = "/operator/optOut/admin/setCounts", method = RequestMethod.POST)
    public String setCounts(LiteYukonUser user, ModelMap map, String count, String dontCount, String programName, FlashScope flashScope) throws Exception {
        boolean countBool = true;
        if (StringUtils.isNotBlank(dontCount)) {
            countBool = false;
        }

        if (StringUtils.isNotBlank(programName)) {
            if (countBool) {
                starsEventLogService.countTowardOptOutLimitTodayByProgramAttempted(user, programName, EventSource.OPERATOR);
            } else {
                starsEventLogService.doNotCountTowardOptOutLimitTodayByProgramAttempted(user, programName, EventSource.OPERATOR);
            }
        } else {
            if (countBool) {
                starsEventLogService.countTowardOptOutLimitTodayAttempted(user, EventSource.OPERATOR);
            } else {
                starsEventLogService.doNotCountTowardOptOutLimitTodayAttempted(user, EventSource.OPERATOR);
            }
        }
    	
    	rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS, user);
    	
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
    	
    	return "redirect:/stars/operator/optOut/admin";
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
    		LiteLmHardwareBase inventory = 
    			(LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);
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
    	
    	private Instant startDate;
    	private Instant stopDate;
    	private String accountNumber;
    	private String serialNumber;
		
    	public Instant getStartDate() {
			return startDate;
		}
		public void setStartDate(Instant startDate) {
			this.startDate = startDate;
		}
		public Instant getStopDate() {
			return stopDate;
		}
		public void setStopDate(Instant stopDate) {
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
}
