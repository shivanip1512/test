package com.cannontech.web.dr;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DateRange;
import com.cannontech.common.util.IntegerRange;
import com.cannontech.common.util.Range;
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
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.collect.Ordering;

public class ProgramControllerHelper {
    public static class ProgramListBackingBean extends ListBackingBean {
        private String state;
        private DateRange start = new DateRange();
        private DateRange stop = new DateRange();
        private IntegerRange priority = new IntegerRange();
        private Range<Double> loadCapacity = new Range<Double>();

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

        public Range<Double> getLoadCapacity() {
            return loadCapacity;
        }

        public void setLoadCapacity(Range<Double> loadCapacity) {
            this.loadCapacity = loadCapacity;
        }
    }

    private SimpleValidator<ProgramListBackingBean> validator =
        new SimpleValidator<ProgramListBackingBean>(ProgramListBackingBean.class) {
            @Override
            protected void doValidation(ProgramListBackingBean target,
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

    private ProgramService programService = null;
    private PaoAuthorizationService paoAuthorizationService;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    private ProgramFieldService programFieldService;
    private ProgramNameField programNameField;
    private FavoritesDao favoritesDao;

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
            ProgramListBackingBean backingBean, BindingResult bindingResult,
            UiFilter<DisplayablePao> detailFilter) {
        validator.validate(backingBean, bindingResult);

        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();
        if (detailFilter != null) {
            filters.add(detailFilter);
        }

        filters.add(new AuthorizedFilter<DisplayablePao>(paoAuthorizationService, 
                                         userContext.getYukonUser(),
                                         Permission.LM_VISIBLE));

        boolean isFiltered = false;
        if (!StringUtils.isEmpty(backingBean.getName())) {
            filters.add(new NameFilter(backingBean.getName()));
            isFiltered = true;
        }
        String stateFilter = backingBean.getState();
        if (!StringUtils.isEmpty(stateFilter)) {
            FilteredState filteredState = FilteredState.valueOf(stateFilter);
            if (filteredState != FilteredState.ALL) {
                filters.add(new StateFilter(programService, filteredState));
                isFiltered = true;
            }
        }
        if (!backingBean.getStart().isUnbounded()) {
            filters.add(new StartStopFilter(programService, backingBean.getStart(), true));
            isFiltered = true;
        }
        if (!backingBean.getStop().isUnbounded()) {
            filters.add(new StartStopFilter(programService, backingBean.getStop(), false));
            isFiltered = true;
        }
        if (!backingBean.getPriority().isUnbounded()) {
            filters.add(new PriorityFilter(programService, backingBean.getPriority()));
            isFiltered = true;
        }
        modelMap.addAttribute("isFiltered", isFiltered);

        // Sorting - name is default sorter
        Comparator<DisplayablePao> defaultSorter = programNameField.getSorter(userContext);
        Comparator<DisplayablePao> sorter = defaultSorter;
        if (!StringUtils.isEmpty(backingBean.getSort())) {
            // If there is a custom sorter, add it
            
            DemandResponseBackingField<LMProgramBase> sortField = 
                programFieldService.getBackingField(backingBean.getSort());
            
            sorter = sortField.getSorter(userContext);
            if (backingBean.getDescending()) {
                sorter = Collections.reverseOrder(sorter);
            }
            
            // Don't double sort if name is the sort field
            if (programNameField != sortField) {
                sorter = Ordering.from(sorter).compound(defaultSorter);
            }
        }
        
        int startIndex = (backingBean.getPage() - 1) * backingBean.getItemsPerPage();
        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);
        SearchResult<DisplayablePao> searchResult =
            programService.filterPrograms(filter, sorter, startIndex, backingBean.getItemsPerPage(),
                                          userContext);

        modelMap.addAttribute("searchResult", searchResult);
        modelMap.addAttribute("programs", searchResult.getResultList());
        Map<Integer, Boolean> favoritesByPaoId =
            favoritesDao.favoritesByPao(searchResult.getResultList(),
                                        userContext.getYukonUser());
        modelMap.addAttribute("favoritesByPaoId", favoritesByPaoId);
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
    
    @Autowired
    public void setProgramFieldService(ProgramFieldService programFieldService) {
        this.programFieldService = programFieldService;
    }
    
    @Autowired
    public void setProgramNameField(ProgramNameField programNameField) {
        this.programNameField = programNameField;
    }

    @Autowired
    public void setFavoritesDao(FavoritesDao favoritesDao) {
        this.favoritesDao = favoritesDao;
    }
}
