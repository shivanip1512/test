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
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.service.CBCService;
import com.cannontech.web.capcontrol.validators.StrategyValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.CBCSelectionLists;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CBCController {

    @Autowired private PaoDao paoDao;
    @Autowired private StrategyValidator validator;
    @Autowired private PointDao pointDao;
    @Autowired private CBCService cbcService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private static final String baseKey = "yukon.web.modules.capcontrol.cbc.edit";

    @RequestMapping(value = "cbc/{id}/edit", method = RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String edit(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
        CapControlCBC capControlCBC = cbcService.getCapControlCBC(id);
        Map<PointType, List<PointInfo>> points = pointDao.getAllPointNamesAndTypesForPAObject(id);
        model.addAttribute("mode", PageEditMode.EDIT);

        return setUpModel(model, capControlCBC, points);
    }

    private String setUpModel(ModelMap model, CapControlCBC capControlCBC, Map<PointType, List<PointInfo>> points) {
        Object modelCapControlCBC = model.get("capControlCBC");
        if (modelCapControlCBC instanceof CapControlCBC) {
            capControlCBC = (CapControlCBC) modelCapControlCBC;
        }

        CBCSelectionLists lists = new CBCSelectionLists();

        model.addAttribute("capControlCBC", capControlCBC);
        model.addAttribute("selectionLists", lists);
        model.addAttribute("timeIntervals", CBCSelectionLists.TIME_INTERVAL);
        model.addAttribute("scanGroups", CBCSelectionLists.SCAN_GROUP);
        model.addAttribute("analogPoints", points.get(PointType.Analog));
        model.addAttribute("pulseAccumulatorPoints", points.get(PointType.PulseAccumulator));
        model.addAttribute("calcAnalogPoints", points.get(PointType.CalcAnalog));
        model.addAttribute("statusPoints", points.get(PointType.Status));
        model.addAttribute("calcStatusPoints", points.get(PointType.CalcStatus));

        return "cbc.jsp";
    }

    @RequestMapping(value = { "cbc" }, method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String save(@ModelAttribute("capControlCBC") CapControlCBC capControlCBC, FlashScope flashScope,
            YukonUserContext userContext) {
        try {
            cbcService.save(capControlCBC);
        } catch (SerialNumberExistsException | PortDoesntExistException | MultipleDevicesOnPortException
            | SameMasterSlaveCombinationException | SQLException e) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".saveFailed", e.getMessage()));
        }

        return "redirect:cbc/" + capControlCBC.getYukonPAObject().getPaObjectID() + "/edit";
    }

}