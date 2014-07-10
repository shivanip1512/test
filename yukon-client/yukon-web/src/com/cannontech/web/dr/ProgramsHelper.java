package com.cannontech.web.dr;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.DateRange;
import com.cannontech.common.util.IntegerRange;
import com.cannontech.common.util.MutableRange;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.filter.AuthorizedFilter;
import com.cannontech.dr.filter.NameFilter;
import com.cannontech.dr.program.filter.PriorityFilter;
import com.cannontech.dr.program.filter.StartStopFilter;
import com.cannontech.dr.program.filter.StateFilter;
import com.cannontech.dr.program.filter.StateFilter.FilteredState;
import com.cannontech.dr.program.model.ProgramNameField;
import com.cannontech.dr.program.service.ProgramFieldService;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.google.common.collect.Ordering;

public class ProgramsHelper {
    
    public static class ProgramFilter {
        
        private String name;
        private String state;
        private DateRange start = new DateRange();
        private DateRange stop = new DateRange();
        private IntegerRange priority = new IntegerRange();
        private MutableRange<Double> loadCapacity = new MutableRange<Double>();
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public DateRange getStart() {
            return start;
        }

        public void setStart(DateRange start) {
            this.start = start;
        }

        public DateRange getStop() {
            return stop;
        }

        public void setStop(DateRange stop) {
            this.stop = stop;
        }

        public IntegerRange getPriority() {
            return priority;
        }

        public void setPriority(IntegerRange priority) {
            this.priority = priority;
        }

        public MutableRange<Double> getLoadCapacity() {
            return loadCapacity;
        }

        public void setLoadCapacity(MutableRange<Double> loadCapacity) {
            this.loadCapacity = loadCapacity;
        }
    }

    private SimpleValidator<ProgramFilter> validator =
        new SimpleValidator<ProgramFilter>(ProgramFilter.class) {
            @Override
            protected void doValidation(ProgramFilter target,
                    Errors errors) {
                if (!target.start.isValid() || target.start.isEmpty()) {
                    errors.reject("startFromTimeAfterToTime");
                }
                if (!target.stop.isValid() || target.stop.isEmpty()) {
                    errors.reject("stopFromTimeAfterToTime");
                }
                if (!target.priority.isValid() || target.priority.isEmpty()) {
                    errors.reject("priorityFromAfterTo");
                }
            }
    };

    @Autowired private ProgramService programService;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private ProgramFieldService programFieldService;
    @Autowired private ProgramNameField programNameField;

    public void initBinder(WebDataBinder binder, YukonUserContext userContext, String page) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver =
                new YukonMessageCodeResolver("yukon.web.modules.dr." + page + ".");
            binder.setMessageCodesResolver(msgCodesResolver);
        }

        PropertyEditor fullDateTimeEditor =
            datePropertyEditorFactory.getPropertyEditor(DateFormatEnum.DATEHM, userContext);
        binder.registerCustomEditor(Date.class, fullDateTimeEditor);

        PropertyEditor dateTimeEditor =
            datePropertyEditorFactory.getLocalTimePropertyEditor(DateFormatEnum.DATEHM, userContext);
        binder.registerCustomEditor(DateTime.class, dateTimeEditor);

        PropertyEditor localTimeEditor =
            datePropertyEditorFactory.getLocalTimePropertyEditor(DateFormatEnum.TIME24H, userContext);
        binder.registerCustomEditor(LocalTime.class, localTimeEditor);

        // Since Range uses generics, spring can't determine the type of the
        // min and max values on its own.  In any case, for dates, we need
        // to handle the start and end differently.

        PropertyEditor dayStartDateEditor =
            datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.START_OF_DAY, userContext);
        PropertyEditor dayEndDateEditor =
            datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.END_OF_DAY, userContext);

        binder.registerCustomEditor(Date.class, "start.min", dayStartDateEditor);
        binder.registerCustomEditor(Date.class, "start.max", dayEndDateEditor);
        binder.registerCustomEditor(Date.class, "stop.min", dayStartDateEditor);
        binder.registerCustomEditor(Date.class, "stop.max", dayEndDateEditor);
        PropertyEditor numberEditor = new CustomNumberEditor(Integer.class, true);
        binder.registerCustomEditor(Object.class, "priority.min", numberEditor);
        binder.registerCustomEditor(Object.class, "priority.max", numberEditor);
        numberEditor = new CustomNumberEditor(Double.class, true);
        binder.registerCustomEditor(Object.class, "loadCapacity.min", numberEditor);
        binder.registerCustomEditor(Object.class, "loadCapacity.max", numberEditor);
    }

    public void filterPrograms(ModelMap modelMap, YukonUserContext userContext,
            ProgramFilter filter, BindingResult bindingResult,
            UiFilter<DisplayablePao> detailFilter,
            SortingParameters sorting,
            PagingParameters paging) {
        
        validator.validate(filter, bindingResult);

        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();
        if (detailFilter != null) {
            filters.add(detailFilter);
        }

        filters.add(new AuthorizedFilter<DisplayablePao>(paoAuthorizationService, 
                                         userContext.getYukonUser(),
                                         Permission.LM_VISIBLE));

        boolean isFiltered = false;
        if (!StringUtils.isEmpty(filter.getName())) {
            filters.add(new NameFilter(filter.getName()));
            isFiltered = true;
        }
        String stateFilter = filter.getState();
        if (!StringUtils.isEmpty(stateFilter)) {
            FilteredState filteredState = FilteredState.valueOf(stateFilter);
            if (filteredState != FilteredState.ALL) {
                filters.add(new StateFilter(programService, filteredState));
                isFiltered = true;
            }
        }
        if (!filter.getStart().isUnbounded()) {
            filters.add(new StartStopFilter(programService, filter.getStart(), true));
            isFiltered = true;
        }
        if (!filter.getStop().isUnbounded()) {
            filters.add(new StartStopFilter(programService, filter.getStop(), false));
            isFiltered = true;
        }
        if (!filter.getPriority().isUnbounded()) {
            filters.add(new PriorityFilter(programService, filter.getPriority()));
            isFiltered = true;
        }
        modelMap.addAttribute("isFiltered", isFiltered);

        // Sorting - name is default sorter
        Comparator<DisplayablePao> defaultSorter = programNameField.getSorter(userContext);
        Comparator<DisplayablePao> sorter = defaultSorter;
        if (!StringUtils.isEmpty(sorting.getSort())) {
            // If there is a custom sorter, add it
            
            DemandResponseBackingField<LMProgramBase> sortField = 
                programFieldService.getBackingField(sorting.getSort());
            
            sorter = sortField.getSorter(userContext);
            if (sorting.getDirection() == Direction.desc) {
                sorter = Collections.reverseOrder(sorter);
            }
            
            // Don't double sort if name is the sort field
            if (programNameField != sortField) {
                sorter = Ordering.from(sorter).compound(defaultSorter);
            }
        }
        
        UiFilter<DisplayablePao> uiFilter = UiFilterList.wrap(filters);
        SearchResults<DisplayablePao> programs =
            programService.filterPrograms(uiFilter, sorter, 
                    paging.getStartIndex(), paging.getItemsPerPage(), userContext);

        modelMap.addAttribute("programs", programs);
    }

    public void addColumns(ModelMap model, MessageSourceAccessor accessor, SortingParameters sorting) {
        buildColumn(model, accessor, SortBy.NAME, sorting);
        buildColumn(model, accessor, SortBy.STATE, sorting);
        buildColumn(model, accessor, SortBy.START, sorting);
        buildColumn(model, accessor, SortBy.STOP, sorting);
        buildColumn(model, accessor, SortBy.CURRENT_GEAR, sorting);
        buildColumn(model, accessor, SortBy.PRIORITY, sorting);
        buildColumn(model, accessor, SortBy.REDUCTION, sorting);
    }
    
    private void buildColumn(ModelMap model, MessageSourceAccessor accessor, SortBy field, SortingParameters sorting) {
        
        Direction dir = sorting.getDirection();
        SortBy sort = SortBy.valueOf(sorting.getSort());
        
        String text = accessor.getMessage(field);
        boolean active = sort == field;
        SortableColumn col = SortableColumn.of(dir, active, text, field.name());
        model.addAttribute(field.name(), col);
    }
    
    public enum SortBy implements DisplayableEnum {
        
        NAME,
        STATE,
        START,
        STOP,
        CURRENT_GEAR,
        PRIORITY,
        REDUCTION;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.programList.heading." + name();
        }
    }
    
}