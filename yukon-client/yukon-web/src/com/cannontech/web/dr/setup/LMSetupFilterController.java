package com.cannontech.web.dr.setup;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
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
import com.cannontech.common.dr.gear.setup.OperationalState;
import com.cannontech.common.dr.setup.LMDto;
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
import com.cannontech.web.api.dr.setup.dao.LMSetupDao.LoadProgramSortBy;
import com.cannontech.web.api.dr.setup.dao.LMSetupDao.ProgramConstraintSortBy;
import com.cannontech.web.api.dr.setup.dao.LMSetupDao.SortBy;
import com.cannontech.web.api.dr.setup.model.ControlAreaFilteredResult;
import com.cannontech.web.api.dr.setup.model.ControlScenarioFilteredResult;
import com.cannontech.web.api.dr.setup.model.GearFilteredResult;
import com.cannontech.web.api.dr.setup.model.LoadProgramFilteredResult;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.google.common.collect.Maps;

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
        model.addAttribute("loadGroupTypes", PaoType.getAllLMGroupTypes().stream()
                        .sorted(Comparator.comparing(PaoType::getDbString, String.CASE_INSENSITIVE_ORDER))
                        .collect(Collectors.toList()));
        model.addAttribute("loadProgramTypes", PaoType.getDirectLMProgramTypes().stream()
                        .sorted(Comparator.comparing(PaoType::name, String.CASE_INSENSITIVE_ORDER))
                        .collect(Collectors.toList()));
        model.addAttribute("gearTypes", GearControlMethod.getGearTypesByProgramType(PaoType.getDirectLMProgramTypes())
                        .stream().sorted(Comparator.comparing(GearControlMethod::getDisplayName, String.CASE_INSENSITIVE_ORDER))
                        .collect(Collectors.toList()));
        model.addAttribute("operationalStates", OperationalState.values());
        model.addAttribute("isFilterByGearSelected", lmSetupFilter.getFilterByType() == LmSetupFilterType.GEAR);
        model.addAttribute("isFilterByLoadProgramSelected", lmSetupFilter.getFilterByType() == LmSetupFilterType.LOAD_PROGRAM);
        model.addAttribute("isFilterByLoadGroupSelected", lmSetupFilter.getFilterByType() == LmSetupFilterType.LOAD_GROUP);
        model.addAttribute("isFilterByControlAreaSelected", lmSetupFilter.getFilterByType() == LmSetupFilterType.CONTROL_AREA);
        model.addAttribute("isFilterByControlScenarioSelected", lmSetupFilter.getFilterByType() == LmSetupFilterType.CONTROL_SCENARIO);

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
        
        setupModel(filteredResults, lmSetupFilter.getFilterByType(), userContext, model);

        // Build setup model
        model.addAttribute("viewUrlPrefix", lmSetupFilter.getFilterByType().getViewUrl());

        return "dr/setup/list.jsp";
    }

    private void setupModel(SearchResults<?> filteredResults, LmSetupFilterType filterByType, YukonUserContext userContext, ModelMap model) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        // Add text to be displayed for columns for different object based on the LM object being filtered.
        switch (filterByType) {
            case CONTROL_AREA:
                Map<Integer, String> programsForControlArea = Maps.newHashMap();
                List<ControlAreaFilteredResult> controlAreas = (List<ControlAreaFilteredResult>) filteredResults.getResultList();
                for (ControlAreaFilteredResult controlArea : controlAreas) {
                    programsForControlArea.put(controlArea.getControlAreaId(), getAbbreviatedText(accessor, controlArea.getAssignedPrograms()));
                    model.addAttribute("programsForControlArea", programsForControlArea);
                }
                break;
            case CONTROL_SCENARIO:
                Map<Integer, String> loadProgramsForScenario = Maps.newHashMap();
                List<ControlScenarioFilteredResult> filteredControlScenarios = (List<ControlScenarioFilteredResult>) filteredResults.getResultList();
                for (ControlScenarioFilteredResult filteredControlScenario : filteredControlScenarios) {
                    loadProgramsForScenario.put(filteredControlScenario.getScenario().getId(), getAbbreviatedText(accessor, filteredControlScenario.getAssignedPrograms()));
                    model.addAttribute("loadProgramForScenario", loadProgramsForScenario);
                }
                break;
            case LOAD_PROGRAM:
                Map<Integer, String> loadGroupsForProgram = Maps.newHashMap();
                Map<Integer, String> gearsForProgram = Maps.newHashMap();
                List<LoadProgramFilteredResult> loadPrograms = (List<LoadProgramFilteredResult>) filteredResults.getResultList();
                for (LoadProgramFilteredResult loadProgram : loadPrograms) {
                    loadGroupsForProgram.put(loadProgram.getProgram().getId(), getAbbreviatedText(accessor, loadProgram.getLoadGroups()));
                    gearsForProgram.put(loadProgram.getProgram().getId(), getAbbreviatedText(accessor, loadProgram.getGears()));
                }
                model.addAttribute("loadGroupsForProgram", loadGroupsForProgram);
                model.addAttribute("gearsForProgram", gearsForProgram);
                break;
        }
    }

    /**
     * This method returns abbreviated text for the LM object list passed as a parameter.
     */
    private String getAbbreviatedText(MessageSourceAccessor accessor, List<LMDto> lmObjects) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isEmpty(lmObjects)) {
            builder.append(accessor.getMessage("yukon.common.none.choice"));
        } else if (lmObjects.size() > 5) {
            builder.append(lmObjects.subList(0, 5).stream().map(lmObject -> lmObject.getName())
                                                           .collect(Collectors.joining(", ")));
            builder.append(accessor.getMessage("yukon.web.modules.dr.setup.abbreviatedText", lmObjects.size() - 5));
        } else {
            builder.append(lmObjects.subList(0, lmObjects.size()).stream().map(lmObject -> lmObject.getName())
                                                                          .collect(Collectors.joining(", ")));
        }
        return builder.toString();
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
            case LOAD_PROGRAM:
                LoadProgramSortBy loadProgramSortByValue = LMFilterLoadProgramSortBy.valueOf(sorting.getSort()).getValue();
                filterCriteria.setSortingParameters(SortingParameters.of(loadProgramSortByValue.getDbString(), filterCriteria.getSortingParameters().getDirection()));
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
            case LOAD_PROGRAM:
                LMFilterLoadProgramSortBy loadProgramSortBy = LMFilterLoadProgramSortBy.valueOf(sorting.getSort());
                for (LMFilterLoadProgramSortBy column : LMFilterLoadProgramSortBy.values()) {
                    text = accessor.getMessage(column);
                    col = SortableColumn.of(dir, column == loadProgramSortBy, text, column.name());
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
                requestObject = ControlAreaFilteredResult.class;
                break;
            case LOAD_GROUP:
            case MACRO_LOAD_GROUP:
                requestObject = LMPaoDto.class;
                break;
            case PROGRAM_CONSTRAINT:
                requestObject = ProgramConstraint.class;
                break;
            case GEAR:
                requestObject = GearFilteredResult.class;
                break;
            case CONTROL_SCENARIO:
                requestObject = ControlScenarioFilteredResult.class;
                break;
            case LOAD_PROGRAM:
                requestObject = LoadProgramFilteredResult.class;
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
    
    private enum LMFilterLoadProgramSortBy implements DisplayableEnum {

        NAME(LoadProgramSortBy.PROGRAMNAME),
        TYPE(LoadProgramSortBy.PROGRAMTYPE),
        OPERATIONAL_STATE(LoadProgramSortBy.OPERATIONALSTATE),
        CONSTRAINT(LoadProgramSortBy.CONSTRAINT);

        private final LoadProgramSortBy value;

        private LMFilterLoadProgramSortBy(LoadProgramSortBy value) {
            this.value = value;
        }

        public LoadProgramSortBy getValue() {
            return value;
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.setup.filter." + name();
        }
    }
}
