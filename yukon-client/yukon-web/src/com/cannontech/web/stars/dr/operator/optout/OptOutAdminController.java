package com.cannontech.web.stars.dr.operator.optout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.ProgramToAlternateProgramDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutCountsTemporaryOverride;
import com.cannontech.stars.dr.optout.model.OptOutEnabled;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutSurvey;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.stars.dr.optout.service.OptOutSurveyService;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
    @Autowired private StarsEventLogService starsEventLogService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private OptOutSurveyService optOutSurveyService;
    @Autowired private SelectionListService selectionListService;
    
    @RequestMapping(value = "/operator/optOut/admin", method = RequestMethod.GET)
    public String view(YukonUserContext userContext, ModelMap model, Boolean emptyProgramName, Boolean programNotFound) throws Exception {
        
        final LiteYukonUser user = userContext.getYukonUser();
        rolePropertyDao.verifyAnyProperties(user, 
                YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_STATUS,
                YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE,
                YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS,
                YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT, 
                YukonRoleProperty.ADMIN_VIEW_OPT_OUT_EVENTS); 
        
        if(ecDao.isEnergyCompanyOperator(user)){
            EnergyCompany energyCompany = ecDao.getEnergyCompany(user);
            model.addAttribute("energyCompanyId", energyCompany.getId());
    
            Map<String, Object> optOutsJson = systemOptOuts(new ArrayList<Integer>(0), userContext);
            model.addAttribute("totalNumberOfAccounts", optOutsJson.get("totalNumberOfAccounts"));
            model.addAttribute("currentOptOuts", optOutsJson.get("currentOptOuts"));
            model.addAttribute("scheduledOptOuts", optOutsJson.get("scheduledOptOuts"));
            model.addAttribute("alternateEnrollments", optOutsJson.get("alternateEnrollments"));

            // programNameEnabledMap
            OptOutEnabled defaultOptOutEnabledSetting = optOutStatusService.getDefaultOptOutEnabled(user);
            Map<Integer, OptOutEnabled> programSpecificEnabledOptOuts = 
                optOutStatusService.getProgramSpecificEnabledOptOuts(energyCompany.getId()); 
    
            Map<String, OptOutEnabled> programNameEnabledMap = Maps.newLinkedHashMap();
            for (Entry<Integer, OptOutEnabled> programOptOutEnabledEntry : programSpecificEnabledOptOuts.entrySet()) {
                
                int programId = programOptOutEnabledEntry.getKey();
                Program program = programDao.getByProgramId(programId);
                programNameEnabledMap.put(program.getProgramName(), programOptOutEnabledEntry.getValue());
            }
            model.addAttribute("programNameEnabledMap", programNameEnabledMap);
            model.addAttribute("energyCompanyOptOutEnabledSetting", defaultOptOutEnabledSetting);
    
            // programNameCountsMap
            OptOutCountsTemporaryOverride defaultOptOutCountsSetting = optOutStatusService.getDefaultOptOutCounts(user);
            List<OptOutCountsTemporaryOverride> programSpecificOptOutCounts = optOutStatusService.getProgramSpecificOptOutCounts(user);
            
            Map<String, OptOutCounts> programNameCountsMap = Maps.newLinkedHashMap();
            for (OptOutCountsTemporaryOverride setting : programSpecificOptOutCounts) {
                
                int programId = setting.getAssignedProgramId();
                Program program = programDao.getByProgramId(programId);
                programNameCountsMap.put(program.getProgramName(), setting.getOptOutCounts());
            }
            model.addAttribute("programNameCountsMap", programNameCountsMap);
            model.addAttribute("energyCompanyOptOutCountsSetting", defaultOptOutCountsSetting.getOptOutCounts());
            
            // Get the customer search by list for search drop down box
            YukonSelectionList yukonSelectionList = selectionListService.getSelectionList(energyCompany,
                                                          YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE);
            List<YukonListEntry> customerSearchList = new ArrayList<>();
            List<YukonListEntry> yukonListEntries = yukonSelectionList.getYukonListEntries();
            for (YukonListEntry entry : yukonListEntries) {
                if (entry.getYukonDefID() != YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_METER_NO) {
                    customerSearchList.add(entry);
                }
            }
            model.addAttribute("customerSearchList", customerSearchList);
            
            model.addAttribute("emptyProgramName", emptyProgramName);
            model.addAttribute("programNotFound", programNotFound);
        }
        
        // Second column
        setupScheduledOptOuts(user, model);
        setUpOptOutSurveys(user, model);

        return "operator/optout/optOutAdmin.jsp";
    }

    @RequestMapping(value = "/operator/optOut/systemOptOuts", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object>  systemOptOuts(@RequestBody List<Integer> assignedProgramIds, YukonUserContext userContext) {
        YukonEnergyCompany yukonEnergyCompany = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
        Map<String, Object> json = new HashMap<>();

        json.put("totalNumberOfAccounts", customerAccountDao.getTotalNumberOfAccounts(yukonEnergyCompany, assignedProgramIds));
        json.put("currentOptOuts", optOutEventDao.getTotalNumberOfActiveOptOuts(yukonEnergyCompany, assignedProgramIds));
        json.put("scheduledOptOuts", optOutEventDao.getTotalNumberOfScheduledOptOuts(yukonEnergyCompany, assignedProgramIds));
        json.put("alternateEnrollments", programToAlternameProgramDao.getTotalNumberOfDevicesInSeasonalOptOuts(yukonEnergyCompany, assignedProgramIds));

        return json;
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
                flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.byProgramName.confirm." + optOutEnabled.toString(), programName));
                
            } catch (ProgramNotFoundException e) {
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.byProgramName.error.programNotFound", programName));
            }

        } else {
            optOutService.changeOptOutEnabledStateForToday(user, optOutEnabled);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.byProgramName.confirm."+ optOutEnabled.toString() +".allPrograms"));
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
                flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.byProgramName.confirm.successfullyCanceledCurrentOptOuts", programName));
                
            } catch (ProgramNotFoundException e) {
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.byProgramName.error.programNotFound", programName));
            }

        } else {
            optOutService.cancelAllOptOuts(user);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.byProgramName.confirm.successfullyCanceledCurrentOptOuts.allPrograms"));
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
                flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.byProgramName.confirm.countingTodaysOptOuts", programName));
                if (!countBool) {
                    flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.byProgramName.confirm.notCountingTodaysOptOuts", programName));
                }
                
            } catch (ProgramNotFoundException e) {
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.byProgramName.error.programNotFound", programName));
            }

        } else {
            optOutService.changeOptOutCountStateForToday(user, countBool);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.byProgramName.confirm.countingTodaysOptOuts.allPrograms"));
            if (!countBool) {
                flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.byProgramName.confirm.notCountingTodaysOptOuts.allPrograms"));
            }
        }
        
        return "redirect:/stars/operator/optOut/admin";
    }

    public void setupScheduledOptOuts(LiteYukonUser user, ModelMap map) throws Exception {

        // Only load these events when they have the property set
        if (rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_VIEW_OPT_OUT_EVENTS, user)) {
        
            EnergyCompany energyCompany = ecDao.getEnergyCompany(user);
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
            YukonSelectionList yukonSelectionList = selectionListService.getSelectionList(energyCompany,
                                              YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE);
            List<YukonListEntry> customerSearchList = new ArrayList<YukonListEntry>();
            List<YukonListEntry> yukonListEntries = yukonSelectionList.getYukonListEntries();
            for (YukonListEntry entry : yukonListEntries) {
                if (entry.getYukonDefID() != YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_METER_NO) {
                    customerSearchList.add(entry);
                }
            }
            map.addAttribute("customerSearchList", customerSearchList);
        }
    }

    private void setUpOptOutSurveys(LiteYukonUser user, ModelMap model) {
        if (rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_OPT_OUT_SURVEY_EDIT, user)) {
            int energyCompanyId = ecDao.getEnergyCompany(user).getId();
            Set<OptOutSurvey> surveys = new HashSet<>(optOutSurveyService.findSurveys(energyCompanyId, 0, Integer.MAX_VALUE).getResultList());
            model.addAttribute("totalSurveys", surveys.size());
            final Date now = new Date();

           Predicate<OptOutSurvey> isActive = new Predicate<OptOutSurvey>() {
               @Override public boolean apply(OptOutSurvey survey) {
                   if (survey.getStartDate() != null && survey.getStartDate().after(now)) {
                    return false;
                }
                   if (survey.getStopDate() != null && survey.getStopDate().before(now)) {
                    return false;
                }
                   return true;
               }
           };
           Set<OptOutSurvey> activeSurveys = Sets.filter(surveys, isActive);
           model.addAttribute("activeSurveys", activeSurveys.size());

           Instant lastWeek = Instant.now().minus(Duration.standardDays(7));
           Instant last30Days = Instant.now().minus(Duration.standardDays(30));

           int resultsInLastWeek = optOutSurveyService.countAllSurveyResultsBetween(lastWeek, null);
           int resultsInLast30Days = optOutSurveyService.countAllSurveyResultsBetween(last30Days, Instant.now());
           model.addAttribute("resultsInLastWeek", resultsInLastWeek);
           model.addAttribute("resultsInLast30Days", resultsInLast30Days);
        }
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
