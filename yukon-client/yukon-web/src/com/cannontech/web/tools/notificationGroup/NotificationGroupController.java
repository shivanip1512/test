package com.cannontech.web.tools.notificationGroup;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

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
import com.cannontech.web.notificationGroup.NotificationGroup;
import com.cannontech.core.dao.NotificationGroupDao.SortBy;

@Controller
@RequestMapping("/notificationGroup/*")
public class NotificationGroupController {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private static final Logger log = YukonLogManager.getLogger(NotificationGroupController.class);
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String baseKey = "yukon.web.modules.tools.notificationGroup.";
    private static final String redirectListPageLink = "redirect:/tools/notificationGroup/list";

    @GetMapping("create")
    public String create(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);

        return "/notificationGroup/view.jsp";
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
            // TODO : will be completed under YUK-26551 : Notification group view page
            model.addAttribute("notificationGroup", notificationGroup);
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
            @DefaultItemsPerPage(value = 250) PagingParameters paging,
            String name, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {

        ResponseEntity<? extends Object> response = null;
        try {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            NotificationGroupSortBy sortBy = NotificationGroupSortBy.valueOf(sorting.getSort());
            Direction dir = sorting.getDirection();

            List<SortableColumn> columns = new ArrayList<>();
            for (NotificationGroupSortBy column : NotificationGroupSortBy.values()) {
                String text = accessor.getMessage(column);
                SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
                columns.add(col);
                model.addAttribute(column.name(), col);
            }
          //  NotificationGroupSortBy
            String url = helper.findWebServerUrl(request, userContext, ApiURL.notificationGroupUrl);

            URIBuilder ub = new URIBuilder(url);
            ub.addParameter("sort", sortBy.getValue().name());
            ub.addParameter("dir", dir.name());
            ub.addParameter("name", name);
            ub.addParameter("itemsPerPage", Integer.toString(paging.getItemsPerPage()));
            ub.addParameter("page", Integer.toString(paging.getPage()));
            
            response = apiRequestHelper.callAPIForParameterizedTypeObject(userContext, request, ub.toString(),
                    HttpMethod.GET, NotificationGroup.class, Object.class);
            PaginatedResponse<NotificationGroup> notificationGroups = new PaginatedResponse<>();
            if (response.getStatusCode() == HttpStatus.OK) {
                notificationGroups = (PaginatedResponse) response.getBody();
            }
            model.addAttribute("notificationGroups", notificationGroups);
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
    
    public enum NotificationGroupSortBy implements DisplayableEnum {

        name(SortBy.NAME),
        status(SortBy.STATUS);

        private final SortBy value;

        NotificationGroupSortBy(SortBy value) {
            this.value = value;
        }

        @Override
        public String getFormatKey() {
            if (this == name) {
                return "yukon.web.modules.tools.notificationGroup." + name();
            }
            return "yukon.common." + name();
        }

        public SortBy getValue() {
            return value;
        }
    }
}
