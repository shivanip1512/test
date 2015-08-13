package com.cannontech.web.tools.points;

import java.util.ArrayList;
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

import com.cannontech.common.bulk.model.StatusPointUpdateType;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
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
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointAlarming.AlarmNotificationTypes;
import com.cannontech.database.db.point.fdr.FDRTranslation;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.TimeIntervals;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;
import com.cannontech.web.editor.point.StaleData;
import com.cannontech.web.security.annotation.CheckRoleProperty;
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
    @Autowired private StateDao stateDao;
    @Autowired private UnitMeasureDao unitMeasureDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private static final String baseKey = "yukon.web.modules.tools.point";

    @RequestMapping(value = "/points/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id, YukonUserContext userContext) {

        model.addAttribute("mode", PageEditMode.VIEW);
        PointModel pointModel = pointEditorService.getModelForId(id);

        return setUpModel(model, pointModel, userContext);
    }

    @RequestMapping(value = "/points/{id}/edit", method = RequestMethod.GET)
    public String edit(ModelMap model, @PathVariable int id, YukonUserContext userContext) {

        model.addAttribute("mode", PageEditMode.EDIT);
        PointModel pointModel = pointEditorService.getModelForId(id);

        return setUpModel(model, pointModel, userContext);
    }

    @RequestMapping("/points/{type}/create")
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String create(@PathVariable String type, @RequestParam int parentId, YukonUserContext userContext) {

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

        PointBase base = pointModel.getPointBase();

        model.addAttribute("isScalarType", base instanceof ScalarPoint);
        model.addAttribute("isStatusType", base instanceof StatusPoint);
        model.addAttribute("isCalcType", base instanceof CalcStatusPoint || base instanceof CalculatedPoint);
        model.addAttribute("isStatusPoint", base instanceof StatusPoint && !(base instanceof CalcStatusPoint));
        model.addAttribute("isAnalogPoint", base instanceof AnalogPoint);
        model.addAttribute("isAccumulatorPoint", base instanceof AccumulatorPoint);

        model.addAttribute("logicalGroups", PointLogicalGroups.values());
        model.addAttribute("scalarArchiveTypes", PointArchiveType.values());
        model.addAttribute("archiveIntervals", TimeIntervals.getArchiveIntervals());
        model.addAttribute("fdrTranslationNumbers", ImmutableList.of(0, 1, 2, 3, 4));
        model.addAttribute("fdrInterfaceTypes", FdrInterfaceType.values());
        model.addAttribute("fdrDirections", FdrDirection.values());
        model.addAttribute("statusControlTypes", StatusControlType.values());
        model.addAttribute("unitMeasures", unitMeasureDao.getLiteUnitMeasures());
        model.addAttribute("decimalDigits", ImmutableList.of(0, 1, 2, 3, 4, 5, 6, 7, 8));
        model.addAttribute("stateGroups", stateDao.getAllStateGroups());
        model.addAttribute("initialStates", statesForGroup(pointModel.getPointBase().getPoint().getStateGroupID()));
        model.addAttribute("statusArchiveTypes", ImmutableList.of(PointArchiveType.NONE, PointArchiveType.ON_CHANGE));
        model.addAttribute("pointUpdateTypes", StatusPointUpdateType.values());
        model.addAttribute("analogControlTypes", AnalogControlType.values());
        model.addAttribute("staleDataUpdateStyles", StaleData.UpdateStyle.values());
        model.addAttribute("alarmNotificationTypes", AlarmNotificationTypes.values());

        List<LiteNotificationGroup> notificationGroups = new ArrayList<>();

        LiteNotificationGroup noneGroup = new LiteNotificationGroup(PointAlarming.NONE_NOTIFICATIONID, noneChoice);

        notificationGroups.add(noneGroup);
        notificationGroups.addAll(dbCache.getAllContactNotificationGroups());

        model.addAttribute("notificationGroups", notificationGroups);

        model.addAttribute("alarmCategories", dbCache.getAllAlarmCategories());

        List<List<Map<String, Object>>> fdrProperties = new ArrayList<>();
        for (FDRTranslation fdr : base.getPointFDRList()) {

            List<Map<String, Object>> inputs =
                pointEditorService.breakIntoTranslationFields(fdr.getTranslation(), fdr.getInterfaceEnum());
            fdrProperties.add(inputs);
        }

        model.addAttribute("fdrProperties", fdrProperties);
        model.addAttribute("attachment", pointEditorService.getAttachmentStatus(base.getPoint().getPointID()));

        return "point/point.jsp";
    }

    @RequestMapping("/state-group/{id}/states")
    public @ResponseBody LiteState[] statesForGroup(@PathVariable("id") int id) {
        return stateDao.getLiteStates(id);

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
    public static class AnalogPointModel extends PointModel<AnalogPoint> {
    }

    public static class AccumulatorPointModel extends PointModel<AccumulatorPoint> {
    }

    public static class CalculatedPointModel extends PointModel<CalculatedPoint> {
    }

    public static class StatusPointModel extends PointModel<StatusPoint> {
    }

    public static class CalcStatusPointModel extends PointModel<CalcStatusPoint> {
    }

    @RequestMapping(value = "/points/Analog", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String saveAnalog(@ModelAttribute("pointModel") AnalogPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes) {

        return save(pointModel, result, redirectAttributes);
    }

    @RequestMapping(value = "/points/PulseAccumulator", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String saveAccumulator(PointModel<AccumulatorPoint> pointModel, BindingResult result,
            RedirectAttributes redirectAttributes) {

        return save(pointModel, result, redirectAttributes);
    }

    @RequestMapping(value = "/points/CalcAnalog", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String saveCalcAnalog(@ModelAttribute("pointModel") CalculatedPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes) {

        return save(pointModel, result, redirectAttributes);
    }

    @RequestMapping(value = "/points/Status", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String saveStatusAnalog(@ModelAttribute("pointModel") StatusPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes) {

        return save(pointModel, result, redirectAttributes);
    }

    @RequestMapping(value = "/points/CalcStatus", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String saveCalcStatusAnalog(@ModelAttribute("pointModel") CalcStatusPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes) {

        return save(pointModel, result, redirectAttributes);
    }

    private String save(PointModel pointModel, BindingResult result, RedirectAttributes redirectAttributes) {

        pointModel.finishSetup();

        pointValidator.validate(pointModel, result);

        if (result.hasErrors()) {
            return bindAndForward(pointModel, result, redirectAttributes);
        }

        int id = pointEditorService.save(pointModel);

        return "redirect:/tools/points/" + id;
    }

    private String bindAndForward(PointModel pointModel, BindingResult result, RedirectAttributes attrs) {

        attrs.addFlashAttribute("pointModel", pointModel);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.pointModel", result);

        return "redirect:/tools/points/" + pointModel.getId() + "/edit";
    }

    @RequestMapping(value = "/points/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable int id, FlashScope flashScope, YukonUserContext userContext) {

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
}
