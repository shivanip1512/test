package com.cannontech.web.stars.commChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;

@Controller
@RequestMapping("/device/commChannel")
public class CommChannelController {

    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String baseKey = "yukon.web.modules.operator.commChannel.";
    private static final Logger log = YukonLogManager.getLogger(CommChannelController.class);
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private ServerDatabaseCache dbCache;

    @GetMapping("/list")
    public String list(ModelMap model, YukonUserContext userContext, HttpServletRequest request, FlashScope flash,
            @DefaultSort(dir = Direction.asc, sort = "name") SortingParameters sorting) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.commChannelListUrl);
            List<DeviceBaseModel> commChannelList = getDeviceBaseModelResponse(userContext, request, url);

            CommChannelSortBy sortBy = CommChannelSortBy.valueOf(sorting.getSort());
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
            Collections.sort(commChannelList, comparator);

            model.addAttribute("commChannelList", commChannelList);

            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            for (CommChannelSortBy column : CommChannelSortBy.values()) {
                String text = accessor.getMessage(column);
                SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
                model.addAttribute(column.name(), col);
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
        }
        return "/commChannel/list.jsp";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable int id, ModelMap model,YukonUserContext userContext,HttpServletRequest request) {
        
        String deviceNames = getDevicesNamesForPort(userContext, request, id);
        model.addAttribute("id", id);
        model.addAttribute("name", dbCache.getAllPaosMap().get(id).getPaoName());
        model.addAttribute("deviceNames", deviceNames);
        return "/commChannel/view.jsp";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable int id, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {
        try {
            String deleteUrl = helper.findWebServerUrl(request, userContext, ApiURL.commChannelDeleteUrl + id);

            // Fetches device names using this port
            String deviceNames = getDevicesNamesForPort(userContext, request, id);

            String portName = dbCache.getAllPaosMap().get(id).getPaoName();

            if (deviceNames != null) {
                log.error("Error deleting comm channel: {}. Error: {}", portName, deviceNames);
                flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.error", portName, deviceNames));
                return "redirect:" + "/stars/device/commChannel/" + id;
            }

            ResponseEntity<? extends Object> deleteResponse = deleteCommChannel(userContext, request, deleteUrl);

            if (deleteResponse.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", portName));
                return "redirect:" + "/stars/device/commChannel/list";
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + "/stars/device/commChannel/" + id;
        }
        return "redirect:" + "/stars/device/commChannel/list";
    }

    public enum CommChannelSortBy implements DisplayableEnum {
        name,
        type,
        status;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }

    /**
     * Get the response for comm channel delete
     */
    private ResponseEntity<? extends Object> deleteCommChannel(YukonUserContext userContext, HttpServletRequest request, String url) throws RestClientException {
        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, 
                                                                                      request,
                                                                                      url, 
                                                                                      HttpMethod.DELETE, 
                                                                                      Object.class, 
                                                                                      Integer.class);
        return response;
    }

    /**
     * Get devices names using port 
     */
    private String getDevicesNamesForPort(YukonUserContext userContext, HttpServletRequest request, int portId) {
        String assignedDevicesUrl = helper.findWebServerUrl(request, userContext, ApiURL.commChannelDevicesAssignedUrl + portId);
        List<DeviceBaseModel> devicesList = getDeviceBaseModelResponse(userContext, request, assignedDevicesUrl);

        if (!devicesList.isEmpty()) {
            return devicesList.stream()
                              .map(device -> device.getName())
                              .collect(Collectors.joining(", "));
        } else {
            return null;
        }
    }

    /**
     * Get the response in form of DevicesBaseModel
     */
    private List<DeviceBaseModel> getDeviceBaseModelResponse(YukonUserContext userContext, HttpServletRequest request, String url) {
        List<DeviceBaseModel> deviceBaseModelList = new ArrayList<>();

        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForList(userContext,
                                                                                    request,
                                                                                    url,
                                                                                    DeviceBaseModel.class,
                                                                                    HttpMethod.GET,
                                                                                    DeviceBaseModel.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            deviceBaseModelList = (List<DeviceBaseModel>) response.getBody();
        }
        return deviceBaseModelList;
    }
}
