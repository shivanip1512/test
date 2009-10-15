package com.cannontech.web.dr;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.SessionStatus;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.Range;
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
import com.cannontech.dr.program.model.ProgramNameField;
import com.cannontech.dr.program.service.ProgramFieldService;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Ordering;

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
    private ProgramFieldService programFieldService;
    private ProgramNameField programNameField;
    private FavoritesDao favoritesDao;

    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        PropertyEditor fullDateTimeEditor =
            datePropertyEditorFactory.getPropertyEditor(DateFormatEnum.DATEHM, userContext);
        binder.registerCustomEditor(Date.class, fullDateTimeEditor);

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
                                         userContext.getYukonUser(),
                                         Permission.LM_VISIBLE));
        
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

        // Sorting - name is default sorter
        Comparator<DisplayablePao> defaultSorter = programNameField.getSorter(userContext);
        Comparator<DisplayablePao> sorter = defaultSorter;
        if(!StringUtils.isEmpty(backingBean.getSort())) {
            // If there is a custom sorter, add it
            
            DemandResponseBackingField<LMProgramBase> sortField = 
                programFieldService.getBackingField(backingBean.getSort());
            
            sorter = sortField.getSorter(userContext);
            if(backingBean.getDescending()) {
                sorter = Collections.reverseOrder(sorter);
            }
            
            // Don't double sort if name is the sort field
            if(programNameField != sortField) {
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
