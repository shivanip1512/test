package com.cannontech.web.dr;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.SessionStatus;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.Range;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.dr.filter.AuthorizedFilter;
import com.cannontech.dr.filter.NameFilter;
import com.cannontech.dr.program.filter.PriorityFilter;
import com.cannontech.dr.program.filter.StartStopFilter;
import com.cannontech.dr.program.filter.StateFilter;
import com.cannontech.dr.program.model.ProgramDisplayField;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.user.YukonUserContext;

public class ProgramControllerHelper {
    public static class ProgramListBackingBean extends ListBackingBean {
        private String state;
        private Range<Date> start = new Range<Date>();
        private Range<Date> stop = new Range<Date>();
        private Range<Integer> priority = new Range<Integer>();
        private Range<Double> loadCapacity = new Range<Double>();

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public Range<Date> getStart() {
            return start;
        }

        public void setStart(Range<Date> start) {
            this.start = start;
        }

        public Range<Date> getStop() {
            return stop;
        }

        public void setStop(Range<Date> stop) {
            this.stop = stop;
        }

        public Range<Integer> getPriority() {
            return priority;
        }

        public void setPriority(Range<Integer> priority) {
            this.priority = priority;
        }

        public Range<Double> getLoadCapacity() {
            return loadCapacity;
        }

        public void setLoadCapacity(Range<Double> loadCapacity) {
            this.loadCapacity = loadCapacity;
        }
    }

    private ProgramService programService = null;
    private PaoAuthorizationService paoAuthorizationService;
    private DatePropertyEditorFactory datePropertyEditorFactory;

    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        // Since Range uses generics, spring can't determine the type of the
        // min and max values on its own.  In any case, for dates, we need
        // to handle the start and end differently.

        PropertyEditor dayStartDateEditor =
            datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.START_OF_DAY, userContext);
        PropertyEditor dayEndDateEditor =
            datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.END_OF_DAY, userContext);

        binder.registerCustomEditor(Object.class, "start.min", dayStartDateEditor);
        binder.registerCustomEditor(Object.class, "start.max", dayEndDateEditor);
        binder.registerCustomEditor(Object.class, "stop.min", dayStartDateEditor);
        binder.registerCustomEditor(Object.class, "stop.max", dayEndDateEditor);
        PropertyEditor numberEditor = new CustomNumberEditor(Integer.class, true);
        binder.registerCustomEditor(Object.class, "priority.min", numberEditor);
        binder.registerCustomEditor(Object.class, "priority.max", numberEditor);
        numberEditor = new CustomNumberEditor(Double.class, true);
        binder.registerCustomEditor(Object.class, "loadCapacity.min", numberEditor);
        binder.registerCustomEditor(Object.class, "loadCapacity.max", numberEditor);
    }

    public void filterPrograms(ModelMap modelMap, YukonUserContext userContext,
            ProgramControllerHelper.ProgramListBackingBean backingBean,
            BindingResult result, SessionStatus status,
            UiFilter<DisplayablePao> detailFilter) {
        // TODO:  validation on backing bean
        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();
        if (detailFilter != null) {
            filters.add(detailFilter);
        }

        filters.add(new AuthorizedFilter(paoAuthorizationService,
                                         userContext.getYukonUser()));
        if (!StringUtils.isEmpty(backingBean.getName())) {
            filters.add(new NameFilter(backingBean.getName()));
        }
        String stateFilter = backingBean.getState();
        if (!StringUtils.isEmpty(stateFilter)) {
            if (stateFilter.equals("active")) {
                filters.add(new StateFilter(programService, true));
            } else if (stateFilter.equals("inactive")) {
                filters.add(new StateFilter(programService, false));
            }
        }
        if (!backingBean.getStart().isUnbounded()) {
            filters.add(new StartStopFilter(programService, backingBean.getStart(), true));
        }
        if (!backingBean.getStop().isUnbounded()) {
            filters.add(new StartStopFilter(programService, backingBean.getStop(), false));
        }
        if (!backingBean.getPriority().isUnbounded()) {
            filters.add(new PriorityFilter(programService, backingBean.getPriority()));
        }

        ProgramDisplayField sortField = StringUtils.isEmpty(backingBean.getSort())
            ? ProgramDisplayField.NAME : ProgramDisplayField.valueOf(backingBean.getSort());
        Comparator<DisplayablePao> sorter =
            sortField.getSorter(programService, userContext, backingBean.getDescending());

        int startIndex = (backingBean.getPage() - 1) * backingBean.getItemsPerPage();
        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);
        SearchResult<DisplayablePao> searchResult =
            programService.filterPrograms(userContext, filter, sorter,
                                startIndex, backingBean.getItemsPerPage());

        modelMap.addAttribute("searchResult", searchResult);
        modelMap.addAttribute("programs", searchResult.getResultList());
        modelMap.addAttribute("backingBean", backingBean);
    }

    @Autowired
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }

    @Autowired
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }

    @Autowired
    public void setDatePropertyEditorFactory(DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
}
