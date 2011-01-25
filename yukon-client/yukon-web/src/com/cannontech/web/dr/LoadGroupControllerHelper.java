package com.cannontech.web.dr;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DateRange;
import com.cannontech.common.util.Range;
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
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.collect.Ordering;

public class LoadGroupControllerHelper {
    public static class LoadGroupListBackingBean extends ListBackingBean {
        private String state;
        private DateRange lastAction = new DateRange();
        private Range<Double> loadCapacity = new Range<Double>();
        
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
        public Range<Double> getLoadCapacity() {
            return loadCapacity;
        }
        public void setLoadCapacity(Range<Double> loadCapacity) {
            this.loadCapacity = loadCapacity;
        }
    }

    private SimpleValidator<LoadGroupListBackingBean> validator =
        new SimpleValidator<LoadGroupListBackingBean>(LoadGroupListBackingBean.class) {
            @Override
            protected void doValidation(LoadGroupListBackingBean target,
                    Errors errors) {
                if (!target.lastAction.isValid() || target.lastAction.isEmpty()) {
                    errors.reject("lastActionFromTimeAfterToTime");
                }
            }
    };

    private LoadGroupService loadGroupService = null;
    private PaoAuthorizationService paoAuthorizationService;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    private LoadGroupFieldService loadGroupFieldService;
    private LoadGroupNameField loadGroupNameField;
    private FavoritesDao favoritesDao;

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
            LoadGroupListBackingBean backingBean, BindingResult bindingResult,
            UiFilter<DisplayablePao> detailFilter, FlashScope flashScope) {
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
            if (stateFilter.equals("active")) {
                filters.add(new LoadGroupStateFilter(loadGroupService, true));
                isFiltered = true;
            } else if (stateFilter.equals("inactive")) {
                filters.add(new LoadGroupStateFilter(loadGroupService, false));
                isFiltered = true;
            }
        }
        if (!backingBean.getLastAction().isUnbounded()) {
            filters.add(new LoadGroupLastActionFilter(loadGroupService, backingBean.getLastAction()));
            isFiltered = true;
        }
        if (!backingBean.getLoadCapacity().isUnbounded()) {
            filters.add(new LoadGroupLoadCapacityFilter(loadGroupService, backingBean.getLoadCapacity()));
            isFiltered = true;
        }
        model.addAttribute("isFiltered", isFiltered);

        // Sorting - name is default sorter
        Comparator<DisplayablePao> defaultSorter = loadGroupNameField.getSorter(userContext);
        Comparator<DisplayablePao> sorter = defaultSorter;
        if(!StringUtils.isEmpty(backingBean.getSort())) {
            // If there is a custom sorter, add it
            
            DemandResponseBackingField<LMDirectGroupBase> sortField = 
                loadGroupFieldService.getBackingField(backingBean.getSort());
            
            sorter = sortField.getSorter(userContext);
            if(backingBean.getDescending()) {
                sorter = Collections.reverseOrder(sorter);
            }
            
            // Don't double sort if name is the sort field
            if(loadGroupNameField != sortField) {
                sorter = Ordering.from(sorter).compound(defaultSorter);
            }
        }
        
        int startIndex = (backingBean.getPage() - 1) * backingBean.getItemsPerPage();
        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);
        SearchResult<DisplayablePao> searchResult =
            loadGroupService.filterGroups(filter, sorter, startIndex,
                                          backingBean.getItemsPerPage(), userContext);

        model.addAttribute("searchResult", searchResult);
        model.addAttribute("loadGroups", searchResult.getResultList());
        Map<Integer, Boolean> favoritesByPaoId =
            favoritesDao.favoritesByPao(searchResult.getResultList(),
                                        userContext.getYukonUser());
        model.addAttribute("favoritesByPaoId", favoritesByPaoId);

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

    @Autowired
    public void setLoadGroupService(LoadGroupService loadGroupService) {
        this.loadGroupService = loadGroupService;
    }

    @Autowired
    public void setPaoAuthorizationService(
            PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }

    @Autowired
    public void setDatePropertyEditorFactory(
            DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
    
    @Autowired
    public void setLoadGroupFieldService(LoadGroupFieldService loadGroupFieldService) {
        this.loadGroupFieldService = loadGroupFieldService;
    }
    
    @Autowired
    public void setLoadGroupNameField(LoadGroupNameField loadGroupNameField) {
        this.loadGroupNameField = loadGroupNameField;
    }

    @Autowired
    public void setFavoritesDao(FavoritesDao favoritesDao) {
        this.favoritesDao = favoritesDao;
    }
}
