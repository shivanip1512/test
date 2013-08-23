package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
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

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.ApplianceWithRuntime;
import com.cannontech.dr.assetavailability.SimpleAssetAvailability;
import com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.dr.filter.AuthorizedFilter;
import com.cannontech.dr.filter.NameFilter;
import com.cannontech.dr.program.filter.ForScenarioFilter;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.service.ScenarioService;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.chart.service.AssetAvailabilityChartService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.dr.ProgramControllerHelper.ProgramListBackingBean;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@Controller
@CheckRoleProperty(value={YukonRoleProperty.SHOW_SCENARIOS,YukonRoleProperty.DEMAND_RESPONSE}, requireAll=true)
public class ScenarioController {
    
    @Autowired private ScenarioDao scenarioDao;
    @Autowired private ScenarioService scenarioService;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private ProgramControllerHelper programControllerHelper;
    @Autowired private FavoritesDao favoritesDao;
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    @Autowired private AssetAvailabilityChartService assetAvailabilityChartService;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private InventoryDao inventoryDao;

    private static Map<AssetAvailabilityCombinedStatus, String> colorMap;
    static {
        colorMap = new HashMap<>();
        colorMap.put(AssetAvailabilityCombinedStatus.RUNNING, "green");
        colorMap.put(AssetAvailabilityCombinedStatus.NOT_RUNNING, "orange");
        colorMap.put(AssetAvailabilityCombinedStatus.UNAVAILABLE, "red");
        colorMap.put(AssetAvailabilityCombinedStatus.OPTED_OUT, "grey");
    }
    private static final String ITEMS_PER_PAGE = "10";

    @RequestMapping("/scenario/list")
    public String list(ModelMap model,
            @ModelAttribute("backingBean") ListBackingBean backingBean,
            BindingResult bindingResult, FlashScope flashScope,
            YukonUserContext userContext) {
        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();

        filters.add(new AuthorizedFilter<DisplayablePao>(paoAuthorizationService, 
                                         userContext.getYukonUser(),
                                         Permission.LM_VISIBLE));

        boolean isFiltered = false;
        if (!StringUtils.isEmpty(backingBean.getName())) {
            filters.add(new NameFilter(backingBean.getName()));
            isFiltered = true;
        }
        model.addAttribute("isFiltered", isFiltered);

        // Sorting - name is default sorter
        Comparator<DisplayablePao> sorter = new DisplayablePaoComparator();
        if(backingBean.getDescending()) {
            sorter = Collections.reverseOrder(sorter);
        }
        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);
        int startIndex = (backingBean.getPage() - 1) * backingBean.getItemsPerPage();
        SearchResult<DisplayablePao> searchResult =
            scenarioService.filterScenarios(userContext, filter, sorter, startIndex,
                                            backingBean.getItemsPerPage());

        model.addAttribute("searchResult", searchResult);
        model.addAttribute("scenarios", searchResult.getResultList());
        Map<Integer, Boolean> favoritesByPaoId =
            favoritesDao.favoritesByPao(searchResult.getResultList(),
                                        userContext.getYukonUser());
        model.addAttribute("favoritesByPaoId", favoritesByPaoId);

        addFilterErrorsToFlashScopeIfNecessary(model, bindingResult, flashScope);

        return "dr/scenario/list.jsp";
    }

    @RequestMapping("/scenario/detail")
    public String detail(int scenarioId, ModelMap model,
            @ModelAttribute("backingBean") ProgramListBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope) {
        
        DisplayablePao scenario = scenarioDao.getScenario(scenarioId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     scenario, 
                                                     Permission.LM_VISIBLE);
        favoritesDao.detailPageViewed(scenarioId);
        model.addAttribute("scenario", scenario);
        boolean isFavorite =
            favoritesDao.isFavorite(scenarioId, userContext.getYukonUser());
        model.addAttribute("isFavorite", isFavorite);

        UiFilter<DisplayablePao> detailFilter = new ForScenarioFilter(scenarioId);
        programControllerHelper.filterPrograms(model, userContext, backingBean,
                                               bindingResult, detailFilter);

        addFilterErrorsToFlashScopeIfNecessary(model, bindingResult, flashScope);
        
        try {
            SimpleAssetAvailabilitySummary aaSummary = 
                    assetAvailabilityService.getAssetAvailabilityFromDrGroup(scenario.getPaoIdentifier());
            model.addAttribute("assetAvailabilitySummary", aaSummary);
            model.addAttribute("assetTotal", aaSummary.getAll().size());
            model.addAttribute("pieJSONData", assetAvailabilityChartService.getJSONPieData(aaSummary, userContext));
        } catch(DynamicDataAccessException e) {
            model.addAttribute("dispatchDisconnected", true);
        }
        
        return "dr/scenario/detail.jsp";
    }

    @RequestMapping("/scenario/assetDetails")
    public String assetDetails(@RequestParam(defaultValue=ITEMS_PER_PAGE) int itemsPerPage, 
                               @RequestParam(defaultValue="1") int page,
                               int assetId, 
                               ModelMap model, 
                               YukonUserContext context) {
        
        DisplayablePao scenario = scenarioService.getScenario(assetId);

        List<AssetAvailabilityDetails> resultsList = getResultsList(assetId, context, null);
        
        SearchResult<AssetAvailabilityDetails> result = 
                SearchResult.pageBasedForWholeList(page, itemsPerPage, resultsList);

        try {
            SimpleAssetAvailabilitySummary aaSummary = 
                    assetAvailabilityService.getAssetAvailabilityFromDrGroup(scenario.getPaoIdentifier());
            model.addAttribute("assetAvailabilitySummary", aaSummary);
            model.addAttribute("pieJSONData", assetAvailabilityChartService.getJSONPieData(aaSummary, context));
        } catch(DynamicDataAccessException e) {
            model.addAttribute("dispatchDisconnected", true);
        }
        
        model.addAttribute("assetId", assetId);
        model.addAttribute("scenarioId", assetId);
        model.addAttribute("scenario", scenario);
        model.addAttribute("type", "scenario");
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
        
        DisplayablePao scenario = scenarioService.getScenario(assetId);
        paoAuthorizationService.verifyAllPermissions(context.getYukonUser(), scenario, Permission.LM_VISIBLE);
        
        Map<Integer, SimpleAssetAvailability> resultMap = 
                assetAvailabilityService.getAssetAvailability(scenario.getPaoIdentifier());
        List<AssetAvailabilityDetails> resultList = new ArrayList<>(resultMap.size());

        // Create set of applianceCategoryId's (& eliminates duplicates)
        Set<Integer> applianceCatIds = Sets.newHashSet();
        for (SimpleAssetAvailability entry : resultMap.values()) {
            ImmutableSet<ApplianceWithRuntime> appRuntime = entry.getApplianceRuntimes();
            for (ApplianceWithRuntime dispAppRun : appRuntime) {
                applianceCatIds.add(dispAppRun.getApplianceCategoryId());
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
    @RequestMapping("/scenario/page")
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
    }
}
