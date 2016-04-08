package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.service.CbcService;
import com.cannontech.web.capcontrol.validators.CbcValidator;
import com.cannontech.web.common.TimeIntervals;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableMap;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CbcController {

    @Autowired private CbcValidator cbcValidator;
    @Autowired private PointDao pointDao;
    @Autowired private CbcService cbcService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DeviceConfigurationDao deviceConfigDao;
    @Autowired private AttributeService attributeService;
    @Autowired private CapbankDao capbankDao;
    @Autowired private CapControlCache ccCache;


    private static final String baseKey = "yukon.web.modules.capcontrol.cbc";
    
    private static final Map<BuiltInAttribute,String> formatMappings = ImmutableMap.<BuiltInAttribute,String>builder()
            .put(BuiltInAttribute.FIRMWARE_VERSION, "{rawValue|firmwareVersion}")
            .put(BuiltInAttribute.IP_ADDRESS, "{rawValue|ipAddress}")
            .put(BuiltInAttribute.NEUTRAL_CURRENT_SENSOR, "{rawValue|neutralCurrent}")
            .put(BuiltInAttribute.SERIAL_NUMBER, "{rawValue|long}")
            .put(BuiltInAttribute.UDP_PORT, "{rawValue|long}")
            .put(BuiltInAttribute.LAST_CONTROL_REASON, "{rawValue|lastControlReason}")
            .put(BuiltInAttribute.IGNORED_CONTROL_REASON, "{rawValue|ignoredControlReason}")
            .build();

    @RequestMapping(value = "cbc/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
        CapControlCBC cbc = cbcService.getCbc(id);
        model.addAttribute("mode", PageEditMode.VIEW);
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
            RedirectAttributes redirectAttributes) {

        cbcValidator.validate(cbc, result);

        if (result.hasErrors()) {
            return bindAndForward(cbc, result, redirectAttributes);
        }
        cbcService.save(cbc);

        return "redirect:/capcontrol/cbc/" + cbc.getId();
    }

    @RequestMapping(value="cbc/{id}/copy", method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String copy(
            FlashScope flash,
            @PathVariable int id,
            String newName,
            boolean copyPoints) {
        try {
            int newId = cbcService.copy(id, newName, copyPoints);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".copy.success"));
            return "redirect:/capcontrol/cbc/" + newId;
        } catch (IllegalArgumentException e) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".copy.fail"));
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

        model.addAttribute("paoTypes", PaoType.getCbcTypes());
        model.addAttribute("timeIntervals", TimeIntervals.getCapControlIntervals());
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
                    Area area = ccCache.getArea(areaId);

                    model.addAttribute("areaId", area.getCcId());
                    model.addAttribute("areaName", area.getCcName());
                    
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
            //check for special formats
            for(List<PointInfo> pointList : points.values()){
                for(PointInfo point : pointList){
                    LitePoint litePoint = pointDao.getLitePoint(point.getPointId());
                    PointIdentifier pid = new PointIdentifier(point.getType(), litePoint.getPointOffset());
                    PaoTypePointIdentifier pptId = PaoTypePointIdentifier.of(cbc.getPaoType(), pid);
                    //This set should contain 0 items if there is not a special format, or 1 if there is
                    Set<BuiltInAttribute> attributes = attributeService.findAttributesForPoint(pptId, formatMappings.keySet());
                    for (BuiltInAttribute attribute: attributes) {
                        if (formatMappings.get(attribute) != null) {
                            point.setFormat(formatMappings.get(attribute));
                        }
                    }
                }
            }
            model.addAttribute("points", points);
        }

        List<LiteYukonPAObject> routes = new ArrayList<>(dbCache.getAllRoutes());
        String noneOption = accessor.getMessage("yukon.common.none.choice");
        LiteYukonPAObject none = new LiteYukonPAObject(0, noneOption, null, null, null);
        routes.add(0, none);
        model.addAttribute("availableRoutes", routes);
        if (cbc.isTwoWay()) {
            DNPConfiguration dnpConfig = cbcService.getDnpConfigForDevice(cbc);
            model.addAttribute("dnpConfig", dnpConfig);
        }
        model.addAttribute("twoWayTypes", CapControlCBC.getTwoWayTypes());

        Set<Integer> tcpPorts = dbCache.getAllPorts().stream()
            .filter(new Predicate<LiteYukonPAObject> () {

                @Override
                public boolean test(LiteYukonPAObject port) {
                    return port.getPaoType() == PaoType.TCPPORT;
                }

            }).map(new Function<LiteYukonPAObject, Integer>(){

                @Override
                public Integer apply(LiteYukonPAObject port) {
                    return port.getLiteID();
                }

            }).collect(Collectors.toSet());

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

        return "redirect:cbc/" + cbc.getId();
    }

}