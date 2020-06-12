package com.cannontech.web.stars.virtualDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.google.common.collect.Lists;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.VIEW)
public class VirtualDeviceController {
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @GetMapping("virtualDevices")
    public String virtualDevices(ModelMap model, @DefaultSort(dir = Direction.asc, sort = "name") SortingParameters sorting, 
                                 @DefaultItemsPerPage(value=250) PagingParameters paging, YukonUserContext userContext) {
        List<LiteYukonPAObject> virtualDevices = getMockVirtualDevices();
        SearchResults<LiteYukonPAObject> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, virtualDevices.size());

        VirtualSortBy sortBy = VirtualSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();

        List<LiteYukonPAObject>itemList = Lists.newArrayList(virtualDevices);
        
        Comparator<LiteYukonPAObject> comparator = (o1, o2) -> o1.getPaoName().compareTo(o2.getPaoName());
        if (sortBy == VirtualSortBy.status) {
            comparator = (o1, o2) -> o1.getDisableFlag().compareTo(o2.getDisableFlag());
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (VirtualSortBy column : VirtualSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }

        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, virtualDevices.size());
        searchResult.setResultList(itemList);
        model.addAttribute("virtualDevices", searchResult);
        return "/virtualDevices/list.jsp";
    }
    
    private List<LiteYukonPAObject> getMockVirtualDevices() {
        List<LiteYukonPAObject> list = new ArrayList<>();
        list.add(new LiteYukonPAObject(123, "VirtualDevice001", PaoType.VIRTUAL_SYSTEM, null, "Y"));
        list.add(new LiteYukonPAObject(124, "VirtualDevice002", PaoType.VIRTUAL_SYSTEM, null, "N"));
        list.add(new LiteYukonPAObject(125, "VirtualDevice003", PaoType.VIRTUAL_SYSTEM, null, "Y"));
        return list;
    }
    
    public enum VirtualSortBy implements DisplayableEnum {

        name,
        status;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.virtualDevices.list." + name();
        }
    }
}
