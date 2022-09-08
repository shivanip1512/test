package com.cannontech.web.stars.signalTransmitter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URIBuilder;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.terminal.model.DataFormat;
import com.cannontech.common.device.terminal.model.IdentifierFormat;
import com.cannontech.common.device.terminal.model.PagingTapTerminal;
import com.cannontech.common.device.terminal.model.Protocol;
import com.cannontech.common.device.terminal.model.SNPPTerminal;
import com.cannontech.common.device.terminal.model.SignalTransmitterModelFactory;
import com.cannontech.common.device.terminal.model.TNPPTerminal;
import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.device.terminal.model.TerminalCopy;
import com.cannontech.common.device.terminal.model.WCTPTerminal;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PaginatedResponse;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/device/signalTransmitter/")
public class SignalTransmitterController {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private SignalTransmitterValidator<? extends TerminalBase<?>> signalTransmitterValidator;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private static final Logger log = YukonLogManager.getLogger(SignalTransmitterController.class);
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String bindingResultKey = "org.springframework.validation.BindingResult.signalTransmitter";
    private static final String baseKey = "yukon.web.modules.operator.signalTransmitter.";
    private static final String redirectListPageLink = "redirect:/stars/device/signalTransmitter/list";

    private static final List<PaoType> webSupportedSignalTransmitterTypes = Stream
            .of(PaoType.WCTP_TERMINAL, PaoType.TAPTERMINAL, PaoType.SNPP_TERMINAL, PaoType.TNPP_TERMINAL)
            .sorted((p1, p2) -> p1.getDbString().compareTo(p2.getDbString()))
            .collect(Collectors.toList());

    @GetMapping("list")
    public String list(ModelMap model, @DefaultSort(dir = Direction.asc, sort = "name") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging,
            String filterValueName,
            YukonUserContext userContext, FlashScope flash,
            HttpServletRequest request) throws URISyntaxException {
        ResponseEntity<? extends Object> response = null;
        try {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            SignalTransmitterSortBy sortBy = SignalTransmitterSortBy.valueOf(sorting.getSort());
            Direction dir = sorting.getDirection();

            List<SortableColumn> columns = new ArrayList<>();
            for (SignalTransmitterSortBy column : SignalTransmitterSortBy.values()) {
                String text = accessor.getMessage(column);
                SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
                columns.add(col);
                model.addAttribute(column.name(), col);
            }
            String url = helper.findWebServerUrl(request, userContext, ApiURL.pagingTerminalUrl);
            URIBuilder ub = new URIBuilder(url);
            ub.addParameter("sort", sortBy.getValue().name());
            ub.addParameter("dir", dir.name());
            ub.addParameter("name", filterValueName);
            ub.addParameter("itemsPerPage", Integer.toString(paging.getItemsPerPage()));
            ub.addParameter("pageNumber", Integer.toString(paging.getPageNumber()));

            response = apiRequestHelper.callAPIForParameterizedTypeObject(userContext, request, ub.toString(), HttpMethod.GET,
                    TerminalBase.class);
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        } catch (RestClientException ex) {
            log.error("Error retrieving details: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.signalTransmitter.filter.error",
                    ex.getMessage()));
            return redirectListPageLink;
        }
        PaginatedResponse<TerminalBase> signalTransmitters = new PaginatedResponse<>();
        if (response.getStatusCode() == HttpStatus.OK) {
            signalTransmitters = (PaginatedResponse) response.getBody();
        }

        model.addAttribute("signalTransmitters", signalTransmitters);
        model.addAttribute("filterValueName", filterValueName);
        return "/signalTransmitter/list.jsp";
    }

    @GetMapping("/{id}/edit")
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.pagingTerminalUrl + "/" + id);
            model.addAttribute("mode", PageEditMode.EDIT);
            TerminalBase terminalBase = retrieveSignalTransmitter(userContext, request, id, url);
            if (terminalBase == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "signalTransmitter.retrieve.error"));
                return redirectListPageLink;
            } else if (model.containsAttribute("signalTransmitter")) {
                terminalBase = (TerminalBase) model.get("signalTransmitter");
                terminalBase.setId(id);
            }

            model.addAttribute("signalTransmitter", terminalBase);
            model.addAttribute("selectedSignalTransmitterType", terminalBase.getType());
            setupModel(model, request, userContext);
            return "/signalTransmitter/view.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        }
    }

    @GetMapping("/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            model.addAttribute("mode", PageEditMode.VIEW);
            String url = helper.findWebServerUrl(request, userContext, ApiURL.pagingTerminalUrl + "/" + id);

            TerminalBase terminalBase = retrieveSignalTransmitter(userContext, request, id, url);
            if (terminalBase == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "retrieve.error"));
                return redirectListPageLink;
            }
            model.addAttribute("selectedSignalTransmitterType", terminalBase.getType());
            model.addAttribute("signalTransmitter", terminalBase);
            return "/signalTransmitter/view.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        }

    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        TerminalBase signalTransmitter = new TerminalBase<>();
        if (model.containsAttribute("signalTransmitter")) {
            signalTransmitter = (TerminalBase) model.get("signalTransmitter");
        }
        model.addAttribute("signalTransmitter", signalTransmitter);
        model.addAttribute("selectedSignalTransmitterType", signalTransmitter.getType());
        setupModel(model, request, userContext);
        return "/signalTransmitter/view.jsp";
    }

    @RequestMapping(value = "create/{type}", method = RequestMethod.GET)
    public String create(ModelMap model, @PathVariable String type, @RequestParam String name,
            YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        TerminalBase signalTransmitter = null;
        if (model.containsAttribute("signalTransmitter")) {
            switch (PaoType.valueOf(type)) {
            case WCTP_TERMINAL:
                signalTransmitter = (WCTPTerminal) model.get("signalTransmitter");
                break;
            case SNPP_TERMINAL:
                signalTransmitter = (SNPPTerminal) model.get("signalTransmitter");
                break;
            case TNPP_TERMINAL:
                signalTransmitter = (TNPPTerminal) model.get("signalTransmitter");
                break;
            case TAPTERMINAL:
                signalTransmitter = (PagingTapTerminal) model.get("signalTransmitter");
                break;
            }
        } else {
            signalTransmitter = SignalTransmitterModelFactory.createSignalTransmitter(PaoType.valueOf(type));
            signalTransmitter.setName(name);
            signalTransmitter.setType(PaoType.valueOf(type));
        }
        model.addAttribute("signalTransmitter", signalTransmitter);
        model.addAttribute("selectedSignalTransmitterType", type);
        setupModel(model, request, userContext);
        return "/signalTransmitter/details.jsp";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("signalTransmitter") TerminalBase signalTransmitter, BindingResult result,
            YukonUserContext userContext, FlashScope flash, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        try {
            signalTransmitterValidator.validate(signalTransmitter, result);
            if (result.hasErrors()) {
                return bindAndForward(signalTransmitter, result, redirectAttributes);
            }
            String url = null;
            ResponseEntity<? extends Object> apiResponse = null;
            if (signalTransmitter.getId() == null) {
                url = helper.findWebServerUrl(request, userContext, ApiURL.pagingTerminalUrl);
                apiResponse = saveSignalTransmitter(userContext, request, url, signalTransmitter, HttpMethod.POST);
            } else {
                url = helper.findWebServerUrl(request, userContext, ApiURL.pagingTerminalUrl) + "/" + signalTransmitter.getId();
                apiResponse = saveSignalTransmitter(userContext, request, url, signalTransmitter, HttpMethod.PUT);
            }

            if (apiResponse.getStatusCode() == HttpStatus.OK || apiResponse.getStatusCode() == HttpStatus.CREATED) {
                TerminalBase signalTransmitterCreated = (TerminalBase) apiResponse.getBody();
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", signalTransmitter.getName()));
                return "redirect:/stars/device/signalTransmitter/" + signalTransmitterCreated.getId();
            }

            if (apiResponse.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(signalTransmitter, "signalTransmitter");
                result = helper.populateBindingErrorForApiErrorModel(result, error, apiResponse, "yukon.web.error.");
                return bindAndForward(signalTransmitter, result, redirectAttributes);
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        } catch (RestClientException ex) {
            log.error("Error creating signal transmitter: {}. Error: {}", signalTransmitter.getName(), ex.getMessage());
            flash.setError(
                    new YukonMessageSourceResolvable("yukon.web.api.save.error", signalTransmitter.getName(), ex.getMessage()));
            return redirectListPageLink;
        }
        return null;
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable int id, @ModelAttribute LMDelete lmDelete, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request) {

        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.pagingTerminalUrl + "/" + id);
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request,
                    url, HttpMethod.DELETE, Object.class, lmDelete);

            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", lmDelete.getName()));
                return redirectListPageLink;
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        } catch (RestClientException ex) {
            log.error("Error deleting signal transmitter : {}. Error: {}", lmDelete.getName(), ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", lmDelete.getName(), ex.getMessage()));
            return redirectListPageLink;
        }
        return redirectListPageLink;
    }

    @GetMapping("/{id}/renderCopyDialog")
    public String renderCopyDialog(@PathVariable int id, ModelMap model, YukonUserContext userContext) {

        PaoType selectedSignalTransmitterType = getPaoTypeForPaoId(id);
        TerminalCopy terminalCopy = new TerminalCopy();

        LiteYukonPAObject litePao = dbCache.getAllPaosMap().get(id);
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
        terminalCopy.setName(messageSourceAccessor.getMessage("yukon.common.copyof", litePao.getPaoName()));
        model.addAttribute("terminalCopy", terminalCopy);
        model.addAttribute("signalTransmitterId", id);
        model.addAttribute("selectedSignalTransmitterType", selectedSignalTransmitterType);
        return "/signalTransmitter/copySignalTransmitter.jsp";
    }

    @PostMapping("/{id}/copy")
    public String copy(@ModelAttribute("terminalCopy") TerminalCopy terminalCopy, @PathVariable int id, BindingResult result,
            YukonUserContext userContext, FlashScope flash, ModelMap model, HttpServletRequest request,
            HttpServletResponse servletResponse) throws IOException {
        Map<String, String> json = new HashMap<>();
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.pagingTerminalUrl + "/" + id + "/copy");
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.POST, Object.class, terminalCopy);

            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(terminalCopy, "terminalCopy");
                result = helper.populateBindingErrorForApiErrorModel(result, error, response, "yukon.web.error.");
                return bindAndForwardForCopy(terminalCopy, result, model, servletResponse, id);
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> map = (Map<String, Object>) response.getBody();
                json.put("signalTransmitterId", map.get("id").toString());
                servletResponse.setContentType("application/json");
                JsonUtils.getWriter().writeValue(servletResponse.getOutputStream(), json);
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.copy.success", String.valueOf(map.get("name"))));
                return null;
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            json.put("redirectUrl", redirectListPageLink);
            servletResponse.setContentType("application/json");
            JsonUtils.getWriter().writeValue(servletResponse.getOutputStream(), json);
            return null;
        } catch (RestClientException ex) {
            log.error("Error copying signal transmitter: {}. Error: {}", terminalCopy.getName(), ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.common.copy.error", terminalCopy.getName(), ex.getMessage()));
            json.put("redirectUrl", redirectListPageLink);
            servletResponse.setContentType("application/json");
            JsonUtils.getWriter().writeValue(servletResponse.getOutputStream(), json);
            return null;
        }
        return null;
    }

    private void setupModel(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        model.addAttribute("types", webSupportedSignalTransmitterTypes);
        model.addAttribute("commChannels", getCommChannels(request, userContext));
        model.addAttribute("identifierFormats", IdentifierFormat.values());
        model.addAttribute("dataFormats", DataFormat.values());
        model.addAttribute("protocols", Protocol.values());
    }

    private List<LMDto> getCommChannels(HttpServletRequest request, YukonUserContext userContext) {
        String url = helper.findWebServerUrl(request, userContext, ApiURL.commChannelUrl + "/");
        List<LMDto> commChannelList = new ArrayList<>();
        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForList(userContext, request, url,
                DeviceBaseModel.class, HttpMethod.GET, DeviceBaseModel.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            commChannelList = ((List<DeviceBaseModel>) response.getBody()).stream()
                    .map(commChannel -> new LMDto(commChannel.getDeviceId(), commChannel.getDeviceName()))
                    .collect(Collectors.toList());
        }
        return commChannelList;

    }

    private ResponseEntity<? extends Object> saveSignalTransmitter(YukonUserContext userContext, HttpServletRequest request,
            String webserverUrl, TerminalBase signalTransmitter, HttpMethod methodtype) throws RestClientException {
        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, webserverUrl,
                methodtype, TerminalBase.class, signalTransmitter);
        return response;
    }

    private String bindAndForward(TerminalBase signalTransmitter, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("signalTransmitter", signalTransmitter);
        attrs.addFlashAttribute(bindingResultKey, result);
        if (signalTransmitter.getId() == null) {
            return "redirect:/stars/device/signalTransmitter/create";
        }
        return "redirect:/stars/device/signalTransmitter/" + signalTransmitter.getId() + "/edit";
    }

    private TerminalBase retrieveSignalTransmitter(YukonUserContext userContext, HttpServletRequest request, int id, String url) {
        TerminalBase terminalBase = null;
        try {
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.GET, TerminalBase.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                terminalBase = (TerminalBase) response.getBody();
                terminalBase.setId(id);
            }

        } catch (RestClientException ex) {
            log.error("Error retrieving signal transmitter: " + ex.getMessage());
        }
        return terminalBase;
    }

    private PaoType getPaoTypeForPaoId(int signalTransmitterId) {
        LiteYukonPAObject litePao = dbCache.getAllPaosMap().get(signalTransmitterId);
        return litePao.getPaoType();
    }

    private String bindAndForwardForCopy(TerminalCopy terminalCopy, BindingResult result, ModelMap model,
            HttpServletResponse response, int id) {
        PaoType selectedSignalTransmitterType = getPaoTypeForPaoId(id);
        model.addAttribute("terminalCopy", terminalCopy);
        model.addAttribute("selectedSignalTransmitterType", selectedSignalTransmitterType);
        model.addAttribute("org.springframework.validation.BindingResult.terminalCopy", result);
        model.addAttribute("signalTransmitterId", id);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return "/signalTransmitter/copySignalTransmitter.jsp";
    }
}
