package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.service.CbcHelperService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.HeartbeatConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.loader.jaxb.CategoryType;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.service.CbcService;
import com.cannontech.web.capcontrol.validators.CbcValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeListType;
import com.cannontech.web.deviceConfiguration.enumeration.DnpTimeOffset.Offsets;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.rtu.service.RtuService;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CbcController {

    @Autowired private CbcValidator cbcValidator;
    @Autowired private PointDao pointDao;
    @Autowired private CbcService cbcService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DeviceConfigurationDao deviceConfigDao;
    @Autowired private CbcHelperService cbcHelperService;
    @Autowired private CapbankDao capbankDao;
    @Autowired private CapControlCache ccCache;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private RtuService rtuService;

    private static final String baseKey = "yukon.web.modules.capcontrol.cbc";

    @RequestMapping(value = "cbc/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {
        CapControlCBC cbc = cbcService.getCbc(id);
        model.addAttribute("mode", PageEditMode.VIEW);
        if (model.get("isError") != null && (boolean) model.get("isError")) {
            return setUpModel(model, cbc, userContext);
        }
        List<MessageSourceResolvable> duplicatePointMessages = rtuService.generateDuplicatePointsErrorMessages(id, request);
        flash.setError(duplicatePointMessages, FlashScopeListType.NONE);
        return setUpModel(model, cbc, userContext);
    }
    
    @RequestMapping(value = "cbc/{id}/edit", method = RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String edit(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
        CapControlCBC cbc = cbcService.getCbc(id);
        model.addAttribute("mode", PageEditMode.EDIT);
        return setUpModel(model, cbc, userContext);
    }

    @RequestMapping(value = "cbc/create", method = RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String create(ModelMap model, YukonUserContext userContext) {

        CapControlCBC cbc = new CapControlCBC();
        int defaultDnpConfig = deviceConfigDao.getDefaultDNPConfiguration().getConfigurationId();
        cbc.setDnpConfigId(defaultDnpConfig);

        model.addAttribute("mode", PageEditMode.CREATE);

        return setUpModel(model, cbc, userContext);
    }

    @RequestMapping(value="cbc", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String save(
            @ModelAttribute("cbc") CapControlCBC cbc, 
            BindingResult result, 
            RedirectAttributes redirectAttributes,
            FlashScope flash) {

        cbcValidator.validate(cbc, result);
        boolean isValidDNP = cbc.isTwoWay() ? isValidDnpConfig(cbc) : true;

        if (result.hasErrors() || !isValidDNP) {
            if (!isValidDNP){
                flash.setError(new YukonMessageSourceResolvable(baseKey + ".error.invalidConfig"));
            }
            if (cbc.isTwoWay() && cbc.getDeviceDirectCommSettings() == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + ".error.noCommChannel"));
            }
            return bindAndForward(cbc, result, redirectAttributes);
        }

        cbcService.save(cbc);

        // Success
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".info.saved"));
        return "redirect:/capcontrol/cbc/" + cbc.getId();
    }

    @RequestMapping(value="cbc/{id}/copy", method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String copy(
            FlashScope flash,
            @PathVariable int id,
            String newName,
            boolean copyPoints,
            YukonUserContext userContext, ModelMap model, RedirectAttributes redirectAttributes) {
        try {
            int newId = cbcService.copy(id, newName, copyPoints, userContext.getYukonUser());
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".copy.success"));
            return "redirect:/capcontrol/cbc/" + newId;
        } catch (IllegalArgumentException e) {
            flash.setError(new YukonMessageSourceResolvable(e.getMessage()));
            redirectAttributes.addFlashAttribute("isError", true);
            return "redirect:/capcontrol/cbc/" + id;
        }
    }

    @RequestMapping(value="cbc/{id}", method=RequestMethod.DELETE)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String delete(ModelMap model, @PathVariable int id, FlashScope flash, YukonUserContext userContext) {

        if (!cbcService.delete(id)) {
            CapControlCBC cbc = cbcService.getCbc(id);
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".delete.fail"));
            return setUpModel(model, cbc, userContext);
        }

        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".delete.success"));

        return "redirect:/capcontrol/search/searchResults?cbc_lastSearch=__cti_oCBCs__";
    }

    private String setUpModel(ModelMap model, CapControlCBC cbc, YukonUserContext userContext) {

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        Object modelCbc = model.get("cbc");
        if (modelCbc instanceof CapControlCBC) {
            cbc = (CapControlCBC) modelCbc;
        }
        model.addAttribute("cbc", cbc);
        model.addAttribute("cbcId", cbc.getId());
        model.addAttribute("cbcName", cbc.getName());

        List<PaoType> cbcTypes =
            PaoType.getCbcTypes().stream().filter(cbcType -> paoDefinitionDao.getPaoDefinition(cbcType).isCreatable()).collect(
                Collectors.toList());
            
        model.addAttribute("paoTypes", cbcTypes);
        model.addAttribute("timeIntervals", TimeIntervals.getCapControlIntervals());
        model.addAttribute("altTimeIntervals", TimeIntervals.getAnalysisIntervals());
        model.addAttribute("scanGroups", CapControlCBC.ScanGroup.values());
        model.addAttribute("availablePorts", dbCache.getAllPorts());

        if (cbc.getId() != null) {
            //parent may not be populated if validation errors were found and we are redirecting back to the page
            PaoIdentifier capbank = capbankDao.findCapBankByCbc(cbc.getId());
            if(capbank == null) {
                model.addAttribute("orphan", true);
            } else {
                LiteYukonPAObject parent = dbCache.getAllPaosMap().get(capbank.getPaoId());
                cbc.setParent(parent);
                int capbankId = parent.getLiteID();
                model.addAttribute("orphan", false);
                
                model.addAttribute("capbankId", capbankId);
                model.addAttribute("capbankName", parent.getPaoName());
                
                try {
                    SubStation substation = ccCache.getParentSubstation(capbankId);
                    model.addAttribute("substationId", substation.getCcId());
                    model.addAttribute("substationName", substation.getCcName());

                    int areaId = ccCache.getParentAreaId(capbankId);
                    LiteYukonPAObject area = dbCache.getAllPaosMap().get(areaId);

                    model.addAttribute("areaId", area.getLiteID());
                    model.addAttribute("areaName", area.getPaoName());
                    
                    int busId = ccCache.getParentSubBusId(capbankId);
                    SubBus bus = ccCache.getSubBus(busId);

                    model.addAttribute("busId", bus.getCcId());
                    model.addAttribute("busName", bus.getCcName());
                    
                    int feederId = ccCache.getParentFeederId(capbankId);
                    Feeder feeder = ccCache.getFeeder(feederId);

                    model.addAttribute("feederId", feeder.getCcId());
                    model.addAttribute("feederName", feeder.getCcName());

                } catch (NotFoundException e) {
                    model.addAttribute("orphan", true);
                }

            }

            Map<PointType, List<PointInfo>> points = pointDao.getAllPointNamesAndTypesForPAObject(cbc.getId());

            List<PointInfo> pointInfo = points.values().stream().flatMap(List::stream).collect(Collectors.toList());
            
            //check for special formats
            Map<PointInfo, String> pointFormats = 
                            cbcHelperService.getPaoTypePointFormats(cbc.getPaoType(), pointInfo, PointInfo::getPointId, PointInfo::getPointIdentifier);
            
            pointFormats.forEach((pi, format) -> pi.setFormat(format));
            
            model.addAttribute("points", points);
        }

        List<LiteYukonPAObject> routes = new ArrayList<>(dbCache.getAllRoutes());
        String noneOption = accessor.getMessage("yukon.common.none.choice");
        LiteYukonPAObject none = new LiteYukonPAObject(0, noneOption, null, null, null);
        routes.add(0, none);
        model.addAttribute("availableRoutes", routes);
        if (cbc.isTwoWay()) {
            DNPConfiguration dnpConfig = cbcService.getDnpConfigForDevice(cbc);
            if (dnpConfig == null) {
                dnpConfig = new DNPConfiguration(cbc.getDnpConfigId(), deviceConfigDao.getLightConfigurationById(cbc.getDnpConfigId()).getName(), "");
                dnpConfig.setTimeOffset(Offsets.UTC.toString());
                model.addAttribute("dnpCategoryUnassigned", true);
            }
            model.addAttribute("dnpConfig", dnpConfig);
        }
        if (cbc.isHeartBeat()) {
            HeartbeatConfiguration heartbeat = cbcService.getCBCHeartbeatConfigForDevice(cbc);
            model.addAttribute("heartbeatConfig", heartbeat);
        }
        if (paoDefinitionDao.isCategoryTypeSupportedByPaoType(cbc.getPaoType(), CategoryType.CBC_ATTRIBUTE_MAPPING)) {
            model.addAttribute("supportsAttributeMapping", true);
            DeviceConfigCategory attMapping = cbcService.getAttributeMappingForDevice(cbc);
            model.addAttribute("attributeMapping", attMapping);
        }

        List<CategoryType> requiredCategoryTypes = paoDefinitionDao.getRequiredCategoriesByPaoType(cbc.getPaoType());
        model.addAttribute("heartbeatRequired", requiredCategoryTypes.contains(CategoryType.CBC_HEARTBEAT));
        model.addAttribute("attributeRequired", requiredCategoryTypes.contains(CategoryType.CBC_ATTRIBUTE_MAPPING));
        
        model.addAttribute("twoWayTypes", CapControlCBC.getTwoWayTypes());
        model.addAttribute("logicalTypes", CapControlCBC.getLogicalTypes());

        Set<Integer> tcpPorts = dbCache.getAllPorts().stream()
            .filter(port -> port.getPaoType() == PaoType.TCPPORT)
            .map(LiteYukonPAObject::getLiteID)
            .collect(Collectors.toSet());

        model.addAttribute("tcpCommPorts", tcpPorts);

        List<LightDeviceConfiguration> configs = deviceConfigDao.getAllConfigurationsByType(cbc.getPaoType());
        model.addAttribute("configs", configs);

        return "cbc.jsp";
    }
    
    private String bindAndForward(CapControlCBC cbc, BindingResult result, RedirectAttributes attrs) {

        attrs.addFlashAttribute("cbc", cbc);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.cbc", result);

        if (cbc.getId() == null) {
            return "redirect:cbc/create";
        }

        return "redirect:cbc/" + cbc.getId() + "/edit";
    }

    private boolean isValidDnpConfig(CapControlCBC cbc) {
        LightDeviceConfiguration config = new LightDeviceConfiguration(cbc.getDnpConfigId(), null, null);
        return deviceConfigDao.isTypeSupportedByConfiguration(config, cbc.getPaoType());
    }

}