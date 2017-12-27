package com.cannontech.web.dr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityPingService;
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
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.dr.LoadGroupHelper.LoadGroupFilter;
import com.cannontech.web.dr.ProgramsHelper.ProgramFilter;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
public class ProgramController extends ProgramControllerBase {

    @Autowired private AssetAvailabilityPingService assetAvailabilityPingService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private LoadGroupHelper loadGroupHelper;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ScenarioService scenarioService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;


    @RequestMapping(value = "/program/list", method = RequestMethod.GET)
    public String list(ModelMap model, YukonUserContext userContext,
            @ModelAttribute("filter") ProgramFilter filter, BindingResult bindingResult,
            @DefaultItemsPerPage(25) PagingParameters paging,
            @DefaultSort(dir=Direction.asc, sort="NAME") SortingParameters sorting,
            FlashScope flashScope) {
        
        programsHelper.filterPrograms(model, userContext, filter, bindingResult, null, sorting, paging);
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        programsHelper.addColumns(model, accessor, sorting);

        addFilterErrorsToFlashScopeIfNecessary(model, bindingResult, flashScope);

        return "dr/program/list.jsp";
    }

    @RequestMapping(value = "/program/detail", method = RequestMethod.GET)
    public String detail(int programId, ModelMap model, LiteYukonUser user,
            @ModelAttribute("filter") LoadGroupFilter filter,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope,
            @DefaultItemsPerPage(25) PagingParameters paging,
            @DefaultSort(dir=Direction.asc, sort="NAME") SortingParameters sorting) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(user, program, Permission.LM_VISIBLE);
        model.addAttribute("program", program);

        boolean changeGearAllowed = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHANGE_GEARS,
                userContext.getYukonUser());
        model.addAttribute("changeGearAllowed", changeGearAllowed);
        boolean enableDisableProgramsAllowed = rolePropertyDao.checkProperty(YukonRoleProperty.ENABLE_DISABLE_PROGRAM,
                userContext.getYukonUser());
        model.addAttribute("enableDisableProgramsAllowed", enableDisableProgramsAllowed);
        
        UiFilter<DisplayablePao> detailFilter = new LoadGroupsForProgramFilter(programId);
        loadGroupHelper.filterGroups(model, userContext, filter,
                                               bindingResult, detailFilter, flashScope,
                                               paging, sorting);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        loadGroupHelper.addColumns(model, accessor, sorting);

        DisplayablePao parentControlArea =
            controlAreaService.findControlAreaForProgram(userContext, programId);
        model.addAttribute("parentControlArea", parentControlArea);
        List<Scenario> parentScenarios = scenarioDao.findScenariosForProgram(programId);
        model.addAttribute("parentScenarios", parentScenarios);
        
        return "dr/program/detail.jsp";
    }
    
    @RequestMapping("/program/assetAvailability")
    public String assetAvailability(ModelMap model, YukonUserContext userContext, int paoId) {
        model.addAttribute("paoId", paoId);
        DisplayablePao program = programService.getProgram(paoId);
        if(rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY, userContext.getYukonUser())) {
            getAssetAvailabilityInfo(program, model, userContext);
        }
        return "dr/assetAvailability.jsp";
    }

    @RequestMapping("/program/assetDetails")
    public String assetDetails(@DefaultItemsPerPage(25) PagingParameters paging,
            @DefaultSort(dir = Direction.asc, sort = "SERIAL_NUM") SortingParameters sorting, int assetId,
            ModelMap model, YukonUserContext userContext) throws IOException {

        rolePropertyDao.verifyProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY, userContext.getYukonUser());
        DisplayablePao program = programService.getProgram(assetId);

        SearchResults<AssetAvailabilityDetails> result = getResultsList(program, userContext, null, paging, sorting);

        getAssetAvailabilityInfo(program, model, userContext);

        model.addAttribute("assetId", assetId);
        model.addAttribute("programId", assetId);
        model.addAttribute("program", program);
        model.addAttribute("type", "program");
        model.addAttribute("result", result);
        model.addAttribute("filter",
            AssetAvailabilityCombinedStatus.getStringValues(AssetAvailabilityCombinedStatus.values()));

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        addAssetColumns(model, accessor, sorting);

        return "dr/assetDetails.jsp";
    }

    @ResponseBody
    @RequestMapping("/program/pingDevices")
    public void pingDevices(int assetId, LiteYukonUser user) {
        DisplayablePao controlArea = programService.getProgram(assetId);
        assetAvailabilityPingService.readDevicesInDrGrouping(controlArea.getPaoIdentifier(), user);
    }

    /**
     * Used for paging and filtering operations.
     * @throws IOException 
     */
    @RequestMapping("/program/page")
    public String page(ModelMap model, YukonUserContext userContext, int assetId,
            @DefaultItemsPerPage(25) PagingParameters paging,
            @DefaultSort(dir = Direction.asc, sort = "SERIAL_NUM") SortingParameters sorting,
            @RequestParam(value = "filter[]", required = false) AssetAvailabilityCombinedStatus[] filters) {

        DisplayablePao program = programService.getProgram(assetId);
        SearchResults<AssetAvailabilityDetails> result = getResultsList(program, userContext, filters, paging, sorting);
        
        model.addAttribute("result", result);
        model.addAttribute("type", "program");
        model.addAttribute("assetId", assetId);
        model.addAttribute("colorMap", colorMap);
        
        model.addAttribute("filter", AssetAvailabilityCombinedStatus.getStringValues(filters));
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        addAssetColumns(model, accessor, sorting);

        return "dr/assetTable.jsp";
    }

    @RequestMapping("/program/{id}/aa/download/{type}")
    public void downloadAssetAvailability(HttpServletResponse response, 
            YukonUserContext userContext, 
            @PathVariable int id, 
            @PathVariable String type) 
    throws IOException {
        
        List<AssetAvailabilityCombinedStatus> filters = getAssetAvailabilityFilters(type);
        
        downloadAssetAvailability(id, userContext, filters.toArray(new AssetAvailabilityCombinedStatus[]{}), response);
    }
    
    @RequestMapping("/program/downloadToCsv")
    public void downloadToCsv(int assetId,
            @RequestParam(value="filter[]", required=false) AssetAvailabilityCombinedStatus[] filters,
            HttpServletResponse response,
            YukonUserContext userContext) throws IOException {
        
        downloadAssetAvailability(assetId, userContext, filters, response);
    }
    
    private void downloadAssetAvailability(int assetId, 
            YukonUserContext userContext, 
            AssetAvailabilityCombinedStatus[] filters, 
            HttpServletResponse response) throws IOException {
        
        DisplayablePao program = programService.getProgram(assetId);
        
        // get the header row
        String[] headerRow = getDownloadHeaderRow(userContext);
        // get the data rows
        List<String[]> dataRows = getDownloadDataRows(program, filters, userContext);
        
        String dateStr = dateFormattingService.format(new LocalDateTime(userContext.getJodaTimeZone()), 
                DateFormatEnum.BOTH, userContext);
        String fileName = "program_" + program.getName() + "_" + dateStr + ".csv";
        WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
    }

    @RequestMapping("/program/getChangeGearValue")
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
    
    @RequestMapping("/program/changeGear")
    public @ResponseBody Map<String, String> changeGear(int programId, int gearNumber, YukonUserContext userContext,
                                                        FlashScope flashScope) {
        
        DisplayablePao program = programService.getProgram(programId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        programService.changeGear(programId, gearNumber);
        
        demandResponseEventLogService.threeTierProgramChangeGear(yukonUser, program.getName());
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.getChangeGearValue.gearChanged"));

        return Collections.singletonMap("action", "reload");
    }
    
    @RequestMapping("/program/changeGearMultiplePopup")
    public String changeGearMultiplePopup(ModelMap model, @ModelAttribute("filter") ChangeMultipleGearsBackingBean filter,
                                          BindingResult bindingResult, YukonUserContext userContext, FlashScope flashScope) {
        UiFilter<DisplayablePao> uifilter = null;

        String paoName = null;
        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        if (filter.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(filter.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         controlArea,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("controlArea", controlArea);
            paoName = controlArea.getName();
            uifilter = new ForControlAreaFilter(filter.getControlAreaId());
        }
        if (filter.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(filter.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         scenario,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("scenario", scenario);
            paoName = scenario.getName();
            uifilter = new ForScenarioFilter(filter.getScenarioId());
            scenarioPrograms =
                scenarioDao.findScenarioProgramsForScenario(filter.getScenarioId());
            model.addAttribute("scenarioPrograms", scenarioPrograms);
        }

        if (uifilter == null) {
            throw new IllegalArgumentException();
        }

        SearchResults<DisplayablePao> searchResult =
            programService.filterPrograms(uifilter, new DisplayablePaoComparator(),
                                          0, Integer.MAX_VALUE, userContext);
        List<DisplayablePao> programs = searchResult.getResultList();
        if (programs == null || programs.size() == 0) {

            model.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.startMultiplePrograms.noPrograms." +
                                                 (filter.getControlAreaId() != null ? "controlArea" : "scenario"),
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
        filter.setProgramGearChangeInfo(programGearChangeInfo);
        
        addGearsToModel(searchResult.getResultList(), model);
        addErrorsToFlashScopeIfNecessary(bindingResult, flashScope);
        
        Map<Integer, Map<Integer, Boolean>> programIndexTargetGearMap = getIndexBasedIsTargetGearMap(programs);

        model.addAttribute("targetGearMap",programIndexTargetGearMap);
      return "dr/program/changeMultipleProgramsGearsDetails.jsp";
  }
    
    @RequestMapping("/program/changeMultipleGears")
    public @ResponseBody Map<String, String> changeMultipleGears(ModelMap model,
                @ModelAttribute("filter") ChangeMultipleGearsBackingBean filter, BindingResult bindingResult,
                YukonUserContext userContext, FlashScope flashScope) {

        if (filter.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(filter.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         controlArea,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("controlArea", controlArea);
        }
        if (filter.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(filter.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         scenario,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("scenario", scenario);
        }
        boolean gearChanged = false;
        for (ProgramGearChangeInfo gearChangeInfo : filter.getProgramGearChangeInfo()) {
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

        return Collections.singletonMap("action", "reload");
    }
    
    @RequestMapping("/program/sendEnableConfirm")
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
    
    @RequestMapping("/program/setEnabled")
    public @ResponseBody Map<String, String> setEnabled(int programId, boolean isEnabled, YukonUserContext userContext,
            Boolean suppressRestoration, FlashScope flashScope) {
        
        DisplayablePao program = programService.getProgram(programId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        if (!isEnabled && (suppressRestoration != null && suppressRestoration)) {
            programService.disableAndSupressRestoration(programId);
        } else {
            programService.setEnabled(programId, isEnabled);
        }

        if(isEnabled) {
            demandResponseEventLogService.threeTierProgramEnabled(yukonUser, program.getName());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendEnableConfirm.enabled"));
        } else {
            demandResponseEventLogService.threeTierProgramDisabled(yukonUser, program.getName());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendEnableConfirm.disabled"));
        }
        
        return Collections.singletonMap("action", "reload");
    }
    
    @RequestMapping("/program/sendEnableDisableProgramsConfirm")
    public String sendEnableDisableProgramsConfirm(ModelMap modelMap, YukonUserContext userContext,
                                             Integer controlAreaId, Integer scenarioId, boolean enable) {
        
        modelMap.addAttribute("enable", enable);
        
        //Determine if parent object is a control area or scenario
        DisplayablePao parent = null;
        UiFilter<DisplayablePao> uifilter = null;
        if(controlAreaId != null) {
            parent = controlAreaService.getControlArea(controlAreaId);
            uifilter = new ForControlAreaFilter(controlAreaId);
        } else if(scenarioId != null){
            parent = scenarioService.getScenario(scenarioId);
            uifilter = new ForScenarioFilter(scenarioId);
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
        SearchResults<DisplayablePao> searchResult = programService.filterPrograms(uifilter, 
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
                MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
                String dashes = messageSourceAccessor.getMessage("");
                gears.add(dashes);
            }
        }
        modelMap.addAttribute("states", states);
        modelMap.addAttribute("gears", gears);
        
        return "dr/program/sendEnableDisableProgramsConfirm.jsp";
    }
    
    @RequestMapping("/program/enableDisablePrograms")
    public @ResponseBody Map<String, String> enableDisablePrograms(HttpServletRequest request, FlashScope flashScope,
                Boolean supressRestoration, boolean enable) {
        
        String[] programIds = request.getParameterValues("disableProgram");
        
        if(programIds == null) {
            YukonMessageSourceResolvable message = new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendDisableProgramsConfirm.noProgramsSelected");
            flashScope.setError(message);

            return Collections.singletonMap("action", "reload");
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
        
        return Collections.singletonMap("action", "reload");
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
        programsHelper.initBinder(binder, userContext, "programList");
        loadGroupHelper.initBinder(binder, userContext);
    }
}
