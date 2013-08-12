package com.cannontech.web.dr.loadcontrol;

import java.beans.PropertyEditor;

import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaInput;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;

@Controller
@RequestMapping("/formula/*")
public class EstimatedLoadFormulaController {


    @Autowired private FormulaDao formulaDao;
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
        
        model.addAttribute("formulaBean", new FormulaBean(formula));

        setupCommonModel(model, formula.getType() ,formula.getCalculationType());

        return "dr/formula/formula.jsp";
    }

    @RequestMapping(value="create", method=RequestMethod.GET)
    public String createPage(ModelMap model, FormulaBean formulaBean) {
        model.addAttribute("mode", PageEditMode.CREATE);

        if (formulaBean == null) {
            formulaBean = new FormulaBean();
        }
        model.addAttribute("formulaBean", formulaBean);
        setupCommonModel(model, formulaBean.getFormulaType(), formulaBean.getCalculationType());

        return "dr/formula/formula.jsp";
    }

    @RequestMapping(value="create", method=RequestMethod.POST)
    public String doCreate(ModelMap model, FormulaBean formulaBean, BindingResult bindingResult, FlashScope flashScope) {

        boolean errors = formulaBeanValidator.doBeanValidation(formulaBean, bindingResult, flashScope);
        if (errors) {
            model.addAttribute("mode", PageEditMode.CREATE);
            setupCommonModel(model, formulaBean.getFormulaType(), formulaBean.getCalculationType());
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

        setupCommonModel(model, formula.getType(), formula.getCalculationType());

        model.addAttribute("formulaBean", new FormulaBean(formula));
        model.addAttribute("tableEntryBean", new TableEntryBean());

        return "dr/formula/formula.jsp";
    }

    @RequestMapping(value="edit/*", method=RequestMethod.POST)
    public String doEdit(ModelMap model, FormulaBean formulaBean, BindingResult bindingResult, FlashScope flashScope) {

        boolean errors = formulaBeanValidator.doBeanValidation(formulaBean, bindingResult, flashScope);
        if (errors) {
            setupCommonModel(model, formulaBean.getFormulaType() ,formulaBean.getCalculationType());
            return "dr/formula/formula.jsp";
        }

        formulaDao.saveFormula(formulaBean.getFormula());

        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "success.updated", formulaBean.getName()));

        return "redirect:/dr/formula/view/" + formulaBean.getFormulaId();
    }

    @RequestMapping("delete")
    public String doDelete(Integer formulaId, FlashScope flashScope) {
        formulaDao.deleteFormulaById(formulaId);
        flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Formula " + formulaId + " Deleted Successfully"));

        return "redirect:list";
    }

    private void setupCommonModel(ModelMap model, Formula.Type type, Formula.CalculationType inputType) {
        if (type == Formula.Type.GEAR) {
            model.addAttribute("formulaInputs", FormulaInput.InputType.getGearInputs());
        } else if (inputType == Formula.CalculationType.FUNCTION) {
            model.addAttribute("formulaInputs", FormulaInput.InputType.getApplianceCategoryFunctionInputs());
        } else {
            model.addAttribute("formulaInputs", FormulaInput.InputType.getApplianceCategoryTableInputs());
        }

        model.addAttribute("formulaTypes", Formula.Type.values());
        model.addAttribute("calculationTypes", Formula.CalculationType.values());

        model.addAttribute("dummyFunctionBean", new FunctionBean());
        model.addAttribute("dummyLookupTableBean", new LookupTableBean());
        model.addAttribute("dummyTableEntryBean", new TableEntryBean());
        model.addAttribute("dummyTimeTableEntryBean", new TimeTableEntryBean());
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder, YukonUserContext userContext) {
        PropertyEditor localTimeEditor = datePropertyEditorFactory.getLocalTimePropertyEditor(DateFormatEnum.TIME24H, userContext, BlankMode.NULL);
        webDataBinder.registerCustomEditor(LocalTime.class, localTimeEditor);
    }
}
