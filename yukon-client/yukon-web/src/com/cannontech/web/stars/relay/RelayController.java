package com.cannontech.web.stars.relay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.rfn.model.RfnDeviceSearchCriteria;
import com.cannontech.common.rfn.model.RfnRelay;
import com.cannontech.common.rfn.service.RfnRelayService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.google.common.collect.Lists;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.VIEW)
public class RelayController {
    
    @Autowired private DeviceDao deviceDao;
    @Autowired private RfnRelayService rfnRelayService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private PaoNotesService paoNotesService;
    
    private static final String baseKey = "yukon.web.modules.operator.relayDetail.";
    
    @RequestMapping(value = { "/relay", "/relay/" }, method = RequestMethod.GET)
    public String list(ModelMap model, YukonUserContext userContext, @DefaultSort(dir=Direction.asc, sort="name") SortingParameters sorting, 
                       PagingParameters paging, @ModelAttribute("criteria") RfnDeviceSearchCriteria criteria, String type) throws ServletException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        model.addAttribute("criteria", criteria);
        
        List<PaoType> relayTypes = new ArrayList<PaoType>(PaoType.getRfRelayTypes());
        if (Strings.isNotBlank(type)) {
            PaoType relayType = PaoType.valueOf(type);
            if (relayType != null) {
                relayTypes = Collections.singletonList(relayType);
                model.addAttribute("selectedRelayType", relayType);
            }
        }

        Set<RfnRelay> relays = rfnRelayService.searchRelays(criteria, relayTypes);
        
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
        } else if (sortBy == RelaySortBy.type) {
            comparator = (o1, o2) -> o1.getType().compareTo(o2.getType());
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
        
        List<Integer> notesIds = paoNotesService.getPaoIdsWithNotes(itemList.stream()
                                                                            .map(relay -> relay.getDeviceId())
                                                                            .collect(Collectors.toList()));
        model.addAttribute("notesIds", notesIds);
        model.addAttribute("relayTypes", PaoType.getRfRelayTypes());
        
        return "/relay/list.jsp";
    }
    
    @RequestMapping("/relay/home")
    public String home(ModelMap model, int deviceId) {
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        
        model.addAttribute("deviceId", deviceId);
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(device).getName());

        return "/relay/relayHome.jsp";
    }
    
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.OWNER)
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
        serialNumber,
        type;

        @Override
        public String getFormatKey() {
            return "yukon.common." + name();
        }
    }

}