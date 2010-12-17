package com.cannontech.web.stars.dr.operator;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.StarsApplianceDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.service.StarsApplianceService;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventoryEnrollment;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareDto;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareUiService;
import com.cannontech.web.stars.dr.operator.model.DisplayableApplianceListEntry;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.DisplayableApplianceService;
import com.cannontech.web.stars.dr.operator.validator.ApplianceValidator;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_APPLIANCES)
@RequestMapping(value = "/operator/appliances/*")
public class OperatorApplianceController {

    private AccountEventLogService accountEventLogService;
    private ApplianceValidator applianceValidator;
    private ApplianceCategoryDao applianceCategoryDao;
    private DisplayableApplianceService displayableApplianceService;
    private DisplayableInventoryEnrollmentDao displayableInventoryEnrollmentDao;
    private HardwareUiService hardwareUiService;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private ProgramDao programDao;
    private RolePropertyDao rolePropertyDao;
    private StarsApplianceDao starsApplianceDao;
    private StarsApplianceService starsApplianceService;
    private StarsDatabaseCache starsDatabaseCache;

    // APPLIANCE LIST
    @RequestMapping
    public String applianceList(ModelMap modelMap,
                                YukonUserContext userContext,
                                AccountInfoFragment accountInfoFragment) 
            throws ServletRequestBindingException {

        List<DisplayableApplianceListEntry> displayableApplianceListEntries = 
            displayableApplianceService.getDisplayableApplianceListEntries(
                                            accountInfoFragment.getAccountId());
        Collections.sort(displayableApplianceListEntries,
                         DisplayableApplianceListEntry.APPLIANCE_NAME_COMPARATOR);
        modelMap.addAttribute("displayableApplianceListEntries", displayableApplianceListEntries);

        List<ApplianceCategory> applianceCategories = 
            applianceCategoryDao.findApplianceCategories(accountInfoFragment.getAccountId());
        Collections.sort(applianceCategories, ApplianceCategory.NAME_COMPARATOR);
        modelMap.addAttribute("applianceCategories", applianceCategories);

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/appliance/applianceList.jsp";
    }

    // APPLIANCE NEW
    @RequestMapping(params="new")
    public String applianceNew(int applianceCategoryId, ModelMap modelMap,
                               YukonUserContext userContext,
                               AccountInfoFragment accountInfoFragment) 
            throws ServletRequestBindingException {

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE, 
                                       userContext.getYukonUser());
        boolean allowAccountEditing = 
            rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                                    userContext.getYukonUser());
        
        // Builds up modelMap
        ApplianceCategory applianceCategory = applianceCategoryDao.getById(applianceCategoryId);
        StarsAppliance starsAppliance = new StarsAppliance();
        starsAppliance.setApplianceCategory(applianceCategory);
        modelMap.addAttribute("starsAppliance", starsAppliance);

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        modelMap.addAttribute("mode", 
                              allowAccountEditing ? PageEditMode.CREATE : PageEditMode.VIEW);

        modelMap.addAttribute("applianceCategoryName", applianceCategory.getName());
        modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());

        return "operator/appliance/applianceEdit.jsp";
    }

    // APPLIANCE CREATE
    @RequestMapping(params="create")
    public String applianceCreate(@ModelAttribute("starsAppliance") StarsAppliance starsAppliance,
                                  BindingResult bindingResult,
                                  ModelMap modelMap,
                                  YukonUserContext userContext,
                                  HttpSession session, FlashScope flashScope,
                                  AccountInfoFragment accountInfoFragment) {
        
        // Log appliance deletion attempt
        int inventoryId = starsAppliance.getInventoryID();
        String serialNumber = "";
        String programName = "";
        if (inventoryId != 0) {
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
            serialNumber = lmHardwareBase != null ? lmHardwareBase.getManufacturerSerialNumber() : "";
            int programId = starsAppliance.getProgramID();
            Program program = programDao.getByProgramId(programId);
            programName = program != null ? program.getProgramName() : "";
        }

        accountEventLogService.applianceAdditionAttemptedByOperator(userContext.getYukonUser(), 
                                                                    accountInfoFragment.getAccountNumber(), 
                                                                    starsAppliance.getApplianceCategory().getName(), 
                                                                    serialNumber, 
                                                                    programName);
        
        // Check permissions
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, 
                                       userContext.getYukonUser());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE, 
                                       userContext.getYukonUser());


        ApplianceCategory applianceCategory = 
            applianceCategoryDao.getById(starsAppliance.getApplianceCategory().getApplianceCategoryId());
        starsAppliance.setApplianceCategory(applianceCategory);

        // validate/create
        try {

            applianceValidator.validate(starsAppliance, bindingResult);

            if (!bindingResult.hasErrors()) {
                starsApplianceService.createStarsAppliance(starsAppliance,
                                                           accountInfoFragment.getEnergyCompanyId(),
                                                           accountInfoFragment.getAccountId(),
                                                           userContext.getYukonUser());
                EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(),
                                         EventUtils.EVENT_CATEGORY_ACCOUNT,
                                         YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED,
                                         accountInfoFragment.getAccountId(),
                                         session);
            }
        } finally {

            setupApplianceEditModelMap(accountInfoFragment, modelMap, userContext,
                                       starsAppliance.getApplianceID());

            if (bindingResult.hasErrors()) {
                List<MessageSourceResolvable> messages = 
                    YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
                modelMap.addAttribute("mode", PageEditMode.CREATE);

                return "operator/appliance/applianceEdit.jsp";
            }
        }

        flashScope.setConfirm(new YukonMessageSourceResolvable(
                                      "yukon.web.modules.operator.appliance.applianceCreated"));

        setupApplianceEditModelMap(accountInfoFragment, modelMap, userContext,
                                   starsAppliance.getApplianceID());
        return "redirect:applianceList";
    }

    // APPLIANCE EDIT
    @RequestMapping
    public String applianceEdit(int applianceId, ModelMap modelMap,
                                YukonUserContext userContext,
                                AccountInfoFragment accountInfoFragment)
            throws ServletRequestBindingException {

        boolean allowAccountEditing = 
            rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                                    userContext.getYukonUser());
        
        // Appliance Information
        LiteStarsAppliance liteStarsAppliance = 
            starsApplianceDao.getByApplianceIdAndEnergyCompanyId(applianceId,
                                                                 accountInfoFragment.getEnergyCompanyId());
        LiteStarsEnergyCompany energyCompany = 
            starsDatabaseCache.getEnergyCompany(accountInfoFragment.getEnergyCompanyId());
        StarsAppliance starsAppliance = StarsLiteFactory.createStarsAppliance(liteStarsAppliance,
                                                                              energyCompany);
        modelMap.addAttribute("starsAppliance", starsAppliance);

        // Enrollment
        DisplayableInventoryEnrollment displayableInventoryEnrollment = 
            displayableInventoryEnrollmentDao.find(accountInfoFragment.getAccountId(),
                                                   liteStarsAppliance.getInventoryID(),
                                                   liteStarsAppliance.getProgramID());
        modelMap.addAttribute("displayableInventoryEnrollment",
                              displayableInventoryEnrollment);

        // Hardware Summary
        if (liteStarsAppliance.getInventoryID() > 0) {
            HardwareDto hardwareDto = 
                hardwareUiService.getHardwareDto(liteStarsAppliance.getInventoryID(),
                                               accountInfoFragment.getEnergyCompanyId(), 
                                               accountInfoFragment.getAccountId());
            modelMap.addAttribute("hardware", hardwareDto);
        }

        setupApplianceEditModelMap(accountInfoFragment, modelMap, userContext, applianceId);
        modelMap.addAttribute("mode", 
                              allowAccountEditing ? PageEditMode.EDIT : PageEditMode.VIEW);
        modelMap.addAttribute("applianceCategoryName",
                              liteStarsAppliance.getApplianceCategory().getName());

        return "operator/appliance/applianceEdit.jsp";
    }

    // APPLIANCE UPDATE
    @RequestMapping(params="update")
    public String applianceUpdate(@ModelAttribute("starsAppliance") StarsAppliance starsAppliance,
                                   BindingResult bindingResult,
                                   ModelMap modelMap,
                                   YukonUserContext userContext,
                                   HttpSession session, FlashScope flashScope,
                                   AccountInfoFragment accountInfoFragment) {

        // Log appliance deletion attempt
        int inventoryId = starsAppliance.getInventoryID();
        String serialNumber = "";
        String programName = "";
        if (inventoryId != 0) {
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
            serialNumber = lmHardwareBase != null ? lmHardwareBase.getManufacturerSerialNumber() : "";
            int programId = starsAppliance.getProgramID();
            Program program = programDao.getByProgramId(programId);
            programName = program != null ? program.getProgramName() : "";
        }

        accountEventLogService.applianceUpdateAttemptedByOperator(userContext.getYukonUser(), 
                                                                  accountInfoFragment.getAccountNumber(), 
                                                                  starsAppliance.getApplianceCategory().getName(), 
                                                                  serialNumber, 
                                                                  programName);
        
        // Check permissions
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, 
                                       userContext.getYukonUser());
        
        // validate/update
        try {

            applianceValidator.validate(starsAppliance, bindingResult);

            if (!bindingResult.hasErrors()) {
                ApplianceCategory applianceCategory = 
                    applianceCategoryDao.getById(starsAppliance.getApplianceCategory().getApplianceCategoryId());

                starsAppliance.setApplianceCategory(applianceCategory);
                starsApplianceService.updateStarsAppliance(starsAppliance,
                                                           accountInfoFragment.getEnergyCompanyId(),
                                                           userContext.getYukonUser());

                EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(),
                                         EventUtils.EVENT_CATEGORY_ACCOUNT,
                                         YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED,
                                         accountInfoFragment.getAccountId(),
                                         session);
            }
        } finally {

            setupApplianceEditModelMap(accountInfoFragment, modelMap,
                                       userContext,
                                       starsAppliance.getApplianceID());
            if (bindingResult.hasErrors()) {

                List<MessageSourceResolvable> messages = 
                    YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setMessage(messages, FlashScopeMessageType.ERROR);

                return "operator/appliance/applianceEdit.jsp";
            }
        }

        flashScope.setConfirm(new YukonMessageSourceResolvable(
                                      "yukon.web.modules.operator.appliance.applianceUpdated"));

        setupApplianceEditModelMap(accountInfoFragment, modelMap, userContext,
                                   starsAppliance.getApplianceID());
        return "redirect:applianceList";
    }

    // APPLIANCE DELETE
    @RequestMapping(params="delete")
    public String applianceDelete(int applianceId, 
                                   ModelMap modelMap,
                                   AccountInfoFragment accountInfoFragment,
                                   YukonUserContext userContext,
                                   FlashScope flashScope,
                                   HttpSession session)
            throws ServletRequestBindingException {

        
        
        // Log appliance deletion attempt
        LiteStarsAppliance liteStarsAppliance = 
            starsApplianceDao.getByApplianceIdAndEnergyCompanyId(applianceId,
                                                                 accountInfoFragment.getEnergyCompanyId());
        int inventoryId = liteStarsAppliance.getInventoryID();
        String serialNumber = "";
        String programName = "";
        if (inventoryId != 0) {
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
            serialNumber = lmHardwareBase != null ? lmHardwareBase.getManufacturerSerialNumber() : "";
            int programId = liteStarsAppliance.getProgramID();
            Program program = programDao.getByProgramId(programId);
            programName = program != null ? program.getProgramName() : "";
        }

        accountEventLogService.applianceDeletionAttemptedByOperator(userContext.getYukonUser(), 
                                                                    accountInfoFragment.getAccountNumber(), 
                                                                    liteStarsAppliance.getApplianceCategory().getName(), 
                                                                    serialNumber, 
                                                                    programName);
        
        // Check permissions
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, 
                                       userContext.getYukonUser());

        starsApplianceService.removeStarsAppliance(applianceId,
                                                   accountInfoFragment.getEnergyCompanyId(),
                                                   accountInfoFragment.getAccountNumber(),
                                                   userContext.getYukonUser());
        EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(),
                                 EventUtils.EVENT_CATEGORY_ACCOUNT,
                                 YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED,
                                 accountInfoFragment.getAccountId(), session);

        flashScope.setConfirm(new YukonMessageSourceResolvable(
                                      "yukon.web.modules.operator.appliance.applianceDeleted"));
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:applianceList";
    }

    @RequestMapping(params="cancel")
    public String cancel(ModelMap modelMap,
                          AccountInfoFragment accountInfoFragment,
                          YukonUserContext userContext,
                          HttpSession session) {

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:applianceList";
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {

        if (binder.getTarget() != null) {
            DefaultMessageCodesResolver msgCodesResolver = new DefaultMessageCodesResolver();
            msgCodesResolver.setPrefix("yukon.web.modules.operator.appliance.");
            binder.setMessageCodesResolver(msgCodesResolver);
        }

    }

    // MISC MODEL ITEMS FOR APPLIANCE EDIT
    private void setupApplianceEditModelMap(AccountInfoFragment accountInfoFragment,
                                              ModelMap modelMap,
                                              YukonUserContext userContext,
                                              int applianceId) {

        modelMap.addAttribute("applianceId", applianceId);
        modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
    }

    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
    
    @Autowired
    public void setApplianceValidator(ApplianceValidator applianceValidator) {
        this.applianceValidator = applianceValidator;
    }

    @Autowired
    public void setApplianceCategoryDao(ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

    @Autowired
    public void setDisplayableApplianceService(DisplayableApplianceService displayableApplianceService) {
        this.displayableApplianceService = displayableApplianceService;
    }

    @Autowired
    public void setDisplayableInventoryEnrollmentDao(
                      DisplayableInventoryEnrollmentDao displayableInventoryEnrollmentDao) {
        this.displayableInventoryEnrollmentDao = displayableInventoryEnrollmentDao;
    }

    @Autowired
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setHardwareUiService(HardwareUiService hardwareUiService) {
        this.hardwareUiService = hardwareUiService;
    }

    @Autowired
    public void setStarsApplianceDao(StarsApplianceDao starsApplianceDao) {
        this.starsApplianceDao = starsApplianceDao;
    }

    @Autowired
    public void setStarsApplianceService(StarsApplianceService starsApplianceService) {
        this.starsApplianceService = starsApplianceService;
    }
}
