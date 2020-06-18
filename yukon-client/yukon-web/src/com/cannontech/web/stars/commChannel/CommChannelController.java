package com.cannontech.web.stars.commChannel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.model.PaoModelFactory;
import com.cannontech.common.device.port.BaudRate;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.JsonUtils;
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

@Controller
@RequestMapping("/device/commChannel")
public class CommChannelController {

    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String baseKey = "yukon.web.modules.operator.commChannel.";
    private static final Logger log = YukonLogManager.getLogger(CommChannelController.class);
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private CommChannelValidator<? extends PortBase<?>> commChannelValidator;
    @Autowired private CommChannelSetupHelper commChanelSetupHelper;
    @Autowired private ServerDatabaseCache dbCache;
    private static final List<PaoType> webSupportedCommChannelTypes = Stream.of(PaoType.TSERVER_SHARED, PaoType.TCPPORT, PaoType.UDPPORT, PaoType.LOCAL_SHARED)
                                                                            .sorted((p1, p2) -> p1.getDbString().compareTo(p2.getDbString()))
                                                                            .collect(Collectors.toList());

    @GetMapping("/list")
    public String list(ModelMap model, YukonUserContext userContext, HttpServletRequest request, FlashScope flash,
            @DefaultSort(dir = Direction.asc, sort = "name") SortingParameters sorting) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.commChannelUrl + "/");
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
    public String view(@PathVariable int id, ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("id", id);
        model.addAttribute("name", dbCache.getAllPaosMap().get(id).getPaoName());
        model.addAttribute("deviceNames", getDevicesNamesForPort(userContext, request, id));
        return "/commChannel/view.jsp";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable int id, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {
        try {
            String deleteUrl = helper.findWebServerUrl(request, userContext, ApiURL.commChannelUrl + "/" + id);
            String portName = dbCache.getAllPaosMap().get(id).getPaoName();

            ResponseEntity<? extends Object> deleteResponse = deleteCommChannel(userContext, request, deleteUrl);

            if (deleteResponse.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", portName));
                return "redirect:" + "/stars/device/commChannel/list";
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + "/stars/device/commChannel/" + id;
        } catch (RestClientException ex) {
            String portName = dbCache.getAllPaosMap().get(id).getPaoName();
            log.error("Error deleting comm Channel: {}. Error: {}", portName, ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", portName, ex.getMessage()));
            return "redirect:" + "/stars/device/commChannel/" + id;
        }
        return "redirect:" + "/stars/device/commChannel/list";
    }

    @GetMapping("/create")
    public String create(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        PortBase commChannel = new PortBase();
        if (model.containsAttribute("commChannel")) {
            commChannel = (PortBase) model.get("commChannel");
            if (commChannel.getType() != null) {
                commChanelSetupHelper.setupCommChannelFields(commChannel, model);
            }
        }
        model.addAttribute("baudRateList", BaudRate.values());
        commChannel.setType(PaoType.TCPPORT);
        setupDefaultFieldValue(commChannel, model);
        return "/commChannel/create.jsp";
    }

    @GetMapping("/create/{type}")
    public String create(ModelMap model, @PathVariable String type, @RequestParam String name,
            YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        PortBase commChannel = (PortBase) PaoModelFactory.getModel(PaoType.valueOf(type));
        if (model.containsAttribute("commChannel")) {
            commChannel = (PortBase) model.get("commChannel");
        } else {
            commChannel.setName(name);
            commChannel.setType(PaoType.valueOf(type));
        }
        commChanelSetupHelper.setupCommChannelFields(commChannel, model);
        setupDefaultFieldValue(commChannel, model);
        return "/commChannel/create.jsp";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("commChannel") PortBase commChannel, BindingResult result, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request, HttpServletResponse resp, ModelMap model) throws IOException {
        try {
            commChannelValidator.validate(commChannel, result);
            if (result.hasErrors()) {
                setupErrorFields(resp, commChannel, model, userContext, result);
                return "/commChannel/create.jsp";
            }
            String url = helper.findWebServerUrl(request, userContext, ApiURL.commChannelUrl);
            ResponseEntity<? extends Object> response =
                    apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, Object.class, commChannel);

            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(commChannel, "commChannel");
                result = helper.populateBindingError(result, error, response);
                if (result.hasErrors()) {
                    setupErrorFields(resp, commChannel, model, userContext, result);
                    return "/commChannel/create.jsp";
                }
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                HashMap<String, Integer> savedCommChannel = (HashMap<String, Integer>) response.getBody();
                Map<String, Object> json = new HashMap<>();
                json.put("id", savedCommChannel.get("id"));
                resp.setContentType("application/json");
                JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", commChannel.getName()));
                return null;
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return null;
        } catch (RestClientException ex) {
            log.error("Error creating comm channel: {}. Error: {}", commChannel.getName(), ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.save.error", commChannel.getName(), ex.getMessage()));
            return null;
        }
        return null;
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
     * Returns comma separated device names for that port 
     */
    private String getDevicesNamesForPort(YukonUserContext userContext, HttpServletRequest request, int portId) {
        String assignedDevicesUrl = helper.findWebServerUrl(request, userContext,
                ApiURL.commChannelUrl + "/" + portId + "/devicesAssigned");
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

    private void setupErrorFields(HttpServletResponse resp, PortBase commChannel, ModelMap model, YukonUserContext userContext,
            BindingResult result) {
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        commChanelSetupHelper.setupCommChannelFields(commChannel, model);
        commChanelSetupHelper.setupPhysicalPort(commChannel, model);
        commChanelSetupHelper.setupGlobalError(result, model, userContext, commChannel.getType());
        model.addAttribute("commChannel", commChannel);
        model.addAttribute("webSupportedCommChannelTypes", webSupportedCommChannelTypes);
    }

    private void setupDefaultFieldValue(PortBase commChannel, ModelMap model) {
        commChannel.setBaudRate(BaudRate.BAUD_1200);
        commChannel.setEnable(true);
        model.addAttribute("commChannel", commChannel);
        model.addAttribute("webSupportedCommChannelTypes", webSupportedCommChannelTypes);
    }
}
