package com.cannontech.web.stars.relay;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.rfn.model.RfnDeviceSearchCriteria;
import com.cannontech.common.rfn.model.RfnRelay;
import com.cannontech.common.rfn.service.RfnRelayService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.google.common.collect.Lists;

@Controller
public class RelayController {
    
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private RfnRelayService rfnRelayService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @RequestMapping(value = { "/relay", "/relay/" }, method = RequestMethod.GET)
    public String list(HttpServletRequest request, ModelMap model, YukonUserContext userContext, @DefaultSort(dir=Direction.asc, sort="name") SortingParameters sorting, PagingParameters paging) throws ServletException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        RfnDeviceSearchCriteria criteria = new RfnDeviceSearchCriteria();
        criteria.setName(ServletRequestUtils.getStringParameter(request, "selectedName"));
        criteria.setSerialNumber(ServletRequestUtils.getStringParameter(request, "selectedSerialNumber"));
        model.addAttribute("criteria", criteria);

        Set<RfnRelay> relays = rfnRelayService.searchRelays(criteria);
        
        SearchResults<RfnRelay> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, relays.size());

        RelaySortBy sortBy = RelaySortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();

        List<RfnRelay>itemList = Lists.newArrayList(relays);
        
        Comparator<RfnRelay> comparator = (o1, o2) -> o1.getName().compareTo(o2.getName());
        if (sortBy == RelaySortBy.serialNumber) {
            comparator = (o1, o2) -> o1.getSerialNumber().compareTo(o2.getSerialNumber());
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);

        for (RelaySortBy column : RelaySortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }

        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, relays.size());
        searchResult.setResultList(itemList);
        
        model.addAttribute("relays", searchResult);
        
        return "/relay/list.jsp";
    }
    
    @RequestMapping("/relay/home")
    public String home(HttpServletRequest request, ModelMap model, LiteYukonUser user, int deviceId) {
        
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);        
        model.addAttribute("deviceId", deviceId);
        
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(device).getName());

        return "/relay/relayHome.jsp";
    }
    
    public enum RelaySortBy implements DisplayableEnum {

        name,
        serialNumber;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.relays.list." + name();
        }
    }

}