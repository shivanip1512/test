package com.cannontech.web.dr;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityPingService;
import com.cannontech.dr.loadgroup.filter.LoadGroupsForMacroLoadGroupFilter;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
public class LoadGroupController extends DemandResponseControllerBase {
    
    @Autowired private AssetAvailabilityPingService assetAvailabilityPingService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DemandResponseEventLogService demandResponseEventLogService;
    @Autowired private LoadGroupControllerHelper loadGroupControllerHelper;
    @Autowired private LoadGroupService loadGroupService;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private ProgramService programService;
    @Autowired private RolePropertyDao rolePropertyDao;

    
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
    public String detail(int loadGroupId, ModelMap model, LiteYukonUser user,
            @ModelAttribute("backingBean") LoadGroupControllerHelper.LoadGroupListBackingBean backingBean,
            BindingResult bindingResult, FlashScope flashScope, YukonUserContext userContext) {
        
        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        paoAuthorizationService.verifyAllPermissions(user, loadGroup, Permission.LM_VISIBLE);
        model.addAttribute("loadGroup", loadGroup);

        model.addAttribute("parentPrograms",
                              programService.findProgramsForLoadGroup(loadGroupId, userContext));
        model.addAttribute("parentLoadGroups",
                              loadGroupService.findLoadGroupsForMacroLoadGroup(loadGroupId, userContext));

        UiFilter<DisplayablePao> detailFilter = new LoadGroupsForMacroLoadGroupFilter(loadGroupId);
        loadGroupControllerHelper.filterGroups(model, userContext, backingBean,
                                               bindingResult, detailFilter, flashScope);

        return "dr/loadGroup/detail.jsp";
    }
    
    @RequestMapping("/loadGroup/assetAvailability")
    public String assetAvailability(ModelMap model, YukonUserContext userContext, int paoId) {
        model.addAttribute("paoId", paoId);
        DisplayablePao loadGroup = loadGroupService.getLoadGroup(paoId);
        if(rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY, userContext.getYukonUser())) {
            getAssetAvailabilityInfo(loadGroup, model, userContext);
        }
        return "dr/assetAvailability.jsp";
    }
    
    @RequestMapping("/loadGroup/assetDetails")
    public String assetDetails(@RequestParam(defaultValue=ITEMS_PER_PAGE) int itemsPerPage, 
                               @RequestParam(defaultValue="1") int page,
                               @RequestParam(defaultValue="SERIAL_NUM") AssetDetailsColumn sortBy,
                               final boolean descending,
                               int assetId, 
                               ModelMap model, 
                               YukonUserContext userContext) throws IOException {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY, userContext.getYukonUser());
        DisplayablePao loadGroup = loadGroupService.getLoadGroup(assetId);

        List<AssetAvailabilityDetails> resultsList = getResultsList(loadGroup, userContext, null);
        sortAssetDetails(resultsList, sortBy, descending, userContext);
        
        itemsPerPage = CtiUtilities.itemsPerPage(itemsPerPage);
        SearchResults<AssetAvailabilityDetails> result = 
                SearchResults.pageBasedForWholeList(page, itemsPerPage, resultsList);

        model = getAssetAvailabilityInfo(loadGroup, model, userContext);
        
        model.addAttribute("assetId", assetId);
        model.addAttribute("loadGroupId", assetId);
        model.addAttribute("loadGroup", loadGroup);
        model.addAttribute("type", "loadGroup");
        model.addAttribute("result", result);
        model.addAttribute("itemsPerPage", itemsPerPage);
        
        return "dr/assetDetails.jsp";
    }

    @ResponseBody
    @RequestMapping("/loadGroup/pingDevices")
    public void pingDevices(int assetId, LiteYukonUser user) {
        DisplayablePao controlArea = loadGroupService.getLoadGroup(assetId);
        assetAvailabilityPingService.readDevicesInDrGrouping(controlArea.getPaoIdentifier(), user);
    }

    /**
     * Used for paging and filtering operations.
     */
    @RequestMapping("/loadGroup/page")
    public String page(ModelMap model, 
                       YukonUserContext userContext,
                       String assetId,
                       @RequestParam(defaultValue="SERIAL_NUM") AssetDetailsColumn sortBy,
                       final boolean descending,
                       @RequestParam(defaultValue=ITEMS_PER_PAGE) int itemsPerPage, 
                       @RequestParam(defaultValue="1") int page,
                       @RequestParam(value="filter[]", required=false) AssetAvailabilityCombinedStatus[] filters) {

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(Integer.parseInt(assetId));
        List<AssetAvailabilityDetails> resultsList = getResultsList(loadGroup, userContext, filters);
        sortAssetDetails(resultsList, sortBy, descending, userContext);

        itemsPerPage = CtiUtilities.itemsPerPage(itemsPerPage);
        SearchResults<AssetAvailabilityDetails> result = 
                SearchResults.pageBasedForWholeList(page, itemsPerPage, resultsList);
        
        model.addAttribute("result", result);
        model.addAttribute("type", "loadGroup");
        model.addAttribute("assetId", assetId);
        model.addAttribute("colorMap", colorMap);
        model.addAttribute("itemsPerPage", itemsPerPage);
        
        return "dr/assetTable.jsp";
    }

    @RequestMapping("/loadGroup/{id}/aa/download/{type}")
    public void downloadAssetAvailability(HttpServletResponse response, 
            YukonUserContext userContext, 
            @PathVariable int id, 
            @PathVariable String type) 
    throws IOException {
        
        List<AssetAvailabilityCombinedStatus> filters = getAssetAvailabilityFilters(type);
        
        downloadAssetAvailability(id, userContext, filters.toArray(new AssetAvailabilityCombinedStatus[]{}), response);
    }
    
    @RequestMapping("/loadGroup/downloadToCsv")
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
        
        DisplayablePao loadGroup = loadGroupService.getLoadGroup(assetId);
        
        // get the header row
        String[] headerRow = getDownloadHeaderRow(userContext);
        // get the data rows
        List<String[]> dataRows = getDownloadDataRows(loadGroup, filters, userContext);
        
        String dateStr = dateFormattingService.format(new LocalDateTime(userContext.getJodaTimeZone()), 
                DateFormatEnum.BOTH, userContext);
        String fileName = "loadGroup_" + loadGroup.getName() + "_" + dateStr + ".csv";
        WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
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
    public @ResponseBody Map<String, String> sendShed(int loadGroupId, int durationInSeconds,
                                                      YukonUserContext userContext, FlashScope flashScope) {

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        loadGroupService.sendShed(loadGroupId, durationInSeconds);
        
        demandResponseEventLogService.threeTierLoadGroupShed(yukonUser, loadGroup.getName(), durationInSeconds);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.loadGroup.sendShedConfirm.shedSent"));
        
        return Collections.singletonMap("action", "reload");
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
    public @ResponseBody Map<String, String> sendRestore(int loadGroupId, YukonUserContext userContext, 
                                                         FlashScope flashScope) {

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        loadGroupService.sendRestore(loadGroupId);
        
        demandResponseEventLogService.threeTierLoadGroupRestore(yukonUser, loadGroup.getName());
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.loadGroup.sendRestoreConfirm.restoreSent"));

        return Collections.singletonMap("action", "reload");
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
    public @ResponseBody Map<String, String> setEnabled(int loadGroupId, boolean isEnabled,
                                                        YukonUserContext userContext, FlashScope flashScope) {

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

        return Collections.singletonMap("action", "reload");
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
