package com.cannontech.web.tools.points;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
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
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
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
import com.cannontech.database.data.point.PointOffsetUtils;
import com.cannontech.database.data.point.PointType;
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
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeListType;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;
import com.cannontech.web.editor.point.StaleData;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.stars.rtu.service.RtuService;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.cannontech.web.tools.points.model.PointModel;
import com.cannontech.web.tools.points.service.PointEditorService;
import com.cannontech.web.tools.points.service.PointEditorService.AttachedException;
import com.cannontech.web.tools.points.validators.CopyPointValidator;
import com.cannontech.web.tools.points.validators.PointValidator;
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
    @Autowired private PointDao pointDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private PointService pointService;
    @Autowired private RtuService rtuService;
    @Autowired private CopyPointValidator copyPointValidator;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    
    private static final String baseKey = "yukon.web.modules.tools.point";

    private final Validator validator = new SimpleValidator<PointBackingBean>(PointBackingBean.class) {
        @Override
        protected void doValidation(PointBackingBean bean, Errors errors) {
            if (bean.getValue() != null) {
                YukonValidationUtils.checkIsValidDouble(errors, "value", bean.getValue());
            }
        }
    };

    @RequestMapping(value = "/points/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, FlashScope flashScope, @PathVariable int id, YukonUserContext userContext, HttpServletRequest request) {
        verifyRoles(userContext.getYukonUser(), HierarchyPermissionLevel.RESTRICTED);
        model.addAttribute("mode", PageEditMode.VIEW);
        return retrievePointAndModel(model, userContext, flashScope, id, request);
    }
    
    @RequestMapping(value = "/points/{pointId}/render-copy-point", method = RequestMethod.GET)
    public String renderCopyPoint(ModelMap model, FlashScope flashScope, @PathVariable Integer pointId,
            YukonUserContext userContext, HttpServletRequest request) {
        verifyRoles(userContext.getYukonUser(), HierarchyPermissionLevel.CREATE);
        LitePointModel copyPointModel = null;
        if (model.containsAttribute("copyPointModel")) {
            copyPointModel = (LitePointModel) model.get("copyPointModel");
        } else {
            try {
                copyPointModel = pointEditorService.getLitePointModel(pointId);
                
                // set copy point name
                MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
                String newPointName = accessor.getMessage(YukonMessageSourceResolvable.createSingleCodeWithArguments(
                    "yukon.common.copyof", copyPointModel.getPointName()));
                copyPointModel.setPointName(newPointName);
                
                // set next valid physical offset.
                int pointOffset = PointOffsetUtils.getValidPointOffset(copyPointModel.getPaoId(), copyPointModel.getPointType());
                copyPointModel.setPointOffset(pointOffset);
                model.addAttribute("isCalcType",copyPointModel.getPointType().isCalcPoint());
                model.addAttribute("copyPointModel", copyPointModel);
                
                // Add paoType to model
                model.addAttribute("paoType", dbCache.getAllPaosMap().get(copyPointModel.getPaoId()).getPaoType());
            } catch (NotFoundException e) {
                Log.error(e);
                flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".notFoundError", pointId));
            }
        }
        return "point/copyPointPopup.jsp";
    }
    
    @RequestMapping(value = "/points/copy-point", method = RequestMethod.POST)
    public String copyPoint(@ModelAttribute("copyPointModel") LitePointModel pointModel, BindingResult result,
            ModelMap model, FlashScope flashScope, YukonUserContext userContext, HttpServletResponse response)
            throws JsonGenerationException, JsonMappingException, IOException {
        verifyRoles(userContext.getYukonUser(), HierarchyPermissionLevel.CREATE);
        copyPointValidator.validate(pointModel, result);

        if (result.hasErrors()) {
            model.addAttribute("paoType", dbCache.getAllPaosMap().get(pointModel.getPaoId()).getPaoType());
            model.addAttribute("copyPointModel", pointModel);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "point/copyPointPopup.jsp";
        }

        int pointId = pointEditorService.copy(pointModel);
        Map<String, Object> json = new HashMap<>();
        json.put("pointId", pointId);
        response.setContentType("application/json");
        JsonUtils.getWriter().writeValue(response.getOutputStream(), json);
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".copyPoint.success",
            pointModel.getPointName(), dbCache.getAllPaosMap().get(pointModel.getPaoId()).getPaoName()));
        return null;
    }
    
    @RequestMapping(value = "/points/getNextAvaliablePointOffset", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getNextValidPointOffset(@RequestParam("paoId") int paoId,
            @RequestParam("pointType") String pointType) {
        int pointOffset = PointOffsetUtils.getValidPointOffset(paoId, PointType.getForString(pointType));
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("nextValidPointOffset", pointOffset);
        return json;
    }

    private String retrievePointAndModel(ModelMap model, YukonUserContext userContext, FlashScope flashScope, int id, HttpServletRequest request){
        PointModel<? extends PointBase> pointModel = null;
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

            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(base.getPoint().getPaoID());
            if (pao.getPaoType() == PaoType.RTU_DNP || pao.getPaoType() == PaoType.CBC_LOGICAL) {
                Point point = pointModel.getPointBase().getPoint();
                List<MessageSourceResolvable> duplicatePointMessages = rtuService.generateDuplicatePointsErrorMessages(
                    point.getPaoID(), new PointIdentifier(point.getPointTypeEnum(), point.getPointOffset()), request);
                flashScope.setError(duplicatePointMessages, FlashScopeListType.NONE);
            }
      
            return setUpModel(model, pointModel, userContext);
        }
    }

    @RequestMapping(value = "/points/{id}/edit", method = RequestMethod.GET)
    public String edit(ModelMap model, FlashScope flashScope, @PathVariable int id, YukonUserContext userContext, HttpServletRequest request) {
        verifyRoles(userContext.getYukonUser(), HierarchyPermissionLevel.UPDATE);
        model.addAttribute("mode", PageEditMode.EDIT);
        return retrievePointAndModel(model, userContext, flashScope, id, request);
    }

    @RequestMapping("/points/{type}/create")
    public String create(@PathVariable String type, @RequestParam int parentId, YukonUserContext userContext) {

        verifyRoles(userContext.getYukonUser(), HierarchyPermissionLevel.CREATE);

        int pointType = PointTypes.getType(type);

        int newId = pointEditorService.create(pointType, parentId, userContext);

        return "redirect:/tools/points/" + newId + "/edit";
    }

    private String setUpModel(ModelMap model, PointModel<? extends PointBase> pointModel, YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        String noneChoice = messageAccessor.getMessage("yukon.common.none.choice");

        Object modelPointBase = model.get("pointModel");
        if (modelPointBase instanceof PointModel) {
            pointModel = (PointModel) modelPointBase;
        }
        
        model.addAttribute("pointModel", pointModel);

        LiteYukonPAObject parent = dbCache.getAllPaosMap().get(pointModel.getPointBase().getPoint().getPaoID());
        model.addAttribute("parentName", parent.getPaoName());
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
        model.addAttribute("updateRate", TimeIntervals.getUpdateAndScanRate());
        model.addAttribute("fdrTranslationNumbers", ImmutableList.of(0, 1, 2, 3, 4));
        model.addAttribute("fdrInterfaceTypes", interfaceTypes);
        model.addAttribute("fdrDirections", FdrDirection.values());
        model.addAttribute("statusControlTypes", StatusControlType.values());
        model.addAttribute("unitMeasures", unitMeasureDao.getLiteUnitMeasures());
        model.addAttribute("decimalPlaces", ImmutableList.of(0, 1, 2, 3, 4, 5, 6, 7, 8));
        model.addAttribute("meterDials", ImmutableList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
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

        verifyRoles(userContext.getYukonUser(), HierarchyPermissionLevel.UPDATE);
        return save(pointModel, result, redirectAttributes, flash, userContext);
    }

    @RequestMapping(value = "/points/PulseAccumulator", method = RequestMethod.POST)
    public String saveAccumulator(AccumulatorPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes, YukonUserContext userContext, FlashScope flash) {

        verifyRoles(userContext.getYukonUser(), HierarchyPermissionLevel.UPDATE);
        return save(pointModel, result, redirectAttributes, flash, userContext);
    }
    
    @RequestMapping(value = "/points/DemandAccumulator", method = RequestMethod.POST)
    public String saveDemandAccumulator(AccumulatorPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes, YukonUserContext userContext, FlashScope flash) {

        verifyRoles(userContext.getYukonUser(), HierarchyPermissionLevel.UPDATE);
        return save(pointModel, result, redirectAttributes, flash, userContext);
    }

    @RequestMapping(value = "/points/CalcAnalog", method = RequestMethod.POST)
    public String saveCalcAnalog(@ModelAttribute("pointModel") CalculatedPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes, YukonUserContext userContext, FlashScope flash) {

        verifyRoles(userContext.getYukonUser(), HierarchyPermissionLevel.UPDATE);
        return save(pointModel, result, redirectAttributes, flash, userContext);
    }

    @RequestMapping(value = "/points/Status", method = RequestMethod.POST)
    public String saveStatusAnalog(@ModelAttribute("pointModel") StatusPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes, YukonUserContext userContext, FlashScope flash) {

        verifyRoles(userContext.getYukonUser(), HierarchyPermissionLevel.UPDATE);
        return save(pointModel, result, redirectAttributes, flash, userContext);
    }

    @RequestMapping(value = "/points/CalcStatus", method = RequestMethod.POST)
    public String saveCalcStatusAnalog(@ModelAttribute("pointModel") CalcStatusPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes, YukonUserContext userContext, FlashScope flash) {

        verifyRoles(userContext.getYukonUser(), HierarchyPermissionLevel.UPDATE);

        return save(pointModel, result, redirectAttributes, flash, userContext);
    }

    @RequestMapping(value = "/points/System", method = RequestMethod.POST)
    public String saveSystem(@ModelAttribute("pointModel") SystemPointModel pointModel, BindingResult result,
            RedirectAttributes redirectAttributes, YukonUserContext userContext, FlashScope flash) {

        verifyRoles(userContext.getYukonUser(), HierarchyPermissionLevel.UPDATE);
        return save(pointModel, result, redirectAttributes, flash, userContext);
    }

    private String save(PointModel pointModel, BindingResult result, RedirectAttributes redirectAttributes, FlashScope flash, YukonUserContext userContext) {

        pointModel.finishSetup();

        pointValidator.validate(pointModel, result);

        if (result.hasErrors()) {
            return bindAndForward(pointModel, result, redirectAttributes);
        }
        
        int id = pointEditorService.save(pointModel, userContext);
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

        verifyRoles(userContext.getYukonUser(), HierarchyPermissionLevel.OWNER);

        PointModel pointModel = pointEditorService.getModelForId(id);
        int paoId = pointModel.getPointBase().getPoint().getPaoID();

        try {
            pointEditorService.delete(id, userContext);

        } catch (AttachedException e) {

            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

            String reason = accessor.getMessage(e.getStatus());

            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".deleteFailed", reason));

            return "redirect:/tools/points/" + pointModel.getId() + "/edit";
        }

        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(paoId);
        String pointName = pointModel.getPointBase().getPoint().getPointName();

        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".deleteSuccess", pointName));

        String redirectPaoHome = paoDetailUrlHelper.getUrlForPaoDetailPage(pao);
        if (redirectPaoHome != null) {
            return "redirect:" + redirectPaoHome;
        } else {
            // no details page exists yet
            return "redirect:/dashboard";
        }
    }

    @RequestMapping(value = "/points/manual-entry", method = RequestMethod.POST)
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_POINT_DATA, level = HierarchyPermissionLevel.UPDATE)
    public String manualEntry(YukonUserContext userContext, ModelMap model, int pointId) {

        PointBackingBean backingBean = new PointBackingBean();
        backingBean.setPointId(pointId);
        LitePoint litePoint = pointDao.getLitePoint(pointId);
        PointValueQualityHolder pointValue = asyncDynamicDataSource.getPointValue(pointId);
        if (litePoint.getPointTypeEnum() == PointType.Status
            || litePoint.getPointTypeEnum() == PointType.CalcStatus) {
            LiteStateGroup group = stateGroupDao.getStateGroup(litePoint.getStateGroupID());
            model.put("stateList", group.getStatesList());
            backingBean.setStateId((int) pointValue.getValue());
        } else {
            backingBean.setValue(pointValue.getValue());
        }
        LiteYukonPAObject liteYukonPAO = dbCache.getAllPaosMap().get(litePoint.getPaobjectID());
        model.put("deviceName", liteYukonPAO.getPaoName());
        model.put("pointName", litePoint.getPointName());
        model.addAttribute("backingBean", backingBean);
        return "../common/pao/manualEntryPopup.jsp";
    }

    @RequestMapping(value = "/points/manualEntrySend", method = RequestMethod.POST)
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_POINT_DATA, level = HierarchyPermissionLevel.UPDATE)
    public String manualEntrySend(HttpServletResponse response, YukonUserContext userContext,
            @ModelAttribute("backingBean") PointBackingBean backingBean, BindingResult bindingResult, ModelMap model,
            FlashScope flashScope) throws IOException {

        double newPointValue;
        LitePoint litePoint = pointDao.getLitePoint(backingBean.getPointId());
        if (litePoint.getPointTypeEnum() == PointType.Status
            || litePoint.getPointTypeEnum() == PointType.CalcStatus) {
            newPointValue = backingBean.getStateId();
        } else {
            validator.validate(backingBean, bindingResult);
            if (bindingResult.hasErrors()) {
                LiteYukonPAObject liteYukonPAO = dbCache.getAllPaosMap().get(litePoint.getPaobjectID());
                model.put("deviceName", liteYukonPAO.getPaoName());
                model.put("pointName", litePoint.getPointName());
                model.addAttribute("backingBean", backingBean);
                List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setError(messages);
                return "../common/pao/manualEntryPopup.jsp";
            }

            newPointValue = backingBean.getValue();
        }
        pointService.addPointData(backingBean.getPointId(), newPointValue, userContext);

        response.setContentType("application/json");
        response.getWriter().write(JsonUtils.toJson(Collections.singletonMap("action", "close")));
        return null;
    }

    /**
     * Checks if the user has either the DB Editor Role, or the Cap Control Editor role
     *
     * @throws NotAuthorizedException if user doesn't have required permissions
     */
    private void verifyRoles(LiteYukonUser user, HierarchyPermissionLevel hierarchyPermissionLevel) throws NotAuthorizedException {
        boolean capControlEditor = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        boolean isPointEditor = rolePropertyDao.checkLevel(YukonRoleProperty.MANAGE_POINTS, hierarchyPermissionLevel, user);

        if (!capControlEditor && !isPointEditor) {
            throw new NotAuthorizedException("User not allowed to edit points");
        }
    }
}
