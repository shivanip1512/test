package com.cannontech.web.stars.infrastructureWarnings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.infrastructure.dao.InfrastructureWarningsDao;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningDeviceCategory;
import com.cannontech.infrastructure.model.InfrastructureWarningSummary;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.common.widgets.service.InfrastructureWarningsWidgetService;
import com.cannontech.web.util.WebFileUtils;
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
    @Autowired private PaoNotesService paoNotesService;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;

    private final static String baseKey = "yukon.web.widgets.infrastructureWarnings.";
    private final static String widgetKey = "yukon.web.widgets.";
    private static final Instant epoch1990 = new Instant(CtiUtilities.get1990GregCalendar().getTime());

    @GetMapping("forceUpdate")
    public @ResponseBody Map<String, Object> forceUpdate() {
        Map<String, Object> json = new HashMap<>();
        widgetService.initiateRecalculation();
        json.put("success", true);
        return json;
    }
    
    @GetMapping("updateWidget")
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
        
        model.addAttribute("epoch1990", epoch1990);

        return "infrastructureWarnings/widgetView.jsp";
    }
    
    private InfrastructureWarningDeviceCategory[] getTypesInSystem() {
        InfrastructureWarningSummary summary = widgetService.getWarningsSummary();
        return Arrays.stream(InfrastructureWarningDeviceCategory.values())
                     .filter(category -> summary.getTotalDevices(category) != 0)
                     .toArray(InfrastructureWarningDeviceCategory[]::new);
    }
    
    @GetMapping("detail")
    public String detail(@DefaultSort(dir=Direction.desc, sort="timestamp") SortingParameters sorting, PagingParameters paging, 
                         InfrastructureWarningDeviceCategory[] types, ModelMap model, YukonUserContext userContext) {
        InfrastructureWarningSummary summary = widgetService.getWarningsSummary();
        model.addAttribute("summary", summary);

        getFilteredResults(types, model, paging, sorting, userContext);
        return "infrastructureWarnings/detail.jsp";
    }
    
    @GetMapping("filteredResults")
    public String filteredResults(@DefaultSort(dir=Direction.desc, sort="timestamp") SortingParameters sorting, PagingParameters paging, 
                         InfrastructureWarningDeviceCategory[] types, ModelMap model, YukonUserContext userContext) {
        getFilteredResults(types, model, paging, sorting, userContext);
        return "infrastructureWarnings/filteredTable.jsp";
    }
    
    private void getFilteredResults(InfrastructureWarningDeviceCategory[] types, ModelMap model, PagingParameters paging, 
                                    SortingParameters sorting, YukonUserContext userContext) {
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
        } else if (sortBy == DetailSortBy.timestamp) {
            comparator = (o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp());
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
        
        model.addAttribute("epoch1990", epoch1990);
        
        List<Integer> notesList = paoNotesService.getPaoIdsWithNotes(itemList.stream()
                                                                             .map(warning -> warning.getPaoIdentifier().getPaoId())
                                                                             .collect(Collectors.toList()));
        model.addAttribute("notesList", notesList);
    }
    
    @GetMapping(value = "collectionAction")
    public String collectionAction(InfrastructureWarningDeviceCategory[] types, String actionUrl) {
        types = types != null ? types : getTypesInSystem();
        List<InfrastructureWarning> warnings = infrastructureWarningsDao.getWarnings(types);
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        List<YukonPao> devices = warnings.stream().map(d -> new SimpleDevice(d.getPaoIdentifier())).collect(Collectors.toList());
        deviceGroupMemberEditorDao.addDevices(tempGroup, devices);
        return "redirect:" + actionUrl + "?collectionType=group&group.name=" + tempGroup.getFullName();
    }
    
    @GetMapping("download")
    public String download(InfrastructureWarningDeviceCategory[] types, YukonUserContext userContext, HttpServletResponse response) throws IOException {
        types = types != null ? types : getTypesInSystem();
        List<InfrastructureWarning> warnings = infrastructureWarningsDao.getWarnings(types);
        
        String[] headerRow = getHeaderRows(userContext);
        List<String[]> dataRows = getDataRows(warnings, userContext);

        String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "infrastructureWarnings_" + now + ".csv");
        return null;
      }
    
    @GetMapping("downloadAll")
    public String downloadAll(YukonUserContext userContext, HttpServletResponse response) throws IOException {
        List<InfrastructureWarning> warnings = infrastructureWarningsDao.getWarnings(getTypesInSystem());

        String[] headerRow = getHeaderRows(userContext);
        List<String[]> dataRows = getDataRows(warnings, userContext);

        String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "infrastructureWarnings_" + now + ".csv");
        return null;
      }
    
    private String[] getHeaderRows(YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[4];

        headerRow[0] = accessor.getMessage(DetailSortBy.name);
        headerRow[1] = accessor.getMessage(DetailSortBy.type);
        headerRow[2] = accessor.getMessage(DetailSortBy.status);
        headerRow[3] = accessor.getMessage(DetailSortBy.timestamp);
        
        return headerRow;
    }
    
    private List<String[]> getDataRows(List<InfrastructureWarning> warnings, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        List<String[]> dataRows = Lists.newArrayList();
        for (InfrastructureWarning warning: warnings) {
            String[] dataRow = new String[4];
            dataRow[0] = cache.getAllPaosMap().get(warning.getPaoIdentifier().getPaoId()).getPaoName();
            dataRow[1] = warning.getPaoIdentifier().getPaoType().getPaoTypeName();
            dataRow[2] = accessor.getMessage(warning.getWarningType().getFormatKey() + "." + warning.getSeverity().name(), warning.getArguments());
            if (warning.getTimestamp().isBefore(epoch1990)) {
                dataRow[3] = accessor.getMessage("yukon.common.dashes");
            } else {
                dataRow[3] = (warning.getTimestamp() == null) ? "" : dateFormattingService.format(warning.getTimestamp(), DateFormatEnum.BOTH, userContext);
            }
            dataRows.add(dataRow);
        }
        return dataRows;
    }
    
    public enum DetailSortBy implements DisplayableEnum {

        name,
        type,
        status,
        timestamp;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }

    }

}
