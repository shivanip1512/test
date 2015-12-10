package com.cannontech.web.capcontrol;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.cbc.exceptions.MultipleDevicesOnPortException;
import com.cannontech.cbc.exceptions.PortDoesntExistException;
import com.cannontech.cbc.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.cbc.exceptions.SerialNumberExistsException;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.core.dao.PaoDao;
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
import com.cannontech.web.capcontrol.validators.StrategyValidator;
import com.cannontech.web.common.TimeIntervals;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CbcController {

    @Autowired private PaoDao paoDao;
    @Autowired private StrategyValidator validator;
    @Autowired private PointDao pointDao;
    @Autowired private CbcService cbcService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DeviceConfigurationDao deviceConfigDao;

    private static final String baseKey = "yukon.web.modules.capcontrol.cbc";

    @RequestMapping(value = "cbc/{id}", method = RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String edit(ModelMap model, @PathVariable int id, YukonUserContext userContext) {

        CapControlCBC cbc = cbcService.getCbc(id);

        boolean canEdit = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, userContext.getYukonUser());
        PageEditMode mode = canEdit ? PageEditMode.EDIT : PageEditMode.VIEW;
        model.addAttribute("mode", mode);

        return setUpModel(model, cbc);
    }

    @RequestMapping(value="cbc", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String save(@ModelAttribute("cbc") CapControlCBC cbc, FlashScope flashScope) {
        try {
            cbcService.save(cbc);
        } catch (SerialNumberExistsException | PortDoesntExistException | MultipleDevicesOnPortException
            | SameMasterSlaveCombinationException | SQLException e) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".save.fail", e.getMessage()));
        }

        return "redirect:/capcontrol/cbc/" + cbc.getId() + "/edit";
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
            return "redirect:/capcontrol/cbc/" + newId + "/edit";
        } catch (IllegalArgumentException e) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".copy.fail"));
            return "redirect:/capcontrol/cbc/" + id + "/edit";
        }
    }

    @RequestMapping(value="cbc/{id}", method=RequestMethod.DELETE)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String delete(ModelMap model, @PathVariable int id, FlashScope flash) {

        if (!cbcService.delete(id)) {
            CapControlCBC cbc = cbcService.getCbc(id);
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".delete.fail"));
            return setUpModel(model, cbc);
        }

        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".delete.success"));

        return "redirect:/capcontrol/search/searchResults?cbc_lastSearch=__cti_oCBCs__";
    }

    private String setUpModel(ModelMap model, CapControlCBC cbc) {
        Object modelCbc = model.get("cbc");
        if (modelCbc instanceof CapControlCBC) {
            cbc = (CapControlCBC) modelCbc;
        }
        Map<PointType, List<PointInfo>> points = pointDao.getAllPointNamesAndTypesForPAObject(cbc.getId());

        List<LiteYukonPAObject> ports = dbCache.getAllPorts();
        model.addAttribute("availablePorts", ports);
        List<LiteYukonPAObject> routes = dbCache.getAllRoutes();
        model.addAttribute("availableRoutes", routes);
        model.addAttribute("cbc", cbc);
        model.addAttribute("timeIntervals", TimeIntervals.getCapControlIntervals());
        model.addAttribute("analogPoints", points.get(PointType.Analog));
        model.addAttribute("pulseAccumulatorPoints", points.get(PointType.PulseAccumulator));
        model.addAttribute("calcAnalogPoints", points.get(PointType.CalcAnalog));
        model.addAttribute("statusPoints", points.get(PointType.Status));
        model.addAttribute("calcStatusPoints", points.get(PointType.CalcStatus));
        model.addAttribute("scanGroups", CapControlCBC.ScanGroup.values());

        DNPConfiguration dnpConfig = cbcService.getDnpConfigForDevice(cbc);
        model.addAttribute("dnpConfig", dnpConfig);

        List<LightDeviceConfiguration> configs = deviceConfigDao.getAllConfigurationsByType(cbc.getPaoType());
        model.addAttribute("configs", configs);

        return "cbc.jsp";
    }

}