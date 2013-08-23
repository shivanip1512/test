package com.cannontech.web.dr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.ApplianceWithRuntime;
import com.cannontech.dr.assetavailability.SimpleAssetAvailability;
import com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.dr.loadgroup.filter.LoadGroupsForProgramFilter;
import com.cannontech.dr.program.filter.ForControlAreaFilter;
import com.cannontech.dr.program.filter.ForScenarioFilter;
import com.cannontech.dr.program.model.ProgramState;
import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.dr.scenario.service.ScenarioService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.chart.service.AssetAvailabilityChartService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.dr.LoadGroupControllerHelper.LoadGroupListBackingBean;
import com.cannontech.web.dr.ProgramControllerHelper.ProgramListBackingBean;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
@RequestMapping("/program/*")
public class ProgramController extends ProgramControllerBase {
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ScenarioService scenarioService;
    @Autowired private LoadGroupControllerHelper loadGroupControllerHelper;
    @Autowired private FavoritesDao favoritesDao;
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    @Autowired private AssetAvailabilityChartService assetAvailabilityChartService;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;

    private static Map<AssetAvailabilityCombinedStatus, String> colorMap;
    static {
        colorMap = new HashMap<>();
        colorMap.put(AssetAvailabilityCombinedStatus.RUNNING, "green");
        colorMap.put(AssetAvailabilityCombinedStatus.NOT_RUNNING, "orange");
        colorMap.put(AssetAvailabilityCombinedStatus.UNAVAILABLE, "red");
        colorMap.put(AssetAvailabilityCombinedStatus.OPTED_OUT, "grey");
    }
    private static final String ITEMS_PER_PAGE = "10";

    @RequestMapping
    public String list(ModelMap model, YukonUserContext userContext,
            @ModelAttribute("backingBean") ProgramListBackingBean backingBean,
            BindingResult bindingResult, FlashScope flashScope) {
        programControllerHelper.filterPrograms(model, userContext, backingBean,
                                               bindingResult, null);

        addFilterErrorsToFlashScopeIfNecessary(model, bindingResult, flashScope);

        return "dr/program/list.jsp";
    }

    @RequestMapping
    public String detail(int programId, ModelMap model,
            @ModelAttribute("backingBean") LoadGroupListBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE);
        favoritesDao.detailPageViewed(programId);
        model.addAttribute("program", program);
        boolean isFavorite =
            favoritesDao.isFavorite(programId, userContext.getYukonUser());
        model.addAttribute("isFavorite", isFavorite);

        UiFilter<DisplayablePao> detailFilter = new LoadGroupsForProgramFilter(programId);
        loadGroupControllerHelper.filterGroups(model, userContext, backingBean,
                                               bindingResult, detailFilter, flashScope);

        DisplayablePao parentControlArea =
            controlAreaService.findControlAreaForProgram(userContext, programId);
        model.addAttribute("parentControlArea", parentControlArea);
        List<Scenario> parentScenarios = scenarioDao.findScenariosForProgram(programId);
        model.addAttribute("parentScenarios", parentScenarios);
        
        try {
            SimpleAssetAvailabilitySummary aaSummary = 
                    assetAvailabilityService.getAssetAvailabilityFromDrGroup(program.getPaoIdentifier());
            model.addAttribute("assetAvailabilitySummary", aaSummary);
            model.addAttribute("assetTotal", aaSummary.getAll().size());
            model.addAttribute("pieJSONData", assetAvailabilityChartService.getJSONPieData(aaSummary, userContext));
        } catch(DynamicDataAccessException e) {
            model.addAttribute("dispatchDisconnected", true);
        }
        
        return "dr/program/detail.jsp";
    }

    @RequestMapping("/program/assetDetails")
    public String assetDetails(@RequestParam(defaultValue=ITEMS_PER_PAGE) int itemsPerPage, 
                               @RequestParam(defaultValue="1") int page,
                               int assetId, 
                               ModelMap model, 
                               YukonUserContext context) {
        
        DisplayablePao program = programService.getProgram(assetId);

        List<AssetAvailabilityDetails> resultsList = getResultsList(assetId, context, null);
        
        SearchResult<AssetAvailabilityDetails> result = 
                SearchResult.pageBasedForWholeList(page, itemsPerPage, resultsList);

        try {
            SimpleAssetAvailabilitySummary aaSummary = 
                    assetAvailabilityService.getAssetAvailabilityFromDrGroup(program.getPaoIdentifier());
            model.addAttribute("assetAvailabilitySummary", aaSummary);
            model.addAttribute("pieJSONData", assetAvailabilityChartService.getJSONPieData(aaSummary, context));
        } catch(DynamicDataAccessException e) {
            model.addAttribute("dispatchDisconnected", true);
        }
        
        model.addAttribute("assetId", assetId);
        model.addAttribute("programId", assetId);
        model.addAttribute("program", program);
        model.addAttribute("type", "program");
        model.addAttribute("result", result);
        model.addAttribute("colorMap", colorMap);
        model.addAttribute("itemsPerPage", itemsPerPage);
        
        return "dr/assetDetails.jsp";
    }

    private List<AssetAvailabilityDetails> getResultsList(int assetId, YukonUserContext context, JSONArray filters) {

        // Create a HashSet for the filtered AssetAvailabilityCombinedStatus values.
        // Either null or an empty JSONArray will display all data values.
        Set<AssetAvailabilityCombinedStatus> filterSet = Sets.newHashSet();
        if (filters == null || filters.isEmpty()) {
            filterSet.add(AssetAvailabilityCombinedStatus.RUNNING);
            filterSet.add(AssetAvailabilityCombinedStatus.NOT_RUNNING);
            filterSet.add(AssetAvailabilityCombinedStatus.OPTED_OUT);
            filterSet.add(AssetAvailabilityCombinedStatus.UNAVAILABLE);
        } else {
            if (filters.contains(AssetAvailabilityCombinedStatus.RUNNING)) {
                filterSet.add(AssetAvailabilityCombinedStatus.RUNNING);
            }
            if (filters.contains(AssetAvailabilityCombinedStatus.NOT_RUNNING)) {
                filterSet.add(AssetAvailabilityCombinedStatus.NOT_RUNNING);
            }
            if (filters.contains(AssetAvailabilityCombinedStatus.OPTED_OUT)) {
                filterSet.add(AssetAvailabilityCombinedStatus.OPTED_OUT);
            }
            if (filters.contains(AssetAvailabilityCombinedStatus.UNAVAILABLE)) {
                filterSet.add(AssetAvailabilityCombinedStatus.UNAVAILABLE);
            }
        }
        
        DisplayablePao program = programService.getProgram(assetId);
        paoAuthorizationService.verifyAllPermissions(context.getYukonUser(), program, Permission.LM_VISIBLE);
        
        Map<Integer, SimpleAssetAvailability> resultMap = 
                assetAvailabilityService.getAssetAvailability(program.getPaoIdentifier());
        List<AssetAvailabilityDetails> resultList = new ArrayList<>(resultMap.size());

        // Create set of applianceCategoryId's (& eliminates duplicates)
        Set<Integer> applianceCatIds = Sets.newHashSet();
        for (SimpleAssetAvailability entry : resultMap.values()) {
            ImmutableSet<ApplianceWithRuntime> appRuntime = entry.getApplianceRuntimes();
            for (ApplianceWithRuntime AppRun : appRuntime) {
                applianceCatIds.add(AppRun.getApplianceCategoryId());
            }
        }
        Map<Integer, ApplianceCategory> appCatMap = applianceCategoryDao.getByApplianceCategoryIds(applianceCatIds);
        
        for (Map.Entry<Integer, SimpleAssetAvailability> entry : resultMap.entrySet()) {
            
            AssetAvailabilityDetails aaDetails = new AssetAvailabilityDetails();
            HardwareSummary hwSum = inventoryDao.findHardwareSummaryById(entry.getKey());

            // Set the Serial Number and Type columns
            aaDetails.setSerialNumber(hwSum.getSerialNumber());
            aaDetails.setType(hwSum.getHardwareType().toString());
            
            SimpleAssetAvailability value = entry.getValue();
            // set the Last Communication column
            aaDetails.setLastComm(value.getLastCommunicationTime());

            // parse through the appRuntime set to get list of appliances & last runtime.
            ImmutableSet<ApplianceWithRuntime> appRuntime = value.getApplianceRuntimes();
            Iterator<ApplianceWithRuntime> iter = appRuntime.iterator();
            String applianceStr = "";
            Instant lastRun = null;
            while (iter.hasNext()) {
                ApplianceWithRuntime tmp = (ApplianceWithRuntime) iter.next();
                ApplianceCategory appCat = appCatMap.get(tmp.getApplianceCategoryId());
                // The Appliances will be a comma-separated list of the appliances for this device. 
                // No Internationalization since this comes from YukonSelectionList.
                applianceStr = (applianceStr == "") ? appCat.getDisplayName() 
                                                    : applianceStr + ", " + appCat.getDisplayName();
                Instant lnzrt = tmp.getLastNonZeroRuntime();
                if (lnzrt != null) {
                    if (lastRun == null) {
                        lastRun = lnzrt;  
                    } else {
                        lastRun = (lastRun.isAfter(lnzrt)) ? lastRun : lnzrt;
                    }
                }
            }
            aaDetails.setAppliances(applianceStr);
            aaDetails.setLastRun(lastRun);

            aaDetails.setAvailability(value.getCombinedStatus());
            if (filterSet.contains(aaDetails.getAvailability())) {
                resultList.add(aaDetails);
            } else {
                aaDetails = null;
            }
        }
        return resultList;
    }

    /**
     * Used for paging and filtering operations.
     */
    @RequestMapping("/program/page")
    public String page(ModelMap model, 
                       YukonUserContext context,
                       String type,
                       String assetId,
                       @RequestParam(defaultValue=ITEMS_PER_PAGE) int itemsPerPage, 
                       @RequestParam(defaultValue="1") int page,
                       String filter) {

        JSONArray filters = (filter == null || filter.length() == 0) ? null : JSONArray.fromObject(filter);
        List<AssetAvailabilityDetails> resultsList = getResultsList(Integer.parseInt(assetId), context, filters);

        SearchResult<AssetAvailabilityDetails> result = 
                SearchResult.pageBasedForWholeList(page, itemsPerPage, resultsList);
        
        model.addAttribute("result", result);
        model.addAttribute("type", type);
        model.addAttribute("assetId", assetId);
        model.addAttribute("colorMap", colorMap);
        model.addAttribute("itemsPerPage", itemsPerPage);
        
        return "dr/assetTable.jsp";
    }


    @RequestMapping
    public String getChangeGearValue(ModelMap modelMap, int programId, YukonUserContext userContext) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);

        addGearsToModel(program, modelMap);
        modelMap.addAttribute("program", program);

        return "dr/program/getChangeGearValue.jsp";
    }
    
    @RequestMapping
    public @ResponseBody JSONObject changeGear(HttpServletResponse resp, ModelMap modelMap, int programId, int gearNumber, 
                             YukonUserContext userContext, FlashScope flashScope) throws IOException {
        
        DisplayablePao program = programService.getProgram(programId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        programService.changeGear(programId, gearNumber);
        
        demandResponseEventLogService.threeTierProgramChangeGear(yukonUser, program.getName());
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.getChangeGearValue.gearChanged"));

        JSONObject json = new JSONObject();
        json.put("action", "reload");
        return json;
    }
    
    @RequestMapping
    public String changeGearMultiplePopup(ModelMap model, @ModelAttribute("backingBean") ChangeMultipleGearsBackingBean backingBean,
                                          BindingResult bindingResult, YukonUserContext userContext, FlashScope flashScope) {
        UiFilter<DisplayablePao> filter = null;

        String paoName = null;
        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        if (backingBean.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         controlArea,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("controlArea", controlArea);
            paoName = controlArea.getName();
            filter = new ForControlAreaFilter(backingBean.getControlAreaId());
        }
        if (backingBean.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         scenario,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("scenario", scenario);
            paoName = scenario.getName();
            filter = new ForScenarioFilter(backingBean.getScenarioId());
            scenarioPrograms =
                scenarioDao.findScenarioProgramsForScenario(backingBean.getScenarioId());
            model.addAttribute("scenarioPrograms", scenarioPrograms);
        }

        if (filter == null) {
            throw new IllegalArgumentException();
        }

        SearchResult<DisplayablePao> searchResult =
            programService.filterPrograms(filter, new DisplayablePaoComparator(),
                                          0, Integer.MAX_VALUE, userContext);
        List<DisplayablePao> programs = searchResult.getResultList();
        if (programs == null || programs.size() == 0) {

            model.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.startMultiplePrograms.noPrograms." +
                                                 (backingBean.getControlAreaId() != null ? "controlArea" : "scenario"),
                                                 paoName);
            model.addAttribute("userMessage", error);
            return "common/userMessage.jsp";
        }
        model.addAttribute("programs", programs);

        List<ProgramGearChangeInfo> programGearChangeInfo = new ArrayList<ProgramGearChangeInfo>(programs.size());
        
        for (DisplayablePao program : programs) {
            int programId = program.getPaoIdentifier().getPaoId();
            int gearNumber = 1;
            if (scenarioPrograms != null) {
                ScenarioProgram scenarioProgram = scenarioPrograms.get(programId);
                if (scenarioProgram != null) {
                    gearNumber = scenarioProgram.getStartGear();
                }
            }
            programGearChangeInfo.add(new ProgramGearChangeInfo(programId, gearNumber, true));
        }
        backingBean.setProgramGearChangeInfo(programGearChangeInfo);
        
        addGearsToModel(searchResult.getResultList(), model);
        addErrorsToFlashScopeIfNecessary(bindingResult, flashScope);
        
        Map<Integer, Map<Integer, Boolean>> programIndexTargetGearMap = getIndexBasedIsTargetGearMap(programs);

        model.addAttribute("targetGearMap",programIndexTargetGearMap);
      return "dr/program/changeMultipleProgramsGearsDetails.jsp";
  }
    
    @RequestMapping
    public @ResponseBody JSONObject changeMultipleGears(HttpServletResponse resp, ModelMap model, @ModelAttribute("backingBean") ChangeMultipleGearsBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext, FlashScope flashScope) throws IOException {
        

        if (backingBean.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         controlArea,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("controlArea", controlArea);
        }
        if (backingBean.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         scenario,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("scenario", scenario);
        }
        boolean gearChanged = false;
        for (ProgramGearChangeInfo gearChangeInfo : backingBean.getProgramGearChangeInfo()) {
            if (gearChangeInfo.isChangeGear()) {
                gearChanged = true;
                DisplayablePao program = programService.getProgram(gearChangeInfo.getProgramId());
    
                LiteYukonUser yukonUser = userContext.getYukonUser();
                programService.changeGear(gearChangeInfo.getProgramId(), gearChangeInfo.getGearNumber());
                demandResponseEventLogService.threeTierProgramChangeGear(yukonUser, program.getName());

            }
        }
        if (gearChanged) {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.getChangeGearValue.multipleGearChanged"));
        }
        
        JSONObject json = new JSONObject();
        json.put("action", "reload");
        return json;
    }
    
    @RequestMapping
    public String sendEnableConfirm(ModelMap modelMap, int programId, boolean isEnabled,
            YukonUserContext userContext) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        modelMap.addAttribute("program", program);
        modelMap.addAttribute("isEnabled", isEnabled);
        return "dr/program/sendEnableConfirm.jsp";
    }
    
    @RequestMapping
    public @ResponseBody JSONObject setEnabled(HttpServletResponse resp, ModelMap modelMap, int programId, boolean isEnabled,
            YukonUserContext userContext, FlashScope flashScope) throws IOException {
        
        DisplayablePao program = programService.getProgram(programId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        programService.setEnabled(programId, isEnabled);

        if(isEnabled) {
            demandResponseEventLogService.threeTierProgramEnabled(yukonUser, program.getName());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendEnableConfirm.enabled"));
        } else {
            demandResponseEventLogService.threeTierProgramDisabled(yukonUser, program.getName());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendEnableConfirm.disabled"));
        }
        
        JSONObject json = new JSONObject();
        json.put("action", "reload");
        return json;
    }
    
    @RequestMapping
    public String sendEnableDisableProgramsConfirm(ModelMap modelMap, YukonUserContext userContext,
                                             Integer controlAreaId, Integer scenarioId, boolean enable) {
        
        modelMap.addAttribute("enable", enable);
        
        //Determine if parent object is a control area or scenario
        DisplayablePao parent = null;
        UiFilter<DisplayablePao> filter = null;
        if(controlAreaId != null) {
            parent = controlAreaService.getControlArea(controlAreaId);
            filter = new ForControlAreaFilter(controlAreaId);
        } else if(scenarioId != null){
            parent = scenarioService.getScenario(scenarioId);
            filter = new ForScenarioFilter(scenarioId);
        } else {
            modelMap.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendDisableProgramsConfirm.invalidParent");
            modelMap.addAttribute("userMessage", error);
            return "common/userMessage.jsp";
        }
        modelMap.addAttribute("parent", parent);
        
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                     parent,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);
        //Get programs
        SearchResult<DisplayablePao> searchResult = programService.filterPrograms(filter, 
                                                                                  new DisplayablePaoComparator(),
                                                                                  0, 
                                                                                  Integer.MAX_VALUE, 
                                                                                  userContext);
        List<DisplayablePao> programs = searchResult.getResultList();
        if (programs == null || programs.size() == 0) {
            modelMap.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendDisableProgramsConfirm.noPrograms." +
                                                (controlAreaId != null ? "controlArea" : "scenario"));
            modelMap.addAttribute("userMessage", error);
            return "common/userMessage.jsp";
        }
        modelMap.addAttribute("programs", programs);
        
        //get current gears and statuses
        List<String> gears = Lists.newArrayListWithCapacity(programs.size());
        List<ProgramState> states = Lists.newArrayListWithCapacity(programs.size());
        for(DisplayablePao program : programs) {
            DatedObject<LMProgramBase> datedObject = programService.findDatedProgram(program.getPaoIdentifier().getPaoId());
            LMProgramBase programBase = datedObject.getObject();
            LMProgramDirectGear gear = null;
            
            ProgramState state = programBase.getProgramState();
            states.add(state);
            
            if (programBase instanceof IGearProgram) {
                gear = ((IGearProgram) programBase).getCurrentGear();
            }
            if (gear != null) {
                gears.add(gear.getGearName());
            } else {
                MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                String dashes = messageSourceAccessor.getMessage("");
                gears.add(dashes);
            }
        }
        modelMap.addAttribute("states", states);
        modelMap.addAttribute("gears", gears);
        
        return "dr/program/sendEnableDisableProgramsConfirm.jsp";
    }
    
    @RequestMapping
    public @ResponseBody JSONObject enableDisablePrograms(HttpServletResponse resp, ModelMap modelMap, HttpServletRequest request, FlashScope flashScope,
                      Boolean supressRestoration, boolean enable) throws IOException {
        
        String[] programIds = request.getParameterValues("disableProgram");
        
        JSONObject json = new JSONObject();
        json.put("action", "reload");
        
        if(programIds == null) {
            YukonMessageSourceResolvable message = new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendDisableProgramsConfirm.noProgramsSelected");
            flashScope.setError(message);

            return json;
        }
        
        for(String programIdString : programIds) {
            int programId = Integer.parseInt(programIdString);
            
            if(!enable && (supressRestoration != null && supressRestoration)) {
                programService.disableAndSupressRestoration(programId);
            } else {
                programService.setEnabled(programId, enable);
            }
        }
        
        YukonMessageSourceResolvable message;
        if(enable) {
            message = new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendEnableProgramsConfirm.programsEnabledConfirmation", programIds.length);
        } else {
            message = new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendDisableProgramsConfirm.programsDisabledConfirmation", programIds.length);
        }
        flashScope.setConfirm(message);
        
        return json;
    }
    
    private void addFilterErrorsToFlashScopeIfNecessary(ModelMap model,
            BindingResult bindingResult, FlashScope flashScope) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("hasFilterErrors", true);
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programControllerHelper.initBinder(binder, userContext, "programList");
        loadGroupControllerHelper.initBinder(binder, userContext);
    }
}
