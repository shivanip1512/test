package com.cannontech.web.amr.meterProgramming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.programming.dao.MeterProgrammingDao;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.tools.device.programming.dao.MeterProgrammingSummaryDao;
import com.cannontech.web.tools.device.programming.model.MeterProgramStatistics;

@Controller
@RequestMapping("/meterProgramming/*")
public class MeterProgrammingSummaryController {
    
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private MeterProgrammingSummaryDao meterProgrammingSummaryDao;
    @Autowired private MeterProgrammingDao meterProgrammingDao;
    
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
    public String summary(ModelMap model, YukonUserContext userContext) {

        return "meterProgramming/summary.jsp";
    }

}