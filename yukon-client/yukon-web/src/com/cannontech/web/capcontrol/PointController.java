package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import com.cannontech.common.fdr.FdrInterfaceOption;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrOptionType;
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
import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointAlarming.AlarmNotificationTypes;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.models.PointModel;
import com.cannontech.web.capcontrol.models.TimeIntervals;
import com.cannontech.web.capcontrol.service.PointEditorService;
import com.cannontech.web.capcontrol.validators.PointValidator;
import com.cannontech.web.editor.point.StaleData;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableList;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class PointController {
    
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PointEditorService pointEditorService;
    @Autowired private PointValidator pointValidator;
    @Autowired private StateDao stateDao;
    @Autowired private UnitMeasureDao unitMeasureDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    
    @RequestMapping(value="points/{id}", method=RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
        
        model.addAttribute("mode", PageEditMode.VIEW);
        PointModel pointModel = pointEditorService.getModelForId(id);
        
        return setUpModel(model, pointModel, userContext);
    }
    
    @RequestMapping(value="points/{id}/edit", method=RequestMethod.GET)
    public String edit(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        PointModel pointModel = pointEditorService.getModelForId(id);
        
        return setUpModel(model, pointModel, userContext);
    }
    
    private String setUpModel(ModelMap model, PointModel pointModel, YukonUserContext userContext) {
        
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        String noneChoice = messageAccessor.getMessage("yukon.common.none.choice");
        
        Object modelPointBase= model.get("pointModel");
        if (modelPointBase instanceof PointModel) {
            pointModel = (PointModel) modelPointBase;
        }
        
        model.addAttribute("pointModel", pointModel);
        
        LiteYukonPAObject parent = dbCache.getAllPaosMap().get(pointModel.getPointBase().getPoint().getPaoID());
        model.addAttribute("parent", parent);
        
        PointBase base = pointModel.getPointBase();
        
        model.addAttribute("isScalarType", base instanceof ScalarPoint);
        model.addAttribute("isStatusType", base instanceof StatusPoint);
        model.addAttribute("isCalcType", base instanceof CalcStatusPoint ||
                                         base instanceof CalculatedPoint);
        model.addAttribute("isStatusPoint", base instanceof StatusPoint &&
                                          !(base instanceof CalcStatusPoint));
        model.addAttribute("isAnalogPoint", base instanceof AnalogPoint);
        model.addAttribute("isAccumulatorPoint", base instanceof AccumulatorPoint);
        
        model.addAttribute("logicalGroups", PointLogicalGroups.values());
        model.addAttribute("scalarArchiveTypes", PointArchiveType.values());
        model.addAttribute("archiveIntervals", TimeIntervals.getArchiveIntervals());
        model.addAttribute("fdrInterfaceTypes", FdrInterfaceType.values());
        model.addAttribute("fdrDirections", FdrDirection.values());
        model.addAttribute("statusControlTypes", StatusControlType.values());
        model.addAttribute("unitMeasures", unitMeasureDao.getLiteUnitMeasures());
        model.addAttribute("decimalDigits", ImmutableList.of(0, 1, 2, 3, 4, 5, 6, 7, 8));
        model.addAttribute("stateGroups", stateDao.getAllStateGroups());
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

        
        
        model.addAttribute("initialStates", statesForGroup(pointModel.getPointBase().getPoint().getStateGroupID()));
        
        return "point.jsp";
    }
    
    @RequestMapping("state-group/{id}/states")
    public @ResponseBody LiteState[] statesForGroup(@PathVariable("id") int id) {
        return stateDao.getLiteStates(id);
    
    }

    @RequestMapping("fdr/{type}/directions")
    public @ResponseBody List<String> fdrDirectionsForInterface(@PathVariable("type") FdrInterfaceType type) {
        
        List<String> values =  type.getSupportedDirectionsList().stream().map(new Function<FdrDirection, String> () {
            
            @Override
            public String apply(FdrDirection t) {
                return t.getValue();
            }})
            
        .collect(Collectors.toList());
        
        return values;
    }
    
    @RequestMapping("fdr/{type}/translation")
    public @ResponseBody String getDefaultTranslation(
            @PathVariable("type") FdrInterfaceType type, 
            @RequestParam("point-type") String pointType) {
        
        List<FdrInterfaceOption> options = type.getInterfaceOptionsList();
        
        String result = "";
        for (FdrInterfaceOption option : options) {
            result += (option.getOptionLabel() + ":");
            if (option.getOptionType() == FdrOptionType.COMBO) {
                result += option.getOptionValues()[0];
            } else {
                result += "(none)";
            }
            result += ";";
        }
        
        result += "POINTTYPE:" + pointType + ";";
        
        return result;
    }
    
    /* These classes are here to prevent type erasure of generics */
    public static class AnalogPointModel extends PointModel<AnalogPoint> {}
    public static class AccumulatorPointModel extends PointModel<AccumulatorPoint> {}
    public static class CalculatedPointModel extends PointModel<CalculatedPoint> {}
    public static class StatusPointModel extends PointModel<StatusPoint> {}
    public static class CalcStatusPointModel extends PointModel<CalcStatusPoint> {}
    
    @RequestMapping(value={"points/Analog"}, method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String saveAnalog(
            @ModelAttribute("pointModel") AnalogPointModel pointModel,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        return save(pointModel, result, redirectAttributes);
    }
    
    @RequestMapping(value={"points/PulseAccumulator"}, method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String saveAccumulator(
            @ModelAttribute("pointModel") AccumulatorPointModel pointModel,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        return save(pointModel, result, redirectAttributes);
    }
    
    @RequestMapping(value={"points/CalcAnalog"}, method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String saveCalcAnalog(
            @ModelAttribute("pointModel") CalculatedPointModel pointModel,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        return save(pointModel, result, redirectAttributes);
    }
    
    @RequestMapping(value={"points/Status"}, method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String saveStatusAnalog(
            @ModelAttribute("pointModel") StatusPointModel pointModel,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        return save(pointModel, result, redirectAttributes);
    }
    
    @RequestMapping(value={"points/CalcStatus"}, method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String saveCalcStatusAnalog(
            @ModelAttribute("pointModel") CalcStatusPointModel pointModel,
            BindingResult result,
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
   
        return "redirect:/capcontrol/points/" + id;
    }
    
    private String bindAndForward(PointModel pointModel, BindingResult result, RedirectAttributes attrs) {
        
        attrs.addFlashAttribute("pointModel", pointModel);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.pointModel", result);
        
        if (pointModel.getId() == null) {
            return "redirect:capcontrol/points/create";
        }
        
        return "redirect:/capcontrol/points/" + pointModel.getId() + "/edit";
    }
}
