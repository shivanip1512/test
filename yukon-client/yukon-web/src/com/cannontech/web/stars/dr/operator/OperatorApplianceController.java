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
import com.cannontech.common.validator.YukonValidationUtils;
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
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareDto;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareService;
import com.cannontech.web.stars.dr.operator.model.DisplayableApplianceListEntry;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.DisplayableApplianceService;
import com.cannontech.web.stars.dr.operator.validator.ApplianceValidator;

@Controller
@RequestMapping(value = "/operator/appliances/*")
public class OperatorApplianceController {

    private ApplianceValidator applianceValidator;
    private ApplianceCategoryDao applianceCategoryDao;
    private DisplayableApplianceService displayableApplianceService;
    private DisplayableInventoryEnrollmentDao displayableInventoryEnrollmentDao;
    private HardwareService hardwareService;
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
    @RequestMapping
    public String applianceNew(int applianceCategoryId, ModelMap modelMap,
                               YukonUserContext userContext,
                               AccountInfoFragment accountInfoFragment) 
            throws ServletRequestBindingException {

        ApplianceCategory applianceCategory = applianceCategoryDao.getById(applianceCategoryId);
        StarsAppliance starsAppliance = new StarsAppliance();
        starsAppliance.setApplianceCategory(applianceCategory);
        modelMap.addAttribute("starsAppliance", starsAppliance);

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        modelMap.addAttribute("mode", PageEditMode.CREATE);

        modelMap.addAttribute("applianceCategoryName", applianceCategory.getName());

        return "operator/appliance/applianceNew.jsp";
    }

    // APPLIANCE CREATE
    @RequestMapping
    public String applianceCreate(@ModelAttribute("starsAppliance") StarsAppliance starsAppliance,
                                  BindingResult bindingResult,
                                  ModelMap modelMap,
                                  YukonUserContext userContext,
                                  HttpSession session, FlashScope flashScope,
                                  AccountInfoFragment accountInfoFragment) {

        ApplianceCategory applianceCategory = 
            applianceCategoryDao.getById(starsAppliance.getApplianceCategory().getApplianceCategoryId());
        starsAppliance.setApplianceCategory(applianceCategory);

        // validate/create
        try {

            applianceValidator.validate(starsAppliance, bindingResult);

            if (!bindingResult.hasErrors()) {
                starsApplianceService.createStarsAppliance(starsAppliance,
                                                           accountInfoFragment.getEnergyCompanyId(),
                                                           accountInfoFragment.getAccountId());
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

                return "operator/appliance/applianceNew.jsp";
            }
        }

        flashScope.setConfirm(new YukonMessageSourceResolvable(
                                      "yukon.web.modules.operator.appliance.applianceCreated"));

        setupApplianceEditModelMap(accountInfoFragment, modelMap, userContext,
                                   starsAppliance.getApplianceID());
        return "redirect:applianceEdit";
    }

    // APPLIANCE EDIT
    @RequestMapping
    public String applianceEdit(int applianceId, ModelMap modelMap,
                                YukonUserContext userContext,
                                AccountInfoFragment accountInfoFragment)
            throws ServletRequestBindingException {

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
                hardwareService.getHardwareDto(liteStarsAppliance.getInventoryID(),
                                               accountInfoFragment.getEnergyCompanyId(), accountInfoFragment.getAccountId());
            modelMap.addAttribute("hardware", hardwareDto);
        }

        setupApplianceEditModelMap(accountInfoFragment, modelMap, userContext, applianceId);
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        modelMap.addAttribute("applianceCategoryName",
                              liteStarsAppliance.getApplianceCategory().getName());

        return "operator/appliance/applianceEdit.jsp";
    }

    // APPLIANCE UPDATE
    @RequestMapping
    public String applianceUpdate(@ModelAttribute("starsAppliance") StarsAppliance starsAppliance,
                                   BindingResult bindingResult,
                                   ModelMap modelMap,
                                   YukonUserContext userContext,
                                   HttpSession session, FlashScope flashScope,
                                   AccountInfoFragment accountInfoFragment) {

        // validate/update
        try {

            applianceValidator.validate(starsAppliance, bindingResult);

            if (!bindingResult.hasErrors()) {
                ApplianceCategory applianceCategory = 
                    applianceCategoryDao.getById(starsAppliance.getApplianceCategory().getApplianceCategoryId());

                starsAppliance.setApplianceCategory(applianceCategory);
                starsApplianceService.updateStarsAppliance(starsAppliance,
                                                           accountInfoFragment.getEnergyCompanyId());
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
                                      "yukon.web.modules.operator.appliance.edit.applianceUpdated"));

        setupApplianceEditModelMap(accountInfoFragment, modelMap, userContext,
                                   starsAppliance.getApplianceID());
        return "redirect:applianceEdit";
    }

    // APPLIANCE DELETE
    @RequestMapping
    public String applianceDelete(int applianceId, ModelMap modelMap,
                                  AccountInfoFragment accountInfoFragment,
                                  YukonUserContext userContext,
                                  HttpSession session)
            throws ServletRequestBindingException {

        starsApplianceService.removeStarsAppliance(applianceId,
                                                   accountInfoFragment.getEnergyCompanyId(),
                                                   accountInfoFragment.getAccountNumber(),
                                                   userContext.getYukonUser());
        EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(),
                                 EventUtils.EVENT_CATEGORY_ACCOUNT,
                                 YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED,
                                 accountInfoFragment.getAccountId(), session);

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
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
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
    public void setDisplayableApplianceService(
                      DisplayableApplianceService displayableApplianceService) {
        this.displayableApplianceService = displayableApplianceService;
    }

    @Autowired
    public void setDisplayableInventoryEnrollmentDao(
                      DisplayableInventoryEnrollmentDao displayableInventoryEnrollmentDao) {
        this.displayableInventoryEnrollmentDao = displayableInventoryEnrollmentDao;
    }

    @Autowired
    public void setHardwareService(HardwareService hardwareService) {
        this.hardwareService = hardwareService;
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
