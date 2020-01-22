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
import com.cannontech.common.dr.setup.ProgramConstraint;
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
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.dr.setup.dao.LMSetupDao.GearSortBy;
import com.cannontech.web.api.dr.setup.dao.LMSetupDao.ProgramConstraintSortBy;
import com.cannontech.web.api.dr.setup.dao.LMSetupDao.SortBy;
import com.cannontech.web.api.dr.setup.model.GearFilteredResult;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
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
    public String filter(@ModelAttribute LMSetupFilter lmSetupFilter, @DefaultSort(dir = Direction.asc, sort = "NAME") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging, ModelMap model, YukonUserContext userContext, HttpServletRequest request,
            FlashScope flash) {

        FilterCriteria<LMSetupFilter> filterCriteria = getFilterCriteria(lmSetupFilter, sorting, paging);
        setSortableColumnInModel(model, lmSetupFilter, sorting.getDirection(), sorting, userContext);

        model.addAttribute("filterByTypes", LmSetupFilterType.values());
        model.addAttribute("lmSetupFilter", lmSetupFilter);
        model.addAttribute("loadGroupTypes", PaoType.getAllLMGroupTypes());
        model.addAttribute("loadProgramTypes", PaoType.getDirectLMProgramTypes());
        model.addAttribute("gearTypes", GearControlMethod.values());
        model.addAttribute("isFilterByGearSelected", lmSetupFilter.getFilterByType() == LmSetupFilterType.GEAR);
        model.addAttribute("isFilterByLoadProgramSelected", lmSetupFilter.getFilterByType() == LmSetupFilterType.LOAD_PROGRAM);
        model.addAttribute("isFilterByLoadGroupSelected", lmSetupFilter.getFilterByType() == LmSetupFilterType.LOAD_GROUP);

        ResponseEntity<? extends Object> response = null;
        // Make API call to get filtered result.
        try {
            response = callAPIForParameterizedTypeObject(lmSetupFilter, filterCriteria, userContext, request);
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        } catch (RestClientException ex) {
            log.error("Error retrieving details: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.setup.filter.error", ex.getMessage()));
            return "redirect:/dr/setup/list";
        }

        SearchResults<?> filteredResults = (SearchResults<?>) response.getBody();
        model.addAttribute("filteredResults", filteredResults);

        // Build setup model
        model.addAttribute("viewUrlPrefix", lmSetupFilter.getFilterByType().getViewUrl());

        return "dr/setup/list.jsp";
    }

    /**
     * Get FilterCriteria based on LMSetupFilter, SortingParameters, PagingParameters.
     */

    private FilterCriteria<LMSetupFilter> getFilterCriteria(LMSetupFilter lmSetupFilter, SortingParameters sorting, PagingParameters paging) {
        FilterCriteria<LMSetupFilter> filterCriteria = new FilterCriteria<LMSetupFilter>(lmSetupFilter, sorting, paging);

        switch (lmSetupFilter.getFilterByType()) {
            case CONTROL_AREA:
            case CONTROL_SCENARIO:
            case LOAD_GROUP:
            case LOAD_PROGRAM:
            case MACRO_LOAD_GROUP:
                SortBy sortByValue = LMFilterSortBy.valueOf(sorting.getSort()).getValue();
                filterCriteria.setSortingParameters(SortingParameters.of(sortByValue.getDbString(), filterCriteria.getSortingParameters().getDirection()));
                break;
            case GEAR:
                GearSortBy gearSortBy = LMFilterGearSortBy.valueOf(sorting.getSort()).getValue();
                filterCriteria.setSortingParameters(SortingParameters.of(gearSortBy.getDbString(), filterCriteria.getSortingParameters().getDirection()));
                break;
            case PROGRAM_CONSTRAINT:
                ProgramConstraintSortBy constraintSortBy = LMFilterProgramConstraintSortBy.valueOf(sorting.getSort()).getValue();
                filterCriteria.setSortingParameters(SortingParameters.of(constraintSortBy.getDbString(), filterCriteria.getSortingParameters().getDirection()));
                break;
        }
        return filterCriteria;
    }


    /**
     * Set SortableColumn in Model based on LMSetupFilter.
     */

    private void setSortableColumnInModel(ModelMap model, LMSetupFilter lmSetupFilter, Direction dir, SortingParameters sorting, YukonUserContext userContext) {

        String text;
        SortableColumn col;

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        switch (lmSetupFilter.getFilterByType()) {
            case CONTROL_AREA:
            case CONTROL_SCENARIO:
            case MACRO_LOAD_GROUP:
                LMFilterSortBy filterSortBy = LMFilterSortBy.valueOf(sorting.getSort());
                text = accessor.getMessage(LMFilterSortBy.NAME);
                col = SortableColumn.of(dir, LMFilterSortBy.NAME == filterSortBy, text, LMFilterSortBy.NAME.name());
                model.addAttribute(SortBy.NAME.name(), col);
                break;
            case GEAR:
                LMFilterGearSortBy gearSortBy = LMFilterGearSortBy.valueOf(sorting.getSort());
                for (LMFilterGearSortBy column : LMFilterGearSortBy.values()) {
                    text = accessor.getMessage(column);
                    col = SortableColumn.of(dir, column == gearSortBy, text, column.name());
                    model.addAttribute(column.name(), col);
                }
                break;
            case LOAD_GROUP:
            case LOAD_PROGRAM:
                LMFilterSortBy sortBy = LMFilterSortBy.valueOf(sorting.getSort());
                for (LMFilterSortBy column : LMFilterSortBy.values()) {
                    text = accessor.getMessage(column);
                    col = SortableColumn.of(dir, column == sortBy, text, column.name());
                    model.addAttribute(column.name(), col);
                }
                break;
            case PROGRAM_CONSTRAINT:

                LMFilterProgramConstraintSortBy constraintSortBy = LMFilterProgramConstraintSortBy.valueOf(sorting.getSort());
                for (LMFilterProgramConstraintSortBy column : LMFilterProgramConstraintSortBy.values()) {
                    text = accessor.getMessage(column);
                    col = SortableColumn.of(dir, column == constraintSortBy, text, column.name());
                    model.addAttribute(column.name(), col);
                }
                break;
        }

    }

    /**
     * Call API based on LMSetupFilter.
     */
    private ResponseEntity<? extends Object> callAPIForParameterizedTypeObject(LMSetupFilter lmSetupFilter, FilterCriteria<LMSetupFilter> filterCriteria,
            YukonUserContext userContext, HttpServletRequest request) {

        Class<?> requestObject = null;
        switch (lmSetupFilter.getFilterByType()) {
            case CONTROL_AREA:
            case CONTROL_SCENARIO:
            case LOAD_GROUP:
            case LOAD_PROGRAM:
            case MACRO_LOAD_GROUP:
                requestObject = LMPaoDto.class;
                break;
            case PROGRAM_CONSTRAINT:
                requestObject = ProgramConstraint.class;
                break;
            case GEAR:
                requestObject = GearFilteredResult.class;
                break;
        }

        String url = helper.findWebServerUrl(request, userContext, ApiURL.drSetupFilterUrl);
        return apiRequestHelper.callAPIForParameterizedTypeObject(userContext, request, url, HttpMethod.POST, requestObject, filterCriteria);

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

    public enum LMFilterGearSortBy implements DisplayableEnum {

        NAME(GearSortBy.GEARNAME),
        TYPE(GearSortBy.GEARTYPE),
        ORDER(GearSortBy.GEARNUMBER),
        PROGRAM(GearSortBy.PROGRAM);

        private final GearSortBy value;

        private LMFilterGearSortBy(GearSortBy value) {
            this.value = value;
        }

        public GearSortBy getValue() {
            return value;
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.setup.filter." + name();

        }
    }

    public enum LMFilterProgramConstraintSortBy implements DisplayableEnum {

        NAME(ProgramConstraintSortBy.CONSTRAINTNAME);

        private final ProgramConstraintSortBy value;

        private LMFilterProgramConstraintSortBy(ProgramConstraintSortBy value) {
            this.value = value;
        }

        public ProgramConstraintSortBy getValue() {
            return value;
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.setup.filter." + name();
        }
    }
   
}
