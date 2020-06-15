package com.cannontech.web.stars.virtualDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.virtualDevice.VirtualDeviceModel;
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
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.google.common.collect.Lists;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.VIEW)
public class VirtualDeviceController {
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private ServerDatabaseCache dbCache;

    private static final Logger log = YukonLogManager.getLogger(VirtualDeviceController.class);
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    
    @GetMapping("virtualDevices")
    public String virtualDevices(ModelMap model, @DefaultSort(dir = Direction.asc, sort = "name") SortingParameters sorting, 
                                 @DefaultItemsPerPage(value=250) PagingParameters paging, YukonUserContext userContext, 
                                 HttpServletRequest request, FlashScope flash) {
        try {
            //String url = helper.findWebServerUrl(request, userContext, ApiURL.virtualDeviceListUrl);
            //List<VirtualDeviceModel> virtualDevices = getVirtualDeviceListResponse(userContext, request, url);
            
            List<VirtualDeviceModel> virtualDevices = getMockVirtualDevices();
            SearchResults<VirtualDeviceModel> searchResult = new SearchResults<>();
            int startIndex = paging.getStartIndex();
            int itemsPerPage = paging.getItemsPerPage();
            int endIndex = Math.min(startIndex + itemsPerPage, virtualDevices.size());
    
            VirtualSortBy sortBy = VirtualSortBy.valueOf(sorting.getSort());
            Direction dir = sorting.getDirection();
    
            List<VirtualDeviceModel>itemList = Lists.newArrayList(virtualDevices);
            
            Comparator<VirtualDeviceModel> comparator = (o1, o2) -> o1.getName().compareTo(o2.getName());
            if (sortBy == VirtualSortBy.status) {
                comparator = (o1, o2) -> o1.getEnable().compareTo(o2.getEnable());
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
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
        }
        return "/virtualDevices/list.jsp";
    }
    
    @GetMapping("virtualDevice/{id}")
    public String view(ModelMap model, @PathVariable int id) {
        model.addAttribute("mode", PageEditMode.VIEW);
        model.addAttribute("id", id);
        model.addAttribute("name", dbCache.getAllPaosMap().get(id).getPaoName());
        return "/virtualDevices/view.jsp";
    }
    
    @DeleteMapping("virtualDevice/{id}/delete")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public String delete(@PathVariable int id, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {
        try {
            String deleteUrl = helper.findWebServerUrl(request, userContext, ApiURL.virtualDeviceUrl + id);
            String paoName = dbCache.getAllPaosMap().get(id).getPaoName();

            ResponseEntity<? extends Object> deleteResponse = deleteVirtualDevice(userContext, request, deleteUrl);

            if (deleteResponse.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", paoName));
                return "redirect:" + "/stars/virtualDevices";
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + "/stars/virtualDevice/" + id;
        } catch (RestClientException ex) {
            String paoName = dbCache.getAllPaosMap().get(id).getPaoName();
            log.error("Error deleting virtual device: {}. Error: {}", paoName, ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", paoName, ex.getMessage()));
            return "redirect:" + "/stars/virtualDevice/" + id;
        }
        return "redirect:" + "/stars/virtualDevices";
    }
    
    /**
     * Get the response for virtual device delete
     */
    private ResponseEntity<? extends Object> deleteVirtualDevice(YukonUserContext userContext, HttpServletRequest request, String url) throws RestClientException {
        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, 
                                                                                      request,
                                                                                      url, 
                                                                                      HttpMethod.DELETE, 
                                                                                      Object.class, 
                                                                                      Integer.class);
        return response;
    }
    
    /**
     * Get the Virtual Devices from API
     */
    private List<VirtualDeviceModel> getVirtualDeviceListResponse(YukonUserContext userContext, HttpServletRequest request, String url) {
        List<VirtualDeviceModel> virtualDeviceList = new ArrayList<>();

        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForList(userContext,
                                                                                    request,
                                                                                    url,
                                                                                    DeviceBaseModel.class,
                                                                                    HttpMethod.GET,
                                                                                    VirtualDeviceModel.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            virtualDeviceList = (List<VirtualDeviceModel>) response.getBody();
        }
        return virtualDeviceList;
    }
    
    private List<VirtualDeviceModel> getMockVirtualDevices() {
        List<VirtualDeviceModel> list = new ArrayList<>();
        VirtualDeviceModel d1 = new VirtualDeviceModel();
        d1.setEnable(true);
        d1.setId(123);
        d1.setName("VirtualDevice001");
        d1.setType(PaoType.VIRTUAL_SYSTEM);
        list.add(d1);
        VirtualDeviceModel d2 = new VirtualDeviceModel();
        d2.setEnable(false);
        d2.setId(124);
        d2.setName("VirtualDevice002");
        d2.setType(PaoType.VIRTUAL_SYSTEM);
        list.add(d2);
        VirtualDeviceModel d3 = new VirtualDeviceModel();
        d3.setEnable(true);
        d3.setId(125);
        d3.setName("VirtualDevice003");
        d3.setType(PaoType.VIRTUAL_SYSTEM);
        list.add(d3);
        VirtualDeviceModel d4 = new VirtualDeviceModel();
        d4.setEnable(true);
        d4.setId(126);
        d4.setName("VirtualDevice004");
        d4.setType(PaoType.VIRTUAL_SYSTEM);
        list.add(d4);
        return list;
    }
    
    public enum VirtualSortBy implements DisplayableEnum {

        name,
        status;

        @Override
        public String getFormatKey() {
            return "yukon.common." + name();
        }
    }
}
