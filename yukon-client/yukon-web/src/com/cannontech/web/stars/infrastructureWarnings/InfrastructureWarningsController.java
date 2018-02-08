package com.cannontech.web.stars.infrastructureWarnings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.infrastructure.dao.InfrastructureWarningsDao;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningDeviceCategory;
import com.cannontech.infrastructure.model.InfrastructureWarningSummary;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.common.widgets.service.InfrastructureWarningsWidgetService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/infrastructureWarnings/*")
public class InfrastructureWarningsController {
    
    @Autowired private InfrastructureWarningsDao infrastructureWarningsDao;
    @Autowired private InfrastructureWarningsWidgetService widgetService ;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private IDatabaseCache cache;
    @Autowired private DateFormattingService dateFormattingService;

    private final static String baseKey = "yukon.web.widgets.infrastructureWarnings.";
    private final static String widgetKey = "yukon.web.widgets.";

    @RequestMapping("forceUpdate")
    public @ResponseBody Map<String, Object> forceUpdate() {
        Map<String, Object> json = new HashMap<>();
        widgetService.initiateRecalculation();
        json.put("success", true);
        return json;
    }
    
    @RequestMapping(value="updateWidget", method=RequestMethod.GET)
    public String updateWidget(ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        InfrastructureWarningSummary summary = widgetService.getWarningsSummary();
        model.addAttribute("summary", summary);
        List<InfrastructureWarning> warnings = widgetService.getWarnings();
        Comparator<InfrastructureWarning> comparator = (o1, o2) -> o1.getSeverity().name().compareTo(o2.getSeverity().name());
        Collections.sort(warnings, comparator);
        if (warnings.size() > 10) {
            warnings = warnings.subList(0,  10);
        }
        model.addAttribute("warnings",  warnings);
        model.addAttribute("lastAttemptedRefresh", widgetService.getRunTime(false));
        Instant nextRun = widgetService.getRunTime(true);
        if (nextRun.isAfterNow()) {
            model.addAttribute("nextRefresh", nextRun);
            model.addAttribute("isRefreshPossible", false);
            String nextRefreshDate = dateFormattingService.format(nextRun, DateFormattingService.DateFormatEnum.DATEHMS_12, userContext);
            model.addAttribute("refreshTooltip", accessor.getMessage(widgetKey + "nextRefresh") + nextRefreshDate);
        } else {
            model.addAttribute("isRefreshPossible", true);
            model.addAttribute("refreshTooltip", accessor.getMessage(widgetKey + "forceUpdate"));
        }
        return "infrastructureWarnings/widgetView.jsp";
    }
    
    private InfrastructureWarningDeviceCategory[] getTypesInSystem() {
        InfrastructureWarningSummary summary = widgetService.getWarningsSummary();
        return Arrays.stream(InfrastructureWarningDeviceCategory.values())
                     .filter(category -> summary.getTotalDevices(category) != 0)
                     .toArray(InfrastructureWarningDeviceCategory[]::new);
    }
    
    @RequestMapping("detail")
    public String detail(@DefaultSort(dir=Direction.asc, sort="name") SortingParameters sorting, PagingParameters paging, 
                         InfrastructureWarningDeviceCategory[] types, ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        types = types != null ? types : getTypesInSystem();
        List<InfrastructureWarning> warnings = infrastructureWarningsDao.getWarnings(types);
        
        SearchResults<InfrastructureWarning> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, warnings.size());

        DetailSortBy sortBy = DetailSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();

        List<InfrastructureWarning> itemList = Lists.newArrayList(warnings);
        
        Comparator<InfrastructureWarning> comparator = (o1, o2) -> 
            cache.getAllPaosMap().get(o1.getPaoIdentifier().getPaoId()).getPaoName()
                .compareTo(cache.getAllPaosMap().get(o2.getPaoIdentifier().getPaoId()).getPaoName());
        if (sortBy == DetailSortBy.type) {
            comparator = (o1, o2) -> o1.getPaoIdentifier().getPaoType().getPaoTypeName().compareTo(o2.getPaoIdentifier().getPaoType().getPaoTypeName());
        } else if (sortBy == DetailSortBy.status) {
            comparator = (o1, o2) -> o1.getSeverity().name().compareTo(o2.getSeverity().name());
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);

        List<SortableColumn> columns = new ArrayList<>();
        for (DetailSortBy column : DetailSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }

        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, warnings.size());
        searchResult.setResultList(itemList);
        
        model.addAttribute("warnings", searchResult);
        
        model.addAttribute("deviceTypes", getTypesInSystem());
        model.addAttribute("selectedTypes", Lists.newArrayList(types));

        return "infrastructureWarnings/detail.jsp";
    }
    
    public enum DetailSortBy implements DisplayableEnum {

        name,
        type,
        status;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }

    }

}
