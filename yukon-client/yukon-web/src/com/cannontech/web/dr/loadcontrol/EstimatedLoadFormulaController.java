package com.cannontech.web.dr.loadcontrol;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.impl.LMGearDaoImpl;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaInput;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;

@Controller
@RequestMapping("/formula/*")
public class EstimatedLoadFormulaController {

    @Autowired private FormulaDao formulaDao;
    @Autowired private LMGearDaoImpl gearDao;
    @Autowired ApplianceCategoryDao applianceCategoryDao;
    @Autowired private PointDao pointDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;

    private String baseKey = "yukon.web.modules.dr.formula.";
    private FormulaBeanValidator formulaBeanValidator = new FormulaBeanValidator(baseKey);

    @RequestMapping(value={"list","view"})
    public String listPage(ModelMap model) {
        model.addAttribute("formulas", FormulaBean.toBeans(formulaDao.getAllFormulas()));
        return "dr/formula/list.jsp";
    }

    @RequestMapping("view/{formulaId}")
    public String viewPage(ModelMap model, @PathVariable Integer formulaId) {
        model.addAttribute("mode", PageEditMode.VIEW);

        Formula formula = formulaDao.getFormulaById(formulaId);
        FormulaBean formulaBean = new FormulaBean(formula);

        setupCommonModel(model, formulaBean);

        return "dr/formula/formula.jsp";
    }

    @RequestMapping(value="create", method=RequestMethod.GET)
    public String createPage(ModelMap model, FormulaBean formulaBean) {
        model.addAttribute("mode", PageEditMode.CREATE);

        if (formulaBean == null) {
            formulaBean = new FormulaBean();
        }

        setupCommonModel(model, formulaBean);

        return "dr/formula/formula.jsp";
    }

    @RequestMapping(value="create", method=RequestMethod.POST)
    public String doCreate(ModelMap model, FormulaBean formulaBean, BindingResult bindingResult, FlashScope flashScope) {

        formulaBeanValidator.validate(formulaBean, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", PageEditMode.CREATE);
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);

            setupCommonModel(model, formulaBean);

            return "dr/formula/formula.jsp";
        }

        int newId = formulaDao.saveFormula(formulaBean.getFormula());

        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "success.created", formulaBean.getName()));

        return "redirect:/dr/formula/edit/" + newId;
    }

    @RequestMapping(value="edit/{formulaId}", method=RequestMethod.GET)
    public String editPage(ModelMap model, @PathVariable Integer formulaId) {
        model.addAttribute("mode", PageEditMode.EDIT);

        Formula formula = formulaDao.getFormulaById(formulaId);

        FormulaBean formulaBean = new FormulaBean(formula);

        setupCommonModel(model, formulaBean);

        return "dr/formula/formula.jsp";
    }

    @RequestMapping(value="edit/*", method=RequestMethod.POST)
    public String doEdit(ModelMap model, FormulaBean formulaBean, BindingResult bindingResult, FlashScope flashScope) {

        formulaBeanValidator.validate(formulaBean, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupCommonModel(model, formulaBean);
            model.addAttribute("pointNames", getPointNames(formulaBean));
            return "dr/formula/formula.jsp";
        }

        formulaDao.saveFormula(formulaBean.getFormula());

        if (formulaBean.getFormulaType() == Formula.Type.APPLIANCE_CATEGORY) {
            formulaDao.saveAppCategoryAssignmentsForId(formulaBean.getFormulaId(), formulaBean.getAssignments());
        } else {
            formulaDao.saveGearAssignmentsForId(formulaBean.getFormulaId(), formulaBean.getAssignments());
        }

        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "success.updated", formulaBean.getName()));

        model.addAttribute("pointNames", getPointNames(formulaBean));
        return "redirect:/dr/formula/view/" + formulaBean.getFormulaId();
    }

    @RequestMapping("delete")
    public String doDelete(Integer formulaId, FlashScope flashScope) {
        formulaDao.deleteFormulaById(formulaId);
        flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Formula " + formulaId + " Deleted Successfully"));

        return "redirect:list";
    }

    @RequestMapping("assignments")
    public String assignmentsPage() {
        return "dr/formula/assignments.jsp";
    }

    private void setupCommonModel(ModelMap model, FormulaBean formulaBean) {

        Set<FormulaInput.InputType> formulaInputs;
        if (formulaBean.getFormulaType() == Formula.Type.GEAR) {
            formulaInputs = FormulaInput.InputType.getGearInputs();
        } else if (formulaBean.getCalculationType() == Formula.CalculationType.FUNCTION) {
            formulaInputs = FormulaInput.InputType.getApplianceCategoryFunctionInputs();
        } else {
            formulaInputs = FormulaInput.InputType.getApplianceCategoryTableInputs();
        }

        if(formulaBean.getFormulaId() != null) {
            if (formulaBean.getFormulaType() == Formula.Type.APPLIANCE_CATEGORY) {
                List<Integer> assignmentIds = formulaDao.getAppCategoryAssignmentsById(formulaBean.getFormulaId());
                formulaBean.setAssignments(assignmentIds);
                Map<Integer, ApplianceCategory> assignments = applianceCategoryDao.getByApplianceCategoryIds(assignmentIds);
                model.addAttribute("assignments", assignments);
            } else {
                List<Integer> assignmentIds = formulaDao.getGearAssignmentsById(formulaBean.getFormulaId());
                formulaBean.setAssignments(assignmentIds);
                Map<Integer, LMProgramDirectGear> assignments = gearDao.getByGearIds(assignmentIds);
                model.addAttribute("assignments", assignments);
            }
        }

        model.addAttribute("formulaInputs",formulaInputs);
        model.addAttribute("formulaTypes", Formula.Type.values());
        model.addAttribute("calculationTypes", Formula.CalculationType.values());

        model.addAttribute("dummyFunctionBean", new FunctionBean());
        model.addAttribute("dummyLookupTableBean", new LookupTableBean());
        model.addAttribute("dummyTableEntryBean", new TableEntryBean());
        model.addAttribute("dummyTimeTableEntryBean", new TimeTableEntryBean());

        model.addAttribute("pointNames", getPointNames(formulaBean));
        model.addAttribute("formulaBean", formulaBean);
    }

    /**
     * Gets point names for use on the jsp. Used to display point name rather than just id.
     */
    private Map<Integer, String> getPointNames(FormulaBean formula) {
        Set<Integer> pointIds = new HashSet<>();
        if(formula.getFunctions() != null) {
            for (FunctionBean f : formula.getFunctions()) {
                if (f.getInputType() == FormulaInput.InputType.POINT) {
                    pointIds.add(f.getInputPointId());
                }
            }
        }
        if(formula.getTables() != null) {
            for (LookupTableBean t : formula.getTables()) {
                if (t.getInputType() == FormulaInput.InputType.POINT) {
                    pointIds.add(t.getInputPointId());
                }
            }
        }
        List<LitePoint> points = pointDao.getLitePoints(pointIds);
        Map<Integer, String> pointNames = new HashMap<>(points.size());
        for (LitePoint point : points) {
            pointNames.put(point.getPointID(), point.getPointName());
        }
        return pointNames;
    }


    @InitBinder
    public void initBinder(WebDataBinder webDataBinder, YukonUserContext userContext) {
        PropertyEditor localTimeEditor = datePropertyEditorFactory.getLocalTimePropertyEditor(DateFormatEnum.TIME24H, userContext, BlankMode.NULL);
        webDataBinder.registerCustomEditor(LocalTime.class, localTimeEditor);
    }
}
