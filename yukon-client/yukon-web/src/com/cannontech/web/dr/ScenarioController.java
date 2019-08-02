package com.cannontech.web.dr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
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
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityPingService;
import com.cannontech.dr.filter.AuthorizedFilter;
import com.cannontech.dr.filter.NameFilter;
import com.cannontech.dr.program.filter.ForScenarioFilter;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.service.ScenarioService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.dr.ProgramsHelper.ProgramFilter;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;

@Controller
@CheckRoleProperty(YukonRoleProperty.SHOW_SCENARIOS)
public class ScenarioController extends DemandResponseControllerBase {
    
    @Autowired private AssetAvailabilityPingService assetAvailabilityPingService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private ProgramsHelper programsHelper;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ScenarioDao scenarioDao;
    @Autowired private ScenarioService scenarioService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    @RequestMapping(value = "/scenario/list", method = RequestMethod.GET)
    public String list(ModelMap model,
            @DefaultItemsPerPage(25) PagingParameters paging,
            String name,
            YukonUserContext userContext) {
        
        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();

        filters.add(new AuthorizedFilter<DisplayablePao>(paoAuthorizationService, 
                                         userContext.getYukonUser(),
                                         Permission.LM_VISIBLE));
        
        if (StringUtils.isNotEmpty(name)) {
            filters.add(new NameFilter(name));
        }
        
        // Sorting - name is default sorter
        Comparator<DisplayablePao> sorter = new DisplayablePaoComparator();
        UiFilter<DisplayablePao> uifilter = UiFilterList.wrap(filters);
        SearchResults<DisplayablePao> scenarios =
            scenarioService.filterScenarios(userContext, uifilter, sorter, 
                    paging.getStartIndex(), paging.getItemsPerPage());
        
        model.addAttribute("scenarios", scenarios);
        model.addAttribute("paging", paging);
        model.addAttribute("name", name);
        
        return "dr/scenario/list.jsp";
    }

    @RequestMapping(value = "/scenario/detail", method = RequestMethod.GET)
    public String detail(int scenarioId, ModelMap model,
            @ModelAttribute("filter") ProgramFilter filter,
            BindingResult bindingResult, YukonUserContext userContext,
            @DefaultItemsPerPage(25) PagingParameters paging,
            @DefaultSort(dir=Direction.asc, sort="NAME") SortingParameters sorting,
            FlashScope flashScope) {
        
        DisplayablePao scenario = scenarioDao.getScenario(scenarioId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), scenario, Permission.LM_VISIBLE);
        model.addAttribute("scenario", scenario);

        boolean changeGearAllowed = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHANGE_GEARS,
                userContext.getYukonUser());
        model.addAttribute("changeGearAllowed", changeGearAllowed);
        boolean enableDisableProgramsAllowed = rolePropertyDao.checkProperty(YukonRoleProperty.ENABLE_DISABLE_PROGRAM,
                userContext.getYukonUser());
        model.addAttribute("enableDisableProgramsAllowed", enableDisableProgramsAllowed);
        
        UiFilter<DisplayablePao> detailFilter = new ForScenarioFilter(scenarioId);
        programsHelper.filterPrograms(model, userContext, filter, bindingResult, detailFilter, sorting, paging);

        addFilterErrorsToFlashScopeIfNecessary(model, bindingResult, flashScope);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        programsHelper.addColumns(model, accessor, sorting);
        
        return "dr/scenario/detail.jsp";
    }
    
    @RequestMapping("/scenario/assetAvailability")
    public String assetAvailability(ModelMap model, YukonUserContext userContext, int paoId) {
        model.addAttribute("paoId", paoId);
        DisplayablePao scenario = scenarioDao.getScenario(paoId);
        if(rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY, userContext.getYukonUser())) {
            getAssetAvailabilityInfo(scenario, model, userContext);
        }
        return "dr/assetAvailability.jsp";
    }

    @ResponseBody
    @RequestMapping("/scenario/pingDevices")
    public void pingDevices(int assetId, LiteYukonUser user) {
        DisplayablePao controlArea = scenarioService.getScenario(assetId);
        assetAvailabilityPingService.readDevicesInDrGrouping(controlArea.getPaoIdentifier(), user);
    }

    @RequestMapping("/scenario/{id}/aa/download/{type}")
    public void downloadAssetAvailability(HttpServletResponse response, 
            YukonUserContext userContext, 
            @PathVariable int id, 
            @PathVariable String type) 
    throws IOException {
        
        List<AssetAvailabilityCombinedStatus> filters = getAssetAvailabilityFilters(type);
        
        downloadAssetAvailability(id, userContext, filters.toArray(new AssetAvailabilityCombinedStatus[]{}), response);
    }
    
    @RequestMapping("/scenario/downloadToCsv")
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
        
        DisplayablePao scenario = scenarioService.getScenario(assetId);
        
        // get the header row
        String[] headerRow = getDownloadHeaderRow(userContext);
        // get the data rows
        List<String[]> dataRows = getDownloadDataRows(scenario, filters, userContext);
        
        String dateStr = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        String fileName = "Scenario_" + scenario.getName() + "_" + dateStr + ".csv";
        WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
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
    }
}
