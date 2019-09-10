package com.cannontech.web.amr.meterProgramming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.programming.dao.MeterProgrammingDao;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.tools.device.programming.dao.MeterProgrammingSummaryDao;
import com.cannontech.web.tools.device.programming.dao.MeterProgrammingSummaryDao.SortBy;
import com.cannontech.web.tools.device.programming.model.MeterProgramInfo;
import com.cannontech.web.tools.device.programming.model.MeterProgramStatistics;
import com.cannontech.web.tools.device.programming.model.MeterProgramSummaryDetail;
import com.cannontech.web.tools.device.programming.model.MeterProgrammingSummaryFilter;
import com.cannontech.web.tools.device.programming.model.MeterProgrammingSummaryFilter.DisplayableStatus;

@Controller
@RequestMapping("/meterProgramming/*")
public class MeterProgrammingSummaryController {
    
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private MeterProgrammingSummaryDao meterProgrammingSummaryDao;
    @Autowired private MeterProgrammingDao meterProgrammingDao;
    @Autowired private DeviceGroupService deviceGroupService;

    private final static String baseKey = "yukon.web.modules.amr.meterProgramming.";

    @GetMapping("home")
    public String home(@DefaultSort(dir=Direction.asc, sort="program") SortingParameters sorting, ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        List<MeterProgramStatistics> detail = meterProgrammingSummaryDao.getProgramStatistics(userContext);
        
        ProgramSortBy sortBy = ProgramSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        
        Comparator<MeterProgramStatistics> comparator = (o1, o2) -> o1.getProgramInfo().getName().compareTo(o2.getProgramInfo().getName());
        if (sortBy == ProgramSortBy.numberOfDevices) {
            comparator = (o1, o2) -> Integer.valueOf(o1.getDeviceTotal()).compareTo(Integer.valueOf(o2.getDeviceTotal()));
        } else if (sortBy == ProgramSortBy.numberInProgress) {
            comparator = (o1, o2) -> Integer.valueOf(o1.getInProgressTotal()).compareTo(Integer.valueOf(o2.getInProgressTotal()));
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(detail, comparator);
        
        List<SortableColumn> columns = new ArrayList<>();
        for (ProgramSortBy column : ProgramSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }
        
        model.addAttribute("programs", detail);

        
        return "meterProgramming/home.jsp";
    }
    
    @DeleteMapping("/{guid}/delete")
    public void delete(@PathVariable String guid, FlashScope flash, HttpServletResponse resp) {
        UUID uuid = UUID.fromString(guid);
        MeterProgram program = meterProgrammingDao.getMeterProgram(uuid);
        try {
            meterProgrammingDao.deleteMeterProgram(uuid);
        } catch (Exception e) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.error", program.getName()));
            resp.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }
        resp.setStatus(HttpStatus.NO_CONTENT.value());
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", program.getName()));
    }
    
    public enum ProgramSortBy implements DisplayableEnum {

        program,
        numberOfDevices,
        numberInProgress;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }
    
    @GetMapping("summary")
    public String summary(MeterProgrammingSummaryFilter filter, @DefaultSort(dir=Direction.asc, sort="DEVICE_NAME") SortingParameters sorting, 
                          PagingParameters paging, ModelMap model, YukonUserContext userContext) {
        List<MeterProgramInfo> programs = meterProgrammingSummaryDao.getMeterProgramInfos(userContext);
        if (filter.getPrograms() != null) {
            List<String> selectedProgramNames = filter.getPrograms().stream()
                    .map(MeterProgramInfo::getName)
                    .collect(Collectors.toList());
            model.addAttribute("selectedPrograms", selectedProgramNames);
        }

        getFilteredResults(filter, sorting, paging, model, userContext);
        model.addAttribute("programList", programs);
        model.addAttribute("statusList", DisplayableStatus.values());
        return "meterProgramming/summary.jsp";
    }
    
    @GetMapping("summaryFilter")
    public String summaryFilter(@ModelAttribute("filter") MeterProgrammingSummaryFilter filter, String deviceGroupName, 
                                @DefaultSort(dir=Direction.asc, sort="DEVICE_NAME") SortingParameters sorting,
                                PagingParameters paging, ModelMap model, YukonUserContext userContext) {
        if (!StringUtils.isBlank(deviceGroupName)) {
            DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroupName);
            filter.setGroups(Arrays.asList(group));
        }
        getFilteredResults(filter, sorting, paging, model, userContext);
        return "meterProgramming/summaryResults.jsp";
    }
    
    private void getFilteredResults(MeterProgrammingSummaryFilter filter, SortingParameters sorting, PagingParameters paging, ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        SortBy sortBy = SortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        for (SortBy column : SortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        
        if (filter.getPrograms() == null || filter.getPrograms().isEmpty()) {
            List<MeterProgramInfo> programs = meterProgrammingSummaryDao.getMeterProgramInfos(userContext);
            filter.setPrograms(programs);
        }
        if (filter.getStatuses() == null || filter.getStatuses().isEmpty()) {
            filter.setStatuses(Arrays.asList(DisplayableStatus.values()));
        }
        
        model.addAttribute("filter", filter);

        SearchResults<MeterProgramSummaryDetail> detail = meterProgrammingSummaryDao.getSummary(filter, paging, sortBy, dir, userContext);
        model.addAttribute("searchResults", detail);
    }

}