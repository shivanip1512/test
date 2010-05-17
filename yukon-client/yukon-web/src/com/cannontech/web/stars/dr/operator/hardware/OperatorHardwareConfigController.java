package com.cannontech.web.stars.dr.operator.hardware;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.dr.loadgroup.dao.LoadGroupDao;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventoryEnrollment;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardwareConfig.service.HardwareConfigService;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.validator.MeterConfigValidator;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
@RequestMapping("/operator/hardware/config/*")
public class OperatorHardwareConfigController {
    private DisplayableInventoryEnrollmentDao displayableInventoryEnrollmentDao;
    private InventoryDao inventoryDao;
    private LoadGroupDao loadGroupDao;
    private AssignedProgramDao assignedProgramDao;
    private RolePropertyDao rolePropertyDao;
    private EnrollmentDao enrollmentDao;
    private EnrollmentHelperService enrollmentHelperService;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private HardwareConfigService hardwareConfigService;
    private ApplianceCategoryDao applianceCategoryDao;
    private MeterDao meterDao;
    private MeterConfigValidator meterConfigValidator;
    private PaoDao paoDao;

    @RequestMapping
    public String list(ModelMap model, int inventoryId,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);

        populateModel(model, inventoryId, userContext);

        List<DisplayableInventoryEnrollment> enrollments =
            displayableInventoryEnrollmentDao.find(accountInfoFragment.getAccountId(),
                                                   inventoryId);
        model.addAttribute("enrollments", enrollments);

        Set<Integer> assignedProgramIds = Sets.newHashSet();
        for (DisplayableInventoryEnrollment enrollment : enrollments) {
            assignedProgramIds.add(enrollment.getAssignedProgramId());
        }
        List<AssignedProgram> assignedProgramsList =
            assignedProgramDao.getByIds(assignedProgramIds);
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

        return "operator/hardware/config/list.jsp";
    }

    @RequestMapping
    public String edit(ModelMap model, int inventoryId, int assignedProgramId,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        populateModel(model, inventoryId, userContext);

        AssignedProgram assignedProgram =
            assignedProgramDao.getById(assignedProgramId);

        DisplayableInventoryEnrollment enrollment =
            displayableInventoryEnrollmentDao.find(accountInfoFragment.getAccountId(),
                                                   inventoryId, assignedProgramId);
        if (enrollment == null) {
            enrollment =
                new DisplayableInventoryEnrollment(assignedProgramId,
                                                   assignedProgram.getProgramId(),
                                                   assignedProgram.getName());
        }
        model.addAttribute("enrollment", enrollment);

        List<ControllablePao> loadGroups =
            loadGroupDao.getForProgram(enrollment.getProgramId());
        model.addAttribute("loadGroups", loadGroups);

        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);

        return "operator/hardware/config/edit.jsp";
    }
    
    @RequestMapping
    public String enroll(ModelMap model, HttpServletRequest request,
            String action, int inventoryId, int assignedProgramId,
            int loadGroupId, int relay, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment, FlashScope flashScope) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);

        StarsUpdateLMHardwareConfig hardwareConfig = new StarsUpdateLMHardwareConfig();
        hardwareConfig.setInventoryID(inventoryId);
        hardwareConfig.setSaveToBatch("saveToBatch".equals(action));
        hardwareConfig.setSaveConfigOnly("saveConfigOnly".equals(action));

        StarsLMHardwareConfig config = new StarsLMHardwareConfig();
        config.setGroupID(loadGroupId);
        config.setProgramID(assignedProgramId);
        config.setLoadNumber(relay);
        hardwareConfig.addStarsLMHardwareConfig(config);

        // We need to add all other currently enrolled programs for this device
        // or they will get unenrolled.
        List<ProgramEnrollment> currentEnrollments =
            enrollmentDao.getActiveEnrollmentsByInventoryId(accountInfoFragment.getAccountId(),
                                                            inventoryId);
        for (ProgramEnrollment currentEnrollment : currentEnrollments) {
            if (currentEnrollment.getAssignedProgramId() != assignedProgramId) {
                config = new StarsLMHardwareConfig();
                config.setGroupID(currentEnrollment.getLmGroupId());
                config.setProgramID(currentEnrollment.getAssignedProgramId());
                config.setLoadNumber(currentEnrollment.getRelay());
                hardwareConfig.addStarsLMHardwareConfig(config);
            }
        }

        try {
            /*
             TODO:
            if (request.getParameter("UseHardwareAddressing") != null) {
                StarsLMConfiguration lmConfig = new StarsLMConfiguration();
                InventoryManagerUtil.setStarsLMConfiguration(lmConfig, request);
                hardwareConfig.setStarsLMConfiguration(lmConfig);
            }
            */
            LiteStarsLMHardware liteHw = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(inventoryId);
            LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(accountInfoFragment.getEnergyCompanyId());
            UpdateLMHardwareConfigAction.updateLMHardwareConfig(hardwareConfig,
                liteHw, userContext.getYukonUser().getUserID(), energyCompany);
        } catch (WebClientException wce) {
            MessageSourceResolvable errorMessage = YukonMessageSourceResolvable.createDefaultWithoutCode(wce.getMessage());
            flashScope.setError(errorMessage);

            return closeDialog(model);
        }

        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        AssignedProgram assignedProgram = assignedProgramDao.getById(assignedProgramId);
        MessageSourceResolvable confirmationMessage =
            new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareConfig.inventoryEnrolled",
                                             hardware.getDisplayName(), assignedProgram.getDisplayName());
        flashScope.setConfirm(confirmationMessage);

        return closeDialog(model);
    }

    @RequestMapping
    public String unenroll(ModelMap model, int inventoryId,
            int assignedProgramId, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment, FlashScope flashScope) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        List<ProgramEnrollment> programEnrollments =
            enrollmentDao.getActiveEnrollmentsByInventoryId(accountInfoFragment.getAccountId(),
                                                            inventoryId);
        for (ProgramEnrollment currentEnrollment : programEnrollments) {
            if (currentEnrollment.getAssignedProgramId() == assignedProgramId) {
                currentEnrollment.setEnroll(false);
            }
        }
        enrollmentHelperService.updateProgramEnrollments(programEnrollments,
                                                         accountInfoFragment.getAccountId(),
                                                         userContext);

        model.addAttribute("inventoryId", inventoryId);
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        AssignedProgram assignedProgram = assignedProgramDao.getById(assignedProgramId);
        MessageSourceResolvable confirmationMessage =
            new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareConfig.inventoryUnenrolled",
                                             hardware.getDisplayName(), assignedProgram.getDisplayName());
        flashScope.setConfirm(confirmationMessage);
        return "redirect:/spring/stars/operator/hardware/config/list";
    }

    @RequestMapping
    public String disable(ModelMap model, int inventoryId,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment, FlashScope flashScope) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        model.addAttribute("inventoryId", inventoryId);

        try {
            hardwareConfigService.disable(inventoryId,
                                          accountInfoFragment.getAccountId(),
                                          accountInfoFragment.getEnergyCompanyId(),
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

        return "redirect:/spring/stars/operator/hardware/config/list";
    }
    
    @RequestMapping
    public String enable(ModelMap model, int inventoryId,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment, FlashScope flashScope) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        model.addAttribute("inventoryId", inventoryId);
        
        try {
            hardwareConfigService.enable(inventoryId,
                                         accountInfoFragment.getAccountId(),
                                         accountInfoFragment.getEnergyCompanyId(),
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
        
        return "redirect:/spring/stars/operator/hardware/config/list";
    }
    
    private String closeDialog(ModelMap model) {
        model.addAttribute("popupId", "hardwareConfigEditDialog");
        return "closePopup.jsp";
    }

    private void populateModel(ModelMap model, int inventoryId,
            YukonUserContext userContext) {
        model.addAttribute("inventoryId", inventoryId);

        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
        model.addAttribute("deviceLabel", hardware.getDeviceLabel());
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
           boolean allowAccountEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
           modelMap.addAttribute("mode", allowAccountEditing ? PageEditMode.EDIT : PageEditMode.VIEW);
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
    public void setEnrollmentHelperService(
            EnrollmentHelperService enrollmentHelperService) {
        this.enrollmentHelperService = enrollmentHelperService;
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
}