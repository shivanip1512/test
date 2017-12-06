package com.cannontech.web.tools.points;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.common.bulk.model.AnalogPointUpdateType;
import com.cannontech.common.bulk.model.StatusPointUpdateType;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogControlType;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.data.point.SystemPoint;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointAlarming.AlarmNotificationTypes;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.database.db.point.fdr.FDRTranslation;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.TimeIntervals;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;
import com.cannontech.web.editor.point.StaleData;
import com.cannontech.web.tools.points.model.PointModel;
import com.cannontech.web.tools.points.service.PointEditorService;
import com.cannontech.web.tools.points.service.PointEditorService.AttachedException;
import com.cannontech.web.tools.points.validators.PointValidator;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableList;

@Controller
public class PointController {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDetailUrlHelper paoDetailUrlHelper;
    @Autowired private PointEditorService pointEditorService;
    @Autowired private PointValidator pointValidator;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private AlarmCatDao alarmCatDao;
    @Autowired private UnitMeasureDao unitMeasureDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private YukonListDao listDao;
    
    private static final String baseKey = "yukon.web.modules.tools.point";

    @RequestMapping(value = "/points/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, FlashScope flashScope, @PathVariable int id, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.VIEW);
        return retrievePointAndModel(model, userContext, flashScope, id);
    }
    
    private String retrievePointAndModel(ModelMap model, YukonUserContext userContext, FlashScope flashScope, int id){
        PointModel pointModel = null;
        try {
            pointModel = pointEditorService.getModelForId(id);
        } catch (NotFoundException e){
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".notFoundError", id));
            return "point/point.jsp";
        }

        PointBase base = pointModel.getPointBase();
        if (base instanceof SystemPoint){
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".viewError", base.getPoint().getPointName()));
            return "point/point.jsp";
        } else {
            if (pointModel.getPointBase().getPoint().getPseudoFlag().equals(Point.PSEUDOFLAG_PSEUDO)) {
                pointModel.getPointBase().getPoint().setPhysicalOffset(false);
            }
            return setUpModel(model, pointModel, userContext);
        }
    }

    @RequestMapping(value = "/points/{id}/edit", method = RequestMethod.GET)
    public String edit(ModelMap model, FlashScope flashScope, @PathVariable int id, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.EDIT);
        return retrievePointAndModel(model, userContext, flashScope, id);
    }

    @RequestMapping("/points/{type}/create")
    public String create(@PathVariable String type, @RequestParam int parentId, YukonUserContext userContext) {

        verifyRoles(userContext.getYukonUser());

        int pointType = PointTypes.getType(type);

        int newId = pointEditorService.create(pointType, parentId, userContext);

        return "redirect:/tools/points/" + newId + "/edit";
    }

    private String setUpModel(ModelMap model, PointModel pointModel, YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        String noneChoice = messageAccessor.getMessage("yukon.common.none.choice");

        Object modelPointBase = model.get("pointModel");
        if (modelPointBase instanceof PointModel) {
            pointModel = (PointModel) modelPointBase;
        }
        
        model.addAttribute("pointModel", pointModel);

        LiteYukonPAObject parent = dbCache.getAllPaosMap().get(pointModel.getPointBase().getPoint().getPaoID());
        model.addAttribute("parentName", StringEscapeUtils.escapeXml10(parent.getPaoName()));
        model.addAttribute("parentLink", paoDetailUrlHelper.getUrlForPaoDetailPage(parent));
        
        FdrInterfaceType[] interfaceTypes = FdrInterfaceType.values();
        Arrays.sort(interfaceTypes, FdrInterfaceType.alphabeticalComparator);

        PointBase base = pointModel.getPointBase();

        model.addAttribute("isScalarType", base instanceof ScalarPoint);
        model.addAttribute("isStatusType", base instanceof StatusPoint);
        boolean isCalcType = base instanceof CalcStatusPoint || base instanceof CalculatedPoint;
        model.addAttribute("isCalcType", isCalcType);
        model.addAttribute("isCalcStatusPoint", base instanceof CalcStatusPoint);
        model.addAttribute("isStatusPoint", base instanceof StatusPoint && !(base instanceof CalcStatusPoint));
        model.addAttribute("isAnalogPoint", base instanceof AnalogPoint);
        model.addAttribute("isAccumulatorPoint", base instanceof AccumulatorPoint);
        model.addAttribute("isSystemPoint", base instanceof SystemPoint);

        model.addAttribute("logicalGroups", PointLogicalGroups.values());
        model.addAttribute("scalarArchiveTypes", PointArchiveType.values());
        model.addAttribute("archiveIntervals", TimeIntervals.getArchiveIntervals());
        model.addAttribute("updateRate", TimeIntervals.getUpdateRate());
        model.addAttribute("fdrTranslationNumbers", ImmutableList.of(0, 1, 2, 3, 4));
        model.addAttribute("fdrInterfaceTypes", interfaceTypes);
        model.addAttribute("fdrDirections", FdrDirection.values());
        model.addAttribute("statusControlTypes", StatusControlType.values());
        model.addAttribute("unitMeasures", unitMeasureDao.getLiteUnitMeasures());
        model.addAttribute("decimalDigits", ImmutableList.of(0, 1, 2, 3, 4, 5, 6, 7, 8));
        model.addAttribute("stateGroups", stateGroupDao.getAllStateGroups());
        model.addAttribute("initialStates", statesForGroup(pointModel.getPointBase().getPoint().getStateGroupID()));
        model.addAttribute("statusArchiveTypes", ImmutableList.of(PointArchiveType.NONE, PointArchiveType.ON_CHANGE));
        boolean calcAnalog = base instanceof CalculatedPoint && !(base instanceof CalcStatusPoint);
        model.addAttribute("isCalcAnalogPoint", calcAnalog);
        model.addAttribute("pointUpdateTypes", calcAnalog ? AnalogPointUpdateType.values() : StatusPointUpdateType.values());
        model.addAttribute("analogControlTypes", AnalogControlType.values());
        model.addAttribute("staleDataUpdateStyles", StaleData.UpdateStyle.values());
        model.addAttribute("alarmNotificationTypes", AlarmNotificationTypes.values());
        if (calcAnalog) {
            model.addAttribute("baseLines", dbCache.getAllBaselines());
        }
        List<LiteNotificationGroup> notificationGroups = new ArrayList<>();

        LiteNotificationGroup noneGroup = new LiteNotificationGroup(PointAlarming.NONE_NOTIFICATIONID, noneChoice);

        notificationGroups.add(noneGroup);
        notificationGroups.addAll(dbCache.getAllContactNotificationGroups());

        model.addAttribute("notificationGroups", notificationGroups);

        model.addAttribute("alarmCategories", alarmCatDao.getAlarmCategories());

        List<List<Map<String, Object>>> fdrProperties = new ArrayList<>();
        for (FDRTranslation fdr : base.getPointFDRList()) {

            List<Map<String, Object>> inputs =
                pointEditorService.breakIntoTranslationFields(fdr.getTranslation(), fdr.getInterfaceEnum());
            fdrProperties.add(inputs);
        }

        model.addAttribute("fdrProperties", fdrProperties);
        model.addAttribute("attachment", pointEditorService.getAttachmentStatus(base.getPoint().getPointID()));
        
        //get info needed for Calculation Tab
        if (isCalcType) {
            model.addAttribute("operators", CalcComponentTypes.CALC_OPERATIONS);
            model.addAttribute("types", CalcComponentTypes.CALC_TYPES);
            model.addAttribute("functionOperators", listDao.getYukonSelectionList(CalcComponentTypes.CALC_FUNCTION_LIST_ID));
        }

        return "point/point.jsp";
    }

    @RequestMapping("/state-group/{id}/states")
    public @ResponseBody List<LiteState> statesForGroup(@PathVariable("id") int id) {
        return stateGroupDao.getLiteStates(id);

    }

    @RequestMapping("/calculationRow/add")
    public String addCalculationRow(@RequestParam("nextIndex") int nextIndex, @RequestParam("pointId") int pointId, ModelMap model) {
        model.addAttribute("baseLines", dbCache.getAllBaselines());
        model.addAttribute("operators", CalcComponentTypes.CALC_OPERATIONS);
        model.addAttribute("types", CalcComponentTypes.CALC_TYPES);
        model.addAttribute("functionOperators", listDao.getYukonSelectionList(CalcComponentTypes.CALC_FUNCTION_LIST_ID));
        model.addAttribute("nextIndex", nextIndex);
        PointModel pointModel = pointEditorService.getModelForId(pointId);
        //check if there is a current calc component for this index (it was previously removed)
        CalcComponent comp = null;
        PointBase base = pointModel.getPointBase();
        if (base instanceof CalcStatusPoint) {
            CalcStatusPoint calcStatus = (CalcStatusPoint) base;
            if (calcStatus.getCalcComponents().size() > nextIndex) {
                comp = calcStatus.getCalcComponents().get(nextIndex);
            }
        } else if (base instanceof CalculatedPoint) {
            CalculatedPoint calcPoint = (CalculatedPoint) base;
            if (calcPoint.getCalcComponents().size() > nextIndex) {
                comp = calcPoint.getCalcComponents().get(nextIndex);
            }
        }
        //if there is currently a calc component for this index, set the point id back to 0
        if (comp != null) {
            comp.setComponentPointID(0);
        }
        model.addAttribute("pointModel", pointModel);

        return "point/calculationRow.jsp";
    }
    
    @RequestMapping("/fdr/{type}")
    public @ResponseBody Map<String, Object> fdrInterfaceInfo(@PathVariable("type") FdrInterfaceType interfaceType,
            @RequestParam("point-type") String pointType) {

        Map<String, Object> result = new HashMap<>();

        List<String> directions = pointEditorService.getDirectionsFor(interfaceType);
        result.put("directions", directions);

        List<Map<String, Object>> translationFields = pointEditorService.getTranslationFieldsFor(interfaceType, pointType);
        result.put("translations", translationFields);

        return result;
    }

    /* These classes are here to prevent type erasure of generics */
    public static class AnalogPointModel extends PointModel<AnalogPoint> {}
    public static class AccumulatorPointModel extends PointModel<AccumulatorPoint> {}
    public static class CalculatedPointModel extends PointModel<CalculatedPoint> {}
    public static class StatusPointModel extends PointModel<StatusPoint> {}
    public static class CalcStatusPointModel extends PointModel<CalcStatusPoint> {}
    public static class SystemPointModel extends PointModel<SystemPoint> {}

    @RequestMapping(value = "/points/Analog", method = RequestMethod.POST)
    public String saveAnalog(@ModelAttribute("pointModel") AnalogPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes, YukonUserContext userContext, FlashScope flash) {

        verifyRoles(userContext.getYukonUser());
        return save(pointModel, result, redirectAttributes, flash);
    }

    @RequestMapping(value = "/points/PulseAccumulator", method = RequestMethod.POST)
    public String saveAccumulator(AccumulatorPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes, YukonUserContext userContext, FlashScope flash) {

        verifyRoles(userContext.getYukonUser());
        return save(pointModel, result, redirectAttributes, flash);
    }
    
    @RequestMapping(value = "/points/DemandAccumulator", method = RequestMethod.POST)
    public String saveDemandAccumulator(AccumulatorPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes, YukonUserContext userContext, FlashScope flash) {

        verifyRoles(userContext.getYukonUser());
        return save(pointModel, result, redirectAttributes, flash);
    }

    @RequestMapping(value = "/points/CalcAnalog", method = RequestMethod.POST)
    public String saveCalcAnalog(@ModelAttribute("pointModel") CalculatedPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes, YukonUserContext userContext, FlashScope flash) {

        verifyRoles(userContext.getYukonUser());
        return save(pointModel, result, redirectAttributes, flash);
    }

    @RequestMapping(value = "/points/Status", method = RequestMethod.POST)
    public String saveStatusAnalog(@ModelAttribute("pointModel") StatusPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes, YukonUserContext userContext, FlashScope flash) {

        verifyRoles(userContext.getYukonUser());
        return save(pointModel, result, redirectAttributes, flash);
    }

    @RequestMapping(value = "/points/CalcStatus", method = RequestMethod.POST)
    public String saveCalcStatusAnalog(@ModelAttribute("pointModel") CalcStatusPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes, YukonUserContext userContext, FlashScope flash) {

        verifyRoles(userContext.getYukonUser());

        return save(pointModel, result, redirectAttributes, flash);
    }

    @RequestMapping(value = "/points/System", method = RequestMethod.POST)
    public String saveSystem(@ModelAttribute("pointModel") SystemPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes, YukonUserContext userContext, FlashScope flash) {

        verifyRoles(userContext.getYukonUser());
        return save(pointModel, result, redirectAttributes, flash);
    }

    private String save(PointModel pointModel, BindingResult result, RedirectAttributes redirectAttributes, FlashScope flash) {

        pointModel.finishSetup();

        pointValidator.validate(pointModel, result);

        if (result.hasErrors()) {
            return bindAndForward(pointModel, result, redirectAttributes);
        }
        
        int id = pointEditorService.save(pointModel);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".saveSuccess"));
        
        return "redirect:/tools/points/" + id;
    }

    private String bindAndForward(PointModel pointModel, BindingResult result, RedirectAttributes attrs) {

        attrs.addFlashAttribute("pointModel", pointModel);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.pointModel", result);

        return "redirect:/tools/points/" + pointModel.getId() + "/edit";
    }

    @RequestMapping(value = "/points/{id}", method = RequestMethod.POST)
    public String delete(@PathVariable int id, FlashScope flashScope, YukonUserContext userContext) {

        verifyRoles(userContext.getYukonUser());

        PointModel pointModel = pointEditorService.getModelForId(id);
        int paoId = pointModel.getPointBase().getPoint().getPaoID();

        try {
            pointEditorService.delete(id);

        } catch (AttachedException e) {

            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

            String reason = accessor.getMessage(e.getStatus());

            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".deleteFailed", reason));

            return "redirect:/tools/points/" + pointModel.getId() + "/edit";
        }

        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(paoId);
        String pointName = pointModel.getPointBase().getPoint().getPointName();

        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".deleteSuccess", pointName));

        return "redirect:" + paoDetailUrlHelper.getUrlForPaoDetailPage(pao);
    }

    /**
     * Checks if the user has either the DB Editor Role, or the Cap Control Editor role
     *
     * @throws NotAuthorizedException if user doesn't have required permissions
     */
    private void verifyRoles(LiteYukonUser user) throws NotAuthorizedException {
        boolean capControlEditor = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        boolean dbEditor = rolePropertyDao.checkRole(YukonRole.DATABASE_EDITOR, user);

        if (!capControlEditor && !dbEditor) {
            throw new NotAuthorizedException("User not allowed to edit points");
        }
    }
}
