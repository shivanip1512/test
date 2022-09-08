package com.cannontech.web.widget;

import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.service.impl.PortServiceImpl.CommChannelSortBy;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display devices which are linked to comm channel
 */
@Controller 
@RequestMapping("/commChannelLinkedDeviceWidget/*")
public class CommChannelLinkedDeviceWidget extends AdvancedWidgetControllerBase {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    @Autowired
    public CommChannelLinkedDeviceWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
    }

    @SuppressWarnings("unchecked") 
    @GetMapping("render")
    public String render(ModelMap model, HttpServletRequest request, YukonUserContext userContext,
            @DefaultSort(dir = Direction.asc, sort = "name") SortingParameters sorting,
            @DefaultItemsPerPage(value = 25) PagingParameters paging) throws ServletRequestBindingException, URISyntaxException {
        CommChannelSortBy sortBy = CommChannelSortBy.valueOf(sorting.getSort());
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");

        String assignedDevicesUrl = helper.findWebServerUrl(request, userContext,
                ApiURL.commChannelUrl + "/" + deviceId + "/devicesAssigned");
        SearchResults<DeviceBaseModel> searchResults = new SearchResults<DeviceBaseModel>();
        
        Direction dir = sorting.getDirection();
        
        URIBuilder ub = new URIBuilder(assignedDevicesUrl);
        ub.addParameter("sort", sortBy.name());
        ub.addParameter("dir", dir.name());
        ub.addParameter("itemsPerPage", Integer.toString(paging.getItemsPerPage()));
        ub.addParameter("page", Integer.toString(paging.getPageNumber()));

        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForParameterizedTypeObject(userContext,
                request, ub.toString(), HttpMethod.GET, DeviceBaseModel.class, Object.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            searchResults = (SearchResults<DeviceBaseModel>) response.getBody();
        }
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (CommChannelSortBy column : CommChannelSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        model.addAttribute("deviceId", deviceId);
        model.addAttribute("searchResult", searchResults);
        return "commChannelLinkedDeviceWidget/render.jsp";
    }
}
