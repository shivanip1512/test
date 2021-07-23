package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.cannontech.amr.meter.model.PointSortField;
import com.cannontech.common.device.model.DeviceBaseModel;
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
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.pao.service.LiteYukonPoint;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.stars.commChannel.CommChannelController.CommChannelSortBy;
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
                         @DefaultItemsPerPage(value=250) PagingParameters paging)
            throws ServletRequestBindingException {
        CommChannelSortBy sortBy = CommChannelSortBy.valueOf(sorting.getSort());
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");

        String assignedDevicesUrl = helper.findWebServerUrl(request, userContext, ApiURL.commChannelUrl + "/" + deviceId + "/devicesAssigned");
        List<DeviceBaseModel> devicesList = new ArrayList<>();

        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForList(userContext, request, assignedDevicesUrl,
                DeviceBaseModel.class, HttpMethod.GET, DeviceBaseModel.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            devicesList = (List<DeviceBaseModel>) response.getBody();
        }
        
        Direction dir = sorting.getDirection();
        Comparator<DeviceBaseModel> comparator = (o1, o2) -> {
            return o1.getName().compareToIgnoreCase(o2.getName());
        };
        if (sortBy == CommChannelSortBy.type) {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            comparator = (o1, o2) -> accessor.getMessage(o1.getPaoIdentifier().getPaoType().getFormatKey())
                                             .compareToIgnoreCase(accessor.getMessage(o2.getPaoIdentifier().getPaoType().getFormatKey()));
        }
        if (sortBy == CommChannelSortBy.status) {
            comparator = (o1, o2) -> o1.getEnable().compareTo(o2.getEnable());
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(devicesList, comparator);
        
        model.addAttribute("devicesList", devicesList);
        model.addAttribute("deviceId", deviceId);
        
        SearchResults<DeviceBaseModel> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, devicesList.size());
        
        devicesList = devicesList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, devicesList.size());
        searchResult.setResultList(devicesList);
        
        model.addAttribute("searchResult", searchResult);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (CommChannelSortBy column : CommChannelSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        
        return "commChannelLinkedDeviceWidget/render.jsp";
    }
}
