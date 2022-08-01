package com.cannontech.web.tools.notificationGroup;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PaginatedResponse;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.notificationGroup.CICustomer;
import com.cannontech.web.notificationGroup.Contact;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.notificationGroup.NotificationGroup;
import com.cannontech.web.util.JsTreeNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.cannontech.core.dao.NotificationGroupDao.SortBy;

@Controller
@RequestMapping("/notificationGroup/*")
public class NotificationGroupController {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private NotificationGroupValidator notificationGroupValidator;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private NotificationGroupControllerHelper notificationGroupControllerHelper;
    
    private static final TypeReference<List<CICustomer>> ciCustomerTargetType = new TypeReference<List<CICustomer>>() {};
    private static final TypeReference<List<Contact>> unassingedContactTargetType = new TypeReference<List<Contact>>() {};

    private static final Logger log = YukonLogManager.getLogger(NotificationGroupController.class);
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String baseKey = "yukon.web.modules.tools.notificationGroup.";
    private static final String redirectListPageLink = "redirect:/tools/notificationGroup/list";
    
    @GetMapping("create")
    public String create(ModelMap model, YukonUserContext userContext) throws JsonProcessingException {
        model.addAttribute("mode", PageEditMode.CREATE);
        NotificationGroup notificationGroup = new NotificationGroup();
        notificationGroup.setEnabled(true);
        if (model.containsAttribute("notificationGroup")) {
            notificationGroup = (NotificationGroup) model.get("notificationGroup");
        }
        setupModel(model, userContext, notificationGroup);
        return "/notificationGroup/view.jsp";
    }

    private void setupModel(ModelMap model, YukonUserContext userContext, NotificationGroup notificationGroup)
            throws JsonProcessingException {
        final MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        JsTreeNode notificationTreeJson = notificationGroupControllerHelper.buildNotificationTree(messageSourceAccessor, notificationGroup);
        model.addAttribute("notificationTreeJson", JsonUtils.toJson(notificationTreeJson.toMap(), true));
        model.addAttribute("notificationGroup", notificationGroup);
    }
    
    @GetMapping("/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            model.addAttribute("mode", PageEditMode.VIEW);
            String url = helper.findWebServerUrl(request, userContext, ApiURL.notificationGroupUrl + "/" + id);

            NotificationGroup notificationGroup = retrieveNotificationGroup(userContext, request, id, url);
            
            if (notificationGroup == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "retrieve.error"));
                return redirectListPageLink;
            }
            model.addAttribute("notificationGroup", notificationGroup);
            return "/notificationGroup/view.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        }
    }
    
    @GetMapping("/{id}/edit")
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) throws JsonProcessingException {
        try {
            model.addAttribute("mode", PageEditMode.EDIT);
            NotificationGroup notificationGroup = null;
            if (model.containsAttribute("notificationGroup")) {
                notificationGroup = (NotificationGroup) model.getAttribute("notificationGroup");
            } else {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.notificationGroupUrl + "/" + id);
                notificationGroup = retrieveNotificationGroup(userContext, request, id, url);
            }
            setupModel(model, userContext, notificationGroup);
            return "/notificationGroup/view.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        }
    }

    private NotificationGroup retrieveNotificationGroup(YukonUserContext userContext, HttpServletRequest request, int id,
            String url) {
        NotificationGroup notificationGroup = null;
        try {
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.GET, NotificationGroup.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                notificationGroup = (NotificationGroup) response.getBody();
                notificationGroup.setId(id);
            }
        } catch (RestClientException ex) {
            log.error("Error retrieving notification group: " + ex.getMessage());
        }
        return notificationGroup;
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable int id, @ModelAttribute LMDelete lmDelete, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request) {

        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.notificationGroupUrl + "/" + id);
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request,
                    url, HttpMethod.DELETE, Object.class, lmDelete);

            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", lmDelete.getName()));
                return redirectListPageLink;
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/tools/notificationGroup/" + id;
        } catch (RestClientException ex) {
            log.error("Error deleting notification group : {}. Error: {}", lmDelete.getName(), ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", lmDelete.getName(), ex.getMessage()));
            return "redirect:/tools/notificationGroup/" + id;
        }
        return redirectListPageLink;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("list")
    public String list(ModelMap model, @DefaultSort(dir = Direction.asc, sort = "name") SortingParameters sorting,
            @DefaultItemsPerPage(value = 25) PagingParameters paging,
            String filterValueName, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {

        ResponseEntity<? extends Object> response = null;
        try {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            NotificationGroupSortBy sortBy = NotificationGroupSortBy.valueOf(sorting.getSort());
            Direction dir = sorting.getDirection();

            List<SortableColumn> columns = new ArrayList<>();
            for (NotificationGroupSortBy column : NotificationGroupSortBy.values()) {
                String text = accessor.getMessage(column);
                SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
                columns.add(col);
                model.addAttribute(column.name(), col);
            }
            String url = helper.findWebServerUrl(request, userContext, ApiURL.notificationGroupUrl);

            URIBuilder ub = new URIBuilder(url);
            ub.addParameter("sort", sortBy.getValue().name());
            ub.addParameter("dir", dir.name());
            ub.addParameter("name", filterValueName);
            ub.addParameter("itemsPerPage", Integer.toString(paging.getItemsPerPage()));
            ub.addParameter("page", Integer.toString(paging.getPage()));

            response = apiRequestHelper.callAPIForParameterizedTypeObject(userContext, request, ub.toString(),
                    HttpMethod.GET, NotificationGroup.class, Object.class);
            PaginatedResponse<NotificationGroup> notificationGroups = new PaginatedResponse<>();
            if (response.getStatusCode() == HttpStatus.OK) {
                notificationGroups = (PaginatedResponse<NotificationGroup>) response.getBody();
            }
            model.addAttribute("notificationGroups", notificationGroups);
            model.addAttribute("filterValueName", filterValueName);
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
        } catch (RestClientException ex) {
            log.error("Error retrieving details: " + ex.getMessage());
            flash.setError(
                    new YukonMessageSourceResolvable("yukon.web.modules.tools.notificationGroup.filter.error", ex.getMessage()));
        } catch (URISyntaxException e) {
            log.error("Error in URI: " + e.getMessage());
            flash.setError(
                    new YukonMessageSourceResolvable("yukon.web.modules.tools.notificationGroup.filter.error", e.getMessage()));
        }
        return "/notificationGroup/list.jsp";
    }
    
    @PostMapping("/save")
    public String save(@ModelAttribute("notificationGroup") NotificationGroup notificationGroup, BindingResult result,
            YukonUserContext userContext, FlashScope flash, RedirectAttributes redirectAttributes,
            @RequestParam(required = false, name = "ciCustomersJsonString") String ciCustomersJsonString,
            @RequestParam(required = false, name = "unassignedContactsJsonString") String unassignedContactsJsonString,
            HttpServletRequest request) throws IOException {
        List<CICustomer> ciCustomers = Lists.newArrayList();
        if (StringUtils.isNoneBlank(ciCustomersJsonString)) {
            ciCustomers = JsonUtils.fromJson(ciCustomersJsonString, ciCustomerTargetType);
        }
        notificationGroup.setcICustomers(ciCustomers);
        
        List<Contact> unassignedContacts = Lists.newArrayList();
        if (StringUtils.isNoneBlank(unassignedContactsJsonString)) {
            unassignedContacts = JsonUtils.fromJson(unassignedContactsJsonString, unassingedContactTargetType);
        }
        notificationGroup.setUnassignedContacts(unassignedContacts);
        
        try {
            notificationGroupValidator.validate(notificationGroup, result);
            if (result.hasErrors()) {
                return bindAndForward(notificationGroup, result, redirectAttributes);
            }
            String url = null;
            ResponseEntity<? extends Object> apiResponse = null;
            if (notificationGroup.getId() == null) {
                url = helper.findWebServerUrl(request, userContext, ApiURL.notificationGroupUrl);
                apiResponse = saveNotificationGroup(userContext, request, url, notificationGroup, HttpMethod.POST);
            } else {
                url = helper.findWebServerUrl(request, userContext, ApiURL.notificationGroupUrl) + "/" + notificationGroup.getId();
                apiResponse = saveNotificationGroup(userContext, request, url, notificationGroup, HttpMethod.PUT);
            }

            if (apiResponse.getStatusCode() == HttpStatus.OK || apiResponse.getStatusCode() == HttpStatus.CREATED) {
                NotificationGroup notificationGroupResponse = (NotificationGroup) apiResponse.getBody();
                flash.setConfirm(
                        new YukonMessageSourceResolvable("yukon.common.save.success", notificationGroupResponse.getName()));
                return "redirect:/tools/notificationGroup/" + notificationGroupResponse.getId();
            }

            if (apiResponse.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                flash.setError(new YukonMessageSourceResolvable("yukon.web.error.genericMainMessage"));
                log.error("Error saving notification group", JsonUtils.beautifyJson(apiResponse.getBody().toString()));
                return bindAndForward(notificationGroup, result, redirectAttributes);
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        } catch (RestClientException ex) {
            log.error("Error creating notification Group: {}. Error: {}", notificationGroup.getName(), ex.getMessage());
            log.error(ex.getStackTrace());
            flash.setError(
                    new YukonMessageSourceResolvable("yukon.web.api.save.error", notificationGroup.getName(), ex.getMessage()));
            return redirectListPageLink;
        }
        return null;
    }
    
    private String bindAndForward(NotificationGroup notificationGroup, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("notificationGroup", notificationGroup);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.notificationGroup", result);
        if (notificationGroup.getId() == null) {
            return "redirect:/tools/notificationGroup/create";
        }
        return "redirect:/tools/notificationGroup/" + notificationGroup.getId() + "/edit";
    }

    private ResponseEntity<? extends Object> saveNotificationGroup(YukonUserContext userContext, HttpServletRequest request,
            String webserverUrl, NotificationGroup notificationGroup, HttpMethod methodtype) throws RestClientException {
        return  apiRequestHelper.callAPIForObject(userContext, request, webserverUrl,
                methodtype, NotificationGroup.class, notificationGroup);
    }

    public enum NotificationGroupSortBy implements DisplayableEnum {

        name(SortBy.NAME),
        status(SortBy.STATUS);

        private final SortBy value;

        NotificationGroupSortBy(SortBy value) {
            this.value = value;
        }

        @Override
        public String getFormatKey() {
            return "yukon.common." + name();
        }

        public SortBy getValue() {
            return value;
        }
    }
}
