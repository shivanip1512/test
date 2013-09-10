package com.cannontech.web.dr.loadcontrol;

import java.beans.PropertyEditor;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.EnergyCompanyNotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.impl.LMGearDaoImpl;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.dr.estimatedload.ApplianceCategoryAssignment;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaInput;
import com.cannontech.dr.estimatedload.GearAssignment;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.weather.GeographicCoordinate;
import com.cannontech.loadcontrol.weather.NoaaWeatherDataService;
import com.cannontech.loadcontrol.weather.WeatherDataService;
import com.cannontech.loadcontrol.weather.WeatherLocation;
import com.cannontech.loadcontrol.weather.WeatherObservation;
import com.cannontech.loadcontrol.weather.WeatherStation;
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
@RequestMapping("/estimatedLoad/*")
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.ENABLE_ESTIMATED_LOAD)
public class EstimatedLoadController {

    @Autowired private FormulaDao formulaDao;
    @Autowired private LMGearDaoImpl gearDao;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private PointDao pointDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private ObjectFormattingService objectFormatingService;
    @Autowired private PaoDao paoDao;
    @Autowired private WeatherDataService weatherDataService;
    @Autowired private NoaaWeatherDataService noaaWeatherDataService;
    @Autowired private AttributeService attributeService;

    private String baseKey = "yukon.web.modules.dr.formula.";
    private FormulaBeanValidator formulaBeanValidator = new FormulaBeanValidator(baseKey);
    public static enum SortBy {NAME, CALCULATION_TYPE, TYPE, GEAR_CONTROL_METHOD,
                               APP_CAT_AVERAGE_LOAD, IS_ASSIGNED, PROGRAM_NAME}

    @RequestMapping("home")
    public String home(ModelMap model,YukonUserContext context) {

        listPageAjax(model, context, SortBy.NAME, false, 10, 1);
        appCatAssignmentsPage(model, context, SortBy.NAME, false, 10, 1);
        gearAssignmentsPage(model, context, SortBy.NAME, false, 10, 1);
        wetherLocationsTableAjax(model);

        return "dr/estimatedLoad/home.jsp";
    }

    @RequestMapping
    public String wetherLocationsTableAjax(ModelMap model) {

        List<WeatherLocation> weatherLocations = weatherDataService.getAllWeatherLocations();
        Map<String, WeatherStation> weatherStations = noaaWeatherDataService.getAllWeatherStations();

        model.addAttribute("weatherLocations", weatherLocations);
        model.addAttribute("weatherStations", weatherStations);
        model.addAttribute("weatherLocationBean", new WeatherLocationBean());

        Map<String, WeatherObservation> weatherObservations = new HashMap<>();
        for (WeatherLocation weatherLoc : weatherLocations) {
            WeatherObservation observation = weatherDataService.getCurrentWeatherObservation(weatherLoc);
            weatherObservations.put(weatherLoc.getStationId(), observation);
        }
        model.addAttribute("weatherObservations", weatherObservations);

        return "dr/estimatedLoad/_weatherLocationsTable.jsp";
    }

    @RequestMapping
    public String removeWeatherLocation(ModelMap model, FlashScope flashScope, int paoId, YukonUserContext context) {

        List<PointBase> points = pointDao.getPointsForPao(paoId);
        boolean isUsed = true;
        for (PointBase point : points) {
            isUsed |= formulaDao.isPointAFormulaInput(point.getPoint().getPointID());
        }

        if (isUsed) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.errors.cannotRemoveWeatherLoc"));
        } else {
            weatherDataService.deleteWeatherLocation(paoId);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.success.removedWeatherLocation"));
        }
        return "redirect:home";
    }

    @RequestMapping
    public String saveWeatherLocation(ModelMap model, WeatherLocationBean weatherLocationBean, BindingResult bindingResult) {

        validateLatLon(weatherLocationBean.getLatitude(), weatherLocationBean.getLongitude(), bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("dialogState", "searching");
            return "dr/estimatedLoad/_weatherStations.jsp";
        }

        double lat = Double.parseDouble(weatherLocationBean.getLatitude());
        double lon = Double.parseDouble(weatherLocationBean.getLongitude());
        GeographicCoordinate requestedCoordinate = new GeographicCoordinate(lat, lon);

        if (StringUtils.isBlank(weatherLocationBean.getName())) {
            bindingResult.rejectValue("name", "yukon.web.modules.dr.estimatedLoad.errors.blankName");
        } else if (!weatherDataService.isNameAvailableForWeatherLocation(weatherLocationBean.getName())){
            bindingResult.rejectValue("name", "yukon.web.modules.dr.estimatedLoad.errors.nameAlreadyUsed");
        }
        if (StringUtils.isBlank(weatherLocationBean.getStationId())) {
            bindingResult.rejectValue("stationId", "yukon.web.modules.dr.estimatedLoad.errors.noStationId");
        }

        if (bindingResult.hasErrors()) {
            addWeatherStationsToModel(model, requestedCoordinate);
            model.addAttribute("dialogState", "saving");
            return "dr/estimatedLoad/_weatherStations.jsp";
        }

        WeatherLocation weatherLocation = new WeatherLocation(null, null,
                                                              weatherLocationBean.getName(),
                                                              weatherLocationBean.getStationId(),
                                                              requestedCoordinate);
        weatherDataService.createWeatherLocation(weatherLocation);

        model.addAttribute("dialogState", "done");
        return "dr/estimatedLoad/_weatherStations.jsp";
    }

    @RequestMapping
    public String findCloseStations(ModelMap model, WeatherLocationBean wheatherLocationBean, BindingResult bindingResult) {

        validateLatLon(wheatherLocationBean.getLatitude(), wheatherLocationBean.getLongitude(), bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("dialogState", "searching");
            return "dr/estimatedLoad/_weatherStations.jsp";
        }

        double lat = Double.parseDouble(wheatherLocationBean.getLatitude());;
        double lon = Double.parseDouble(wheatherLocationBean.getLongitude());

        GeographicCoordinate requestedCoordinate = new GeographicCoordinate(lat, lon);
        addWeatherStationsToModel(model, requestedCoordinate);

        model.addAttribute("requestedLat", lat);
        model.addAttribute("requestedLon", lon);
        model.addAttribute("dialogState", "saving");

        return "dr/estimatedLoad/_weatherStations.jsp";
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

        return "dr/estimatedLoad/_formulasTable.jsp";
    }

    @RequestMapping("formula/view")
    public String viewPage(ModelMap model, Integer formulaId) {
        model.addAttribute("mode", PageEditMode.VIEW);

        Formula formula = formulaDao.getFormulaById(formulaId);
        FormulaBean formulaBean = new FormulaBean(formula);

        populateFormulaPageModel(model, formulaBean);

        return "dr/estimatedLoad/formula.jsp";
    }

    @RequestMapping(value="formula/create", method=RequestMethod.GET)
    public String createPage(ModelMap model, FormulaBean formulaBean) {
        model.addAttribute("mode", PageEditMode.CREATE);

        if (formulaBean == null) {
            formulaBean = new FormulaBean();
        }

        populateFormulaPageModel(model, formulaBean);

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
    public String editPage(ModelMap model, Integer formulaId) {
        model.addAttribute("mode", PageEditMode.EDIT);

        Formula formula = formulaDao.getFormulaById(formulaId);
        FormulaBean formulaBean = new FormulaBean(formula);

        populateFormulaPageModel(model, formulaBean);

        return "dr/estimatedLoad/formula.jsp";
    }

    @RequestMapping(value="formula/edit", method=RequestMethod.POST)
    public String doEdit(ModelMap model, FormulaBean formulaBean, BindingResult bindingResult, FlashScope flashScope) {

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

        return "dr/estimatedLoad/_gearFormulaPicker.jsp";
    }

    @RequestMapping
    public String gearAssignmentsPage(ModelMap model, YukonUserContext context,
                       @RequestParam(defaultValue="NAME") SortBy sort, final boolean descending,
                       @RequestParam(defaultValue="10") int itemsPerPage, 
                       @RequestParam(defaultValue="1") int page) {


        Map<Integer, LMProgramDirectGear> allGears = gearDao.getAllGears();
        List<GearAssignment> gearAssignments = formulaDao.getAssignmentsForGears(allGears.keySet());

        Map<Integer, LiteYukonPAObject> gearPrograms = getGearPrograms();
        gearAssignments = sortGearAssignments(gearAssignments, gearPrograms, sort, descending, context);

        SearchResult<GearAssignment> pagedGears
            = SearchResult.pageBasedForWholeList(page, itemsPerPage, gearAssignments);

        model.addAttribute("gearSort", sort);
        model.addAttribute("gearDescending", descending);
        model.addAttribute("pagedGears", pagedGears);
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

        return "dr/estimatedLoad/_appCatAssignmentsTable.jsp";
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

    private void addWeatherStationsToModel(ModelMap model, GeographicCoordinate requestedCoordinate) {
        List<WeatherStation> weatherStationResults = noaaWeatherDataService.getWeatherStationsByDistance(requestedCoordinate);
        weatherStationResults = weatherStationResults.subList(0, 5);

        Map<String, Integer> distanceToStation = new HashMap<>();
        for (WeatherStation station : weatherStationResults) {
            int distance = (int) station.getGeoCoordinate().distanceTo(requestedCoordinate);
            distanceToStation.put(station.getStationId(), distance);
        }

        model.addAttribute("weatherStationResults", weatherStationResults);
        model.addAttribute("distanceToStation", distanceToStation);
    }

    private void validateLatLon(String lat, String lon, BindingResult bindingResult) {
        try {
            Double.parseDouble(lat);
        } catch (NullPointerException | NumberFormatException e) {
            bindingResult.rejectValue("latitude", "yukon.web.modules.dr.estimatedLoad.errors.invalidLatitude");
        }

        try {
            Double.parseDouble(lon);
        } catch (NullPointerException | NumberFormatException e) {
            bindingResult.rejectValue("longitude", "yukon.web.modules.dr.estimatedLoad.errors.invalidLongitude");
        }
    }

    /**
     * Returns a map of PaoId to LiteYukonPAObject for gears.
     * 
     * This is used to get programs for gears. We need to display which program is currently tied to each gear.
     */
    private Map<Integer, LiteYukonPAObject> getGearPrograms() {
        Map<Integer, LiteYukonPAObject> gearPrograms = new HashMap<>();
        List<LiteYukonPAObject> programs = paoDao.getLiteYukonPAObjectByType(DeviceTypes.LM_DIRECT_PROGRAM);
        for(LiteYukonPAObject program : programs) {
            gearPrograms.put(program.getLiteID(), program);
        }
        return gearPrograms;
    }

    private List<GearAssignment> sortGearAssignments(List<GearAssignment> gearAssignments,
            final Map<Integer, LiteYukonPAObject> gearPrograms, SortBy sortBy,
            final boolean desc, final YukonUserContext context) {
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
        case PROGRAM_NAME:
            Collections.sort(gearAssignments, new Comparator<GearAssignment>() {
                @Override public int compare(GearAssignment o1, GearAssignment o2) {
                    LiteYukonPAObject program1 = gearPrograms.get(o1.getGear().getDeviceId());
                    LiteYukonPAObject program2 = gearPrograms.get(o2.getGear().getDeviceId());
                    if (program1 == program2) {
                        return 0;
                    }
                    if (program1 == null) {
                        return desc ? 1 : -1;
                    }
                    if (program2 == null) {
                        return desc ? -1 : 1;
                    }
                    int compare = program1.getPaoName().compareToIgnoreCase(program2.getPaoName());
                    return desc ? -compare : compare;
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
        // Although the weather inputs are also points, we want a more descriptive name for the point
        // so instead of 'Temperature' we will display 'Temperature - Minneapolis' for example
        List<WeatherLocation> weatherLocations = weatherDataService.getAllWeatherLocations();
        for (WeatherLocation weatherLoc : weatherLocations) {
            pointNames.put(weatherLoc.getTempPoint().getPointID(), weatherLoc.getName());
            pointNames.put(weatherLoc.getHumidityPoint().getPointID(), weatherLoc.getName());
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

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder, YukonUserContext userContext) {
        PropertyEditor localTimeEditor = datePropertyEditorFactory.getLocalTimePropertyEditor(DateFormatEnum.TIME24H, userContext, BlankMode.NULL);
        webDataBinder.registerCustomEditor(LocalTime.class, localTimeEditor);
    }
}
