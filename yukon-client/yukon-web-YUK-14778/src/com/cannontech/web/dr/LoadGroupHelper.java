package com.cannontech.web.dr;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
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
import com.cannontech.common.util.MutableRange;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.filter.AuthorizedFilter;
import com.cannontech.dr.filter.NameFilter;
import com.cannontech.dr.loadgroup.filter.LoadGroupLastActionFilter;
import com.cannontech.dr.loadgroup.filter.LoadGroupLoadCapacityFilter;
import com.cannontech.dr.loadgroup.filter.LoadGroupStateFilter;
import com.cannontech.dr.loadgroup.model.LoadGroupNameField;
import com.cannontech.dr.loadgroup.service.LoadGroupFieldService;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.google.common.collect.Ordering;

public class LoadGroupHelper {
    
    public static class LoadGroupFilter {
        
        private String name;
        private String state;
        private DateRange lastAction = new DateRange();
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
        public DateRange getLastAction() {
            return lastAction;
        }
        public void setLastAction(DateRange lastAction) {
            this.lastAction = lastAction;
        }
        public MutableRange<Double> getLoadCapacity() {
            return loadCapacity;
        }
        public void setLoadCapacity(MutableRange<Double> loadCapacity) {
            this.loadCapacity = loadCapacity;
        }
        
    }

    private SimpleValidator<LoadGroupFilter> validator =
        new SimpleValidator<LoadGroupFilter>(LoadGroupFilter.class) {
            @Override
            protected void doValidation(LoadGroupFilter target, Errors errors) {
                if (!target.lastAction.isValid() || target.lastAction.isEmpty()) {
                    errors.reject("lastActionFromTimeAfterToTime");
                }
            }
    };

    @Autowired private LoadGroupService loadGroupService;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private LoadGroupFieldService loadGroupFieldService;
    @Autowired private LoadGroupNameField loadGroupNameField;

    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        // Since Range uses generics, spring can't determine the type of the
        // min and max values on its own.  In any case, for dates, we need
        // to handle the start and end differently.
        PropertyEditor dayStartDateEditor =
            datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.START_OF_DAY, userContext);
        PropertyEditor dayEndDateEditor =
            datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.END_OF_DAY, userContext);

        binder.registerCustomEditor(Object.class, "lastAction.min", dayStartDateEditor);
        binder.registerCustomEditor(Object.class, "lastAction.max", dayEndDateEditor);
        PropertyEditor numberEditor = new CustomNumberEditor(Double.class, true);
        binder.registerCustomEditor(Object.class, "loadCapacity.min", numberEditor);
        binder.registerCustomEditor(Object.class, "loadCapacity.max", numberEditor);
    }

    public void filterGroups(ModelMap model, YukonUserContext userContext,
            LoadGroupFilter filter, BindingResult bindingResult,
            UiFilter<DisplayablePao> detailFilter, FlashScope flashScope,
            PagingParameters paging, SortingParameters sorting) {
        
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
            if (stateFilter.equals("active")) {
                filters.add(new LoadGroupStateFilter(loadGroupService, true));
                isFiltered = true;
            } else if (stateFilter.equals("inactive")) {
                filters.add(new LoadGroupStateFilter(loadGroupService, false));
                isFiltered = true;
            }
        }
        if (!filter.getLastAction().isUnbounded()) {
            filters.add(new LoadGroupLastActionFilter(loadGroupService, filter.getLastAction()));
            isFiltered = true;
        }
        if (!filter.getLoadCapacity().isUnbounded()) {
            filters.add(new LoadGroupLoadCapacityFilter(loadGroupService, filter.getLoadCapacity()));
            isFiltered = true;
        }
        model.addAttribute("isFiltered", isFiltered);

        // Sorting - name is default sorter
        Comparator<DisplayablePao> defaultSorter = loadGroupNameField.getSorter(userContext);
        Comparator<DisplayablePao> sorter = defaultSorter;
        if(!StringUtils.isEmpty(sorting.getSort())) {
            // If there is a custom sorter, add it
            
            DemandResponseBackingField<LMDirectGroupBase> sortField = 
                loadGroupFieldService.getBackingField(sorting.getSort());
            
            sorter = sortField.getSorter(userContext);
            if(sorting.getDirection() == Direction.desc) {
                sorter = Collections.reverseOrder(sorter);
            }
            
            // Don't double sort if name is the sort field
            if(loadGroupNameField != sortField) {
                sorter = Ordering.from(sorter).compound(defaultSorter);
            }
        }
        
        UiFilter<DisplayablePao> uifilter = UiFilterList.wrap(filters);
        SearchResults<DisplayablePao> loadGroups =
            loadGroupService.filterGroups(uifilter, sorter, paging.getStartIndex(),
                                          paging.getItemsPerPage(), userContext);

        model.addAttribute("loadGroups", loadGroups);

        addFilterErrorsToFlashScopeIfNecessary(model, bindingResult, flashScope);
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
    
    public void addColumns(ModelMap model, MessageSourceAccessor accessor, SortingParameters sorting) {
        buildColumn(model, accessor, SortBy.NAME, sorting);
        buildColumn(model, accessor, SortBy.STATE, sorting);
        buildColumn(model, accessor, SortBy.LAST_ACTION, sorting);
        buildColumn(model, accessor, SortBy.CONTROL_STATISTICS, sorting);
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
        LAST_ACTION,
        CONTROL_STATISTICS,
        REDUCTION;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.loadGroupList.heading." + name();
        }
    }

}
