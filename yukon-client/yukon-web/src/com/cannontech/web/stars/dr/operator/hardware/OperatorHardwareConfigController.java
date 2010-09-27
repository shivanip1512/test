package com.cannontech.web.stars.dr.operator.hardware;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventoryEnrollment;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.HardwareConfigType;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardwareConfig.service.HardwareConfigService;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.HardwareAction;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareConfigurationDto;
import com.cannontech.web.stars.dr.operator.hardware.model.ProgramEnrollmentDto;
import com.cannontech.web.stars.dr.operator.hardware.validator.ColdLoadPickupValidator;
import com.cannontech.web.stars.dr.operator.hardware.validator.HardwareConfigurationDtoValidator;
import com.cannontech.web.stars.dr.operator.hardware.validator.MeterConfigValidator;
import com.cannontech.web.stars.dr.operator.hardware.validator.TamperDetectValidator;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
@RequestMapping("/operator/hardware/config/*")
public class OperatorHardwareConfigController {
    private HardwareEventLogService hardwareEventLogService;
    
    private DisplayableInventoryEnrollmentDao displayableInventoryEnrollmentDao;
    private InventoryDao inventoryDao;
    private LoadGroupDao loadGroupDao;
    private AssignedProgramDao assignedProgramDao;
    private RolePropertyDao rolePropertyDao;
    private EnrollmentDao enrollmentDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private HardwareConfigService hardwareConfigService;
    private ApplianceCategoryDao applianceCategoryDao;
    private MeterDao meterDao;
    private MeterConfigValidator meterConfigValidator;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private PaoDao paoDao;
    private ColdLoadPickupValidator coldLoadPickupValidator = new ColdLoadPickupValidator();
    private TamperDetectValidator tamperDetectValidator = new TamperDetectValidator();

    @RequestMapping
    public String edit(ModelMap model, int inventoryId,
            YukonUserContext userContext, AccountInfoFragment accountInfo) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfo, model);

        verifyHardwareIsForAccount(inventoryId, accountInfo);

        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("displayName", hardware.getDisplayName());

        HardwareConfigurationDto configuration =
            new HardwareConfigurationDto(hardware.getHardwareType().getHardwareConfigType());
        configuration.setAccountId(accountInfo.getAccountId());
        configuration.setInventoryId(inventoryId);
        boolean trackHardwareAddressing =
            rolePropertyDao.checkProperty(YukonRoleProperty.TRACK_HARDWARE_ADDRESSING,
                                          userContext.getYukonUser());
        if (trackHardwareAddressing) {
            // add the STARS LM configuration for the device to the model
            LiteStarsEnergyCompany energyCompany =
                StarsDatabaseCache.getInstance().getEnergyCompany(accountInfo.getEnergyCompanyId());
            StarsCustAccountInformation starsCustAccountInfo =
                energyCompany.getStarsCustAccountInformation(accountInfo.getAccountId());
            StarsInventories inventories = starsCustAccountInfo.getStarsInventories();
            for (int index = 0; index < inventories.getStarsInventoryCount(); index++) {
                StarsInventory inventory = inventories.getStarsInventory(index);
                if (inventory.getInventoryID() == inventoryId) {
                    StarsLMConfiguration starsConfig = inventory.getLMHardware().getStarsLMConfiguration();
                    configuration.setStarsLMConfiguration(starsConfig,
                                                   hardware.getHardwareType()); 
                    break;
                }
            }
        }

        model.addAttribute("configuration", configuration);
        return prepareForEdit(true, model, configuration, null, userContext, accountInfo);
    }

    private String prepareForEdit(boolean initConfig, ModelMap model,
            @ModelAttribute("configuration") HardwareConfigurationDto configuration,
            BindingResult bindingResult, YukonUserContext userContext,
            AccountInfoFragment accountInfo) {
        int inventoryId = configuration.getInventoryId();

        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
        model.addAttribute("inService", enrollmentDao.isInService(inventoryId));

        List<DisplayableInventoryEnrollment> enrollments =
            displayableInventoryEnrollmentDao.find(accountInfo.getAccountId(), inventoryId);
        Collections.sort(enrollments, new Comparator<DisplayableInventoryEnrollment>() {
            @Override
            public int compare(DisplayableInventoryEnrollment enrollment1,
                    DisplayableInventoryEnrollment enrollment2) {
                String displayName1 = enrollment1.getProgramName().getDisplayName();
                String displayName2 = enrollment2.getProgramName().getDisplayName();
                return displayName1.compareToIgnoreCase(displayName2);
            }});
        model.addAttribute("enrollments", enrollments);

        Map<Integer, List<LoadGroup>> loadGroupsByProgramId = Maps.newHashMap();
        model.addAttribute("loadGroupsByProgramId", loadGroupsByProgramId);
        List<ProgramEnrollmentDto> programEnrollments =
            Lists.newArrayListWithCapacity(enrollments.size());
        Set<Integer> assignedProgramSet = Sets.newHashSet();
        for (DisplayableInventoryEnrollment enrollment : enrollments) {
            List<LoadGroup> loadGroups = loadGroupDao.getByProgramId(enrollment.getProgramId());
            loadGroupsByProgramId.put(enrollment.getAssignedProgramId(), loadGroups);

            ProgramEnrollmentDto programEnrollment = new ProgramEnrollmentDto();
            programEnrollment.setAssignedProgramId(enrollment.getAssignedProgramId());
            programEnrollment.setLoadGroupId(enrollment.getLoadGroupId());
            programEnrollment.setRelay(enrollment.getRelay());
            programEnrollments.add(programEnrollment);

            assignedProgramSet.add(enrollment.getAssignedProgramId());
        }
        List<AssignedProgram> assignedProgramsList =
            assignedProgramDao.getByIds(assignedProgramSet);
        if (initConfig) {
            configuration.setProgramEnrollments(programEnrollments);
        }

        Set<Integer> applianceCategoryIds = Sets.newHashSet();
        Map<Integer, AssignedProgram> assignedPrograms = Maps.newHashMap();
        for (AssignedProgram assignedProgram : assignedProgramsList) {
            applianceCategoryIds.add(assignedProgram.getApplianceCategoryId());
            assignedPrograms.put(assignedProgram.getAssignedProgramId(),
                                 assignedProgram);
        }
        model.addAttribute("assignedPrograms", assignedPrograms);

        Map<Integer, ApplianceCategory> applianceCategories =
            applianceCategoryDao.getByApplianceCategoryIds(applianceCategoryIds);
        model.addAttribute("applianceCategories", applianceCategories);

        return "operator/hardware/config/edit.jsp";
    }

    @SuppressWarnings("unchecked")
    private BindingResult bind(ModelMap model, HttpServletRequest request,
            Object target, String objectName, YukonUserContext userContext) {
        ServletRequestDataBinder binder =
            new ServletRequestDataBinder(target, objectName);
        initBinder(binder, userContext);
        binder.bind(request);
        BindingResult bindingResult = binder.getBindingResult();
        model.addAttribute("configuration", target);
        model.putAll(binder.getBindingResult().getModel());
        return bindingResult;
    }

    @RequestMapping
    public String commit(ModelMap model, HttpServletRequest request,
            int inventoryId, YukonUserContext userContext,
            AccountInfoFragment accountInfo, FlashScope flashScope) {

        // Log hardware configuration attempt
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.hardwareConfigAttemptedByOperator(userContext.getYukonUser(),
                                                                  lmHardwareBase.getManufacturerSerialNumber(),
                                                                  accountInfo.getAccountNumber());
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());

        // Manually bind the configuration so we can create the correct type
        // based on hardwareConfigType.
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        HardwareConfigType hardwareConfigType = hardware.getHardwareType().getHardwareConfigType();
        HardwareConfigurationDto configuration = new HardwareConfigurationDto(hardwareConfigType);
        BindingResult bindingResult = bind(model, request, configuration,
                                           "configuration", userContext);

        verifyHardwareIsForAccount(inventoryId, accountInfo);

        String action = configuration.getAction();
        StarsUpdateLMHardwareConfig hardwareConfig = new StarsUpdateLMHardwareConfig();
        hardwareConfig.setInventoryID(inventoryId);
        hardwareConfig.setSaveToBatch("saveToBatch".equals(action));
        hardwareConfig.setSaveConfigOnly("saveConfigOnly".equals(action));

        boolean trackHardwareAddressing =
            rolePropertyDao.checkProperty(YukonRoleProperty.TRACK_HARDWARE_ADDRESSING,
                                          userContext.getYukonUser());
        for (ProgramEnrollmentDto programEnrollment: configuration.getProgramEnrollments()) {
            StarsLMHardwareConfig starsConfig = new StarsLMHardwareConfig();
            if (!trackHardwareAddressing) {
                starsConfig.setGroupID(programEnrollment.getLoadGroupId());
            }
            starsConfig.setProgramID(programEnrollment.getAssignedProgramId());
            starsConfig.setLoadNumber(programEnrollment.getRelay());
            hardwareConfig.addStarsLMHardwareConfig(starsConfig);
        }

        try {
            if (trackHardwareAddressing) {
                HardwareConfigurationDtoValidator validator =
                    new HardwareConfigurationDtoValidator();
                validator.validate(configuration, bindingResult);
                coldLoadPickupValidator.validate(configuration, bindingResult);
                if (hardware.getHardwareType().isHasTamperDetect()) {
                    tamperDetectValidator.validate(configuration, bindingResult);
                }
                if (bindingResult.hasErrors()) {
                    List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
                    flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
                    return prepareForEdit(false, model, configuration,
                                            bindingResult, userContext, accountInfo);
                }
                hardwareConfig.setStarsLMConfiguration(configuration.getStarsLMConfiguration(hardware.getHardwareType()));
            }
            LiteStarsLMHardware liteHw = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(inventoryId);
            LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(accountInfo.getEnergyCompanyId());
            HardwareAction.updateLMHardwareConfig(hardwareConfig, liteHw, userContext.getYukonUser().getUserID(), energyCompany);

            MessageSourceResolvable confirmationMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareConfig." +
                                                 action + "Complete",
                                                 hardware.getDisplayName());
            flashScope.setConfirm(confirmationMessage);
        } catch (WebClientException wce) {
            MessageSourceResolvable errorMessage = YukonMessageSourceResolvable.createDefaultWithoutCode(wce.getMessage());
            flashScope.setError(errorMessage);
        }

        return "redirect:/spring/stars/operator/hardware/config/edit?accountId=" +
            accountInfo.getAccountId() + "&inventoryId=" + inventoryId;
    }

    @RequestMapping
    public String disable(ModelMap model, int inventoryId,
            YukonUserContext userContext,
            AccountInfoFragment accountInfo, FlashScope flashScope) {
        
        // Log hardware disable attempt
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.hardwareDisableAttemptedByOperator(userContext.getYukonUser(),
                                                                   lmHardwareBase.getManufacturerSerialNumber(),
                                                                   accountInfo.getAccountNumber());
        
        // Validate request
        verifyHardwareIsForAccount(inventoryId, accountInfo);
        model.addAttribute("accountId", accountInfo.getAccountId());
        model.addAttribute("inventoryId", inventoryId);

        try {
            hardwareConfigService.disable(inventoryId,
                                          accountInfo.getAccountId(),
                                          accountInfo.getEnergyCompanyId(),
                                          userContext);

            MessageSourceResolvable confirmationMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareConfig.disableCommandSent");
            flashScope.setConfirm(confirmationMessage);
        } catch (WebClientException wce) {
            MessageSourceResolvable errorMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareConfig.disableCommandFailed",
                                                 wce.getMessage());
            flashScope.setError(errorMessage);
        }

        return "redirect:/spring/stars/operator/hardware/config/edit";
    }

    @RequestMapping
    public String enable(ModelMap model, int inventoryId,
            YukonUserContext userContext,
            AccountInfoFragment accountInfo, FlashScope flashScope) {

        // Log hardware disable attempt
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.hardwareEnableAttemptedByOperator(userContext.getYukonUser(),
                                                                  lmHardwareBase.getManufacturerSerialNumber(),
                                                                  accountInfo.getAccountNumber());
        
        // Validate request
        verifyHardwareIsForAccount(inventoryId, accountInfo);
        model.addAttribute("accountId", accountInfo.getAccountId());
        model.addAttribute("inventoryId", inventoryId);

        try {
            hardwareConfigService.enable(inventoryId,
                                         accountInfo.getAccountId(),
                                         accountInfo.getEnergyCompanyId(),
                                         userContext);

            MessageSourceResolvable confirmationMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareConfig.enableCommandSent");
            flashScope.setConfirm(confirmationMessage);
        } catch (WebClientException wce) {
            MessageSourceResolvable errorMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareConfig.enableCommandFailed",
                                                 wce.getMessage());
            flashScope.setError(errorMessage);
        }

        return "redirect:/spring/stars/operator/hardware/config/edit";
    }

    private void verifyHardwareIsForAccount(int inventoryId,
            AccountInfoFragment accountInfo) {
        LiteInventoryBase inventory = starsInventoryBaseDao.getByInventoryId(inventoryId);
        if (inventory.getAccountID() != accountInfo.getAccountId()) {
            throw new NotAuthorizedException("The device " + inventoryId +
                                             " does not belong to account " +
                                             accountInfo.getAccountId());
        }
    }

    @RequestMapping
    public String meterConfig(ModelMap model, int meterId,
                              YukonUserContext userContext,
                              AccountInfoFragment accountInfoFragment) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);

        setPageMode(model, userContext);

        Meter meter = meterDao.getForId(meterId);
        model.addAttribute("meter", meter);
        model.addAttribute("displayName", meter.getName());

        List<LiteYukonPAObject> routes = Lists.newArrayList(paoDao.getRoutesByType(new int[]{RouteTypes.ROUTE_CCU,RouteTypes.ROUTE_MACRO}));
        model.addAttribute("routes", routes);

        return "operator/hardware/config/meterConfig.jsp";
    }

    @RequestMapping
    public String updateMeterConfig(@ModelAttribute("meter") Meter meter, BindingResult bindingResult,
                                 ModelMap model,
                                 YukonUserContext userContext,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 AccountInfoFragment accountInfoFragment) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);

        meterConfigValidator.validate(meter, bindingResult);

        if(!bindingResult.hasErrors()) {
            meterDao.update(meter);
        } else {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            List<LiteYukonPAObject> routes = Lists.newArrayList(paoDao.getRoutesByType(new int[]{RouteTypes.ROUTE_CCU,RouteTypes.ROUTE_MACRO}));
            model.addAttribute("routes", routes);
            return "operator/hardware/config/meterConfig.jsp";
        }

        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareConfig.meterConfigUpdated"));
        return "redirect:/spring/stars/operator/hardware/hardwareList";
    }

    @RequestMapping(params="cancel")
    public String cancel(ModelMap modelMap,
                          AccountInfoFragment accountInfoFragment,
                          YukonUserContext userContext,
                          HttpSession session) {

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:/spring/stars/operator/hardware/hardwareList";
    }

    private void setPageMode(ModelMap modelMap, YukonUserContext userContext) {
           boolean allowAccountEditing =
               rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                             userContext.getYukonUser());
           modelMap.addAttribute("mode", allowAccountEditing ? PageEditMode.EDIT : PageEditMode.VIEW);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver =
                new YukonMessageCodeResolver("yukon.web.modules.operator.hardwareConfig.");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
    }

    @Autowired
    public void setHardwareEventLogService(HardwareEventLogService hardwareEventLogService) {
        this.hardwareEventLogService = hardwareEventLogService;
    }
    
    @Autowired
    public void setDisplayableInventoryEnrollmentDao(
            DisplayableInventoryEnrollmentDao displayableInventoryEnrollmentDao) {
        this.displayableInventoryEnrollmentDao = displayableInventoryEnrollmentDao;
    }

    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Autowired
    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
    }

    @Autowired
    public void setAssignedProgramDao(AssignedProgramDao assignedProgramDao) {
        this.assignedProgramDao = assignedProgramDao;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
        this.enrollmentDao = enrollmentDao;
    }

    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }

    @Autowired
    public void setHardwareConfigService(HardwareConfigService hardwareConfigService) {
        this.hardwareConfigService = hardwareConfigService;
    }

    @Autowired
    public void setApplianceCategoryDao(ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }

    @Autowired
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Autowired
    public void setMeterConfigValidator(MeterConfigValidator meterConfigValidator) {
        this.meterConfigValidator = meterConfigValidator;
    }
    
    @Autowired
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }
}