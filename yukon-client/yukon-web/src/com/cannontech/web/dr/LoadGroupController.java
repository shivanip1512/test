package com.cannontech.web.dr;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.DisplayableApplianceWithRuntime;
import com.cannontech.dr.assetavailability.SimpleAssetAvailability;
import com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.dr.loadgroup.filter.LoadGroupsForMacroLoadGroupFilter;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.chart.service.AssetAvailabilityChartService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
public class LoadGroupController {
    
    @Autowired private LoadGroupService loadGroupService;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private ProgramService programService;
    @Autowired private LoadGroupControllerHelper loadGroupControllerHelper;
    @Autowired private DemandResponseEventLogService demandResponseEventLogService;
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

    private final static Map<Integer, String> shedTimeOptions;
    static {
        // TODO:  make this immutable...can we update google collections so
        // we can use ImmutableSortedMap.Builder?
        shedTimeOptions = new TreeMap<Integer, String>();
        // TODO:  localize
        shedTimeOptions.put(60 * 5, "5 minutes");
        shedTimeOptions.put(60 * 7, "7 minutes");
        shedTimeOptions.put(60 * 10, "10 minutes");
        shedTimeOptions.put(60 * 15, "15 minutes");
        shedTimeOptions.put(60 * 20, "20 minutes");
        shedTimeOptions.put(60 * 30, "30 minutes");
        shedTimeOptions.put(60 * 45, "45 minutes");
        shedTimeOptions.put(60 * 60 * 1, "1 hour");
        shedTimeOptions.put(60 * 60 * 2, "2 hours");
        shedTimeOptions.put(60 * 60 * 3, "3 hours");
        shedTimeOptions.put(60 * 60 * 4, "4 hours");
        shedTimeOptions.put(60 * 60 * 6, "6 hours");
        shedTimeOptions.put(60 * 60 * 8, "8 hours");
    }

    @RequestMapping("/loadGroup/list")
    public String list(ModelMap model,
            @ModelAttribute("backingBean") LoadGroupControllerHelper.LoadGroupListBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope) {
        loadGroupControllerHelper.filterGroups(model, userContext, backingBean,
                                               bindingResult, null, flashScope);
        return "dr/loadGroup/list.jsp";
    }    

    @RequestMapping("/loadGroup/detail")
    public String detail(int loadGroupId, ModelMap model,
            @ModelAttribute("backingBean") LoadGroupControllerHelper.LoadGroupListBackingBean backingBean,
            BindingResult bindingResult, FlashScope flashScope, YukonUserContext userContext) {
        
        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE);
        favoritesDao.detailPageViewed(loadGroupId);
        model.addAttribute("loadGroup", loadGroup);
        boolean isFavorite =
            favoritesDao.isFavorite(loadGroupId, userContext.getYukonUser());
        model.addAttribute("isFavorite", isFavorite);
        model.addAttribute("parentPrograms",
                              programService.findProgramsForLoadGroup(loadGroupId, userContext));
        model.addAttribute("parentLoadGroups",
                              loadGroupService.findLoadGroupsForMacroLoadGroup(loadGroupId, userContext));

        UiFilter<DisplayablePao> detailFilter =
            new LoadGroupsForMacroLoadGroupFilter(loadGroupId);
        loadGroupControllerHelper.filterGroups(model, userContext, backingBean,
                                               bindingResult, detailFilter, flashScope);
        
        try {
            SimpleAssetAvailabilitySummary aaSummary = 
                    assetAvailabilityService.getAssetAvailabilityFromDrGroup(loadGroup.getPaoIdentifier());
            model.addAttribute("assetAvailabilitySummary", aaSummary);
            model.addAttribute("assetTotal", aaSummary.getAll().size());
            model.addAttribute("pieJSONData", assetAvailabilityChartService.getJSONPieData(aaSummary, userContext));
        } catch(DynamicDataAccessException e) {
            model.addAttribute("dispatchDisconnected", true);
        }

        return "dr/loadGroup/detail.jsp";
    }

    @RequestMapping("/loadGroup/assetDetails")
    public String assetDetails(@RequestParam(defaultValue=ITEMS_PER_PAGE) int itemsPerPage, 
                               @RequestParam(defaultValue="1") int page,
                               int assetId, 
                               ModelMap model, 
                               YukonUserContext context) {
        
        DisplayablePao loadGroup = loadGroupService.getLoadGroup(assetId);

        List<AssetAvailabilityDetails> resultsList = getResultsList(assetId, context, null);
        
        SearchResult<AssetAvailabilityDetails> result = 
                SearchResult.pageBasedForWholeList(page, itemsPerPage, resultsList);

        try {
            SimpleAssetAvailabilitySummary aaSummary = 
                    assetAvailabilityService.getAssetAvailabilityFromDrGroup(loadGroup.getPaoIdentifier());
            model.addAttribute("assetAvailabilitySummary", aaSummary);
            model.addAttribute("pieJSONData", assetAvailabilityChartService.getJSONPieData(aaSummary, context));
        } catch(DynamicDataAccessException e) {
            model.addAttribute("dispatchDisconnected", true);
        }
        
        model.addAttribute("assetId", assetId);
        model.addAttribute("loadGroupId", assetId);
        model.addAttribute("loadGroup", loadGroup);
        model.addAttribute("type", "loadGroup");
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
        
        DisplayablePao loadGroup = loadGroupService.getLoadGroup(assetId);
        paoAuthorizationService.verifyAllPermissions(context.getYukonUser(), loadGroup, Permission.LM_VISIBLE);
        
        Map<Integer, SimpleAssetAvailability> resultMap = 
                assetAvailabilityService.getAssetAvailability(loadGroup.getPaoIdentifier());
        List<AssetAvailabilityDetails> resultList = new ArrayList<>(resultMap.size());

        // Create set of applianceCategoryId's (& eliminates duplicates)
        Set<Integer> applianceCatIds = Sets.newHashSet();
        for (SimpleAssetAvailability entry : resultMap.values()) {
            ImmutableSet<DisplayableApplianceWithRuntime> appRuntime = entry.getApplianceRuntimes();
            for (DisplayableApplianceWithRuntime dispAppRun : appRuntime) {
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
            ImmutableSet<DisplayableApplianceWithRuntime> appRuntime = value.getApplianceRuntimes();
            Iterator<DisplayableApplianceWithRuntime> iter = appRuntime.iterator();
            String applianceStr = "";
            Instant lastRun = null;
            while (iter.hasNext()) {
                DisplayableApplianceWithRuntime tmp = (DisplayableApplianceWithRuntime) iter.next();
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
    @RequestMapping("/loadGroup/page")
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


    @RequestMapping("/loadGroup/sendShedConfirm")
    public String sendShedConfirm(ModelMap modelMap, int loadGroupId,
            YukonUserContext userContext) {

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);

        modelMap.addAttribute("loadGroup", loadGroup);
        modelMap.addAttribute("shedTimeOptions", shedTimeOptions);
        return "dr/loadGroup/sendShedConfirm.jsp";
    }

    @RequestMapping("/loadGroup/sendShed")
    public @ResponseBody JSONObject sendShed(HttpServletResponse resp, ModelMap modelMap, int loadGroupId,
            int durationInSeconds, YukonUserContext userContext,
            FlashScope flashScope) throws IOException {

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        loadGroupService.sendShed(loadGroupId, durationInSeconds);
        
        demandResponseEventLogService.threeTierLoadGroupShed(yukonUser, 
                                                             loadGroup.getName(), 
                                                             durationInSeconds);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.loadGroup.sendShedConfirm.shedSent"));
        
        JSONObject json = new JSONObject();
        json.put("action", "reload");
        
        return json;
    }
    
    @RequestMapping("/loadGroup/sendRestoreConfirm")
    public String sendRestoreConfirm(ModelMap modelMap, int loadGroupId,
            YukonUserContext userContext) {

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);

        modelMap.addAttribute("loadGroup", loadGroup);
        return "dr/loadGroup/sendRestoreConfirm.jsp";
    }

    @RequestMapping("/loadGroup/sendRestore")
    public @ResponseBody JSONObject sendRestore(HttpServletResponse resp, ModelMap modelMap, int loadGroupId, YukonUserContext userContext,
                              FlashScope flashScope) throws IOException {

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        loadGroupService.sendRestore(loadGroupId);
        
        demandResponseEventLogService.threeTierLoadGroupRestore(yukonUser, loadGroup.getName());
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.loadGroup.sendRestoreConfirm.restoreSent"));
        
        JSONObject json = new JSONObject();
        json.put("action", "reload");
        
        return json;
    }

    @RequestMapping("/loadGroup/sendEnableConfirm")
    public String sendEnableConfirm(ModelMap modelMap, int loadGroupId, boolean isEnabled,
            YukonUserContext userContext) {
        
        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        modelMap.addAttribute("loadGroup", loadGroup);
        modelMap.addAttribute("isEnabled", isEnabled);
        return "dr/loadGroup/sendEnableConfirm.jsp";
    }
    
    @RequestMapping("/loadGroup/setEnabled")
    public @ResponseBody JSONObject setEnabled(HttpServletResponse resp, ModelMap modelMap, int loadGroupId,
                             boolean isEnabled, YukonUserContext userContext,
                             FlashScope flashScope) throws IOException {

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);

        loadGroupService.setEnabled(loadGroupId, isEnabled);

        if (isEnabled) {
            demandResponseEventLogService.threeTierLoadGroupEnabled(yukonUser, loadGroup.getName());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.loadGroup.sendEnableConfirm.enabled"));
        } else {
            demandResponseEventLogService.threeTierLoadGroupDisabled(yukonUser, loadGroup.getName());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.loadGroup.sendEnableConfirm.disabled"));
        }

        JSONObject json = new JSONObject();
        json.put("action", "reload");
        
        return json;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver =
                new YukonMessageCodeResolver("yukon.web.modules.dr.loadGroup.");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
        loadGroupControllerHelper.initBinder(binder, userContext);
    }
}
