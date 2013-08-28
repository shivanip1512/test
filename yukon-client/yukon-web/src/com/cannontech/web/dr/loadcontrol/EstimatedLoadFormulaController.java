package com.cannontech.web.dr.loadcontrol;

import java.beans.PropertyEditor;
import java.util.Collections;
import java.util.Comparator;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.EnergyCompanyNotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.impl.LMGearDaoImpl;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.dr.estimatedload.ApplianceCategoryAssignment;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaInput;
import com.cannontech.dr.estimatedload.GearAssignment;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.ApplianceTypeEnum;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.AuthorizeByCparm;
import com.google.common.base.Function;

@Controller
@RequestMapping("/formula/*")
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.ENABLE_ESTIMATED_LOAD)
public class EstimatedLoadFormulaController {

    @Autowired private FormulaDao formulaDao;
    @Autowired private LMGearDaoImpl gearDao;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private PointDao pointDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private ObjectFormattingService objectFormatingService;

    private String baseKey = "yukon.web.modules.dr.formula.";
    private FormulaBeanValidator formulaBeanValidator = new FormulaBeanValidator(baseKey);
    public static enum SortBy {NAME, CALCULATION_TYPE, TYPE, GEAR_CONTROL_METHOD, APP_CAT_AVERAGE_LOAD, IS_ASSIGNED}

    private Function<GearAssignment, GearControlMethod> controlMethodFunction
        = new Function<GearAssignment, GearControlMethod>() {
        @Override public GearControlMethod apply(GearAssignment from) {
            return from.getGear().getControlMethod();
        }
    };

    private Function<ApplianceCategoryAssignment, ApplianceTypeEnum> applianceTypeFunction
        = new Function<ApplianceCategoryAssignment, ApplianceTypeEnum>() {
        @Override public ApplianceTypeEnum apply(ApplianceCategoryAssignment from) {
            return from.getApplianceCategory().getApplianceType();
        }
    };

    private Function<FormulaBean, Formula.Type> formulaTypeFunction
        = new Function<FormulaBean, Formula.Type>() {
        @Override public Formula.Type apply(FormulaBean from) {
            return from.getFormulaType();
        }
    };

    private Function<FormulaBean, Formula.CalculationType> calculationTypeFunction
        = new Function<FormulaBean, Formula.CalculationType>() {
        @Override public Formula.CalculationType apply(FormulaBean from) {
            return from.getCalculationType();
        }
    };


    
    @RequestMapping(value={"list","view"})
    public String listPage(ModelMap model, YukonUserContext context) {
        listPageAjax(model, context, SortBy.NAME, false, 10, 1);
        return "dr/formula/list.jsp";
    }

    @RequestMapping
    public String listPageAjax(ModelMap model, YukonUserContext context,
                       @RequestParam(defaultValue="NAME") SortBy sort, final boolean descending,
                       @RequestParam(defaultValue="10") int itemsPerPage, 
                       @RequestParam(defaultValue="1") int page) {

        List<FormulaBean> allFormulas = FormulaBean.toBeans(formulaDao.getAllFormulas());
        allFormulas = sortFormulas(allFormulas, sort, descending, context);
        SearchResult<FormulaBean> pagedFormulas
            = SearchResult.pageBasedForWholeList(page, itemsPerPage, allFormulas);

        model.addAttribute("sort", sort);
        model.addAttribute("descending", descending);
        model.addAttribute("pagedFormulas", pagedFormulas);

        return "dr/formula/_formulasTable.jsp";
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
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "success.deleted"));
        return "redirect:list";
    }

    @RequestMapping("assignFormulaToGear")
    public String assignFormulaToGearViaAjax(Integer formulaId, Integer gearId, boolean unassign, ModelMap model) {

        if (unassign) {
            formulaDao.unassignGear(gearId);
        } else {
            formulaDao.saveGearAssignmentForId(formulaId, gearId);
        }

        GearAssignment currentAssignment = formulaDao.getAssignmentForGear(gearId);

        model.addAttribute("gearAssignment", currentAssignment);

        return "dr/formula/_gearFormulaPicker.jsp";
    }

    @RequestMapping
    public String gearAssignmentsPage(ModelMap model, YukonUserContext context,
                       @RequestParam(defaultValue="NAME") SortBy sort, final boolean descending,
                       @RequestParam(defaultValue="10") int itemsPerPage, 
                       @RequestParam(defaultValue="1") int page) {


        Map<Integer, LMProgramDirectGear> allGears = gearDao.getAllGears();
        List<GearAssignment> gearAssignments = formulaDao.getAssignmentsForGears(allGears.keySet());

        gearAssignments = sortGearAssignments(gearAssignments, sort, descending, context);

        SearchResult<GearAssignment> pagedGears
            = SearchResult.pageBasedForWholeList(page, itemsPerPage, gearAssignments);

        model.addAttribute("gearSort", sort);
        model.addAttribute("gearDescending", descending);
        model.addAttribute("pagedGears", pagedGears);

        return "dr/formula/_gearAssignmentsTable.jsp";
    }

    @RequestMapping("assignFormulaToAppCat")
    public String assignFormulaToAppCatViaAjax(Integer formulaId, Integer appCatId, boolean unassign, ModelMap model) {

        // need validation here
        if (unassign) {
            formulaDao.unassignAppCategory(appCatId);
        } else {
            formulaDao.saveAppCategoryAssignmentForId(formulaId, appCatId);
        }

        ApplianceCategoryAssignment currentAssignment = formulaDao.getAssignmentForApplianceCategory(appCatId);

        model.addAttribute("appCatAssignment", currentAssignment);
        return "dr/formula/_appCatFormulaPicker.jsp";
    }

    @RequestMapping
    public String appCatAssignmentsPage(ModelMap model, YukonUserContext context,
                       @RequestParam(defaultValue="NAME") SortBy sort, final boolean descending,
                       @RequestParam(defaultValue="10") int itemsPerPage, 
                       @RequestParam(defaultValue="1") int page) {

        YukonEnergyCompany yec = null;
        try {
            yec = yukonEnergyCompanyService.getEnergyCompanyByOperator(context.getYukonUser());
        } catch (EnergyCompanyNotFoundException e) {
            model.addAttribute("noEnergyCompany", true);
            return "dr/formula/_appCatAssignmentsTable.jsp";
        }

        List<Integer> appCatIds = applianceCategoryDao.getApplianceCategoryIdsByEC(yec.getEnergyCompanyId());
        Map<Integer, Integer> energyCompanyIds = applianceCategoryDao.getEnergyCompanyIdsForApplianceCategoryIds(appCatIds);
        List<ApplianceCategoryAssignment> appCatAssignments = formulaDao.getAssignmentsForApplianceCategories(appCatIds);

        appCatAssignments = sortAppCatAssignments(appCatAssignments, sort, descending, context);

        SearchResult<ApplianceCategoryAssignment> pagedAppCats
            = SearchResult.pageBasedForWholeList(page, itemsPerPage, appCatAssignments);

        model.addAttribute("energyCompanyIds", energyCompanyIds);
        model.addAttribute("appCatSort", sort);
        model.addAttribute("appCatDescending", descending);
        model.addAttribute("pagedAppCats", pagedAppCats);

        return "dr/formula/_appCatAssignmentsTable.jsp";
    }

    @RequestMapping("assignments")
    public String assignmentsPage(ModelMap model, YukonUserContext context) {
        appCatAssignmentsPage(model, context, SortBy.NAME, false, 10, 1);
        gearAssignmentsPage(model, context, SortBy.NAME, false, 10, 1);
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
                List<Integer> assignmentIds = formulaDao.getAppCategoryAssignmentIds(formulaBean.getFormulaId());
                formulaBean.setAssignments(assignmentIds);
                Map<Integer, ApplianceCategory> assignments = applianceCategoryDao.getByApplianceCategoryIds(assignmentIds);
                model.addAttribute("assignments", assignments);
            } else {
                List<Integer> assignmentIds = formulaDao.getGearAssignmentIds(formulaBean.getFormulaId());
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

    private List<GearAssignment> sortGearAssignments(List<GearAssignment> gearAssignments, SortBy sortBy, final boolean desc, final YukonUserContext context) {
        switch(sortBy) {
        default:
        case NAME:
            Collections.sort(gearAssignments, new Comparator<GearAssignment>() {
                @Override public int compare(GearAssignment o1, GearAssignment o2) {
                    int compare = o1.getGear().getGearName().compareToIgnoreCase(o2.getGear().getGearName());
                    return desc ? -compare : compare;
                }
            });
            return gearAssignments;
        case GEAR_CONTROL_METHOD:
            return objectFormatingService.sortDisplayableValuesWithMapper(gearAssignments, null, null, controlMethodFunction, desc, context);
        case IS_ASSIGNED:
            Collections.sort(gearAssignments, new Comparator<GearAssignment>() {
                @Override public int compare(GearAssignment o1, GearAssignment o2) {
                    if (o1.getFormulaId() == o2.getFormulaId()) {
                        return 0;
                    }
                    if (o1.getFormulaId() == null) {
                        return desc ? 1 : -1;
                    }
                    return desc ? -1 : 1;
                }
            });
            return gearAssignments;
        }
    }

    private List<ApplianceCategoryAssignment> sortAppCatAssignments(List<ApplianceCategoryAssignment> appCatAssignments, SortBy sortBy,final boolean desc, final YukonUserContext context) {
        switch(sortBy) {
        default:
        case NAME:
            Collections.sort(appCatAssignments, new Comparator<ApplianceCategoryAssignment>() {
                @Override public int compare(ApplianceCategoryAssignment o1, ApplianceCategoryAssignment o2) {
                    int compare = o1.getApplianceCategory().getName().compareToIgnoreCase(o2.getApplianceCategory().getName());
                    return desc ? -compare : compare;
                }
            });
            return appCatAssignments;
        case TYPE:
            return objectFormatingService.sortDisplayableValuesWithMapper(appCatAssignments, null, null, applianceTypeFunction, desc, context);
        case APP_CAT_AVERAGE_LOAD:
            Collections.sort(appCatAssignments, new Comparator<ApplianceCategoryAssignment>() {
                @Override public int compare(ApplianceCategoryAssignment o1, ApplianceCategoryAssignment o2) {
                    int compare = Double.compare(o1.getApplianceCategory().getApplianceLoad(), o2.getApplianceCategory().getApplianceLoad());
                    return desc ? -compare : compare;
                }
            });
            return appCatAssignments;
        case IS_ASSIGNED:
            Collections.sort(appCatAssignments, new Comparator<ApplianceCategoryAssignment>() {
                @Override public int compare(ApplianceCategoryAssignment o1, ApplianceCategoryAssignment o2) {
                    if (o1.getFormulaId() == o2.getFormulaId()) {
                        return 0;
                    }
                    if (o1.getFormulaId() == null) {
                        return desc ? 1 : -1;
                    }
                    return desc ? -1 : 1;
                }
            });
            return appCatAssignments;
        }
    }

    private List<FormulaBean> sortFormulas(List<FormulaBean> appCatAssignments, SortBy sortBy, final boolean desc, final YukonUserContext context) {
        switch(sortBy) {
        default:
        case NAME:
            Collections.sort(appCatAssignments, new Comparator<FormulaBean>() {
                @Override public int compare(FormulaBean o1, FormulaBean o2) {
                    int compare = o1.getName().compareToIgnoreCase(o2.getName());
                    return desc ? -compare : compare;
                }
            });
            return appCatAssignments;
        case TYPE:
            return objectFormatingService.sortDisplayableValuesWithMapper(appCatAssignments, null, null, formulaTypeFunction, desc, context);
        case CALCULATION_TYPE:
            return objectFormatingService.sortDisplayableValuesWithMapper(appCatAssignments, null, null, calculationTypeFunction, desc, context);
        }
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
