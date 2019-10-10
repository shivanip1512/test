package com.cannontech.web.dr.setup;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.dr.setup.dao.LMSetupDao.SortBy;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.RESTRICTED)
@RequestMapping("/setup")
public class LMSetupFilterController {

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private ApiControllerHelper helper;
    
    private static final Logger log = YukonLogManager.getLogger(LMSetupFilterController.class);
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    
    @GetMapping("/list")
    public String list(ModelMap model, YukonUserContext userContext, HttpServletRequest request, FlashScope flash) {
        LMSetupFilter lmSetupFilter = null;
        if (!model.containsAttribute("lmSetupFilter")) {
            lmSetupFilter = new LMSetupFilter();
            lmSetupFilter.setFilterByType(LmSetupFilterType.CONTROL_AREA);
        } else {
            lmSetupFilter = (LMSetupFilter) model.get("lmSetupFilter");
        }
        filter(lmSetupFilter, SortingParameters.of("NAME", Direction.asc), PagingParameters.EVERYTHING, model,
            userContext, request, flash);
        return "dr/setup/list.jsp";
    }
    
    @GetMapping("/filter")
    public String filter(@ModelAttribute LMSetupFilter lmSetupFilter,
            @DefaultSort(dir = Direction.asc, sort = "NAME") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging, ModelMap model, YukonUserContext userContext,
            HttpServletRequest request, FlashScope flash) {
        FilterCriteria<LMSetupFilter> filterCriteria =
            new FilterCriteria<LMSetupFilter>(lmSetupFilter, sorting, paging);

        SortBy sortByValue = LMFilterSortBy.valueOf(sorting.getSort()).getValue();
        filterCriteria.setSortingParameters(
            SortingParameters.of(sortByValue.getDbString(), filterCriteria.getSortingParameters().getDirection()));

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        LMFilterSortBy sortBy = LMFilterSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        if (lmSetupFilter.getFilterByType() == LmSetupFilterType.LOAD_GROUP
            || lmSetupFilter.getFilterByType() == LmSetupFilterType.LOAD_PROGRAM) {
            for (LMFilterSortBy column : LMFilterSortBy.values()) {
                String text = accessor.getMessage(column);
                SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
                model.addAttribute(column.name(), col);
            }
        } else {
            String text = accessor.getMessage(LMFilterSortBy.NAME);
            SortableColumn col =
                SortableColumn.of(dir, LMFilterSortBy.NAME == sortBy, text, LMFilterSortBy.NAME.name());
            model.addAttribute(SortBy.NAME.name(), col);
        }
        model.addAttribute("filterByTypes", LmSetupFilterType.values());
        model.addAttribute("lmSetupFilter", lmSetupFilter);
        model.addAttribute("loadGroupTypes", PaoType.getAllLMGroupTypes());
        model.addAttribute("loadProgramTypes", PaoType.getDirectLMProgramTypes());
        
        SearchResults<LMPaoDto> filteredResults = null;
        ResponseEntity<? extends Object> response = null;
        // Make API call to get filtered result.
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drSetupFilterUrl);
            response = apiRequestHelper.callAPIForParameterizedTypeObject(userContext, request, url, HttpMethod.POST,
                LMPaoDto.class, filterCriteria);
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        } catch (RestClientException ex) {
            log.error("Error retrieving details: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.setup.filter.error", ex.getMessage()));
            return "redirect:/dr/setup/list";
        }
        filteredResults = (SearchResults<LMPaoDto>) response.getBody();

        // Build setup model
        model.addAttribute("viewUrlPrefix", lmSetupFilter.getFilterByType().getViewUrl());
        model.addAttribute("filteredResults", filteredResults);
        return "dr/setup/list.jsp";
    }
    
    
    public enum LMFilterSortBy implements DisplayableEnum {

        NAME(SortBy.NAME),
        TYPE(SortBy.TYPE);
        
        private final SortBy value;

        private LMFilterSortBy(SortBy value) {
            this.value = value;
        }

        public SortBy getValue() {
            return value;
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.setup.filter." + name();

        }
    }
}
