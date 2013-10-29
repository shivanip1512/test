package com.cannontech.web.dr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
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
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityPingService;
import com.cannontech.dr.filter.AuthorizedFilter;
import com.cannontech.dr.filter.NameFilter;
import com.cannontech.dr.program.filter.ForScenarioFilter;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.service.ScenarioService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.dr.ProgramControllerHelper.ProgramListBackingBean;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.ListBackingBean;
import com.cannontech.web.util.WebFileUtils;

@Controller
@CheckRoleProperty(value={YukonRoleProperty.SHOW_SCENARIOS,YukonRoleProperty.DEMAND_RESPONSE}, requireAll=true)
public class ScenarioController extends DemandResponseControllerBase {
    
    @Autowired private AssetAvailabilityPingService assetAvailabilityPingService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private ProgramControllerHelper programControllerHelper;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ScenarioDao scenarioDao;
    @Autowired private ScenarioService scenarioService;


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
        SearchResults<DisplayablePao> searchResult =
            scenarioService.filterScenarios(userContext, filter, sorter, startIndex,
                                            backingBean.getItemsPerPage());

        model.addAttribute("searchResult", searchResult);
        model.addAttribute("scenarios", searchResult.getResultList());

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
        model.addAttribute("scenario", scenario);

        UiFilter<DisplayablePao> detailFilter = new ForScenarioFilter(scenarioId);
        programControllerHelper.filterPrograms(model, userContext, backingBean,
                                               bindingResult, detailFilter);

        addFilterErrorsToFlashScopeIfNecessary(model, bindingResult, flashScope);
        
        model = getAssetAvailabilityInfo(scenario, model, userContext);
        
        return "dr/scenario/detail.jsp";
    }

    @RequestMapping("/scenario/assetDetails")
    public String assetDetails(@RequestParam(defaultValue=ITEMS_PER_PAGE) int itemsPerPage, 
                               @RequestParam(defaultValue="1") int page,
                               @RequestParam(defaultValue="SERIAL_NUM") AssetDetailsColumn sortBy,
                               final boolean descending,
                               int assetId, 
                               ModelMap model, 
                               YukonUserContext userContext) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY, userContext.getYukonUser());
        DisplayablePao scenario = scenarioService.getScenario(assetId);

        List<AssetAvailabilityDetails> resultsList = getResultsList(scenario, userContext, null);
        sortAssetDetails(resultsList, sortBy, descending, userContext);
        
        if (itemsPerPage > CtiUtilities.MAX_ITEMS_PER_PAGE) {
            // Limit the maximum items per page
            itemsPerPage = CtiUtilities.MAX_ITEMS_PER_PAGE;
        }
        SearchResults<AssetAvailabilityDetails> result = 
                SearchResults.pageBasedForWholeList(page, itemsPerPage, resultsList);

        model = getAssetAvailabilityInfo(scenario, model, userContext);
        
        model.addAttribute("assetId", assetId);
        model.addAttribute("scenarioId", assetId);
        model.addAttribute("scenario", scenario);
        model.addAttribute("type", "scenario");
        model.addAttribute("result", result);
        model.addAttribute("itemsPerPage", itemsPerPage);
        model.addAttribute("assetDetailsSortBy", sortBy);
        model.addAttribute("assetDetailsSortDesc", descending);
        
        return "dr/assetDetails.jsp";
    }

    @ResponseBody
    @RequestMapping("/scenario/pingDevices")
    public void pingDevices(int assetId, LiteYukonUser user) {
        DisplayablePao controlArea = scenarioService.getScenario(assetId);
        assetAvailabilityPingService.readDevicesInDrGrouping(controlArea.getPaoIdentifier(), user);
    }

    /**
     * Used for paging and filtering operations.
     */
    @RequestMapping("/scenario/page")
    public String page(ModelMap model, 
                       YukonUserContext userContext,
                       String type,
                       String assetId,
                       @RequestParam(defaultValue="SERIAL_NUM") AssetDetailsColumn sortBy,
                       final boolean descending,
                       @RequestParam(defaultValue=ITEMS_PER_PAGE) int itemsPerPage, 
                       @RequestParam(defaultValue="1") int page,
                       String filter) {

        JSONArray filters = (filter == null || filter.length() == 0) ? null : JSONArray.fromObject(filter);

        DisplayablePao scenario = scenarioService.getScenario(Integer.parseInt(assetId));
        List<AssetAvailabilityDetails> resultsList = getResultsList(scenario, userContext, filters);
        sortAssetDetails(resultsList, sortBy, descending, userContext);

        if (itemsPerPage > CtiUtilities.MAX_ITEMS_PER_PAGE) {
            // Limit the maximum items per page
            itemsPerPage = CtiUtilities.MAX_ITEMS_PER_PAGE;
        }
        SearchResults<AssetAvailabilityDetails> result = 
                SearchResults.pageBasedForWholeList(page, itemsPerPage, resultsList);
        
        model.addAttribute("result", result);
        model.addAttribute("type", type);
        model.addAttribute("assetId", assetId);
        model.addAttribute("colorMap", colorMap);
        model.addAttribute("itemsPerPage", itemsPerPage);
        model.addAttribute("assetDetailsSortBy", sortBy);
        model.addAttribute("assetDetailsSortDesc", descending);
        
        return "dr/assetTable.jsp";
    }

    @RequestMapping("/scenario/downloadToCsv")
    public void downloadToCsv(String assetId,
                              String filter,
                              String type,
                              HttpServletRequest request,
                              HttpServletResponse response,
                              YukonUserContext userContext) throws IOException {
        
        DisplayablePao scenario = scenarioService.getScenario(Integer.parseInt(assetId));

        // get the header row
        String[] headerRow = getDownloadHeaderRow(userContext);

        // get the data rows
        List<String[]> dataRows = getDownloadDataRows(scenario, filter, request, response, userContext);
        
        String dateStr = dateFormattingService.format(new LocalDateTime(userContext.getJodaTimeZone()), 
                                                      DateFormatEnum.BOTH, userContext);
        String fileName = type + "_" + scenario.getName() + "_" + dateStr + ".csv";
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
        programControllerHelper.initBinder(binder, userContext, "programList");
    }
}
