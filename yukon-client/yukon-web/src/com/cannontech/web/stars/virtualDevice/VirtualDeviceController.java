package com.cannontech.web.stars.virtualDevice;

import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.utils.URIBuilder;
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
import com.cannontech.common.device.virtualDevice.VirtualDeviceBaseModel;
import com.cannontech.common.device.virtualDevice.VirtualDeviceSortableField;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PaginatedResponse;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;

import com.cannontech.common.pao.PaoType;
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
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("virtualMeterType", PaoType.VIRTUAL_METER);
        
        VirtualSortBy sortBy = VirtualSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        
        for (VirtualSortBy column : VirtualSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }

        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.virtualDeviceUrl);
            URIBuilder ub = new URIBuilder(url);
            ub.addParameter("sort", sortBy.getValue().name());
            ub.addParameter("direction", dir.name());
            ub.addParameter("itemsPerPage", Integer.toString(paging.getItemsPerPage()));
            ub.addParameter("page", Integer.toString(paging.getPageNumber()));

            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForParameterizedTypeObject(userContext, request, ub.toString(), 
                                                                                        HttpMethod.GET, VirtualDeviceBaseModel.class, Object.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                PaginatedResponse<VirtualDeviceBaseModel> pageResponse = (PaginatedResponse) response.getBody();
                model.addAttribute("virtualDevices", pageResponse);
            }
                        
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
        } catch (RestClientException ex) {
            log.error("Error retrieving virtual devices. Error: {}", ex.getMessage(), ex);
            String virtualDevicesLabel = accessor.getMessage("yukon.web.modules.operator.virtualDevices.list.pageName");
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.retrieve.error", virtualDevicesLabel, ex.getMessage()));
        } catch (URISyntaxException e) {
            log.error("URI syntax error while creating builder for retrieving virtual devices.", e);
            String virtualDevicesLabel = accessor.getMessage("yukon.web.modules.operator.virtualDevices.list.pageName");
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.retrieve.error", virtualDevicesLabel, e.getMessage()));        }
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
        String paoName = dbCache.getAllPaosMap().get(id).getPaoName();
        try {
            String deleteUrl = helper.findWebServerUrl(request, userContext, ApiURL.virtualDeviceUrl + "/" + id);
            ResponseEntity<? extends Object> deleteResponse = apiRequestHelper.callAPIForObject(userContext, request, deleteUrl,
                                                                                                HttpMethod.DELETE, Object.class, Integer.class);

            if (deleteResponse.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", paoName));
                return "redirect:" + "/stars/virtualDevices";
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + "/stars/virtualDevice/" + id;
        } catch (RestClientException ex) {
            log.error("Error deleting virtual device: {}. Error: {}", paoName, ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", paoName, ex.getMessage()));
            return "redirect:" + "/stars/virtualDevice/" + id;
        }
        return "redirect:" + "/stars/virtualDevices";
    }
    
    public enum VirtualSortBy implements DisplayableEnum {

        name(VirtualDeviceSortableField.PAO_NAME),
        meterNumber(VirtualDeviceSortableField.METER_NUMBER),
        status(VirtualDeviceSortableField.DISABLE_FLAG);
        
        private final VirtualDeviceSortableField value;
        
        private VirtualSortBy(VirtualDeviceSortableField value) {
            this.value = value;
        }

        @Override
        public String getFormatKey() {
            return "yukon.common." + name();
        }

        public VirtualDeviceSortableField getValue() {
            return value;
        }
    }
}
