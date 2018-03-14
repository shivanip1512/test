package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;

@Controller
@RequestMapping("/*")
public class RecentResultsController {
    
    @Autowired private CollectionActionDao collectionActionDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private final static String baseKey = "yukon.web.modules.tools.recentResults.";
    
    @RequestMapping(value = "recentResults", method = RequestMethod.GET)
    public String view(YukonUserContext userContext, ModelMap model, @ModelAttribute("filter") CollectionActionFilter filter,
                       @DefaultSort(dir=Direction.desc, sort="action") SortingParameters sorting,
                       @DefaultItemsPerPage(value=250) PagingParameters paging) {
        
        filter.setStatuses(Arrays.asList((CommandRequestExecutionStatus.values())));
        Direction dir = sorting.getDirection();
        ResultSortBy sortBy = ResultSortBy.valueOf(sorting.getSort());
        setupModel(userContext, model, filter, sorting, dir, sortBy);
        
        return "recentResults.jsp";
    }
    
    @RequestMapping(value = "filter", method = RequestMethod.GET)
    public String filter(YukonUserContext userContext, ModelMap model, @ModelAttribute("filter") CollectionActionFilter filter,
                         @DefaultSort(dir=Direction.desc, sort="action") SortingParameters sorting,
                         @DefaultItemsPerPage(value=250) PagingParameters paging) {
        
        Direction dir = sorting.getDirection();
        ResultSortBy sortBy = ResultSortBy.valueOf(sorting.getSort());
        setupModel(userContext, model, filter, sorting, dir, sortBy);
        SearchResults<CollectionActionFilteredResult> results = collectionActionDao.getCollectionActionFilteredResults(filter, paging, sortBy.getValue(), dir);

        model.addAttribute("recentActions", results);
        
        return "recentResults.jsp";
    }
    
    private void setupModel(YukonUserContext userContext, ModelMap model, CollectionActionFilter filter,
                            SortingParameters sorting, Direction dir, ResultSortBy sortBy) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        if (StringUtils.isEmpty(filter.getAction()) || filter.getAction().equals( "Select All")) {
            filter.setActions(Arrays.asList((CollectionAction.values())));
        } else {
            filter.setActions(Collections.singletonList(CollectionAction.valueOf(filter.getAction())));
        }
        
        List<CommandRequestExecutionStatus> statusList = new ArrayList<>();
        statusList.add(CommandRequestExecutionStatus.STARTED);
        statusList.add(CommandRequestExecutionStatus.COMPLETE);
        statusList.add(CommandRequestExecutionStatus.FAILED);
        statusList.add(CommandRequestExecutionStatus.CANCELLED);

        model.addAttribute("filter", filter);
        model.addAttribute("statuses", statusList);
        model.addAttribute("actions", Arrays.asList((CollectionAction.values())));
                
        for (ResultSortBy columnHeader : ResultSortBy.values()) {
            String text = accessor.getMessage(columnHeader);
            SortableColumn col = SortableColumn.of(dir, columnHeader == sortBy, text, columnHeader.name());
            model.addAttribute(columnHeader.name(), col);
        }
        model.addAttribute("detail", accessor.getMessage("yukon.web.modules.tools.recentResults.detail"));
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
