package com.cannontech.web.collectionActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao.SortBy;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionFilter;
import com.cannontech.common.bulk.collection.device.model.CollectionActionFilteredResult;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;

@Controller
@RequestMapping("/*")
public class RecentResultsController {
    
    @Autowired private CollectionActionDao collectionActionDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private YukonUserDao userDao;
    
    private final static String baseKey = "yukon.web.modules.tools.collectionActions.recentResults.";
    
    @RequestMapping(value = "recentResults", method = RequestMethod.GET)
    public String view(YukonUserContext userContext, ModelMap model, @ModelAttribute("filter") CollectionActionFilter filter,
                       @DefaultSort(dir=Direction.desc, sort="startTime") SortingParameters sorting,
                       @DefaultItemsPerPage(value=250) PagingParameters paging) {
        
        Direction dir = sorting.getDirection();
        ResultSortBy sortBy = ResultSortBy.valueOf(sorting.getSort());
        setupModel(userContext, model, filter, sorting, dir, sortBy);
        filter(userContext, model, filter, sorting, paging);
        
        return "recentResults.jsp";
    }
    
    @RequestMapping(value = "filter", method = RequestMethod.GET)
    public String filter(YukonUserContext userContext, ModelMap model, @ModelAttribute("filter") CollectionActionFilter filter,
                         @DefaultSort(dir=Direction.desc, sort="startTime") SortingParameters sorting,
                         @DefaultItemsPerPage(value=250) PagingParameters paging) {
        
        Direction dir = sorting.getDirection();
        ResultSortBy sortBy = ResultSortBy.valueOf(sorting.getSort());
        setupModel(userContext, model, filter, sorting, dir, sortBy);
        boolean allTypesSelected = false;
        if (filter.getActions() == null) {
            allTypesSelected = true;
            filter.setActions(Arrays.asList(CollectionAction.values()));
        }
        SearchResults<CollectionActionFilteredResult> results = collectionActionDao.getCollectionActionFilteredResults(filter, paging, sortBy.getValue(), dir);
        if (allTypesSelected) {
            filter.setActions(null);
        }
        
        model.addAttribute("recentActions", results);
        
        return "recentResults.jsp";
    }
    
    private void setupModel(YukonUserContext userContext, ModelMap model, CollectionActionFilter filter,
                            SortingParameters sorting, Direction dir, ResultSortBy sortBy) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        List<CommandRequestExecutionStatus> statusList = new ArrayList<>();
        statusList.add(CommandRequestExecutionStatus.STARTED);
        statusList.add(CommandRequestExecutionStatus.COMPLETE);
        statusList.add(CommandRequestExecutionStatus.CANCELLED);
        statusList.add(CommandRequestExecutionStatus.FAILED);
        
        if (filter.getStatuses() == null) {
            filter.setStatuses(statusList);
        }
        
        if (filter.getStartDate() == null || filter.getEndDate() == null) {
            DateTime from = new DateTime(userContext.getJodaTimeZone()).withTimeAtStartOfDay().minusDays(6);
            DateTime to = new DateTime(userContext.getJodaTimeZone()).plusDays(1);
            filter.setStartDate(from.toDate());
            filter.setEndDate(to.toDate());
        }
        
        if (filter.getUserIds() != null && !filter.getUserIds().isEmpty()) {
            List<String> userNames = new ArrayList<>();
            for (String id : filter.getUserIds().split(",")) {
                userNames.add(userDao.getLiteYukonUser(Integer.parseInt(id)).getUsername());
            }
            filter.setUserNames(userNames);
        }
        model.addAttribute("filter", filter);
        model.addAttribute("statuses", statusList);
        Comparator<CollectionAction> comparator = (o1, o2) -> {
            return o1.name().compareTo(o2.name());
        };
        List<CollectionAction> actions = Arrays.asList(CollectionAction.values());
        Collections.sort(actions, comparator);
        model.addAttribute("actionsList", actions);
        for (ResultSortBy columnHeader : ResultSortBy.values()) {
            String text = accessor.getMessage(columnHeader);
            SortableColumn col = SortableColumn.of(dir, columnHeader == sortBy, text, columnHeader.name());
            model.addAttribute(columnHeader.name(), col);
        }
    }
    
    public enum ResultSortBy implements DisplayableEnum {
        
        action(SortBy.ACTION),
        startTime(SortBy.START_TIME),
        success(SortBy.SUCCESS),
        failure(SortBy.FAILURE),
        notAttempted(SortBy.NOT_ATTEMPTED),
        status(SortBy.STATUS),
        userName(SortBy.USER_NAME),
        ;
        
        private ResultSortBy(SortBy value) {
            this.value = value;
        }
        
        private final SortBy value;
        
        private SortBy getValue() {
            return value;
        }
        
        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }
}
