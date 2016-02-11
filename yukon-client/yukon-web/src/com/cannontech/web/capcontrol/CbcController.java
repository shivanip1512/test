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

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.service.CbcService;
import com.cannontech.web.capcontrol.validators.CbcValidator;
import com.cannontech.web.common.TimeIntervals;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CbcController {

    @Autowired private CbcValidator cbcValidator;
    @Autowired private PointDao pointDao;
    @Autowired private CbcService cbcService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DeviceConfigurationDao deviceConfigDao;

    private static final String baseKey = "yukon.web.modules.capcontrol.cbc";

    @RequestMapping(value = "cbc/{id}", method = RequestMethod.GET)
    public String edit(ModelMap model, @PathVariable int id, YukonUserContext userContext) {

        CapControlCBC cbc = cbcService.getCbc(id);

        boolean canEdit = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, userContext.getYukonUser());
        PageEditMode mode = canEdit ? PageEditMode.EDIT : PageEditMode.VIEW;
        model.addAttribute("mode", mode);

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

        model.addAttribute("paoTypes", PaoType.getCbcTypes());
        model.addAttribute("timeIntervals", TimeIntervals.getCapControlIntervals());
        model.addAttribute("scanGroups", CapControlCBC.ScanGroup.values());
        model.addAttribute("availablePorts", dbCache.getAllPorts());

        if (cbc.getId() != null) {
            Map<PointType, List<PointInfo>> points = pointDao.getAllPointNamesAndTypesForPAObject(cbc.getId());
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