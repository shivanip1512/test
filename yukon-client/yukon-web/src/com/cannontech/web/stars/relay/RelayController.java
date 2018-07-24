package com.cannontech.web.stars.relay;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnDeviceSearchCriteria;
import com.cannontech.common.rfn.model.RfnRelay;
import com.cannontech.common.rfn.service.RfnRelayService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
public class RelayController {
    
    @Autowired private PaoDao paoDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnRelayService rfnRelayService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private static final String baseKey = "yukon.web.modules.operator.relayDetail.";
    
    @RequestMapping(value = { "/relay", "/relay/" }, method = RequestMethod.GET)
    public String list(ModelMap model, YukonUserContext userContext, @DefaultSort(dir=Direction.asc, sort="name") SortingParameters sorting, 
                       PagingParameters paging, @RequestParam(value = "selectedName", required = false) String name, 
                       @RequestParam(value = "selectedSerialNumber", required = false) String serialNumber) throws ServletException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        RfnDeviceSearchCriteria criteria = new RfnDeviceSearchCriteria(name, serialNumber);
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
    public String home(ModelMap model, int deviceId) {
        RfnDevice device;
        
        try {
            device = rfnDeviceDao.getDeviceForId(deviceId);
        } catch (Exception e) {
         // Allows clicking on an Rfn Relay Template to not throw a Yukon Exception
            device = new RfnDevice(paoDao.getYukonPAOName(deviceId), paoDao.getYukonPao(deviceId), null);
        }
        
        model.addAttribute("deviceId", deviceId);
        model.addAttribute("deviceName", device.getName());

        return "/relay/relayHome.jsp";
    }
    
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_DELETE)
    @RequestMapping(value="/relay/{id}", method=RequestMethod.DELETE)
    public String delete(FlashScope flash, @PathVariable int id, ModelMap model) {
        boolean success = rfnRelayService.deleteRelay(id);
        if (success) {
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success"));
            return "redirect:/stars/relay";
        } else {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.failure"));
            model.addAttribute("deviceId", id);
            return "redirect:/stars/relay/home";
        }
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