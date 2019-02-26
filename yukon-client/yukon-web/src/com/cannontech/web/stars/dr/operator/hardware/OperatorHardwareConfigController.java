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
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.dao.ExpressComReportedAddressDao;
import com.cannontech.dr.dao.LmReportedAddress;
import com.cannontech.dr.dao.SepReportedAddressDao;
import com.cannontech.dr.itron.service.ItronCommunicationException;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventoryEnrollment;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentInService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.dao.StaticLoadGroupMappingDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.service.HardwareConfigService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.action.HardwareAction;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.model.AssignedHardwareConfig;
import com.cannontech.web.stars.dr.operator.hardware.model.ProgramEnrollmentDto;
import com.cannontech.web.stars.dr.operator.hardware.validator.ColdLoadPickupValidator;
import com.cannontech.web.stars.dr.operator.hardware.validator.HardwareConfigurationDtoValidator;
import com.cannontech.web.stars.dr.operator.hardware.validator.MeterConfigValidator;
import com.cannontech.web.stars.dr.operator.hardware.validator.TamperDetectValidator;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.util.SpringWebUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
@RequestMapping("/operator/hardware/config/*")
public class OperatorHardwareConfigController {
    
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private DisplayableInventoryEnrollmentDao displayableInventoryEnrollmentDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private LoadGroupDao loadGroupDao;
    @Autowired private StaticLoadGroupMappingDao staticLoadGroupMappingDao;
    @Autowired private AssignedProgramDao assignedProgramDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private HardwareConfigService hardwareConfigService;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private MeterDao meterDao;
    @Autowired private MeterConfigValidator meterConfigValidator;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private PaoDao paoDao;
    @Autowired private ExpressComReportedAddressDao expressComReportedAddressDao;
    @Autowired private SepReportedAddressDao sepReportedAddressDao;
    @Autowired private AttributeService attributeService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;
    
    private final ColdLoadPickupValidator coldLoadPickupValidator = new ColdLoadPickupValidator();
    private final TamperDetectValidator tamperDetectValidator = new TamperDetectValidator();
    
    @RequestMapping("edit")
    public String edit(ModelMap model, int inventoryId,
            YukonUserContext userContext, AccountInfoFragment accountInfo) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfo, model);
        
        verifyHardwareIsForAccount(inventoryId, accountInfo);
        
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("displayName", hardware.getDisplayName());
        HardwareType type = hardware.getHardwareType();
        
        AssignedHardwareConfig configuration = new AssignedHardwareConfig(type.getHardwareConfigType());
        configuration.setAccountId(accountInfo.getAccountId());
        configuration.setInventoryId(inventoryId);
        boolean trackHardwareAddressing =
                energyCompanySettingDao.getBoolean(EnergyCompanySettingType.TRACK_HARDWARE_ADDRESSING,
                                                   accountInfo.getEnergyCompanyId());
        
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
                    configuration.setStarsLMConfiguration(starsConfig,type); 
                    break;
                }
            }
        }
        
        model.addAttribute("showSaveOnly", trackHardwareAddressing);
        model.addAttribute("configuration", configuration);
        model.addAttribute("inventoryId", inventoryId);
        return prepareForEdit(true, model, configuration, null, userContext, accountInfo);
    }
    
    private String prepareForEdit(boolean initConfig, ModelMap model,
            @ModelAttribute("configuration") AssignedHardwareConfig configuration,
            BindingResult bindingResult, YukonUserContext userContext,
            AccountInfoFragment accountInfo) {
        int inventoryId = configuration.getInventoryId();
        
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
                
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
        
        String batchedSwitchCommandToggle =
            globalSettingDao.getString(GlobalSettingType.BATCHED_SWITCH_COMMAND_TOGGLE);
        boolean useStaticLoadGroups =
            StarsUtils.BATCH_SWITCH_COMMAND_MANUAL.equals(batchedSwitchCommandToggle)
                && VersionTools.staticLoadGroupMappingExists();
        
        Map<Integer, List<LoadGroup>> loadGroupsByProgramId = Maps.newHashMap();
        model.addAttribute("loadGroupsByProgramId", loadGroupsByProgramId);
        List<ProgramEnrollmentDto> programEnrollments =
            Lists.newArrayListWithCapacity(enrollments.size());
        Map<Integer, AssignedProgram> assignedPrograms = Maps.newHashMap();
        for (DisplayableInventoryEnrollment enrollment : enrollments) {
            AssignedProgram assignedProgram =
                assignedProgramDao.getById(enrollment.getAssignedProgramId());
            assignedPrograms.put(assignedProgram.getAssignedProgramId(), assignedProgram);
            List<LoadGroup> loadGroups = null;
            if (useStaticLoadGroups) {
                List<Integer> loadGroupIds =
                    staticLoadGroupMappingDao.getLoadGroupIdsForApplianceCategory(assignedProgram.getApplianceCategoryId());
                loadGroups = loadGroupDao.getByIds(loadGroupIds);
            } else {
                loadGroups = loadGroupDao.getByProgramId(enrollment.getProgramId());
            }
            
            loadGroupsByProgramId.put(enrollment.getAssignedProgramId(), loadGroups);
            
            ProgramEnrollmentDto programEnrollment = new ProgramEnrollmentDto();
            programEnrollment.setAssignedProgramId(enrollment.getAssignedProgramId());
            programEnrollment.setLoadGroupId(enrollment.getLoadGroupId());
            programEnrollment.setRelay(enrollment.getRelay());
            programEnrollments.add(programEnrollment);
        }
        model.addAttribute("assignedPrograms", assignedPrograms);
        if (initConfig) {
            configuration.setProgramEnrollments(programEnrollments);
        }
        
        Set<Integer> applianceCategoryIds = Sets.newHashSet();
        for (AssignedProgram assignedProgram : assignedPrograms.values()) {
            applianceCategoryIds.add(assignedProgram.getApplianceCategoryId());
        }
        
        Map<Integer, ApplianceCategory> applianceCategories =
            applianceCategoryDao.getByApplianceCategoryIds(applianceCategoryIds);
        model.addAttribute("applianceCategories", applianceCategories);
        
        InventoryIdentifier inventory = inventoryDao.getYukonInventory(inventoryId);
        
        EnrollmentInService inService = EnrollmentInService.NA;
        if (!inventory.getHardwareType().isZigbee()) {
            inService = EnrollmentInService.determineInService(enrollmentDao.isInService(inventoryId));
        }
        model.addAttribute("inService", inService);
        model.addAttribute("canEnableDisable", !inventory.getHardwareType().isZigbee() 
                                            && !inventory.getHardwareType().isEcobee()
                                            && !inventory.getHardwareType().isHoneywell()
                                            && !inventory.getHardwareType().isNest());
        model.addAttribute("hideHardareAddressing", inventory.getHardwareType().isHideHardwareAddressing());
        model.addAttribute("isZigbee", inventory.getHardwareType().isZigbee());
        int deviceId = inventoryDao.getDeviceId(inventoryId);
        
        boolean supportsAddressReporting = inventory.getHardwareType().isRf() || inventory.getHardwareType().isZigbee() 
        									|| inventory.getHardwareType().isTwoWayPlcLcr();
        boolean supportsServiceStatusReporting = inventory.getHardwareType().isRf();
        
        if (!supportsServiceStatusReporting) {
            model.addAttribute("showStaticServiceStatus", true);
        } else {
            LitePoint serviceStatusPoint = attributeService.getPointForAttribute(paoDao.getYukonPao(deviceId), BuiltInAttribute.SERVICE_STATUS);
            model.addAttribute("serviceStatusPointId", serviceStatusPoint.getPointID());
        }
        
        if (supportsAddressReporting) {
            model.addAttribute("showDeviceReportedConfig", true);
            model.addAttribute("deviceId", deviceId);
            
            try { //TODO replace this with a smart retriever similar to star lm hardware row mapper 
                LmReportedAddress address = null;
                if (inventory.getHardwareType().getHardwareConfigType() == HardwareConfigType.EXPRESSCOM) {
                    address = expressComReportedAddressDao.getCurrentAddress(deviceId);
                } else {
                    address = sepReportedAddressDao.getCurrentAddress(deviceId);
                }
                model.addAttribute("reportedConfig", address);
            } catch (NotFoundException e) {/* Ignore */}
        }
        
        YukonEnergyCompany yukonEnergyCompany = 
                ecDao.getEnergyCompanyByAccountId(accountInfo.getAccountId());
        model.addAttribute("energyCompanyId", yukonEnergyCompany.getEnergyCompanyId());
        return "operator/hardware/config/edit.jsp";
    }
    
    @RequestMapping("commit")
    public String commit(ModelMap model, HttpServletRequest request,
            int inventoryId, YukonUserContext userContext,
            AccountInfoFragment accountInfo, FlashScope flashScope) {
        
        // Log hardware configuration attempt
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.hardwareConfigAttempted(userContext.getYukonUser(),
                                                        lmHardwareBase.getManufacturerSerialNumber(),
                                                        accountInfo.getAccountNumber(),
                                                        EventSource.OPERATOR);
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());
        
        // Manually bind the configuration so we can create the correct type
        // based on hardwareConfigType.
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        HardwareConfigType configType = hardware.getHardwareType().getHardwareConfigType();
        AssignedHardwareConfig configuration = new AssignedHardwareConfig(configType);
        
        BindingResult result = SpringWebUtil.bind(model, request, configuration, "configuration", 
                "yukon.web.modules.operator.hardwareConfig.");
        
        verifyHardwareIsForAccount(inventoryId, accountInfo);
        
        String action = configuration.getAction();
        StarsUpdateLMHardwareConfig hardwareConfig = new StarsUpdateLMHardwareConfig();
        hardwareConfig.setInventoryID(inventoryId);
        hardwareConfig.setSaveToBatch("saveToBatch".equals(action));
        hardwareConfig.setSaveConfigOnly("saveConfigOnly".equals(action));
        
        boolean trackHardwareAddressing =
                energyCompanySettingDao.getBoolean(EnergyCompanySettingType.TRACK_HARDWARE_ADDRESSING,
                                                   accountInfo.getEnergyCompanyId());
        
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
                validator.validate(configuration, result);
                coldLoadPickupValidator.validate(configuration, result);
                if (configType.isHasTamperDetect()) {
                    tamperDetectValidator.validate(configuration, result);
                }
                if (result.hasErrors()) {
                    List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
                    flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
                    return prepareForEdit(false, model, configuration,
                                            result, userContext, accountInfo);
                }
                hardwareConfig.setStarsLMConfiguration(configuration.getStarsLMConfiguration());
            }
            LiteLmHardwareBase liteHw = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);
            LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(accountInfo.getEnergyCompanyId());
            HardwareAction.updateLMHardwareConfig(hardwareConfig, liteHw, userContext.getYukonUser().getUserID(), energyCompany);
            
            MessageSourceResolvable confirmationMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareConfig." +
                                                 action + "Complete",
                                                 hardware.getDisplayName());
            flashScope.setConfirm(confirmationMessage);
        } catch (ItronCommunicationException e) {
          flashScope.setError(e.getItronMessage());  
        } catch (CommandCompletionException wce) {
            MessageSourceResolvable errorMessage = YukonMessageSourceResolvable.createDefaultWithoutCode(wce.getMessage());
            flashScope.setError(errorMessage);
        }
        
        return "redirect:/stars/operator/hardware/config/edit?accountId=" +
            accountInfo.getAccountId() + "&inventoryId=" + inventoryId;
    }
    
    @RequestMapping("disable")
    public String disable(ModelMap model, int inventoryId,
            YukonUserContext userContext,
            AccountInfoFragment accountInfo, FlashScope flashScope) {
        
        // Log hardware disable attempt
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.hardwareDisableAttempted(userContext.getYukonUser(),
                                                         lmHardwareBase.getManufacturerSerialNumber(),
                                                         accountInfo.getAccountNumber(),
                                                         EventSource.OPERATOR);
        
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
        } catch (ItronCommunicationException e) {
            flashScope.setError(e.getItronMessage());
        } catch (CommandCompletionException e) {
            MessageSourceResolvable errorMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareConfig.disableCommandFailed", e.getMessage());
            flashScope.setError(errorMessage);
        }
        
        return "redirect:/stars/operator/hardware/config/edit";
    }
    
    @RequestMapping("enable")
    public String enable(ModelMap model, int inventoryId,
            YukonUserContext userContext,
            AccountInfoFragment accountInfo, FlashScope flashScope) {
        
        // Log hardware disable attempt
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.hardwareEnableAttempted(userContext.getYukonUser(),
                                                        lmHardwareBase.getManufacturerSerialNumber(),
                                                        accountInfo.getAccountNumber(),
                                                        EventSource.OPERATOR);
        
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
        } catch (ItronCommunicationException e) {
            flashScope.setError(e.getItronMessage());
        } catch (CommandCompletionException e) {
            MessageSourceResolvable errorMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareConfig.enableCommandFailed",
                                                 e.getMessage());
            flashScope.setError(errorMessage);
        }
        
        return "redirect:/stars/operator/hardware/config/edit";
    }
    
    private void verifyHardwareIsForAccount(int inventoryId,
            AccountInfoFragment accountInfo) {
        LiteInventoryBase inventory = inventoryBaseDao.getByInventoryId(inventoryId);
        if (inventory.getAccountID() != accountInfo.getAccountId()) {
            throw new NotAuthorizedException("The device " + inventoryId +
                                             " does not belong to account " +
                                             accountInfo.getAccountId());
        }
    }
    
    @RequestMapping("meterConfig")
    public String meterConfig(ModelMap model, int meterId,
                              YukonUserContext userContext,
                              AccountInfoFragment accountInfoFragment) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        
        setPageMode(model, userContext);
        
        // The operator pages appear to only be able to support PLC (not RFN) meters.
        PlcMeter meter = meterDao.getPlcMeterForId(meterId);
        model.addAttribute("meter", meter);
        model.addAttribute("displayName", meter.getName());
        
        List<LiteYukonPAObject> routes = Lists.newArrayList(paoDao.getRoutesByType(new PaoType[]{PaoType.ROUTE_CCU, PaoType.ROUTE_MACRO}));
        model.addAttribute("routes", routes);
        
        return "operator/hardware/config/meterConfig.jsp";
    }
    
    @RequestMapping("updateMeterConfig")
    public String updateMeterConfig(@ModelAttribute("meter") PlcMeter meter, BindingResult bindingResult,
                                 ModelMap model,
                                 YukonUserContext userContext,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        
        int deviceTypeId = ServletRequestUtils.getRequiredIntParameter(request, "deviceTypeId");
        int paoId = ServletRequestUtils.getRequiredIntParameter(request, "paoId");
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, PaoType.getForId(deviceTypeId));
        meter.setPaoIdentifier(paoIdentifier);
        
        meterConfigValidator.validate(meter, bindingResult);
        
        if(!bindingResult.hasErrors()) {
            meterDao.update(meter);
        } else {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            List<LiteYukonPAObject> routes = Lists.newArrayList(paoDao.getRoutesByType(new PaoType[]{PaoType.ROUTE_CCU, PaoType.ROUTE_MACRO}));
            model.addAttribute("routes", routes);
            return "operator/hardware/config/meterConfig.jsp";
        }
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareConfig.meterConfigUpdated"));
        return "redirect:/stars/operator/hardware/list";
    }
    
    @RequestMapping(value="cancel", params="cancel")
    public String cancel(ModelMap modelMap,
                          AccountInfoFragment accountInfoFragment,
                          YukonUserContext userContext,
                          HttpSession session) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:/stars/operator/hardware/list";
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
    
}