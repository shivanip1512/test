package com.cannontech.web.stars.signalTransmitter;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.terminal.model.PagingTapTerminal;
import com.cannontech.common.device.terminal.model.SNPPTerminal;
import com.cannontech.common.device.terminal.model.SignalTransmitterModelFactory;
import com.cannontech.common.device.terminal.model.TNPPTerminal;
import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.device.terminal.model.TerminalCopy;
import com.cannontech.common.device.terminal.model.WCTPTerminal;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.i18n.MessageSourceAccessor;
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
            .of(PaoType.WCTP_TERMINAL, PaoType.TAPTERMINAL, PaoType.SNPP_TERMINAL)
            .sorted((p1, p2) -> p1.getDbString().compareTo(p2.getDbString()))
            .collect(Collectors.toList());

    @GetMapping("list")
    public String list(ModelMap model, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {
        ResponseEntity<? extends Object> response = null;
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.pagingTerminalUrl);
            response = apiRequestHelper.callAPIForList(userContext, request, url, TerminalBase.class, HttpMethod.GET, TerminalBase.class);
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        } catch (RestClientException ex) {
            log.error("Error retrieving details: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.signalTransmitter.filter.error", ex.getMessage()));
            return redirectListPageLink;
        }
        
        List<TerminalBase> signalTransmitters = new ArrayList<>();
        if (response.getStatusCode() == HttpStatus.OK) {
            signalTransmitters = (List<TerminalBase>) response.getBody();
        }
        model.addAttribute("signalTransmitters", signalTransmitters);
        return "/signalTransmitter/list.jsp";
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
            String url;
            ResponseEntity<? extends Object> apiResponse = null;
            if (signalTransmitter.getId() == null) {
                url = helper.findWebServerUrl(request, userContext, ApiURL.pagingTerminalUrl);
                apiResponse = saveSignalTransmitter(userContext, request, url, signalTransmitter, HttpMethod.POST);
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
    public String renderCopyDialog(@PathVariable int id, ModelMap model, YukonUserContext userContext,
            HttpServletRequest request) {

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
            String url = helper.findWebServerUrl(request, userContext, ApiURL.pagingTerminalUrl + "/" + id + "/copy" );
            ResponseEntity<? extends Object> response = 
                    apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, Object.class, terminalCopy);

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
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "copy.success", String.valueOf(map.get("name"))));
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
            log.error("Error copying load group: {}. Error: {}", terminalCopy.getName(), ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "copy.error", terminalCopy.getName(), ex.getMessage()));
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
        // TODO: change this URL to edit functionality later.
        return "redirect:/stars/device/signalTransmitter/create";
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
