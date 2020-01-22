package com.cannontech.web.dr.loadcontrol;

import java.beans.PropertyEditor;
import java.text.Collator;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.common.weather.WeatherDataService;
import com.cannontech.common.weather.WeatherLocation;
import com.cannontech.core.dao.EnergyCompanyNotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.impl.LMGearDaoImpl;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.dr.estimatedload.ApplianceCategoryAssignment;
import com.cannontech.dr.estimatedload.ApplianceCategoryInfoNotFoundException;
import com.cannontech.dr.estimatedload.EstimatedLoadAmount;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
import com.cannontech.dr.estimatedload.EstimatedLoadResult;
import com.cannontech.dr.estimatedload.EstimatedLoadSummary;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaInput;
import com.cannontech.dr.estimatedload.FormulaInput.InputType;
import com.cannontech.dr.estimatedload.GearAssignment;
import com.cannontech.dr.estimatedload.InputOutOfRangeException;
import com.cannontech.dr.estimatedload.InputValueNotFoundException;
import com.cannontech.dr.estimatedload.NoAppCatFormulaException;
import com.cannontech.dr.estimatedload.NoGearFormulaException;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.cannontech.dr.estimatedload.service.EstimatedLoadBackingServiceHelper;
import com.cannontech.dr.estimatedload.service.impl.EstimatedLoadBackingServiceHelperImpl.ButtonInfo;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.ApplianceTypeEnum;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

@Controller
@RequestMapping("/estimatedLoad/*")
@CheckCparm(MasterConfigBoolean.ENABLE_ESTIMATED_LOAD)
@CheckRole(YukonRole.DEMAND_RESPONSE)
public class EstimatedLoadController {

    @Autowired private FormulaDao formulaDao;
    @Autowired private LMGearDaoImpl gearDao;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private PointDao pointDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private EnergyCompanyService ecService; 
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private ObjectFormattingService objectFormatingService;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private WeatherDataService weatherDataService;
    @Autowired private FormulaBeanValidator formulaBeanValidator;
    @Autowired private EstimatedLoadBackingServiceHelper helper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public static enum SortBy {
        NAME, 
        CALCULATION_TYPE, 
        TYPE, 
        GEAR_CONTROL_METHOD,
        APP_CAT_AVERAGE_LOAD, 
        IS_ASSIGNED, 
        PROGRAM_NAME
    }

    private String baseKey = "yukon.web.modules.dr.formula.";
    private String elKey = "yukon.web.modules.dr.estimatedLoad.";

    @RequestMapping("home")
    public String home(ModelMap model, YukonUserContext context) {

        listPageAjax(model, context);
        appCatAssignmentsPage(model, context);
        gearAssignmentsPage(model, context);

        return "dr/estimatedLoad/home.jsp";
    }

    @RequestMapping("assignments")
    public String assignments(ModelMap model, YukonUserContext context) {

        model.addAttribute("selectAssignmentsTab", true);
        return home(model, context);
    }

    @RequestMapping("listPageAjax")
    public String listPageAjax(ModelMap model, YukonUserContext userContext) {
        
        List<FormulaBean> allFormulas = FormulaBean.toBeans(formulaDao.getAllFormulas(), userContext);
        allFormulas = sortFormulas(allFormulas, SortBy.NAME, false, userContext);

        model.addAttribute("allFormulas", allFormulas);

        return "dr/estimatedLoad/_formulasTable.jsp";
    }

    @RequestMapping("formula/view")
    public String viewPage(ModelMap model, Integer formulaId, YukonUserContext userContext) {
        
        model.addAttribute("mode", PageEditMode.VIEW);
        Formula formula = formulaDao.getFormulaById(formulaId);
        FormulaBean formulaBean = new FormulaBean(formula, userContext);
        populateFormulaPageModel(model, formulaBean);

        return "dr/estimatedLoad/formula.jsp";
    }

    @RequestMapping(value="formula/create", method=RequestMethod.GET)
    public String createPage(ModelMap model) {
        
        model.addAttribute("mode", PageEditMode.CREATE);
        populateFormulaPageModel(model, new FormulaBean());

        return "dr/estimatedLoad/formula.jsp";
    }

    @RequestMapping(value="formula/create", method=RequestMethod.POST)
    public String doCreate(ModelMap model, FormulaBean formulaBean, BindingResult bindingResult, FlashScope flashScope) {

        formulaBeanValidator.validate(formulaBean, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", PageEditMode.CREATE);
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);

            populateFormulaPageModel(model, formulaBean);

            return "dr/estimatedLoad/formula.jsp";
        }

        int newId = formulaDao.saveFormula(formulaBean.getFormula());

        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "success.created", formulaBean.getName()));

        return "redirect:/dr/estimatedLoad/formula/edit?formulaId=" + newId;
    }

    @RequestMapping(value="formula/edit", method=RequestMethod.GET)
    public String editPage(ModelMap model, Integer formulaId, YukonUserContext userContext) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        Formula formula = formulaDao.getFormulaById(formulaId);
        FormulaBean formulaBean = new FormulaBean(formula, userContext);
        populateFormulaPageModel(model, formulaBean);

        return "dr/estimatedLoad/formula.jsp";
    }

    @RequestMapping(value="formula/edit", method=RequestMethod.POST)
    public String doEdit(ModelMap model, FormulaBean formulaBean, BindingResult bindingResult, FlashScope flashScope) {
        
        // Clean up Time-Of-Day min/max, they are disabled inputs so they are not
        // included in the request.
        for (FunctionBean func : formulaBean.getFunctions()) {
            InputType type = func.getInputType();
            if (type == InputType.TIME_FUNCTION) {
                func.setInputMin(0.0);
                func.setInputMax(24.0);
            }
        }
        
        formulaBeanValidator.validate(formulaBean, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            populateFormulaPageModel(model, formulaBean);
            model.addAttribute("pointNames", getPointNames(formulaBean));
            return "dr/estimatedLoad/formula.jsp";
        }

        formulaDao.saveFormula(formulaBean.getFormula());

        if (formulaBean.getFormulaType() == Formula.Type.APPLIANCE_CATEGORY) {
            formulaDao.saveAppCategoryAssignmentsForId(formulaBean.getFormulaId(), formulaBean.getAssignments());
        } else {
            formulaDao.saveGearAssignmentsForId(formulaBean.getFormulaId(), formulaBean.getAssignments());
        }

        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "success.updated", formulaBean.getName()));

        model.addAttribute("pointNames", getPointNames(formulaBean));
        
        return "redirect:view?formulaId=" + formulaBean.getFormulaId();
    }

    @RequestMapping("formula/delete")
    public String doDelete(Integer formulaId, FlashScope flashScope) {
        
        formulaDao.deleteFormulaById(formulaId);
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "success.deleted"));
        
        return "redirect:../home";
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

        return "dr/estimatedLoad/_gearFormulaPicker.jsp";
    }

    @RequestMapping("gearAssignmentsPage")
    public String gearAssignmentsPage(ModelMap model, YukonUserContext context) {

        Map<Integer, LMProgramDirectGear> allGears = gearDao.getAllGears();
        List<GearAssignment> gearAssignments = formulaDao.getAssignmentsForGears(allGears.keySet());

        Map<Integer, LiteYukonPAObject> gearPrograms = getGearPrograms();
        gearAssignments = sortGearAssignments(gearAssignments, gearPrograms, SortBy.NAME, false, context);

        model.addAttribute("gearAssignments", gearAssignments);
        model.addAttribute("gearPrograms", gearPrograms);

        return "dr/estimatedLoad/_gearAssignmentsTable.jsp";
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
        
        return "dr/estimatedLoad/_appCatFormulaPicker.jsp";
    }

    @RequestMapping("appCatAssignmentsPage")
    public String appCatAssignmentsPage(ModelMap model, YukonUserContext context) {

        YukonEnergyCompany yec = null;
        try {
            yec = ecDao.getEnergyCompanyByOperator(context.getYukonUser());
        } catch (EnergyCompanyNotFoundException e) {
            model.addAttribute("noEnergyCompany", true);
            return "dr/formula/_appCatAssignmentsTable.jsp";
        }

        List<Integer> appCatIds = applianceCategoryDao.getApplianceCategoryIdsByEC(yec.getEnergyCompanyId());
        Map<Integer, Integer> energyCompanyIds = applianceCategoryDao.getEnergyCompanyIdsForApplianceCategoryIds(appCatIds);
        List<ApplianceCategoryAssignment> appCatAssignments = formulaDao.getAssignmentsForApplianceCategories(appCatIds);

        appCatAssignments = sortAppCatAssignments(appCatAssignments, SortBy.NAME, false, context);

        model.addAttribute("energyCompanyIds", energyCompanyIds);
        model.addAttribute("appCatAssignments", appCatAssignments);

        return "dr/estimatedLoad/_appCatAssignmentsTable.jsp";
    }
    
    @RequestMapping("program-error")
    public String programErrorPopup(ModelMap model, YukonUserContext userContext, int programId) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        EstimatedLoadResult result = helper.findProgramValue(programId, true);
        String programName = cache.getAllPaosMap().get(programId).getPaoName();
        
        ProgramError error = buildProgramError(userContext, programId, accessor, result, programName);
        
        model.addAttribute("title", accessor.getMessage(elKey + "popup.program.title", programName));
        model.addAttribute("errors", Collections.singleton(error));
        
        return "dr/estimatedLoad/programError.jsp";
    }
    
    @RequestMapping("scenario-program-error")
    public String programErrorPopup(ModelMap model, YukonUserContext userContext, int programId, int scenarioId) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
              
        EstimatedLoadResult result;
        result = helper.findScenarioProgramValue(programId, scenarioId, true);
        
        String programName = cache.getAllPaosMap().get(programId).getPaoName();
        
        ProgramError error = buildProgramError(userContext, programId, accessor, result, programName);
        
        model.addAttribute("title", accessor.getMessage(elKey + "popup.program.title", programName));
        model.addAttribute("errors", Collections.singleton(error));
        
        return "dr/estimatedLoad/programError.jsp";
    }
    
    /**
     * @param model the model map
     * @param userContext the user context
     * @param paoId the PAObject Id.  This should either be a control area or scenario otherwise it will return an error page
     * @return the jsp of the view
     */
    @RequestMapping("summary-error")
    public String summaryErrorPopup(ModelMap model, YukonUserContext userContext, int paoId) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        String name = pao.getPaoName();
        model.addAttribute("title", accessor.getMessage(elKey + "popup.program.title", name));
        
        EstimatedLoadSummary summary = null;

        if (pao.getPaoIdentifier().getPaoType() == PaoType.LM_CONTROL_AREA) {
            summary = helper.getControlAreaValue(pao.getPaoIdentifier(), true);
        } else if (pao.getPaoIdentifier().getPaoType() == PaoType.LM_SCENARIO) {
            summary = helper.getScenarioValue(pao.getPaoIdentifier(), true);
        } else {
            // Not a control area or scenario, so this paoId will not have an estimated load summary.
            model.addAttribute("initialMessage", accessor.getMessage(elKey + "error.notLoadManagement"));
            return "dr/estimatedLoad/programError.jsp";
        }
        
        List<ProgramError> errors = new ArrayList<>();
        if (summary.getTotalPrograms() == 0) {
            model.addAttribute("initialIcon", ButtonInfo.INFO.getIcon());
            model.addAttribute("initialMessage", accessor.getMessage(elKey + "error.noPrograms"));
        } else {
            model.addAttribute("initialMessage", accessor.getMessage(elKey + "error.summary", summary.getErrors()));
            for (int programId : summary.getProgramsInError()) {
                EstimatedLoadResult result;
                if (pao.getPaoIdentifier().getPaoType() == PaoType.LM_SCENARIO) {
                    result = helper.findScenarioProgramValue(programId, paoId, true);
                } else {
                    result = helper.findProgramValue(programId, true);
                }
                String programName = cache.getAllPaosMap().get(programId).getPaoName();
                errors.add(buildProgramError(userContext, programId, accessor, result, programName));
            }
            model.addAttribute("errors", errors);
        }
        return "dr/estimatedLoad/programError.jsp";
    }

    /**
     * This method transforms estimated load results (EstimatedLoadException) into ProgramError objects which are 
     * used by programError.jsp to display load programs that are in error.  
     * If applicable, a link is generated which will assist the user in resolving the error.
     * 
     * @param userContext Context of the user requesting the popup.
     * @param programId ProgramId of the program in error.
     * @param accessor The message accessor that will be used to resolve messages.
     * @param result The estimated load result being converted to a ProgramError.
     * @param programName The name of the load program in error.
     * @return The model object that will be passed to programError.jsp for display.
     */
    private ProgramError buildProgramError(YukonUserContext userContext, int programId, MessageSourceAccessor accessor,
            EstimatedLoadResult result, String programName) {
        
        ProgramError error = new ProgramError();
        error.setProgramId(programId);
        error.setProgramName(programName);
        
        if (result instanceof EstimatedLoadException) {
            error.setError(true);
            error.setIcon(helper.findIconStringForException((EstimatedLoadException) result));
            
            MessageSourceResolvable resolvable = helper.resolveException((EstimatedLoadException) result, userContext);
            error.setErrorMessage(accessor.getMessage(resolvable));
            
            if (result instanceof ApplianceCategoryInfoNotFoundException) {
                // Link to the corresponding appliance category
                LiteYukonUser yukonUser = userContext.getYukonUser();
                int ecId = ecDao.getEnergyCompany(yukonUser).getId();
                try {
                    ecService.verifyViewPageAccess(yukonUser, ecId);  // Throws the NotAuthorizedException if user is not an operator.
                    int appCatId = ((ApplianceCategoryInfoNotFoundException)result).getApplianceCategoryId();
                    String appCatName = applianceCategoryDao.getById(appCatId).getDisplayName();
                    
                    String linkText = accessor.getMessage(elKey + "link.applianceCategory", appCatName);
                    String linkValue = "/admin/energyCompany/applianceCategory/view?";
                    linkValue += "ecId=" + ecId;
                    linkValue += "&applianceCategoryId=" + appCatId;
                    
                    error.setLinkText(linkText);
                    error.setLinkValue(linkValue);
                } catch (NotAuthorizedException e) {
                    // Rather than show an error page, just hide the link by not including linkText.
                }
            } else if (result instanceof InputOutOfRangeException) {
                // Link to the formula detail page
                String formulaName = ((InputOutOfRangeException) result).getFormula().getName();
                int formulaId = ((InputOutOfRangeException) result).getFormula().getFormulaId();
                String linkText = accessor.getMessage(elKey + "link.formula", formulaName);
                String linkValue = "/dr/estimatedLoad/formula/view?formulaId=" + formulaId;
                
                error.setLinkText(linkText);
                error.setLinkValue(linkValue);
                
            } else if (result instanceof InputValueNotFoundException) {
                // Link to the formula detail page
                String formulaName = ((InputValueNotFoundException) result).getFormulaName();
                int formulaId = ((InputValueNotFoundException) result).getFormulaId();
                String linkText = accessor.getMessage(elKey + "link.formula", formulaName);
                String linkValue = "/dr/estimatedLoad/formula/view?formulaId=" + formulaId;
                
                error.setLinkText(linkText);
                error.setLinkValue(linkValue);

            } else if (result instanceof NoAppCatFormulaException || result instanceof NoGearFormulaException) {
                // Link to the formula assignments page
                String linkText = accessor.getMessage(elKey + "link.assignments");
                String linkValue = "/dr/estimatedLoad/assignments";
                
                error.setLinkText(linkText);
                error.setLinkValue(linkValue);
            }
        } else if (result instanceof EstimatedLoadAmount) {
            error.setError(false);
        }
        return error;
    }
    
    public class ProgramError {
        
        private int programId;
        private boolean error;
        private String errorMessage;
        private String programName;
        private String linkText;
        private String linkValue;
        private String icon;
        
        public int getProgramId() {
            return programId;
        }
        public void setProgramId(int programId) {
            this.programId = programId;
        }
        public boolean isError() {
            return error;
        }
        public void setError(boolean error) {
            this.error = error;
        }
        public String getErrorMessage() {
            return errorMessage;
        }
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        public String getProgramName() {
            return programName;
        }
        public void setProgramName(String programName) {
            this.programName = programName;
        }
        public String getLinkText() {
            return linkText;
        }
        public void setLinkText(String linkText) {
            this.linkText = linkText;
        }
        public String getLinkValue() {
            return linkValue;
        }
        public void setLinkValue(String linkValue) {
            this.linkValue = linkValue;
        }
        public void setIcon(String icon) {
            this.icon = icon;
        }
        public String getIcon() {
            return icon;
        }
    }

    private void populateFormulaPageModel(ModelMap model, FormulaBean formulaBean) {
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
                model.addAttribute("gearPrograms", getGearPrograms());
            }
        }

        List<WeatherLocation> weatherLocations = weatherDataService.getAllWeatherLocations();

        model.addAttribute("weatherLocations", weatherLocations);
        if (weatherLocations.isEmpty()) {
            formulaInputs.remove(InputType.HUMIDITY);
            formulaInputs.remove(InputType.TEMP_C);
            formulaInputs.remove(InputType.TEMP_F);
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
     * Returns a map of PaoId to LiteYukonPAObject for gears.
     * 
     * This is used to get programs for gears. We need to display which program is currently tied to each gear.
     */
    private Map<Integer, LiteYukonPAObject> getGearPrograms() {
        Map<Integer, LiteYukonPAObject> gearPrograms = new HashMap<>();
        
        Iterable<LiteYukonPAObject> programs = Iterables.filter(cache.getAllLMPrograms(), new Predicate<LiteYukonPAObject>() {
            @Override
            public boolean apply(LiteYukonPAObject input) {
                return PaoType.getDirectLMProgramTypes().contains(input.getPaoType());
            }
        });
        
        for (LiteYukonPAObject program : programs) {
            gearPrograms.put(program.getLiteID(), program);
        }
        return gearPrograms;
    }

    private List<GearAssignment> sortGearAssignments(List<GearAssignment> gearAssignments,
            final Map<Integer, LiteYukonPAObject> gearPrograms, SortBy sortBy,
            final boolean desc, final YukonUserContext userContext) {
        switch(sortBy) {
        default:
        case NAME:
            Ordering<GearAssignment> ordering
                = Ordering.from(Collator.getInstance(userContext.getLocale())).onResultOf(gearNameFunction);
            Collections.sort(gearAssignments, desc ? ordering.reverse() : ordering);
            return gearAssignments;
        case GEAR_CONTROL_METHOD:
            return objectFormatingService.sortDisplayableValuesWithMapper(gearAssignments, null, null, controlMethodFunction, desc, userContext);
        case IS_ASSIGNED:
            Collections.sort(gearAssignments, desc ? Collections.reverseOrder(compareGearAssignment) : compareGearAssignment);
            return gearAssignments;
        case PROGRAM_NAME:
            Collections.sort(gearAssignments, desc ? 
                      Collections.reverseOrder(new ProgramNameComparator(gearPrograms))
                    : new ProgramNameComparator(gearPrograms));
            return gearAssignments;
        }
    }

    private List<ApplianceCategoryAssignment> sortAppCatAssignments(List<ApplianceCategoryAssignment> appCatAssignments,
                                                                    SortBy sortBy,final boolean desc,
                                                                    final YukonUserContext userContext) {
        switch(sortBy) {
        default:
        case NAME:
            Ordering<ApplianceCategoryAssignment> ordering
                = Ordering.from(Collator.getInstance(userContext.getLocale())).onResultOf(appCatNameFunction);
            Collections.sort(appCatAssignments, desc ? ordering.reverse() : ordering);
            return appCatAssignments;
        case TYPE:
            return objectFormatingService.sortDisplayableValuesWithMapper(appCatAssignments, null, null, applianceTypeFunction, desc, userContext);
        case APP_CAT_AVERAGE_LOAD:
            Collections.sort(appCatAssignments, desc ? Collections.reverseOrder(compareAverageLoad) : compareAverageLoad);
            return appCatAssignments;
        case IS_ASSIGNED:
            Collections.sort(appCatAssignments, desc ? Collections.reverseOrder(compareAppCatAssignment) : compareAppCatAssignment);
            return appCatAssignments;
        }
    }

    private List<FormulaBean> sortFormulas(List<FormulaBean> appCatAssignments, SortBy sortBy,
                                           final boolean desc, final YukonUserContext userContext) {
        switch(sortBy) {
        default:
        case NAME:
            Ordering<FormulaBean> ordering
                = Ordering.from(Collator.getInstance(userContext.getLocale())).onResultOf(formulaNameFunction);
            Collections.sort(appCatAssignments, desc ? ordering.reverse() : ordering);
            return appCatAssignments;
        case TYPE:
            return objectFormatingService.sortDisplayableValuesWithMapper(appCatAssignments, null, null, formulaTypeFunction, desc, userContext);
        case CALCULATION_TYPE:
            return objectFormatingService.sortDisplayableValuesWithMapper(appCatAssignments, null, null, calculationTypeFunction, desc, userContext);
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
        // Although the weather inputs are also points, we want a more descriptive name for the point
        // so instead of 'Temperature' we will display 'Temperature - Minneapolis' for example
        List<WeatherLocation> weatherLocations = weatherDataService.getAllWeatherLocations();
        for (WeatherLocation weatherLoc : weatherLocations) {
            if (weatherLoc.getTempPoint() != null) {
                pointNames.put(weatherLoc.getTempPoint().getPointID(), weatherLoc.getName());
            }
            if (weatherLoc.getHumidityPoint() != null) {
                pointNames.put(weatherLoc.getHumidityPoint().getPointID(), weatherLoc.getName());
            }
        }
        return pointNames;
    }

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

    private final static Function<FormulaBean, String> formulaNameFunction = new Function<FormulaBean, String>() {
        @Override
        public String apply(FormulaBean bean) {
            return bean.getName();
        }
    };

    private final static Function<ApplianceCategoryAssignment, String> appCatNameFunction = new Function<ApplianceCategoryAssignment, String>() {
        @Override public String apply(ApplianceCategoryAssignment from) {
            return from.getApplianceCategory().getName();
        }
    };

    private final static Function<GearAssignment, String> gearNameFunction = new Function<GearAssignment, String>() {
        @Override public String apply(GearAssignment from) {
            return from.getGear().getGearName();
        }
    };

    private final static Comparator<GearAssignment> compareGearAssignment = new Comparator<GearAssignment>() {
        @Override public int compare(GearAssignment o1, GearAssignment o2) {
            if (o1.getFormulaId() == o2.getFormulaId()) {
                return 0;
            }
            if (o1.getFormulaId() == null) {
                return -1;
            }
            return 1;
        }
    };

    private final static Comparator<ApplianceCategoryAssignment> compareAppCatAssignment = new Comparator<ApplianceCategoryAssignment>() {
        @Override public int compare(ApplianceCategoryAssignment o1, ApplianceCategoryAssignment o2) {
            if (o1.getFormulaId() == o2.getFormulaId()) {
                return 0;
            }
            if (o1.getFormulaId() == null) {
                return -1;
            }
            return 1;
        }
    };

    private final static Comparator<ApplianceCategoryAssignment> compareAverageLoad = new Comparator<ApplianceCategoryAssignment>() {
        @Override public int compare(ApplianceCategoryAssignment o1, ApplianceCategoryAssignment o2) {
            int compare = Double.compare(o1.getApplianceCategory().getApplianceLoad(), o2.getApplianceCategory().getApplianceLoad());
            return compare;
        }
    };

    private class ProgramNameComparator implements Comparator<GearAssignment> {
        private Map<Integer, LiteYukonPAObject> gearPrograms;
        public ProgramNameComparator(Map<Integer, LiteYukonPAObject> gearPrograms) {
            this.gearPrograms = gearPrograms;
        }
        @Override
        public int compare(GearAssignment o1, GearAssignment o2) {
            LiteYukonPAObject program1 = gearPrograms.get(o1.getGear().getProgramId());
            LiteYukonPAObject program2 = gearPrograms.get(o2.getGear().getProgramId());
            if (program1 == program2) {
                return 0;
            }
            if (program1 == null) {
                return -1;
            }
            if (program2 == null) {
                return 1;
            }
            int compare = program1.getPaoName().compareToIgnoreCase(program2.getPaoName());
            return compare;
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder, YukonUserContext userContext) {
        PropertyEditor localTimeEditor = datePropertyEditorFactory.getLocalTimePropertyEditor(DateFormatEnum.TIME24H, userContext, BlankMode.NULL);
        webDataBinder.registerCustomEditor(LocalTime.class, localTimeEditor);
    }
}
